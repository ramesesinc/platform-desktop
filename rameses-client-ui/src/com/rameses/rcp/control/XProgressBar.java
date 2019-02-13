package com.rameses.rcp.control;

import com.rameses.rcp.common.ProgressModel;
import com.rameses.rcp.common.PropertySupport.PropertyInfo;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.Beans;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JProgressBar;

public class XProgressBar extends JProgressBar 
    implements UIControl, MouseEventSupport.ComponentInfo, ActiveControl  
{ 
    private Binding binding;
    private String[] depends;
    private int index;
    private int stretchWidth;
    private int stretchHeight; 
    private String visibleWhen; 
    private String disableWhen;
    
    private String handler;
    private ProgressModel progmodel;
    
    public XProgressBar() { 
        initComponent();
    }
  
    // <editor-fold defaultstate="collapsed" desc=" initComponent ">
    
    private void initComponent() {
        setPreferredSize(new Dimension(100, 20)); 
        setStringPainted( true ); 
        new MouseEventSupport(this).install(); 
        
        if (Beans.isDesignTime()) {
            setMinimum(0);
            setMaximum(100);
            setValue(75); 
        }
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" Properties ">
    
    public String getHandler() { return handler; } 
    public void setHandler( String handler ) {
        this.handler = handler;
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl ">
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public String[] getDepends() { return depends; }
    public void setDepends( String[] depends ) {
        this.depends = depends; 
    }

    public int getIndex() { return index; }
    public void setIndex( int index ) {
        this.index = index; 
    }

    public void load() {
        Object handlerObj = null; 
        String handler = getHandler(); 
        if ( handler != null && handler.trim().length() > 0 ) {
            handlerObj = UIControlUtil.getBeanValue(getBinding(), handler); 
        } 
        
        if ( progmodel != null ) { 
            progmodel.setProvider( null ); 
        } 
        
        if ( handlerObj instanceof ProgressModel ) {
            progmodel = (ProgressModel) handlerObj; 
        } else {
            progmodel = new ProgressModel(); 
        }
                
        progmodel.setProvider( new ProviderImpl()); 
    }

    public void refresh() { 
        int min = Math.max(progmodel.getMinValue(), 0);
        int max = progmodel.getMaxValue(); 
        max = (max > 0 ? max : (min+1));
        setMinimum( min ); 
        setMaximum( max ); 
        setValue( progmodel.getValue() ); 
         
        String expr = getDisableWhen();
        if ( expr != null && expr.length() > 0 ) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(binding.getBean(), expr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setEnabled( result ); 
        }
        
        expr = getVisibleWhen();
        if ( expr != null && expr.length() > 0 ) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(binding.getBean(), expr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }
    }

    public void setPropertyInfo(PropertyInfo info) {
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
    public void setVisibleWhen(String visibleWhen) { 
        this.visibleWhen = visibleWhen; 
    }

    public String getDisableWhen() { return disableWhen; }
    public void setDisableWhen(String disableWhen) { 
        this.disableWhen = disableWhen; 
    }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo ">
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("handler", getHandler()); 
        map.put("disableWhen", getDisableWhen());
        map.put("visibleWhen", getVisibleWhen()); 
        return map;
    }
    
    // </editor-fold>     

    // <editor-fold defaultstate="collapsed" desc=" ActiveControl ">
    
    private ControlProperty property; 
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        }
        return property; 
    }
    
    public boolean isRequired() { 
        return getControlProperty().isRequired(); 
    }    
    public void setRequired(boolean required) {
        getControlProperty().setRequired( required ); 
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

    // <editor-fold defaultstate="collapsed" desc=" Model Provider ">

    private class ProviderImpl implements ProgressModel.Provider {
        
        XProgressBar root = XProgressBar.this; 
        
        public Object getBinding() { 
            return root.getBinding(); 
        } 

        public void adjustValues( int min, int max, int value ) {
            min = Math.max(min, 0); 
            max = (max > 0 ? max : (min+1)); 
            value = Math.max(value, 0); 
            
            root.setMinimum(min);
            root.setMaximum(max);
            root.setValue(value); 
            root.repaint();
        }
        
        public void adjustText( String text ) { 
            if ( text != null ) {
                root.setString( text ); 
                root.repaint(); 
            }
        }
    }

    // </editor-fold>     
}
