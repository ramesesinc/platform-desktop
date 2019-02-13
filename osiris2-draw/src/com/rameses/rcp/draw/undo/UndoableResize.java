/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.Rectangle;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;

public class UndoableResize extends AbstractUndoableEdit{
    private Figure source;
    private Rectangle oldBounds;
    private Rectangle newBounds;
    
    public UndoableResize(Figure source, Rectangle oldBounds, Rectangle newBounds) {
        this.source = source;
        this.oldBounds = oldBounds;
        this.newBounds = newBounds;
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        super.addEdit(anEdit);
        return false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }
    
    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        if (source != null){
            source.setDisplayBox(oldBounds);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        if (source != null){
            source.setDisplayBox(newBounds);
            
        }
    }
    
}
