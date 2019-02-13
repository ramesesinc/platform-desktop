package com.rameses.rcp.draw.decorators;

import com.rameses.rcp.draw.support.AttributeKeys;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.LineDecoration;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;


public abstract class AbstractLineDecoration implements LineDecoration{
    private boolean filled;
    private boolean stroked;
    private boolean solid;
    
    public AbstractLineDecoration(){
        this(true, true, true);
    }
    
    public AbstractLineDecoration(boolean filled, boolean stroked, boolean solid){
        this.filled = filled;
        this.stroked = stroked;
        this.solid = solid;
    }
    
    /**
     * Method to calculate the path of the decorator
     */
    protected abstract Path2D.Double getDecoratorPath(Figure figure);
    
    /**
     * Method to calculate the radius of the decorator path.
     */
    protected abstract double getDecoratorPathRadius(Figure figure);
    
    @Override
    public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2) {
        Path2D.Double path = getTransformedDecoratorPath(f, p1, p2);
        Color color;
        if (isFilled()) {
            if (isSolid()) {
                color = f.get(STROKE_COLOR);
            } else {
                color = f.get(FILL_COLOR);
            }
            if (color != null) {
                g.setColor(color);
                g.fill(path);
            }
        }
        if (isStroked()) {
            color = f.get(STROKE_COLOR);
            if (color != null) {
                g.setColor(color);
                g.setStroke(AttributeKeys.getStroke(f));
                g.draw(path);
            }
        }
    }
    
    
    protected boolean isFilled(){
        return filled;
    }
    
    protected void setFilled(boolean filled){
        this.filled = filled;
    }
    
    protected boolean isStroked(){
        return stroked;
    }
    
    protected void setStroked(boolean stroked){
        this.stroked = stroked;
    }
    
    protected boolean isSolid(){
        return solid;
    }
    
    protected void setSolid(boolean solid){
        this.solid = solid;
    }
    
    private Path2D.Double getTransformedDecoratorPath(Figure f, Point2D.Double p1, Point2D.Double p2) {
        Path2D.Double path = getDecoratorPath(f);
        double strokeWidth = 1d;
        
        AffineTransform transform = new AffineTransform();
        transform.translate(p1.x, p1.y);
        transform.rotate(Math.atan2(p1.x - p2.x, p2.y - p1.y));
        if (strokeWidth > 1f) {
            transform.scale(1d + (strokeWidth - 1d) / 2d, 1d + (strokeWidth - 1d) / 2d);
        }
        path.transform(transform);
        
        return path;
    }
}
