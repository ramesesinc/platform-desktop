/*
 * ServiceLookupController1.java
 *
 * Created on February 4, 2014, 8:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

/**
 *
 * @author Elmo
 */
public class ServiceLookupController extends LookupController {
    
    private String serviceName;
    private String entityName;
    private String tag;
    
    private Boolean allowSearch;
    
    /** Creates a new instance of ServiceLookupController1 */
    public ServiceLookupController() {
    }
    
    public String getServiceName() {
        if (serviceName == null) {
            serviceName = (String) super.getControllerProperties().get("serviceName");
            if (serviceName == null)
                throw new RuntimeException("Please specify a serviceName attribute in your workunit");
        }
        return serviceName;
    }
    
    public String getEntityName() {
        if (entityName == null) {
            entityName = (String) super.getControllerProperties().get("entityName");
        }
        return entityName;
    }
    
    public String getTag() {
        if (tag == null) {
            tag = (String) super.getControllerProperties().get("tag");
            if (tag == null) tag = super.getTag();
        }
        return tag;
    }
    
    public boolean isAllowSearch() {
        if (allowSearch == null) {
            allowSearch = "true".equals(getControllerProperties().get("allowSearch"));
        }
        return allowSearch;
    }
}
