/*
 * XEditorPane.java
 *
 * Created on October 6, 2010, 9:39 AM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.WebBrowserModel;
import com.rameses.rcp.control.webbrowser.WebEditorKit;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

public class XWebBrowser extends JEditorPane implements UIControl, ActiveControl  
{    
    private Binding binding;
    private String[] depends;
    private int index;
    private boolean refreshed;
    
    private int stretchWidth;
    private int stretchHeight;   
    private String visibleWhen;
    
    private WebBrowserModel model;
        
    public XWebBrowser() {
        super();
        
        if ( Beans.isDesignTime() ) {
            setContentType("text/html");
        }
        
        super.setEditable(false);
        
        attachEventsListeners();
    }
    
    //<editor-fold defaultstate="collapsed" desc="  helper methods  ">
    private void attachEventsListeners() {
        addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                processHyperlinkEvent(e);
            }
        });
        
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( model != null ) model.back();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), JComponent.WHEN_FOCUSED);
        
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( model != null ) model.forward();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 1), JComponent.WHEN_FOCUSED);
    }
    
    private void processHyperlinkEvent(HyperlinkEvent e) {
        EventType evt = e.getEventType();
        if (evt == EventType.ACTIVATED) {
            try {
                String desc = e.getDescription();
                if ( !ValueUtil.isEmpty(desc) )  {
                    desc = desc.trim();
                    //process reference
                    if ( desc.startsWith("#") ) {
                        if ( desc.length() > 1)
                            model.setLocation( desc );
                    }
                    //process url link
                    else {
                        if ( desc.startsWith("http") || desc.startsWith("www.") ) {
                            model.setLocation( desc );
                        } else {
                            model.setRelativeLocation( desc );
                        }
                    }
                }
            } catch(Exception ex){
                MsgBox.err(new IllegalStateException(ex));
            }
        }
    }
    //</editor-fold>
    
    
    public void refresh() {
        //refresh only on the first display
        //next refresh can be done in the model (model.refresh())
        if( !refreshed ) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    refreshContent();
                }
            });
        }
        
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }        
    }
    
    public void load() {
        model = (WebBrowserModel) UIControlUtil.getBeanValue(this);
        setEditorKit(new WebEditorKit( model.getCacheContext() ));
        model.setListener(new WebBrowserModel.Listener() {
            
            public void refresh() {
                refreshContent();
            }
            
        });
    }
    
    private void refreshContent() {
        try {
            if ( ValueUtil.isEqual(super.getPage(), model.getLocation()) ) {
                //force reload
                Document doc = getDocument();
                doc.putProperty(Document.StreamDescriptionProperty, null);
            }
            
            URL currentLoc = super.getPage();
            URL newLoc = model.getLocation();
            String extForm = newLoc.toExternalForm();
            int hashIdx = -1;
            
            if ( (hashIdx = extForm.indexOf("#")) != -1 ) {
                String[] ss = extForm.split("#");
                if ( currentLoc != null && currentLoc.toExternalForm().split("#")[0].equals(ss[0]) )
                    super.scrollToReference( ss[1] );
                else
                    super.setPage( newLoc );
                
            } else {
                super.setPage( newLoc );
            }
            
            binding.notifyDepends(this);
            
        } catch (Exception ex) {
            MsgBox.err(ex);
        }
        refreshed = true;
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    

    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public void setName(String name) {
        super.setName(name);
        if ( Beans.isDesignTime() ) {
            setText(name);
        }
    }
    
    public void setEditable(boolean editable) {}
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public void setBinding(Binding binding) { this.binding = binding; }
    public Binding getBinding() { return binding; }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
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
    
}
