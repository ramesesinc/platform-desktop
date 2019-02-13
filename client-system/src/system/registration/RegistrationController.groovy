package system.registration;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

public class RegistrationController 
{
    int option = 0;

    String terminalkey;
    String name;

    @Script("User")
    def user;

    public def check() {
        user.macaddress = MachineInfo.instance.macAddress;
        def t = new TerminalKey();
        if (t.open()) { 
            def terminalSvc = InvokerProxy.instance.create("TerminalService"); 

            def data = null; 
            try { 
                data = terminalSvc.findTerminal([terminalid: t.terminalid]); 
            } catch(Exception ex) { 
                def ce = findConnectionError( ex ); 
                if ( ce == null ) throw ex; 
                
                println 'Could not connect to the server...';
            }
            
            if (data) { 
                user.terminalId = t.terminalid; 
                return "_close";                 
            }
            //remove the file since this is an invalid terminal file
            t.delete(); 
        } 
        return "default";	
    }

    public def next() {
        switch(option)  {
            case 0: return "new";
            case 1: return  recoverTerminal();
        }    
        return null;
    }

    private void saveTerminal(def o) {
        def t = new TerminalKey( o );
        t.save();
        user.terminalId = t.terminalid;
    }

    public def registerNew() {
        def svc = InvokerProxy.instance.create( "TerminalService");
        def o = [
            terminalid: terminalkey,
            macaddress: user.macaddress,
            registeredby:name
        ];
        svc.register( o );
        saveTerminal( o );
        return "_close";
    }

    public def recoverTerminal() {
        def svc = InvokerProxy.instance.create( "TerminalService");
        def o = [
            macaddress: user.macaddress,
        ];
        def m = svc.recover( o );
        o.terminalid = m.terminalid;
        saveTerminal( o );
        return "_close";
    }
    
    private def findConnectionError(Throwable t) { 
        if ( t == null ) return null; 
        else if ( t instanceof java.net.ConnectException ) return t; 
        else return findConnectionError( t.getCause() ); 
    }
}
