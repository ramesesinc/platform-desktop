/*
 * XTextPane.java
 *
 * Created on May 5, 2014, 6:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextPane;

/**
 *
 * @author wflores 
 */
public class XTextPane extends JTextPane 
    implements UIInput, ActiveControl, MouseEventSupport.ComponentInfo 
{
    private Binding binding;
    private String[] depends;
    private int index;
    
    private boolean nullWhenEmpty;
    private boolean readonly;
    
    private ControlProperty property; 
    private String fontStyle;
    private Font sourceFont;
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen; 
    
    public XTextPane() {
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent ">     
    
    private void initComponent() {
        setEditable(false); 
        setNullWhenEmpty(true); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation "> 

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { 
        this.binding = binding; 
    }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex(int index) {
        this.index = index; 
    }

    public void load() {
    }

    public void refresh() {
        try {
            setValue(UIControlUtil.getBeanValue(getBinding(), getName())); 
        } catch(Throwable t){;} 
        
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }        
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput implementation "> 
    
    public Object getValue() { 
        String text = getText(); 
        if (text == null) {
            return null;
        } else if (text.length() == 0 && isNullWhenEmpty()) {
            return null; 
        } else { 
            return text; 
        } 
    }
    public void setValue(Object value) {
        setText(value == null? "": value.toString());
    }

    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty; 
    }

    public boolean isReadonly() { return readonly; }    
    public void setReadonly(boolean readonly) {
        if (!isEnabled()) return;

        this.readonly = readonly;
        setEditable(!readonly);
        super.firePropertyChange("editable", readonly, !readonly);        
    }

    public boolean isImmediate() { return false; }
    
    public void setRequestFocus(boolean focus) {
        if (focus) requestFocus(); 
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    public ControlProperty getControlProperty() {
        if (property == null) {
            property = new ControlProperty();
        }
        return property; 
    }
    
    public String getCaption() {
        return getControlProperty().getCaption();
    }    
    public void setCaption(String caption) {
        getControlProperty().setCaption(caption);
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }
    
    public boolean isRequired() {
        return getControlProperty().isRequired();
    }    
    public void setRequired(boolean required) {
        getControlProperty().setRequired(required);
    }
    
    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    }    
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    } 
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }     
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } else {
            super.setFont(sourceFont); 
        } 
        new FontSupport().applyStyles(this, fontStyle);
    }      
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation "> 
    
    public Map getInfo() {
        Map map = new HashMap();
        map.put("focusAccelerator", getFocusAccelerator());
        map.put("nullWhenEmpty", isNullWhenEmpty()); 
        map.put("required", isRequired());        
        return map;        
    }
    
    // </editor-fold>
}
