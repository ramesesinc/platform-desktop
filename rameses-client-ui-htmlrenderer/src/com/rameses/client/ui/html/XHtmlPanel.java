/*
 * XHtmlPanel.java
 *
 * Created on September 6, 2013, 11:47 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.ui.html;

import com.rameses.rcp.common.DocViewModel;
import com.rameses.rcp.common.HtmlViewModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.Task;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import org.w3c.dom.NamedNodeMap;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.FSMouseListener;

/**
 *
 * @author wflores
 */
public class XHtmlPanel extends JPanel implements UIControl, ActiveControl 
{
    private ControlProperty controlProperty;    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String fontStyle;
    private DocViewModel docModel; 
    private Point mousePoint;   
    private XHTMLPanel viewer; 
    private FSScrollPane scroller;
    
    private String visibleWhen; 
    
    public XHtmlPanel() {
        super.setLayout(new BorderLayout()); 
        super.setPreferredSize(new Dimension(200,50)); 
        viewer = new XHTMLPanel(); 
        scroller = new FSScrollPane(viewer);
        scroller.setHorizontalScrollBarPolicy(FSScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        scroller.setVerticalScrollBarPolicy(FSScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
        add(scroller);          
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 

    public final void setLayout(LayoutManager mgr){;} 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }
    
    public void setName(String name) {
        super.setName(name); 
        if (Beans.isDesignTime()) {
            setText((name == null? "": name)); 
        }
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }        
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation "> 

    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public void load() { 
    }

    public void refresh() { 
        DocViewModel newModel = null; 
        try {
            Object value = UIControlUtil.getBeanValue(this);            
            if (value instanceof DocViewModel) {
                newModel = (DocViewModel) value; 
                value = newModel.getValue();
            } 
            
            Object result = null;
            if (value == null) {
                //do nothing 
            } else if (value instanceof URL) {
                result = (URL) value;
            } else if (value.toString().startsWith("file://")) {
                result = new URL(value.toString()); 
            } else if (value.toString().startsWith("http://")) {
                result = new URL(value.toString()); 
            } else { 
                result = value.toString().getBytes(); 
            } 
            
            if (newModel != null) newModel.setProvider(new ViewProviderImpl());                
            if (docModel != null) docModel.setProvider(null);
            
            docModel = newModel; 
            URLWorkerTask uwt = null;
            if (result instanceof URL) { 
                uwt = new URLWorkerTask((URL) result);
            } else if (result instanceof byte[]) {
                uwt = new URLWorkerTask((byte[]) result); 
            }
            
            if (uwt != null) { 
                ClientContext.getCurrentContext().getTaskManager().addTask(uwt); 
            }
        } catch(Throwable t) {
            setText("");
            if (newModel != null) newModel.setProvider(null); 
            
            System.out.println("[WARN] refresh failed caused by " + t.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
        } finally {
            
        }
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    public ControlProperty getControlProperty() { 
        if (controlProperty == null) {
            controlProperty = new ControlProperty();
        }
        return controlProperty; 
    }   
    
    public boolean isRequired() { 
        return getControlProperty().isRequired(); 
    }    
    public void setRequired(boolean required) {
        getControlProperty().setRequired(required);
    }
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
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
    public void setShowCaption(boolean showCaption) {
        getControlProperty().setShowCaption(showCaption);
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
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private void setText(String text) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes()); 
            viewer.setDocument(bais, ""); 
        } catch(Throwable e) {;} 
    } 

    @Override
    public int getStretchWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStretchWidth(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getStretchHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStretchHeight(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ViewProviderImpl (class) "> 
    
    private class ViewProviderImpl implements HtmlViewModel.ViewProvider 
    {        
        XHtmlPanel root = XHtmlPanel.this;
        
        public void insertText(String text) {}

        public String getText() { return null; } 
        public void setText(String text) {}

        public void load() {}

        public void refresh() {
            root.refresh(); 
        } 
        
        public void requestFocus() { 
            if (!root.isEnabled()) return;
            
            root.grabFocus(); 
            root.requestFocusInWindow(); 
        } 

        public void appendText(String string) {
            
        }
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" URLWorkerTask (class) "> 
    
    private class URLWorkerTask extends Task { 
        
        XHtmlPanel root = XHtmlPanel.this;

        private URL url;         
        private byte[] bytes;
        private boolean done;
        
        URLWorkerTask(byte[] bytes) {
            this.bytes = bytes; 
        }
        
        URLWorkerTask(URL url) {
            this.url = url; 
        }        
        
        public boolean accept() {
            return (done? false: true); 
        }

        public void execute() {
            showStartupContent();
            try {
                if (url != null) {
                    root.viewer.setDocument(url.openStream(), url.toString()); 
                } else if (bytes != null) {
                    root.viewer.setDocument(new ByteArrayInputStream(bytes), ""); 
                }
            } catch(Throwable t) { 
                showErrorContent(t); 
                
                if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
            } finally { 
                done = true; 
            } 
        }    
        
        private void showStartupContent() {
            String text = "<html><body><b>Loading...</b></body></html>"; 
            root.setText(text); 
        }
        
        private void showErrorContent(Throwable t) {
            String text = "<html><body color=\"red\">error caused by "+t.getMessage()+"</body></html>"; 
            root.setText(text); 
        } 
    }
    
    // </editor-fold>    
}
