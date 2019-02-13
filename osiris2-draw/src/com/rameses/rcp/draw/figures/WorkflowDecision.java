
package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.support.AttributeKeys;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.tools.WorkflowCreationTool;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Dimension;

public class WorkflowDecision extends ImageFigure{
    private int nPoints = 4;
    private int[] xPoints = new int[nPoints];
    private int[] yPoints = new int[nPoints];
    
    public WorkflowDecision(){
        set(FILL_COLOR, Color.LIGHT_GRAY);
    }
    
    @Override
    public String getCategory() {
        return "workflow";
    }
    
    @Override
    public String getToolCaption() {
        return "Decision";
    }
    
        
    @Override
    public String getType(){
        return "decision";
    }

    @Override
    public String getIcon() {
        return "images/draw/workflow-decision16.png";
    }
    
        @Override
    public Tool getTool() {
        return new WorkflowCreationTool();
    }
    
    @Override
    protected void drawFigure(Graphics2D g) {
        if (getImage() == null){
            drawNodeSymbol(g);
            drawCaption(g);
        }
        else{
            super.drawFigure(g);
        }
    }

    protected void drawNodeSymbol(Graphics2D g) {
        buildPolygonPoints();
        
        g.setStroke(AttributeKeys.getStroke(this));
        g.setColor(get(FILL_COLOR));
        g.fillPolygon(xPoints, yPoints, nPoints);
        
        g.setColor(get(STROKE_COLOR));
        g.drawPolygon(xPoints, yPoints, nPoints);
    }
    
    @Override
    public boolean isEmpty() {
        Rectangle r = getDisplayBox();
        if ( r.width <= 2 || r.height <= 2 ) {
            return true;
        }
        return false;
    }

    @Override
    protected void drawIndex(Graphics2D g) {
        Rectangle r = getDisplayBox();
        Dimension ts = DrawUtil.getTextSize(g, getIndex()+"");
        int x = r.x + (r.width - ts.width) / 2;
        int y = r.y + r.height + ts.height - 4;
        g.setColor(Color.RED);
        g.drawString(getIndex()+"", x, y);
    }

    private void buildPolygonPoints() {
        Rectangle r = getDisplayBox();
        
        //top center
        xPoints[0] = r.x + (r.width / 2);
        yPoints[0] = r.y;
        
        //right mid 
        xPoints[1] = r.x + r.width;
        yPoints[1] = r.y + (r.height / 2);
        
        //bottom center
        xPoints[2] = r.x + (r.width / 2);
        yPoints[2] = r.y + r.height;
        
        //left mid 
        xPoints[3] = r.x;
        yPoints[3] = r.y + (r.height / 2);
    }

}
