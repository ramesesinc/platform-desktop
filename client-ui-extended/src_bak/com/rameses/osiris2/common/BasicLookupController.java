/*
 * BasicLookupController.java
 *
 * Created on August 3, 2013, 12:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.annotations.Controller;

/**
 *
 * @author Elmo
 */
public class BasicLookupController extends LookupController {
    
    @Controller
    WorkUnitUIController controller;
    
    private String serviceName;
    private String tag;
    
    public String getServiceName() {
        if (serviceName == null) {
            serviceName = (String) controller.getWorkunit().getWorkunit().getProperties().get("serviceName");
            if (serviceName == null)
                throw new RuntimeException("Please specify a serviceName attribute in your workunit");
        }
        return serviceName;
    }
    
    public String getTag() {
        if (tag == null) {
            tag = (String)controller.getWorkunit().getWorkunit().getProperties().get("tag");
        }
        return tag;
    }
    
}
