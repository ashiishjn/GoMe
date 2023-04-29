package com.example.gome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathGameActivity extends AppCompatActivity {

    String questions[] = {"578 + 222 =", "8 * 15 =", "145 - 55 =", "126 / 3 =", "0, 1, 1, 2, 3, ?, 8",
            "2, 3, 5, 8, ?, 17, 23", "1, 8, 27, ?, 125"};

    String answers[] = {"800", "120", "90", "42", "5", "12", "64"};

    List<Integer> l = new ArrayList<>();


    TextView questionType, question;
    EditText answer;

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
        setContentView(R.layout.activity_math_game);

        Intent intent = getIntent();
        key = intent.getStringExtra("Value");

        questionType = findViewById(R.id.questionType);
        question = findViewById(R.id.question);
        answer = findViewById(R.id.answer);

        shuffle();
        playGame();
    }

    public void shuffle(){
        for(int i=0;i<7; i++){
            l.add(i);
        }
        Collections.shuffle(l);
    }

    int pos = 0;
    public void playGame(){
        if(pos == 4){
            if(key.equals("0")){
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
            }
            else {
                Intent intent = new Intent(this, WordGameActivity.class);
                intent.putExtra("Value", "1");
                startActivity(intent);
                finish();
            }
        }
        else {
            if (l.get(pos) <= 3)
                questionType.setText("Solve this expression.");
            else
                questionType.setText("Guess the missing number in the Series.");
            question.setText(questions[l.get(pos)]);
        }
    }

    public void Next(View v){
        if(answer.getText().toString().equals("")){
            Toast.makeText(this, "Please answer the question.", Toast.LENGTH_SHORT).show();
        }
        else {
            String ans = answer.getText().toString().trim();
            if(ans.equals(answers[l.get(pos)])){
                Toast.makeText(this, "Perfect", Toast.LENGTH_SHORT).show();
                pos++;
                playGame();
            }
            else {
                Toast.makeText(this, "Incorrect Answer! Please try again.", Toast.LENGTH_SHORT).show();

            }
            answer.setText("");
        }
    }

}