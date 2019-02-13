/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.WorkflowDecision;
import com.rameses.rcp.draw.figures.WorkflowNode;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;


public class ConnectorHandleTool extends HandleTool{
    
    public ConnectorHandleTool(Editor editor, Handle handle){
        super(editor, handle);
    }
    
    @Override
    public void mouseMoved(int x, int y, MouseEvent e) {
        Figure toFigure = getDrawing().figureAt(x, y, getOwner());
        if (isAllowConnect(toFigure, x, y)){
            getCanvas().setCursor(getHandle().getCursor());
        }else {
            getCanvas().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        super.mouseReleased(x, y, e);
        
        Figure toFigure = getDrawing().figureAt(x, y, getOwner());
        if (!isAllowConnect(toFigure, x, y)){
            return;
        }
        
        Connector c = (Connector) getOwner();
        
        Figure fromFigure = null;
        if (isHead(x, y)){
            fromFigure = c.getEndFigure();
            c.setEndFigure(toFigure);
        }else{
            fromFigure = c.getStartFigure();
            c.setStartFigure(toFigure, true);
            c.setEndFigure(c.getEndFigure());
        }
        fromFigure.removeConnector(c);
        toFigure.addConnector(c);
        
        //notify handlers
        getEditor().connectionChanged(c, fromFigure, toFigure);
    }

    private boolean isAllowConnect(Figure f, int x, int y) {
        if (f == null) 
            return false;
        if (!(f instanceof WorkflowNode || f instanceof WorkflowDecision ))
            return false;
        if (!f.isConnectionAllowed())
            return false;
        
        if (isHead(x, y) && !f.isEndConnectionAllowed())
            return false;
        if (isTail(x, y) && !f.isStartConnectionAllowed())
            return false;
        
        if (!(isHead(x, y) || isTail(x, y))){
            return false;
        }
        
        return true;
    }
    
    private boolean isHead(int x, int y){
        Connector c = (Connector)getOwner();
        Point p = c.getPoints().get(c.getPoints().size() - 1);
        return getHandle().getDisplayBox().contains(p);
    }
    
    private boolean isTail(int x, int y){
        Connector c = (Connector)getOwner();
        Point p = c.getPoints().get(0);
        return getHandle().getDisplayBox().contains(p);
    }
    
}
