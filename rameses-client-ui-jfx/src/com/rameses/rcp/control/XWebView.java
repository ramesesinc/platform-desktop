/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.DocViewModel;
import com.rameses.rcp.common.HtmlViewModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.jfx.WebViewPane;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Font;
import java.awt.Insets;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class XWebView extends WebViewPane implements UIControl {
    
    private Binding binding;
    private String[] depends;
    private int index;
    private int stretchWidth;
    private int stretchHeight;
    
    private DocViewModel docModel; 
    private String visibleWhen;
    private boolean dynamic;
    
    public XWebView() {
        super(); 
        initComponents();
    }
    
    private void initComponents() {
        
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic( boolean dynamic ) {
        this.dynamic = dynamic;
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
    }

    public void refresh() { 
        DocViewModel newModel = null; 
        try {
            Object value = UIControlUtil.getBeanValue( this ); 
            if (value instanceof DocViewModel) {
                newModel = (DocViewModel) value; 
                value = newModel.getValue();
            } 

            if (newModel != null) { 
                newModel.setProvider(new ViewProviderImpl());
            } 
            
            docModel = newModel; 
            if ( docModel != null ) { 
                setContextMenuEnabled( docModel.isContextMenuEnabled()); 
            }
            loadView( value ); 
        } 
        catch(Throwable t) {
            if (newModel != null) newModel.setProvider(null); 
            
            System.out.println("[WARN] refresh failed caused by " + t.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
        } 

        try { 
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.trim().length() > 0) { 
                Object bean = getBinding().getBean();
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(bean, visibleWhen);
                } 
                catch(Throwable t) { 
                    t.printStackTrace();  
                } 
                setVisible( false );
            } 
        } 
        catch(Throwable t) {;} 
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
 
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    private ControlProperty controlProperty;
    
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
    
    // <editor-fold defaultstate="collapsed" desc=" ViewProviderImpl (class) "> 
    
    private class ViewProviderImpl implements HtmlViewModel.ViewProvider { 
        
        XWebView root = XWebView.this;
        
        public void insertText(String text) {}
        public void appendText(String text) {} 

        public String getText() { return null; } 
        public void setText(String text) { 
            root.loadView( text == null ? "" : text ); 
        } 

        public void load() {}

        public void refresh() {
            root.refresh(); 
        } 
        
        public void requestFocus() { 
        } 
    }
    
    // </editor-fold> 
    
    protected void processAction(String name, Map param) { 
        if (name == null || name.length() == 0) return;
        
        Object outcome = null; 
        try { 
            MethodResolver mresolver = MethodResolver.getInstance();
            outcome = mresolver.invoke(getBinding().getBean(), name, new Object[]{param}); 
        } 
        catch(Throwable t) { 
            System.out.println("[WARN] error invoking method '"+name+"' caused by " + t.getMessage()); 
            MsgBox.err( t );
            return; 
        } 

        if (outcome instanceof Opener) {
            if (outcome instanceof PopupMenuOpener) {
                MsgBox.alert("PopupMenuOpener is not supported using XWebView");
                return; 
            } 

            Opener op = (Opener) outcome;
            String target = op.getTarget(); 
            if ( target == null || target.trim().length()==0 || target.equals("self")) {
                op.setTarget("window"); 
            } 
            getBinding().fireNavigation( op ); 
        } 
    }    
}
