package com.rameses.platform.interfaces;

import java.util.Map;

public interface SubWindow 
{    
    static final long serialVersionUID = 1L;
    
    String getName();
    
    String getTitle();
    void setTitle(String title);
    
    void closeWindow(); 
        
    void setListener(SubWindowListener listener);
    
    void update(Map windowAttributes);
    
}
