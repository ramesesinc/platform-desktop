package system.controller;

import com.rameses.osiris2.client.*;
import com.rameses.platform.interfaces.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.util.*;

public class LogoutController 
{
    @Script("User")
    def user;

    public void init() {
        if (user.sessionId) { 
            def service = InvokerProxy.instance.create("LogoutService"); 
            def handler = new LogoutHandler(service: service, user: user); 
            OsirisContext.clientContext.properties.logoutHandler = handler; 
            OsirisContext.mainWindowListener.add(handler); 
        }
    }
}

public class LogoutHandler implements MainWindowListener 
{
    def service;
    def user;
    
    public Object onEvent(String eventName, Object evt) {
        return null;
    }
    
    public boolean confirmLogoff() {
        return MsgBox.confirm("Are you sure you want to logout this application?");         
    }

    public boolean confirmRestart() {
        return MsgBox.confirm("Are you sure you want to restart the application?");         
    }

    public boolean confirmExit() {
        return MsgBox.confirm("Are you sure you want to exit the application?");         
    } 
    
    public boolean onClose() {
        if (service) service.logout( user.env );

        return true;
    }
}
