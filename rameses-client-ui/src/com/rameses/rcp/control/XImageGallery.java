/*
 * XImageGallery.java
 *
 * Created on April 21, 2014, 2:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.ImageGalleryModel;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.image.ThumbnailItem;
import com.rameses.rcp.control.image.ThumbnailListModel;
import com.rameses.rcp.control.image.ThumbnailPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wflores
 */
public class XImageGallery extends ThumbnailPanel implements UIControl, ActiveControl 
{
    private Binding binding;
    private String[] depends;
    private int index;

    private String handler;    
    private String visibleWhen;
    private String enabledWhen;
    private boolean dynamic;
    private int stretchWidth;
    private int stretchHeight;     
    
    private ImageGalleryModel model; 
            
    public XImageGallery() {
        super();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public String getEnabledWhen() { return enabledWhen; } 
    public void setEnabledWhen(String enabledWhen) {
        this.enabledWhen = enabledWhen; 
    } 
        
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    }
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding; 
    }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex(int index) {
        this.index = index; 
    }

    public void load() {
        Object bean = getBinding().getBean();
        Object ohandler = UIControlUtil.getBeanValue(bean, getHandler()); 
        if (ohandler instanceof ImageGalleryModel) { 
            model = (ImageGalleryModel)ohandler; 
        } else { 
            model = new DefaultImageGalleryModel(); 
        } 
        model.setProvider(new DefaultImageGalleryModelProvider()); 
    }

    private boolean list_loaded;
    
    public void refresh() {
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

        try { 
            String enabledWhen = getEnabledWhen(); 
            if (enabledWhen != null && enabledWhen.length() > 0) { 
                Object bean = getBinding().getBean();
                try { 
                    boolean b = UIControlUtil.evaluateExprBoolean(bean, enabledWhen);
                    setEnabled(b); 
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
            } 
        } catch(Throwable t) {;} 
        
        if (!list_loaded || isDynamic()) {
            refreshImages();
            list_loaded = true; 
        } 
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
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
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    private void refreshImages() { 
        if (model == null) return;
        
        Map params = new HashMap(); 
        Map query = model.getQuery(); 
        if (query != null) params.putAll(query); 
        
        int rows = model.getRows();
        params.put("_limit", (rows > 0? rows: 10)); 
        params.put("_start", 0);
        
        ThumbnailListModel lm = new ThumbnailListModel();
        List list = model.fetchList( params ); 
        if ( list != null ) {
            for (Object item : list) { 
                Map map = (Map) item; 
                lm.add( map ); 
            } 
        }
        
        setModel( lm ); 
        
        Object item = getSelectedItem(); 
        if (item == null) selectFirstItem(); 

        final Runnable task = new Runnable() {
            public void run() {
                revalidate(); 
                repaint();
            }
        };
        
        
    }

    protected void selectionChanged() {
        super.selectionChanged();
        
        ThumbnailItem item = getSelectedItem(); 
        Object data = (item == null ? null : item.getData()); 
        EventQueue.invokeLater( new OnSelectTask( data )); 
    } 
    
    private class OnSelectTask implements Runnable { 
    
        XImageGallery root = XImageGallery.this;
        
        private Object data;
        
        OnSelectTask( Object data ) {
            this.data = data; 
        }
        
        public void run() { 
            Binding binding = getBinding();
            Object bean = binding.getBean();
            String sname = getName();
            if (bean == null || sname == null) return;

            UIControlUtil.setBeanValue(bean, sname, this.data );  
            Object outcome = (model == null? null: model.onselect( this.data ));
            binding.notifyDepends( sname ); 
            if (outcome instanceof Opener) { 
                binding.fireNavigation(outcome); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultImageGalleryModel ">
    
    private class DefaultImageGalleryModel extends ImageGalleryModel {        
    }
    
    private class DefaultImageGalleryModelProvider implements ImageGalleryModel.Provider {
        
        XImageGallery root = XImageGallery.this;

        public Object getBinding() {
            return root.getBinding(); 
        }
        
        public void reload() {
            refreshImages(); 
        }

        public void refresh() {
            root.refresh(); 
        }        
        
        public void moveNext() {
            root.moveNext();
        }

        public void movePrevious() {
            root.movePrevious(); 
        }

        public void remove(int index) { 
            int count = root.getModel().getSize(); 
            if ( count <= 0 ) return;
            
            root.getModel().remove( index ); 
            count = root.getModel().getSize(); 
            if ( index >= count ) index -= 1; 
            if ( index >= 0 && index < root.getModel().getSize()) {
                root.setSelectedIndex( index );
            }
        }
    }
    
    // </editor-fold>
    
}
