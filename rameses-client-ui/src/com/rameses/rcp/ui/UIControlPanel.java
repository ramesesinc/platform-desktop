/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import com.rameses.rcp.common.PropertySupport.PropertyInfo;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.util.UIControlUtil;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public abstract class UIControlPanel extends UIActiveControlPanel implements UIControl {

    private Binding binding;
    private String[] depends;
    private int index; 
    
    @Override
    protected void initComponent() {
    }

    @Override
    public Binding getBinding() {
        return binding;
    }
    @Override
    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    @Override
    public String[] getDepends() {
        return depends;
    }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }
    
    @Override
    public int getIndex() {
        return index; 
    }
    public void setIndex( int index ) {
        this.index = index; 
    } 

    @Override
    public abstract void load();

    @Override
    public abstract void refresh();

    @Override
    public void setPropertyInfo(PropertyInfo info) {
    }

    @Override
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }

    @Override
    protected void loadComponentInfo(Map info) {
        super.loadComponentInfo(info);
        
        info.put("index", getIndex()); 
        info.put("depends", join(getDepends()));
        info.put("disableWhen", getDisableWhen());
        info.put("visibleWhen", getVisibleWhen());
        info.put("stretchWidth", getStretchWidth());
        info.put("stretchHeight", getStretchHeight());
        info.put("fontStyle", getFontStyle());
    }
    
    private String join(String[] arr) {
        if ( arr == null || arr.length == 0 ) {
            return null; 
        }
        
        StringBuilder sb = new StringBuilder(); 
        for (int i=0; i<arr.length; i++) {
            if ( i > 0 ) sb.append(", ");
            sb.append( arr[i]); 
        }
        return sb.toString(); 
    }
    
    public final Object getBindingBean() {
        Binding binding = getBinding(); 
        return ( binding == null ? null : binding.getBean()); 
    }
}
