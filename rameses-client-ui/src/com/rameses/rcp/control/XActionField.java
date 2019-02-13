/*
 * XActionField.java
 *
 * Created on December 7, 2013, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.ActionFieldModel;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.text.DefaultTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.BreakException;
import com.rameses.util.ExceptionManager;
import com.rameses.util.IgnoreException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class XActionField extends JPanel implements MouseEventSupport.ComponentInfo, UIControl, ActiveControl  
{
    private MouseEventSupport mouseSupport; 
    private DefaultTextField field; 
    private JButton button; 
    
    private int spacing; 
    private String handler; 
    private boolean readonly; 
    private String expression; 
    
    private Binding binding;
    private String[] depends; 
    private int index;
    
    private int stretchWidth;
    private int stretchHeight;
    
    private String visibleWhen;
    private String disableWhen;
    
    public XActionField() { 
        initComponent(); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent ">
   
    private void initComponent() {
        super.setLayout(new DefaultLayout());
        setOpaque(false); 
        setSpacing(2); 
        add(getField()); 
        add(getButton());  
        
        mouseSupport = new MouseEventSupport(this);
        mouseSupport.install(); 
    } 
 
    private DefaultTextField getField() {
        if (field == null) {
            field = new DefaultTextField();
            field.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    actionPerformedImpl(e);
                }
            }); 
        }
        return field;
    }
    
    private JButton getButton() {
        if (button == null) {
            button = new JButton("...");
            button.setMargin(new Insets(1,5,1,4));
            button.setFocusable(false); 
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    actionPerformedImpl(e);
                }
            }); 
        }
        return button;
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public void setLayout(LayoutManager mgr) {
    } 
    
    public int getSpacing() { return spacing; } 
    public void setSpacing(int spacing) { 
        this.spacing = spacing; 
    } 
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    }
    
    public boolean isReadonly() { return readonly; } 
    public void setReadonly(boolean readonly) {
        this.readonly = readonly; 
        getField().setEditable(!readonly); 
    }
    
    public String getExpression() { return expression; } 
    public void setExpression(String expression) {
        this.expression = expression; 
    }
        
    public String getActionText() { 
        return getButton().getText(); 
    } 
    public void setActionText(String text) {
        getButton().setText(text); 
    }
    
    public Insets getActionTextMargin() {
        return getButton().getMargin(); 
    }
    public void setActionTextMargin(Insets margin) {
        getButton().setMargin(margin); 
    }
    
    public Icon getActionIcon() {
        return getButton().getIcon(); 
    }
    public void setActionIcon(Icon icon) {
        getButton().setIcon(icon); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" Font support implementation ">
    
    private FontSupport fontSupport;
    private Font sourceFont;     
    private String fontStyle; 
    
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }    
    
    public void setFont(Font font) { 
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            sourceFont = getFontSupport().applyStyles(sourceFont, attrs); 
        }
        
        super.setFont(sourceFont); 
        getField().setFont(sourceFont); 
    }     
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        
        if (sourceFont == null) sourceFont = super.getFont(); 
        
        Font font = sourceFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        font = getFontSupport().applyStyles(sourceFont, attrs); 
        
        super.setFont(font); 
        getField().setFont(font); 
    } 
    
    private Font actionFont;     
    private String actionFontStyle; 
    
    public Font getActionFont() {
        return getButton().getFont(); 
    }
    public void setActionFont(Font font) { 
        actionFont = font; 
        if (actionFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getActionFontStyle()); 
            actionFont = getFontSupport().applyStyles(actionFont, attrs); 
        }
        getButton().setFont(actionFont); 
    } 
    
    public String getActionFontStyle() { return actionFontStyle; } 
    public void setActionFontStyle(String actionFontStyle) {
        this.actionFontStyle = actionFontStyle;
        
        if (actionFont == null) actionFont = getButton().getFont();
        
        Font font = actionFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        if (!attrs.isEmpty()) font = font.deriveFont(attrs); 
        
        getButton().setFont(font); 
    }     
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation ">
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("expression", getExpression()); 
        map.put("handler", getHandler()); 
        return map;
    }   
    
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
            getField().setEnabled( !disabled ); 
            getButton().setEnabled( !disabled ); 
        }
        
        String expression = getExpression();
        Object bean = getBinding().getBean();
        if (expression != null && expression.length() > 0) { 
            Object result = UIControlUtil.evaluateExpr(bean, expression);
            getField().setText(result == null? "": result.toString());  
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
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">
    
    private ControlProperty property;
    
    public ControlProperty getControlProperty() {
        if (property == null) {
            property = new ControlProperty(); 
        }
        return property; 
    }
    
    public boolean isShowCaption() { 
        return getControlProperty().isShowCaption(); 
    }
    public void setShowCaption(boolean show) { 
        getControlProperty().setShowCaption(show); 
    }
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
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
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic(); 
    }
    public void setCaptionMnemonic(char captionMnemonic) { 
        getControlProperty().setCaptionMnemonic(captionMnemonic); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods ">
    
    public boolean isFocusable() {
        return false; 
    }
    public void setFocusable(boolean focusable) { 
        super.setFocusable(false); 
        getField().setFocusable(focusable); 
    } 
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); 
        getField().setEnabled(enabled);
        getButton().setEnabled(enabled); 
    }

    public void requestFocus() {
        getField().requestFocus(); 
    }
            
    private void actionPerformedImpl(ActionEvent ae) {
        try { 
            boolean ctrlDown = ((ae.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
            boolean shiftDown = ((ae.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK);
            if (ctrlDown && shiftDown) {
                mouseSupport.showComponentInfo(); 
                return; 
            }

            invokeAction(); 
        
        } catch(Exception x) {
            Exception e = ExceptionManager.getOriginal(x); 
            if (e instanceof IgnoreException || e instanceof BreakException) return; 
            
            MsgBox.err(x); 
        }
    } 
    
    protected void invokeAction() {
        String handler = getHandler();
        Object bean = getBinding().getBean();   
        Object info = new PropertyInfo();        
        Object result = null;
        
        Method method = findGetterMethod(bean, handler, null); 
        if (method != null) {
            result = invokeMethod(method, bean, null); 
        } else {
            method = findGetterMethod(bean, handler, new Class[]{Object.class}); 
            if (method != null) {
                result = invokeMethod(method, bean, new Object[]{info}); 
            }
        }
        
        if (result instanceof Opener) {
            showOutcome(result);
            
        } else { 
            ActionFieldModel model = null;
            if (result instanceof ActionFieldModel) {
                model = (ActionFieldModel) result;
            } else {
                model = new DefaultActionFieldModel(result); 
            }
            model.setProvider(new DefaultActionFieldModelProvider()); 
            Object outcome = model.invoke(info); 
            showOutcome(outcome); 
        }
    } 
    
    private void showOutcome(Object outcome) {
        if (outcome instanceof Opener) {
            getBinding().fireNavigation(outcome); 
        } 
    }
    
    private Method findGetterMethod(Object bean, String methodName, Class[] paramTypes) {
        if (methodName == null || methodName.length() == 0) return null; 
        if (paramTypes == null) paramTypes = new Class[]{};
        
        String gname = "get" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1); 
        Class beanClass = bean.getClass();
        try {
            return beanClass.getMethod(gname, paramTypes); 
        } catch (Throwable t) {
            return null;
        } 
    }
    
    private Object invokeMethod(Method method, Object bean, Object[] params) {
        if (method == null || bean == null) return null; 
        if (params == null) params = new Object[]{};
        try {
            return method.invoke(bean, params); 
        } catch (Throwable t) { 
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
            
            return null; 
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultLayout "> 
    
    private class DefaultLayout implements LayoutManager 
    {
        XActionField root = XActionField.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent);
        }
        
        private Dimension layoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                int spacing = Math.max(root.getSpacing(), 0);
                if (root.field != null && root.field.getParent() != null) {
                    Dimension dim = root.field.getPreferredSize();
                    w = dim.width;
                    h = dim.height; 
                    
                    if (root.button != null && root.button.getParent() != null) {
                        dim = root.button.getPreferredSize();
                        w += spacing + dim.width;
                        h = Math.max(dim.height, h);
                    }
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) { 
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth();
                int ph = parent.getHeight();
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);

                if (root.field != null && root.field.getParent() != null) {
                    int cw = w;
                    if (root.button != null && root.button.getParent() != null) {
                        Dimension dim = root.button.getPreferredSize();
                        cw = Math.max(w - dim.width, 0);
                        root.button.setBounds(x+cw, y, dim.width, h); 
                        
                        int spacing = Math.max(root.getSpacing(), 0);
                        cw = Math.max(cw - spacing, 0); 
                    } 
                    
                    root.field.setBounds(x, y, cw, h); 
                }
            } 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultActionFieldModel ">
    
    private class DefaultActionFieldModel extends ActionFieldModel 
    {
        private Object callback;
        private CallbackHandlerProxy proxy;
        
        DefaultActionFieldModel(Object callback) {
            if (callback != null) {
                proxy = new CallbackHandlerProxy(callback); 
            } 
        } 

        public Object invoke(Object info) {
            if (proxy == null) return null;
            
            return proxy.call(info); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultActionFieldModelProvider ">
    
    private class DefaultActionFieldModelProvider implements ActionFieldModel.Provider
    {
        XActionField root = XActionField.this;
        
        public Object getBinding() { 
            return root.getBinding(); 
        } 
    } 
    
    //  </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyInfo ">
    
    public class PropertyInfo 
    {
        XActionField root = XActionField.this;
        
        public Object getBinding() {
            return root.getBinding(); 
        }
        
        public String getName() {  
            return root.getName(); 
        } 
        
        public String getValue() {
            return root.getField().getText(); 
        }
        public void setValue(String value) {
            root.getField().setText(value == null? "": value); 
            root.getField().repaint(); 
        }
    }
    
    // </editor-fold>
}
