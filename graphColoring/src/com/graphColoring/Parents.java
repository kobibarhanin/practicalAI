package com.graphColoring;

import java.util.Random;

public class Parents {
    public int indexP1=0;
    public int indexP2=0;


    public void generateUsingTournament(GaVector population){

        int bestFitness = Integer.MAX_VALUE;

        for(int i=0; i<GA.SELECTION_FACTOR.getInt(); i++){
            int randIdx = new Random().nextInt(GA.POPSIZE.getInt());
            if(population.geneVector.get(randIdx).geneFitness <= bestFitness){
                indexP1=randIdx;
                bestFitness=population.geneVector.get(randIdx).geneFitness;
            }
        }

        bestFitness = Integer.MAX_VALUE;

        for(int i=0; i<GA.SELECTION_FACTOR.getInt(); i++){
            int randIdx = new Random().nextInt(GA.POPSIZE.getInt());
            if(population.geneVector.get(randIdx).geneFitness <= bestFitness){
                indexP2=randIdx;
                bestFitness=population.geneVector.get(randIdx).geneFitness;
            }
        }

    }

    public void generateUsingFPS(GaVector population){
        int maxFitness = population.geneVector.get(population.geneVector.size()-1).geneFitness;


        int fitnessSum=0;
        for(int i=0; i<population.geneVector.size();i++){
            fitnessSum+=maxFitness-population.geneVector.get(i).geneFitness;
        }
        int r1= new Random().nextInt(fitnessSum);
        int accum = 0,k=0;
        while(r1>accum){
            accum+=maxFitness-population.geneVector.get(k).geneFitness;
            k++;
        }
        indexP1=k;

        int r2= new Random().nextInt(fitnessSum);
        accum = 0;
        k=0;
        while(r2>accum){
            accum+=maxFitness-population.geneVector.get(k).geneFitness;
            k++;
        }
        indexP2=k;
    }

}
