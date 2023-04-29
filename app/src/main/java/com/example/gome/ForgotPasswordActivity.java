package com.example.gome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth auth;

    EditText mailId;

    @Override
    public void onBackPressed() {

        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mailId = findViewById(R.id.mail);
        auth = FirebaseAuth.getInstance();

        //onclick for login
        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = mailId.getText().toString();
                if(emailId.isEmpty()){
                    mailId.setError("Email is Empty");
                    mailId.requestFocus();
                }
                else
                {
                    auth.sendPasswordResetEmail(emailId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                startActivity(new Intent(ForgotPasswordActivity.this, LogInActivity.class));
                                finish();

                                Toast.makeText(ForgotPasswordActivity.this, "Mail sent successfully!", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(ForgotPasswordActivity.this, "Email Id does not exists!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

    }
}