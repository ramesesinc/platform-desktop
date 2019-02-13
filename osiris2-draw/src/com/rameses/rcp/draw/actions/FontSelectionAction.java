package com.rameses.rcp.draw.actions;

import com.rameses.osiris2.client.Inv;
import com.rameses.rcp.draw.interfaces.Editor;
import java.util.HashMap;
import java.util.Map;


public class FontSelectionAction extends DrawAction{
    public FontSelectionAction() { 
    }
    
    public FontSelectionAction(Editor editor){
        super(editor);
        setTooltip("Font");
        setIcon("images/draw/font16.png");
    }
        
    @Override
    public Object execute() { 
        if (getEditor().getDrawing().getSelections().isEmpty()){
            return null;
        }
        
        Map params = new HashMap();
        params.put("editor", getEditor());
        return Inv.lookupOpener("drawtool:font", params);
    }
}
