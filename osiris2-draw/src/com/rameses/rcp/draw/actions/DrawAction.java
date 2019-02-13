
package com.rameses.rcp.draw.actions;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.draw.interfaces.Editor;

public abstract class DrawAction extends Action{
    private Editor editor;
    
    public DrawAction() { 
    }
    
    public DrawAction(Editor editor){
        this.editor = editor;
    }
    
    public Editor getEditor(){
        return editor;
    }
    
    public void setEditor(Editor editor){
        this.editor = editor;
    }
        
    @Override
    public abstract Object execute();
}
