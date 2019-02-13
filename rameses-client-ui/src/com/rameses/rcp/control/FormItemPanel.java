/*
 * GroupItemPanel.java
 *
 * Created on May 27, 2013, 1:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertyChangeSupport;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class FormItemPanel extends JPanel implements FormItemProperty 
{
    private FormPanelPropertySupport formPropertySupport;     
    private CustomLayout layout;
    private JLabel lblCaption; 
    private String id;
    
    public FormItemPanel(String id) 
    {        
        this.id = id;        
        super.setLayout(layout = new CustomLayout()); 
        
        lblCaption = new JLabel(" ");
        lblCaption.setFont(lblCaption.getFont().deriveFont(Font.BOLD)); 
        formPropertySupport = new FormPanelPropertySupport(null); 
        //setBorder( BorderFactory.createLineBorder(Color.RED));
        super.setOpaque(false); 
    }

    public final LayoutManager getLayout() { return layout; }
    public final void setLayout(LayoutManager mgr) {}
    
    public FormPanelProperty getFormPanelProperty() { return formPropertySupport.getSource(); } 
    public void setFormPanelProperty(FormPanelProperty formProperty) { 
        this.formPropertySupport = new FormPanelPropertySupport(formProperty); 
    } 
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCaption() 
    { 
        String txt = lblCaption.getText(); 
        if (txt == null || txt.length() == 0 || " ".equals(txt)) return null; 
        
        return txt; 
    } 
    
    public void setCaption(String caption) 
    {
        if (caption == null || caption.length() == 0) caption = " ";
        
        this.lblCaption.setText(caption); 
    }
            
    protected final void addImpl(Component comp, Object constraints, int index) 
    {
        int count = getComponentCount(); 
        if (count == 0) super.addImpl(lblCaption, "header", index);

        onaddImpl(comp, constraints, index); 
    }
    
    protected void onaddImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index); 
    }

    public final void remove(int index) 
    {
        super.remove(index);         
        ItemPanel ip = getLastItem(); 
        if (ip == null && getComponentCount() > 0) 
            super.remove(lblCaption); 
    }

    public void remove(Component comp) 
    {
        super.remove(comp);
        ItemPanel ip = getLastItem(); 
        if (ip == null && getComponentCount() > 0) 
            super.remove(lblCaption);   
    }
    
    private ItemPanel getLastItem() 
    {
        for (int i=0; i<getComponentCount(); i++) 
        {
            Component c = getComponent(i); 
            if (c instanceof ItemPanel) 
                return (ItemPanel) c;
        }
        return null; 
    }   

    public int getStretchWidth() {
        return 0; 
    }

    public int getStretchHeight() {
        return 0;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" FormPanelPropertySupport (class) ">
    
    private class FormPanelPropertySupport implements FormPanelProperty 
    {
        private FormPanelProperty property; 
        
        FormPanelPropertySupport(FormPanelProperty property) {
            this.property = property;
        }
        
        public FormPanelProperty getSource() { return property; }
        
        public int getCellspacing() { 
            return (property == null? 2: property.getCellspacing());
        }
        
        public Insets getCellpadding() { 
            Insets pad = (property == null? null: property.getCellpadding()); 
            if (pad == null) pad = new Insets(0,0,0,0); 
            
            return pad; 
        }
        
        public boolean isShowCategory() { 
            boolean b = (property == null? false: property.isShowCategory());             
            return b;
        }

        public PropertyChangeSupport getPropertySupport() { 
            return (property == null? null: property.getPropertySupport()); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CustomLayout (class) ">
    
    private class CustomLayout implements LayoutManager
    {
        private int DEFAULT_LEFT_SPACING = 10;
        private int DEFAULT_TOP_SPACING = 5;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        private Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int cellSpacing = formPropertySupport.getCellspacing();
                boolean showCategory = formPropertySupport.isShowCategory();                
                Insets cellPadding = formPropertySupport.getCellpadding();
                if (cellPadding == null) cellPadding = new Insets(0,0,0,0);
                        
                int w=0, h=0, leftSpacing=0; 
                boolean hasVisibleComponents = false;
                if (showCategory) 
                {
                    Dimension dim = lblCaption.getPreferredSize(); 
                    w = dim.width; 
                    h = dim.height + DEFAULT_TOP_SPACING; 
                    leftSpacing = DEFAULT_LEFT_SPACING;
                    hasVisibleComponents = true;                    
                }
                
                boolean hasItemPanels = false;
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) 
                {
                    Component c = comps[i]; 
                    if (!c.isVisible()) continue;
                    if (!(c instanceof ItemPanel)) continue;  
                    if (hasVisibleComponents) h += cellSpacing;
                    
                    Dimension dim = c.getPreferredSize();                    
                    w = Math.max(w, dim.width + leftSpacing);
                    h += dim.height + cellPadding.top + cellPadding.bottom;                     
                    hasVisibleComponents = true;
                    hasItemPanels = true;
                }
                
                if (!hasItemPanels) 
                {
                    w=0; 
                    h=0;
                }
                
                Insets margin = parent.getInsets();
                w += margin.left + margin.right;
                h += margin.top + margin.bottom;
                return new Dimension(w, h); 
            }
        }

        public void layoutContainer(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {   
                int cellSpacing = formPropertySupport.getCellspacing();
                boolean showCategory = formPropertySupport.isShowCategory();                
                Insets cellPadding = formPropertySupport.getCellpadding();
                if (cellPadding == null) cellPadding = new Insets(0,0,0,0);
                
                int pWidth = parent.getWidth(), pHeight = parent.getHeight(); 

                Insets margin = parent.getInsets(); 
                int x = margin.left, y = margin.top;
                int w = pWidth - (margin.left + margin.right); 
                int h = pHeight - (margin.top + margin.bottom); 
                int leftSpacing = 0;
                
                boolean hasVisibleComponents = false; 
                if (showCategory) 
                {
                    leftSpacing = DEFAULT_LEFT_SPACING;                    
                    Dimension dim = lblCaption.getPreferredSize();
                    lblCaption.setBounds(x, y, w, dim.height); 
                    y += dim.height + DEFAULT_TOP_SPACING; 
                    x += leftSpacing;
                    w -= leftSpacing;
                    hasVisibleComponents = true;
                }
                
                boolean hasItemPanels = false;
                Component[] comps = parent.getComponents(); 
                for (int i=0; i<comps.length; i++) 
                {
                    Component c = comps[i]; 
                    if (!c.isVisible()) continue;
                    if (!(c instanceof ItemPanel)) continue;  
                    if (hasVisibleComponents) y += cellSpacing; 
                    
                    y += cellPadding.top;                     
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, w, dim.height);
                    y += cellPadding.bottom + dim.height;
                    hasVisibleComponents = true; 
                    hasItemPanels = true;
                } 
                
                if (!hasItemPanels) lblCaption.setBounds(-10,-10,10,10);
            }
        }        
    }
    
    // </editor-fold>
}
