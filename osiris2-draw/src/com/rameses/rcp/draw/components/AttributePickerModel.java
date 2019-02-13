package com.rameses.rcp.draw.components;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.support.AttributeKey;

public class AttributePickerModel {
    private Editor editor;
    private AttributeKey key;
    
    public AttributePickerModel(){
        
    }
    
    public AttributePickerModel(Editor editor, AttributeKey key){
        this.editor = editor;
        this.key = key;
    }
    
    public Editor getEditor(){
        return editor;
    }
    
    public AttributeKey getAttributeKey(){
        return key;
    }
    
    public String getCaption(){
        return null;
    }
    
}
