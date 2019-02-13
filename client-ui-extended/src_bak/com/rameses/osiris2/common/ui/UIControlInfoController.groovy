package com.rameses.osiris2.common.ui;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
        
public class DefaultUIControlInfoController 
{
    def info;
    def properties;
        
    void init() { 
    }
    
    def getHtmlviewprops() {
        def buffer = new StringBuffer();
        buffer.append('<html>');
        buffer.append(''' 
            <head>
                <style>
                    #title { 
                        background-color:#dfdfdf; text-indent:5;
                        font-weight:bold;
                    }
                    #key, #value, #keyvaluesep {
                        white-space:nowrap;
                    }
                    #key {
                        text-indent:5;
                    }
                    #keyvaluesep {
                        font-weight:bold;
                    }
                </style>
            </head>
        '''); 
        buffer.append('<body>');
        buffer.append('<div id="title">Properties</div>');
        
        if (properties) {
            buffer.append('<table cellpadding="1" cellspacing="0">');
            properties?.each{k,v-> 
                buffer.append('<tr>');
                buffer.append('<td id="key">'+k+'</td>');
                buffer.append('<td id="keyvaluesep">&nbsp;&nbsp; : &nbsp;&nbsp;</td>');
                buffer.append('<td id="value">'+v+'</td>');
                buffer.append('</tr>');
            }
            buffer.append('</table>');
        }
        
        buffer.append('</body>');
        buffer.append('</html>');
        return buffer;
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