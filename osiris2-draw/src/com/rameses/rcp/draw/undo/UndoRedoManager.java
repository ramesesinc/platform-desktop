/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.interfaces.Editor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class UndoRedoManager extends UndoManager {
    private Editor editor;
    private boolean undoRedoInProgress = false;
    private UndoAction undoAction;
    private RedoAction redoAction;

    public UndoRedoManager(Editor editor) {
        this.editor = editor;
        this.undoAction = new UndoAction();
        this.redoAction = new RedoAction();
    }
    
    @Override
    public synchronized boolean addEdit(UndoableEdit anEdit) {
        if (undoRedoInProgress){
            anEdit.die();
            return true;
        }
        return super.addEdit(anEdit);
    }

    @Override
    public synchronized void undo() throws CannotUndoException {
        undoRedoInProgress = true;
        try{
            super.undo();
        }finally{
            undoRedoInProgress = false;
            editor.getCanvas().refresh();
        }           
    }

    @Override
    public synchronized void redo() throws CannotRedoException {
        undoRedoInProgress = true;
        try{
            super.redo();
        }finally{
            undoRedoInProgress = false;
            editor.getCanvas().refresh();
        }   
    }
    
    public UndoAction getUndoAction(){
        return undoAction;
    }
    
    public RedoAction getRedoAction(){
        return redoAction;
    }
    
    
    
    public class UndoAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                undo();
            }catch(CannotUndoException ex){
                System.out.println("Cannot undo: " + ex);
            }
        }
        
    }
    
    public class RedoAction extends AbstractAction{
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                redo();
            }catch(CannotRedoException ex){
                System.out.println("Cannot redo: " + ex );
            }
        }
    }
    
    
}
