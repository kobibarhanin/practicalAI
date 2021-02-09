package com.stringBuilder;

import java.util.ArrayList;

public class GaVector {
    ArrayList<GaStruct> geneVector = new ArrayList<GaStruct>();
    float fitnessAvarrege = 0;

    public GaVector(){

    }
    public GaVector(ArrayList<GaStruct> geneVector) {
        this.geneVector = geneVector;
    }

    public float calcFitnessAvarege(){
        float total = 0;
        for(int i=0; i<geneVector.size(); i++){
            total+=(float)geneVector.get(i).geneFitness;
        }
        fitnessAvarrege=total/(float)geneVector.size();
        return fitnessAvarrege;
    }

    public float calcStandardDeviation(){

        float avg = (fitnessAvarrege==0) ? calcFitnessAvarege(): fitnessAvarrege;
        float std = 0;

        for(int i=0; i<geneVector.size(); i++) {
            std += Math.pow((float)geneVector.get(i).geneFitness - avg, 2);
        }

        return (float)Math.sqrt(std/(float)geneVector.size());
    }

}
