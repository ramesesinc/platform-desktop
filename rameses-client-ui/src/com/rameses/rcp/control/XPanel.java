/*
 * XPanel.java
 *
 * Created on April 21, 2014, 9:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class XPanel extends JPanel implements UIControl, ControlContainer, ActiveControl {
    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String visibleWhen;
    
    private int stretchWidth;
    private int stretchHeight;     
    
    public XPanel() {
    }

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
        LayoutManager lm = getLayout();
        if (lm instanceof CardLayout) { 
            String sname = UIControlUtil.getBeanValue(getBinding(), getName())+""; 
            CardLayout cardlayout = (CardLayout)lm; 
            cardlayout.show(this, sname ); 
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
        
        invalidate(); 
        repaint();
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
    
    // <editor-fold defaultstate="collapsed" desc=" ControlContainer implementation "> 
    
    public boolean isHasNonDynamicContents() { 
        return true; 
    }

    public UIControl find(String name) {
        return null; 
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

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
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
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    

    // </editor-fold>
}
