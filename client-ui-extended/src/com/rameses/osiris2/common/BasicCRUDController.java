package com.rameses.osiris2.common;

import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.annotations.Controller;


public  class BasicCRUDController extends CRUDController {
    
    @Controller
    protected WorkUnitUIController controller;
    
    public String getServiceName() {
        String svcName = (String) controller.getWorkunit().getWorkunit().getProperties().get("serviceName");
        if(svcName==null)
            throw new RuntimeException("Please indicate serviceName in the workunit controller");
        return svcName;
    }
    
    public String getEntityName() {
        String entityName = (String) controller.getWorkunit().getWorkunit().getProperties().get("entityName");
        if(entityName==null)
            throw new RuntimeException("Please indicate entityName in the workunit controller");
        return entityName;
    }
    
    
}
