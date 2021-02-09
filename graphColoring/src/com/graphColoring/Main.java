package com.graphColoring;

import java.io.*;
import java.util.*;

public class Main {

    public static Graph graph;

    /* Global Search - DFS heuristics (backjumping, forward checking, arc consistency, MRV/LCV)  */
    /* ========================================================================================= */

    //converts coloring table -> coloring vector
    public static ArrayList<Integer> tableToVector(ColoringTable coloringTable1){

        ArrayList<Integer> coloringVector = new ArrayList<Integer>();
        for(int i = 0; i< coloringTable1.vertices; i++) {
            coloringVector.add(-1);
        }

//        ColoringTable coloringTable1 = new ColoringTable(coloringTable);

        for(int i = 0; i< coloringTable1.vertices; i++){

            Iterator it = coloringTable1.colorTable.get(i).entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                if(pair.getValue()==TYPE.COLORED) {
                    coloringVector.set(i,(int)pair.getKey());
                    continue;
                }

                it.remove();
            }

        }



        return coloringVector;
    }

    //converts coloring table <- coloring vector
    public static ColoringTable vectorToTable(ArrayList<Integer> coloringVector, int colors, Graph graph, ColoringTable coloringTable){
        coloringTable.clearTable();

        for(int i = 0; i<coloringVector.size(); i++){
            if(coloringVector.get(i)!=-1) {
                coloringTable.assignColor(i, coloringVector.get(i));
                coloringTable.banColor(i, coloringVector.get(i), graph);
                coloringTable.remainingValues.set(i, Integer.MAX_VALUE);
            }
        }

        return coloringTable;
    }

    //clears the stack up to a given amount
    public static void clearFirst(Stack<State> stack, int toClear){
        Stack<State>  tmpStack = new Stack<State>();

        while (stack.size()>toClear){
            State tmp = stack.pop();
            tmpStack.push(tmp);
        }

        stack.clear();

        while (!tmpStack.empty()){
            State tmp = tmpStack.pop();
            stack.push(tmp);
        }

        tmpStack.clear();

    }

    //finds a valid coloring using DFS global search, returns null if fails
    public static State findColoringUsingDFS(Graph graph, int colors, TYPE filteringMethod, TYPE orderingMethod, TYPE trackingMethod, Stats stats){

        Timer timer = new Timer();
        timer.schedule(new ProgressLine(), 0, 2000);

        int stateDeveloped = 0;
        ArrayList<State> stateExpansion = new ArrayList<State>();
        Stack<State> stack = new Stack<State>();
        State initialState = new State(graph.numVertices, colors);
        stack.push(initialState);
        State currentState;

        ColoringTable coloringTable = new ColoringTable(graph.numVertices, colors);

        int verticesAssigningParameter = 0;

        while (!stack.isEmpty()){


            if((trackingMethod==TYPE.BACKJUMPING)&&(stack.size()>=CONSTANTS.STACK_MAX)) {
                clearFirst(stack,CONSTANTS.STACK_CLEAR);
            }

            stateDeveloped++;
            currentState = stack.pop();

            coloringTable = vectorToTable(currentState.coloringVector,colors,graph,coloringTable);

            if((trackingMethod==TYPE.BACKJUMPING)&&(stateDeveloped>graph.numVertices+CONSTANTS.BJ_FACTOR)) {
                stats.statesCounter=stateDeveloped;
                timerKill(timer);
                return null;
            }
            if((trackingMethod==TYPE.BACKTRACKING)&&(filteringMethod==TYPE.FC_AC)&& stateDeveloped>CONSTANTS.MIN_STATES){
                if(Math.abs(currentState.verticesAssigned-verticesAssigningParameter)==0) {
                    stats.statesCounter=stateDeveloped;
                    timerKill(timer);
                    return null;
                }
            }

            if(stateDeveloped%10==0) verticesAssigningParameter = currentState.verticesAssigned;

            if((trackingMethod==TYPE.BACKJUMPING)&&!currentState.isValidState(graph)){
                int assignmentTarget = currentState.getConflictSetRoot(graph);
                for(int i=0; i<colors; i++){
                    currentState.coloringVector.set(assignmentTarget, i);
                    if(currentState.isValidState(graph)) {
                        coloringTable.assignColor(assignmentTarget,i);
                        continue;
                    }
                }
                if(!currentState.isValidState(graph)){
                    continue;
                }
            }

            if(currentState.isGoalState(graph)) {
                stats.statesCounter=stateDeveloped;
                timerKill(timer);
                System.out.println("Global Search Succeeded for " + colors + " colors: " + currentState.coloringVector.toString());
                return currentState;
            }

            if(filteringMethod==TYPE.FC_AC ||filteringMethod==TYPE.AC) {
                if (!coloringTable.imposeArcConsistency(graph))
                    continue;
            }
            currentState.coloringVector=tableToVector(coloringTable);

            stateExpansion.clear();
            stateExpansion.addAll(currentState.expand(graph, filteringMethod,orderingMethod,coloringTable,stateExpansion));

            for (int i = 0; i < stateExpansion.size(); i++){
                if(!stateExpansion.get(i).fullyExpanded)
                    stack.push(stateExpansion.get(i));
            }
        }
        stats.statesCounter=stateDeveloped;
        timerKill(timer);
        return null;
    }

    /* Local Search - Genetic Algorithm Framework */
    /* ========================================================================================= */

    //collects all the "bad vertices" (=are oart of a bad edge)
    public static ArrayList<Integer> getBadVertices (ArrayList<Integer> gene, Graph graph){
        ArrayList<Integer> badVertices = new ArrayList<Integer>();

        for (int i=0; i<gene.size(); i++){
            if(!graph.checkNeighborhood(gene,i)) badVertices.add(i);
        }

        return badVertices;
    }

    //determines a random assignment for bad vertices (for init population)
    public static ArrayList<Integer> assignRandom (ArrayList<Integer> gene,ArrayList<Integer> badVertices, int colors){
        ArrayList<Integer> assign = new ArrayList<Integer>(gene);

        for (int i=0; i<badVertices.size(); i++){
            assign.set(badVertices.get(i), new Random().nextInt(colors));
        }
        return assign;
    }

    //inits the population for the Genetic Algorithm
    public static void init_population(GaVector population, GaVector buffer, Graph graph, ArrayList<Integer> initialGene, int colors) {


        ArrayList<Integer> badVertices = new ArrayList<Integer>();
        badVertices = getBadVertices(initialGene,graph);

        for (int i=0; i<GA.POPSIZE.getInt(); i++) {

            ArrayList<Integer> assignment = new ArrayList<Integer>();
            assignment = assignRandom(initialGene,badVertices,colors);
            GaStruct citizen = new GaStruct(assignment,0,colors);

            population.geneVector.add(citizen);
            buffer.geneVector.add(new GaStruct());

        }

    }

    //calculate the fitness for the population
    public static void calc_fitness(GaVector population, Graph graph) {


        int fitness;

        for (int i=0; i<GA.POPSIZE.getInt(); i++) {
            fitness= calcGeneFitness(population.geneVector.get(i), graph);
//            System.out.println("fitness calculated: " + fitness + " for gene: " + population.geneVector.get(i).geneNums.toString());;
            population.geneVector.get(i).geneFitness = fitness;
        }
    }

    //calculate the fitness for a single gene
    public static int calcGeneFitness(GaStruct gene, Graph graph){
        int fitness = 0;
        for (int i=0; i<gene.geneNums.size(); i++){
            if(!graph.checkNeighborhood(gene.geneNums,i)) fitness++;
        }
        return fitness;
    }

    //sorts the population according to fitness
    public static void sort_by_fitness(GaVector population){
        Collections.sort(population.geneVector, new GaVectorComperator());
    }

    //seperates elite population to move to next generation
    public static void elitism(GaVector population, GaVector buffer, int esize) {
        for (int i=0; i<esize; i++) {
            buffer.geneVector.get(i).geneNums = population.geneVector.get(i).geneNums;
            buffer.geneVector.get(i).geneFitness = population.geneVector.get(i).geneFitness;
            buffer.geneVector.get(i).colors = population.geneVector.get(i).colors;

        }
    }

    //mutate a gene via random heuristic
    public static void mutateRandom(GaStruct gene) {
        int tsize = graph.numVertices;
        int pos1 = new Random().nextInt(tsize);
        int numAtPos1 = gene.geneNums.get(pos1);
        int pos2 = new Random().nextInt(tsize);
        int numAtPos2 = gene.geneNums.get(pos2);
        gene.geneNums.set(pos1,numAtPos2);
        gene.geneNums.set(pos2,numAtPos1);
    }

    //mmutate a gene via scramble heuristic
    public static void scrambleMutate(GaStruct gene) {
        int tsize = graph.numVertices;
        Random r = new Random();
        int pos1 = r.nextInt(tsize);
        int pos2 = r.nextInt(tsize);

        int minPos = Integer.min(pos1, pos2);
        int maxPos = Integer.max(pos1, pos2);

        for (int i = minPos; i < maxPos; i++) {
            int newPos = r.nextInt(maxPos - minPos) + minPos;
            Collections.swap(gene.geneNums, newPos, i);
        }
    }

    //a crossover heuristic
    public static ArrayList partiallyMatchedCrossover(GaVector population){
        // Choose a random index. The values of father and mother at this index will be swapped.
        // Repeat half of the problem size times

        Random rand = new Random();
        int tsize = graph.numVertices;
        int pos;

        Parents parents = new Parents();
        parents.generateUsingTournament(population);
        ArrayList<Integer> father = new ArrayList<>(population.geneVector.get(parents.indexP1).geneNums);
        ArrayList<Integer> mother = new ArrayList<>(population.geneVector.get(parents.indexP2).geneNums);

        ArrayList output = father;

        // Swap between half of the nodes
        for (int i = 0; i < tsize / 2; i++) {
            // generate a position for mother to compare with father
            pos = rand.nextInt(tsize);
            int motherVal = mother.get(i);

            // look for mother's value in father
            for (int j = 0; j < tsize; j++) {
                int fatherVal = father.get(j);
                if (fatherVal == motherVal) {
                    // Swap the two positions
                    Collections.swap(output, pos, j);
                }
            }
        }
        return output;
    }

    //mates genes in the population
    public static void mate(GaVector population, GaVector buffer) {

        int esize = (int)(GA.POPSIZE.getInt() * GA.ELITRATE.get());
        int tsize = graph.numVertices;

        elitism(population, buffer, esize);

        int colors = population.geneVector.get(0).colors;
        // Mate the rest
        for (int i=esize; i<GA.POPSIZE.getInt(); i++) {
            buffer.geneVector.get(i).geneNums = crossoverRandom(population);
//            buffer.geneVector.get(i).geneNums = partiallyMatchedCrossover(population);

            buffer.geneVector.get(i).colors=colors;
            if (new Random().nextInt() < GA.MUTATION.get()){
                mutateRandom(buffer.geneVector.get(i));
//                scrambleMutate(buffer.geneVector.get(i));

            }
        }
    }

    //a random crossover heuristic
    public static ArrayList<Integer> crossoverRandom(GaVector population){
        int tsize = graph.numVertices;
        int spos = new Random().nextInt(tsize);

        Parents parents = new Parents();
        parents.generateUsingTournament(population);
        ArrayList<Integer> parent1 = new ArrayList<Integer>(population.geneVector.get(parents.indexP1).geneNums);
        ArrayList<Integer> parent2 = new ArrayList<Integer>(population.geneVector.get(parents.indexP2).geneNums);

        ArrayList<Integer> output = new ArrayList<Integer>();

        for(int i=0; i<tsize; i++){
            if(i<spos) output.add(parent1.get(i));
            else output.add(parent2.get(i));
        }
        return output;
    }

    //prints information of the best gene in the generation
    public static void print_best(GaVector gav) {
        System.out.print("Best: " + gav.geneVector.get(0).geneNums + " (" + gav.geneVector.get(0).geneFitness + ")" );
    }

    //prints information about the entire generation
    public static void printGenerationData(int generation, GaVector population, double generationTime){
        System.out.print("Generation " + (generation+1) + ": ");;
        print_best(population);		// print the best one
        System.out.print(" Population Fitness Avarege: " + population.calcFitnessAvarege());
        System.out.print(" Population Fitness STD: " + population.calcStandardDeviation());
        System.out.println(" Generation development time: " + generationTime);
//        System.out.println("============================");
    }

    //calculate the time passed
    public static double timePassedFrom(Date from){
        Date currentTime = new Date();
        return ((float)(currentTime.getTime()-from.getTime()))/1000;
    }

    //find coloring using Genetic Algorithm
    public static ArrayList<Integer> findColoringUsingGA(ArrayList<Integer> initialGene, Graph graph, int colors, Stats stats){
        Timer timer = new Timer();
        timer.schedule(new ProgressLine(), 0, 2000);

        Date startTime = new Date();
        double time = 0;

        GaVector population  = new GaVector();
        GaVector buffer  = new GaVector();

        init_population(population, buffer, graph, initialGene ,colors);

        for (int i=0; i<GA.MAXITER.getInt(); i++) {

            Date startTime2 = (i==0) ? startTime : new Date();

            calc_fitness(population,graph);
            sort_by_fitness(population);

//            printGenerationData(i,population, time + timePassedFrom(startTime2));

            if (population.geneVector.get(0).geneFitness == 0) {
                timerKill(timer);
                System.out.println("Success for k = " + colors + " with: " + population.geneVector.get(0).geneNums.toString());
                stats.statesCounter=i;
                return population.geneVector.get(0).geneNums;
            }

            Date newGenerationTime = new Date();

            mate(population, buffer);
            population = buffer;

            time = timePassedFrom(newGenerationTime);
        }

        timerKill(timer);
        System.out.println("Failure for k = " + colors);
        stats.statesCounter=GA.MAXITER.getInt();
        return null;

    }

    /* Local Search - Kemp Chains Algorithm Framework */
    /* ========================================================================================= */

    //converts color classes to coloring vectors
    public static ArrayList<Integer> colorClassesToColoring(ArrayList<HashSet<Integer>> colorClasses, int vertices){
        ArrayList<Integer> coloring = new ArrayList<Integer>();

        for(int i=0; i<vertices; i++){
            coloring.add(-1);
        }

        for(int i=0; i<colorClasses.size(); i++){
            ArrayList<Integer> hs = new ArrayList<Integer>();
            hs.addAll(colorClasses.get(i));
            for(int j=0; j<hs.size(); j++){
                coloring.set(hs.get(j),i);
            }
        }

        return coloring;
    }

    //calculates SA value
    public static boolean simulatedAnnealing(double delta, Date time){
        float random = new Random().nextFloat();
        return (Math.exp(delta/(double) (new Date().getTime()-time.getTime())))<random;
    }

    //copies color classes
    public static ArrayList<HashSet<Integer>> copyColorClasses (ArrayList<HashSet<Integer>> initialColorClasses){
        ArrayList<HashSet<Integer>> colorClasses = new ArrayList<HashSet<Integer>>();

        for(int i = 0; i<initialColorClasses.size(); i++){
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.addAll(initialColorClasses.get(i));
            colorClasses.add(hs);
        }
        return colorClasses;
    }

    //reduces color classes using kemp chains
    private static ArrayList<HashSet<Integer>> reduceColorClassesKC(ArrayList<HashSet<Integer>> initialColorClasses, Graph graph, Date time){

        ArrayList<HashSet<Integer>> colorClasses = copyColorClasses (initialColorClasses);
        int initValue = calculateColorClassesValue(initialColorClasses);

        int iClass = new Random().nextInt(initialColorClasses.size());
        int jClass = new Random().nextInt(initialColorClasses.size());
        int sourceVertice = getRanVerticeFromSet(initialColorClasses.get(iClass));

        ArrayList<HashSet<Integer>> newColorClasses = performKempChainsExchange(graph, initialColorClasses, iClass, sourceVertice,jClass);
        int newValue = calculateColorClassesValue(newColorClasses);
        if(newValue>=initValue)
            return newColorClasses;
        else {
            if(simulatedAnnealing(initValue-newValue, time))
                return newColorClasses;
            else return colorClasses;

        }
    }

    //check if 2 classes are independent one from the other
    public static boolean ClassesAreIndependent(Graph graph, ArrayList<HashSet<Integer>> initialColorClasses, int iClass,int jClass){

        HashSet<Integer> ic = initialColorClasses.get(iClass);
        HashSet<Integer> jc = initialColorClasses.get(jClass);

        Iterator<Integer> iterator = ic.iterator();
        while (iterator.hasNext()) {
            if(getRelevantNeighborhood(graph,iterator.next(),jc).size()>0) return false;
        }

        return true;
    }

    //preforms the kemp transition between 2 sets
    public static ArrayList<HashSet<Integer>> performKempChainsExchange(Graph graph, ArrayList<HashSet<Integer>> initialColorClasses, int iClass, int sourceVertice, int jClass){

        if(ClassesAreIndependent(graph, initialColorClasses,iClass,jClass)){
            ArrayList<HashSet<Integer>> calculatedColorClasses = copyColorClasses(initialColorClasses);
            calculatedColorClasses.get(iClass).addAll(calculatedColorClasses.get(jClass));
            calculatedColorClasses.get(jClass).clear();
            return calculatedColorClasses;
        }

        ArrayList<Integer> sourceNeighborhood = getRelevantNeighborhood(graph,sourceVertice,initialColorClasses.get(jClass));
        HashSet<Integer> iClassGroup = new HashSet<Integer>();
        HashSet<Integer> jClassGroup = new HashSet<Integer>();
        jClassGroup.addAll(sourceNeighborhood);

        Queue<Integer> queue = new LinkedList<Integer>();
        queue.addAll(sourceNeighborhood);

        HashMap<Integer,Boolean> queued = new HashMap<Integer, Boolean>();
        queued.putAll(ALtoHM(sourceNeighborhood));
        queued.put(sourceVertice,true);

        while (!queue.isEmpty()){
            int vertice = queue.poll();
            char verticeRelevance;
            if(initialColorClasses.get(iClass).contains(vertice)) verticeRelevance = 'i';
            else verticeRelevance = 'j';
            if (verticeRelevance == 'i'){
                ArrayList<Integer> verticeNeighborhood = getRelevantNeighborhood(graph,vertice,initialColorClasses.get(jClass));
                jClassGroup.addAll(verticeNeighborhood);
                for(int i=0; i<verticeNeighborhood.size(); i++){
                    if(!queued.containsKey(verticeNeighborhood.get(i)))
                        queue.add(verticeNeighborhood.get(i));
                }
                queued.putAll(ALtoHM(verticeNeighborhood));
            }
            if (verticeRelevance == 'j'){
                ArrayList<Integer> verticeNeighborhood = getRelevantNeighborhood(graph,vertice,initialColorClasses.get(iClass));
                iClassGroup.addAll(verticeNeighborhood);
                for(int i=0; i<verticeNeighborhood.size(); i++){
                    if(!queued.containsKey(verticeNeighborhood.get(i)))
                        queue.add(verticeNeighborhood.get(i));
                }
                queued.putAll(ALtoHM(verticeNeighborhood));
            }
        }

        ArrayList<HashSet<Integer>> calculatedColorClasses = copyColorClasses(initialColorClasses);
        calculatedColorClasses.get(iClass).removeAll(iClassGroup);
        calculatedColorClasses.get(iClass).addAll(jClassGroup);
        calculatedColorClasses.get(jClass).removeAll(jClassGroup);
        calculatedColorClasses.get(jClass).addAll(iClassGroup);

        return calculatedColorClasses;
    }

    //eliminates empty color sets
    public static int eliminateEmptyClasses(ArrayList<HashSet<Integer>> colorClasses){
        int eliminated = -1;

        for(int i=0; i<colorClasses.size(); i++){
            if(colorClasses.get(i).isEmpty()) {
                eliminated = i;
                colorClasses.remove(colorClasses.get(i));
            }
        }

        return eliminated;
    }

    //utility - ArrayList to HashMap
    public static HashMap<Integer, Boolean> ALtoHM(ArrayList<Integer> input){
        HashMap<Integer,Boolean> map = new HashMap<Integer, Boolean>();

        for(int i=0; i<input.size(); i++){

            map.put(input.get(i),true);

        }

        return map;
    }

    //takes a vertice and a set - returns all vertices from the set who are in the same neighborhood
    public static ArrayList<Integer> getRelevantNeighborhood (Graph graph, Integer source, HashSet<Integer> destSet){
        ArrayList<Integer> relevantNeighborhood = new ArrayList<Integer>();
        ArrayList<Integer> sourceNeighborhood = graph.getNeighborhood(source);
        for(int i=0; i<sourceNeighborhood.size(); i++){
            if(destSet.contains(sourceNeighborhood.get(i)))
                relevantNeighborhood.add(sourceNeighborhood.get(i));
        }
        return relevantNeighborhood;
    }

    //selects a random number out ofa set
    public static int getRanVerticeFromSet(HashSet<Integer> set){

        int size = set.size();
        int rand = new Random().nextInt(size);
        int i = 0;
        for(Integer vertice : set)
        {
            if (i == rand)
                return vertice;
            i++;
        }

        return -1;
    }

    //builds color classes out of a coloring
    public static ArrayList<HashSet<Integer>> buildColorClasses (GreedyColoringType greedyColoring){
        ArrayList<HashSet<Integer>> colorClasses = new ArrayList<HashSet<Integer>>();

        for(int i=0; i<greedyColoring.colors; i++){
            colorClasses.add(new HashSet<Integer>());
        }

        for(int i=0; i<greedyColoring.greedyColoring.size(); i++){

            colorClasses.get(greedyColoring.greedyColoring.get(i)).add(i);

        }
        return colorClasses;
    }

    //calculates color classes value = sigma((c_i)^2)
    public static int calculateColorClassesValue(ArrayList<HashSet<Integer>> colorClasses){
        int sum =0;
        for(int i=0; i<colorClasses.size(); i++){
            sum+=colorClasses.get(i).size()*colorClasses.get(i).size();
        }
        return sum;

    }

    /* Local Search - Combined Algorithm Framework */
    /* ========================================================================================= */

    //reduces color classes using bad Edges method
    private static ArrayList<HashSet<Integer>> reduceColorClassesBE(ArrayList<HashSet<Integer>> initialColorClasses, Graph graph, Date time){

        ArrayList<HashSet<Integer>> colorClasses = copyColorClasses (initialColorClasses);

        int initValue = calculateColorClassesValueBE(initialColorClasses, graph);

        int iClass = new Random().nextInt(initialColorClasses.size());
        int jClass = new Random().nextInt(initialColorClasses.size());


        int sourceVertice = getRanVerticeFromSet(initialColorClasses.get(jClass));

        ArrayList<HashSet<Integer>> newColorClasses = performExchangeBE(graph, initialColorClasses, iClass, sourceVertice,jClass);

        int newValue = calculateColorClassesValueBE(newColorClasses, graph);

        if(newValue<initValue)
            return newColorClasses;
        else  return colorClasses;

    }

    //moving a random vertice from one set to another
    public static ArrayList<HashSet<Integer>> performExchangeBE(Graph graph, ArrayList<HashSet<Integer>> initialColorClasses, int iClass, int sourceVertice, int jClass){

        ArrayList<HashSet<Integer>> calculatedColorClasses = copyColorClasses(initialColorClasses);
        calculatedColorClasses.get(jClass).remove(sourceVertice);
        calculatedColorClasses.get(iClass).add(sourceVertice);

        return calculatedColorClasses;
    }

    //calculates color classes value = sigma(2*(b_i)*(c_i))-sigma((c_i)^2)
    public static int calculateColorClassesValueBE(ArrayList<HashSet<Integer>> colorClasses, Graph graph){

        int lhs =0;
        int rhs =0;

        for(int i=0; i<colorClasses.size(); i++){
            rhs+=colorClasses.get(i).size()*colorClasses.get(i).size();
        }

        for(int i=0; i<colorClasses.size(); i++){
            lhs+=2*colorClasses.get(i).size()*calcBadEdges(colorClasses.get(i),graph);
        }

        return lhs-rhs;

    }

    //returns the amount of bad edges in a set
    public static int calcBadEdges (HashSet<Integer> colorClass, Graph graph){
        int badEdges = 0;

        ArrayList<Integer> colorClassArray = new ArrayList<Integer>();
        colorClassArray.addAll(colorClass);

        for(int i=0; i<colorClassArray.size(); i++){
            for (int j=i+1; j<colorClassArray.size(); j++){
                if(graph.isEdge(colorClassArray.get(i),colorClassArray.get(j))) badEdges++;
            }
        }


        return badEdges;

    }

    /* Auxiliary Functions */
    /* ========================================================================================= */

    //kils the timer that sets dots to show progress
    public static void timerKill(Timer timer){
        timer.cancel();
        timer.purge();
        System.out.println();
    }

    //removes a color from a coloring vector
    public static ArrayList<Integer> removeColorFrom(ArrayList<Integer> input, int colors) {

        ArrayList<Integer> lessColors = new ArrayList<Integer>(input);

        int colorToRemove = colors;
        int colorToAssign = colors - 1;

        for (int i = 0; i < input.size(); i++) {
            if (input.get(i) == colorToRemove) lessColors.set(i, colorToAssign);
        }
        return lessColors;
    }

    //builds a greedy coloring - main
    public static GreedyColoringType getGreedyColoring(Graph graph, TYPE greedyType){
        System.out.println("Finding Coloring Using Greedy Algorithm ("+greedyType+")");
        Timer timer = new Timer();
        timer.schedule(new ProgressLine(), 0, 2000);
        GreedyColoringType greedyColoring = null;
        if (greedyType==TYPE.ADVANCED) greedyColoring = getGreedyColoringAdvanced(graph);
        if (greedyType==TYPE.BASIC) greedyColoring = getGreedyColoringBasic(graph);
        timerKill(timer);
        System.out.println("Greedy Coloring: " + greedyColoring.greedyColoring.toString());
        System.out.println("Using Greedy Coloring - Chromatic Number found: " + greedyColoring.colors);
        System.out.println("======================================================");
        return greedyColoring;
    }

    //builds a greedy coloring - efficient greedy
    public static GreedyColoringType getGreedyColoringAdvanced(Graph graph){

        GreedyColoringType greedy = new GreedyColoringType(graph.numVertices);


        for(int i=0; i<graph.numVertices; i++){

            boolean isValid = false;

            int color = 0;
            while (!isValid){
                greedy.greedyColoring.set(i,color);
                isValid = isValidState(graph, greedy.greedyColoring,i);
                if(!isValid) color++;
                greedy.colors = (greedy.colors > color) ? greedy.colors : color;
            }

        }

        greedy.colors++;
        return greedy;
    }

    //builds a greedy coloring - partly efficient greedy
    public static GreedyColoringType getGreedyColoringBasic(Graph graph){

        GreedyColoringType greedy = new GreedyColoringType(graph.numVertices);


        for(int i=0; i<graph.numVertices; i++){


            int rand = new Random().nextInt(100);
            if(rand<CONSTANTS.GREED_FACT){
                greedy.colors++;
                greedy.greedyColoring.set(i, greedy.colors);
            }
            else {
                int color = 0;
                boolean isValid = false;
                while (!isValid) {
                    greedy.greedyColoring.set(i, color);
                    isValid = isValidState(graph, greedy.greedyColoring, i);
                    if (!isValid) color++;
                    greedy.colors = (greedy.colors > color) ? greedy.colors : color;
                }
            }
        }

        greedy.colors++;
        return greedy;
    }

    //checks if a given, coloring vector is valid partial coloring
    public static boolean isValidState(Graph graph, ArrayList<Integer> coloringVector,int verticesAssigned){


        for(int i=0; i<verticesAssigned; i++){
            if(!graph.checkNeighborhood(coloringVector,i)) return false;
        }

        return true;
    }

    //aux function for reading input files from folder
    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> paths = new ArrayList<String>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                paths.add(folder.toPath()+"\\"+fileEntry.getName());
            }
        }

        return paths;
    }

    //Test run function
    public static void runAllTests(ArrayList<String> tests, boolean writeToFile, HashSet<Integer> exceptions, int startFrom){
        if(writeToFile) {
            try {
                PrintStream out = new PrintStream(new FileOutputStream("test_results.txt"));
                System.setOut(out);
            } catch (Exception ex) {
            }
        }

        for(int i = startFrom; i<tests.size(); i++) {
            if(exceptions.contains(i)) continue;

            System.out.println("==================== Test number " + i + " =====================\n");
            System.out.println("Graph: " + tests.get(i));

            graph = new Graph(tests.get(i));

            GreedyColoringType greedyColoringGlobal = getGreedyColoring(graph, TYPE.ADVANCED);

        /* Solve using Global Search */
            findChromaticNumberDFS(graph, TYPE.NONE, TYPE.MRV, TYPE.BACKJUMPING,greedyColoringGlobal);
            findChromaticNumberDFS(graph, TYPE.FC_AC, TYPE.MRV_LCV, TYPE.BACKTRACKING,greedyColoringGlobal);

            GreedyColoringType greedyColoringLocal = getGreedyColoring(graph, TYPE.BASIC);

        /* Solve using Local Search - Genetic Algorithm */
            findChromaticNumberGA(graph,greedyColoringLocal);

        /* Solve using Local Search - Kemp Chains */
            findChromaticNumberKC(graph,greedyColoringLocal);

        /* Solve using Local Search - Kemp Chains */
            findChromaticNumberBE(graph, greedyColoringLocal);

        }
    }

    /* Main Execution functions */
    /* ========================================================================================= */

    //initiate with DFS
    public static int findChromaticNumberDFS(Graph graph, TYPE filteringMethod, TYPE orderingMethod, TYPE trackingMethod, GreedyColoringType greedyColoring){
        Date startTime = new Date();
        Stats stats = new Stats();

        System.out.println("Finding Coloring Using DFS (" + filteringMethod + " , " + orderingMethod + " , " + trackingMethod+")");

        boolean foundColoringForK = true;
        int k = greedyColoring.colors;
        State solution;
        double totalStates = 0;
        ArrayList<Integer> coloring = greedyColoring.greedyColoring;
        double runs = 0;
        while (foundColoringForK){
            runs++;
            solution = findColoringUsingDFS(graph, k, filteringMethod, orderingMethod, trackingMethod, stats);
            totalStates+=stats.statesCounter;
            if(solution!=null) coloring = solution.coloringVector;
            if(solution==null){
                foundColoringForK = false;
                k++;
                k = (k < greedyColoring.colors) ? k : greedyColoring.colors;
                System.out.println("Best coloring from global search: " + coloring.toString());
                continue;
            }
            k--;
        }


        System.out.println("Using DFS - Chromatic Number found: " + k);
        System.out.println("Time taken: " + timePassedFrom(startTime));
        System.out.println("Total States developed: " + stats.statesCounter);
        System.out.println("Average States per run: " + (totalStates/runs));
        System.out.println("======================================================");
        return k;
    }

    //initiate with Genetic Algorithm
    public static int findChromaticNumberGA(Graph graph, GreedyColoringType greedyColoring){
        Date startTime = new Date();
        System.out.println("Finding Coloring Using GA: ");
        Stats stats = new Stats();
        boolean foundColoringForK = true;
        int k = greedyColoring.colors-1;
        ArrayList<Integer> coloring = greedyColoring.greedyColoring;
        double totalStates = 0;
        double runs = 0;
        while (foundColoringForK){
            runs++;
            ArrayList<Integer> lessColors = removeColorFrom(coloring, k);
            coloring = findColoringUsingGA(lessColors, graph, k,stats);
            if(coloring==null){
                foundColoringForK = false;
                k++;
                continue;
            }
            totalStates+=stats.statesCounter;
            k--;
        }

        System.out.println("Using GA - Chromatic Number found: " + k);
        System.out.println("Time taken: " + timePassedFrom(startTime));
        System.out.println("Total States developed: " + totalStates);
        System.out.println("Average States per run: " + (totalStates/runs));
        System.out.println("======================================================");

        return k;
    }

    //initiate with kemp Chains
    public static int findChromaticNumberKC(Graph graph, GreedyColoringType greedyColoring){
        Date startTime = new Date();
        System.out.println("Finding Coloring Using Color Classes KC: ");
        Timer timer = new Timer();
        timer.schedule(new ProgressLine(), 0, 2000);
        ArrayList<HashSet<Integer>> colorClasses = buildColorClasses(greedyColoring);

        long i=0;
        long max = CONSTANTS.MAX_CC;
        while (max>i){
            colorClasses = reduceColorClassesKC(colorClasses, graph,startTime);
            int elimination = eliminateEmptyClasses(colorClasses);
            if(elimination!=-1){
                System.out.println("Success for: " + colorClasses.size() + " color classes");
                i=0;
                max+=CONSTANTS.ADV_CC;
            }
            i++;
        }
        timerKill(timer);
        System.out.println("coloring: " + colorClassesToColoring(colorClasses,graph.numVertices));
        System.out.println("Using KC - Chromatic Number found: " + colorClasses.size());
        System.out.println("Time taken: " + timePassedFrom(startTime));
        System.out.println("Iterations performed: " + i);
        System.out.println("======================================================");
        return colorClasses.size();
    }

    //initiate with Bad Edges
    public static int findChromaticNumberBE(Graph graph, GreedyColoringType greedyColoring){
        Date startTime = new Date();
        System.out.println("Finding Coloring Using Color Classes Combined: ");
        Timer timer = new Timer();
        timer.schedule(new ProgressLine(), 0, 2000);
        ArrayList<HashSet<Integer>> colorClasses = buildColorClasses(greedyColoring);

        long i=0;
        long max = CONSTANTS.MAX_CC;;
        while (max>i){
            colorClasses = reduceColorClassesBE(colorClasses, graph,startTime);
            int elimination = eliminateEmptyClasses(colorClasses);
            if(elimination!=-1){
                System.out.println("Success for: " + colorClasses.size() + " color classes");
                i=0;
                max+=CONSTANTS.ADV_CC;;
            }
            i++;
        }

        ArrayList<Integer> coloring = colorClassesToColoring(colorClasses,graph.numVertices);
        timerKill(timer);
        System.out.println("coloring: " + coloring);
        System.out.println("Using Combined Method - Chromatic Number found: " + colorClasses.size());
        System.out.println("Time taken: " + timePassedFrom(startTime));
        System.out.println("Iterations performed: " + i);
        System.out.println("======================================================");
        return colorClasses.size();
    }

    public static void main(String[] args) {

        graph = new Graph(args[0]);

        int algorithm = Integer.parseInt(args[1]);

        GreedyColoringType greedyColoringGlobal, greedyColoringLocal;

        switch (algorithm){
            case 1:
                greedyColoringGlobal = getGreedyColoring(graph, TYPE.ADVANCED);
                findChromaticNumberDFS(graph, TYPE.NONE, TYPE.MRV, TYPE.BACKJUMPING,greedyColoringGlobal);
                break;
            case 2:
                greedyColoringGlobal = getGreedyColoring(graph, TYPE.ADVANCED);
                findChromaticNumberDFS(graph, TYPE.FC_AC, TYPE.MRV_LCV, TYPE.BACKTRACKING,greedyColoringGlobal);
                break;
            case 3:
                greedyColoringLocal = getGreedyColoring(graph, TYPE.BASIC);
                findChromaticNumberGA(graph,greedyColoringLocal);
                break;
            case 4:
                greedyColoringLocal = getGreedyColoring(graph, TYPE.BASIC);
                findChromaticNumberKC(graph,greedyColoringLocal);
                break;
            case 5:
                greedyColoringLocal = getGreedyColoring(graph, TYPE.BASIC);
                findChromaticNumberBE(graph,greedyColoringLocal);
                break;
        }
    }
}

