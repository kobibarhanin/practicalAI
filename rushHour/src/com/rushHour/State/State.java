package com.rushHour.State;
import com.rushHour.State.StateObject.Object;
import com.rushHour.State.StateObject.Direction;
import com.rushHour.State.StateObject.Orientation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class State {

    int size;
    public String boardString;
    public ArrayList<Object> objects = new ArrayList<Object>();
    ArrayList<ArrayList<Character>> boardMatrix = new ArrayList<ArrayList<Character>>();


    public ArrayList<State> expansion = new ArrayList<State>();
    State nextState;
    public State parent = null;
    public String prevMove = "";
    public int depth=0;

    public State(int size, String boardString) {
        this.size = size;
        this.boardString = boardString;
        buildBoardMatrix();
        analyzeBoard();
    }

    public State(){
    }

    public boolean compare(State other) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (boardMatrix.get(i).get(j) != other.boardMatrix.get(i).get(j)) return false;
            }
        }
        return true;
    }

    public ArrayList<State> expand() {

        for (int i = 0; i < objects.size(); i++) {
            int moves = 1;
            if (objects.get(i).orientation == Orientation.HORIZONTAL) {
                while (canMove(objects.get(i), Direction.LEFT, moves)) {
                    nextState = new State(6, boardString);
                    nextState.move(nextState.objects.get(i), Direction.LEFT, moves);
                    nextState.parent = this;
                    nextState.prevMove += Character.toString(objects.get(i).identifier) + "L" + moves;
                    nextState.depth=depth+1;
                    expansion.add(nextState);
                    moves++;
                }
                moves = 1;
                while (canMove(objects.get(i), Direction.RIGHT, moves)) {
                    nextState = new State(6, boardString);
                    nextState.move(nextState.objects.get(i), Direction.RIGHT, moves);
                    nextState.parent = this;
                    nextState.prevMove += Character.toString(objects.get(i).identifier) + "R" + moves;
                    nextState.depth=depth+1;
                    expansion.add(nextState);
                    moves++;
                }
            }
            if (objects.get(i).orientation == Orientation.VERTICAL) {
                while (canMove(objects.get(i), Direction.UP, moves)) {
                    nextState = new State(6, boardString);
                    nextState.move(nextState.objects.get(i), Direction.UP, moves);
                    nextState.parent = this;
                    nextState.prevMove += Character.toString(objects.get(i).identifier) + "U" + moves;
                    nextState.depth=depth+1;
                    expansion.add(nextState);
                    moves++;
                }
                moves = 1;
                while (canMove(objects.get(i), Direction.DOWN, moves)) {
                    nextState = new State(6, boardString);
                    nextState.move(nextState.objects.get(i), Direction.DOWN, moves);
                    nextState.parent = this;
                    nextState.prevMove += Character.toString(objects.get(i).identifier) + "D" + moves;
                    nextState.depth=depth+1;
                    expansion.add(nextState);
                    moves++;
                }
            }
        }
        return expansion;
    }

    public void printBord() {
        printHeader();
        for (int i = 0; i < size; i++) {
            System.out.print(" " + (i + 1) + " | ");
            for (int j = 0; j < size; j++) {
                System.out.print(boardMatrix.get(i).get(j) + " ");
            }
            if (i == 2) {
                System.out.println("   ==>");
                continue;
            }
            System.out.println("|");
        }
        printFooter();
    }

    public boolean goalState() {
        if (targetBlockers() == 0) return true;
        return false;
    }

    public void buildBoardMatrix() {
        for (int i = 0; i < size; i++) {
            boardMatrix.add(new ArrayList<Character>());
            for (int j = 0; j < size; j++) {
                boardMatrix.get(i).add(boardString.charAt(i * size + j));
            }
        }
    }

    public boolean canMove(Object object, Direction direction, int distance) {
        switch (direction) {
            case UP:
                if (object.locationFrom == 0 || distance > object.locationFrom) return false;
                for (int i = 1; i <= distance; i++) {
                    if (boardMatrix.get(object.locationFrom - i).get(object.locationRowCol) != '.') return false;
                }
                return true;
            case DOWN:
                if (object.locationTo == size - 1 || distance > size - object.locationTo - 1) return false;
                for (int i = 1; i <= distance; i++) {
                    if (boardMatrix.get(object.locationTo + i).get(object.locationRowCol) != '.') return false;
                }
                return true;
            case LEFT:
                if (object.locationFrom == 0 || distance > object.locationFrom) return false;
                for (int i = 1; i <= distance; i++) {
                    if (boardMatrix.get(object.locationRowCol).get(object.locationFrom - i) != '.') return false;
                }
                return true;
            case RIGHT:
                if (object.locationTo == size - 1 || distance > size - object.locationTo - 1) return false;
                for (int i = 1; i <= distance; i++) {
                    if (boardMatrix.get(object.locationRowCol).get(object.locationTo + i) != '.') return false;
                }
                return true;
        }
        return false;
    }

    public void move(Object object, Direction direction, int distance) {
        if (direction == Direction.DOWN) {
            eraseCar(object);
            placeCar(object, object.locationFrom + distance);
            object.locationFrom += distance;
            object.locationTo += distance;
        }
        if (direction == Direction.UP) {
            eraseCar(object);
            placeCar(object, object.locationFrom - distance);
            object.locationFrom -= distance;
            object.locationTo -= distance;
        }
        if (direction == Direction.LEFT) {
            eraseCar(object);
            placeCar(object, object.locationFrom - distance);
            object.locationFrom -= distance;
            object.locationTo -= distance;
        }
        if (direction == Direction.RIGHT) {
            eraseCar(object);
            placeCar(object, object.locationFrom + distance);
            object.locationFrom += distance;
            object.locationTo += distance;
        }
        writeString();
    }

    public void writeString() {
        StringBuilder sb = new StringBuilder(boardString);
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.setCharAt(k, boardMatrix.get(i).get(j));
                k++;
            }
        }
        boardString = sb.toString();
    }

    public void placeCar(Object object, int locationFrom) {
        if (object.orientation == Orientation.HORIZONTAL) {
            for (int i = locationFrom; i < locationFrom + object.carSize; i++) {
                boardMatrix.get(object.locationRowCol).set(i, object.identifier);
            }
        }
        if (object.orientation == Orientation.VERTICAL) {
            for (int i = locationFrom; i < locationFrom + object.carSize; i++) {
                boardMatrix.get(i).set(object.locationRowCol, object.identifier);
            }
        }
    }

    public void eraseCar(Object object) {
        if (object.orientation == Orientation.HORIZONTAL) {
            for (int i = object.locationFrom; i <= object.locationTo; i++) {
                boardMatrix.get(object.locationRowCol).set(i, '.');
            }
        }
        if (object.orientation == Orientation.VERTICAL) {
            for (int i = object.locationFrom; i <= object.locationTo; i++) {
                boardMatrix.get(i).set(object.locationRowCol, '.');
            }
        }
    }

    public void analyzeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (j == size - 1) continue;
                int objectSize = 1;
                while (boardMatrix.get(i).get(j) != '.' && boardMatrix.get(i).get(j) == boardMatrix.get(i).get(j + 1)) {
                    objectSize++;
                    j++;
                    if (j == size - 1) break;
                }
                if (objectSize > 1) {
                    objects.add(new Object(boardMatrix.get(i).get(j), Orientation.HORIZONTAL, i, j - objectSize + 1, j));
                }
            }
        }
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                if (i == size - 1) continue;
                int objectSize = 1;
                while (boardMatrix.get(i).get(j) != '.' && boardMatrix.get(i).get(j) == boardMatrix.get(i + 1).get(j)) {
                    objectSize++;
                    i++;
                    if (i == size - 1) break;
                }
                if (objectSize > 1) {
                    objects.add(new Object(boardMatrix.get(i).get(j), Orientation.VERTICAL, j, i - objectSize + 1, i));
                }
            }
        }

    }

    public void printHeader() {
        System.out.print("     ");
        for (int j = 1; j <= size; j++)
            System.out.print(j + " ");

        System.out.print("\n   +");
        for (int k = 1; k <= size * 2 + 1; k++)
            System.out.print("-");
        System.out.println("+");
    }

    public void printFooter() {
        System.out.print("   +");
        for (int k = 1; k <= size * 2 + 1; k++)
            System.out.print("-");
        System.out.println("+");
    }

    public Object getCar(char identifier) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).identifier == identifier)
                return objects.get(i);
        }
        return null;
    }

    public int targetDistance() {
        int targetCarLocation = 0;
        for (; targetCarLocation < objects.size(); targetCarLocation++) {
            if (objects.get(targetCarLocation).identifier == 'X') break;
        }

        return (size - objects.get(targetCarLocation).locationTo);
    }

    public int targetBlockers() {
        int blockers = 0;
        int targetCarIndex;
        int targetCarLocation = 0;
        for (targetCarIndex = 0; targetCarIndex < objects.size(); targetCarIndex++) {
            if (objects.get(targetCarIndex).identifier == 'X') {
                targetCarLocation = objects.get(targetCarIndex).locationFrom;
                break;
            }
        }

        for (int i = targetCarLocation; i < size; i++) {
            if (!boardMatrix.get(2).get(i).equals('.') && !boardMatrix.get(2).get(i).equals('X')) blockers++;
        }
        return blockers;
    }

    public int calcBlockers(char identifier, Orientation orientation, int locColRow, int currentDepth, int targetDepth) {
        int blockers = 0;
        ArrayList<Object> blockerObjects = new ArrayList<Object>();

        if (currentDepth == 0) {

            int targetCarIndex;
            int targetCarLocation = 0;

            for (targetCarIndex = 0; targetCarIndex < objects.size(); targetCarIndex++) {
                if (objects.get(targetCarIndex).identifier == identifier) {
                    targetCarLocation = objects.get(targetCarIndex).locationFrom;
                    break;
                }
            }

            for (int i = targetCarLocation; i < size; i++) {
                if (!boardMatrix.get(2).get(i).equals('.') && !boardMatrix.get(2).get(i).equals('X')) {
                    blockerObjects.add(getCar(boardMatrix.get(2).get(i)));
                    blockers++;
                }
            }
        } else {
            if (orientation == Orientation.VERTICAL) {

                for (int i = 0; i < size; i++) {
                    if (!boardMatrix.get(i).get(locColRow).equals('.') && !boardMatrix.get(i).get(locColRow).equals(identifier)) {
                        blockerObjects.add(getCar(boardMatrix.get(i).get(locColRow)));
                        blockers++;
                    }
                }
            }
            if (orientation == Orientation.HORIZONTAL) {
                for (int i = 0; i < size; i++) {
                    if (!boardMatrix.get(locColRow).get(i).equals('.') && !boardMatrix.get(locColRow).get(i).equals(identifier)) {
                        blockerObjects.add(getCar(boardMatrix.get(locColRow).get(i)));
                        blockers++;
                    }
                }
            }
        }

        if (currentDepth == targetDepth) return blockers;

        if (currentDepth < targetDepth) {
            for (int i = 0; i < blockerObjects.size(); i++) {
                blockers += calcBlockers(blockerObjects.get(i).identifier, blockerObjects.get(i).orientation, blockerObjects.get(i).locationRowCol, currentDepth + 1, targetDepth);
            }
        }

        return blockers;
    }

    public static ArrayList<State> path (State state){

        ArrayList<State> path = new ArrayList<>();
        while (state.parent!=null){
            State newState =new State(6, state.boardString);
            newState.prevMove= state.prevMove;
            path.add(newState);
            state = state.parent;
        }
        return path;
    }

    public static void printPath(ArrayList<State> path, boolean printBoard){
        System.out.println("Path size: " + path.size());
        for(int i = path.size()-1; i >=0 ; i--){
            if (printBoard) path.get(i).printBord();
            else System.out.print(path.get(i).prevMove + " ");
        }
        System.out.print("\n");
    }

    public static double calcCutOffs(String type, HashMap<String,State> openedStates){
        double cutoffs=openedStates.size();
        double cutoffsTotal=0;
        int maxCutOff=-1;
        int minCutoff=Integer.MAX_VALUE;
        Iterator it = openedStates.entrySet().iterator();

        switch (type){
            case "MIN":
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    minCutoff = min(minCutoff,((State)(pair.getValue())).depth);
                    it.remove();
                }
                return minCutoff;
            case "MAX":
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    maxCutOff = max(maxCutOff,((State)(pair.getValue())).depth);
                    it.remove();
                }
                return maxCutOff;
            case "AVG":
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    cutoffsTotal +=((State)(pair.getValue())).depth;
                    it.remove();
                }
                return (cutoffsTotal/cutoffs);
        }
        return -1;
    }

    public int clacHeuristicValue(){
        if (getCar('X').locationTo >= 4)
            return calcBlockers('X', Orientation.HORIZONTAL, 2, 0, 1) + size - targetDistance();
        else
            return calcBlockers('X', Orientation.HORIZONTAL, 2, 0, 1) + targetDistance();
    }

    public int getGroundValue(int intensity){
        return depth*intensity;
    }

    public int calcHeuristicFunction() {
        return clacHeuristicValue() + getGroundValue(2);
    }

}

