/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.util;

import com.rameses.rcp.common.StyleRule;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.StyleRuleParser;
import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.annotations.StyleSheet;
import java.awt.Component;
import java.awt.Container;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores 
 */
public final class UIHelper {
    
    private final static UIHelper instance = new UIHelper();
    
    public static synchronized UIHelper getInstance() { 
        return instance; 
    } 
    public static synchronized UIHelper newInstance() {
        return new UIHelper(); 
    } 
    
        
    
    // <editor-fold defaultstate="collapsed" desc=" BindingHelper ">
    
    public static class BindingHelper { 
        
        public final static BindingHelper instance = new BindingHelper(); 
        
        public void beforeRegister( UIControl uic ) {
        }
        public void afterRegister( UIControl uic ) {
        }

        public void beforeUnregister( UIControl uic ) {
        }
        public void afterUnregister( UIControl uic ) {
        }

        public void bind( Binding binding, Container container ) {
            Component[] comps = container.getComponents(); 
            for ( Component c : comps ) { 
                bind( binding, c ); 
            } 
        }

        public void bind( Binding binding, Component c ) {
            if ( c instanceof UIControl ) { 
                UIControl uic = (UIControl) c; 
                if ( binding == null ) {
                    unbind(binding, c) ; 
                } else { 
                    binding.bind( uic ); 
                    beforeRegister( uic );
                    binding.register( uic ); 
                    afterRegister( uic ); 
                } 

                if( c instanceof ControlContainer && ((ControlContainer) c).isHasNonDynamicContents() && c instanceof Container ) {
                    bind( binding, (Container)c);
                }
            } else if( c instanceof Container ) {
                bind( binding, (Container)c); 
            } 
        } 

        public void unbind( Binding binding, Component comp ) {
            if ( comp instanceof UIControl ) {
                UIControl uic = (UIControl)comp; 
                try { 
                    uic.setBinding( null ); 
                } catch(Throwable t) {;} 
                
                beforeUnregister( uic ); 
                binding.unregister( uic ); 
                afterUnregister( uic ); 
            } 
        } 
        
        public void loadStyleRule( Binding binding, Class pageClass ) {
            if ( !pageClass.isAnnotationPresent(StyleSheet.class) ) return;

            List<String> sources = new ArrayList();
            StyleSheet ss = (StyleSheet) pageClass.getAnnotation(StyleSheet.class);
            String source = ss.value();
            if(source.trim().length()>0) {
                if ( source.indexOf(",") > -1 ) {
                    for (String s: source.split("\\s*,\\s*")) {
                        sources.add( s );
                    }
                } else {
                    sources.add( source );
                }
            }

            List<StyleRule> newRules = new ArrayList();
            ClassLoader loader = ClientContext.getCurrentContext().getClassLoader();

            InputStream is = null;
            if( sources.size() > 0 ) {
                for ( String s : sources ) {
                    is = loader.getResourceAsStream(s);
                    List<StyleRule> sr = getStyles(is);
                    if ( sr.size() > 0 ) {
                        newRules.addAll( sr );
                    }
                }
            } else {
                is = pageClass.getResourceAsStream(pageClass.getSimpleName()+".style");
                List<StyleRule> sr = getStyles(is);
                if ( sr.size() > 0 ) {
                    newRules.addAll( sr );
                }
            }

            //if ( newRules.size() == 0 ) return;

            StyleRule[] oldRules = binding.getStyleRules();
            List list = new ArrayList();
            if(oldRules!=null) {
                for(StyleRule s : oldRules) {
                    list.add(s);
                }
            }

            for(Object s: newRules) {
                list.add((StyleRule)s);
            }
            StyleRule[] sr =(StyleRule[]) list.toArray(new StyleRule[]{});
            binding.setStyleRules(sr); 
        } 
        
        public List<StyleRule> getStyles( InputStream inp ) {
            if( inp != null ) {
                try {
                    StyleRuleParser parser = new StyleRuleParser();
                    StyleRuleParser.DefaultParseHandler handler = new StyleRuleParser.DefaultParseHandler();
                    parser.parse(inp, handler);
                    return handler.getList();
                } catch (Throwable t) {
                    //do nothing 
                } finally {
                    try { inp.close(); } catch(Throwable t){;}
                }
            } 
            return new ArrayList();
        }
    } 
    
    // </editor-fold>
} 
