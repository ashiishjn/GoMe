package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class YogaActivity extends AppCompatActivity {

    VideoView videoView;

    String key="";

    @Override
    public void onBackPressed() {
        if(key.equals("0")){
            startActivity(new Intent(YogaActivity.this, HomeScreenActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoga);

        Intent intent = getIntent();
        key = intent.getStringExtra("Value");

        videoView = findViewById(R.id.videView);

        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.yoga);

        MediaController mediaController = new MediaController(this);

        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);

        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(key.equals("0")){
                    startActivity(new Intent(YogaActivity.this, HomeScreenActivity.class));
                    finish();
                }
                else {
                    Intent intent = new Intent(YogaActivity.this, OptionActivity.class);
                    intent.putExtra("Value", "1");
                    startActivity(intent);
                    finish();
                }

            }
        });



    }
}