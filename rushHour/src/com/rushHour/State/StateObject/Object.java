package com.rushHour.State.StateObject;

import static java.lang.Math.abs;

public class Object {

    public char identifier;
    public Orientation orientation;
    public int locationRowCol;
    public int locationFrom;
    public int locationTo;
    public int carSize;

    public Object(char identifier, Orientation orientation, int locationRowCol, int locationFrom, int locationTo) {
        this.identifier = identifier;
        this.orientation = orientation;
        this.locationRowCol = locationRowCol;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
        this.carSize=abs(locationFrom-locationTo)+1;
    }

    public boolean isBlocker(Object other){
        if(this.orientation==other.orientation) return false;
        if(abs(this.locationTo-other.locationRowCol)==1||abs(this.locationFrom-other.locationRowCol)==1) return true;
        return false;
    }

    public boolean compare(Object other){
        if(identifier==other.identifier) return true;
        return false;
    }

}
