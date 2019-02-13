package com.rameses.rcp.draw.support;

import java.awt.Dimension;
import java.awt.Point;


public class RotatedRectangle {
    private Dimension size = new Dimension(0, 0);
    private Point[] points = new Point[4];
    private Point center;
    
    public Point[] getPoints(){
        return points;
    }
    
    public void setPoints(Point[] points){
        this.points = points;
    }
    
    public Point getCenter(){
        return center;
    }
    
    public void setCenter(Point center){
        this.center = center;
    }
    
    public Dimension getSize(){
        return size; 
    }
    
    public void setSize(Dimension size){
        this.size = size; 
    }
}
