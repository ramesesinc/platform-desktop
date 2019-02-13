/*
 * XImage.java
 *
 * Created on July 19, 2013, 9:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport; 
import com.rameses.rcp.control.border.CSSBorder;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class XImage extends JLabel implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo
{
    private Binding binding;
    private String[] depends;
    private int index; 
    private boolean dynamic;
    
    private Border sourceBorder;
    private Insets padding;
    private String iconResource;
    private String borderCSS;
    private boolean hideWhenNoIcon;
    
    private ImageIcon iconResourceObj;
    private ImageIcon imageIcon;
    private Dimension scaleSize;
    
    private int stretchWidth;
    private int stretchHeight;     
    private boolean shrinkToFit; 
    private String visibleWhen;
         
    public XImage() 
    {
        super();
        setPadding(null); 
        setBorder((Border) null); 
        new MouseEventSupport(this).install(); 
    }

    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">     
    
    public boolean isShrinkToFit() {
        return shrinkToFit; 
    }
    public void setShrinkToFit( boolean shrinkToFit ) {
        this.shrinkToFit = shrinkToFit;
    }
    
    public void setName(String name) { 
        super.setName(name); 
        
        if (Beans.isDesignTime()) 
            super.setText((name==null? "": name));
    } 
    
    public void setBorder(Border border) 
    {
        BorderWrapper wrapper = new BorderWrapper(border, getPadding()); 
        super.setBorder(wrapper); 
        this.sourceBorder = wrapper.getBorder(); 
    }
    
    public void setBorder(String uiresource) 
    {
        try 
        { 
            Border border = UIManager.getLookAndFeelDefaults().getBorder(uiresource); 
            if (border != null) setBorder(border); 
        } 
        catch(Exception ex) {;} 
    } 
    
    public String getBorderCSS() { return borderCSS; }
    public void setBorderCSS(String borderCSS) {
        this.borderCSS = borderCSS; 
        setBorder(CSSBorder.parse(borderCSS)); 
    }
    
    public Insets getPadding() { return padding; } 
    public void setPadding(Insets padding) { 
        this.padding = (padding == null? new Insets(1,3,1,1): padding); 
        setBorder(this.sourceBorder);         
    }
    
    public void setText(String text) { 
        if (Beans.isDesignTime()) super.setText(text); 
    }
    
    public String getIconResource() { return iconResource; } 
    public void setIconResource(String iconResource) { 
        this.iconResource = iconResource; 
        this.iconResourceObj = getImageIcon(); //loads the icon
        setIcon( scaleIcon(this.iconResourceObj) ); 
        repaint();
    } 
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; } 
    
    private ImageIcon getImageIcon() 
    {
        String iconRes = getIconResource();
        if (iconRes == null) return null;

        ImageIconSupport iis = ImageIconSupport.getInstance(); 
        if (isDynamic()) iis.removeIcon(iconRes); 

        return iis.getIcon(iconRes); 
    } 
    
    public Dimension getScaleSize() { return scaleSize; } 
    public void setScaleSize(Dimension scaleSize) {
        this.scaleSize = scaleSize; 
        ImageIcon iicon = (this.imageIcon == null? this.iconResourceObj: this.imageIcon); 
        setIcon(iicon);
        repaint();
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }    
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">    
    
    public boolean isHideWhenNoIcon() { return hideWhenNoIcon; } 
    public void setHideWhenNoIcon(boolean hideWhenNoIcon) {
        this.hideWhenNoIcon = hideWhenNoIcon; 
    }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }

    public void load() {
    }

    public void refresh() { 
        try { 
            ImageIcon iicon = null; 
            String name = getName(); 
            if (name != null && name.length() > 0) { 
                Object value = UIControlUtil.getBeanValue(getBinding(), name); 
                if ( value instanceof byte[] ) {
                    iicon = new ImageIcon((byte[]) value);
                } else if ( value != null ) { 
                    iicon = ImageIconSupport.getInstance().getIcon(value.toString()); 
                } 
            } 
            this.imageIcon = iicon; 
            setIcon( scaleIcon(this.imageIcon) );
        } catch(Throwable t){ 
            this.imageIcon = null; 
        } 
        
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
        
        if (isHideWhenNoIcon() && getIcon() == null) { 
            setVisible(false); 
        } else { 
            setVisible(true); 
        } 
                
        repaint();        
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        return map;
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
    
    // <editor-fold defaultstate="collapsed" desc=" BorderWrapper (class) ">

    private class BorderWrapper extends AbstractBorder
    {   
        XImage root = XImage.this;
        private Border border;
        private Insets padding;
        
        BorderWrapper(Border border, Insets padding) {
            if (border instanceof BorderWrapper) 
                this.border = ((BorderWrapper) border).getBorder(); 
            else 
                this.border = border; 
            
            this.padding = copy(padding); 
        }
        
        public Border getBorder() { return border; } 
        
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }
        
        public Insets getBorderInsets(Component c, Insets ins) {
            if (ins == null) new Insets(0,0,0,0);
            
            ins.top = ins.left = ins.bottom = ins.right = 0;
            if (border != null) 
            {
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
            if (border != null) border.paintBorder(c, g, x, y, w, h); 
        }
        
        private Insets copy(Insets padding) {
            if (padding == null) return new Insets(0, 0, 0, 0);
            
            return new Insets(padding.top, padding.left, padding.bottom, padding.right); 
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods ">
    
    private ImageIcon scaleIcon(ImageIcon iicon) {
        if (iicon == null) { return null; } 
        
        Dimension scaledim = null; 
        if ( isShrinkToFit() ) {
            scaledim = new Dimension(getWidth(), getHeight());
        } else {
            scaledim = getScaleSize(); 
        }
        
        if (scaledim == null) { scaledim = new Dimension(0,0); } 
        if (scaledim.width == 0 && scaledim.height == 0) { return iicon; } 
        
        int iw = iicon.getIconWidth();
        int ih = iicon.getIconHeight(); 
        double scaleX = (double)scaledim.width  / (double)iw;
        double scaleY = (double)scaledim.height / (double)ih;
        double scale  = (scaleY > scaleX)? scaleX: scaleY;
        int nw = (int) (iw * scale);
        int nh = (int) (ih * scale);
        //int nx = (scaledim.width - nw) / 2;
        //int ny = (scaledim.height - nh) / 2;
        
        BufferedImage bi = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) bi.createGraphics(); 
        g2.drawImage(iicon.getImage(), 0, 0, nw, nh, null);
        g2.dispose(); 
        return new ImageIcon(bi); 
    }
    
    // </editor-fold>
}
