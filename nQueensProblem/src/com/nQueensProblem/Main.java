package com.nQueensProblem;

import java.util.*;

import static java.lang.Math.abs;

public class Main {

    public static int boardSize=15;
    public static int mutationMethod=1;
    public static int matingMethod=1;

    public static ArrayList<ArrayList<Integer>> listPermutations(ArrayList<Integer> list) {
        ArrayList<ArrayList<Integer>> returnMe = new ArrayList<ArrayList<Integer>>();

        for(int i=0; i<GA.POPSIZE.getInt(); i++){
            ArrayList<Integer> shuffleList = new ArrayList<Integer>(list);
            Collections.shuffle(shuffleList);
            returnMe.add(shuffleList);
        }

        return returnMe;
    }

    public static ArrayList<Integer> generateOutline(int size){
        ArrayList<Integer> outline = new ArrayList<Integer>();
        for(int i=1; i<=size; i++){
            outline.add(i);
        }
        return outline;
    }

    public static void init_population(GaVector population, GaVector buffer) {

        int tsize = boardSize;
        ArrayList<Integer> outline = new ArrayList<Integer>();
        outline = generateOutline(tsize);

        ArrayList<ArrayList<Integer>> permutations = new ArrayList<ArrayList<Integer>>();
        permutations = listPermutations(outline);

        long factorialTarget = factorial(boardSize);
        long popsize;
        if(GA.POPSIZE.getInt()>factorialTarget && factorialTarget>0) popsize= factorialTarget;
        else popsize = GA.POPSIZE.getInt();

        for (int i=0; i<popsize; i++) {

            GaStruct citizen = new GaStruct(permutations.get(i),0);
            population.geneVector.add(citizen);
            buffer.geneVector.add(new GaStruct());
        }

    }

    public static long factorial(long number) {
        if (number <= 1) return 1;
        else return number * factorial(number - 1);
    }

    public static void calc_fitness(GaVector population) {

        int fitness;

        for (int i=0; i<GA.POPSIZE.getInt(); i++) {
            fitness= calcGeneFitness(population.geneVector.get(i));
            population.geneVector.get(i).geneFitness = fitness;
        }
    }

    public static int calcGeneFitness(GaStruct gene){
        int tsize = boardSize;
        int fitness = 0;
        for (int i=0; i<tsize; i++) {
            for(int j=0; j<tsize; j++){
                int x1=gene.geneNums.get(i);
                int x2=gene.geneNums.get(j);
                if((i!=j) && (abs(x1-x2)==abs(i-j)|| x1 == x2)){
                    fitness++;
                }
            }
        }
        return fitness;
    }

    public static void sort_by_fitness(GaVector population){
        Collections.sort(population.geneVector, new GaVectorComperator());
    }

    public static void elitism(GaVector population, GaVector buffer, int esize) {
        for (int i=0; i<esize; i++) {
            buffer.geneVector.get(i).geneNums = population.geneVector.get(i).geneNums;
            buffer.geneVector.get(i).geneFitness = population.geneVector.get(i).geneFitness;
        }
    }

    public static void mutateByWorstFitness(GaStruct gene) {

        int tsize = boardSize;
        int pos1 = calcWorstFitnessObject(gene);
        int numAtPos1 = gene.geneNums.get(pos1);
        int pos2 = new Random().nextInt(tsize);
        int numAtPos2 = gene.geneNums.get(pos2);
        gene.geneNums.set(pos1,numAtPos2);
        gene.geneNums.set(pos2,numAtPos1);
    }

    public static void mutateRandom(GaStruct gene) {

        int tsize = boardSize;
        int pos1 = new Random().nextInt(tsize);
        int numAtPos1 = gene.geneNums.get(pos1);
        int pos2 = new Random().nextInt(tsize);
        int numAtPos2 = gene.geneNums.get(pos2);
        gene.geneNums.set(pos1,numAtPos2);
        gene.geneNums.set(pos2,numAtPos1);
    }

    public static int calcWorstFitnessObject(GaStruct gene){

        int tsize = boardSize;
        int worstObjectAmount = 0;
        int worstObjectIndex = -1;
        for (int i=0; i<tsize; i++) {
            int currentObjectAmount = 0;
            for(int j=0; j<tsize; j++){
                int x1=gene.geneNums.get(i);
                int x2=gene.geneNums.get(j);
                if((i!=j) && abs(x1-x2)==abs(i-j)){
                    currentObjectAmount++;
                }
            }
            if(currentObjectAmount >= worstObjectAmount){
                worstObjectAmount = currentObjectAmount;
                worstObjectIndex = i;
            }
        }
        return worstObjectIndex;
    }

    public static void mate(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.get() * GA.ELITRATE.get());
        int tsize = boardSize;

        elitism(population, buffer, esize);

        // Mate the rest
        for (int i=esize; i<GA.POPSIZE.getInt(); i++) {
            buffer.geneVector.get(i).geneNums = (matingMethod==1)? crossoverUsingMasterSlave(population): crossoverRandom(population);
            if (new Random().nextInt() < GA.MUTATION.get()){
                if(mutationMethod==1) mutateByWorstFitness(buffer.geneVector.get(i));
                else mutateRandom(buffer.geneVector.get(i));
            }
        }
    }

    public static ArrayList<Integer> crossoverRandom(GaVector population){
        int tsize = boardSize;
        int spos = new Random().nextInt(tsize);

        Parents parents = new Parents();
        parents.generateUsingTournament(population);
        ArrayList<Integer> parent1 = new ArrayList<Integer>(population.geneVector.get(parents.indexP1).geneNums);
        ArrayList<Integer> parent2 = new ArrayList<Integer>(population.geneVector.get(parents.indexP2).geneNums);

        parent1 = new ArrayList<Integer>(parent1.subList(0, spos));
        ArrayList<Integer> output = new ArrayList<Integer>(parent1.subList(0, spos));

        for (int i = 0; i<parent2.size(); i++){
            boolean diff=true;
            for(int j=0; j<spos; j++){

                diff&=(parent1.get(j)==parent2.get(i)) ? false: true;
            }
            if(diff)output.add(parent2.get(i));
        }
        return output;
    }

    public static ArrayList<Integer> crossoverUsingMasterSlave(GaVector population){
        int tsize = boardSize;
        int spos = new Random().nextInt(tsize);
        Parents parents = new Parents();
        parents.generateUsingTournament(population);

        ArrayList<Integer> parent1 = new ArrayList<Integer>(population.geneVector.get(parents.indexP1).geneNums);
        ArrayList<Integer> parent2 = new ArrayList<Integer>(population.geneVector.get(parents.indexP2).geneNums);

        ArrayList<Integer> master = (population.geneVector.get(parents.indexP1).geneFitness<population.geneVector.get(parents.indexP2).geneFitness)
                ? parent1:parent2;
        ArrayList<Integer> slave = (population.geneVector.get(parents.indexP1).geneFitness<population.geneVector.get(parents.indexP2).geneFitness)
                ? parent2:parent1;
        ArrayList<Integer> output = new ArrayList<Integer>(population.geneVector.get(parents.indexP1).geneNums);

        for (int i = spos; i<tsize; i++){
            if(!hasNumber(master,i,slave.get(i))) output.set(i,slave.get(i));
        }
        return output;
    }

    public static boolean hasNumber(ArrayList<Integer> arr, int upto ,Integer num){
        for(int i=0; i<=upto; i++){
            if(arr.get(i)==num) return true;
        }
        return false;
    }

    public static void print_best(GaVector gav) {
        System.out.print("Best: " + gav.geneVector.get(0).geneNums + " (" + gav.geneVector.get(0).geneFitness + ")" );
    }

    public static void printGenerationData(int generation, GaVector population, double generationTime){
        System.out.print("Generation " + (generation+1) + ": ");;
        print_best(population);		// print the best one
        System.out.print(" Population Fitness Avarege: " + population.calcFitnessAvarege());
        System.out.print(" Population Fitness STD: " + population.calcStandardDeviation());
        System.out.println(" Generation development time: " + generationTime);
//        System.out.println("============================");
    }

    public static double timePassedFrom(Date from){
        Date currentTime = new Date();
        return ((float)(currentTime.getTime()-from.getTime()))/1000;
    }

    public static void printBoard(ArrayList<Integer> geneNums) {
        for (int i = 0; i < geneNums.size(); i++) {
            for (int j = 0; j < geneNums.size(); j++) {
                if (geneNums.get(i) == j)
                    System.out.print("|Q");
                else
                    System.out.print("| ");
            }
            System.out.println("|");
        }
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Genetic Queens problem solver! ");
        System.out.println("This program will solve the N-Queens problem for a given n. ");
        System.out.println("The result is a permutation of n so that for a given index i queens are placed on the board in locations (i,result[i]).");
        System.out.println("Select Mutation method: <1,2> " +
                "\n1.Worst Fitness mutates." +
                "\n2.Random.");
        mutationMethod = input.nextInt();
        System.out.println("Select Crossover method: <1,2> " +
                "\n1.Master-Slave." +
                "\n2.Random.");
        matingMethod = input.nextInt();

        System.out.print("Enter board size to develop: ");
        input.nextLine();
        boardSize=input.nextInt();

        Date startTime = new Date();
        double time = 0;

        GaVector population  = new GaVector();
        GaVector buffer  = new GaVector();

        init_population(population, buffer);

        for (int i=0; i<GA.MAXITER.getInt(); i++) {

            Date startTime2 = (i==0) ? startTime : new Date();

            calc_fitness(population);
            sort_by_fitness(population);

            printGenerationData(i,population, time + timePassedFrom(startTime2));

            if (population.geneVector.get(0).geneFitness == 0) {
                System.out.println("Solution:");
                printBoard(population.geneVector.get(0).geneNums);
                System.out.println("Elapsed Time: " + timePassedFrom(startTime));
                break;
            }

            Date newGenerationTime = new Date();

            mate(population, buffer);
            population = buffer;

            time = timePassedFrom(newGenerationTime);
        }

    }

}
