package com.nguyentrongtri.ailatrieuphu4.model;

public class HighScore {
    String name;
    int score;

    public HighScore(String name, int score) {
        this.name = name;
        this.score = score;
    }
    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }

}
