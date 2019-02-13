package com.rameses.rcp.draw.handles;

import com.rameses.rcp.draw.figures.TextFigure;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class TextHandles extends AbstractHandle{
    private Point center; 
    
    public TextHandles(){
    }
    
    public TextHandles(Figure owner){
        this(owner, null);
    }
    
    public TextHandles(Figure owner, Point center){
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
    }
    
    public static void addHandles(TextFigure owner){
        owner.clearHandles();
        for (Point pt : owner.getBoundPoints()){
            owner.addHandle(new TextHandle(owner, pt));
        }
    }
    
}


class TextHandle extends AbstractHandle{
    private Point center; 
    
    public TextHandle(){
    }
    
    public TextHandle(Figure owner, Point center){
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
        
    }

}
