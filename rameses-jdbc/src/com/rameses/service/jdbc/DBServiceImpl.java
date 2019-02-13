/*
 * DefaultScriptService.java
 * Created on December 21, 2011, 9:51 AM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.service.jdbc;

import com.rameses.service.ScriptServiceContext;
import java.util.Map;

/**
 *
 * @author jzamss
 */
public class DBServiceImpl implements DBService {
        
    private Map conf;
    private String serviceName = "DBService";
    private String adapter;
   
    
    /** Creates a new instance of DefaultScriptService */
    public DBServiceImpl(Map c, String adapterName) {
        conf = c;
        if(adapterName!=null ) adapter = adapterName;
    }
    
    public DBServiceImpl(Map c, String adapterName, String serviceName) {
        conf = c;
        if( serviceName!=null ) this.serviceName = serviceName;
        if(adapterName!=null ) this.adapter = adapterName;
    }
    
    public Map getResultSet(String statement, Object parameters) throws Exception {
        if( conf.get("readTimeout") == null ) {
            conf.put("readTimeout", "0");
        }
        if( conf.get("connectionTimeout") == null ) {
            conf.put("connectionTimeout", "0");
        }
        ScriptServiceContext ssc = new ScriptServiceContext(conf);
        DBService dbs = ssc.create(serviceName, DBService.class);
        if( parameters !=null && (parameters instanceof Map)) {
            ((Map)parameters).put("_adapter", adapter);
        }
        return dbs.getResultSet(statement, parameters);
    }
 
    
}
