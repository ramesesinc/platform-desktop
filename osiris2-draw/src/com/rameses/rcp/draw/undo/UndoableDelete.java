/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.undo;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;

public class UndoableDelete extends AbstractUndoableEdit{
    private List<Figure> deletedFigures;
    private Editor editor;
    
    public UndoableDelete(Editor editor, List<Figure> deletedFigures) {
        this.editor = editor;
        this.deletedFigures = deletedFigures;
    }
    
    @Override
    public void undo() throws CannotRedoException {
        super.undo();
        for (Figure f : deletedFigures){
            editor.addToDrawing(f);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        for (Figure f : deletedFigures){
            editor.getDrawing().addSelection(f);
        }
        editor.deleteSelections();
    }
    
}
