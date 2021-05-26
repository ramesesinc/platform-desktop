/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public abstract class UIActiveControlPanel extends JPanel 
    implements ActiveControl, MouseEventSupport.ComponentInfo {
    
    private String visibleWhen;
    private String disableWhen;
    
    private int stretchWidth;
    private int stretchHeight; 

    private String fontStyle;
    private Font sourceFont;
        
    private MainLayout mainLayout;
    
    public UIActiveControlPanel() {
        super();
        init0();
        initComponent();
    }
    
    private void init0() {
        mainLayout = new MainLayout(); 
        super.setLayout( mainLayout ); 
        
    }
    
    protected abstract void initComponent();
    
    public Component getView() {
        return (mainLayout == null ? null : mainLayout.view); 
    }
    public void setView( Component view ) {
        removeAll();
        add("view", view); 
    }
    
    public String getVisibleWhen() {
        return visibleWhen; 
    }
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen; 
    }
    
    public String getDisableWhen() {
        return disableWhen;
    }
    public void setDisableWhen( String disableWhen ) {
        this.disableWhen = disableWhen; 
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
    
    public String getFontStyle() { 
        return fontStyle; 
    } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } 
        else {
            super.setFont(sourceFont); 
        } 
        new FontSupport().applyStyles(this, fontStyle);
    } 
    
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property;
    
    @Override
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

    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation ">    
    
    @Override
    public Map getInfo() {
        Map map = new HashMap();
        map.put("name", getName()); 
        loadComponentInfo( map ); 
        return map;
    }
    
    protected void loadComponentInfo( Map info ) {
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" MainLayout ">    

    private class MainLayout implements LayoutManager {

        UIActiveControlPanel root = UIActiveControlPanel.this;
        
        Component view;
                
        @Override
        public void addLayoutComponent(String name, Component comp) {
            if ( "view".equals(name + "")) {
                this.view = comp;
            }
        }

        @Override
        public void removeLayoutComponent(Component comp) {
            if ( view != null && comp != null && view.equals(comp)) {
                view = null; 
            }
        }
        
        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized( parent.getTreeLock()) {
                int w = 0, h = 0; 
                if ( view != null && view.isVisible()) {
                    Dimension dim = view.getPreferredSize(); 
                    w = dim.width;
                    h = dim.height;
                }
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right); 
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized( parent.getTreeLock()) {
                int w = 0, h = 0; 
                if ( view != null && view.isVisible()) {
                    Dimension dim = view.getMinimumSize(); 
                    w = dim.width;
                    h = dim.height;                    
                }
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right); 
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h);                 
            }            
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized( parent.getTreeLock()) {
                if ( view == null || !view.isVisible() ) {
                    return;
                }
                
                Insets margin = parent.getInsets();
                int x = margin.left; 
                int y = margin.top;
                int w = parent.getWidth() - (margin.left + margin.right); 
                int h = parent.getHeight() - (margin.top + margin.bottom); 
                view.setBounds(x, y, w, h); 
            }
        }
    }
    
    // </editor-fold>
}
