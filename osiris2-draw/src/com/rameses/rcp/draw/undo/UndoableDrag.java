/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.Point;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

public class UndoableDrag extends AbstractUndoableEdit{
    private Figure source;
    private Point initialLocation;
    private int dx; 
    private int dy;
    
    public UndoableDrag(Figure source, Point initialLocation, int dx, int dy) {
        this.source= source;
        this.initialLocation = initialLocation;
        this.dx = dx;
        this.dy = dy;
    }
    
    @Override
    public boolean isSignificant() {
        return true;
    }
    
    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        if (source instanceof Connector){
            LineConnector c = (LineConnector)source;
            if (c.getPoints().size() > 2){
                for (int i = 1; i < c.getPoints().size() - 1; i++){
                    Point pt = c.getPoints().get(i);
                    pt.x -= dx;
                    pt.y -= dy;
                }
            }
            if (c.getInnerText() != null){
                Point pt = c.getInnerText().getLocation();
                pt.x -= dx;
                pt.y -= dy;
                c.getInnerText().setLocation(pt);
            }
        }else {
            source.moveBy(-dx, -dy, null);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        if (source instanceof Connector){
            LineConnector c = (LineConnector)source;
            if (c.getPoints().size() > 2){
                for (int i = 1; i < c.getPoints().size() - 1; i++){
                    Point pt = c.getPoints().get(i);
                    pt.x += dx;
                    pt.y += dy;
                }
            }
            if (c.getInnerText() != null){
                Point pt = c.getInnerText().getLocation();
                pt.x += dx;
                pt.y += dy;
                c.getInnerText().setLocation(pt);
            }
        }else {
            source.moveBy(dx, dy, null);
        }
        
    }
    
}
