
package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.support.AttributeKeys;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.Point;

public class WorkflowSender extends WorkflowNode {
    public WorkflowSender(){
        super();
    }
    
    
    @Override
    public String getToolCaption() {
        return "Sender";
    }
    
        
    @Override
    public String getType(){
        return "sender";
    }

    @Override
    public String getIcon() {
        return "images/draw/workflow-sender16.png";
    }
    
    @Override
    protected void drawNodeSymbol(Graphics2D g) {
        Point[] pts  = getPolyPoints();
        
        int[] xpts = new int[5];
        int[] ypts = new int[5];
        
        for (int i = 0; i < pts.length; i++){
            xpts[i] = pts[i].x;
            ypts[i] = pts[i].y;
        }
        
        g.setStroke(AttributeKeys.getStroke(this));
        g.setColor(get(FILL_COLOR));
        g.fillPolygon(xpts, ypts, pts.length);
        
        g.setColor(get(STROKE_COLOR));
        g.drawPolygon(xpts, ypts, pts.length);
    }
    
    private Point[] getPolyPoints(){
        Rectangle r = getDisplayBox();
        
        float slopeOffset = 0.80f;
        int nw = (int)(r.width * slopeOffset);
        int cy = r.y + (r.height / 2);
        
        Point[] pts = new Point[5];
        pts[0] = new Point(r.x, r.y);
        pts[1] = new Point(r.x + nw, r.y );
        pts[2] = new Point(r.x + r.width, cy );
        pts[3] = new Point(r.x + nw, r.y + r.height );
        pts[4] = new Point(r.x, r.y + r.height);
        return pts;
    }


}
