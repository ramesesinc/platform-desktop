
package com.rameses.rcp.draw.figures;

import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.handles.BoxHandle;
import com.rameses.rcp.draw.support.AttributeKeys;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public class RectangleFigure extends AbstractAttributedFigure{
    public RectangleFigure(){
        this(new Point(0,0), new Point(0,0));
    }
    
    public RectangleFigure(Point startPoint, Point endPoint){
        setDisplayBox(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        BoxHandle.addHandles(this);
    }
    
    @Override
    public String getToolCaption() {
        return "Rectangle";
    }
    
        
    @Override
    public String getType(){
        return "rectangle";
    }
    
    @Override
    public String getIcon() {
        return "images/draw/rect16.png";
    }
    
    @Override
    protected void drawFigure(Graphics2D g) {
        Rectangle r = getDisplayBox();
        g.setStroke(AttributeKeys.getStroke(this));
        
        g.setColor(get(FILL_COLOR));
        g.fillRect(r.x, r.y, r.width, r.height);
        
        g.setColor(get(STROKE_COLOR));
        g.drawRect(r.x, r.y, r.width, r.height);
    }
}
