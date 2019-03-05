/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.jfx;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 *
 * @author wflores
 */
public class WebViewPane extends JPanel {
    
    private final static Object LOAD_VIEW_LOCKED = new Object();
    
    private JFXPanel fxp;
    private WebView wv; 
    private JLabel stat;
    
    private boolean contextMenuEnabled;
    
    public WebViewPane() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 250)); 
        setBorder( BorderFactory.createLineBorder(new Color(180,180,180), 1)); 
        setBackground(Color.WHITE); 
        contextMenuEnabled = false; 
        stat = new JLabel("");
        fxp = new JFXPanelImpl(); 
        add(fxp, BorderLayout.CENTER); 
    }
    
    public boolean isContextMenuEnabled() {
        return contextMenuEnabled; 
    }
    public void setContextMenuEnabled( boolean contextMenuEnabled ) {
        this.contextMenuEnabled = contextMenuEnabled; 
    }
    
    protected void loadView( Object value ) {
        Platform.runLater( new WebViewLoader( value ) ); 
    }
    
    protected void processAction( String name, Map param ) {
    }
    
    private void updateStat( String text ) {
        if ( text == null ) text = ""; 
        if ( text.trim().length() > 0 ) {
            stat.setText(" "+ text +"          "); 
        } else {
            stat.setText(""); 
        } 
        fxp.repaint(); 
    }
    
    private class JFXPanelImpl extends JFXPanel {
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            String text = stat.getText(); 
            if ( text == null || text.trim().length() == 0 ) {
                // no painting of status 
                return; 
            }
            
            Dimension dim = stat.getPreferredSize(); 
            int dh = dim.height + 5;

            Insets margin = getInsets();
            int x = margin.left + 1; 
            int y = getHeight() - margin.bottom - dh -1;
            y = Math.max(y, margin.top); 

            AlphaComposite alpha = createAlphaComposite(0.5f); 
            if ( alpha != null ) {
                Graphics2D g2 = (Graphics2D) g.create(); 
                g2.setComposite(alpha); 
                g2.fillRect(x, y, dim.width, dh);
                g2.dispose();
            }

            Graphics2D g2 = (Graphics2D) g.create(); 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
            g2.setColor( Color.WHITE ); 
            g2.drawString( stat.getText(), x+2, y+13); 
            g2.dispose();
        }
        
        private AlphaComposite createAlphaComposite(float alpha) {
            try {
                return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha); 
            } catch (Throwable t) {
                return null; 
            } 
        }         
    }
    
    private class WebViewLoader implements Runnable {
        
        private Object value; 
        
        WebViewLoader( Object value ) {
            this.value = value; 
        }
        
        public void run() {
            synchronized( LOAD_VIEW_LOCKED ) {
                runImpl();
            }
        }
        
        private void runImpl() {
            if ( wv == null ) {
                wv = new WebView(); 
                Worker worker = wv.getEngine().getLoadWorker(); 
                worker.stateProperty().addListener(new ChangeListener() {
                    public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                        //System.out.println("working... "+ newValue.toString());
                        if ( newValue == Worker.State.READY ) {
                            updateStat("Connecting..."); 
                        }
                        else if ( newValue == Worker.State.SCHEDULED ) {
                            updateStat("Connecting..."); 
                        }
                        else if ( newValue == Worker.State.RUNNING ) {
                            updateStat("Waiting..."); 
                        }
                        else if ( newValue == Worker.State.CANCELLED ) {
                            updateStat("Cancelled"); 
                        }
                        else if ( newValue == Worker.State.FAILED ) {
                            updateStat("Failed"); 
                        }
                        
                        if ( newValue == Worker.State.SUCCEEDED ) {
                            updateStat( null ); 
                            Document doc = wv.getEngine().getDocument(); 
                            hookActionEvent( doc.getElementsByTagName("a")); 
                            hookActionEvent( doc.getElementsByTagName("button")); 
                            hookActionEvent( doc.getElementsByTagName("input")); 
                        }
                    }
                });             

                fxp.setScene(new Scene(wv)); 
            }

            if (value == null) value = "";

            StringBuilder styles = new StringBuilder(); 
            styles.append(" -fx-context-menu-enabled: "+ isContextMenuEnabled() +"; "); 
            wv.setStyle( styles.toString() );  

            WebEngine we = wv.getEngine();            
            if (value instanceof URL) {
                we.load( value.toString()); 
            } 
            else if (value.toString().matches("[a-zA-Z]{1,}://.*")) {
                we.load( value.toString()); 
            } 
            else { 
                we.loadContent( value.toString()); 
            } 
        }
        
        private void hookActionEvent( NodeList nodes ) {
            if ( nodes == null ) return;
            
            for (int i=0; i<nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (!(node instanceof Element)) continue; 
                if (!(node instanceof EventTarget)) continue;
                
                Element elem = (Element) node; 
                String actionName = null;
                String stype = elem.getAttribute("type")+""; 
                if ( "A".equalsIgnoreCase( node.getNodeName()) ) {
                    actionName = elem.getAttribute("action");
                }
                else if ( "BUTTON".equalsIgnoreCase( node.getNodeName())) {
                    actionName = elem.getAttribute("action");
                }
                else if ( "INPUT".equalsIgnoreCase( node.getNodeName()) && "button".equalsIgnoreCase(stype)) {
                    actionName = elem.getAttribute("action"); 
                }
                
                if ( actionName != null && actionName.trim().length() > 0) {
                    EventTarget et = (EventTarget) node; 
                    et.addEventListener("click", new LinkActionHandler(), false);
                }
            }
            
        }
    }
 
    private class LinkActionHandler implements EventListener {
        public void handleEvent(Event e) {
            String domEventType = e.getType();
            if ( "click".equals(domEventType)) {
                e.stopPropagation(); 
                e.preventDefault();
                
                if ( e.getTarget() instanceof Node ) {
                    Node node = (Node) e.getTarget(); 
                    NamedNodeMap attrs = node.getAttributes(); 
                    HashMap param = new HashMap();
                    for (int i=0; i<attrs.getLength(); i++) { 
                        node = attrs.item(i); 
                        param.put(node.getNodeName(), node.getNodeValue()); 
                    }

                    fireProcessAction( param );
                } 
            } 
        }
        
        private void fireProcessAction( Map param ) {
            try {
                Object o = param.get("action"); 
                String aname = (o == null ? null : o.toString()); 
                processAction( aname, param ); 
            } 
            catch(Throwable t) { 
                t.printStackTrace();  
            } 
        }
    } 
    
}
