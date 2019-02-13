package com.rameses.rcp.draw.actions;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Tool;


public class FigureAction extends Action{
    private Editor editor;
    private Figure figure;
    
    public FigureAction() { 
    }
    
    public FigureAction(Editor editor, Figure figure){
        this.editor = editor;
        this.figure = figure;
    }
    
    public Editor getEditor(){
        return editor;
    }
    
    public void setEditor(Editor editor){
        this.editor = editor;
    }
    
    public Figure getFigure(){
        return figure;
    }
    
    public void setFigure(Figure figure){
        this.figure = figure;
    }
    
    @Override
    public Object execute() { 
        Tool tool = figure.getTool();
        tool.setPrototype(figure.getClass());
        tool.setEditor(editor);
        editor.setCurrentTool(tool);
        return null;
    }
}
