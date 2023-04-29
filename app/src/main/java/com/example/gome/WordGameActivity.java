package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WordGameActivity extends AppCompatActivity {

    String words[] = {"AIDE", "AIDS", "DAIS", "DATE", "DESI", "DIES",
            "DIET", "EDIT", "IDEA", "SIDE", "SAID", "TIED", "TIDE",
            "EATS", "EAST", "SEAT",  "SATI", "SITE", "TIES", "TEAS", "TASE", "SATE", "EAST" };

    String word="";

    String prev_words[] = {"","",""};
    int count=0;

    TextView text;

    String key="";

    public void onBackPressed() {
        if(key.equals("0")){
            startActivity(new Intent(this, HomeScreenActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_game);

        Intent intent = getIntent();
        key = intent.getStringExtra("Value");

        text = findViewById(R.id.text);
    }

    public void Check(View v){
        if(word.length() < 4){
            Toast.makeText(this, "It should be a four-letter word!", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i=0;i<count;i++){
                if(prev_words[i].equals(word)){
                    Toast.makeText(this, "You can not repeat a word again!", Toast.LENGTH_SHORT).show();
                    word="";
                    text.setText("");
                    return;
                }
            }
            int flag = 0;
            for(int i = 0; i<words.length; i++){
                if(words[i].equals(word))
                {
                    flag=1;
                    Toast.makeText(this, "Perfect", Toast.LENGTH_SHORT).show();
                    prev_words[count++]=word;
                    break;
                }
            }
            if(flag == 0)
                Toast.makeText(this, "Word not found!", Toast.LENGTH_SHORT).show();
            word="";
            text.setText("");
            if(count == 3)
            {
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
            }
        }
    }

    public void formWord(View v){
        Button bt = findViewById(v.getId());
        if(word.indexOf(bt.getText().toString()) == -1) {
            if (text.getText().toString().length() < 4) {
                word += bt.getText().toString();
                text.setText(word);
            }
        }
    }

    public void backspace(View v){
        if(text.getText().toString().length()>0)
        {
            word = word.substring(0,word.length()-1);
            text.setText(word);
        }
    }
}