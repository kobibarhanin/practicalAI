package com.knapsackProblem;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class GaStruct implements Comparable <GaStruct>{

    public ArrayList<Integer> geneNums = new ArrayList<Integer>();
    public int geneFitness;

    public GaStruct(ArrayList<Integer> geneNums, int geneFitness) {
        this.geneNums.addAll(geneNums);
        this.geneFitness = geneFitness;
    }

    public GaStruct() {}

    public int compareTo(GaStruct other) {

        Integer num = new Integer(0);
        num = Integer.compare(this.geneFitness, other.geneFitness);
        if(num>=0)return 1;
        else return -1;
    }

}
