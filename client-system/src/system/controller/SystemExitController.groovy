package system.controller;

import com.rameses.platform.interfaces.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;

public class SystemExitController 
{
    @Script("User")
    def user;
    
    public void logoff() {
        def ctx = ClientContext.currentContext; 
        def handler = ctx.properties.logoutHandler; 
        if (handler != null && !handler.confirmLogoff()) return; 
        
        ctx.platform.logoff();
        ctx.headers.clear(); 
        ctx.taskManager.stop();
    }

    public void restart() {
        def ctx = ClientContext.currentContext; 
        def handler = ctx.properties.logoutHandler; 
        if (handler != null && !handler.confirmRestart()) return; 
        
        ctx.platform.logoff();
        ctx.headers.clear(); 
        ctx.taskManager.stop();
    }

    public def shutdown() {
        def ctx = ClientContext.currentContext; 
        def handler = ctx.properties.logoutHandler; 
        if (handler != null && !handler.confirmExit()) return; 
        
        ctx.platform.shutdown();
        ctx.headers.clear(); 
    }
}
