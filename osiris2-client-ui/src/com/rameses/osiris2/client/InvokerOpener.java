/*
 * InvokerOpener.java
 *
 * Created on September 3, 2013, 12:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.client;

import com.rameses.osiris2.Invoker;
import com.rameses.rcp.common.Opener;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class InvokerOpener extends Opener 
{    
    private Invoker invoker;
    
    public InvokerOpener(Invoker invoker, Map params, String caption) {
        this.invoker = invoker;
        init(params, caption);
    }
    
    private void init(Map params, String caption) {
        setName(invoker.getWorkunitid()); 
        setId(createInvokerId());
        
        if (caption == null) caption = invoker.getCaption();
        if (caption == null) caption = invoker.getWorkunitid();

        setCaption(caption); 
        setAction(invoker.getAction());
        
        String target = getPropertyString("target");
        if (target != null) target = target.replaceAll("^([^_])", "_$1");

        setTarget(target); 
        if (params != null) setParams(params); 
        
        Map invProps = invoker.getProperties();
        if (!invProps.isEmpty()) getProperties().putAll(invProps);
     
        getProperties().put("_INVOKER_", invoker);
        //set this as loader so it can automatically load the handler
        setLoader(new OpenerLoaderImpl()); 
        setProvider(new ProviderImpl()); 
    }
    
    private String createInvokerId() {
        StringBuffer sb = new StringBuffer();
        sb.append( invoker.getWorkunitid() );

        String caption = invoker.getCaption();        
        String id = getPropertyString("id");
        if (id != null && id.trim().length() > 0) {
            sb.append("_" + id);
        } else if (caption != null && caption.trim().length() > 0) {
            sb.append("_" + caption);
        }
        return sb.toString();
    }
    
    private String getPropertyString(String name) {
        Object ov = (invoker == null? null: invoker.getProperties().get(name)); 
        return (ov == null? null: ov.toString()); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" OpenerLoaderImpl "> 
    
    private class OpenerLoaderImpl implements Opener.OpenerLoader
    {
        public void load(Opener o) {
            InvokerUtil.invokeOpener( o );
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl "> 
    
    private class ProviderImpl implements Opener.Provider 
    {
        InvokerOpener root = InvokerOpener.this;
        
        public Opener createInstance(Map params, String caption) {
            return new InvokerOpener(root.invoker, params, caption); 
        }         
    }
    
    // </editor-fold>
}
