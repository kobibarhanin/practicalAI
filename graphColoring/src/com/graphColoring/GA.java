package com.graphColoring;

import java.util.Random;

public enum GA {

    SELECTION_FACTOR(10),
//    SELECTION_FACTOR(5),
    POPSIZE(200),
//    MAXITER(16384),
    MAXITER(600),
    ELITRATE(0.4f),
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
