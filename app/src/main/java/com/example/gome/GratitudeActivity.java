package com.example.gome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Random;

public class GratitudeActivity extends AppCompatActivity {

    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();

    EditText ed;
    Button submit;

    String timeStamp;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gratitude);

        timeStamp = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());

        ed = findViewById(R.id.answer);
        submit = findViewById(R.id.submit);

        checkForDailyCheckIn();

    }

    public void checkForDailyCheckIn(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namedb = snapshot.child("Gratitude").getValue(String.class);
                if(namedb.equals(timeStamp))
                {
                    Toast.makeText(GratitudeActivity.this, "You have already filled it today.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GratitudeActivity.this, HomeScreenActivity.class));
                    finish();
                }
                else
                    fetchQuestion();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    public void fetchQuestion(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Gratitude").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Random random = new Random();
                int randomNumber = (int)(Math.random() * 12) + 1;
                String namedb = snapshot.child(String.valueOf(randomNumber)).getValue(String.class);
                TextView question = (TextView) findViewById(R.id.question);
                question.setText(namedb);
                ed.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void submit(View v){
        if(ed.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Thank you for filling it up", Toast.LENGTH_SHORT).show();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
            reference.child("Gratitude").setValue(timeStamp);
            startActivity(new Intent(this, HomeScreenActivity.class));
        }
    }
}