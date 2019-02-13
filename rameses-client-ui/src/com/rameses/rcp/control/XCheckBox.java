package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Beans;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author jaycverg
 */
public class XCheckBox extends JCheckBox implements UIInput, ActiveControl, MouseEventSupport.ComponentInfo 
{
    private ItemListenerImpl itemHandler = new ItemListenerImpl(); 
    private ControlProperty property = new ControlProperty();   
    private Binding binding;
    private String[] depends;
    private String fontStyle;
    private boolean readonly;
    private boolean inited;
    private int index;

    private Font sourceFont;
    private Class valueType = Boolean.class; 
    private Object uncheckValue = false;    
    private Object checkValue = true;

    private int stretchWidth;
    private int stretchHeight;
    
    private String disableWhen;
    private String visibleWhen;
    
    public XCheckBox() {
        new MouseEventSupport(this).install();         
        setOpaque( false ); 
    }
    
    public void refresh() {
        try {
            //disable the item handler to prevent cyclic updating of values
            itemHandler.enabled = false; 
            
            String whenExpr = getVisibleWhen();
            if (whenExpr != null && whenExpr.length() > 0) {
                boolean result = false; 
                try { 
                    result = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                setVisible( result ); 
            }

            whenExpr = getDisableWhen();
            if (whenExpr != null && whenExpr.length() > 0) {
                boolean disabled = false; 
                try { 
                    disabled = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                setEnabled( !disabled ); 
            } 
            
            if (isEnabled()) setReadonly( isReadonly() );
            
            //check if this component is owned by the JTable
            if ("true".equals(getClientProperty(JTable.class)+"")) 
            {
                setHorizontalAlignment(SwingConstants.CENTER); 
                setBorderPainted(true); 
            }
            
            resolveValues();
            
            Object value = UIControlUtil.getBeanValue(this);
            boolean selected = resolveValue(value); 
            setSelected(selected);
        } 
        catch(Exception e) 
        {
            setSelected(false);
            
            if (ClientContext.getCurrentContext().isDebugMode())
                e.printStackTrace();
        } 
        finally 
        {
            //enable the item handler
            itemHandler.enabled = true;   
        }
    }
    
    public void load() 
    {
        resolveValues();   
        removeItemListener(itemHandler); 
        
        itemHandler.enabled = true;        
        addItemListener(itemHandler);
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    private void resolveValues()
    {
        if (checkValue == null || uncheckValue == null) 
        {
            checkValue = true;
            uncheckValue = false; 
        } 
    }
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("mnemonic", (char) getMnemonic()); 
        map.put("checkValue", getCheckValue());
        map.put("uncheckValue", getUncheckValue()); 
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
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    private boolean resolveValue(Object value) 
    {
        boolean selected = false; 
        if (value == null) { /* do nothing */ }
        else if (value != null && checkValue != null && checkValue.equals(value)) selected = true; 
        else if ((checkValue+"").equals(value+"")) selected = true; 
        else if ("true".equals(value+"")) selected = true; 
        else if ("yes".equals(value+"")) selected = true; 
        else if ("t".equals(value+"")) selected = true; 
        else if ("y".equals(value+"")) selected = true;             
        else if ("1".equals(value+"")) selected = true; 
        
        return selected;
    }
    
    public Object getValue() {
        return isSelected()? getCheckValue() : getUncheckValue();
    } 
    
    public void setValue(Object value) 
    {
        if (value == null) { 
            setSelected(false); 
        } 
        else if ( value instanceof EventObject ) 
        {
            refresh();
            setSelected(!isSelected());
        } 
        else 
        {
            boolean selected = false; 
            if (value != null && checkValue != null && checkValue.equals(value)) selected = true; 
            else if ((checkValue+"").equals(value+"")) selected = true; 
            else if ("true".equals(value+"")) selected = true; 
            else if ("1".equals(value+"")) selected = true; 
            
            UIInputUtil.updateBeanValue(this, false, false);
            setSelected(selected);
        }
    }
    
    public String getCaption() {
        return property.getCaption();
    }    
    public void setCaption(String caption) {
        property.setCaption(caption);
    }
    
    public char getCaptionMnemonic() {
        return property.getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
    }
    
    public boolean isShowCaption() {
        return property.isShowCaption();
    }    
    public void setShowCaption(boolean showCaption) {
        property.setShowCaption(showCaption);
    }
    
    public Font getCaptionFont() {
        return property.getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
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
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }
    
    public boolean isNullWhenEmpty() { return false; }
    
    public String[] getDepends() { return this.depends; }    
    public void setDepends(String[] depends) {
        this.depends = depends;
    }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    
    public ControlProperty getControlProperty() { return property; }
    
    public Object getCheckValue() { return checkValue; }    
    public void setCheckValue(Object checkValue) {
        if ( !Beans.isDesignTime() && isExpression(checkValue) ) {
            checkValue = UIControlUtil.evaluateExpr(binding.getBean(), checkValue+"");
        }
        this.checkValue = checkValue;
    }
    
    public Object getUncheckValue() { return uncheckValue; }    
    public void setUncheckValue(Object uncheckValue) 
    {
        if ( !Beans.isDesignTime() && isExpression(uncheckValue) ) {
            uncheckValue = UIControlUtil.evaluateExpr(binding.getBean(), uncheckValue+"");
        }
        this.uncheckValue = uncheckValue;
    }
    
    private boolean isExpression(Object exp) 
    {
        if ( exp == null || !(exp instanceof String) ) return false;
        
        String expr = exp.toString();
        if (expr.matches(".*#\\{[^\\{\\}]+\\}.*")) return true; 
        else if (expr.matches(".*\\$\\{[^\\{\\}]+\\}.*")) return true; 
        else return false; 
    }

    public boolean isReadonly() { return readonly; }    
    public void setReadonly(boolean readonly) 
    {
        this.readonly = readonly;
        super.setEnabled(!readonly);
        super.firePropertyChange("enabled", readonly, !readonly); 
        repaint();
    }
    
    public void setRequestFocus(boolean focus) {
        if ( focus ) requestFocus();
    }
    
    public boolean isImmediate() { return true; }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (info == null) return;
        
        PropertyInfoWrapper pi = new PropertyInfoWrapper(info); 
        this.valueType = pi.getValueType();
        setCheckValue(pi.getCheckValue());
        setUncheckValue(pi.getUncheckValue()); 
    } 

    public void setMnemonic(char mnemonic) {
        super.setMnemonic(mnemonic);
        resolveDisplayMnemonic();
    } 
    
    public void setText(String text) {
        super.setText(text); 
        resolveDisplayMnemonic();
    }
    
    private void resolveDisplayMnemonic() {
        char mnemonic = (char) getMnemonic();
        if (mnemonic == '\u0000') return;
        
        String text = getText();
        if (text == null) return;
                
        String stext = text.toLowerCase();
        if (!stext.trim().matches("<html>.*</html>")) return;
        
        char cval = Character.toLowerCase(mnemonic);        
        Pattern p = Pattern.compile("<.*?>");
        Matcher m = p.matcher(text); 
        int startindex = 0;
        int locIndex = -1;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (start > startindex) {
                char[] chars = stext.substring(startindex, start).toCharArray();
                for (int i=0; i<chars.length; i++) {
                    if (chars[i] == cval) {
                        locIndex = startindex+i; 
                        break; 
                    } 
                } 
            } 

            if (locIndex >= 0) break; 

            startindex = end;
        } 

        if (locIndex < 0) return;

        StringBuffer sb = new StringBuffer(); 
        sb.append(text.substring(0, locIndex));
        sb.append("<u>" + text.charAt(locIndex) + "</u>"); 
        sb.append(text.substring(locIndex+1));
        super.setText(sb.toString());          
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    
    public String getDisableWhen() { return disableWhen; } 
    public void setDisableWhen( String disableWhen ) {
        this.disableWhen = disableWhen;
    }     
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" ItemListenerImpl (Class) ">
    
    private class ItemListenerImpl implements ItemListener
    {
        boolean enabled = true;
        private boolean processing;
        
        public void itemStateChanged(ItemEvent e) 
        {
            //if component is currently not enabled, exit right away
            if (!isEnabled()) return;
            //check internal flags if passed
            if (!enabled) return;
            if (processing) return;
            
            try 
            {
                processing = true;
                UIInputUtil.updateBeanValue(XCheckBox.this); 
            } 
            catch(Exception ex) {;} 
            finally {  
                processing = false; 
            }
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyInfoWrapper (Class)  "> 
    
    private class PropertyInfoWrapper 
    {
        private PropertySupport.CheckBoxPropertyInfo property;
        private Map map = new HashMap(); 
        
        PropertyInfoWrapper(PropertySupport.PropertyInfo info) 
        {
            if (info instanceof Map) map = (Map) info;
            if (info instanceof PropertySupport.CheckBoxPropertyInfo)
                property = (PropertySupport.CheckBoxPropertyInfo) info;
        }
        
        public Class getValueType() 
        {
            Object value = map.get("valueType");
            if (value == null && property != null)
                value = property.getValueType();
            
            return (property == null? Boolean.class: (Class) value);
        }
        
        public Object getCheckValue() 
        {
            Object value = map.get("checkValue");
            if (value == null && property != null)
                value = property.getCheckValue();
            
            return value;
        }   
        
        public Object getUncheckValue() 
        {
            Object value = map.get("uncheckValue");
            if (value == null && property != null)
                value = property.getUncheckValue();
            
            return value; 
        }        
    }
    
    // </editor-fold>    
}
