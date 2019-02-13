package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.beans.Beans;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class XImagePanel extends JPanel implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo 
{    
    private static String DISPLAY_NORMAL        = "NORMAL";
    private static String DISPLAY_TILE          = "TILE";
    private static String DISPLAY_STRETCH       = "STRETCH";
    private static String DISPLAY_AUTO_RESIZE   = "AUTO";

    private String displayMode = DISPLAY_NORMAL; 
    private ImageIcon imageIcon;
    private Border innerBorder;
    private Border outerBorder;
    
    private Binding binding;
    private String[] depends;
    private boolean dynamic; 
    private int index; 
    private String iconResource; 
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen;
    
    public XImagePanel() {
        dynamic = true; 
        new MouseEventSupport(this).install(); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc="  setter(s)/getter(s)  ">
    
    public Border getBorder() { return innerBorder; } 
    public void setBorder(Border border) 
    {
        innerBorder = border; 
        if (innerBorder == null)
            super.setBorder(outerBorder); 
        else 
            super.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder)); 
    } 
    
    public String getDisplayMode() { return displayMode; }
    public void setDisplayMode(String displayMode) 
    {
        if (displayMode != null) 
            this.displayMode = displayMode.toUpperCase(); 
        else
            this.displayMode = DISPLAY_NORMAL; 
    }
    
    public String[] getDepends() { return depends; }   
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }
        
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public Icon getIcon() { return imageIcon; } 
    
    public String getIconResource() { return iconResource; } 
    public void setIconResource(String iconResource)
    {
        this.iconResource = iconResource; 
        
        URL url = null;
        try { 
            url = getClass().getResource(iconResource); 
        } catch(Exception ign) {;} 
        
        try 
        { 
            if (url == null)    
                url = ClientContext.getCurrentContext().getClassLoader().getResource(iconResource);  
        } catch(Exception ign) {;} 
        
        try 
        { 
            if (url == null)
                url = ClientContext.getCurrentContext().getClassLoader().getResource(iconResource); 
        } catch(Exception ign) {;}         
        
        try 
        {
            imageIcon = new ImageIcon(url); 
            int iw = imageIcon.getIconWidth(); 
            int ih = imageIcon.getIconHeight(); 
            setPreferredSize(new Dimension(iw, ih));  
            setMinimumSize(new Dimension(iw, ih));             
            setDynamic(true);
        } 
        catch(Exception ign) { 
            imageIcon = null; 
        } 
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }    
    
    // </editor-fold>
    
    private URL getImageResource(String path) 
    {
        if (ValueUtil.isEmpty(path)) return null;
        
        ClassLoader cl = ClientContext.getCurrentContext().getClassLoader();
        return cl.getResource(path);
    }
        
    public void refresh() 
    {
        if (isDynamic()) { 
            Object value = UIControlUtil.getBeanValue(this);
            try {
                imageIcon = null;

                if (value instanceof URL) {
                    imageIcon = new ImageIcon((URL) value); 
                } else if(value instanceof String) {
                    URL url = getImageResource(value.toString());
                    if (url != null) imageIcon = new ImageIcon(url);

                    if(ValueUtil.isEmpty(imageIcon))
                        imageIcon = new ImageIcon(new URL(value.toString()));
                } 
                else if (value instanceof byte[]) {
                    imageIcon = new ImageIcon((byte[])value);
                } 
                else if (value instanceof ImageIcon) {
                    imageIcon = (ImageIcon) value;
                } 
                else if (value instanceof File) 
                {
                    File file = (File) value; 
                    imageIcon = new ImageIcon(file.toURL());
                }
                else if (value instanceof Image)
                {
                    imageIcon = new ImageIcon((Image) value);
                }

                int iw = imageIcon.getIconWidth();
                int ih = imageIcon.getIconHeight();
                setPreferredSize(new Dimension(iw, ih)); 
                setMinimumSize(new Dimension(iw, ih));
            } catch(Throwable ex) {;}
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
    }
    
    public void load() {
    }
                
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        map.put("iconResource", getIconResource()); 
        return map;
    }    

    protected void paintComponent(Graphics g) 
    {
        super.paintComponents(g);
        
        if (Beans.isDesignTime()) return; 
        if (imageIcon == null) return; 
                
        int imgWidth = imageIcon.getIconWidth();
        int imgHeight = imageIcon.getIconHeight(); 
        int cw = getWidth();
        int ch = getHeight();
        if (DISPLAY_TILE.equals(displayMode)) 
        {
            Color oldColor = g.getColor();
            int cols = cw / imgWidth;
            int rows = ch / imgHeight;
            if (cw % imgWidth > 0) cols += 1;
            if (ch % imgHeight > 0) rows += 1;
            
            for (int i = 0 ; i < rows; i++) {
                for(int ii = 0; ii < cols; ii++) {
                    g.drawImage(imageIcon.getImage(), ii * imgWidth, i * imgHeight, imgWidth, imgHeight, null);
                }
            }
        }
        else if (DISPLAY_STRETCH.equals(displayMode)) 
        {
            cw = Math.max(cw, imgWidth); 
            ch = Math.max(ch, imgHeight); 
            g.drawImage(imageIcon.getImage(), 0, 0, cw, ch, null); 
        }
        else {
            g.drawImage(imageIcon.getImage(), 0, 0, imgWidth, imgHeight, null);
        }        
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
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

