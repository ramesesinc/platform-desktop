package com.rameses.rcp.draw.actions;

import com.rameses.rcp.draw.commands.ReindexCommand;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;


public class ReIndexAction extends DrawAction{
    public ReIndexAction() { 
    }
    
    public ReIndexAction(Editor editor){
        super(editor);
        setTooltip("Reindex");
        setIcon("images/draw/reindex16.png");
    }
        
    @Override
    public Object execute() { 
        if (getEditor().getDrawing().getFigures().isEmpty()){
            return null;
        }
        
        ReindexCommand cmd = new ReindexCommand(getEditor().getCanvas());
        cmd.actionPerformed(null);
        return null;
    }
}
