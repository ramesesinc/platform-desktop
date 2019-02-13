/*
 * MainDialog.java
 *
 * Created on October 27, 2009, 4:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.impl;

import com.rameses.platform.interfaces.MainWindow;
import com.rameses.platform.interfaces.MainWindowListener;
import com.rameses.platform.interfaces.SubWindow;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author elmo
 */
class MainDialog implements MainWindow 
{
    private JFrame dialog;
    private MainWindowListener listener;
    private Component toolbar;
    private Component statusbar;
    private ExtTabbedPane tabbedPane;
    
    private Component defaultContentPane;
    private GlassPaneImpl glassPane; 
    private PlatformImpl platform;
    
    public MainDialog(PlatformImpl platform) {
        this.platform = platform;
        dialog = new JFrame();
        dialog.setTitle("Main Dialog");
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            URL ico = getClass().getResource("/com/rameses/rcp/icons/os2-icon.png");
            dialog.setIconImage(new ImageIcon(ico).getImage());
        }
        catch(Exception e){}
        
        dialog.getContentPane().setLayout(new BorderLayout()); 
        dialog.setGlassPane(glassPane = new GlassPaneImpl(platform, this)); 
        dialog.add((defaultContentPane = new TestPlatformContentPane()), BorderLayout.CENTER);
        
        dialog.addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
                close(); 
            } 
        }); 
    } 
    
    public JFrame getComponent() { return dialog; }
    
    public void show() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                showImpl(); 
            }
        });
    }
    
    private void showImpl() {        
        dialog.pack();

        KeyStroke ks = KeyStroke.getKeyStroke("ctrl shift I"); 
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInfo(e); 
            }
        };
        JRootPane rootPane = dialog.getRootPane(); 
        rootPane.registerKeyboardAction(al, ks, JComponent.WHEN_IN_FOCUSED_WINDOW); 
        dialog.setVisible(true); 
    } 
        
    private void showInfo(ActionEvent e) {
        if (tabbedPane != null) tabbedPane.showInfo(); 
    } 
    
    public void close() {
        if ( listener != null ) {
            try {
                if ( !listener.onClose() ) return;
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        
        dialog.dispose();
    }
    
    public void setTitle(String title) {
        dialog.setTitle(title);
    }
    
    public void setListener(MainWindowListener listener) {
        this.listener = listener;
    }
    
    public void setComponent(JComponent comp, String constraint) {
        //do nothing if comp or constraint is null
        if ( comp == null || constraint == null ) return;
        
        if ( constraint.equals(MainWindow.MENUBAR)) {
            dialog.setJMenuBar((JMenuBar) comp);
        } else if ( constraint.equals(MainWindow.TOOLBAR) ) {
            if ( comp instanceof JToolBar )
                ((JToolBar) comp).setFloatable(false);
            if ( toolbar != null ) {
                dialog.remove(toolbar);
            }
            
            toolbar = comp;
            comp.setBorder(new ToolbarBorder());
            dialog.add(comp, BorderLayout.NORTH, 1);
        } else if ( constraint.equals(MainWindow.CONTENT) ) {
            if( comp instanceof PlatformTabWindow ) {
                PlatformTabWindow tab = (PlatformTabWindow) comp;
                if( tabbedPane == null ) {
                    if( defaultContentPane != null )
                        dialog.remove(defaultContentPane);
                    
                    tabbedPane = new ExtTabbedPane();
                    dialog.add(tabbedPane, BorderLayout.CENTER);
                }
                tabbedPane.add((SubWindow) tab);
                //tabbedPane.addTab(tab.getTitle(), tab);
            }
        } else if ( constraint.endsWith(MainWindow.STATUSBAR)) {
            if( statusbar != null )
                dialog.remove(statusbar);
            
            statusbar = comp;
            comp.setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
            dialog.add(comp, BorderLayout.SOUTH);
        }
        SwingUtilities.updateComponentTreeUI( dialog.getContentPane() );
    } 
    
    void showGlassPane() {
        glassPane.setVisible(true);
        SwingUtilities.updateComponentTreeUI(glassPane); 
    }
    
    void hideGlassPane() {
        glassPane.setVisible(false); 
        glassPane.removeAll(); 
    }
    
    Container getGlassPane() {
        return glassPane; 
    }
    
    void requestFocus() {
        if (tabbedPane != null) {
            Component comp = tabbedPane.getSelectedComponent(); 
            if (comp != null) {
                tabbedPane.setFocusable(true);
                tabbedPane.requestFocus();
                tabbedPane.transferFocus(); 
            } 
        } else {
            dialog.requestFocus(); 
            dialog.transferFocus();
        } 
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarBorder ">
    
    private class ToolbarBorder extends AbstractBorder 
    {
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics g2 = g.create();
            g2.setColor(UIManager.getColor("controlShadow"));
            g2.drawLine(x, height-1, width-1, height-1);
            g2.setColor(UIManager.getColor("controlHighlight"));
            g2.drawLine(x, height-2, width-1, height-2);
            g2.dispose();
        }
        
        public Insets getBorderInsets(Component c) {
            return new Insets(1,1,4,1);
        }
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" StatusbarBorder ">
    
    private class StatusbarBorder extends AbstractBorder 
    { 
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics g2 = g.create();            
            g2.setColor(UIManager.getColor("controlShadow"));
            g2.drawLine(x, y, width-1, y);
            g2.setColor(UIManager.getColor("controlHighlight"));
            g2.drawLine(x, y+1, width-1, y+1);
            g2.dispose();
        }
        
        public Insets getBorderInsets(Component c) {
            return new Insets(4,1,1,1);
        }
    }
    
    // </editor-fold> 
}
