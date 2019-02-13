package com.rameses.rcp.draw.models;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.draw.utils.DrawUtil;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.Font;
import com.rameses.rcp.draw.interfaces.Figure;

public class FontPickerModel{
    def editor;
    
    void init(){
    }
    
    def handler = [
        getEditor : { editor },
    ] as com.rameses.rcp.draw.components.AttributePickerModel;
}