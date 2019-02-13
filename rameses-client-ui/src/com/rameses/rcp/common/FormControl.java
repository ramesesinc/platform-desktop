/*
 * FormControl.java
 *
 * Created on October 18, 2010, 11:51 AM
 * @author jaycverg
 */

package com.rameses.rcp.common;

import java.util.HashMap;
import java.util.Map;

public class FormControl 
{    
    private static final long serialVersionUID = 1L;
    private Map properties = new HashMap();
    private String categoryid;
    private String type;
    
    public FormControl() {
    }
    
    public FormControl(String type, Map props) {
        init(type, props, null);
    }
    
    public FormControl(String type, Map props, String categoryid) {
        init(type, props, categoryid);
    }
    
    public FormControl(Map data) {
        String type = (String) data.remove("type");
        String categoryid = (String) data.remove("categoryid");
        init(type, data, categoryid);
    }    
    
    protected void init(String type, Map props, String categoryid) {
        this.type = type;
        this.properties = (props == null? new HashMap(): props);
        this.categoryid = categoryid;
        
        Integer height = 20;
        Integer width = 0;
        if(props.containsKey("width")) {
            width = (Integer)props.get("width");
        }
        if (!this.properties.containsKey("preferredSize")) 
            this.properties.put("preferredSize", width+","+height);        
    }
        
    public String getType() { return type; }    
    public void setType(String type) { this.type = type; }
    
    public Map getProperties() { return properties; }    
    public void setProperties(Map properties) {
        this.properties = properties ;
    }
    
    public String getCategoryid() { return categoryid; } 
    public void setCategoryid(String categoryid) { this.categoryid = categoryid; }
    
    public String toString() {
        return "type:" + type + ", categoryid:" + categoryid + ", properties:" + properties;
    }
        
}
