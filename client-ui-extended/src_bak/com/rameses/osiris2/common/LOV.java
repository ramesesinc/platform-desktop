/*
 * LOVModel.java
 *
 * Created on May 15, 2013, 2:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerProxy;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class LOV  {
    
    private static Map<String, List> lovs = new Hashtable();
    
    public static Object get(String key) {
        if(!lovs.containsKey(key)) {
            try {
                LOVService lv =(LOVService)InvokerProxy.getInstance().create("LOVService", LOVService.class);
                List m = lv.getKeyValues(key);
                lovs.put(key, m);
                return m;
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return lovs.get(key);
    }
    
    
}
