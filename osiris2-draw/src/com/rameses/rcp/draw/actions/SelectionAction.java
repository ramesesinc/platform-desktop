package com.rameses.rcp.draw.actions;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.tools.SelectionTool;


public class SelectionAction extends DrawAction{
    public SelectionAction() { 
    }
    
    public SelectionAction(Editor editor){
        super(editor);
        setTooltip("Select");
        setIcon("images/draw/select16.png");
    }
            
    @Override
    public Object execute() { 
        Tool tool = new SelectionTool(getEditor());
        getEditor().setCurrentTool(tool);
        return null;
    }
}
