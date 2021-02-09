package com.rushHour.State;

import java.util.Comparator;


public class HeuristicComperator implements Comparator<State>
{
    @Override
    public int compare(State x, State y)
    {
        if (x.calcHeuristicFunction() < y.calcHeuristicFunction()) {
            return -1;
        }
        if (x.calcHeuristicFunction() >= y.calcHeuristicFunction()) {
            return 1;
        }
        return 0;
    }
}