package com.rameses.rcp.draw;

import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.support.AttributeKey;
import java.util.EventObject;

public class FigureEvent extends EventObject {
    private AttributeKey key;
    private String propertyName;
    private Class type;
    private Object oldValue;
    private Object newValue;

    public FigureEvent(Figure source, AttributeKey key, Object oldValue, Object newValue) {
        super(source);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public FigureEvent(Figure source, String propertyName, Class type, Object oldValue, Object newValue) {
        super(source);
        this.propertyName = propertyName;
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public Figure getFigure(){
        return (Figure)getSource();
    }
    
    public AttributeKey getAttributeKey(){
        return key;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class getType() {
        return type;
    }
    
    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
    
}
