package system.home;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;

public class HomeControllerImpl 
{
    @Binding
    def binding;

    def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    def session = OsirisContext.getSession();
    def model;
    def items;
    def icon;
    def self = this;
    
    void init() { 
        icon = 'classpath://images/logo.png'; 
        def appEnv = clientContext.appEnv; 
        def customfolder = appEnv['app.custom']; 
        def homeicon = 'images/' + customfolder + '/home-icon.png';  
        def custom_homeicon = clientContext.getResource(homeicon); 
        if (!custom_homeicon) homeicon = 'home/icons/folder.png'; 
        
        items = [];        
        
        def folders = session?.getFolders('home');
        folders?.each {
            if (it.invoker == null) { 
                def result = hasItems(it); 
                if (result) { 
                    def map = [:];
                    map.putAll( it.properties );
                    map.userObject = it; 
                    items.add( map ); 
                }
            } 
        } 
        
        items.addAll(Inv.lookupActions('home.action'));
        items.each { 
            if (!it.icon) it.icon = homeicon;
        } 
        
        model = [
            fetchList: {o-> 
                return items; 
            }, 
            onOpenItem: {o-> 
                def userobj = null; 
                try {
                    userobj = o.userObject;
                } catch(Throwable t) {
                    userobj = o; 
                }
                
                if (userobj instanceof com.rameses.osiris2.Folder) {
                    def opener = Inv.lookupOpener('home_group_action', [folder: userobj]); 
                    if (!opener.target) opener.target = 'window'; 
                    
                    binding.fireNavigation( opener ); 
                } else {
                    return o.execute();
                }
            }
        ] as TileViewModel
    } 
    
    private boolean hasItems( def folder ) {
        if (folder.invoker != null) return true; 
    
        def list = session.getFolders(folder.fullId); 
        if (list) {
            for (o in list) {
                if (hasItems(o)) return true;  
            }
        } 
        return false; 
    }
    
    public boolean hasChildren( String name ) { 
        def folders = session?.getFolders( name ); 
        if ( folders == null ) return;
        
        for ( o in folders ) {
            if ( hasItems(o)) return true; 
        }
        return false; 
    }
    
    public def getEnv() { 
        return clientContext.headers; 
    } 
} 
