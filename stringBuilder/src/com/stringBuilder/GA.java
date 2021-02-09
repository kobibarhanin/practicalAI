package com.stringBuilder;

import java.util.Random;

public enum GA {

    SELECTION_FACTOR(50),
    POPSIZE(2048),
    MAXITER(16384),
    ELITRATE(0.1f),
    MUTATIONRATE(0.25f),
    MUTATION(new Random().nextInt(32767) * MUTATIONRATE.get());


    private float value;
    GA(final float newValue) {
        value = newValue;
    }
    public float get() { return value; }


    private String target;
    GA(final String newValue) {
        target = newValue;
    }
    public String getTarget() { return target; }
}
