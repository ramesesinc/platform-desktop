/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.filemgmt;

import com.rameses.io.FileLocTypeProvider;
import com.rameses.io.FileTransferSession;
import com.rameses.io.IOStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class FileSysLocTypeProvider implements FileLocTypeProvider, FileLocationRegistry { 

    private final static Map<String,FileLocationConf> confs = new HashMap(); 
    
    
    private final static String PROVIDER_NAME = "file";  
    
    public String getName() { 
        return PROVIDER_NAME; 
    }

    public FileTransferSession createUploadSession() {
        return new UploadSession();
    }

    public FileTransferSession createDownloadSession() {
        return null; 
    }

    public void deleteFile(String name, String locationConfigId) {
    }

    public void register(FileLocationConf conf) {
        if ( conf == null ) return; 
        if ( PROVIDER_NAME.equals(conf.getType())) {
            FileLocationConf cc = confs.get(conf.getName()); 
            if ( cc == null ) {
                confs.put(conf.getName(), conf); 
            }
        }
    }
    
    private FileLocationConf getDefaultConf() {
        FileLocationConf[] values = confs.values().toArray(new FileLocationConf[]{});
        for (FileLocationConf c : values ) {
            if ( c.isDefaulted()) return c; 
        }
        return null; 
    }
    
    private class UploadSession extends FileTransferSession {
        public void run() {
            if ( isCancelled()) {
                return; 
            }
            
            FileLocationConf c = getDefaultConf(); 
            if ( c == null ) {
                System.out.println("No default file location conf");
                return; 
            }

            String rootdir = c.getRootDir(); 
            if ( rootdir == null ) rootdir = "";
            
            File dir = null; 
            try {
                dir = new File( new URL( c.getUrl()).toURI());
            } catch (MalformedURLException e1) {
                throw new RuntimeException(e1.getMessage(), e1); 
            } catch (URISyntaxException e2) {
                throw new RuntimeException(e2.getMessage(), e2); 
            }

            if ( rootdir.trim().length() > 0 ) {
                dir = new File( dir, rootdir.trim()); 
            }

            File destfile = new File(dir, getTargetName()); 
            File srcfile = getFile(); 
            
            Handler handler = getHandler();
            long filesize = getFileSize(srcfile); 
            
            int len = 0;
            long bytesprocessed = 0;
            boolean finished = false;
            FileInputStream fis = null; 
            FileOutputStream fos = null; 
            try {
                fos = new FileOutputStream( destfile );  
                fis = new FileInputStream( srcfile); 
                byte[] buffer = new byte[1024 * 100];                 
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    bytesprocessed += len; 
                    if ( handler != null ) {
                        handler.ontransfer( filesize, bytesprocessed );
                    }
                }  
                finished = true; 
            }
            catch (FileNotFoundException ff) {
                throw new RuntimeException(ff.getMessage(), ff); 
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage(), ioe); 
            }
            finally {
                try { fos.close(); }catch(Throwable t){;}
                try { fis.close(); }catch(Throwable t){;}
            }
            
            if ( finished && handler != null ) {
                handler.oncomplete(); 
            }
        }
        
        private long getFileSize( File file ) {
            int len = 0;
            int bytesprocessed = 0;
            FileInputStream fis = null; 
            try {
                fis = new FileInputStream( file ); 
                byte[] buffer = new byte[1024 * 100];                 
                while ((len = fis.read(buffer)) != -1) {
                    bytesprocessed += len; 
                }  
                return bytesprocessed; 
            }
            catch (FileNotFoundException ff) {
                throw new RuntimeException(ff.getMessage(), ff); 
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage(), ioe); 
            }
            finally {
                try { fis.close(); }catch(Throwable t){;}
            }
        }
    }
}
