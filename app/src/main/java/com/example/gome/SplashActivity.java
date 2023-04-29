package com.example.gome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final Handler handler = new Handler();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(currentUser != null){
                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    reference.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

//                            String status = snapshot.child("Questions Status").getValue(String.class);
//                            if(status.equals("0"))
//                            {
//                                startActivity(new Intent(SplashActivity.this, QuestionScreen.class));
//                                finish();
//                            }
//                            else
//                            {
                                startActivity(new Intent(SplashActivity.this, HomeScreenActivity.class));
                                finish();
//                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else{
                    startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                    finish();
                }
            }
        }, 1300);
    }
}