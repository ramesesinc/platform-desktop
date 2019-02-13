package com.rameses.rcp.impl;

import com.rameses.platform.interfaces.Platform;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.rcp.common.*;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.framework.*;
import com.rameses.rcp.ui.UICommand;
import com.rameses.rcp.ui.UIControl;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.util.*;
import javax.swing.JComponent;

/**
 * @author jaycverg
 * handles page navigation using the actions outcome
 */
public class NavigationHandlerImpl implements NavigationHandler {
    
    
    public void navigate(NavigatablePanel panel, UIControl source, Object outcome) {
        if ( panel == null ) return;
        
        JComponent sourceComp = (JComponent) source;
        ClientContext ctx = ClientContext.getCurrentContext();
        Platform platform = ctx.getPlatform();
        
        Stack<UIControllerContext> conStack = panel.getControllers();
        UIControllerContext curController = conStack.peek();
                
        if ( ValueUtil.isEmpty(outcome) ) {
            // if outcome is null or empty just refresh the current view 
            boolean autorefresh = true; 
            if ( source instanceof UICommand ) {
                autorefresh = ((UICommand) source).isAutoRefresh(); 
            }
            if ( autorefresh ) curController.getCurrentView().refresh();
            
        }
        else {
            //-- process Opener outcome
            if ( outcome instanceof Opener )  {
                Opener opener = (Opener) outcome;
                opener = ControlSupport.initOpener( opener, curController.getController() );
                if ( opener instanceof PopupMenuOpener ) {
                    source.putClientProperty(PopupMenuOpener.class, opener);
                    return; 
                }
                
                String opTarget = opener.getTarget()+"";
                if (opTarget.matches("process|_process")) return;
                
                if (opTarget.startsWith("_")) 
                    opTarget = opTarget.substring(1);

                boolean self = !opTarget.matches("topwindow|window|popup|floating|popuppanel|popupmenu");
                String windowId = opener.getController().getId();

                if ( !self && platform.isWindowExists( windowId ) ) {
                    platform.activateWindow( windowId );
                    return;
                }
                
                UIController opCon = opener.getController();
                String permission = opener.getPermission();
                String role = opener.getRole();
                String domain = opener.getDomain();

                //check permission(if specified) if allowed
                if ( !ValueUtil.isEmpty(permission) ) {
                    permission = opCon.getName() + "." + permission;
                    if( !ControlSupport.isPermitted(domain, role, permission) ) {
                        MsgBox.err("You don't have permission to perform this transaction.");
                        return;
                    }
                    
                }
                
                UIControllerContext controller = new UIControllerContext(opCon);
                
                //check if opener has outcome
                if ( !ValueUtil.isEmpty(opener.getOutcome()) ) {
                    if ( "_close".equals( opener.getOutcome()) ) {
                        curController.getCurrentView().refresh();
                        return;
                    }
                    //check if controller has a page reolver
                    controller.setCurrentView( opener.getOutcome() );
                }
                
                if ( self ) {
                    conStack.push(controller);
                } 
                else {
                    UIControllerPanel uic = new UIControllerPanel(controller);
                    
                    Map props = new HashMap();
                    if ( opener.getProperties().size() > 0 ) {
                        props.putAll( opener.getProperties() );
                    }
                                        
                    props.put("id", windowId);
                    props.put("title", controller.getTitle() );
                    props.put("modal", opener.isModal()); 
                    uic.putClientProperty("Opener.properties", props); 
                    
                    if ( "popuppanel".equals(opTarget)) {
                        ContentLayer cpl = findTopContentLayer( sourceComp ); 
                        if ( cpl == null ) {
                            System.out.println("Cannot preview opener popuppanel. No available ContentPane.Layer"); 
                            return; 
                        }
                        
                        cpl.show( uic, props ); 
                        return; 
                    }
                    
                    if ( "popupmenu".equals(opTarget)) { 
                        WindowMenu.show(sourceComp, uic);
                        return; 
                    }
                    
                    if ( "popup".equals(opTarget) ) {
                        platform.showPopup(sourceComp, uic, props);
                        
                    } else if ( opener instanceof FloatingOpener ) {
                        FloatingOpener fo = (FloatingOpener) opener;
                        JComponent owner = (JComponent) source.getBinding().find( fo.getOwner() );
                        if ( !ValueUtil.isEmpty(fo.getOrientation()) ) 
                            props.put("orientation", fo.getOrientation());
                        
                        platform.showFloatingWindow(owner, uic, props);
                        
                    } else {
                        platform.showWindow(sourceComp, uic, props);
                    }
                    
                    return;
                }
            }
            //-- process String outcome
            else {
                String out = outcome+"";
                if ( out.startsWith("_close") ) { 
                    if ( conStack.size() > 1 ) {
                        conStack.pop(); 
                        
                        if( out.contains(":") ) {
                            out = out.substring(out.indexOf(":")+1);
                            navigate(panel, source, out);
                            return;
                        }
                    } else {
                        Object h = panel.getClientProperty(SubWindow.class); 
                        if ( h instanceof SubWindow ) { 
                            ((SubWindow) h).closeWindow(); 
                            return; 
                        }
                        
                        String conId = (curController==null? null : curController.getId());
                        if ( conId != null ) platform.closeWindow( conId );  
                        
                        conId = (String) panel.getClientProperty( NavigatablePanel.PROPERTY_ID ); 
                        if ( conId != null ) platform.closeWindow( conId ); 
                    }
                    
                } else if ( out.startsWith("_exit")) {
                    //get the original owner of he window
                    while ( conStack.size() > 1 ) {
                        conStack.pop();
                    }
                    String conId = conStack.peek().getId();
                    platform.closeWindow(conId);
                    
                } else if ( out.startsWith("_root") ) {
                    //get the original owner of he window
                    while ( conStack.size() > 1 ) {
                        conStack.pop();
                    }
                    if( out.contains(":") ) {
                        out = out.substring(out.indexOf(":")+1);
                        navigate(panel, source, out);
                        return;
                    }
                    
                } else {
                    if ( out.startsWith("_") ) out = out.substring(1);
                    curController.setCurrentView( out );
                    
                    //update binding injections based on current view
                    curController.getCurrentView().getBinding().reinjectAnnotations();
                }
            }
            
            //refresh new view
            panel.renderView();
            
            if ( panel.getControllers().size() <= 0 ) { 
                String pid = (String) panel.getClientProperty( NavigatablePanel.PROPERTY_ID ); 
                if ( pid != null ) platform.closeWindow( pid ); 
            } 
        } 
    }
    
    private ContentLayer findTopContentLayer( JComponent comp ) { 
        ContentLayer cpl = null; 
        if ( comp == null ) return cpl;
        
        Container parent = comp; 
        while ( parent != null ) { 
            if ( parent instanceof ContentLayer ) { 
                cpl = (ContentLayer) parent; 
            } 
            parent = parent.getParent(); 
        }
        return cpl; 
    }
}
