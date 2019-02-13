
package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.tools.LineTool;
import com.rameses.rcp.draw.utils.Geom;
import java.awt.Point;
import java.util.Map;

public class LineFigure extends PolyLineFigure{
    private static int MINIMUM_LENGTH = 2;
    
    public LineFigure(){
    }
    
    public LineFigure(int x1, int y1, int x2, int y2){
        this(new Point(x1, y1), new Point(x2, y2));
    }
    
    public LineFigure(Point p1, Point p2){
        super.addPoint(p1);
        super.addPoint(p2);
    }
    
    @Override
    public String getToolCaption() {
        return "Line";
    }
    
        
    @Override
    public String getType(){
        return "line";
    }
    
    @Override
    public Tool getTool() {
        return new LineTool();
    }

    @Override
    public String getIcon() {
        return "images/draw/line16.png";
    }
    
    
    
    
    @Override
    public boolean isEmpty() {
        Point p1 = getPoints().get(0);
        Point p2 = getPoints().get(1);
        
        if (p1.x == Integer.MAX_VALUE || p2.x == Integer.MAX_VALUE){
            return true;
        }
        return (Geom.length(p1.y, p1.y, p2.x, p2.y) <= MINIMUM_LENGTH );
    }

    @Override
    public boolean isConnectionAllowed() {
        return false;
    }

 
}
