package system.controller;

import com.rameses.platform.interfaces.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.osiris2.client.*;
import com.rameses.util.*;

public class SuspendController 
{
    @Script("User")
    def user;
    
    def pwd;
    boolean canclose;

    def init() {
        if (!user?.sessionId) return '_close'; 
        ClientContext.currentContext.platform.lock();
        return null; 
    }

    @Close
    public boolean canClose() {
        return canclose;
    }

    def resetHandler;    
    public def resume() {
        user.checkPwd(pwd);
        ClientContext.currentContext.platform.unlock();
        if (resetHandler) resetHandler();
        
        canclose = true;
        return "_exit";
    }

    public void logoff() {
        def ctx = ClientContext.currentContext; 
        def handler = ctx.properties.logoutHandler; 
        if (handler != null && !handler.confirmLogoff()) return; 
        
        canclose = true;
        ctx.platform.logoff();
        ctx.headers.clear(); 
        ctx.taskManager.stop();
    }

    public void exit() {
        def ctx = ClientContext.currentContext; 
        def handler = ctx.properties.logoutHandler; 
        if (handler != null && !handler.confirmExit()) return; 
        
        canclose = true;
        ctx.platform.shutdown(); 
        ctx.headers.clear(); 
    }
}
