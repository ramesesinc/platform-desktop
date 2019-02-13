/*
 * XRadio.java
 *
 * Created on July 27, 2010, 2:23 PM
 * @author jaycverg
 */
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
import com.rameses.util.ValueUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;

public class XRadio extends JRadioButton implements UIInput, ItemListener,
        ActiveControl, MouseEventSupport.ComponentInfo {

    private Binding binding;
    private String[] depends;
    private String fontStyle;
    private boolean readonly;
    private int index;
    private Font sourceFont;
    private Object optionValue;
    private ButtonGroup buttonGroup;
    private ControlProperty property = new ControlProperty();
    private int stretchWidth;
    private int stretchHeight;
    private String visibleWhen;

    public XRadio() {
        super(); 
        initComponent(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" init components ">    
    
    private void initComponent() {
        addItemListener(this);
        new MouseEventSupport(this).install();
        
        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true); 
        registerKeyboardAction(new MoveAction(KeyEvent.VK_DOWN), ks, JComponent.WHEN_FOCUSED);
        
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true); 
        registerKeyboardAction(new MoveAction(KeyEvent.VK_UP), ks, JComponent.WHEN_FOCUSED);
    }
    
    private class MoveAction implements ActionListener {

        private int keycode;
        
        MoveAction( int keycode ) {
            this.keycode = keycode;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (buttonGroup == null) return;
            
            if ( e.getSource() instanceof XRadio ) {
                XRadio btn = (XRadio) e.getSource(); 
                ArrayList<XRadio> list = new ArrayList(); 
                Enumeration en = buttonGroup.getElements(); 
                while (en.hasMoreElements()) {
                    Object o = en.nextElement(); 
                    if ( o instanceof XRadio ) {
                        list.add((XRadio) o);
                    }
                }
                if ( list.isEmpty()) return;
                int idx = list.indexOf( btn );
                if ( idx < 0 ) return; 
                
                if ( keycode == KeyEvent.VK_DOWN) {
                    if ( idx+1 < list.size()) {
                        btn = list.get(idx+1);  
                    } else if ( idx+1 == list.size() ) {
                        btn = list.get(0);
                    } 
                } else if (keycode == KeyEvent.VK_UP ) {
                    if ( idx-1 >= 0 && idx-1 < list.size()) {
                        btn = list.get(idx-1);  
                    } else if ( idx-1 < 0 ) {
                        btn = list.get(list.size()-1);
                    } 
                } else {
                    //invalid key code 
                    return;
                }
                
                if ( !btn.isFocusOwner()) {
                    btn.requestFocusInWindow(); 
                    btn.setSelected(true); 
                }
            }
        }
        
    }
    
    // </editor-fold>

    public void refresh() {
        try {
            //force to update component's status
            if (isEnabled()) {
                setReadonly(isReadonly());
            }

            Object value = UIControlUtil.getBeanValue(this);
            setValue(value);
        } catch (Throwable e) {
            if (ClientContext.getCurrentContext().isDebugMode()) {
                e.printStackTrace();
            }
        }

        Object bean = (getBinding() == null ? null : getBinding().getBean());
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false;
            try {
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            setVisible(result);
        }
    }

    public void load() {
        String name = getName();
        if (!ValueUtil.isEmpty(name)) {
            Map<String, ButtonGroup> m = (Map) binding.getProperties().get(ButtonGroup.class);
            if (m == null) {
                m = new HashMap<String, ButtonGroup>();
                binding.getProperties().put(ButtonGroup.class, m);
            }

            if (!m.containsKey(name)) {
                m.put(name, new ButtonGroup());
            }

            buttonGroup = m.get(name);
            buttonGroup.add(this);
        }
    }

    public Map getInfo() {
        Map map = new HashMap();
        map.put("mnemonic", (char) getMnemonic());
        map.put("optionValue", getOptionValue());
        return map;
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            UIInputUtil.updateBeanValue(this);
        }
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

    public String getVisibleWhen() {
        return visibleWhen;
    }

    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }

    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    public void setValue(Object value) {
        if (value != null && value.equals(optionValue)) {
            setSelected(true);
        }
    }

    public Object getValue() {
        if (isSelected()) {
            return optionValue;
        } else {
            return null;
        }
    }

    public boolean isNullWhenEmpty() {
        return true;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        super.setEnabled(!readonly);
        super.firePropertyChange("enabled", readonly, !readonly);
        repaint();
    }

    public void setRequestFocus(boolean focus) {
        if (focus) {
            requestFocus();
        }
    }

    public String[] getDepends() {
        return depends;
    }

    public void setDepends(String[] depends) {
        this.depends = depends;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public Binding getBinding() {
        return binding;
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }

    public Object getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(Object optionValue) {
        this.optionValue = optionValue;
    }

    public ControlProperty getControlProperty() {
        return property;
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

    public boolean isShowCaption() {
        return property.isShowCaption();
    }

    public void setShowCaption(boolean show) {
        property.setShowCaption(show);
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

    public String getFontStyle() {
        return fontStyle;
    }

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

    public boolean isImmediate() {
        return true;
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
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
        if (mnemonic == '\u0000') {
            return;
        }

        String text = getText();
        if (text == null) {
            return;
        }

        String stext = text.toLowerCase();
        if (!stext.trim().matches("<html>.*</html>")) {
            return;
        }

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
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == cval) {
                        locIndex = startindex + i;
                        break;
                    }
                }
            }

            if (locIndex >= 0) {
                break;
            }

            startindex = end;
        }

        if (locIndex < 0) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(text.substring(0, locIndex));
        sb.append("<u>" + text.charAt(locIndex) + "</u>");
        sb.append(text.substring(locIndex + 1));
        super.setText(sb.toString());
    }
    // </editor-fold>
}
