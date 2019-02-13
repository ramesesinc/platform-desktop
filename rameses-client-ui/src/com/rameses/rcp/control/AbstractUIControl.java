/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport.PropertyInfo;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
public abstract class AbstractUIControl extends JPanel 
    implements UIControl, ActiveControl {
    
    private Binding binding;
    private String[] depends;

    private int stretchWidth;
    private int stretchHeight;     
    private int index;
    
    private String visibleWhen;

    public AbstractUIControl() {
        super();
        initComponent_0();
    }
    
    protected abstract void initComponent();
    protected abstract Component getView();
    protected abstract void onLoad();
    protected abstract void onRefresh();
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent_0 ">
    
    private void initComponent_0() {
        setPreferredSize(new Dimension(100, 50)); 
        LayoutImpl layout = new LayoutImpl();
        setLayout( layout ); 
        initComponent(); 
        
        Component view = getView(); 
        if ( view != null ) {
            add( view, "view" ); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
    public Binding getBinding() {
        return binding; 
    }
    public void setBinding(Binding binding) {
        this.binding = binding; 
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
    public void setIndex( int index ) {
        this.index = index; 
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

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }
    
    public final void load() { 
        onLoad(); 
    }

    public void refresh() { 
        try {
            onRefresh();
        } catch(Throwable t) {
            if (ClientContext.getCurrentContext().isDebugMode()) {
                t.printStackTrace();
            }
        }
        
        String expr = getVisibleWhen(); 
        if ( expr != null && expr.trim().length() > 0) { 
            setVisible( evalBooleanExpr( expr )); 
        } 
        
        invalidate(); 
        repaint();        
    }

    public void setPropertyInfo(PropertyInfo info) {
    }
        
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property;
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
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
    
    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" Layout implementation ">
    
    private class LayoutImpl implements LayoutManager {

        private Component compView; 
        
        public void addLayoutComponent(String name, Component comp) {
            if ( comp == null ) return; 
            else if ( name == null ) return; 
            
            if ( "view".equals(name)) {
                compView = comp; 
            }
        }
        public void removeLayoutComponent(Component comp) { 
            if ( comp != null && compView != null && comp.equals(compView)) {
                compView = null; 
            }
        }

        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0; 
                if (compView != null ) {
                    Dimension dim = compView.getPreferredSize(); 
                    w = dim.width;
                    h = dim.height; 
                }
                Insets margin = parent.getInsets(); 
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension( w, h ); 
            }
        }

        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0; 
                Insets margin = parent.getInsets(); 
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension( w, h ); 
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
                
                if ( compView != null ) { 
                    compView.setBounds(x, y, w, h); 
                }
            }
        }
    }
    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc=" other methods ">
    
    public Object getBean() {
        Binding b = getBinding(); 
        return (b == null ? null : b.getBean()); 
    }

    public Object getBeanProperty( String name ) { 
        return UIControlUtil.getBeanValue(getBinding(), name); 
    }
    
    public boolean evalBooleanExpr( String expr ) { 
        if ( expr == null || expr.trim().length() == 0 ) {
            return true; 
        }
        
        Binding binding = getBinding(); 
        Object bean = (binding == null ? null: binding.getBean()); 
        boolean b = false; 
        try { 
            b = UIControlUtil.evaluateExprBoolean(bean, expr);
        } catch(Throwable t) {
            if (ClientContext.getCurrentContext().isDebugMode()) {
                t.printStackTrace();
            }
        } 
        return b; 
    }
    
    // </editor-fold>     
}
