package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactsActivity extends AppCompatActivity {

    public void onBackPressed() {
        startActivity(new Intent(this, HomeScreenActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
    }

    public void Call(View v){
        RelativeLayout rl = (RelativeLayout) findViewById(v.getId());

        TextView tv = (TextView) rl.getChildAt(2);

        Intent intent = new Intent(Intent.ACTION_DIAL);
        String temp = "tel:" + tv.getText().toString();
        intent.setData(Uri.parse(temp));
        startActivity(intent);
    }
}