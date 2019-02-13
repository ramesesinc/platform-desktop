package com.rameses.rcp.draw.handles;

import com.rameses.rcp.draw.figures.PolyLineFigure;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import java.awt.Point;
import java.awt.event.MouseEvent;


public class PolyLineHandle extends AbstractHandle{
    private Point center; 
    
    public PolyLineHandle(){
    }
    
    public PolyLineHandle(Figure owner){
        this(owner, null);
    }
    
    public PolyLineHandle(Figure owner, Point center){
        super.setOwner(owner);
        this.center = center;
    }
    
    @Override
    public Point getCenter() {
        return center;
    }
    
    public void setCenter(Point center) {
        this.center = center;
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        center.x = x;
        center.y = y;
    }
    
    public static void addHandles(PolyLineFigure owner){
        owner.clearHandles();
        for (Point p : owner.getPoints()){
            PolyLineHandle h = new PolyLineHandle(owner, p);
            owner.addHandle(h);
        }
    }
    
}
