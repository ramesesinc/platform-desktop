package com.rameses.rcp.draw.actions;

import com.rameses.rcp.draw.commands.CenterTextCommand;
import com.rameses.rcp.draw.commands.Command;
import com.rameses.rcp.draw.interfaces.Editor;


public class CenterCaptionAction extends DrawAction{
    public CenterCaptionAction() { 
    }
    
    public CenterCaptionAction(Editor editor){
        super(editor);
        setTooltip("Center Caption");
        setIcon("images/draw/center16.png");
    }
        
    @Override
    public Object execute() { 
        if (getEditor().getDrawing().getFigures().isEmpty()){
            return null;
        }
        
        Command cmd = new CenterTextCommand(getEditor().getCanvas());
        cmd.actionPerformed(null);
        return null;
    }
}
