package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import  org.apache.commons.lang3.StringUtils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SpeechRecognitionActivity extends AppCompatActivity {

    String s[] = {"Once upon a time, a man found a butterfly that was starting to hatch from its cocoon.",
            "He sat down and watched the butterfly for hours as it struggled to force itself through a tiny hole.",
            "Then, it suddenly stopped making progress and looked like it was stuck.",
            "Therefore, the man decided to help the butterfly out.",
            "He took a pair of scissors and cut off the remaining bit of the cocoon.",
            "The butterfly then emerged easily, although it had a swollen body and small, shriveled wings.",
            "The man thought nothing of it, and he sat there waiting for the wings to enlarge to support the butterfly.",
            "However, that never happened.",
            "The butterfly spent the rest of its life unable to fly, crawling around with small wings and a swollen body.",
            "Despite his kind heart, he didn’t understand that the restricting cocoon",
            "and the struggle needed by the butterfly to get itself through the small hole",
            "were God’s way of forcing fluid from the body of the butterfly",
            "into its wings to prepare itself for flying once it was free."
            };
    List<String> word = new ArrayList<>();

    float accuracy;

    Button speak, ListenAgain, Next;

    TextView textToSpeak, review;

    TextToSpeech textToSpeech;

    SpeechRecognizer mSpeechrechnonizer;
    Intent mSpeechrechnonizerIntent;

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
        setContentView(R.layout.activity_speech_recognition);

        Intent intent = getIntent();
        key = intent.getStringExtra("Value");

//        Log.i("Hello", key);

        shuffle();
        checkPermission();

        speak = findViewById(R.id.speak);
        ListenAgain = findViewById(R.id.ListenAgain);
        textToSpeak = findViewById(R.id.textToSpeak);
        review = findViewById(R.id.review);
        Next = findViewById(R.id.Next);

        mSpeechrechnonizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechrechnonizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechrechnonizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechrechnonizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        mSpeechrechnonizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                review.setText("Couldn't hear anything!");
                gamePlay();
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches!=null){
                    Log.i("Hello Sent", matches.get(0));
                    findSimilarity(word.get(pos), matches.get(0));
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {
                review.setText("Couldn't hear anything!");
                gamePlay();
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        gamePlay();

    }
    public void shuffle(){
        for(int i=0;i<13;i++)
            word.add(s[i]);
    }

    int pos=0;
    public void gamePlay(){

        final android.os.Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                review.setText("");
                textToSpeak.setText(word.get(pos));
                read(word.get(pos));
            }
        }, 1000);

    }

    public void read(String s){
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        ListenAgain.setVisibility(View.VISIBLE);
        speak.setVisibility(View.VISIBLE);

    }

    public void Listen(View v){
        mSpeechrechnonizer.startListening(mSpeechrechnonizerIntent);
        review.setText("");
        speak.setVisibility(View.INVISIBLE);
        ListenAgain.setVisibility(View.INVISIBLE);
    }

    public void ListenAgain(View v){
        ListenAgain.setVisibility(View.INVISIBLE);
        read(word.get(pos));
    }

    public void next(View v){
        if(key.equals("0")){
            startActivity(new Intent(this, HomeScreenActivity.class));
            finish();
        }
        else {
            Intent intent = new Intent(this, MeditationActivity.class);
            intent.putExtra("Value", "1");
            startActivity(intent);
            finish();
        }
    }

    public void review(){
        Log.i("Hello Accuracy", String.valueOf(accuracy));
        if(accuracy < 0.70f){
            review.setText("Please Try again!");
            read(word.get(pos));
        }
        else if(pos == 12) {
            review.setText("Perfect!");
            Next.setVisibility(View.VISIBLE);
        }
        else{
            pos++;
            gamePlay();
        }
    }


    public void findSimilarity(String x, String y) {
        if (x == null && y == null) {
            accuracy = 1.0f;
            review();
            return ;
        }

        if (x == null || y == null) {
            accuracy = 0.0f;
            review();
            return;
        }

        x=x.toUpperCase(Locale.ROOT);
        y=y.toUpperCase(Locale.ROOT);
        accuracy = (float) StringUtils.getJaroWinklerDistance(x, y);
        review();

    }

    private void checkPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!(ContextCompat.
                    checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


}