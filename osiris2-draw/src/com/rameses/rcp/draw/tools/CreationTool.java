package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class CreationTool extends AbstractTool {
    private Figure createdFigure;
    
    private boolean dragging;
    
    public CreationTool(){
    }
    
    public CreationTool(Editor editor){
        this(editor, null);
    }
    
    public CreationTool(Editor editor, Class prototype){
        super(editor);
        setPrototype(prototype);
    }
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        super.mousePressed(x, y, e);
        createdFigure = createFigure();
        createdFigure.setDisplayBox(x, y, x, y);
        getEditor().addToDrawing(createdFigure);
    }   

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        createdFigure.setDisplayBox(getStartX(), getStartY(), x, y);
        dragging = true;
    }
        
    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        if (createdFigure.isEmpty()){
            getDrawing().removeFigure(createdFigure);
        }else{
            getCanvas().revalidateRect(createdFigure.getDisplayBox());
        }
        getEditor().figureAdded(createdFigure);
        dragging = false;
        
    }
    
    private Figure createFigure(){
        try{
            return (Figure)getPrototype().newInstance();
        }
        catch(Throwable e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void cancel() {
        getDrawing().removeFigure(createdFigure);
        createdFigure = null;
        getEditor().setCurrentTool(getEditor().getDefaultTool());
        getCanvas().refresh();
    }
}
