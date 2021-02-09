package com.graphColoring;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.abs;

public class State {

    /* primitive variables */
    public int vertices;
    public int colors;
    public int verticesAssigned = 0;
    public boolean fullyExpanded = false;
    public int currentVertice = 0;
    public int currentColor = 0;

    /* structures*/
    ArrayList<Integer> coloringVector = new ArrayList<Integer>();//holds colors assignments to vertices (3,1,0,2) => vertice 0 is colored with color 3...
    ArrayList<Integer> assignmentsOrderVector = new ArrayList<Integer>();
    HashMap<Integer,Integer> statesVector = new HashMap<Integer,Integer>();//holds for each color assignment in the coloring vector the state in which it was assigned

    public State(int vertices, int colors) {

        this.vertices = vertices;
        this.colors = colors;
        this.verticesAssigned=0;
        initVectors();

    }

    public State(State other) {

        this.vertices = other.vertices;
        this.colors = other.colors;
        this.verticesAssigned=other.verticesAssigned;
        this.fullyExpanded=other.fullyExpanded;
        this.coloringVector.addAll(other.coloringVector);
        this.assignmentsOrderVector.addAll(other.assignmentsOrderVector);
        this.statesVector = other.statesVector;

    }

    public void initVectors(){
        for (int i=0; i<this.vertices; i++){
            this.coloringVector.add(-1);
            this.assignmentsOrderVector.add(-1);
        }
    }

    public ArrayList<State> expand(Graph graph, TYPE filteringMethod, TYPE orderingMethod , ColoringTable coloringTable, ArrayList<State> expansion){
        if(verticesAssigned<vertices) {
            for (int i = 0; i < colors; i++) {
                State state = new State(this);
                int color=i;
                int vertice = -1;
                switch (orderingMethod) {
                    case NONE:
                        vertice = verticesAssigned;
                        break;
                    case MRV:
                        vertice = getMinRemainingValues(coloringTable);
                        break;
                    case MRV_LCV:
                        vertice = getMinRemainingValues(coloringTable);
                        color = coloringTable.getLeastConstrainingValue(vertice, graph);
                }
                boolean expansionCondition = false;
                switch (filteringMethod) {
                    case NONE:
                        expansionCondition = true;
                        break;
                    case FC:
                        expansionCondition = (verticesAssigned < vertices && coloringTable.assignColor(vertice, color));
                        break;
                    case FC_AC:
                        expansionCondition = (verticesAssigned < vertices && coloringTable.assignColor(vertice, color));
                        break;
                    case AC:
                        expansionCondition = true;
                        break;
                }
                coloringTable = vectorToTable(this.coloringVector,colors,graph);
                if (expansionCondition) {
                    state.currentVertice = vertice;
                    state.currentColor = color;

                    assignmentsOrderVector.set(vertice,verticesAssigned);
                    state.assignmentsOrderVector =this.assignmentsOrderVector;

                    state.coloringVector.set(vertice, color);


                    if (verticesAssigned == vertices) {
                        state.fullyExpanded = true;
                    }
                    if (!state.fullyExpanded) state.verticesAssigned++;
                    state.statesVector.put(vertice, this.verticesAssigned);
                    expansion.add(state);
                }
                else state = null;
            }
        }
        return expansion;
    }

    public int getMinRemainingValues(ColoringTable coloringTable){
        int mrv = Integer.MAX_VALUE;
        int ver = -1;
        for(int i=0; i<coloringTable.remainingValues.size(); i++){
            int rv = coloringTable.remainingValues.get(i);
            if(mrv>=rv){
                mrv=rv;
                ver=i;
            }
        }

        return ver;
    }

    public ArrayList<Integer> getConflictSet(Graph graph, int currnetVertice){
        ArrayList<Integer> conflictSet = new ArrayList<Integer>();
        for(int i=0; i<vertices; i++){
            if(coloringVector.get(i)!=-1 && graph.isEdge(i,currnetVertice)){
                conflictSet.add(i);
            }
        }
        return conflictSet;
    }

    public int getConflictSetRoot(Graph graph){

        ArrayList<Integer> conflictSet = getConflictSet(graph,this.currentVertice);

        int min = Integer.MAX_VALUE;
        int ret=-1;
        for(int i =0; i<conflictSet.size(); i++){
            if(statesVector.get(conflictSet.get(i)) < min){
                min = statesVector.get(conflictSet.get(i));
                ret = conflictSet.get(i);
            }
        }

        return ret;
    }

    public boolean isGoalState(Graph graph){

        if (this.verticesAssigned<this.vertices) return false;

        for(int i=0; i<vertices; i++){
            if(!graph.checkNeighborhood(this.coloringVector,i)) return false;
        }

        return true;
    }

    public boolean isValidState(Graph graph){

        for(int i=0; i<verticesAssigned; i++){
            if(!graph.checkNeighborhood(this.coloringVector,getVerticeAssignedAt(i))) return false;
        }

        return true;
    }

    public int getVerticeAssignedAt(int assignment){

        for(int i = 0; i< assignmentsOrderVector.size(); i++){
            if(assignment== assignmentsOrderVector.get(i)) return i;
        }
        return -1;
    }

    public ColoringTable vectorToTable(ArrayList<Integer> coloringVector, int colors, Graph graph){
        ColoringTable coloringTable = new ColoringTable(coloringVector.size(), colors);

        for(int i = 0; i<coloringVector.size(); i++){
            if(coloringVector.get(i)!=-1) {
                coloringTable.assignColor(i, coloringVector.get(i));
                coloringTable.banColor(i, coloringVector.get(i), graph);
                coloringTable.remainingValues.set(i, Integer.MAX_VALUE);
            }
        }

        return coloringTable;
    }


}
