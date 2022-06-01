package com.example.trivia;

import static java.lang.String.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String HIGHEST_SCORE = "HIGHEST_SCORE_PREF";
    private ActivityMainBinding binding;
    private int currQuestionIndex = 0;
    private Score score;
    private int highest_score = 0;
    List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();

        questions = new Repository().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                Log.d("Main", "onCreate: " + questionArrayList);
                binding.questionTextView.setText(questions.get(currQuestionIndex).getAnswer());
                updateCounter();
                updateScore();
                getHighestScore();
            }
        });
        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currQuestionIndex = (currQuestionIndex+1) % questions.size();
                updateQuestion();
            }
        });
        binding.buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currQuestionIndex>0){
                    currQuestionIndex = (currQuestionIndex-1) % questions.size();
                    updateQuestion();
                }

            }
        });
        binding.buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
                updateQuestion();
                updateScore();

            }
        });
        binding.buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
                updateQuestion();
                updateScore();
            }
        });
    }

    private void checkAnswer(boolean userChoose) {
        boolean answer = questions.get(currQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if (userChoose == answer){ // correct answer
            snackMessageId = R.string.correct_answer;
            score.increaseScore();
            fadeAnimation();
            updateHighestScore();
        }
        else { // incorrect answer
            snackMessageId = R.string.incorrect_answer;
            shakeAnimation();
            score.decreaseScore();

            }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT).show();
    }

    private void updateHighestScore() {
        // update and save the highest score
        highest_score = Math.max(score.getScore(), highest_score);
        binding.highestScore.setText(String.format("%s %d", getString(R.string.Highest_score), highest_score));
        SharedPreferences sharedPreferences = getSharedPreferences(HIGHEST_SCORE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("highest_score", highest_score);
        editor.apply(); // saving to disk
    }

    private void getHighestScore(){
        // get data back from shared prefrences
        SharedPreferences getSharedData = getSharedPreferences(HIGHEST_SCORE, MODE_PRIVATE);
        highest_score = getSharedData.getInt("highest_score", highest_score);
        updateHighestScore();
    }

    private void updateCounter() {
        binding.textViewOutOf.setText(format("Question: %d/%d", currQuestionIndex+1, questions.size()));
    }
    private void updateScore() {
        binding.scoreTextView.setText(format("Your score: %d", score.getScore()));
    }
    private void updateQuestion() {
        binding.questionTextView.setText(questions.get(currQuestionIndex).getAnswer());
        updateCounter();
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        binding.cardView.setAnimation(shake);
        ColorStateList oldColors = binding.questionTextView.getTextColors();
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(oldColors);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        ColorStateList oldColors = binding.questionTextView.getTextColors();
        binding.cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(oldColors);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}