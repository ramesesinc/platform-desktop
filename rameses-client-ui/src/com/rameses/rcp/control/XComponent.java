/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport.PropertyInfo;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UICommand;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
public class XComponent extends JPanel implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo {

    protected Binding binding;

    private ControlProperty property;
    private String[] depends;
    private String visibleWhen;
    private boolean dynamic; 
    private int index; 
    
    private int stretchWidth;
    private int stretchHeight;     
    
    private String dataPrefix; 
    private String actionPrefix; 
    
    public void init() {
    }
    public void registerControl( UIControl uic ) {
    }
    public void unregisterControl( UIControl uic ) {
    }    
    public void afterLoad() {
    }
    public void afterRefresh() {
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl ">
    
    public Binding getBinding() { return binding; }
    public final void setBinding(Binding binding) { 
        this.binding = binding; 
        //
        // initialize this component after Binding is set. 
        //
        init(); 
        //
        // register all components attached to this container 
        //
        registerComponents( this, binding ); 
    } 

    public String[] getDepends() { return depends; } 
    public void setDepends( String[] depends ) { 
        this.depends = depends; 
    } 

    public int getIndex() { return index; }
    public void setIndex( int index ) {
        this.index = index; 
    }

    public final void load() { 
        //
        // invoke a callback method after the component has been loaded 
        //
        afterLoad(); 
    }

    public final void refresh() {  
        String expr = getVisibleWhen(); 
        if (expr != null && expr.length() > 0) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(getBinding().getBean(), expr);
            } catch(Throwable t) {
                t.printStackTrace();
            } 
            setVisible(result); 
        } 
        //
        // invoke a callback method after the component has been refreshed
        //        
        afterRefresh(); 
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

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o); 
    } 
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl ">

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
    
    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo ">

    public Map getInfo() { 
        HashMap map = new HashMap(); 
        map.put("bean", getBean());
        map.put("actionPrefix", getActionPrefix());
        map.put("dataPrefix", getDataPrefix()); 
        map.put("visibleWhen", getVisibleWhen()); 
        return map;
    }
    
    // </editor-fold> 
        
    // <editor-fold defaultstate="collapsed" desc=" Other properties ">
            
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
   
    public Object getProperty( String name ) { 
        Binding binding = getBinding(); 
        if ( binding == null ) return null; 
        
        return UIControlUtil.getBeanValue( binding, name ); 
    }
    
    public Object getBean() {
        Binding binding = getBinding(); 
        return (binding == null? null: binding.getBean()); 
    }
    
    public String getDataPrefix() { return dataPrefix; } 
    public void setDataPrefix( String dataPrefix ) {
        this.dataPrefix = dataPrefix; 
        firePrefixChanged( getDataPrefix(), getActionPrefix(), this ); 
    }

    public String getActionPrefix() { return actionPrefix; } 
    public void setActionPrefix( String actionPrefix ) {
        this.actionPrefix = actionPrefix; 
        firePrefixChanged( getDataPrefix(), getActionPrefix(), this ); 
    }
    
    // </editor-fold> 

    private String getComponentName( UIControl uic ) { 
        String keyword = "XComponent.UIControl.name"; 
        String[] props = (String[]) uic.getClientProperty( keyword ); 
        if ( props == null ) { 
            props = new String[]{ uic.getName() }; 
            uic.putClientProperty( keyword, props ); 
        } 
        return props[0]; 
    }
    private void registerComponents( Container cont, Binding binding ) {
        for ( Component c: cont.getComponents()) { 
            if ( c instanceof UIControl ) { 
                UIControl uic = (UIControl)c; 
                if ( binding == null ) { 
                    uic.setBinding( binding ); 
                    binding.unregister( uic ); 
                    // 
                    // invoke a callback method after a control has been unregistered 
                    // 
                    unregisterControl( uic );
                } else { 
                    binding.bind( uic ); 
                    // 
                    // invoke a callback method before registering this control
                    // 
                    registerControl( uic ); 
                    binding.register( uic ); 
                }
                
                if( c instanceof ControlContainer && ((ControlContainer) c).isHasNonDynamicContents() && c instanceof Container ) {
                    registerComponents( (Container)c, binding );
                } 
            } else if( c instanceof Container ) {
                registerComponents( (Container)c, binding );
            }
        }
    }
    private void firePrefixChanged( String dataPrefix, String actionPrefix, Container cont ) {
        for ( Component c: cont.getComponents()) { 
            if ( c instanceof UIControl ) { 
                UIControl uic = (UIControl)c; 
                String sname = getComponentName( uic );
                if ( uic instanceof UICommand ) {
                    configureUI(c, sname, actionPrefix);
                } else {
                    configureUI(c, sname, dataPrefix);
                }
                
                if( c instanceof ControlContainer && ((ControlContainer) c).isHasNonDynamicContents() && c instanceof Container ) {
                    firePrefixChanged( dataPrefix, actionPrefix, (Container)c );
                } 
            } else if ( c instanceof Container ) {
                firePrefixChanged(dataPrefix, actionPrefix, (Container)c);
            } 
        } 
    } 

    private void configureUI(Component c, String sname, String prefix) {
        String newname = join(new String[]{prefix,sname}, "."); 
        if ( newname == null ) {
            c.setName( null ); 
        } else {
            c.setName( newname ); 
        } 
    }
    private String join(String[] values, String delim) {
        StringBuilder sb = new StringBuilder();
        for ( String sval : values ) {
            if ( sval == null || sval.trim().length()==0 ) continue; 
            if ( sb.length() > 0 ) sb.append(delim); 

            sb.append( sval ); 
        }
        String text = sb.toString(); 
        if ( text == null || text.trim().length()==0 ) {
            return null; 
        } else {
            return text; 
        }
    }
}
