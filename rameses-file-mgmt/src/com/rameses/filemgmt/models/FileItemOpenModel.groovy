package com.rameses.filemgmt.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

public class FileItemOpenModel  {

    @Binding 
    def binding;

    @FormId
    def formId = new java.rmi.server.UID().toString();
    
    @FormTitle
    def formTitle;
    
    @SubWindow
    def window;
    
    private boolean cancelled;
    
    def image; 
    def loadingStatusMessage;
    
    Map eventMap;        
    def fileitem; 
    def filehandler = [
        onTransfer: { fileid, filesize, bytesprocessed-> 
            if ( cancelled ) return; 
            
            Number num = ((bytesprocessed / filesize) * 100); 
            loadingStatusMessage = 'Downloading please wait... ('+ num.intValue() +'%)';
            binding.notifyDepends('loadingStatusMessage'); 
        }, 
        onCompleted: {
            if ( cancelled ) return;
            
            loadImage();            
            binding.fireNavigation('view'); 
        }
    ] as com.rameses.filemgmt.DefaultFileDownloadHandler;

    def fileloctype;
    def images = [:];
    
    def init() { 
        if ( eventMap != null ) { 
            // hook call back events
            eventMap.onchangeItem = onchangeItemHandler;
        }
        
        loadingStatusMessage = 'Processing...';
        formTitle = 'Form Item Viewer ('+ fileitem.objid +')'; 
        
        fileloctype = fileitem.fileloc.loctype;
        if ( fileloctype == 'file' ) {
            loadImage(); 
            return 'view'; 
        }
        
        def fdm = com.rameses.filemgmt.FileDownloadManager.instance; 
        def stat = fdm.getStatus( fileitem.objid ); 
        if ( stat == 'completed') {
            loadImage();
            return 'view'; 
        }
        
        if ( stat == null ) {
            fdm.download( fileitem.objid, fileitem.filetype, fileitem.filelocid, fileitem.filesize, filehandler );         
        } else {
            fdm.fileHandlers.add( fileitem.objid, filehandler ); 
        }
        return null; 
    } 
    
    def onchangeItemHandler = { o-> 
        if ( o == null ) {
            return;
        }
                
        fileitem = o; 
        formTitle = 'Form Item Viewer ('+ fileitem.objid +')'; 
        window.update([ title: formTitle ]);
        
        loadingStatusMessage = 'Processing...';
        binding.fireNavigation("default"); 
        
        def pgname = init(); 
        if ( !pgname ) pgname = "default"; 
        binding.fireNavigation( pgname ); 
        return null; 
    }
    
    @Close 
    void closeForm() { 
        cancelled = true; 
        eventMap.onchangeItem = null;
        
        if ( fileloctype == 'file' ) {
            return ; 
        }
        
        try {
            def fdm = com.rameses.filemgmt.FileDownloadManager.instance; 
            fdm.fileHandlers.remove( fileitem.fileid, filehandler ); 
        } catch(Throwable t) {;} 
    }
    
    void loadImage() { 
        if ( fileloctype == 'file' ) {
            loadImageFromFileLoc(); 
        }
        else { 
            def imageURL = images.get( fileitem.objid );
            if ( imageURL == null ) { 
                def fdm = com.rameses.filemgmt.FileDownloadManager.instance; 
                def file = fdm.getContentFile( fileitem.objid ); 
                imageURL = file.toURI().toURL();
            } 
            image = new javax.swing.ImageIcon( imageURL ); 
            images.put( fileitem.objid, imageURL ); 
        } 
    }
    
    void loadImageFromFileLoc() {
        def imageURL = images.get( fileitem.objid );
        if ( imageURL == null ) { 
            def conf = fileitem.fileloc; 
            def url = new URL( conf.url ); 
            def dir = new java.io.File( url.toURI()); 
            if ( conf.rootdir ) dir = new java.io.File( dir, conf.rootdir ); 

            def fname = fileitem.objid.toString() +'.'+ fileitem.filetype; 
            def file = new File( dir, fname ); 
            imageURL = file.toURI().toURL();
        }
        
        image = new javax.swing.ImageIcon( imageURL ); 
        images.put( fileitem.objid, imageURL ); 
    }
}