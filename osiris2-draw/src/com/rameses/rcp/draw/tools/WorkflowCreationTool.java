package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.WorkflowEndNode;
import com.rameses.rcp.draw.figures.WorkflowStartNode;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.MouseEvent;

public class WorkflowCreationTool extends AbstractTool {
    private Figure createdFigure;
    private boolean existing;
    
    private boolean dragging;
    
    public WorkflowCreationTool(){
    }
    
    public WorkflowCreationTool(Editor editor){
        this(editor, null);
    }
    
    public WorkflowCreationTool(Editor editor, Class prototype){
        super(editor);
        setPrototype(prototype);
        existing = false;
    }
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        super.mousePressed(x, y, e);
        existing = checkExistingFigure();
        if (!existing){
            createdFigure = createFigure();
            createdFigure.setDisplayBox(x, y, x, y);
            getEditor().addToDrawing(createdFigure);
        }
    }   

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        if (createdFigure != null){
            createdFigure.setDisplayBox(getStartX(), getStartY(), x, y);
            dragging = true;
        }
    }
        
    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        if (createdFigure != null){
            if (createdFigure.isEmpty()){
                getDrawing().removeFigure(createdFigure);
            }else{
                getCanvas().revalidateRect(createdFigure.getDisplayBox());
            }
            getEditor().figureAdded(createdFigure);
            dragging = false;
        }
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


    private boolean checkExistingFigure() {
        boolean existing = false;
        if (getPrototype() == WorkflowStartNode.class){
            existing = checkExistStartNode();
        }else if (getPrototype() == WorkflowEndNode.class){
            existing = checkExistEndNode();
        }
        return existing;
    }
    
    
    private boolean checkExistStartNode() {
        for(Figure f : getDrawing().getFigures()){
            if (f instanceof WorkflowStartNode){
                return true;
            }
        }
        return false;
    }
    
    private boolean checkExistEndNode() {
        for(Figure f : getDrawing().getFigures()){
            if (f instanceof WorkflowEndNode){
                return true;
            }
        }
        return false;
    }
}
