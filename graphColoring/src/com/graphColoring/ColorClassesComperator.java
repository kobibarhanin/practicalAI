package com.graphColoring;

import java.util.Comparator;
import java.util.HashSet;

public class ColorClassesComperator implements Comparator <HashSet<Integer>>{
    @Override
    public int compare(HashSet<Integer> x, HashSet<Integer> y) {

        if(x.size() < y.size()) return 1;
        if(x.size() > y.size()) return -1;
        return 0;


    }
}
