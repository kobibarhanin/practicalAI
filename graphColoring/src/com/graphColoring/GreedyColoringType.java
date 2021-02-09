package com.graphColoring;

import java.util.ArrayList;

public class GreedyColoringType {

    public int colors = 0;
    public int vertices = 0;

    public ArrayList<Integer> greedyColoring = new ArrayList<Integer>();

    public GreedyColoringType(int vertices) {

        for(int i=0; i<vertices; i++){
            greedyColoring.add(-1);
        }
    }

    public void clear(){

        while (greedyColoring.remove(new Integer(-1))){}

    }
}
