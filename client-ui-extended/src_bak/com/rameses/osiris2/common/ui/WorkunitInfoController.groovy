package com.rameses.osiris2.common.ui;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
public class DefaultWorkunitInfoController 
{
    def info;
        
    void init() {        
    }
    
    def selectedItem;
    def listhandler = [
        fetchList: {o-> 
            return info?.invokers; 
        } 
    ] as BasicListModel 
            
    def getHtmlview() {
        def buffer = new StringBuffer();
        buffer.append('<html>');
        buffer.append('<body>');
        buffer.append('<b>Properties</b><br/>');
        
        def props = selectedItem?.properties;
        if (props == null) props = [:]; 
        
        buffer.append(props); 
        buffer.append('</body>');
        buffer.append('</html>');
        return buffer;
    }
}