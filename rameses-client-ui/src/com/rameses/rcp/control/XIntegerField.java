package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.text.AbstractNumberDocument;
import com.rameses.rcp.control.text.AbstractNumberField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;

/**
 *
 * @author wflores
 */
public class XIntegerField extends AbstractNumberField implements UIInput, 
    Validatable, ActiveControl, MouseEventSupport.ComponentInfo 
{   
    protected ControlProperty property = new ControlProperty();
    protected ActionMessage actionMessage = new ActionMessage();
    private Binding binding;    
    
    private IntegerDocument model = new IntegerDocument(); 
    private boolean nullWhenEmpty;
    private String[] depends; 
    private String pattern;  
    private int index;
    
    private int stretchWidth;
    private int stretchHeight;     
    
    private ActionCommandInvoker actionCommandInvoker;
        
    public XIntegerField() {
        super();
        initComponent();
    } 

    private void initComponent() {
        if (Beans.isDesignTime()) return;
        
        actionCommandInvoker = new ActionCommandInvoker();
        addActionMapping(ACTION_MAPPING_KEY_ESCAPE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try { refresh(); } catch(Throwable t) {;} 
            }
        }); 
        new MouseEventSupport(this).install(); 
    }
    
    protected AbstractNumberDocument createDocument() 
    {
        if (model == null) model = new IntegerDocument(); 
        
        return model; 
    } 

    protected void oncancelEditing() { 
        try {
            refresh();
        } catch(Exception ex){;} 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public void setName(String name) 
    {
        super.setName(name);
        
        if (Beans.isDesignTime()) super.setText(name);
    }
    
    public Object getValue() 
    {
        Number num = getModel().getValue(); 
        if (num == null) {
            return (isUsePrimitiveValue()? 0.0: null); 
        }
        else {
            return (isUsePrimitiveValue()? num.intValue(): num); 
        }
    }
    
    public void setValue(Object value) 
    {
        if (value instanceof KeyEvent) 
        {
            KeyEvent e = (KeyEvent) value; 
            char c = e.getKeyChar(); 
            if (Character.isDigit(c) || c == '.' || c == '-') 
                getModel().setValue(c+"");
            else 
                setText("");
        }
        else if (value instanceof Integer) {
            getModel().setValue((Integer) value);
        }
        else {
            getModel().setValue((value == null? "": value.toString()));
        } 
        
        revalidate();
        repaint(); 
    }
    
    public String getPattern() { return model.getFormat(); }     
    public void setPattern(String pattern) { model.setFormat(pattern); } 
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (info == null) return;
        
        PropertyInfoWrapper pi = new PropertyInfoWrapper(info);
        setPattern(pi.getFormat()); 
        setMinValue(pi.getMinValue());
        setMaxValue(pi.getMaxValue()); 
    }    
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  IntegerDocument (Class)  "> 
    
    private class IntegerDocument extends AbstractNumberDocument
    {
        IntegerDocument() 
        {
            super();
            setFormat(null); 
        }
        
        public Number decode(String value) 
        {
            try { 
                return new Integer(value); 
            } catch(Exception ex) {
                return null; 
            } 
        } 

        public Number convertValue(Number value) { 
            return value; 
        } 
        
        protected Number getPrimitiveValue(Number value) {
            return value; 
        } 
        
        public void refresh() 
        {
            revalidate();
            repaint(); 
        } 
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="  UIInput implementations  ">     
    
    public boolean isShowCaption() { return property.isShowCaption(); }    
    public void setShowCaption(boolean showCaption) {
        property.setShowCaption(showCaption);
    }
    
    public Font getCaptionFont() { return property.getCaptionFont(); }    
    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() { return property.getCellPadding(); }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }
    
    public char getCaptionMnemonic() { return property.getCaptionMnemonic(); }    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() { return property.getCaptionWidth(); }    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
    }    
        
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty; 
    }

    public void setRequestFocus(boolean focus) {
        if ( focus ) requestFocus();
    }

    public boolean isImmediate() { return false; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }

    public void refresh() {
        try {
            updateBackground();
            
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
            
            Object value = UIControlUtil.getBeanValue(this);
            
            Number number = null;
            if (value == null) {
                //do nothing 
            }
            else if (value instanceof Integer) { 
                number = (Integer) value; 
            } 
            else 
            {
                try {
                     number = getModel().decode(value.toString()); 
                } catch(Exception e) {;} 
            }

            getModel().setValue(number); 
            getModel().refresh();
        } 
        catch(Exception e) 
        {
            setText("");
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace();
        }
    }

    protected InputVerifier getChildInputVerifier() {
        return UIInputUtil.VERIFIER; 
    }
    
    public void load() {
        String cmd = getActionCommand();
        if (cmd != null && cmd.length() > 0) {
            removeActionMapping(ACTION_MAPPING_KEY_ENTER, actionCommandInvoker); 
            addActionMapping(ACTION_MAPPING_KEY_ENTER, actionCommandInvoker); 
        }        
    } 

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }   
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("actionCommand", getActionCommand());
        map.put("focusAccelerator", getFocusAccelerator()); 
        map.put("minValue", getMinValue());
        map.put("maxValue", getMaxValue());
        map.put("pattern", getPattern());
        map.put("usePrimitiveValue", isUsePrimitiveValue()); 
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

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Validatable implementations  ">
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { property.setCaption(caption); }

    public boolean isRequired() { return property.isRequired(); }
    public void setRequired(boolean required) { property.setRequired(required); }

    public void validateInput() { 
        validateInput( actionMessage ); 
    }
    public void validateInput( ActionMessage am ) { 
        am.clearMessages();
        property.setErrorMessage(null);
        if ( ValueUtil.isEmpty(getText()) ) {
            if (isRequired()) { 
                am.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
            }
        } 
        if ( am.hasMessages() ) {
            property.setErrorMessage( am.toString() );
        }
    }

    public ActionMessage getActionMessage() { return actionMessage; }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="  ActiveControl implementations  ">
    
    public ControlProperty getControlProperty() { return property; }
    
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc=" PropertyInfoWrapper (Class)  "> 
    
    private class PropertyInfoWrapper 
    {
        private PropertySupport.IntegerPropertyInfo property;
        private Map map = new HashMap(); 
        
        PropertyInfoWrapper(PropertySupport.PropertyInfo info) 
        {
            if (info instanceof Map) map = (Map)info;
            if (info instanceof PropertySupport.IntegerPropertyInfo)
                property = (PropertySupport.IntegerPropertyInfo) info;
        }
        
        public String getFormat() 
        {
            Object value = map.get("format");
            if (value == null && property != null)
                value = property.getFormat();
            
            return (value == null? null: value.toString());
        }
        
        public int getMinValue() 
        {
            Object value = map.get("minValue");
            if (value == null && property != null)
                value = property.getMinValue();
            
            Number num = convertNumber(value);
            return (num == null? -1: num.intValue());
        }
        
        public int getMaxValue() 
        {
            Object value = map.get("maxValue");
            if (value == null && property != null)
                value = property.getMaxValue();
            
            Number num = convertNumber(value);
            return (num == null? -1: num.intValue());            
        }    
        
        private Number convertNumber(Object value) 
        {
            if (value instanceof Number)
                return (Number) value;
            
            try {
                return Integer.parseInt(value.toString()); 
            } catch(Exception ex) {
                return null; 
            }
        }
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" ActionCommandInvoker ">    
    
    private class ActionCommandInvoker implements ActionListener 
    {
        XIntegerField root = XIntegerField.this; 
        
        public void actionPerformed(ActionEvent e) { 
            try {
                String cmd = root.getActionCommand(); 
                if (cmd == null || cmd.length() == 0) return; 
                
                UIInputUtil.updateBeanValue(root);                 
                Object bean = root.getBinding().getBean();
                Object outcome = MethodResolver.getInstance().invoke(bean, cmd, new Object[]{}); 
                if (outcome instanceof Opener) { 
                    root.getBinding().fireNavigation(outcome); 
                } 
            } catch(Throwable t) { 
                MsgBox.err(t); 
            } 
        } 
        
    }
    
    // </editor-fold>    
}
