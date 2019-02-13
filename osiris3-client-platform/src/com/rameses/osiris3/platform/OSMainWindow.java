/*
 * OSMainWindow.java
 *
 * Created on October 24, 2013, 9:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

import com.rameses.platform.interfaces.MainWindow;
import com.rameses.platform.interfaces.MainWindowListener;
import com.rameses.platform.interfaces.ViewContext;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author wflores
 */
class OSMainWindow implements MainWindow
{
    private MainWindowListener mainWindowListener;
    private JFrame window;
    private JPanel plainContentPanel; 
    private MainWindowPanel mainWindowPanel;
    private MainViewPanel mainViewPanel;
    private OSMainTabbedPane tabbedPane; 
    
    private GlassPaneImpl glassPane;
    private DefaultGlassPane defaultGlassPane;
    
    public OSMainWindow() {
        initComponent();
        initComponents(); 
    }

    private void initComponent() {
        OSPlatformIdentity spi = OSPlatformIdentity.getInstance(); 
        ImageIcon icon = spi.getIcon("icon");
        
        window = new JFrame();
        window.setTitle("Rameses Client Platform");
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setGlassPane(glassPane = new GlassPaneImpl(this)); 
        
        defaultGlassPane = new DefaultGlassPane();
        
        Image image = null; 
        try { image = icon.getImage(); } catch(Throwable t) {;} 
        
        try { 
            if (image == null) image = spi.getDefaultIcon().getImage();
        } catch(Throwable e){;}        
        
        try {
            if (image != null) window.setIconImage(image);
        } catch(Throwable t) {;} 
        
        window.addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
                windowClosingImpl(e);
            } 
        });         
    }
    
    private void initComponents() {
        plainContentPanel = new JPanel(new BorderLayout()); 
        tabbedPane = new OSMainTabbedPane();
        mainViewPanel = new MainViewPanel(); 
        mainViewPanel.setBorder(BorderFactory.createEmptyBorder(3,3,2,3));
        mainViewPanel.setContent(tabbedPane); 

        mainWindowPanel = new MainWindowPanel();
        mainWindowPanel.setContent(mainViewPanel); 
        window.setContentPane(mainWindowPanel); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public final JFrame getComponent() { 
        return window; 
    }
    
    public final JRootPane getRootPane() { 
        return window.getRootPane(); 
    }  
    
    void setContent(Container content) {
        plainContentPanel.removeAll();
        if (content != null) {
            plainContentPanel.add(content); 
            window.setContentPane(plainContentPanel); 
        }
        plainContentPanel.revalidate();
        plainContentPanel.repaint();        
    } 
        
    Component findExplorer(String id) {
        if (id == null) return null;
        
        Component comp = mainViewPanel.getExplorer(); 
        if (comp != null && id.equals(comp.getName())) 
            return comp;
        else 
            return null; 
    }
    
    void reinitialize() {
        mainViewPanel.removeAll();
        plainContentPanel.removeAll();
        initComponents(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MainWindow implementation ">
    
    public void setListener(MainWindowListener mainWindowListener) {
        this.mainWindowListener = mainWindowListener; 
    }

    public void setTitle(String title) {
        StringBuffer sb = new StringBuffer();
        if (title == null || title.length() == 0) {
            sb.append("Rameses Client Platform"); 
        } else {
            sb.append(title); 
        }
        
        OSPlatformIdentity spi = OSPlatformIdentity.getInstance(); 
        String platformtype = spi.getString("platform.type");
        if ("enterpise".equals(platformtype)) {
            sb.append("  Enterprise Edition");
        } else if ("community".equals(platformtype)) {
            sb.append("  Community Edition");
        }
        window.setTitle(sb.toString());
    }

    public void close() {
        try { 
            if (mainWindowListener != null && !mainWindowListener.onClose()) return;  
        } catch(Throwable ex) { 
            ex.printStackTrace(); 
        } 
        
        window.dispose(); 
        
        try { System.exit(0); } catch(Throwable t){;} 
    }

    public void show() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                showImpl(); 
            }
        });        
    }
    
    public void setComponent(final JComponent comp, String constraint) { 
        //do nothing if comp or constraint is null
        if (comp == null || constraint == null) return;
        
        if (MainWindow.MENUBAR.equals(constraint)) { 
            mainWindowPanel.setMenuBar((JMenuBar) comp); 
        } else if (MainWindow.TOOLBAR.equals(constraint)) { 
            if (comp instanceof JToolBar) 
                ((JToolBar) comp).setFloatable(false); 
            if (comp != null) 
                comp.setBorder(new ToolbarBorder());
            
            mainWindowPanel.setToolBar(comp);
            
        } else if (MainWindow.STATUSBAR.equals(constraint)) { 
            comp.setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
            mainWindowPanel.setStatusBar(comp);
            
        } else if (MainWindow.CONTENT.equals(constraint)) { 
            tabbedPane.add(comp); 
            
        } else if ("explorer".equals(constraint)) { 
            String title = " ";
            if (comp instanceof OSTabbedView) {
                title = ((OSTabbedView) comp).getTitle(); 
            } 
            mainViewPanel.setExplorer(comp, title); 
            mainViewPanel.updateCanvas(); 
            
        } else {
            return; 
        } 
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                SwingUtilities.updateComponentTreeUI(mainWindowPanel); 
            }
        }); 
        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    private void showImpl() {      
        Object oval = window.getRootPane().getClientProperty("Window.showInit");
        if (oval == null) { 
            Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
            Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(window.getGraphicsConfiguration()); 
            scrdim.width -= (margin.left + margin.right + 80); 
            scrdim.height -= (margin.top + margin.bottom + 40); 
            window.setSize(scrdim.width, scrdim.height); 
            window.setLocation(40, 20);
            //window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH); 
            
            KeyStroke ks = KeyStroke.getKeyStroke("ctrl shift I"); 
            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showInfo(e); 
                }
            };
            JRootPane rootPane = window.getRootPane(); 
            rootPane.registerKeyboardAction(al, ks, JComponent.WHEN_IN_FOCUSED_WINDOW); 
            window.getRootPane().putClientProperty("Window.showInit", true);
        } 
        window.setVisible(true); 
        OSManager.getInstance().closeStartupWindow();
    } 
    
    private void showInfo(ActionEvent e) {         
        if (mainViewPanel.isExplorerInFocus()) {
            mainViewPanel.showExplorerInfo();
        } else {
            tabbedPane.showInfo(); 
        }
    } 
    
    private void windowClosingImpl(WindowEvent e) { 
        close(); 
    } 
    
    void restoreMainPanel() {
        if (mainWindowPanel == null) return;
        
        Container con = window.getContentPane(); 
        if (mainWindowPanel.equals(con)) return;
        
        window.setContentPane(mainWindowPanel); 
        mainWindowPanel.revalidate();
        mainWindowPanel.repaint(); 
    }
    
    void hideGlassPane() {
        glassPane.setVisible(false); 
        glassPane.removeAll(); 
        defaultGlassPane.setVisible(false);
        defaultGlassPane.removeAll(); 
    }
    
    void showInGlassPane(Component comp, Map props) {
        if (props == null) props = new HashMap();

        if (comp instanceof ViewContext) {
            glassPane.removeAll();
            glassPane.add(comp);

            String id = (String) props.get("id");        
            glassPane.setName(id); 
            showGlassPaneImpl(glassPane);
            OSManager.getInstance().registerView(id, glassPane); 
        } else {
            defaultGlassPane.removeAll();
            defaultGlassPane.add(comp);
            showGlassPaneImpl(defaultGlassPane);
        }
    }
    
    private void showGlassPaneImpl(JComponent jcomp) {
        window.setGlassPane(jcomp);   
        jcomp.setVisible(true);
        SwingUtilities.updateComponentTreeUI(jcomp); 
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
            window.requestFocus(); 
            window.transferFocus();
        } 
    }    
    
    // </editor-fold>
    
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
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultGlassPane ">
    
    private class DefaultGlassPane extends JPanel 
    {
        DefaultGlassPane() {
            setOpaque(true);
            setLayout(new BorderLayout()); 
            addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    e.consume();
                }
                public void mouseEntered(MouseEvent e) {
                }
                public void mouseExited(MouseEvent e) {
                }
                public void mousePressed(MouseEvent e) {
                    e.consume();
                }
                public void mouseReleased(MouseEvent e) {
                    e.consume();
                }
            });
            addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {
                }
                public void mouseMoved(MouseEvent e) {
                }
            });
            addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent e) { 
                    e.consume(); 
                }
                public void keyReleased(KeyEvent e) {
                    e.consume();
                }
                public void keyTyped(KeyEvent e) {
                }
            }); 
        }
    }
    
    // </editor-fold>
}
