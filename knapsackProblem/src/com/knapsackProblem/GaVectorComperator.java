package com.knapsackProblem;

import java.util.Comparator;

public class GaVectorComperator implements Comparator <GaStruct>{
    @Override
    public int compare(GaStruct x, GaStruct y) {

        if(x.geneFitness > y.geneFitness) return -1;
        if(x.geneFitness < y.geneFitness) return 1;
        return 0;

    }
}
