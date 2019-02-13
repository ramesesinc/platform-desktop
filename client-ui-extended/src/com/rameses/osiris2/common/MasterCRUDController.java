/*
 * MasterCRUDController.java
 *
 * Created on April 11, 2014, 11:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.ExpressionResolver;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Opener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Elmo
 */
public class MasterCRUDController extends CRUDController {
    
    private List optionList = new ArrayList();
    private Object selectedOption;
    
    public Object create() {
        super.create();
        return "create";
    }
    
    
    public List getOptionList() {
        return optionList;
    }
    
    public Object getSelectedOption() {
        return selectedOption;
    }
    
    public void setSelectedOption(Object selectedOption) {
        this.selectedOption = selectedOption;
    }
    
    protected void buildOptionList() {
        Map map = new HashMap();
        map.put("entity", getEntity());
        optionList.clear();
        selectedOption = null;
        
        try {
            List test = InvokerUtil.lookupOpeners(getEntityName()+":option", map);
            for( Object op : test ) {
                Opener o = (Opener)op;
                String expr =  (String) o.getProperties().get("visibleWhen");
                boolean vis = true;
                if(expr!=null ) {
                    try {
                        vis = ExpressionResolver.getInstance().evalBoolean( expr, map );
                    } catch(Exception ign){
                        ign.printStackTrace();
                    }
                }
                if(vis) {
                    if(selectedOption == null ) selectedOption = o;
                    optionList.add(o);
                }
            }
        } catch(Exception ign){
            System.out.println("error loading optionList->"+ign.getMessage());
        }
    }
    
    protected void afterSaveCreate(Object data) {
        buildOptionList();
    }
    
    public Object save() {
        super.save();
        return "default";
    }
    
    public Object open() {
        super.open();
        buildOptionList();
        return "default";
    }
    
    public Object approve() {
        super.approve();
        buildOptionList();
        return "default";
    }
    
    
    
}
