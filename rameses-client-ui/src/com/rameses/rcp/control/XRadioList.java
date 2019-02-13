/*
 * XRadioList.java
 *
 * Created on July 30, 2014, 3:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.ObjectProxy;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.swing.RadioListPanel;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class XRadioList extends RadioListPanel implements UIControl, 
    Validatable, ActiveControl, MouseEventSupport.ComponentInfo 
{
    private ControlProperty property;    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private boolean dynamic;
    private boolean required;
    private String fontStyle;
    private String visibleWhen;
    private String handler;
    private String itemKey;
    private String itemExpression;
    private String varName; 
    
    private RadioListModel model;
    private Object modelObject;
    
    private int stretchWidth;
    private int stretchHeight;     
    
    public XRadioList() {
        super(); 
        varName = "item"; 
        setItemCount(2); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    }
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    public String getItemKey() { return itemKey; } 
    public void setItemKey(String itemKey) {
        this.itemKey = itemKey; 
    }

    public String getItemExpression() { return itemExpression; } 
    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression; 
    }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) {
        this.varName = varName; 
    }    
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation "> 

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public void load() { 
        model = null; 
        modelObject = null; 
        
        Binding binding = getBinding(); 
        Object bean = (binding == null? null: binding.getBean()); 
        
        try { 
            Object handlerObj = null; 
            String handler = getHandler();      
            if (handler != null && handler.length() > 0) {
                handlerObj = UIControlUtil.getBeanValue(bean, handler);
            }
            
            if (handlerObj != null) {
                modelObject = handlerObj;
                model = new ObjectProxy().create(modelObject, RadioListModel.class); 
            } 
        } catch(Throwable t) {;}
        
        if (modelObject == null) {
            model = new EmptyRadioListModel();
            modelObject = model; 
        }
    }

    private boolean refreshed;
    public void refresh() { 
        boolean reload = (!refreshed || isDynamic()); 
        try {
            if (reload) buildItems(); 
        } catch(Throwable t) { 
            t.printStackTrace(); 
        } 

        Binding binding = getBinding(); 
        Object bean = (binding == null? null: binding.getBean()); 
        Object value = UIControlUtil.getBeanValue(bean, getName()); 
        int idx = indexOfItemKey(value); 
        setSelectedIndex(idx); 
        
        try { 
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(bean, visibleWhen);
                } catch(Throwable t) { 
                    t.printStackTrace();  
                } 
                setVisible(b); 
            } 
        } catch(Throwable t) {;} 
        
        if (!refreshed) refreshed = true; 
        
        enableComponents(isEnabled()); 
        revalidate();
        repaint();
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public Map getInfo() { 
        return null; 
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
    
    // <editor-fold defaultstate="collapsed" desc=" Validatable implementation "> 
    
    private ActionMessage actionMessage;
    
    private Object getBeanValue() {
        String name = getName();
        if (name == null || name.length() == 0) return null;
        
        Binding binding = getBinding();
        Object bean = (binding == null? null: binding.getBean()); 
        if (bean == null) return null; 
        
        return UIControlUtil.getBeanValue(bean, name); 
    }
    
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
        if (!isRequired()) return; 

        Object value = getBeanValue();
        if (value == null || value.toString().length() == 0) {
            String caption = getCaption(); 
            if (caption == null) caption = getName(); 
            
            am.addMessage("1001", "{0} is required.", new Object[]{caption}); 
            if (am.hasMessages()) { 
                getControlProperty().setErrorMessage(am.toString()); 
            }
        } 
    } 

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    public ControlProperty getControlProperty() { 
        if (property == null) {
            property = new ControlProperty();
        }
        return property; 
    }   
    
    public boolean isRequired() { 
        return getControlProperty().isRequired(); 
    }    
    public void setRequired(boolean required) {
        getControlProperty().setRequired(required);
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
    
    // <editor-fold defaultstate="collapsed" desc=" RadioListModel "> 
    
    interface RadioListModel {
        int getRows(); 
        List fetchList( Map params );
        void onselect( Object obj ); 
    }
    
    private class EmptyRadioListModel implements RadioListModel 
    {
        public int getRows() { return -1; }
        public List fetchList(Map params) { return null; }
        public void onselect(Object obj) {}
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    protected void onselect(Object obj) {
        if (model == null) return;
        
        try {
            String skey = getItemKey();
            if (skey != null && skey.length() > 0) {
                obj = UIControlUtil.getBeanValue(obj, skey); 
            } 
            
            if (getCallbackMethod(modelObject, "onselect") == null) {
                Binding binding = getBinding();
                Object bean = binding.getBean();
                UIControlUtil.setBeanValue(bean, getName(), obj);
                EventQueue.invokeLater(new NotifyDependsRunnable(binding, getName(), obj));
            } else {
                EventQueue.invokeLater(new OnSelectRunnable(obj));
            }
        } catch(Throwable t) {
            MsgBox.err(t); 
        }
    }
    
    private void buildItems() {
        List list = null; 
        if (model != null) { 
            int rows = -1;
            if (getGetterMethod(modelObject, "getRows") != null) {
                rows = model.getRows(); 
            }
            
            Map params = new HashMap(); 
            params.put("_start", 0);
            params.put("_limit", (rows < 0? 100: rows)); 
            list = model.fetchList(params); 
        } 
        
        removeAll();
        if (list != null) {
            String expr = getItemExpression();
            for (Object o : list) { 
                if (expr == null || expr.length() == 0) { 
                    addItem(o==null? "null": o.toString(), o); 
                } else {
                    Object exprBean = createExpressionBean(o); 
                    Object result = UIControlUtil.evaluateExpr(exprBean, expr);
                    addItem(result==null? "null": result.toString(), o); 
                }
            }
        }
    } 
    
    private Object createExpressionBean(Object itemBean) { 
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    }     
    
    private int indexOfItemKey(Object key) {
        String skey = getItemKey();
        List list = getUserObjects(); 
        for (int i=0; i<list.size(); i++) { 
            Object o = list.get(i); 
            if (skey != null && skey.length() > 0) {
                o = UIControlUtil.getBeanValue(o, skey); 
            }
            
            if (o == null && key == null) return i;
            else if (o != null && o.equals(key)) return i; 
            else if (key != null && key.equals(o)) return i; 
        } 
        return -1;
    }
    
    private Method getCallbackMethod(Object obj, String name) {
        if (obj == null || name == null) return null;
        
        Class clazz = obj.getClass();
        try {
            Method m = clazz.getMethod(name, new Class[]{Object[].class});
            if (m != null) return m; 
        } catch(Throwable t) {;} 
        
        try {
            Method m = clazz.getMethod(name, new Class[]{Object.class});
            if (m != null) return m; 
        } catch(Throwable t) {;} 
        
        return null; 
    } 
    
    private Method getGetterMethod(Object obj, String name) {
        if (obj == null || name == null) return null;
        
        try {
            Method m = obj.getClass().getMethod(name, new Class[]{});
            if (m != null) return m; 
        } catch(Throwable t) {;} 
        
        return null; 
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Runnables "> 
    
    private class NotifyDependsRunnable implements Runnable 
    {
        private Binding binding;
        private String name;
        private Object value;
        
        NotifyDependsRunnable(Binding binding, String name, Object value) {
            this.binding = binding; 
            this.name = name;
            this.value = value;
        }
        
        public void run() {
            try { 
                binding.getValueChangeSupport().notify(name, value);
                binding.notifyDepends(name); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            }
        }
    }
    
    private class OnSelectRunnable implements Runnable 
    {
        private Object value;
        
        OnSelectRunnable(Object value) {
            this.value = value;
        }
        
        public void run() {
            try { 
                model.onselect(value); 
            } catch(Throwable t) {
                MsgBox.err(t); 
            }
        }
    }
    
    // </editor-fold>
}
