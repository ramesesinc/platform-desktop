package com.rameses.osiris2.common;

import java.util.Map;

public interface CRUDService 
{
    
    Map create(Map o); 
    
    Map open(Map o);
    
    Map update(Map o);

    void delete(Map o);
    
    Map approve(Map o); 

}
