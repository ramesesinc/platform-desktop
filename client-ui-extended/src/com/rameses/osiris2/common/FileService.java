/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.common;

import com.rameses.io.AbstractChunkHandler;
import com.rameses.io.FileObject;
import com.rameses.io.FileObject.MetaInfo;
import com.rameses.osiris2.client.OsirisContext;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.service.ScriptServiceContext;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Encoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author rameses
 */
public class FileService {
    
    private final static ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private IService iSvc;
    private IUploadService iuSvc;

    public void upload( File file ) {
        upload( file, null ); 
    } 
    
    public void upload( File file, FileUploadHandler handler ) {
        threadPool.submit(new FileUploadTask( file, handler )); 
    }
        
    public File getFile( String id ) {
        ResourceObject ro = new ResourceObject( id );
        return ro.getFile(); 
    }
    
    public URL getURL( String id ) {
        try { 
            File file = getFile( id );
            if ( file == null ) {
                return null; 
            } else { 
                return file.toURI().toURL();
            } 
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex.getMessage(), ex); 
        }  
    } 
    
    public InputStream getInputStream( String id ) { 
        try {
            File file = getFile( id );
            if ( file == null ) {
                return null; 
            } else { 
                return new FileInputStream( file ); 
            } 
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex.getMessage(), ex); 
        }
    }  
    
    public void remove( String id ) { 
        ResourceObject ro = new ResourceObject( id );
        ro.remove( id );      
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Proxy Service "> 
    
    private Map getAppEnv() {
        return ClientContext.getCurrentContext().getAppEnv(); 
    }
    
    private IService getService() {
        if (iSvc == null) {
            ScriptServiceContext context = new ScriptServiceContext( getAppEnv() ); 
            iSvc = context.create("FileService", OsirisContext.getEnv(), IService.class); 
        }
        return iSvc; 
    }    
    private IUploadService getUploadService() {
        if (iuSvc == null) {
            ScriptServiceContext context = new ScriptServiceContext( getAppEnv() ); 
            iuSvc = context.create("FileUploadService", OsirisContext.getEnv(), IUploadService.class); 
        }
        return iuSvc; 
    }
            
    interface IUploadService {
        Object create( Object data ); 
        Object addItem( Object data ); 
        void addItems( Object params );
        void removeFile( Object params );
    } 
    
    interface IService {
        Map findHeader( Map params ); 
        Map findItem( Map params );        
        List getItems( Map params );
        void removeFile( Object params );
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" FileUploadTask "> 
    
    private class FileUploadTask extends AbstractChunkHandler implements Runnable {

        private File file; 
        private FileUploadHandler handler;
        
        private Exception error; 
        private List batches = new ArrayList();   
        private Base64Cipher cipher = new Base64Cipher();
        
        FileUploadTask( File file, FileUploadHandler handler ) {
            this.file = file; 

            if ( handler == null ) { 
                this.handler = new FileUploadHandler(); 
            } else {
                this.handler = handler; 
            }
        } 
        
        public void run() {
            FileObject fo = new FileObject( file ); 
            fo.read( this );  
        }
        
        public void start() { 
            try { 
                MetaInfo meta = getMeta();
                Map data = new HashMap();
                data.put("objid", meta.getId()); 
                data.put("filename", meta.getFileName()); 
                data.put("filetype", meta.getFileType()); 
                data.put("filesize", meta.getFileSize()); 
                data.put("chunkcount", meta.getChunkCount()); 
                getUploadService().create( data ); 
                handler.start( data );
            } catch( Exception ex ) {
                error = ex; 
                cancel();
            } 
        }     
        
        public void end() { 
            if ( isCancelled() ) { 
                try { 
                    MetaInfo meta = getMeta(); 
                    Map params = new HashMap(); 
                    params.put("fileid", meta.getId() ); 
                    getUploadService().removeFile( params ); 
                } catch( Throwable t ) {
                    t.printStackTrace(); 
                }
            }  else {
                try {
                    if ( !batches.isEmpty() ) { 
                        uploadBatch();  
                    } 
                } catch( Exception ex ) {
                    this.error = ex; 
                }       
            } 

            if ( this.error != null ) {
                handler.error( error ); 
            } 
            handler.end(); 
        }
        
        public void handle(int indexno, byte[] bytes) {
            try { 
                MetaInfo meta = getMeta();
                Map item = new HashMap();
                item.put("parentid", meta.getId() ); 
                item.put("indexno", indexno); 
                item.put("content", bytes ); 
                item.put("contentsize", bytes.length ); 
                batches.add( item ); 
                if ( batches.size() >= 5 ) { 
                    uploadBatch(); 
                } 
                handler.process( item );
            } catch(Exception ex) { 
                this.error = ex; 
                cancel(); 
            } 
        } 
        
        private void uploadBatch() {
            MetaInfo meta = getMeta();
            Map params = new HashMap();
            params.put("fileid", meta.getId() ); 
            params.put("items", batches); 
            getUploadService().addItems( params );
            batches.clear(); 
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ResourceObject "> 
    
    private class ResourceObject {
        private Base64Cipher base64;
        private String encid;
        private String id;
        
        ResourceObject(String id) {
            this.id = id; 
            this.base64 = new Base64Cipher();
            this.encid = Encoder.MD5.encode(id); 
        }
        
        File getFile() {
            File basedir = getTempDir();    
            File file = new File( basedir, this.encid ); 
            if ( !file.exists() ) {
                Map params = new HashMap();
                params.put("objid", this.id ); 
                Map result = getService().findHeader(params); 
                Number chunkcount = (Number) result.get("chunkcount"); 
                String context = (String) result.get("context"); 
                
                File tmpfile = new File( basedir, this.encid + "~" );
                downloadData( tmpfile, context, chunkcount.intValue() );
                tmpfile.renameTo( file ); 
            } 
            return file; 
        }
        
        void remove( String id ) { 
            if ( id != null && id.trim().length() > 0 ) {
                File basedir = getTempDir();    
                File file = new File( basedir, this.encid ); 
                if ( file.exists() ) file.delete(); 

                Map params = new HashMap();
                params.put("fileid", this.id ); 
                getService().removeFile( params ); 
            } 
        } 
        
        boolean downloadData( File file, String context, int chunkcount ) {
            FileOutputStream fos = null; 
            try { 
                fos = new FileOutputStream( file );
                
                int startidx = 1; 
                while ( startidx <= chunkcount ) {
                    Map params = new HashMap();
                    params.put("context", context);
                    params.put("fileid", this.id ); 
                    params.put("startindexno", startidx ); 
                    params.put("endindexno", startidx + 5 ); 
                    List results = getService().getItems( params ); 
                    for ( Object item : results ) {
                        Map map = (Map) item; 
                        Object content = map.remove("content");
                        if ( content == null ) { continue; } 
                        
                        Map dmap = (Map) base64.decode( content.toString() ); 
                        byte[] bytes = (byte[]) dmap.remove("content"); 
                        fos.write( bytes, 0, bytes.length ); 
                    }
                    startidx += 6; 
                } 
                fos.flush(); 
                return true; 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) {
                throw new RuntimeException(e.getMessage(), e); 
            } finally {
                try { fos.close(); }catch(Throwable t){;} 
            } 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CleanupTask "> 
    
    public void runCleanupTask() { 
        threadPool.submit( new CleanupTask() ); 
    } 
    
    private File getTempDir() { 
        Map appenv = getAppEnv(); 
        Object objval = (appenv == null? null: appenv.get("tempdir")); 
        String tempdir = (objval == null? null: objval.toString()); 
        if (tempdir == null || tempdir.length() == 0) {
            tempdir = System.getProperty("java.io.tmpdir");
        }
        File ftempdir = new File(tempdir);
        File basedir = new File(ftempdir, "rameses");
        if (!basedir.exists()) basedir.mkdir();
        
        return basedir; 
    }    
    
    private class CleanupTask implements Runnable { 
        
        public void run() { 
            removeObsoleteFiles();
        }
        
        void removeObsoleteFiles() {
            File basedir = getTempDir(); 
            
            Calendar cal = Calendar.getInstance();
            File[] files = basedir.listFiles();
            for (File f : files) { 
                cal.setTimeInMillis(f.lastModified()); 
                cal.add(Calendar.HOUR, 24); 
                if (cal.getTimeInMillis() < System.currentTimeMillis()) { 
                    try { 
                        delete(f); 
                    } catch(Throwable t) {
                        //do nothing 
                    }
                }
            }
        }
        
        void delete(File f) { 
            if (f.isDirectory()) {
                File[] files = f.listFiles();
                for (File o : files) delete(o); 
            } 
            f.delete(); 
        } 
    }
    
    // </editor-fold>
    
} 
 