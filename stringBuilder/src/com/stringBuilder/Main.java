package com.stringBuilder;

import java.util.*;

import static java.lang.Math.abs;

public class Main {

    public static String targetStr;

    public static char getRandomCharacter(){
        Random random = new Random();
        char c = (char)(random.nextInt(90) +32);
        return c;
    }

    public static void init_population(GaVector population, GaVector buffer ) {
        int tsize = targetStr.length();

        for (int i=0; i<GA.POPSIZE.get(); i++) {

            GaStruct citizen = new GaStruct("",0);

            for (int j=0; j<tsize; j++) {
                citizen.geneString += getRandomCharacter();
            }

            population.geneVector.add(citizen);
            buffer.geneVector.add(new GaStruct());
        }

    }

    public static void calc_fitness_LetterDistance(GaVector population) {

        String target = targetStr;
        int tsize = target.length();
        int fitness;

        for (int i=0; i<GA.POPSIZE.get(); i++) {
            fitness= letterDistanceHeuristic(population.geneVector.get(i));
            population.geneVector.get(i).geneFitness = fitness;
        }
    }

    public static void calc_fitness_BullsEye(GaVector population) {

        String target = targetStr;
        int tsize = target.length();
        int fitness;

        for (int i=0; i<GA.POPSIZE.get(); i++) {
            fitness=bullsEyeHeuristic(population.geneVector.get(i));
            population.geneVector.get(i).geneFitness = fitness;
        }
    }

    public static int letterDistanceHeuristic(GaStruct gene){
        String target = targetStr;
        int tsize = target.length();
        int fitness = 0;
        for (int j=0; j<tsize; j++) {
                fitness += abs((int)(gene.geneString.charAt(j) - target.charAt(j)));
        }
        return fitness;
    }

    public static int bullsEyeHeuristic(GaStruct gene){
        String target = targetStr;

        int lettersInPlace = 0, rightLetters = 0;

        for(int i=0; i<gene.geneString.length(); i++){
            if(isCharInString(gene.geneString.charAt(i),target)) rightLetters++;
            if(gene.geneString.charAt(i)==target.charAt(i)) lettersInPlace++;
        }

        int heuristicValue = (target.length()-lettersInPlace)*(target.length()-(rightLetters/2));

        return heuristicValue;
    }

    public static boolean isCharInString(char chr, String str){
        for(int i=0; i<str.length(); i++){
            if(str.charAt(i)==chr) return true;
        }
        return false;
    }

    public static void sort_by_fitness(GaVector population){
        Collections.sort(population.geneVector, new GaVectorComperator());

//        GaStruct [] arr = new GaStruct[population.geneVector.size()];
//        arr = population.geneVector.toArray(arr);
//        Arrays.sort(arr);
//        population.geneVector=new ArrayList<GaStruct>(Arrays.asList(arr));

    }

    public static void elitism(GaVector population, GaVector buffer, int esize ) {
        for (int i=0; i<esize; i++) {
            buffer.geneVector.get(i).geneString = population.geneVector.get(i).geneString;
            buffer.geneVector.get(i).geneFitness = population.geneVector.get(i).geneFitness;
            buffer.geneVector.get(i).age++;
        }
    }

    public static void mutate(GaStruct gene) {
        int tsize = targetStr.length();
        int ipos = new Random().nextInt(tsize);
        int delta = getRandomCharacter();
        StringBuilder sb = new StringBuilder(gene.geneString);

        char guess = (char) ((gene.geneString.charAt(ipos) + delta) % 122);


        sb.setCharAt(ipos,guess);
        gene.geneString = sb.toString();
    }

    public static void mateRandom(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.get() * GA.ELITRATE.get());
        int tsize = targetStr.length(), spos, i1, i2;

        elitism(population, buffer, esize);

        // Mate the rest
        for (int i=esize; i<GA.POPSIZE.get(); i++) {
            i1 = new Random().nextInt((int)(GA.POPSIZE.get() / 2)); // >>selection method to compute mating
            i2 = new Random().nextInt((int)(GA.POPSIZE.get() / 2)); // >>can use roulette wheel selection - prefer fitted objects
            spos = new Random().nextInt(tsize);

            buffer.geneVector.get(i).geneString = population.geneVector.get(i1).geneString.substring(0, spos) + population.geneVector.get(i2).geneString.substring(spos);

            if (new Random().nextInt() < GA.MUTATION.get()) mutate(buffer.geneVector.get(i));
        }
    }

    public static void mateUsingTournament(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.get() * GA.ELITRATE.get());
        elitism(population, buffer, esize);

        for (int i=esize; i<GA.POPSIZE.get(); i++) {

            Parents parents = new Parents();
            parents.generateUsingTournament(population);

            buffer.geneVector.get(i).geneString = crossover(population.geneVector.get(parents.indexP1),population.geneVector.get(parents.indexP2),8);

            if (new Random().nextInt() < GA.MUTATION.get()) mutate(buffer.geneVector.get(i));
        }
    }

    public static void mateUsingAge(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.get() * GA.ELITRATE.get());
        elitism(population, buffer, esize);

        for (int i=esize; i<GA.POPSIZE.get(); i++) {

            Parents parents = new Parents();
            parents.generateUsingTournamentAge(population);

            buffer.geneVector.get(i).geneString = crossover(population.geneVector.get(parents.indexP1),population.geneVector.get(parents.indexP2),6);

            if (new Random().nextInt() < GA.MUTATION.get()) mutate(buffer.geneVector.get(i));
        }
    }

    public static void mateUsingFps(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.get() * GA.ELITRATE.get());
        elitism(population, buffer, esize);

        for (int i=esize; i<GA.POPSIZE.get(); i++) {

            Parents parents = new Parents();
            parents.generateUsingFPS(population);

            buffer.geneVector.get(i).geneString = crossover(population.geneVector.get(parents.indexP1),population.geneVector.get(parents.indexP2),6);

            if (new Random().nextInt() < GA.MUTATION.get()) mutate(buffer.geneVector.get(i));
        }
    }

    public static String crossover(GaStruct parent1, GaStruct parent2, int crossDegree){
        ArrayList<Integer> positions = new ArrayList<Integer>();
        for (int i=0; i<crossDegree;i++){
            positions.add(new Random().nextInt(targetStr.length()-1));
        }
        positions.sort(null);
        String decended = "";
        int pos=0;
        for (int i=0; i<crossDegree;i++){
            if(i%2==0){
                decended+=parent1.geneString.substring(pos, positions.get(i));
                pos=positions.get(i);
            }
            else{
                decended+=parent2.geneString.substring(pos, positions.get(i));
                pos=positions.get(i);
            }
        }
        decended+=parent2.geneString.substring(pos);
        return decended;
    }

    public static void print_best(GaVector gav) {
        System.out.print("Best: " + gav.geneVector.get(0).geneString + " (" + gav.geneVector.get(0).geneFitness + ")" );
    }

    public static void printGenerationData(int generation, GaVector population, double generationTime){
        System.out.print("Generation " + (generation+1) + ": ");;
        print_best(population);		// print the best one
        System.out.print(" Population Fitness Avarege: " + population.calcFitnessAvarege());
        System.out.print(" Population Fitness STD: " + population.calcStandardDeviation());
        System.out.println(" Generation development time: " + generationTime);
    }

    public static double timePassedFrom(Date from){
        Date currentTime = new Date();
        return ((float)(currentTime.getTime()-from.getTime()))/1000;
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        System.out.println("Welcome to Genetic String Builder! ");
        System.out.println("This program will demonstrate the way a genetic algorithm (GA) builds a target string.\n" +
                            "First lets define the calculation methods for the GA.");
        System.out.println("Select Fitness calculation heuristic: <1,2> " +
                "\n1.Letter Distance." +
                "\n2.Bulls Eye.");
        int fitnessCalcMethod = input.nextInt();
        System.out.println("Select Mating method: <1,2,3,4> " +
                "\n1.Random." +
                "\n2.FPS." +
                "\n3.Tournament." +
                "\n4.Age.");
        int matingMethod = input.nextInt();

        System.out.print("We are now ready - Enter String to develop: ");
        input.nextLine();
        targetStr=input.nextLine();


        Date startTime = new Date();
        double time = 0;

        GaVector population  = new GaVector();
        GaVector buffer  = new GaVector();

        init_population(population, buffer);

        for (int i=0; i<GA.MAXITER.get(); i++) {

            Date startTime2 = (i==0) ? startTime : new Date();

            switch (fitnessCalcMethod){
                case 1:
                    calc_fitness_LetterDistance(population);
                    break;
                case 2:
                    calc_fitness_BullsEye(population);
                    break;
            }
            sort_by_fitness(population);

            printGenerationData(i,population, time + timePassedFrom(startTime2));

            if (population.geneVector.get(0).geneFitness == 0) {
                System.out.println("Elapsed Time: " + timePassedFrom(startTime));
                break;
            }

            Date newGenerationTime = new Date();

            switch (matingMethod){
                case 1:
                    mateRandom(population,buffer);
                    break;
                case 2:
                    mateUsingFps(population,buffer);
                    break;
                case 3:
                    mateUsingTournament(population,buffer);
                    break;
                case 4:
                    mateUsingAge(population,buffer);
                    break;
            }
            population = buffer;

            time = timePassedFrom(newGenerationTime);
        }

    }

}
