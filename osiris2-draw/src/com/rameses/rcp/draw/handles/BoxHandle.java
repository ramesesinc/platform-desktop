package com.rameses.rcp.draw.handles;

import com.rameses.rcp.draw.interfaces.Figure;

public class BoxHandle {
    
    public static void addHandles(Figure owner){
        owner.addHandle(RelativeHandle.north(owner));
        owner.addHandle(RelativeHandle.northEast(owner));
        owner.addHandle(RelativeHandle.east(owner));
        owner.addHandle(RelativeHandle.southEast(owner));
        owner.addHandle(RelativeHandle.south(owner));
        owner.addHandle(RelativeHandle.southWest(owner));
        owner.addHandle(RelativeHandle.west(owner));
        owner.addHandle(RelativeHandle.northWest(owner));
    }
    
}
