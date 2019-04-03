/*
 * HtmlViewModel.java
 *
 * Created on August 27, 2013, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class HtmlViewModel extends DocViewModel {
    
    private Object we;
    
    public HtmlViewModel() {
    }
    
    public Object getWebEngine() { return we; } 
    public void setWebEngine( Object we ) {
        this.we = we; 
    }

    public String getStyles() {
        return null; 
    }
    
    public void onCompleted() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ViewProvider interface "> 
    
    public static interface ViewProvider extends DocViewModel.Provider {
        
    }
    
    // </editor-fold>
}
