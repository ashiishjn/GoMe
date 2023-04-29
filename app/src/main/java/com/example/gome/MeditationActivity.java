package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

public class MeditationActivity extends AppCompatActivity {


    VideoView videoView;
    MediaPlayer mediaPlayer;

    SeekBar mediaController;

    String key="";

    @Override
    public void onBackPressed() {
        if(key.equals("0")){
            startActivity(new Intent(this, HomeScreenActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        Intent intent = getIntent();
        key = intent.getStringExtra("Value");

        videoView = findViewById(R.id.visualizer);

        mediaController = findViewById(R.id.mediaController);



//        MediaController mediaController = new MediaController(this);
//
//        mediaController.setAnchorView(videoView);
//
//        videoView.setMediaController(mediaController);


        PlayVideo();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                startActivity(new Intent(M.this, HomeScreenActivity.class));
//                finish();
                videoView.suspend();
                PlayVideo();
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.meditation);
        mediaPlayer.start();

        mediaController.setMax(mediaPlayer.getDuration()/1000);
        mediaController.setProgress(0);



//        mediaPlayer.getDuration();
//        Toast.makeText(this, String.valueOf(mediaPlayer.getDuration()), Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        MeditationActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    mediaController.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });

        mediaController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if(mediaPlayer != null){
//                    mediaPlayer.seekTo(i*1000);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null){
                    mediaPlayer.seekTo(seekBar.getProgress()*1000);
                }
//                seekBar.getPro
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null){
                    mediaPlayer.seekTo(seekBar.getProgress()*1000);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(key.equals("0")){
                    startActivity(new Intent(MeditationActivity.this, HomeScreenActivity.class));
                    finish();
                }
                else {
                    Intent intent = new Intent(MeditationActivity.this, YogaActivity.class);
                    intent.putExtra("Value", "1");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void PlayVideo(){
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.visulaizer);
        videoView.start();
    }


    int play_pause = 0;
    public void Play_Pause(View v){
        if(play_pause == 0)
        {
            play_pause = 1;
            mediaPlayer.pause();
            videoView.pause();
            v.setBackgroundResource(R.drawable.play_button);
        }
        else {
            play_pause = 0;
            mediaPlayer.start();
            videoView.start();
            v.setBackgroundResource(R.drawable.pause_button);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mediaPlayer.start();
    }

}