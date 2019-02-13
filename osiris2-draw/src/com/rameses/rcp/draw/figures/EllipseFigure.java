
package com.rameses.rcp.draw.figures;

import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.handles.BoxHandle;
import com.rameses.rcp.draw.support.AttributeKeys;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class EllipseFigure extends AbstractAttributedFigure{
    public EllipseFigure(){
        this(new Point(0,0), new Point(0,0));
    }
    
    public EllipseFigure(Point startPoint, Point endPoint){
        setDisplayBox(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        BoxHandle.addHandles(this);
    }
    
    @Override
    public String getToolCaption() {
        return "Ellipse";
    }
    
        
    @Override
    public String getType(){
        return "ellipse";
    }
    
    @Override
    public String getIcon() {
        return "images/draw/ellipse16.png";
    }
    
    @Override
    protected void drawFigure(Graphics2D g) {
        Rectangle r = getDisplayBox();
        g.setStroke(AttributeKeys.getStroke(this));
        
        g.setColor(get(FILL_COLOR));
        g.fillOval(r.x, r.y, r.width, r.height);
        
        g.setColor(get(STROKE_COLOR));
        g.drawOval(r.x, r.y, r.width, r.height);
    }
}
