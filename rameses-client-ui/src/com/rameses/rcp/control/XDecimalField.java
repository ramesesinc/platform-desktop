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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.InputVerifier;

/**
 *
 * @author wflores
 */
public class XDecimalField extends AbstractNumberField implements UIInput, Validatable, ActiveControl, MouseEventSupport.ComponentInfo 
{   
    protected ControlProperty property = new ControlProperty();
    protected ActionMessage actionMessage = new ActionMessage();
    private Binding binding;    
    
    private DecimalDocument model = new DecimalDocument(); 
    private Meta meta = new Meta();
    private boolean nullWhenEmpty;
    private String[] depends; 
    private int index;

    private DecimalFormat formatter; 
    private String pattern;      
    private int scale = 2;
    
    private int stretchWidth;
    private int stretchHeight;
    
    private ActionCommandInvoker actionCommandInvoker; 
        
    public XDecimalField() {
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
        if (model == null) {
            model = new DecimalDocument();
            pattern = "#,##0.00";
        }         
        return model; 
    } 

    protected void oncancelEditing() { 
        try {
            refresh();
        } catch(Exception ex){;} 
    }
        
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public void setName(String name) {
        super.setName(name);
        
        if (Beans.isDesignTime()) super.setText(name);
    }
    
    public Object getValue() {
        Number num = getModel().getValue(); 
        if (num == null) {
            return (isUsePrimitiveValue()? 0.0: null); 
        } else {
            return (isUsePrimitiveValue()? num.doubleValue(): num); 
        }
    }
    
    public void setValue(Object value) {
        if (value instanceof KeyEvent) {
            KeyEvent e = (KeyEvent) value; 
            char c = e.getKeyChar(); 
            if (Character.isDigit(c) || c == '.' || c == '-') 
                getModel().setValue(c+"");
            else 
                setText("");
        }
        else if (value instanceof BigDecimal) {
            getModel().setValue((BigDecimal) value);
        }
        else {
            getModel().setValue((value == null? "": value.toString()));
        } 
        
        revalidate();
        repaint(); 
    }
    
    public String getPattern() { return pattern; }     
    public void setPattern(String pattern) { 
        this.pattern = pattern; 
        this.formatter = null;
    } 
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (info == null) return;
        
        PropertyInfoWrapper pi = new PropertyInfoWrapper(info);
        setPattern(pi.getFormat()); 
        setMinValue(pi.getMinValue()); 
        setMaxValue(pi.getMaxValue()); 
        setUsePrimitiveValue(pi.isUsePrimitiveValue()); 
        setScale(pi.getScale());         
    } 
    
    public int getScale() { return scale; } 
    public void setScale(int scale) { this.scale = scale; }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  DecimalDocument (Class)  "> 
    
    private class DecimalDocument extends AbstractNumberDocument {
        XDecimalField root = XDecimalField.this; 
        
        public Number decode(String value) {
            try { 
                return new BigDecimal(value); 
            } catch(Throwable t) {
                return null; 
            } 
        } 

        public Number convertValue(Number value) { 
            BigDecimal num = null; 
            if (value instanceof BigDecimal) { 
                num = (BigDecimal) value; 
            } else { 
                num = new BigDecimal(value.doubleValue());  
            } 

            int scale = root.getScale();
            if ( scale < 1 ) return num;
            
            StringBuilder sb = new StringBuilder("0."); 
            for (int i=0; i<scale; i++) { 
                sb.append("0"); 
            } 
            
            String sval = new DecimalFormat(sb.toString()).format( num ); 
            return new BigDecimal( sval ); 
        } 
        
        protected Number getPrimitiveValue(Number value) {
            return value; 
        } 
        
        protected String formatValue(Number value) {
            if (value == null) return ""; 
            
            return getFormatter().format(value); 
        }
        
        public void refresh() {
            revalidate();
            repaint(); 
        } 
        
        private DecimalFormat getFormatter() {
            if (root.formatter == null) {
                String pattern = root.getPattern();
                if (pattern == null || pattern.length() == 0) pattern = "#,##0.00"; 
                
                root.formatter = new DecimalFormat(pattern);
            }
            return root.formatter;
        }
    }
    
    private class Meta {
        
        Number source; 
        
        public Number getSource() { return source; } 
        void setSource( Number source ) { 
            this.source = source; 
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
            } else if (value instanceof BigDecimal) { 
                number = (BigDecimal) value; 
            } else {
                try {
                     number = getModel().decode(value.toString()); 
                } catch(Throwable t) {;} 
            }

            meta.setSource( number ); 
            getModel().setValue( meta.getSource() ); 
            getModel().showFormattedText(true); 
            getModel().refresh(); 
            
        } catch(Exception e) {
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
        map.put("scale", getScale());
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
        XDecimalField root = XDecimalField.this;
        
        private PropertySupport.DecimalPropertyInfo property;
        private Map map = new HashMap(); 
        
        PropertyInfoWrapper(PropertySupport.PropertyInfo info) 
        {
            if (info instanceof Map) map = (Map)info;
            if (info instanceof PropertySupport.DecimalPropertyInfo)
                property = (PropertySupport.DecimalPropertyInfo) info;
        }
        
        public String getFormat() 
        {
            Object value = map.get("format");
            if (value == null && property != null)
                value = property.getFormat();
            
            return (value == null? null: value.toString());
        }
        
        public double getMinValue() 
        {
            Object value = map.get("minValue");
            if (value == null && property != null)
                value = property.getMinValue();
            
            Number num = convertNumber(value);
            return (num == null? -1.0: num.doubleValue());
        }
        
        public double getMaxValue() 
        {
            Object value = map.get("maxValue");
            if (value == null && property != null)
                value = property.getMaxValue();
            
            Number num = convertNumber(value);
            return (num == null? -1.0: num.doubleValue());            
        }    
        
        public boolean isUsePrimitiveValue() 
        {
            Object value = map.get("usePrimitiveValue");
            if (value == null && property != null)
                value = property.isUsePrimitiveValue(); 
            
            Boolean bool = convertBoolean(value);
            return (bool == null? false: bool.booleanValue()); 
        }     
        
        public int getScale() {
            Object value = map.get("scale");
            if (value == null && property != null) 
                value = property.getScale(); 
            if (value == null) 
                return root.getScale(); 
            
            try {                
                return Integer.parseInt(value.toString()); 
            } catch(Throwable t) {
                return root.getScale();
            }
        }   
        
        private Number convertNumber(Object value) 
        {
            if (value instanceof Number)
                return (Number) value;
            
            try {
                return Double.parseDouble(value.toString()); 
            } catch(Exception ex) {
                return null; 
            }
        }
        
        private Boolean convertBoolean(Object value) 
        {
            if (value instanceof Boolean)
                return (Boolean) value;
            
            try {
                return Boolean.parseBoolean(value.toString()); 
            } catch(Exception ex) {
                return null; 
            }
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ActionCommandInvoker ">    

    private class ActionCommandInvoker implements ActionListener 
    {
        XDecimalField root = XDecimalField.this; 

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
