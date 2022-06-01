package com.example.trivia.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.trivia.R;

public class Prefs {
    private SharedPreferences preferences;
    private static final String HIGHEST_SCORE = "highest_score";
    private static final String TRIVIA_STATE = "trivia_state";

    public Prefs(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveHighestScore(int score){
        // update and save the highest score
        int current_highest_score = preferences.getInt(HIGHEST_SCORE, 0);
        int highest_score = Math.max(current_highest_score, score);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(HIGHEST_SCORE, highest_score);
        editor.apply(); // saving to disk
    }

    public int getHighestScore(){
        // get data back from shared prefrences
        return preferences.getInt(HIGHEST_SCORE, 0);
    }

    public void setState(int idx){
        preferences.edit().putInt(TRIVIA_STATE, idx).apply();
    }
    public int getState(){
        return preferences.getInt(TRIVIA_STATE, 0);
    }
}
