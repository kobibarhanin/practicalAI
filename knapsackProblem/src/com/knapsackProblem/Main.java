package com.knapsackProblem;

import java.util.*;

import static java.lang.Math.abs;

public class Main {

    public static int maxWeight;
    public static int targetSize;
    public static ArrayList<Integer> weights;
    public static ArrayList<Integer> values;

    public static int maxIterations = 10;

    public static ArrayList<Integer> generateOutline(int size){
        ArrayList<Integer> outline = new ArrayList<Integer>();

        int rand = new Random().nextInt(size/2);

        for(int i=0; i<size; i++){
            if(i<rand) outline.add(1);
            else outline.add(0);
        }
        return outline;
    }

    public static void init_population(GaVector population, GaVector buffer) {

        int tsize = targetSize;
        ArrayList<Integer> outline = new ArrayList<Integer>();

        int i=0;
        while (i<GA.POPSIZE.getInt()) {
            outline=generateOutline(tsize);
            Collections.shuffle(outline);
            GaStruct citizen = new GaStruct(outline,0);
            if(calcWeight(citizen)<=maxWeight) {
                population.geneVector.add(citizen);
                buffer.geneVector.add(new GaStruct());
                i++;
            }
        }

    }

    public static int cacValue (GaStruct gene){
        int value = 0;
        for(int i=0; i<gene.geneNums.size(); i++){
            if(gene.geneNums.get(i)==1) value+=values.get(i);
        }
        return value;
    }

    public static int calcWeight (GaStruct gene){
        int weight = 0;
        for(int i=0; i<gene.geneNums.size(); i++){
            if(gene.geneNums.get(i)==1) weight+=weights.get(i);
        }
        return weight;
    }

    public static void calc_fitness(GaVector population) {
        int fitness;
        for (int i=0; i<GA.POPSIZE.getInt(); i++){
            if(calcWeight(population.geneVector.get(i))>maxWeight){
                population.geneVector.remove(i);

                ArrayList<Integer> outline = new ArrayList<Integer>();
                outline=generateOutline(targetSize);
                Collections.shuffle(outline);

                GaStruct gene = new GaStruct(outline,0);
                gene.geneFitness = cacValue(gene);
                population.geneVector.add(gene);
                continue;
            }
            fitness=cacValue(population.geneVector.get(i));
            population.geneVector.get(i).geneFitness = fitness;
        }
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

    public static void mutate(GaStruct gene) {
        int tsize = targetSize;
        int pos1 = new Random().nextInt(tsize);
        if(gene.geneNums.get(pos1)==1) gene.geneNums.set(pos1,0);
        else gene.geneNums.set(pos1,1);
    }

    public static void mate(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.get() * GA.ELITRATE.get());
        int tsize = targetSize;

        elitism(population, buffer, esize);

        // Mate the rest
        int i = esize;
        while (i<GA.POPSIZE.getInt()) {
            buffer.geneVector.get(i).geneNums = crossover(population);
            if (new Random().nextInt() < GA.MUTATION.get()) mutate(buffer.geneVector.get(i));

            if(calcWeight(buffer.geneVector.get(i))<=maxWeight){
                i++;
            }

        }

    }

    public static ArrayList<Integer> crossover(GaVector population){
        int tsize = targetSize;
        int spos = new Random().nextInt(tsize);
        int i1 = new Random().nextInt((int)(GA.POPSIZE.getInt() / 2));
        int i2 = new Random().nextInt((int)(GA.POPSIZE.getInt() / 2));

        ArrayList<Integer> output = new ArrayList<Integer>(population.geneVector.get(i1).geneNums.subList(0, spos));
                output.addAll( new ArrayList<Integer>(population.geneVector.get(i2).geneNums.subList(spos,tsize)));

        return output;
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

    public static ArrayList<Integer> readNumsFromCommandLine(int size) {

        Scanner s = new Scanner(System.in);


        ArrayList<Integer> numbers = new ArrayList<Integer>(size);
        Scanner numScanner = new Scanner(s.nextLine());
        for (int i = 0; i < size; i++) {
            if (numScanner.hasNextInt()) {
                numbers.add(numScanner.nextInt());
            } else {
                System.out.println("You didn't provide enough numbers");
                break;
            }
        }

        return numbers;
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Genetic Knapsack problem solver! ");
        System.out.println("Select your preferred mode of operation: <1,2>\n" +
                            "1.Solve a pre-set problem.\n" +
                            "2.Enter problem input.");
        int type = input.nextInt();
        if(type==1) {
            System.out.print("Select pre-set problem number to solve: [1,8]: ");
            int problemNum = input.nextInt();
            ProblemSet problem = new ProblemSet(problemNum);
            targetSize=problem.targetSize;
            maxWeight=problem.maxWeight;
            weights=problem.weights;
            values=problem.values;
        }
        else if(type==2){
            System.out.println("Enter Sack Size: ");
            targetSize = input.nextInt();
            System.out.println("Enter weights array (space seperated): ");
            weights = readNumsFromCommandLine(targetSize);
            System.out.println("Enter values array (space seperated): ");
            values = readNumsFromCommandLine(targetSize);
            System.out.println("Enter Max Weight required: ");
            maxWeight = input.nextInt();
        }
        else{
            System.out.println("Invalid Input");
            return;
        }

        System.out.print("Select number of iterations for genetic development [positive number]: ");
        maxIterations = input.nextInt();



        System.out.println("Solving for:\n"+
                "Weights: " + weights.toString() + "\n" +
                "Values: " + values.toString() + "\n" +
                "Max Weight: " + maxWeight);

        Date startTime = new Date();
        double time = 0;

        GaVector population  = new GaVector();
        GaVector buffer  = new GaVector();
        init_population(population, buffer);

        int maxValue = 0;
        int bestWeight = 0;

        ArrayList<Integer> best = new ArrayList<Integer>();
        for (int i=0; i<maxIterations; i++) {

            Date startTime2 = (i==0) ? startTime : new Date();

            calc_fitness(population);
            sort_by_fitness(population);

            if(population.geneVector.get(0).geneFitness>maxValue){
                maxValue=population.geneVector.get(0).geneFitness;
                best = population.geneVector.get(0).geneNums;
                bestWeight=calcWeight(population.geneVector.get(0));
            }

            printGenerationData(i,population, time + timePassedFrom(startTime2));


            if (population.geneVector.get(0).geneFitness == 0) {
                System.out.println("Elapsed Time: " + timePassedFrom(startTime));
                break;
            }

            Date newGenerationTime = new Date();

            mate(population, buffer);
            population = buffer;

            time = timePassedFrom(newGenerationTime);
        }

            System.out.println("Final: " + best + " value: " + maxValue + " weight: " + bestWeight);
            System.out.println("Elapsed Time: " + timePassedFrom(startTime));

    }


}
