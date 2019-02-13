package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.text.DefaultPasswordField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 *
 * @author Windhel
 */
public class XPasswordField extends DefaultPasswordField implements UIInput, 
    ActiveControl, Validatable, MouseEventSupport.ComponentInfo 
{    
    protected Binding binding;
    
    private String[] depends = new String[]{};
    private String hint;
    private boolean nullWhenEmpty = true;
    private int index;

    private boolean showHint;
    private boolean isHintShown;
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen;
            
    protected void initDefaults() {
        super.setFont(Font.decode("Monospaced--"));
        
        Insets margin = UIManager.getInsets("TextField.margin");
        if (margin != null) {
            Border borderOut = getBorder();
            Border borderIn = BorderFactory.createEmptyBorder(0, margin.left, 0, 0);
            setBorder(BorderFactory.createCompoundBorder(borderOut, borderIn));
        } 
        
        if (Beans.isDesignTime()) return;
        
        addActionMapping(ACTION_MAPPING_KEY_ESCAPE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try { refresh(); } catch(Throwable t) {;} 
            }
        });
        new MouseEventSupport(this).install(); 
    }    
        
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">
    
    public String getHint() { return hint; }    
    public void setHint(String hint) {
        this.hint = hint;
        showHint = (hint != null && hint.length() > 0); 
    } 
            
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput implementations ">
        
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public boolean isImmediate() { return false; }     
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty; 
    }

    public void setRequestFocus(boolean focus) {
        if (focus) requestFocus(); 
    }    

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o); 
    }

    public Object getValue() { 
        if (Beans.isDesignTime()) return "";
        
        return String.valueOf(getPassword());        
    }

    public void setValue(Object value) {
        super.setText(value == null? "": value.toString()); 
    } 
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public void load() {
        if (showHint) isHintShown = true;
    }

    public void refresh() { 
        try {
            updateBackground(); 
            
            Object value = UIControlUtil.getBeanValue(this);
            setValue(value);
        } 
        catch(Throwable e) {
            //just block the input when the name is null
            setValue(null); 
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace(); 
        }        
        
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
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("focusAccelerator", getFocusAccelerator());
        map.put("nullWhenEmpty", isNullWhenEmpty()); 
        map.put("required", isRequired()); 
        return map;
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

    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementations ">
    
    private ControlProperty property;
    
    public ControlProperty getControlProperty() {
        if (property == null) 
            property = new ControlProperty();
        
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
    
    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }     
    
    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    }    
    public void setShowCaption(boolean showCaption) {
        getControlProperty().setShowCaption(showCaption);
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Validatable implementations ">

    private ActionMessage actionMessage; 
    private boolean required;
        
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public ActionMessage getActionMessage() {
        if (actionMessage == null) { 
            actionMessage = new ActionMessage();
        }
        return actionMessage; 
    }

    public void validateInput() {
        validateInput( getActionMessage() ); 
    }
    public void validateInput( ActionMessage am ) {
        am.clearMessages();
        getControlProperty().setErrorMessage(null);
        char[] chars = getPassword();
        if ((chars == null || chars.length == 0) && isRequired()) {
            am.addMessage("", "{0} is required.", new Object[] {getCaption()});
            getControlProperty().setErrorMessage(am.toString());
        } 
    }
    
    // </editor-fold>
}
