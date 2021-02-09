package com.graphColoring;

import java.util.Comparator;

public class GaVectorComperator implements Comparator <GaStruct>{
    @Override
    public int compare(GaStruct x, GaStruct y) {

        if(x.geneFitness > y.geneFitness) return 1;
        if(x.geneFitness < y.geneFitness) return -1;
        return 0;

//        Integer num = new Integer(0);
//        num = Integer.compare(x.geneFitness, y.geneFitness);
//        if(num>=0)return 1;
//        else return -1;

//        return (x.geneFitness >= y.geneFitness) ? 1 : -1 ;
    }
}
