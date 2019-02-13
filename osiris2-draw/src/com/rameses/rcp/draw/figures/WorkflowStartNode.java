
package com.rameses.rcp.draw.figures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Dimension;

public class WorkflowStartNode extends WorkflowNode{
    public WorkflowStartNode(){
        set(FILL_COLOR, Color.GREEN);
    }

    @Override
    public String getToolCaption() {
        return "Start";
    }
    
        
    @Override
    public String getType(){
        return "start";
    }

    @Override
    public String getIcon() {
        return "images/draw/workflow-start16.png";
    }

    @Override
    public boolean isAllowResize() {
        return false;
    }

    @Override
    public boolean isSystem() {
        return true;
    }
    
    @Override
    public Dimension getSize(){
        return get(WORKFLOW_NODE_DIMENSION);
    }

    @Override
    public boolean isEndConnectionAllowed() {
        return false;
    }

   
    @Override
    protected void drawNodeSymbol(Graphics2D g) {
        Color oldColor = g.getColor();
        Font oldFont = g.getFont();
        
        Rectangle r = getDisplayBox();
        g.setColor(get(FILL_COLOR));
        g.fillOval(r.x, r.y, r.width, r.height);

        g.setColor(get(STROKE_COLOR));
        g.drawOval(r.x, r.y, r.width - 1, r.height - 1);
        
        g.setColor(oldColor);
        g.setFont(oldFont);
    }
    
    @Override
    protected void drawCaption(Graphics2D g) {
        
    }
    
}
