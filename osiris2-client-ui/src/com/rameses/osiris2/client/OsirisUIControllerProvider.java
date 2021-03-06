package com.rameses.osiris2.client;

import com.rameses.rcp.framework.UIController;
import com.rameses.osiris2.SessionContext;
import com.rameses.osiris2.WorkUnit;
import com.rameses.osiris2.WorkUnitInstance;
import com.rameses.rcp.framework.ControllerProvider;

public class OsirisUIControllerProvider extends ControllerProvider {
    
    protected UIController provide(String name, UIController caller) {
        if( name == null )
            throw new RuntimeException("Controller name should not be null.");
        
        //the full qualified name is <module>:<workunit_name>
        //if no module name is passed and caller is not null,
        //then use the caller's module name
        if ( name.indexOf(":") < 0 && caller instanceof WorkUnitUIController ) {
            WorkUnitUIController wuuc = (WorkUnitUIController) caller;
            name = wuuc.getWorkunit().getModule().getName() + ":" + name;
        }
        
        try {
            SessionContext app = OsirisContext.getSession();
            WorkUnit wu = app.getWorkUnit(name);
            WorkUnitInstance wi = wu.newInstance();
            return new WorkUnitUIController( wi );
        } catch(Exception e) {
            throw new RuntimeException("Workunit not found ->"+name);
        }
    }
    
    
}
