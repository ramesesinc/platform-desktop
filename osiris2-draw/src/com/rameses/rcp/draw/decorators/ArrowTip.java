package com.rameses.rcp.draw.decorators;

import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.geom.Path2D;

public class ArrowTip extends AbstractLineDecoration{
    private double angle;
    private double outerRadius;
    private double innerRadius;
    
    public ArrowTip(){
        this(0.35, 12, 11.3);
    }
    
    public ArrowTip(double angle, double outerRadius, double innerRadius) {
        this(angle, outerRadius, innerRadius, true, false, true);
    }
    
    public ArrowTip(double angle, double outerRadius, double innerRadius, boolean filled, boolean stroked, boolean solid) {
        super(filled, stroked, solid);
        this.angle = angle;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }    
    

    @Override
    protected Path2D.Double getDecoratorPath(Figure figure) {
        double offset = (isStroked()) ? 1 : 0;
        Path2D.Double path = new Path2D.Double();
        path.moveTo((outerRadius * Math.sin(-angle)), (offset + outerRadius * Math.cos(-angle)));
        path.lineTo(0, offset);
        path.lineTo((outerRadius * Math.sin(angle)), (offset + outerRadius * Math.cos(angle)));
        if (innerRadius != 0) {
            path.lineTo(0, (innerRadius + offset));
            path.closePath();
        }
        return path;
    }

    @Override
    protected double getDecoratorPathRadius(Figure figure) {
        double offset = (isStroked()) ? 0.5 : -0.1;
        return innerRadius + offset;
    }

 
    
}
