package com.example.gome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    Boolean flag = true;
    EditText username, pw;
    ImageButton showpw;
    Button signup, login;
    String b;
    ProgressDialog p;

    DatabaseReference reference;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();



                //values assignment
        setContentView(R.layout.activity_log_in);
        username = (EditText) findViewById(R.id.username);
        pw = (EditText) findViewById(R.id.password);
        showpw = (ImageButton) findViewById(R.id.showpw);
        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);
        p = new ProgressDialog(this);

        //show password functionality
        showpw.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
        showpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    showpw.setBackgroundResource(R.drawable.ic_remove_red_eye_black_24dp);
                    pw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = false;
                } else {
                    showpw.setBackgroundResource(R.drawable.ic_visibility_off_black_24dp);
                    pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = true;
                }
            }
        });

        //onclick for signup
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(i);
                finish();
            }
        });

        //onclick for login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString();
                String password = pw.getText().toString();

                if(email.isEmpty()){
                    username.setError("Email is Empty");
                    username.requestFocus();
                }
                else if(password.isEmpty()){
                    pw.setError("Password is Empty");
                    pw.requestFocus();
                }
                else{
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                callActivity();
                            }
                            else{
                                  Toast.makeText(LogInActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void callActivity()
    {
        String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                String status = snapshot.child("Questions Status").getValue(String.class);
//                if(status.equals("0"))
//                {
//                    startActivity(new Intent(LogInActivity.this, QuestionScreen.class));
//                    finish();
//                }
//                else
//                {
//                Toast.makeText(LogInActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LogInActivity.this, HomeScreenActivity.class));
                    finish();
//                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }





    public void forgot(View v)
    {
        Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(i);
        finish();
    }
}