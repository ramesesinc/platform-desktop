
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

public class WorkflowNode extends ImageFigure{
    public WorkflowNode(){
        set(FILL_COLOR, Color.LIGHT_GRAY);
    }
    
    @Override
    public String getCategory() {
        return "workflow";
    }
    
    @Override
    public String getToolCaption() {
        return "Node";
    }
    
        
    @Override
    public String getType(){
        return "process";
    }

    @Override
    public String getIcon() {
        return "images/draw/workflow-node16.png";
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
        Rectangle r = getDisplayBox();
        
        g.setStroke(AttributeKeys.getStroke(this));
        g.setColor(get(FILL_COLOR));
        g.fillRect(r.x, r.y, r.width, r.height);

        g.setColor(get(STROKE_COLOR));
        g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
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

}
