package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.undo.UndoableDrag;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.undo.CompoundEdit;

public class MultiSelectTool extends AbstractTool {

    private Figure figure;
    private int lastX, lastY;
    private boolean moved;
    private Map<Figure, Point> initialFigureLocations;

    public MultiSelectTool() {
    }

    public MultiSelectTool(Editor editor, Figure figure) {
        super(editor);
        this.figure = figure;
        moved = false;
    }

    @Override
    public void mousePressed(int x, int y, MouseEvent e) {
        super.mousePressed(x, y, e);
        lastX = x;
        lastY = y;

        if (e.isShiftDown()) {
            getDrawing().toggleSelection(figure);
            figure = null;
        } else if (!getDrawing().isFigureSelected(figure)) {
            getDrawing().clearSelections();
            getDrawing().addSelection(figure);
        }
    }

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        super.mouseDrag(x, y, e);
        moved = (Math.abs(x - getStartX()) > 4) || (Math.abs(y - getStartY()) > 4);

        if (moved && !getEditor().isReadonly()) {
            int dx = x - lastX;
            int dy = y - lastY;
                        
            preservePreviousFigureLocations();
            
            for (Figure f : getDrawing().getSelections()) {
                if (!(f instanceof LineConnector)){
                    f.moveBy(dx, dy, e);
                }
            }
        }
        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        if (moved && initialFigureLocations != null){
            logUndoableMove(x, y);
        }
    }
    
    

    @Override
    public Cursor getToolCursor() {
        return new Cursor(Cursor.HAND_CURSOR);
    }

    private void logUndoableMove(int x, int y) {
        int dx = x - getStartX();
        int dy = y - getStartY();
        
        CompoundEdit ce = new CompoundEdit();
        
        for (Figure f : getDrawing().getSelections()){
            ce.addEdit(new UndoableDrag(f, initialFigureLocations.get(f), dx, dy));
        }
        
        ce.end();
        getEditor().addUndoableEdit(ce);
    }

    private void preservePreviousFigureLocations() {
        if (initialFigureLocations == null){
            initialFigureLocations = new HashMap<Figure,Point>();

            for (Figure f : getDrawing().getSelections()) {
                if (!(f instanceof Connector)){
                    initialFigureLocations.put(f, f.getLocation());
                }
            }
        }
    }
}
