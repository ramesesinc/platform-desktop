/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.client;

import com.rameses.classutils.AnnotationFieldHandler;
import com.rameses.classutils.ClassDefUtil;

/**
 *
 * @author elmonazareno
 */
public class ManagedObjects {
    
    private AnnotationFieldHandler fieldHandler = new FieldInjectionHandler();
    
    private static ManagedObjects _instance ;
    
    public static ManagedObjects getInstance() {
        if(_instance==null) _instance = new ManagedObjects();
        return _instance;
    }
    
    public Object create(Class clazz) throws Exception {
        Object obj = clazz.newInstance();
        ClassDefUtil.getInstance().injectFields(obj, fieldHandler);
        return obj;
    }
    /*
    public Object injectAttributes(Object obj) throws Exception {
        ClassDefUtil.getInstance().injectFields(obj, fieldHandler);
        return obj;
    }
    */
    
}
