package com.rameses.rcp.common;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.util.MessageUtil;
import java.awt.EventQueue;

public final class MsgBox {
    
    private MsgBox() {}
    
    //another option
    public static void alert(Object msg) {
        ClientContext.getCurrentContext().getPlatform().showInfo(null, msg);
    }
    
    //another option
    public static void alert(final Object msg, boolean invokeLater) {
        Runnable runnable = new Runnable() {
            public void run() {
                ClientContext.getCurrentContext().getPlatform().showInfo(null, msg); 
            }
        };
        
        if (invokeLater) {
            EventQueue.invokeLater(runnable); 
        } else { 
            runnable.run();
        }
    }    
    
    public static void err(Exception e) { 
        Exception cause = MessageUtil.getErrorMessage(e); 
        String errmsg = cause.getMessage()+""; 
        int idx = errmsg.indexOf("Exception:"); 
        if ( idx > 0 ) {
            errmsg = errmsg.substring(idx+10); 
        }
        ClientContext.getCurrentContext().getPlatform().showError(null, new Exception(errmsg,e));
    }
    
    public static void err( Exception e, String message ) { 
        Exception cause = MessageUtil.getErrorMessage(e); 
        String errmsg = message; 
        if ( message == null ) { 
            errmsg = cause.getMessage()+""; 
        } 
        int idx = errmsg.indexOf("Exception:"); 
        if ( idx > 0 ) {
            errmsg = errmsg.substring(idx+10); 
        }
        ClientContext.getCurrentContext().getPlatform().showError(null, new Exception(errmsg,e));
    }
    
    public static void err( Object message ) { 
        if ( message instanceof Throwable ) {
            Throwable t = (Throwable) message; 
            err( new Exception( t.getMessage(), t) ); 
            
        } else {
            String errmsg = (message==null? "null": message.toString());
            int idx = errmsg.indexOf("Exception:"); 
            if ( idx > 0 ) {
                errmsg = errmsg.substring(idx+10); 
            }
            ClientContext.getCurrentContext().getPlatform().showError(null, new Exception(errmsg));
        }        
    }
    
    public static void warn(Object msg) {
        ClientContext.getCurrentContext().getPlatform().showAlert(null, msg);
    }
    
    
    public static boolean confirm(Object msg) {
        return ClientContext.getCurrentContext().getPlatform().showConfirm(null, msg);
    }
    
    public static String prompt(Object msg) {
        Object x= ClientContext.getCurrentContext().getPlatform().showInput(null, msg);
        if(x==null) return null;
        return x+"";
    }
    
}
