package com.rameses.rcp.draw.interfaces;

import com.rameses.osiris2.Invoker;
import com.rameses.rcp.common.DrawModel;
import com.rameses.rcp.draw.support.AttributeKey;
import com.rameses.rcp.draw.undo.UndoRedoManager;
import com.rameses.rcp.draw.undo.UndoRedoManager.RedoAction;
import com.rameses.rcp.draw.undo.UndoRedoManager.UndoAction;
import com.rameses.rcp.draw.commands.MoveDirection;
import com.rameses.rcp.draw.figures.LineConnector;
import java.awt.Image;
import java.util.List;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

public interface Editor {
    public Drawing getDrawing();
    public void setDrawing(Drawing drawing);
    
    public Canvas getCanvas();
    public void setCanvas(Canvas canvas);
    
    public void addToDrawing(Figure createdFigure);
    public void addToSelections(Figure source);
    public void addToConnector(Connector connector);
    
    public Image getImage();
    public Image getImage(boolean crop);
    public Image getImage(boolean crop, String imageType);
    
    public Tool getCurrentTool();
    public void setCurrentTool(Tool tool);
    public Tool getDefaultTool();
    
    public boolean isReadonly();
    public void setReadonly(boolean readonly);
    
    public void reset();

    public void deleteSelections();
    public void moveSelections(MoveDirection direction);
    

    public void openFigure(Figure figure);
    
    public void addListener(EditorListener listener);
    public void removeListener(EditorListener listener);
    
    public void figureAdded(Figure createdFigure);
    //public boolean notifyBeforeRemoveListener(List<Figure> figures);
    //public void notifyAfterRemoveListener(List<Figure> deletedItems);
    public void connectionChanged(Connector c, Figure fromFigure, Figure toFigure);

    public List<Invoker> showMenu(Figure figure);

    public void attributeChanged(AttributeKey key, Object value);

    public void setDefaultTool();

    public void loadDrawing(DrawModel handler);
    public void loadDrawing(DrawModel handler, Object drawing);
    
    public UndoRedoManager getUndoRedoManager();
    public UndoAction getUndoAction();
    public RedoAction getRedoAction();

    public void addUndoableEdit(UndoableEdit ce);

    

    

}
