/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.support.AttributeKey;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;

public class UndoableAttribute extends AbstractUndoableEdit{
    private Figure source;
    private AttributeKey key;
    private Object oldValue;
    private Object newValue;

    public UndoableAttribute(Figure source, AttributeKey key, Object oldValue, Object newValue) {
        this.source = source;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
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
            source.set(key, oldValue);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        if (source != null){
            source.set(key, newValue);
        }
    }
    
}
