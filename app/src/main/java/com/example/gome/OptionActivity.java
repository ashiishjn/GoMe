package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OptionActivity extends AppCompatActivity {


    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
    }

    public void PlayGames(View v){
        Intent intent = new Intent(this, MathGameActivity.class);
        intent.putExtra("Value","1");
        startActivity(intent);
        finish();
    }

    public void HomeScreen(View v){
        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }
}