package com.example.gome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;
import com.squareup.picasso.Picasso;

public class HomeScreenActivity extends AppCompatActivity {

    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String timeStamp;
    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        timeStamp = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());

        addDailyQuote();
        CheckPermission();
        login();
        prrogressBarStress();
//        progressBar();
        fetchName();
        addDailyUpdates();
    }


    ImageView daily_quote;
    TextView User_welcome_name;
    public void addDailyQuote(){
        User_welcome_name = findViewById(R.id.User_welcome_name);
        daily_quote = findViewById(R.id.daily_quote);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namedb = snapshot.child("First Name").getValue(String.class);
                String namedb1 = snapshot.child("Last Name").getValue(String.class);
                User_welcome_name.setText(namedb + " "+ namedb1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.child(timeStamp).getValue(String.class);
                Picasso.get().load(link).into(daily_quote);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    FitnessOptions fitnessOptions;
    GoogleSignInAccount account;
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    public void login(){
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_POINTS, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

        account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if(!GoogleSignIn.hasPermissions(account, fitnessOptions))
        {
            GoogleSignIn.requestPermissions(this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
        }
        else {
//            Toast.makeText(this, account.getId(), Toast.LENGTH_SHORT).show();
            fetchData();
        }
    }
    public void fetchData(){
        ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
        ZonedDateTime startTime = endTime.minusDays(1);

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .aggregate(DataType.TYPE_HEART_POINTS)
                .bucketByTime(1, TimeUnit.DAYS)
                .enableServerQueries()
                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build();

//        DataReadRequest sleepDataRequest = new DataReadRequest.Builder()
//                .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.MILLISECONDS)
//                .read(DataType.TYPE_ACTIVITY_SEGMENT)
//                .build();
//
//        Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
//                .listSubscriptions()
//                .addOnSuccessListener(subscriptions -> {
//                    for (Subscription sc : subscriptions) {
//
//                        DataType dt = sc.getDataType();
//                        Log.i("Hello", dt.getName());
//                    }
//                });

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if(dataReadResponse.getBuckets().size()>0)
                        {
//                            Log.i("Hello sizes", String.valueOf(dataReadResponse.getBuckets().size()));
                            List<Bucket> bucketList = dataReadResponse.getBuckets();
                            if(bucketList!=null && !bucketList.isEmpty()) {
                                for (Bucket bucket : bucketList) {
                                    DataSet stepDataSet = bucket.getDataSet(DataType.AGGREGATE_HEART_RATE_SUMMARY);
                                    List<DataPoint> dataPoints = stepDataSet.getDataPoints();
                                    for (DataPoint dataPoint : dataPoints) {
                                        int heartrate[] = new int[3];
                                        int pos = 0;
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            float value = Float.parseFloat(dataPoint.getValue(field).toString());
//                                            Log.i("Hello Heart", "Value " + value);
                                            heartrate[pos++] = (int)value;
                                        }
                                        if(heartrate[0]!=0)
                                            displayHeartRate(heartrate);
                                    }
                                }
                            }
                        }

                    }
                });

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if(dataReadResponse.getBuckets().size()>0)
                        {
//                            Log.i("Hello sizes", String.valueOf(dataReadResponse.getBuckets().size()));
                            List<Bucket> bucketList = dataReadResponse.getBuckets();
                            if(bucketList!=null && !bucketList.isEmpty()) {
                                for (Bucket bucket : bucketList) {
                                    DataSet stepDataSet = bucket.getDataSet(DataType.AGGREGATE_DISTANCE_DELTA);
                                    List<DataPoint> dataPoints = stepDataSet.getDataPoints();
                                    for (DataPoint dataPoint : dataPoints) {
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            float value = Float.parseFloat(dataPoint.getValue(field).toString());
//                                            Log.i("Hello Distance", "Value " + value);
                                            progressBarDistance((int)(value));
                                        }
                                    }
                                }
                            }
                        }

                    }
                });

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if(dataReadResponse.getBuckets().size()>0)
                        {
//                            Log.i("Hello sizes", String.valueOf(dataReadResponse.getBuckets().size()));
                            List<Bucket> bucketList = dataReadResponse.getBuckets();
                            if(bucketList!=null && !bucketList.isEmpty()) {
                                for (Bucket bucket : bucketList) {
                                    DataSet stepDataSet = bucket.getDataSet(DataType.AGGREGATE_STEP_COUNT_DELTA);
                                    List<DataPoint> dataPoints = stepDataSet.getDataPoints();
                                    for (DataPoint dataPoint : dataPoints) {
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            float value = Float.parseFloat(dataPoint.getValue(field).toString());
//                                            Log.i("Hello Steps", "Value " + value);
                                            progressBarSteps((int)value);
                                        }
                                    }
                                }
                            }
                        }

                    }
                });

        Fitness.getHistoryClient(this, account)
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if(dataReadResponse.getBuckets().size()>0)
                        {
//                            Log.i("Hello sizes", String.valueOf(dataReadResponse.getBuckets().size()));
                            List<Bucket> bucketList = dataReadResponse.getBuckets();
                            if(bucketList!=null && !bucketList.isEmpty()) {
                                for (Bucket bucket : bucketList) {
                                    DataSet stepDataSet = bucket.getDataSet(DataType.AGGREGATE_HEART_POINTS);
                                    List<DataPoint> dataPoints = stepDataSet.getDataPoints();
                                    for (DataPoint dataPoint : dataPoints) {
                                        float hp = -1;
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            hp = Float.parseFloat(dataPoint.getValue(field).toString());
//                                            Log.i("Hello Heart Points", "Value " + hp);
                                        }
                                        if(hp!=-1)
                                            displayHeartPoints((int)hp);
                                    }
                                }
                            }
                        }

                    }
                });

//        Fitness.getHistoryClient(this, account)
//                .readData(readRequest)
//                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                    @Override
//                    public void onSuccess(DataReadResponse dataReadResponse) {
//                        if(dataReadResponse.getBuckets().size()>0)
//                        {
////                            Log.i("Hello sizes", String.valueOf(dataReadResponse.getBuckets().size()));
//                            List<Bucket> bucketList = dataReadResponse.getBuckets();
//                            if(bucketList!=null && !bucketList.isEmpty()) {
//                                for (Bucket bucket : bucketList) {
//                                    DataSet stepDataSet = bucket.getDataSet(DataType.AGGREGATE_CALORIES_EXPENDED);
//                                    List<DataPoint> dataPoints = stepDataSet.getDataPoints();
//                                    for (DataPoint dataPoint : dataPoints) {
//                                        float hp = -1;
//                                        for (Field field : dataPoint.getDataType().getFields()) {
//                                            hp = Float.parseFloat(dataPoint.getValue(field).toString());
//                                            Log.i("Hello Calories", "Value " + hp);
//                                        }
//                                        if(hp!=-1)
//                                            displayHeartPoints((int)hp);
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                });

//        Fitness.getHistoryClient(this, account)
//                .readData(sleepDataRequest)
//                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>(){
//                      @Override
//                      public void onSuccess(DataReadResponse dataReadResponse) {
//                          List<DataSet> dataSets = dataReadResponse.getDataSets();
//                          for (DataSet dataSet : dataSets) {
//                              for (DataPoint dp : dataSet.getDataPoints()) {
//                                  for (Field field : dp.getDataType().getFields()){
//                                      float value = Float.parseFloat(dp.getValue(field).toString());
//                                      Log.i("Hello google fit Start: ","VAlue"+value);
//                                  }
//                              }
//                          }
//                      }
//                  });
//        Toast.makeText(this, "Hey", Toast.LENGTH_SHORT).show();
    }

    //Fetch name for navigation drawer
    public void fetchName(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namedb = snapshot.child("First Name").getValue(String.class)+" "+snapshot.child("Last Name").getValue(String.class);
                TextView userName = (TextView) findViewById(R.id.nav_name);
                userName.setText(namedb);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void logout(MenuItem item){
        FirebaseAuth.getInstance().signOut();
//        GoogleSignIn.requestPermissions(this,
//                account,
//                fitnessOptions);
        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(i);
        finish();
    }

    public void addDailyUpdates(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String checks = snapshot.child("Daily Checks").getValue(String.class);
                if(!checks.equals(timeStamp)){
                    LinearLayout ll = findViewById(R.id.daily_update);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View RowView = inflater.inflate(R.layout.activity_daily_report, null);
                    ll.addView(RowView, 0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void checkIn(View v){
        startActivity(new Intent(this, dailyUpdateActivity.class));
    }

    public void SpeechRecognition(View v){
        Intent intent = new Intent(this, SpeechRecognitionActivity.class);
        intent.putExtra("Value","0");
        startActivity(intent);
        finish();
    }
    public void meditation(View v){
        Intent intent = new Intent(this, MeditationActivity.class);
        intent.putExtra("Value","0");
        startActivity(intent);
        finish();
    }

    int stressTextID[] = {R.id.stressdaily, R.id.stressweekly, R.id.stressmonthly};
    int stressGraphImage[] = {R.drawable.stress_daily, R.drawable.stress_weekly, R.drawable.stress_monthly};
    public void stressManagement(View v){
        TextView tv = findViewById(v.getId());
        int tag = Integer.parseInt(tv.getTag().toString());
        for(int i=0;i<3;i++){
            if(i == tag){
                tv = findViewById(stressTextID[i]);
                tv.setBackgroundResource(R.drawable.stress_horizontal_line);
                ImageView img = findViewById(R.id.stressGrapf);
                img.setImageResource(stressGraphImage[i]);
            }
            else {
                tv = findViewById(stressTextID[i]);
                tv.setBackground(null);
            }
        }
    }

    public void yoga(View v){
        Intent intent = new Intent(this, YogaActivity.class);
        intent.putExtra("Value","0");
        startActivity(intent);
        finish();
    }
    public void math_game(View v){
        Intent intent = new Intent(this, MathGameActivity.class);
        intent.putExtra("Value","0");
        startActivity(intent);

        finish();
    }
    public void word_building(View v){
        Intent intent = new Intent(this, WordGameActivity.class);
        intent.putExtra("Value","0");
        startActivity(intent);
        finish();
    }
    public void HomeScreen(View v){
    }

    public void profile(View v)
    {
        Intent i = new Intent(getApplicationContext(), ProfileScreenActivity.class);
        startActivity(i);
        finish();
    }
    public void gotohealth(MenuItem item){
//        Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
//        startActivity(i);
//        finish();
    }

    public void gotocontacts(MenuItem item){
        Intent i = new Intent(getApplicationContext(), ContactsActivity.class);
        startActivity(i);
        finish();
    }

    public void gotostrategy(MenuItem item){
        Intent intent = new Intent(this, SpeechRecognitionActivity.class);
        intent.putExtra("Value","1");
        startActivity(intent);

        finish();
    }

    public void gotowatch(MenuItem item){
        Intent i = new Intent(getApplicationContext(), WatchActivity.class);
        startActivity(i);
        finish();
    }

    public void gotogratitude(MenuItem item){
        Intent i = new Intent(getApplicationContext(), GratitudeActivity.class);
        startActivity(i);
        finish();
    }

    public void gotoprofile(MenuItem item){
        Intent i = new Intent(getApplicationContext(), ProfileScreenActivity.class);
        startActivity(i);
        finish();
    }



    public void showdrawer(View v)
    {
        DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
        // If the navigation drawer is not open then open it, if its already open then close it.
        if(!navDrawer.isDrawerOpen(GravityCompat.START)) navDrawer.openDrawer(GravityCompat.START);
        else navDrawer.closeDrawer(GravityCompat.END);
    }

//    int smilyNonTap[] = {R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4, R.drawable.s5};
//    int smilyTapped[] = {R.drawable.s1t,R.drawable.s2t,R.drawable.s3t,R.drawable.s4t,R.drawable.s5t};
////    int tappedSmily = -1;
//    ImageView imageView;
//    public void SmilyTap(View view){
//        GridLayout smilyGrid = findViewById(R.id.smilyGrid);
//        ImageView smily = (ImageView) findViewById(view.getId());
//        int tag = Integer.parseInt(smily.getTag().toString());
//        for(int i=0;i<5;i++)
//        {
//            if(i == tag){
//                smily.setImageResource(smilyTapped[i]);
//            }
//            else {
//                imageView = (ImageView) smilyGrid.getChildAt(i);
//                imageView.setImageResource(smilyNonTap[i]);
//            }
//        }
//    }
int stress;
    public void prrogressBarStress(){
        ProgressBar progressBar;
        TextView progressText;
        stress = 0;

        progressBar = findViewById(R.id.progress_bar_stress);
        progressText = findViewById(R.id.progress_text_stress);
        progressBar.setMax(100);

        final android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                if (stress <= 72) {
                    progressText.setText(String.valueOf(stress));
                    progressBar.setProgress(stress);
                    stress+=1;
                    handler.postDelayed(this, 1);
                }
                else  {
                    handler.removeCallbacks(this);
                }
            }
        }, 1);
    }


    int steps;
    int start_steps;
    public void progressBarSteps(int x){
        steps = x;
        start_steps = 0;
        ProgressBar progressBar;
        TextView progressText;

        progressBar = findViewById(R.id.progress_bar_steps);
        progressText = findViewById(R.id.progress_text_steps);
        progressBar.setMax(8000);

        final android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                if (start_steps <= 8000 && start_steps <= steps) {
                    progressText.setText("" + start_steps);
                    progressBar.setProgress(start_steps);
                    start_steps+=42;
                    handler.postDelayed(this, 1);
                }
                else if(start_steps <= steps){
                    progressText.setText("" + start_steps);
                    start_steps+=5;
                    handler.postDelayed(this, 1);
                }
                else  {
                    handler.removeCallbacks(this);
                }
            }
        }, 1);
    }

    int distance;
    int start_distance;
    public void progressBarDistance(int x){
        distance = x;
        start_distance=0;
        ProgressBar progressBar;
        TextView progressText;

        progressBar = findViewById(R.id.progress_bar_distance);
        progressBar.setMax(5400);
        progressText = findViewById(R.id.progress_text_distance);

        final android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // set the limitations for the numeric
                // text under the progress bar
                if (start_distance <= 5400 && start_distance<=distance) {
                    float val = (float)Math.round(start_distance/1000f*100)/100;
                    progressText.setText(val+" Km");
                    progressBar.setProgress(start_distance);
                    start_distance+=36;
                    handler.postDelayed(this, 1);
                }
                else if(start_distance<=distance){
                    float val = (float)Math.round(start_distance/1000f*100)/100;
                    progressText.setText(val+" Km");
                    start_distance+=5;
                    handler.postDelayed(this, 1);
                }
                else {
                    handler.removeCallbacks(this);
                }
            }
        }, 1);
    }

    TextView heartPoints;
    public void displayHeartPoints(int hp){
        heartPoints = findViewById(R.id.display_heart_points);
        heartPoints.setText(String.valueOf(hp));
    }
    TextView tv;
    public void displayHeartRate(int[] hr){
        tv = findViewById(R.id.min_heartRate);
        tv.setText(String.valueOf(hr[2]));
        tv = findViewById(R.id.max_heartRate);
        tv.setText(String.valueOf(hr[1]));
        tv = findViewById(R.id.average_heartRate);
        tv.setText(String.valueOf(hr[0]));
    }



    public static final String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BODY_SENSORS};
    public void CheckPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION,  1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION,  0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, 2  );
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION,  3);
        }
    }
}