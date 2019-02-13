package com.rameses.rcp.draw.actions;

import com.rameses.osiris2.client.Inv;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.support.AttributeKey;
import java.util.HashMap;
import java.util.Map;


public class AttributePickerAction extends DrawAction{
    private AttributeKey key;
    
    public AttributePickerAction() { 
    }
    
    public AttributePickerAction(Editor editor, AttributeKey key, String tooltip, String icon){
        super(editor);
        setTooltip(tooltip);
        setIcon(icon);
        this.key = key;
    }
        
    @Override
    public Object execute() { 
        if (getEditor().getDrawing().getSelections().isEmpty()){
            return null;
        }
        
        Map params = new HashMap();
        params.put("editor", getEditor());
        params.put("attributekey", key);
        params.put("caption", getTooltip());
        return Inv.lookupOpener("drawtool:attribute", params);
    }
}
