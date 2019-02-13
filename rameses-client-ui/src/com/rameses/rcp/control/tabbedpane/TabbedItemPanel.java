/*
 * TabbedItemPanel.java
 *
 * Created on September 12, 2013, 10:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.tabbedpane;

import com.rameses.rcp.common.Opener;
import com.rameses.rcp.control.XSubFormPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.util.ControlSupport;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
public class TabbedItemPanel extends JPanel
{
    private Opener opener;
    private Layout layout;
    private XSubFormPanel content;
    private boolean processing;
    private ImageIcon processIcon;
    private char mnemonic;
    
    public TabbedItemPanel(Opener opener) {
        super();
        
        this.opener = opener; 
        this.mnemonic = '\u0000';         
        super.setLayout(layout = new Layout()); 
    }
    
    public final LayoutManager getLayout() { return layout; } 
    public final void setLayout(LayoutManager mgr){
        //do nothing
    }
    
    public char getMnemonic() { return mnemonic; } 
    public void setMnemonic(char mnemonic) {
        this.mnemonic = mnemonic; 
    }
    
    public Opener getOpener() { return opener; } 
    
    public boolean hasContent() { 
        return (content != null); 
    }
    
    public JComponent getContent() { return content; } 
    
    public void loadContent() 
    {
        if (content == null) {
            processing = true;
            Runnable runnable = new Runnable(){
                public void run() {
                    TabbedItemPanel.this.repaint(); 
                } 
            }; 
            new Thread(runnable).start(); 
            
            EventQueue.invokeLater(new ContentLoader());
        }
    }
    
    public void reloadContent() {
        if (opener.getController() == null) {
            loadContent();
            
        } else { 
            EventQueue.invokeLater(new Runnable(){
                public void run() { 
                    reloadContentImpl();
                }            
            });
        }
    }        
    
    private void reloadContentImpl() {
        Opener newOpener = opener.createInstance(null, null); 
        if (newOpener == null) newOpener = opener;
        
        Map openerParams = newOpener.getParams();
        if (openerParams == null) {
            openerParams = new HashMap();
            newOpener.setParams(openerParams); 
        }
        Map udfParams = (provider == null? null: provider.getOpenerParams(newOpener));
        if (udfParams != null) openerParams.putAll(udfParams); 

        setContent(newOpener);
    }
    
    public void refreshContent() {
        if (opener.getController() == null) {
            loadContent();
            
        } else {         
            EventQueue.invokeLater(new Runnable(){
                public void run() { 
                    refreshContentImpl();
                }  
            });
        } 
    }    
    
    private void refreshContentImpl() {
        Map openerParams = opener.getParams();
        if (openerParams == null) {
            openerParams = new HashMap();
            opener.setParams(openerParams); 
        }
        Map udfParams = (provider == null? null: provider.getOpenerParams(opener));
        if (udfParams != null) openerParams.putAll(udfParams); 
        
        Object o = opener.getHandle();
        ControlSupport.setProperties(o, openerParams); 
        if (content != null) content.refreshViews(); 
    }
            
    private void setContent(Opener opener) {
        XSubFormPanel xsf = new XSubFormPanel(opener);
        xsf.setBinding(provider.getBinding());
        xsf.load();
        removeAll();
        content = xsf;
        add(xsf); 
    }
    
    public void paint(Graphics g) {
        super.paint(g); 
        
        if (processing) {
            ImageIcon iicon = getProcessIcon();
            if (iicon == null) return;
            
            iicon.paintIcon(this, g, 10, 10);
        }
    }
    
    private ImageIcon getProcessIcon() {
        if (processIcon == null) {
            processIcon = ImageIconSupport.getInstance().getIcon("com/rameses/rcp/icons/loading32.gif");
        }
        return processIcon; 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" Provider "> 
    
    private Provider provider;
    
    public final void setProvider(Provider provider) {
        this.provider = provider; 
    }
    
    public static interface Provider 
    {
        Binding getBinding();
        Map getOpenerParams(Object o);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Layout "> 
    
    private class Layout implements LayoutManager 
    {
        TabbedItemPanel root = TabbedItemPanel.this;
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int w = margin.left + margin.right;
                int h = margin.top + margin.bottom;
                return new Dimension(w, h);
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth(), ph = parent.getHeight();
                int x = margin.left, y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component c = root.content;
                if (c != null && c.isVisible()) {                 
                    c.setBounds(x, y, w, h); 
                }
            }
        }      
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ContentLoader "> 
    
    private class ContentLoader implements Runnable 
    {        
        TabbedItemPanel root = TabbedItemPanel.this;
        
        public void run() {            
            try {
                runImpl();
            } catch(Throwable t) {
                t.printStackTrace();
            } finally {
                root.processing = false;
                root.repaint();
                root.validate(); 
            }
        } 
        
        private void runImpl() {
            Opener opener = root.opener;
            Map openerParams = opener.getParams();
            if (openerParams == null) {
                openerParams = new HashMap();
                opener.setParams(openerParams); 
            }
            Map udfParams = (root.provider==null? null: root.provider.getOpenerParams(opener));
            if (udfParams != null) openerParams.putAll(udfParams); 

            root.setContent(opener); 
        }
    }
    
    // </editor-fold>        
}
