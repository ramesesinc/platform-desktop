package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.support.AttributeKey;
import com.rameses.rcp.draw.support.AttributeKeys;
import com.rameses.rcp.draw.FigureEvent;
import com.rameses.rcp.draw.utils.DataUtil;
import java.util.*;

public abstract class AbstractAttributedFigure extends AbstractFigure {
    private HashMap<AttributeKey, Object> attributes = new HashMap<AttributeKey,Object>();
    
    public AbstractAttributedFigure() {
    }
    
    @Override
    public Map<AttributeKey, Object> getAttributes() {
        return new HashMap<AttributeKey,Object>(attributes);
    }
        
    @Override
    public void setAttributes(Map<AttributeKey, Object> map) {
        for (Map.Entry<AttributeKey, Object> entry : map.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public <T> void set(AttributeKey<T> key, T newValue) {
        T oldValue = get(key);
        key.put(attributes, newValue);
        fireAttributedChanged(key, oldValue, newValue);
    }
    
    @Override
    public <T> T get(AttributeKey<T> key) {
        return key.get(attributes);
    }

    @Override
    public void readAttributes(Map prop){
        super.readAttributes(prop);
        Map ui = (Map)prop.get("ui");
        Iterator iter = ui.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
            AttributeKey key = AttributeKeys.findByKey(entry.getKey());
            if (key != null){
                set(key, DataUtil.decode(entry.getValue(), key.getAttributeClass()));
            }
        }
    }
    
    @Override
    public Map toMap() {
        Map map = (Map)super.toMap();
        Map ui = (Map)map.get("ui");
        
        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<AttributeKey, Object> entry = (Map.Entry<AttributeKey, Object>)iter.next();
            ui.put(entry.getKey().getKey(), DataUtil.encode(entry.getValue()));
        }
        
        return map;
    }     
}
