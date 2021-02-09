package com.stringBuilder;

import static java.lang.Math.abs;

public class GaStruct implements Comparable <GaStruct>{
    public String geneString;
    public int geneFitness;
    public int age = 0;


    public GaStruct(String geneString, int geneFitness) {
        this.geneString = geneString;
        this.geneFitness = geneFitness;
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
