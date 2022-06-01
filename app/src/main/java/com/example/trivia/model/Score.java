package com.example.trivia.model;

public class Score {
    private int score;

    public Score() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void increaseScore(){
        score+= 10;
    }
    public void decreaseScore(){
        if (score>=10) score-= 10;
    }
}
