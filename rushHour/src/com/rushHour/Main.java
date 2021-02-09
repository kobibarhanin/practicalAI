package com.rushHour;

import com.rushHour.State.*;
import com.rushHour.State.BoardSupply;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Main {

    public static ArrayList<State> A_Star(State initialState, int timeLimit){

        /* Initialize all data structures needed, states counter, and start time object */
        Date startTime = new Date();
        int stateDeveloped = 1;
        HashMap<String,State> closedStates = new HashMap<String,State>();
        HashMap<String,State> openedStates = new HashMap<String,State>();
        ArrayList<State> stateExpansion = new ArrayList<State>();
        /* PriorityQueue is initialized with a comperator that computes the queue according to the heuristic function value */
        PriorityQueue<State> queue = new PriorityQueue<State>(1, new HeuristicComperator());

        double heuristicTotal = 0;

        /* Initialize a priority queue with the first state */
        queue.add(initialState);
        State currentState = new State();

        while (!queue.isEmpty()){

            /* Time evaluation and States counter */
            stateDeveloped++;
            Date currentTime = new Date();
            double timeTaken = ((double)(currentTime.getTime()-startTime.getTime()))/1000;
            if ( timeTaken > timeLimit) {
                System.out.println("Time limit of: "+ timeLimit +" Exceeded at: " + timeTaken);
                return null;
            }

            /* save previous state for statistical analysis */
            State previousState = new State();
            previousState=currentState;

            /* Get current state from queue and remove from open list */
            currentState = queue.poll();
            openedStates.remove(currentState.boardString);

            heuristicTotal+=currentState.calcHeuristicFunction();

            /* Check if current state is the goal state */
            if(currentState.goalState()) {
                System.out.println("States (N): " + stateDeveloped +
                        "\nSolution depth (D): "+ currentState.depth +
                        "\nPentration (D/N): " + ((double)currentState.depth/(double)stateDeveloped) +
                        "\nEBF = root(D,N): " + root(stateDeveloped,currentState.depth) +
                        "\nMin Cutoff: " + State.calcCutOffs("MIN",new HashMap<String,State>(openedStates)) +
                        "\nMax Cutoff: " + State.calcCutOffs("MAX",new HashMap<String,State>(openedStates)) +
                        "\nAverage Cutoff: " + State.calcCutOffs("AVG",new HashMap<String,State>(openedStates)) +
                        "\nAverage Heuristic value: " + (heuristicTotal/(double)stateDeveloped) +
                        "\nComputation Time: " + timeTaken);
                return State.path(currentState);
            }

            /* Expand current state and add to close list */
            stateExpansion.clear();
            stateExpansion.addAll(currentState.expand());
            closedStates.put(currentState.boardString,currentState);


            /* Iterate through expanded nodes, determine if to insert to queue */
            for (int i = 0; i < stateExpansion.size(); i++){
                State inClosed = closedStates.get(stateExpansion.get(i).boardString);
                State inOpened = openedStates.get(stateExpansion.get(i).boardString);

                if(inClosed==null && (inOpened==null || inOpened.depth > stateExpansion.get(i).depth )){
                    queue.add(stateExpansion.get(i));
                    openedStates.put(stateExpansion.get(i).boardString,stateExpansion.get(i));
                }
            }
        }
        return null;
    }

    public static ArrayList<State> ID_DLS(State initialState){
        int stateDeveloped = 1;
        DLS_Type dls_type = new DLS_Type();
        String boarString = initialState.boardString;
        int depth = 0;
        while (true) {
            State state = new State(6, boarString);
            dls_type = DLS(state,depth);
            stateDeveloped+=dls_type.states;
            if (dls_type.path != null) {
                System.out.println("states: " +stateDeveloped);
                return dls_type.path;
            }
            depth++;
        }
    }

    public static DLS_Type DLS(State startNode, int limit) {
        int stateDeveloped = 1;
        Stack<State> nodeStack = new Stack<>();
        ArrayList<State> expansion = new ArrayList<>();
        HashMap<String, State> visitedNodes = new HashMap<String, State>();
        visitedNodes.put(startNode.boardString, startNode);
        nodeStack.add(startNode);

        while (!nodeStack.isEmpty()) {
            stateDeveloped++;
            State current = nodeStack.pop();
            if (current.goalState()) {
                return new DLS_Type(stateDeveloped, State.path(current));
            }
            if (current.depth < limit) {

                expansion.clear();
                expansion.addAll(current.expand());

                for (int i = 0; i < expansion.size(); i++) {
                    State node = visitedNodes.get(expansion.get(i).boardString);
                    if (node == null || (node.depth > expansion.get(i).depth)) {
                        nodeStack.add(expansion.get(i));
                        visitedNodes.put(expansion.get(i).boardString, expansion.get(i));
                    }
                }

            }

        }

        return new DLS_Type(stateDeveloped, null);
    }

    public static double root(double num, double root)
    {
        return Math.pow(Math.E, Math.log(num)/root);
    }

    //========================================================================

    public static void main(String[] args) {
        /* build all problem boards */
        ArrayList<State> states = new ArrayList<State>();
        BoardSupply bs = new BoardSupply();
        states = bs.states;

        int input = 0;

        System.out.println("Welcome to Rush Hour AI Solver!\n" +
                "==================================================\n" +
                "This program will solve Rush Hour type problems as seen below:\n");
        bs.states.get(0).printBord();
        System.out.println();
        System.out.println("The board is a 6X6, the cars are marked by letters and empty spaces by dots(.).\n" +
                "The car to be extracted is the one marked: XX");
        System.out.println("The program is included with some pre-set problems, and receives string input problems.");
        System.out.println("============= Rush Hour Solver Modes =============\n" +
                "1. Run all pre-set problems using A* algorithem.\n" +
                "2. Run all pre-set problems using ID-DLS algorithem.\n" +
                "3. Run a specific pre-set problem using A* algorithem.\n" +
                "4. Run a specific pre-set problem using ID-DLS algorithem.\n" +
                "5. Enter your own problem in a string format (solves with A*).\n" +
                "6. Exit Rush Hour AI Solver.\n" +
                "==================================================\n" +
                "Please enter your choice <1,2,3,4,5,6>: ");
        Scanner scanner = new Scanner(System.in);
        input = scanner.nextInt();


        switch (input) {
            case 1:
                /* run all problems using A_Star */
                System.out.println("Enter the Time Limit to solve each problem [seconds]: ");
                int timeLimit = scanner.nextInt();
                int total1 = 0;
                ArrayList<State> path1 = new ArrayList<State>();
                for (int i = 0; i < 40; i++) {
                    System.out.println("================ Problem numer: " + (i+1) + "================");
                    path1 = A_Star(states.get(i), timeLimit);
                    if (path1 == null) {
                        System.out.println("Problem number " + i+1 + " Failed");
                        continue;
                    } else {
                        total1 += path1.size();
                        State.printPath(path1,false);
                    }
                }
                System.out.println("==================================================");
                System.out.println("Total path = " + total1);
                System.out.println("==================================================\n");
                break;
            case 2:
                /* run all problems using ID-DLS */
                int total2 = 0;
                ArrayList<State> path2 = new ArrayList<State>();
                for (int i = 0; i < 40; i++) {
                    System.out.println("================ Problem numer: " + (i+1) + "================");
                    path2 = ID_DLS(states.get(i));
                    State.printPath(path2,false);
                    total2 += path2.size();
                }
                System.out.println("==================================================");
                System.out.println("Total path = " + total2);
                System.out.println("==================================================\n");
                break;
            case 3:
                ArrayList<State> path3 = new ArrayList<State>();
                System.out.println("Enter pre-set problem number [1,40]: ");
                int problem3 = scanner.nextInt();
                System.out.println("================ Problem numer: " + problem3 + "================");
                path3 = A_Star(states.get(problem3-1), 30);
                if (path3 == null) {
                    System.out.println("Problem number " + problem3 + " Failed");
                } else {
                    State.printPath(path3,true);
                    System.out.println("==================================================\n");
                }
                break;
            case 4:
                ArrayList<State> path4 = new ArrayList<State>();
                System.out.println("Enter pre-set problem number [1,40]: ");
                int problem4 = scanner.nextInt();
                System.out.println("================ Problem numer: " + problem4 + "================");
                path4 = ID_DLS(states.get(problem4-1));
                if (path4 == null) {
                    System.out.println("Problem number " + problem4 + " Failed");
                } else {
                    State.printPath(path4,true);
                    System.out.println("==================================================\n");
                }
                break;
            case 5:
                ArrayList<State> path5 = new ArrayList<State>();
                System.out.println("Enter your problem in string format. for example this board:");
                states.get(0).printBord();
                System.out.println("Is translated to this string:\n" +
                        states.get(0).boardString);
                System.out.println("Enter string: ");
                scanner.nextLine();
                String problem5 = scanner.nextLine();
                System.out.println("================ Problem solution ================");
                path5 = A_Star(new State(6,problem5), 30);
                if (path5 == null) {
                    System.out.println("Problem solution Failed");
                } else {
                    State.printPath(path5,true);
                    System.out.println("==================================================\n");
                }
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid Entry.\n");
                break;
        }

        System.out.println("Thank You.");
    }

}


