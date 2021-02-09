package com.knapsackProblem;

import java.util.Random;

public enum GA {

    POPSIZE(2048),
    ELITRATE(0.10f),
    MUTATIONRATE(0.25f),
    MUTATION(new Random().nextInt(32767) * MUTATIONRATE.get());



    private float value;
    GA(final float newValue) {
        value = newValue;
    }
    public float get() { return value; }

    GA(final int newValue) {
        target = newValue;
    }
    private int target;
    public int getInt() { return target; }



}
