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
import com.example.trivia.utils.Prefs;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private int currQuestionIndex = 0;
    private Score score;
    Prefs prefs;
    List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        score = new Score();
        prefs = new Prefs(MainActivity.this);
        questions = new Repository().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                Log.d("Main", "onCreate: " + questionArrayList);
                binding.questionTextView.setText(questions.get(currQuestionIndex).getAnswer());
                updateCounter();
                updateScore();
                updateHighestScore();
            }
        });
        binding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNextQuestion();
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

    private void getNextQuestion(){
        currQuestionIndex = (currQuestionIndex+1) % questions.size();
        updateQuestion();
    }

    private void checkAnswer(boolean userChoose) {
        boolean answer = questions.get(currQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if (userChoose == answer){ // correct answer
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            score.increaseScore();
            prefs.saveHighestScore(score.getScore());
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
        binding.highestScore.setText(String.format("%s %d", getString(R.string.Highest_score), prefs.getHighestScore()));
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
                getNextQuestion();
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
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}