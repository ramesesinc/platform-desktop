/*
 * XHtmlView.java
 *
 * Created on August 26, 2013, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.DocViewModel;
import com.rameses.rcp.common.HtmlViewModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.Task;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UICommandUtil;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author wflores
 */
public class XHtmlView extends JEditorPane implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo 
{
    private ControlProperty controlProperty;    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String fontStyle;
    private DocViewModel docModel; 
    private Point mousePoint; 
    
    private String visibleWhen;
    
    public XHtmlView() {
        super(); 
        super.setContentType("text/html");
        super.setEditable(false); 
        addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                hyperlinkUpdateImpl(e); 
            }
        });
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                mousePoint = e.getPoint(); 
            } 
        }); 
        
        new MouseEventSupport(this).install(); 
        
        try { 
            Font font = UIManager.getLookAndFeelDefaults().getFont("TextField.font"); 
            super.setFont(font); 
        } catch(Throwable t) {;} 
        
        if (Beans.isDesignTime()) {
            setPreferredSize(new Dimension(100,50));
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }

    public void setDocument(Document doc) {
        try { 
            if (doc instanceof HTMLDocument) {
                ClassLoader cloader = ClientContext.getCurrentContext().getClassLoader();
                URL url = cloader.getResource("images"); 
                if (url != null) ((HTMLDocument) doc).setBase(url); 
            } 
        } catch(Throwable t) {;}
        
        super.setDocument(doc); 
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }

    public void setVisible(boolean visible) {
        Container parent = getParent(); 
        if (parent instanceof JViewport) {
            parent = parent.getParent(); 
            if (parent instanceof JScrollPane) {
                parent.setVisible(visible); 
                return;
            }
        }
        super.setVisible(visible); 
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
        //setInputVerifier(UIInputUtil.VERIFIER);
    }

    public void refresh() { 
        DocViewModel newModel = null; 
        try {
            Object value = UIControlUtil.getBeanValue(this);            
            if (value instanceof DocViewModel) {
                newModel = (DocViewModel) value; 
                value = newModel.getValue();
            } 
            
            URL url = null;
            if (value == null) {
                setText(""); 
                setCaretPosition(0); 
            } else if (value instanceof URL) {
                url = (URL) value;
            } else if (value.toString().startsWith("http://")) {
                url = new URL(value.toString()); 
            } else { 
                setText(value.toString()); 
                setCaretPosition(0); 
            } 
            
            if (newModel != null) newModel.setProvider(new ViewProviderImpl());                
            if (docModel != null) docModel.setProvider(null);
            
            docModel = newModel; 
            if (url != null) { 
                URLWorkerTask uwt = new URLWorkerTask(url);
                ClientContext.getCurrentContext().getTaskManager().addTask(uwt); 
            }
        } catch(Throwable t) {
            setText("");
            if (newModel != null) newModel.setProvider(null); 
            
            System.out.println("[WARN] refresh failed caused by " + t.getMessage());
            if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
        } finally {
            
        }
        
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
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public Map getInfo() { 
        return null; 
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
    
    private void hyperlinkUpdateImpl(HyperlinkEvent e) {
        if (!(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)) return;

        Map params = new HashMap(); 
        AttributeSet aset = e.getSourceElement().getAttributes();
        Enumeration en = aset.getAttributeNames();
        while (en.hasMoreElements()) {
            Object k = en.nextElement();
            Object v = aset.getAttribute(k);
            if (v instanceof AttributeSet) {
                AttributeSet vset = (AttributeSet) v; 
                Enumeration ven = vset.getAttributeNames();
                while (ven.hasMoreElements()) {
                    Object vk = ven.nextElement();
                    Object vv = vset.getAttribute(vk); 
                    params.put(vk.toString(), vv); 
                }
            }
        } 
        
        Object href = params.get("href");
        if (href == null) return;
        
        String shref = href.toString();
        if (!shref.matches("[a-zA-Z0-9_]{1,}")) return;
        
        Object outcome = null; 
        try { 
            MethodResolver mresolver = MethodResolver.getInstance();
            outcome = mresolver.invoke(getBinding().getBean(), shref, new Object[]{params}); 
        } catch(Throwable t) {
            System.out.println("[WARN] error invoking method '"+shref+"' caused by " + t.getMessage()); 
        } 
        
        if (outcome instanceof Opener) {
            if (outcome instanceof PopupMenuOpener) {
                final PopupMenuOpener menu = (PopupMenuOpener) outcome;
                List items = menu.getItems();
                if (items == null || items.isEmpty()) return;

                if (items.size() == 1 && menu.isExecuteOnSingleResult()) { 
                    Object o = menu.getFirst(); 
                    if (o instanceof Opener) 
                        getBinding().fireNavigation((Opener)o); 
                    else 
                        ((Action)o).execute(); 
                } 
                else { 
                    EventQueue.invokeLater(new Runnable() {
                        public void run() { 
                            show(menu); 
                        } 
                    }); 
                }
            } else { 
                getBinding().fireNavigation((Opener)outcome); 
            } 
        } 
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" ViewProviderImpl (class) "> 
    
    private class ViewProviderImpl implements HtmlViewModel.ViewProvider 
    {        
        XHtmlView root = XHtmlView.this;
        
        public void insertText(String text) {}
        public void appendText(String text) {} 

        public String getText() { return root.getText(); } 
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
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" URLWorkerTask (class) "> 
    
    private class URLWorkerTask extends Task 
    { 
        XHtmlView root = XHtmlView.this;
        
        private URL url;
        private boolean done;
        
        URLWorkerTask(URL url) {
            this.url = url; 
        }
        
        public boolean accept() {
            return (done? false: true); 
        }

        public void execute() {
            try {
                root.setText("<html><body><b>Loading...</b></body></html>");
                if (url != null) root.setPage(url); 
                
                root.setCaretPosition(0);
            } catch(Throwable t) { 
                root.setText("<html><body color=\"red\">error caused by "+t.getMessage()+"</body></html>"); 
                
                if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
            } finally { 
                done = true;                 
            } 
        }    
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PopupMenu Support ">
    
    private JPopupMenu popup;
    
    protected void show(PopupMenuOpener menu) {
        if (popup == null)
            popup = new JPopupMenu();
        else
            popup.setVisible(false);
        
        popup.removeAll();
        for (Object o: menu.getItems()) {
            ActionMenuItem ami = null;
            if (o instanceof Opener)
                ami = new ActionMenuItem((Opener)o);
            else
                ami = new ActionMenuItem((Action)o);
            
            Dimension dim = ami.getPreferredSize();
            ami.setPreferredSize(new Dimension(Math.max(dim.width, 100), dim.height));
            popup.add(ami);
        }
        popup.pack();
        
        Rectangle rect = XHtmlView.this.getBounds();
        int x = rect.x + (mousePoint == null? 0: mousePoint.x); 
        int y = rect.y + (mousePoint == null? 0: mousePoint.y); 
        popup.show(XHtmlView.this, x, y);
        popup.requestFocus();
        mousePoint = null; 
    }
    
    private class ActionMenuItem extends JMenuItem
{
        XHtmlView root = XHtmlView.this;
        private Object source;
        
        ActionMenuItem(Opener anOpener) {
            this.source = anOpener;
            setText(anOpener.getCaption());
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    invokeOpener(e);
                }
            });
            
            Object ov = anOpener.getProperties().get("mnemonic");
            if (ov != null && ov.toString().trim().length() > 0)
                setMnemonic(ov.toString().trim().charAt(0));
            
            ov = anOpener.getProperties().get("icon");
            if (ov != null && ov.toString().length() > 0)
                setIcon(ImageIconSupport.getInstance().getIcon(ov.toString()));
        }
        
        ActionMenuItem(Action anAction) {
            this.source = anAction;
            setText(anAction.getCaption());
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    invokeAction(e);
                }
            });
            
            setMnemonic(anAction.getMnemonic());
            
            String sicon = anAction.getIcon();
            if (sicon != null && sicon.length() > 0)
                setIcon(ImageIconSupport.getInstance().getIcon(sicon));
        }
        
        void invokeOpener(ActionEvent e) {
            try {
                UICommandUtil.processAction(root, root.getBinding(), (Opener) source);
            } catch(Exception ex) {
                MsgBox.err(ex);
            }
        }
        
        void invokeAction(ActionEvent e) {
            try {
                Object outcome = ((Action) source).execute();
                if (outcome instanceof Opener)
                    UICommandUtil.processAction(root, root.getBinding(), (Opener)outcome);
            } catch(Exception ex) {
                MsgBox.err(ex);
            }
        }
    }
    
    // </editor-fold>        
}
