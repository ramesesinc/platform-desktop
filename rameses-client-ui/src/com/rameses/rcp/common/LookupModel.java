package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The lookup list model extends paging model
 * - returnSingleResult is when you want the lookup to
 *    immediately return a result without popping the lookup
 *    dialog. null null is the default
 */
public class LookupModel extends ScrollListModel implements LookupDataSource 
{    
    private List DEFAULT_LIST = new ArrayList(); 
    private Map properties = new HashMap(); 
    private LookupSelector selector;
    private Object onselect;
    private Object onempty;
    
    private String returnItemKey;
    private String returnItemValue;
    private String returnFields;    
        
    public Map getProperties() { return properties; } 
    
    public Object getOnselect() { return onselect; }    
    public void setOnselect(Object onselect) { 
        this.onselect = onselect; 
    }
    
    public Object getOnempty() { return onempty; }    
    public void setOnempty(Object onempty) { 
        this.onempty = onempty; 
    }
        
    public LookupSelector getSelector() { return selector; }    
    public void setSelector(LookupSelector s) { this.selector = s; }
    
    public String getReturnItemKey() { return returnItemKey; }    
    public void setReturnItemKey(String returnItemKey) { 
        this.returnItemKey = returnItemKey; 
    }

    public String getReturnItemValue() { return returnItemValue; }    
    public void setReturnItemValue(String returnItemValue) { 
        this.returnItemValue = returnItemValue; 
    } 
    
    public String getReturnFields() { return returnFields; }    
    public void setReturnFields(String returnFields) { 
        this.returnFields = returnFields; 
    }     

    public Object getValue() { 
        return getSelectedValue(); 
    }
    
    public boolean selectSingleResult() { return false; }    
    public boolean errorOnEmpty() { return false; }    
    
    public List fetchList(Map params) { return DEFAULT_LIST; } 
        
    //default implementation for select and cancel
    public Object select() { 
        if (selector != null) { 
            Object selectedValue = getSelectedValue(); 
            Object outcome = selector.select( selectedValue ); 
            if (outcome instanceof Opener) return outcome; 
        } 
        return "_close"; 
    }
    
    public String cancel() 
    {
        if (selector != null) selector.cancelSelection();
        
        return "_close";
    }
    
    public String emptySelection() 
    {
        if (selector != null) selector.select(null);
        
        return "_close";
    }
    
    
    //invoked when the lookup screen is shown
    public boolean show(String searchtext) { 
        setSearchtext(searchtext);        
        load();
        
        if (errorOnEmpty() && getDataList().size() == 0) 
            throw new IllegalStateException("There are no records found");
        
        if (selectSingleResult() && getDataList().size() == 1) 
        {
            Object retVal = getDataList().get(0);
            if (selector != null) selector.select(retVal);
            
            return false;
        } else { 
            return true;
        }
    }
    
    protected void onfinalize() throws Throwable {
        selector = null;
    }

    public void moveLastPage() {}   
    /*
    // <editor-fold defaultstate="collapsed" desc=" ResultKeyValueMapper (class) ">  
    
    private class ResultKeyValueMapper 
    {      
        PropertyResolver res = PropertyResolver.getInstance();
        
        public Object parse(String itemKey, String itemVal, Object value) 
        {
            if (value == null) return null;
            if (itemKey == null && itemVal == null) return value;            
            if (itemKey == null && itemVal != null) itemKey = itemVal;
            
            if (value instanceof List) 
            {                
                List results = new ArrayList();                 
                for (Object o : (List)value) 
                {
                    Object xo = extract(itemKey, itemVal, o);
                    if (xo != null) results.add(xo); 
                }
                return results;
            }
            else if (value != null) {
                return extract(itemKey, itemVal, value); 
            } 
            else {
                return null; 
            }
        }
        
        private Object extract(String itemKey, String itemVal, Object o) 
        {
            Map map = new HashMap(); 
            if (itemKey != null) 
                map.put("key", res.getProperty(o, itemKey)); 
            if (itemVal != null) 
                map.put("value", res.getProperty(o, itemVal)); 
            
            return map; 
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ResultFieldsMapper (class) ">  
    
    private class ResultFieldsMapper 
    {       
        PropertyResolver res = PropertyResolver.getInstance();
        
        public Object parse(String fields, Object value) 
        {
            if (value == null || fields == null) return value;
            
            String[] fldnames = fields.split(",");
            
            if (value instanceof List) 
            {                
                List results = new ArrayList();                 
                for (Object o : (List)value) 
                {
                    Object xo = extract(fldnames, o);
                    if (xo != null) results.add(xo); 
                } 
                return results;
            }
            else if (value != null) {
                return extract(fldnames, value); 
            }
            else {
                return null; 
            }
        }
        
        private Object extract(String[] fldnames, Object o) 
        {
            Map map = new HashMap(); 
            for (String name : fldnames)
            {
                if (name == null || name.length() == 0) continue;
                
                map.put(name, res.getProperty(o, name)); 
            }
            return map; 
        }
    }
    
    // </editor-fold>    
    */
}
