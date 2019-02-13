/*
 * XHtmlEditor.java
 *
 * Created on April 5, 2014, 10:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.HtmlEditorModel;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.HtmlEditorPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import java.awt.Font;
import java.awt.Insets;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author wflores
 */
public class XHtmlEditor extends HtmlEditorPanel implements UIInput, ActiveControl
{
    private Binding binding;
    private String[] depends;
    private int index;
    private boolean nullWhenEmpty;
    private boolean immediate;
    
    private HtmlEditorModel model;
    private ControlProperty controlProperty;    
    private String visibleWhen;    
    private String handler;
    private String varName;
    private String itemExpression;
    
    private int stretchWidth;
    private int stretchHeight;
        
    public XHtmlEditor() {
        super(); 
        initComponent();
    }
 
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 

    private void initComponent() {
        nullWhenEmpty = true;
        varName = "item";
        
        try { 
            HTMLDocument doc = getDocument(); 
            ClassLoader cloader = ClientContext.getCurrentContext().getClassLoader();
            URL url = cloader.getResource("images"); 
            if (url != null) doc.setBase(url); 
        } catch(Throwable t) {;}  
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) {
        this.varName = varName; 
    }    
    
    public String getItemExpression() { return itemExpression; } 
    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression; 
    }        
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput implementation "> 

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public void load() { 
        setEditorInputVerifier(UIInputUtil.VERIFIER);
        setEditorClientProperty(UIInput.class, this); 
        Object bean = getBinding().getBean();
        Object ohandler = UIControlUtil.getBeanValue(bean, getHandler()); 
        if (ohandler instanceof HtmlEditorModel) {
            this.model = (HtmlEditorModel)ohandler; 
        } else {
            this.model = new HtmlEditorModelImpl(); 
        }
    }

    public void refresh() { 
        try {
            setValue(UIControlUtil.getBeanValue(this)); 
        } catch(Throwable t) {
            setText("");
            
            System.out.println("[WARN] refresh failed caused by " + t.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
        } 
        
        try { 
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                Object bean = getBinding().getBean();
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(bean, visibleWhen);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                setVisible(b); 
            } 
        } catch(Throwable t) {;} 
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public Map getInfo() { 
        return null; 
    } 
    
    public Object getValue() { 
        Object value = super.getValue();
        if (value == null) return null;
        
        String str = (value == null? null: value.toString());
        int idx0 = str.indexOf("<body>");
        if (idx0 < 0) return str;
        
        int idx1 = str.indexOf("</body>", idx0);
        if (idx1 < 0) return str;
        
        return str.substring(idx0+7, idx1); 
    }
    public void setValue(Object value) {
        super.setValue(value); 
    }
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }
    
    public boolean isReadonly() { 
        return !isEditable();
    } 
    public void setReadonly(boolean readonly) {
        setEditable(!readonly); 
    }
    
    public boolean isImmediate() { return immediate; } 
    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }
    
    public void setRequestFocus(boolean focus) {
        super.setRequestFocus(focus); 
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
    
    public ControlProperty getControlProperty() { 
        if (controlProperty == null) {
            controlProperty = new ControlProperty();
        }
        return controlProperty; 
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
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private Object createExpressionBean(Object itemBean) { 
        Object bean = getBinding().getBean();
        ExprBeanSupport beanSupport = new ExprBeanSupport(bean);
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    } 
    
    protected Object getFormattedText(Object item) {
        String expression = getItemExpression();
        if (expression == null || expression.length() == 0) {
            return item.toString(); 
        } else {
            Object exprBean = createExpressionBean(item); 
            return UIControlUtil.evaluateExpr(exprBean, expression); 
        }
    }

    protected List fetchList(Map params) {
        return (model == null? null: model.fetchList(params)); 
    }
    
    private Map createMap(Object key, Object value) {
        Map data = new HashMap();
        data.put(key, value);
        return data;
    }
    
    protected String getTemplate(Object item) {
        Object o = (model == null? null: model.getTemplate(item));
        return (o == null? null: o.toString()); 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" HtmlEditorModelImpl "> 
    
    private class HtmlEditorModelImpl extends HtmlEditorModel 
    {
        
    }
    
    // </editor-fold>
}
