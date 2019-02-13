package com.rameses.rcp.draw.components;

import com.rameses.osiris2.client.Inv;
import com.rameses.rcp.common.ComponentBean;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public class AttributePickerComponentModel extends ComponentBean {
    private AttributePickerModel model; 
    
    public void setModel(AttributePickerModel model){
        this.model = model;
    }
    
    public AttributePickerModel getModel() {
        return model;
    }

    public Object openColorChooser(){
        Map params = new HashMap();
        params.put("model", model);
        return Inv.lookupOpener("colorchooser",  params);
    }
}
