/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.interfaces.Figure;
import java.lang.reflect.Method;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

public class UndoableProperty extends AbstractUndoableEdit{
    private Figure source;
    private String propertyName;
    private Object oldValue;
    private Object newValue;
    private Class type;

    public <T> UndoableProperty(Figure source, String propertyName, Class<T> type, Object oldValue, Object newValue) {
        this.source = source;
        this.type = type;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }
    
    
    
    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        try{
            getSetter().invoke(source, oldValue);
        }catch( Exception e){
            InternalError ie = new InternalError("Couldn't invoke setter for property \"" + propertyName + "\" in " + source);
            ie.initCause(e);
            throw ie;
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        try {
            getSetter().invoke(source, newValue);
        } catch (Exception e) {
            InternalError ie = new InternalError("Couldn't invoke setter for property \"" + propertyName + "\" in " + source);
            ie.initCause(e);
            throw ie;
        }
    }
    
    protected Method getSetter() {
        try {
            String methodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            return source.getClass().getMethod(methodName, type);
        } catch (Exception e) {
            InternalError ie = new InternalError("Couldn't find setter for property \"" + propertyName + "\" in " + source);
            ie.initCause(e);
            throw ie;
        }
    }
    
}
