package com.rameses.rcp.draw.actions;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;


public class ShowIndexAction extends DrawAction{
    public ShowIndexAction() { 
    }
    
    public ShowIndexAction(Editor editor){
        super(editor);
        setTooltip("Show Index");
        setIcon("images/draw/showindex16.png");
    }
        
    @Override
    public Object execute() { 
        if (getEditor().getDrawing().getFigures().isEmpty()){
            return null;
        }
        
        for (Figure f : getEditor().getDrawing().getFigures()){
            f.toggleShowIndex();
        }
        getEditor().getCanvas().refresh();
        return null;
    }
}
