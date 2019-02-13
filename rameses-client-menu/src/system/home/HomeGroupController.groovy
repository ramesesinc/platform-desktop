package system.home;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;

public class HomeGroupController
{
    @Binding
    def binding;

    //to be set by the caller
    def folder;
    
    def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    def session = OsirisContext.getSession();
    def model;
    def items;
    def icon;

    @FormId
    def getFormId() {
        return (folder? folder.fullId: 'home-group-action'); 
    }
    
    @FormTitle 
    def getFormTitle() {
        return title; 
    }
    
    def getTitle() {
        return (folder? folder.caption: '[No Caption]');
    }
    
    void init() { 
        def appEnv = clientContext.appEnv; 
        def customfolder = appEnv['app.custom']; 
        if (customfolder) { 
            icon = 'images/' + customfolder + '/logo.png'; 
        } 
        else {
            icon = 'images/logo.png'; 
        }

        def homeicon = 'images/' + customfolder + '/home-icon.png';  
        def custom_homeicon = clientContext.getResource(homeicon); 
        if (!custom_homeicon) homeicon = 'home/icons/folder.png'; 
        
        items = [];
        if (folder) {
            def folders = session?.getFolders(folder.fullId);
            folders?.each{
                if (it.invoker == null) { 
                    def result = hasChildren(it); 
                    if (result) { 
                        def map = [:];
                        map.putAll( it.properties );
                        map.userObject = it; 
                        items.add( map ); 
                    } 
                } else {
                    def map = [:]; 
                    map.putAll( it.invoker.properties ); 
                    map.userObject = it.invoker; 
                    map.domain = it.invoker.domain;
                    map.role = it.invoker.role;
                    map.permission = it.invoker.permission;
                    items.add( map );
                }
            }
        }
        
        items.each { 
            if (!it.icon) it.icon = homeicon;
        } 
        
        model = [
            fetchList: {o-> 
                return items; 
            }, 
            
            onOpenItem: {o-> 
                def opener = null; 
                def userobj = o.userObject;
                if (userobj instanceof com.rameses.osiris2.Folder) {
                    opener = Inv.lookupOpener('home_group_action', [folder: userobj]); 
                    if (!opener.target) opener.target = 'window'; 
                } else { 
                    //userobj is an instanceof Invoker 
                    opener = Inv.createOpener(userobj, [:]); 
                    if (!opener.target) opener.target = 'window'; 
                }
                
                binding.fireNavigation( opener );  
            }
        ] as TileViewModel
    } 
    
    private boolean hasChildren( folder ) {
        if (folder.invoker != null) return true; 
    
        def list = session.getFolders(folder.fullId); 
        if (list) {
            for (o in list) {
                if (hasChildren(o)) return true;  
            }
        } 
        return false; 
    }    
    
    public def getEnv() { 
        return clientContext.headers; 
    } 
}
