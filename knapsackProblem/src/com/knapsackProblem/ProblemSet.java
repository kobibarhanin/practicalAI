package com.knapsackProblem;

import java.util.ArrayList;
import java.util.Arrays;

public class ProblemSet {

    public int problemNumber;

    public ProblemSet(int problemNumber) {
        this.problemNumber = problemNumber;
        build();
    }

    public void setProblem(int problem){
        problemNumber=problem;
    }

    public  Integer [] weightsArr1 = {23,31,29,44,53,38,63,85,89,82};
    public  Integer [] weightsArr2 = {12,7,11,8,9};
    public  Integer [] weightsArr3 = {56,59,80,64,75,17};
    public  Integer [] weightsArr4 = {31,10,20,19,4,3,6};
    public  Integer [] weightsArr5 = {25,35,45,5,25,3,2,2};
    public  Integer [] weightsArr6 = {41,50,49,59,55,57,60};
    public  Integer [] weightsArr7 = {70,73,77,80,82,87,90,94,98,106,110,113,115,118,120};
    public  Integer [] weightsArr8 = {382745,799601,909247,729069,467902,44328, 34610, 698150, 823460, 903959, 853665, 551830, 610856, 670702, 488960, 951111, 323046, 446298, 931161, 31385, 496951, 264724, 224916, 169684};

    public  Integer [] valuesArr1 = {92,57,49,68,60,43,67,84,87,72};
    public  Integer [] valuesArr2 = {24,13,23,15,16};
    public  Integer [] valuesArr3 = {50,50,64,46,50,5};
    public  Integer [] valuesArr4 = {70,20,39,37,7,5,10};
    public  Integer [] valuesArr5 = {350,400,450,20,70,8,5,5};
    public  Integer [] valuesArr6 = {442,525,511,593,546,564,617};
    public  Integer [] valuesArr7 = {135,139,149,150,156,163,173,184,192,201,210,214,221,229,240};
    public  Integer [] valuesArr8 = { 825594, 1677009, 1676628, 1523970, 943972, 97426, 69666, 1296457, 1679693, 1902996, 1844992, 1049289, 1252836, 1319836, 953277, 2067538, 675367, 853655, 1826027, 65731, 901489, 577243, 466257, 369261};

    public  int maxWeight1 = 165;
    public  int maxWeight2 = 26;
    public  int maxWeight3 = 190;
    public  int maxWeight4 = 50;
    public  int maxWeight5 = 104;
    public  int maxWeight6 = 170;
    public  int maxWeight7 = 750;
    public  int maxWeight8 = 6404180;

    public  int [] maxWeights = {maxWeight1,maxWeight2,maxWeight3,maxWeight4,
            maxWeight5,maxWeight6,maxWeight7,maxWeight8};

    public  Integer [][] weightsArrs = {weightsArr1,weightsArr2,weightsArr3,weightsArr4,
            weightsArr5,weightsArr6,weightsArr7,weightsArr8};

    public  Integer [][] valuesArrs = {valuesArr1,valuesArr2,valuesArr3,valuesArr4,
            valuesArr5,valuesArr6,valuesArr7,valuesArr8};

    public void build(){
        weights.addAll(Arrays.asList(weightsArrs[problemNumber-1]));
        values.addAll(Arrays.asList(valuesArrs[problemNumber-1]));
        maxWeight = maxWeights[problemNumber-1];
        targetSize = weights.size();
    }

    public  ArrayList<Integer> weights = new ArrayList<Integer>();
    public  ArrayList<Integer> values = new ArrayList<Integer>();
    public  int maxWeight;
    public  int targetSize;

}
