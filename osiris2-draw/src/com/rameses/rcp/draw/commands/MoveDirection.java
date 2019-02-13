package com.rameses.rcp.draw.commands;


public enum MoveDirection {
    NORTH(0, -1),
    SOUTH(0, 1),
    EAST(1, 0),
    WEST(-1, 0);
    
    private int offsetX = 0;
    private int offsetY = 0;
        
    MoveDirection(int offsetX, int offsetY){
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    
    public int getOffsetX(){
        return offsetX;
    }
    
    public int getOffsetY(){
        return offsetY;
    }
    
}
