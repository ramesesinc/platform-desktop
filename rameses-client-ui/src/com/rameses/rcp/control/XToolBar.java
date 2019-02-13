package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.layout.XLayout;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;

/**
 *
 * @author Windhel
 */
public class XToolBar extends JToolBar implements UIControl, ActiveControl {
    
    private Binding binding;
    private String[] depends;
    private int stretchWidth;
    private int stretchHeight;     
    private int index;
    
    private String visibleWhen; 
    private String alignment;
    private XLayout layout;
    
    public XToolBar() {
        super();
        initComponent();
    }
    
    //<editor-fold defaultstate="collapsed" desc=" init component ">
    
    private void initComponent() {
        super.setFloatable( false ); 
        super.setLayout( (layout=new XLayout()) );
        super.setBorder( BorderFactory.createEmptyBorder() ); 
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen; 
    }

    public void setLayout(LayoutManager mgr) { 
        //do nothing 
    }
    
    public String getAlignment() { return alignment; } 
    public void setAlignment( String alignment ) {
        this.alignment = alignment;
    }
    
    // </editor-fold>    
    
    //<editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
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
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    } 
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }    
    
    public void load() {
    }

    public void refresh() { 
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
                
        revalidate(); 
        repaint(); 
    } 
    
    //</editor-fold>
    
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
}
