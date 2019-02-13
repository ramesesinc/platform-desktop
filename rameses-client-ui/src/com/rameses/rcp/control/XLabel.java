package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.DefaultLabel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UIOutput;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author jaycverg
 */
public class XLabel extends DefaultLabel 
    implements UIOutput, ActiveControl, MouseEventSupport.ComponentInfo {

    private ControlProperty property = new ControlProperty();
    private Binding binding;
    private String[] depends;
    private String expression;
    private String visibleWhen;
    private String varName;
    private int index;
    private boolean useHtml;
    private boolean hideOnEmpty;
    private Insets padding;
    private Format format;
    /**
     * ActiveControl support fields/properties this is used when this UIControl
     * is used as a label for an ActiveControl
     */
    private String labelFor;
    private boolean addCaptionColon = true;
    private boolean forceUseActiveCaption;
    private boolean antiAliasOn;
    private ControlProperty activeProperty;
    private JComponent activeComponent;
    private ActiveControlSupport activeControlSupport;
    private Logger logger;
    private int stretchWidth;
    private int stretchHeight;
    
    private String dateFormat;
    private String numberFormat;

    public XLabel() {
        this(false);
    }

    public XLabel(boolean forceUseActiveCaption) {
        super();
        this.forceUseActiveCaption = forceUseActiveCaption;

        setPadding(new Insets(1, 3, 1, 1));
        new MouseEventSupport(this).install();
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    private Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(getClass().getName());
        }

        return logger;
    }

    public String getDateFormat() { return dateFormat; } 
    public void setDateFormat( String dateFormat ) {
        this.dateFormat = dateFormat;
    }
    
    public String getNumberFormat() { return numberFormat; } 
    public void setNumberFormat( String numberFormat ) {
        this.numberFormat = numberFormat; 
    }
    
    public boolean isHideOnEmpty() {
        return hideOnEmpty;
    }
    public void setHideOnEmpty(boolean hideOnEmpty) {
        this.hideOnEmpty = hideOnEmpty;
    }

    public boolean isUseHtml() {
        return useHtml;
    }
    public void setUseHtml(boolean useHtml) {
        this.useHtml = useHtml;

        if (Beans.isDesignTime()) {
            showDesignTimeValue();
        }
    }

    public String getVarName() {
        return varName;
    }
    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
        if (Beans.isDesignTime()) {
            showDesignTimeValue();
        }
    }

    public void setText(String text) {
        if (Beans.isDesignTime()) {
            setExpression(text);
        } else {
            setTextValue(text);
        }
    }

    public String getVisibleWhen() {
        return visibleWhen;
    }

    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }

    public Border getBorder() {
        Border border = super.getBorder();
        if ( border instanceof BorderWrapper ) {
            border = ((BorderWrapper) border).getBorder(); 
        }
        return border; 
    }    
    public void setBorder(Border border) {
        BorderWrapper wrapper = new BorderWrapper(border, getPadding());
        super.setBorder(wrapper);
    }

    public void setBorder(String uiresource) {
        try {
            Border border = UIManager.getLookAndFeelDefaults().getBorder(uiresource);
            if (border != null) {
                setBorder(border);
            }
        } catch (Exception ex) {;
        }
    }

    public String getCaption() {
        return property.getCaption();
    }

    public void setCaption(String caption) {
        property.setCaption(caption);
    }

    public boolean isShowCaption() {
        return property.isShowCaption();
    }

    public void setShowCaption(boolean show) {
        property.setShowCaption(show);
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

    public Insets getCellPadding() {
        return property.getCellPadding();
    }

    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }

    public String getFor() {
        return labelFor;
    }

    public void setFor(String name) {
        this.labelFor = name;
    }

    public void setLabelFor(Component c) {
        activeComponent = (JComponent) c;
        if (c instanceof ActiveControl) {
            ActiveControl ac = (ActiveControl) c;
            activeProperty = ac.getControlProperty();
            String acCaption = activeProperty.getCaption();
            if (forceUseActiveCaption || (!ValueUtil.isEmpty(acCaption) && !acCaption.equals("Caption"))) {
                setName(null);
                setExpression(null);
                formatText(activeProperty.getCaption(), activeProperty.isRequired());
                super.setDisplayedMnemonic(activeProperty.getCaptionMnemonic());
            }

            activeControlSupport = new ActiveControlSupport();
            activeProperty.addPropertyChangeListener(activeControlSupport);
        }
        super.setLabelFor(c);
    }

    public Insets getPadding() {
        return padding;
    }
    public void setPadding(Insets padding) {
        this.padding = padding;
        
        Border border = getBorder();
        if ( border instanceof BorderWrapper ) {
            border = ((BorderWrapper) border).getBorder(); 
        }
        super.setBorder( new BorderWrapper(border, getPadding())); 
    }

    public boolean isAddCaptionColon() {
        return addCaptionColon;
    }
    public void setAddCaptionColon(boolean addCaptionColon) {
        this.addCaptionColon = addCaptionColon;
        formatText(activeProperty.getCaption(), activeProperty.isRequired());
    }

    public Format getFormat() {
        return format;
    }
    public void setFormat(Format format) {
        this.format = format;
    }

    public boolean isAntiAliasOn() {
        return antiAliasOn;
    }
    public void setAntiAliasOn(boolean antiAliasOn) {
        this.antiAliasOn = antiAliasOn;
    }

    public Color getBackground() {
        try {
            if (isOpaque() && !isEnabled()) {
                Color bgcolor = UIManager.getLookAndFeelDefaults().getColor("ComboBox.disabledBackground");
                if (bgcolor != null) {
                    return bgcolor;
                }
            }
        } catch (Throwable t) {;
        }

        return super.getBackground();
    }
    
    // </editor-fold>    
    
    
    // <editor-fold defaultstate="collapsed" desc=" UIOutput implementation ">    
    
    public Object getValue() {
        Object beanValue = null;
        boolean hasName = !ValueUtil.isEmpty(getName());
        if (hasName) {
            beanValue = UIControlUtil.getBeanValue(this);
        }

        if (!ValueUtil.isEmpty(expression)) {
            Object exprBean = binding.getBean();
            if (getVarName() != null) {
                exprBean = createExpressionBean(beanValue);
            }

            return UIControlUtil.evaluateExpr(exprBean, expression);
        } else if (hasName) {
            return beanValue;
        } else {
            return super.getText();
        }
    }

    public void setName(String name) {
        super.setName(name);

        if (Beans.isDesignTime()) {
            showDesignTimeValue();
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int idx) {
        index = idx;
    }

    public String[] getDepends() {
        return depends;
    }

    public void setDepends(String[] depends) {
        this.depends = depends;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public void load() {
        if (!ValueUtil.isEmpty(labelFor)) {
            UIControl c = binding.find(labelFor);
            if (c instanceof JComponent) {
                this.setLabelFor((JComponent) c);
            }
        }
    }
    
    private Format getFormatter() {
        Format format = getFormat(); 
        if ( format != null ) return format; 
        
        String pattern = getDateFormat(); 
        if ( pattern != null && pattern.trim().length() > 0 ) {
            return new SimpleDateFormat( pattern ); 
        }
        
        pattern = getNumberFormat(); 
        if ( pattern != null && pattern.trim().length() > 0 ) {
            return new DecimalFormat( pattern ); 
        }
        return null; 
    }

    public void refresh() {
        try {
            String name = getName();
            boolean hasName = (name != null && name.length() > 0);

            Object beanValue = null;
            if (hasName) {
                beanValue = UIControlUtil.getBeanValue(getBinding(), name);
            }

            Object value = null;
            String exprStr = getExpression();
            Object exprBean = createExpressionBean(beanValue);            
            if (exprStr != null && exprStr.length() > 0) {
                value = UIControlUtil.evaluateExpr(exprBean, exprStr);
            } else if (hasName) {
                value = beanValue;
            } else {
                value = super.getText();
            }  
            
            Format format = getFormatter(); 
            Object fvalue = formatValue(format, value); 
            
            setTextValue((fvalue == null ? "" : fvalue.toString())); 
            
            String exprWhen = getVisibleWhen();
            if (exprWhen != null && exprWhen.length() > 0) {
                boolean result = false;
                try {
                    result = UIControlUtil.evaluateExprBoolean(exprBean, exprWhen);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                setVisible(result);
            }
            
        } catch (Throwable e) {
            setTextValue("");

            if (ClientContext.getCurrentContext().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }

    public Map getInfo() {
        Map map = new HashMap();
        map.put("expression", getExpression());
        map.put("format", getFormat());
        map.put("varName", getVarName());
        map.put("visibleWhen", getVisibleWhen());
        return map;
    }

    public int getStretchWidth() {
        return stretchWidth;
    }

    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth;
    }

    public int getStretchHeight() {
        return stretchHeight;
    }

    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    public ControlProperty getControlProperty() {
        return property;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" for layout purposes ">   
    
    public boolean isVisible() {
        boolean b = super.isVisible();
        if (Beans.isDesignTime()) {
            return b;
        }
        if (!isHideOnEmpty()) {
            return b;
        }
        if (!b) {
            return false;
        }

        String text = super.getText();
        return (text == null ? false : text.trim().length() > 0);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Owned and helper methods ">   
    
    public void setDisplayedMnemonic(char aChar) {
        super.setDisplayedMnemonic(aChar);
        if (aChar == '\u0000') {
            return;
        }

        String text = getText().toLowerCase();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == aChar) {
                setDisplayedMnemonicIndex(i);
                return;
            }
        }

        Pattern p = Pattern.compile("<.*?>");
        Matcher m = p.matcher(text);
        int startindex = 0;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (start > startindex) {
                chars = text.substring(startindex, start).toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == aChar) {
                        setDisplayedMnemonicIndex(startindex + i);
                        return;
                    }
                }
            }
            startindex = end;
        }
    }

    public void paint(Graphics g) {
        if (isAntiAliasOn()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paint(g2);
            g2.dispose();
        } else {
            super.paint(g);
        }
    }

    private void showDesignTimeValue() {
        if (!Beans.isDesignTime()) {
            return;
        }

        String str = null;
        if ((str = getExpression()) != null) {
            super.setText(resolveText(str));
        } else if ((str = getName()) != null) {
            super.setText(str);
        } else {
            super.setText("");
        }
    }
    private String originalText;

    private void setTextValue(String text) {
        this.originalText = text;
        super.setText(resolveText(text));
    }

    private String resolveText(String text) {
        if (isUseHtml() && text != null) {
            if (text.trim().length() == 0) {
                return "";
            }

            StringBuffer sb = new StringBuffer();
            if (text.toLowerCase().indexOf("<html>") < 0) {
                sb.append("<html>");
            }

            sb.append(text);

            if (text.toLowerCase().lastIndexOf("</html>") < 0) {
                sb.append("</html>");
            }

            return sb.toString();
        }
        return text;
    }

    private Object createExpressionBean(Object itemBean) {
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        if (itemBean != null) {
            beanSupport.setItem(getVarName(), itemBean);
        }

        return beanSupport.createProxy();
    }

    private void formatText(String text, boolean required) {
        StringBuffer sb = new StringBuffer(text);
        if (addCaptionColon && !ValueUtil.isEmpty(text)) {
            sb.append(" :");
        }

        if (required) {
            int mnem = getDisplayedMnemonic();
            int idx = findDisplayedMnemonicIndex(sb, mnem);
            if (idx != -1) {
                sb.replace(idx, idx + 1, "<u>" + sb.charAt(idx) + "</u>");
            }

            sb.insert(0, "<html>");
            sb.append(" <font color=\"red\">*</font>");
            sb.append("</html>");
        }

        super.setText(sb.toString());
    }

    static int findDisplayedMnemonicIndex(StringBuffer text, int mnemonic) {
        if (text == null || mnemonic == '\0') {
            return -1;
        }

        char uc = Character.toUpperCase((char) mnemonic);
        char lc = Character.toLowerCase((char) mnemonic);

        int uci = text.indexOf(uc + "");
        int lci = text.indexOf(lc + "");

        if (uci == -1) {
            return lci;
        } else if (lci == -1) {
            return uci;
        } else {
            return (lci < uci) ? lci : uci;
        }
    }
    
    private Object formatValue( Format fm, Object value ) { 
        if ( value == null || fm == null ) return value; 
        
        if ( fm instanceof DecimalFormat ) { 
            try { 
                if ( value instanceof Number ) {
                    return fm.format((Number) value); 
                } else {
                    return fm.format( new BigDecimal(value.toString()));  
                }                 
            } catch(Throwable t) {
                return value; 
            }
        } 
        
        if ( fm instanceof SimpleDateFormat ) { 
            try { 
                if ( value instanceof java.util.Date ) {
                    return fm.format((java.util.Date) value); 
                }
                
                java.util.Date dt = null; 
                String str = value.toString(); 
                String[] arr = str.split(" "); 
                if ( arr.length == 6 ) {
                    StringBuilder sb = new StringBuilder(); 
                    sb.append(arr[1]).append("-").append(arr[2]).append("-").append(arr[5]).append(" ").append(arr[3]);
                    try { 
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss"); 
                        dt = sdf.parse(sb.toString()); 
                    } catch(Throwable t){;} 
                }
                
                if ( dt != null ) {
                    //do nothing 
                } else if ( str.matches("[0-9]{4,4}-[0-9]{2,2}-[0-9]{2,2} [0-9]{2,2}:[0-9]{2,2}:[0-9]{2,2}[.][0-9]{1,}")) {
                    dt = java.sql.Timestamp.valueOf( str ); 
                } else if ( str.matches("[0-9]{4,4}-[0-9]{2,2}-[0-9]{2,2} [0-9]{2,2}:[0-9]{2,2}:[0-9]{2,2}")) {
                    dt = java.sql.Timestamp.valueOf( str ); 
                } else if ( str.matches("[0-9]{4,4}-[0-9]{2,2}-[0-9]{2,2}")) {
                    dt = java.sql.Date.valueOf( str ); 
                }
                
                if ( dt != null ) { 
                    return fm.format( dt );
                }                 
            } catch(Throwable t) {
                return value; 
            } 
        }
        
        return value;  
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControlSupport (class) ">
    
    private class ActiveControlSupport implements PropertyChangeListener {

        XLabel root = XLabel.this;
        private Color oldFg;

        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            Object value = evt.getNewValue();

            if ("caption".equals(propName)) {
                String text = (value == null) ? "" : value + "";
                formatText(text, activeProperty.isRequired());

            } else if ("captionMnemonic".equals(propName)) {
                setDisplayedMnemonic((value + "").charAt(0));
                formatText(activeProperty.getCaption(), activeProperty.isRequired());

            } else if ("required".equals(propName)) {
                boolean required = "true".equals(value + "");
                formatText(activeProperty.getCaption(), required);

            } else if ("errorMessage".equals(propName)) {
                String message = (value != null) ? value + "" : null;
                boolean error = !ValueUtil.isEmpty(message);
                if (error) {
                    oldFg = getForeground();
                    setForeground(Color.RED);
                } else {
                    setForeground(oldFg);
                }
                setToolTipText(message);
                if (activeComponent != null) {
                    activeComponent.setToolTipText(message);
                }
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BorderWrapper (class) ">
    
    private class BorderWrapper extends AbstractBorder {

        XLabel root = XLabel.this;
        private Border border;
        private Insets padding;

        BorderWrapper(Border border, Insets padding) {
            if (border instanceof BorderWrapper) {
                this.border = ((BorderWrapper) border).getBorder();
            } else {
                this.border = border;
            }

            this.padding = copy(padding);
        }

        public Border getBorder() {
            return border;
        }

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }

        public Insets getBorderInsets(Component c, Insets ins) {
            if (ins == null) {
                new Insets(0, 0, 0, 0);
            }

            ins.top = ins.left = ins.bottom = ins.right = 0;
            if (border != null) {
                Insets ins0 = border.getBorderInsets(c);
                ins.top += ins0.top;
                ins.left += ins0.left;
                ins.bottom += ins0.bottom;
                ins.right += ins0.right;
            }

            ins.top += padding.top;
            ins.left += padding.left;
            ins.bottom += padding.bottom;
            ins.right += padding.right;
            return ins;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            if (border != null) {
                border.paintBorder(c, g, x, y, w, h);
            }
        }

        private Insets copy(Insets padding) {
            if (padding == null) {
                return new Insets(0, 0, 0, 0);
            }

            return new Insets(padding.top, padding.left, padding.bottom, padding.right);
        }
    }
    
    // </editor-fold>
}
