package com.rameses.rcp.draw.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.draw.utils.DrawUtil;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.Color;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.rcp.draw.components.*;

public class AttributePickerModel{
    def editor;
    def attributekey;
    def caption;
    
    void init(){
    }
    
    def handler = [
        getCaption : { caption },
        getEditor : { editor },
        getAttributeKey : { attributekey },
    ] as com.rameses.rcp.draw.components.AttributePickerModel;
}