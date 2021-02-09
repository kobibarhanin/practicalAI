package com.graphColoring;

import java.util.ArrayList;
import java.util.HashMap;


public class ColoringTable {

    public int vertices;
    public int colors;

    ArrayList<HashMap<Integer,TYPE>> colorTable = new ArrayList<HashMap<Integer,TYPE>>();
    ArrayList<Integer> remainingValues = new ArrayList<Integer>();

    public void clearTable(){
        colorTable.clear();
        remainingValues.clear();
        buildColorTable(colors);
        buildRemainingValues(colors);
    }

    public ColoringTable(int vertices, int colors) {
        this.vertices = vertices;
        this.colors = colors;
        buildColorTable(colors);
        buildRemainingValues(colors);
    }

    public boolean imposeArcConsistency(Graph graph) {

        boolean change = true;

        while (change) {

            change = false;

            for (int head = 0; head < vertices; head++) {
                for (int tail = 0; tail < vertices; tail++) {
                    if (graph.isEdge(head, tail)) {
                        int cons = makeConsistent(head, tail);
                        if (cons == 0) return false;
                        if (cons == 1) change = true;
                    }
                }
            }

        }
        return true;
    }

    public int makeConsistent(int head, int tail) {

        boolean retTotal = true;
        boolean change = false;

        for (int colorInTail = 0; colorInTail < colors; colorInTail++) {
            boolean isValidAssign = false;
            if (colorTable.get(tail).get(colorInTail) != TYPE.ASSIGNED && colorTable.get(tail).get(colorInTail) != TYPE.COMPLETED) {
                for (int colorInHead = 0; colorInHead < colors; colorInHead++) {
                    if (colorTable.get(head).get(colorInHead) != TYPE.ASSIGNED && colorTable.get(head).get(colorInHead) != TYPE.COMPLETED) {
                        if (colorInTail != colorInHead){
                            isValidAssign = true;
                        }
                    }
                }

                if(isValidAssign==false){
                    if(getUnassignedColors(tail)>1){ //there are colors left to delete from tail
                        colorTable.get(tail).put(colorInTail,TYPE.ASSIGNED);
                        isValidAssign = true;
                        change = true;
                    }
                    else return 0;
                }
                retTotal&=isValidAssign;
            }
        }


        if(retTotal && change){
            return 1;
        }
        if(retTotal) return 2;
        return 0;
    }

    public int getUnassignedColors(int vertice){

        int unassigned=0;

        for(int i=0; i<colors; i++){
            if(colorTable.get(vertice).get(i)==TYPE.UNASSIGNED)
                unassigned++;
        }
        return unassigned;
    }

    public ColoringTable(ColoringTable other) {
        this.vertices = other.vertices;
        this.colors = other.colors;
        buildColorTable(colors);
        buildRemainingValues(colors);
        for(int i=0; i<vertices; i++){
            this.colorTable.set(i,new HashMap<Integer,TYPE>(other.colorTable.get(i)));
            this.remainingValues.set(i,other.remainingValues.get(i));
        }
    }

    public void buildRemainingValues(int colors){
        for(int i = 0; i< vertices; i++){
            this.remainingValues.add(this.colors);
        }
    }

    public void buildColorTable(int colors){
        for(int i = 0; i< vertices; i++){
            colorTable.add(new HashMap<Integer,TYPE>());
            for(int j=0; j<colors; j++){
                colorTable.get(i).put(j,TYPE.UNASSIGNED);
            }
        }
    }

    public boolean assignColor(int vertice, int color){
        if(colorTable.get(vertice).get(color)!=TYPE.UNASSIGNED) {
            return false;
        }
        else {
            colorTable.get(vertice).put(color,TYPE.COLORED);
            completeVertice(vertice);
            return true;
        }
    }

    public void completeVertice(int vertice){
        for(int i = 0; i< colors; i++){
            if(colorTable.get(vertice).get(i)!=TYPE.COLORED)
                colorTable.get(vertice).put(i,TYPE.COMPLETED);
        }
    }

    public void banColor(int verticeAllowd, int color, Graph graph){
        for(int i = 0; i< vertices; i++){
            if(i!=verticeAllowd && graph.isEdge(verticeAllowd,i)){
                colorTable.get(i).put(color,TYPE.ASSIGNED);
                updateRemainingColors(i);
            }
        }
    }

    public void updateRemainingColors(int vertice){
        int rv = 0;

        if(remainingValues.get(vertice)> colors)return;

        for (int color = 0; color< colors; color++){
            if(colorTable.get(vertice).get(color)==TYPE.UNASSIGNED)
                rv++;
        }
        remainingValues.set(vertice,rv);

    }

    public int getLeastConstrainingValue(int vertice, Graph graph){
        int colorToAssign = -1;
        int minAssignments = Integer.MAX_VALUE;

        ArrayList<Integer> neighborhood = graph.getNeighborhood(vertice);

        for(int color=0; color<colors; color++){
            int colorAssignments = 0;
            if(colorTable.get(vertice).get(color)==TYPE.UNASSIGNED) {
                for (int neighbor = 0; neighbor < neighborhood.size(); neighbor++) {
                    if (colorTable.get(neighbor).get(color) == TYPE.UNASSIGNED) {
                        colorAssignments++;
                    }
                }
                if(colorAssignments<minAssignments){
                    minAssignments=colorAssignments;
                    colorToAssign=color;
                }
            }

        }

        return colorToAssign;
    }

}
