package com.rushHour;

import com.rushHour.State.State;

import java.util.ArrayList;

public class DLS_Type {
    public int states;
    public ArrayList<State> path;

    public DLS_Type() {
    }

    public DLS_Type(int states, ArrayList<State> path) {
        this.states = states;
        this.path = path;
    }
}
