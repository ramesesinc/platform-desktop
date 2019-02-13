package com.rameses.rcp.draw.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.draw.components.AttributePickerModel;

public class ColorChooserModel{
    def model; 
    
    void init(){
        
    }
    
    def handler = [
        getEditor : {model.editor},
        getAttributeKey : { model.attributeKey }
    ] as com.rameses.rcp.draw.components.AttributePickerModel;
}