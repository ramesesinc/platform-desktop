package system.controller;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.util.*;
import com.rameses.osiris2.client.*;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import com.rameses.rcp.framework.ClientContext;

public class SuspendTimerController implements AWTEventListener {

    def _currentTask; 

    public def init() { 
        _currentTask = createTask(); 

        Toolkit.defaultToolkit.addAWTEventListener( this, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK );
        ClientContext.currentContext.taskManager.addTask( _currentTask ); 
        return "_close";
    }

    public void eventDispatched(AWTEvent event) { 
        if ( _currentTask ) _currentTask.reset(); 
    } 

    def resetHandler = {
        if ( _currentTask ) {
            _currentTask.setCancelled( true ); 
        } 
        _currentTask = createTask(); 
        ClientContext.currentContext.taskManager.addTask( _currentTask ); 
    } 

    void showLockScreen() {
        try { 
            def invoker = InvokerUtil.lookup("system:suspend")[0];
            InvokerUtil.invoke(invoker, [resetHandler: resetHandler]); 
        } 
        catch(Warning w) { 
            OsirisContext.mainWindowListener.clear();
            ClientContext.currentContext.platform.logoff();
        } 
        catch(Throwable t) { 
            resetHandler(); 
        } 
    } 

    void fireOnTimeout() {
        try { 
            String sessionId = OsirisContext.env.SESSIONID;
            if (sessionId == null) {
                //This is the fixed when the user log offs the system 
                //If no sessionid is specified then do nothing
                return; 
            } 
            if ( _currentTask ) {
                _currentTask.setCancelled( true ); 
                _currentTask = null; 
            } 

            def proc = { showLockScreen(); } as Runnable; 
            ClientContext.currentContext.taskManager.addTask( proc ); 
        } 
        catch(Warning w) { 
            OsirisContext.mainWindowListener.clear();
            ClientContext.currentContext.platform.logoff();
        } 
        catch(Throwable t) { 
            resetHandler(); 
        } 
    }

    def createTask() {
        return [
            getMaxSeconds: { return 300; }, 
            onTimeout: { fireOnTimeout(); }
        ] as CountDownTimer; 
    } 
}
