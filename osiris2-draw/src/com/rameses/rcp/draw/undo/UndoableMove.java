/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

public class UndoableMove extends AbstractUndoableEdit{
    private Editor editor;
    private Figure source;
    private int dx; 
    private int dy;
    
    public UndoableMove(Editor editor, Figure source, int dx, int dy) {
        this.editor = editor;
        this.source= source;
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
        source.moveBy(-dx, -dy, null);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        source.moveBy(dx, dy, null);
    }
    
}
