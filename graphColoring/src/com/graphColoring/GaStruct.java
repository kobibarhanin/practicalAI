package com.graphColoring;

import java.util.ArrayList;

public class GaStruct implements Comparable <GaStruct>{

    public ArrayList<Integer> geneNums = new ArrayList<Integer>();
    public int geneFitness;
    public int colors;

    public GaStruct(ArrayList<Integer> geneNums, int geneFitness, int colors) {
        this.geneNums.addAll(geneNums);
        this.geneFitness = geneFitness;
        this.colors = colors;
    }

    public GaStruct() {
    }

    public int compareTo(GaStruct other) {

        Integer num = new Integer(0);
        num = Integer.compare(this.geneFitness, other.geneFitness);
        if(num>=0)return 1;
        else return -1;
    }

}
