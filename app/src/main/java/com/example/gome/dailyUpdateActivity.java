package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class dailyUpdateActivity extends AppCompatActivity {

    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String timeStamp;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_update);

        timeStamp = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
    }

    int smilyNonTap[] = {R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4, R.drawable.s5};
    int smilyTapped[] = {R.drawable.s1t,R.drawable.s2t,R.drawable.s3t,R.drawable.s4t,R.drawable.s5t};

    int SocialNonTap[] = {R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4};
    int SocialTap[] = {R.drawable.as1,R.drawable.as2,R.drawable.as3,R.drawable.as4};
    int SocialImageId[] = {R.id.a1,R.id.a2,R.id.a3,R.id.a4};

    int EnergyNonTap[] = {R.drawable.q1,R.drawable.q2,R.drawable.q3,R.drawable.q4};
    int EnergyTap[] = {R.drawable.q1s,R.drawable.q2s,R.drawable.q3s,R.drawable.q4s};
    int EnergyImageId[] = {R.id.q1,R.id.q2,R.id.q3,R.id.q4};
    
    boolean b[] = new boolean[3];

    ImageView imageView;
    public void SmilyTap(View view){
        b[0]=true;
        GridLayout smilyGrid = findViewById(R.id.smilyGrid);
        ImageView smily = (ImageView) findViewById(view.getId());
        int tag = Integer.parseInt(smily.getTag().toString());
        for(int i=0;i<5;i++)
        {
            if(i == tag){
                smily.setImageResource(smilyTapped[i]);
            }
            else {
                imageView = (ImageView) smilyGrid.getChildAt(i);
                imageView.setImageResource(smilyNonTap[i]);
            }
        }
    }

    public void EnergyTap(View v){
        b[1]=true;
        int tag = Integer.parseInt(v.getTag().toString());
        imageView = findViewById(v.getId());
        imageView.setImageResource(EnergyTap[tag]);
        for(int i=0;i<4;i++){
            if(i!=tag){
                imageView = findViewById(EnergyImageId[i]);
                imageView.setImageResource(EnergyNonTap[i]);
            }
        }
    }

    public void SocialTap(View v){
        b[2]=true;
        int tag = Integer.parseInt(v.getTag().toString());
        imageView = findViewById(v.getId());
        imageView.setImageResource(SocialTap[tag]);
        for(int i=0;i<4;i++){
            if(i!=tag){
                imageView = findViewById(SocialImageId[i]);
                imageView.setImageResource(SocialNonTap[i]);
            }
        }
    }
    public void Submit(View v){
        if(b[0] && b[1] && b[2]){
            Toast.makeText(this, "Thank you for filling it up", Toast.LENGTH_SHORT).show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
            reference.child("Daily Checks").setValue(timeStamp);
            startActivity(new Intent(this, HomeScreenActivity.class));
        }
    }
}