package com.rameses.osiris2.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.rcp.sigid.*;
import com.rameses.osiris2.common.*;

class SigIdDeviceListModel {

    def title = 'Signature Devices'; 
    def providers = []; 

    def default_icon = 'classpath://images/toolbars/select.png';
    
    void init() {
        providers = []; 
        SigIdDeviceManager.getProviders().each{
            providers << [name: it.name, desc: it.description ]; 
        }
        def selprov = SigIdDeviceManager.getProvider(); 
        if (selprov) {
            selprov = providers.find{ it.name==selprov.name } 
            if ( selprov ) selprov.icon = default_icon; 
        }
    }
    
    void save() {
        // this is called by the host controller 
        // so we will do nothing for this method 
    } 
    
    void setAsDefault() {
        if ( !selectedItem ) return;

        providers.each{ it.icon=null }
        SigIdDeviceManager.setProvider( selectedItem.name ); 
        def o = SigIdDeviceManager.getProvider();
        if ( o?.name ) {
            o = providers.find{ it.name==o.name }
            if ( o ) o.icon = default_icon;
        }
        listhandler.refresh();
    }
    
    def selectedItem; 
    def listhandler = [
        getColumnList: {
            return [
                [name: 'icon', caption:' ', type:'icon', width:30, maxWidth:30],
                [name: 'name', caption:'Name'],
                [name: 'desc', caption:'Description']
            ]; 
        }, 
        fetchList: {
            return providers; 
        }
    ] as BasicListModel;
    
    def sigModel = [
        onselect: { o-> 
            
        }
    ] as SigIdModel; 
    
    void testDevice() {
        SigIdViewer.open( sigModel ); 
    }
    
}