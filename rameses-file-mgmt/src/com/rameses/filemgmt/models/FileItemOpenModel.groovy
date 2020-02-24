package com.rameses.filemgmt.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

public class FileItemOpenModel  {

    @Binding 
    def binding;

    @FormTitle
    def formTitle;
    
    private boolean cancelled;
    
    def image; 
    def loadingStatusMessage;
    
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
    
    def init() { 
        loadingStatusMessage = 'Processing...';
        formTitle = 'Form Item Viewer ('+ fileitem.objid +')'; 
        println 'fileitem -> '+ fileitem;
        
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
    
    @Close 
    void closeForm() { 
        cancelled = true; 
        
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
            def fdm = com.rameses.filemgmt.FileDownloadManager.instance; 
            def file = fdm.getContentFile( fileitem.objid ); 
            image = new javax.swing.ImageIcon( file.toURI().toURL()); 
        } 
    }
    
    void loadImageFromFileLoc() {
        def conf = fileitem.fileloc; 
        def url = new URL( conf.url ); 
        def dir = new java.io.File( url.toURI()); 
        if ( conf.rootdir ) dir = new java.io.File( dir, conf.rootdir ); 
        
        def fname = fileitem.objid.toString() +'.'+ fileitem.filetype; 
        def file = new File( dir, fname ); 
        image = new javax.swing.ImageIcon( file.toURI().toURL()); 
    }
}