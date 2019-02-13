
package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.support.AttributeKeys;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static com.rameses.rcp.draw.support.AttributeKeys.*;

public class WorkflowState extends WorkflowNode {
    private static int ARC_WIDTH = 30;
    private static int ARC_HEIGHT = 30;
    
    public WorkflowState(){
        super();
    }
    
    @Override
    public String getToolCaption() {
        return "State";
    }
    
        
    @Override
    public String getType(){
        return "state";
    }

    @Override
    public String getIcon() {
        return "images/draw/workflow-state16.png";
    }
    
    @Override
    protected void drawNodeSymbol(Graphics2D g) {
        Rectangle r = getDisplayBox();
        
        g.setStroke(AttributeKeys.getStroke(this));
        g.setColor(get(FILL_COLOR));
        g.fillRoundRect(r.x, r.y, r.width, r.height, ARC_WIDTH, ARC_HEIGHT);

        g.setColor(get(STROKE_COLOR));
        g.drawRoundRect(r.x, r.y, r.width - 1, r.height - 1, ARC_WIDTH, ARC_HEIGHT);
        
    }
}
