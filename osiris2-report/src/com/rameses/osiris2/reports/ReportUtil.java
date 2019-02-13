package com.rameses.osiris2.reports;

import com.rameses.osiris2.client.Inv;
import com.rameses.util.URLStreamHandlers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public final class ReportUtil {
        
    public final static ReportURLStreamHandlerFactory factory = new ReportURLStreamHandlerFactory();
    
    private static boolean developerMode;
    
    static {        
        System.out.println("Starting cache resource cleaner..."); 
        new Thread( new CacheResourceCleaner() ).start(); 
        
        try { 
            Object opener = Inv.lookupOpener("sysreport:edit", new HashMap()); 
            developerMode = ( opener==null ? false : true );  
        } catch(Throwable t) {;}
        
        factory.setDeveloperMode( developerMode ); 
        
        try {
            URL.setURLStreamHandlerFactory( URLStreamHandlers.getFactory()); 
        } catch(Throwable t) {;} 
    }
    
    public ReportUtil() {
    }
        
    public static JasperPrint generateJasper( Object data, Map conf ) throws Exception {
        JasperReport r = (JasperReport)conf.get("main");
        ReportDataSource md = new ReportDataSource(data);
        try {
            return JasperFillManager.fillReport(r,conf,md);
        } catch (JRException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    public static InputStream generatePdf( Object data, Map conf ) throws Exception {
        JasperPrint jp = generateJasper(data, conf);
        ReportDataSource md = new ReportDataSource(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jp, bos);
        return new ByteArrayInputStream( bos.toByteArray() );
    }
    
    public static InputStream generateHtml( Object data, Map conf ) throws Exception {
        JasperPrint jp = generateJasper(data, conf);
        ReportDataSource md = new ReportDataSource(data);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JRHtmlExporter jhtml = new JRHtmlExporter();
        jhtml.setParameter(JRExporterParameter.JASPER_PRINT, jp );
        jhtml.setParameter(JRExporterParameter.OUTPUT_STREAM, bos );
        jhtml.exportReport();
        return new ByteArrayInputStream( bos.toByteArray() );
    }
    
    public static void view( JasperPrint p ) {
        JasperViewer.viewReport( p );
    }

    public static boolean print( JasperPrint jp ) throws Exception {
        return print( jp, true ); 
    }
    public static boolean print( JasperPrint jp, boolean withPrintDialog ) throws Exception {
        return JasperPrintManager.printReport(jp, withPrintDialog );
    }
    
    public static boolean print( String reportName, Object reportData ) throws Exception {
        return print( reportName, reportData, true ); 
    }
    public static boolean print( String reportName, Object reportData, boolean withPrintDialog ) throws Exception {
        if ( reportName==null || reportName.trim().length()==0 ) 
            throw new RuntimeException("reportName parameter is required");
        if ( reportData==null ) 
            throw new RuntimeException("reportData must not be null");
        
        ReportModelImpl rmi = new ReportModelImpl( reportName, reportData ); 
        rmi.viewReport(); 
        return print( rmi.getReport(), withPrintDialog ); 
    }
    
    //this gets the jasper report
    public static JasperReport getJasperReport( String name ) {
        if( name.endsWith(".jrxml")) {
            String reportPath = System.getProperty("user.dir") + "/reports/"; 
            //check first if the file has already been compiled.
            URLConnection uc = null;
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                //fix the directories
                String srptname = reportPath + name.replaceAll("jrxml", "jasper");
                
                String dirPath = srptname.substring(0, srptname.lastIndexOf("/"));
                File fd = new File(dirPath);
                if (!fd.exists()) { fd.mkdirs(); }
                
                File f = new File(srptname);
                URL u = ReportUtil.class.getClassLoader().getResource(name);
                is = u.openStream();
                uc = u.openConnection();
                long newModified = uc.getLastModified();
                
                if( f.exists() ) {
                    long oldModified = f.lastModified();
                    if( newModified != oldModified ) {
                        f.delete();
                        fos = new FileOutputStream(f);
                        JasperCompileManager.compileReportToStream( is,fos );
                        fos.flush();
                        f.setLastModified(newModified);
                    }
                } else {
                    fos = new FileOutputStream(f);
                    JasperCompileManager.compileReportToStream( is,fos );
                    fos.flush();
                    f.setLastModified(newModified);
                }
                return (JasperReport) JRLoader.loadObject(f);
                
            } catch( RuntimeException re ) { 
                throw re; 
            } catch( Exception e ) { 
                throw new IllegalStateException(e.getMessage(), e); 
            } finally { 
                try { is.close(); } catch(Exception ign){;}
                try { fos.close(); } catch(Exception ign){;}
            }
            
        } else if( name.endsWith(".jasper") ) {
            try {
                URL res = getResource( name ); 
                if ( res == null ) { 
                    throw new Exception("Report name "+ name +" not recognized"); 
                }  
                return (JasperReport) JRLoader.loadObject( res ); 
                
            } catch( RuntimeException re ) { 
                throw re; 
            } catch( Exception ex ) { 
                throw new IllegalStateException(ex.getMessage(), ex); 
            } 
        } 
        
        throw new IllegalStateException("Report name "+ name +" not recognized"); 
    } 
    
    public static URL getResource( String name ) { 
        return factory.getResource( name ); 
    } 

    public static InputStream getResourceAsStream( String name ) {
        try { 
            URL res = getResource( name ); 
            return ( res == null ? null : res.openStream() );  
        } catch( Exception e ) { 
            e.printStackTrace();
            return null; 
        } 
    } 

    public static URL getImage( String name ) { 
        if ( name == null || name.trim().length()== 0 ) {
            return null; 
        }
        
        String resname = "images/" + name; 
        URL result = factory.getWebResource( "images/" + name ); 
        if ( result == null ) {
            return getResource( resname ); 
        } else {
            return null; 
        }
    } 
    
    public static InputStream getImageAsStream( String name ) { 
        try {
            URL res = getImage( name ); 
            return ( res == null ? null : res.openStream() );  
        } catch( Exception e ) { 
            e.printStackTrace();
            return null; 
        } 
    } 

    public static boolean hasReport( String name ) {
        URL url = getResource( name ); 
        return ( url != null ); 
    }
        
    public static boolean isDeveloperMode() { 
        return developerMode; 
    } 
    
    public static File getDefaultCustomFolder() {
        File userdir = new File( System.getProperty("user.dir") ); 
        File outputdir = new File( userdir, "customreport" ); 
        if ( !outputdir.exists() ) { 
            outputdir.mkdir();
        } 
        return outputdir; 
    } 
    
    public static File getCustomFolder() { 
        return factory.getDeveloperCustomFolder(); 
    } 
    public static void setCustomFolder( File folder ) {
        factory.setDeveloperCustomFolder( folder ); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" CacheResourceCleaner ">  
    
    private static class CacheResourceCleaner implements Runnable {
        
        public void run() { 
            String cacheName = ReportURLStreamHandlerFactory.CACHE_NAME; 
            File dir = ReportURLStreamHandlerFactory.getCacheDir(); 
            if ( !dir.isDirectory() ) { return; } 
            
            File bakdir = new File( dir.getParentFile(), cacheName+"_"+System.currentTimeMillis()); 
            dir.renameTo( bakdir ); 
            
            for ( File f : bakdir.listFiles() ) { 
                try {
                    if ( f.isFile() ) {
                        f.delete(); 
                    } 
                } catch (Throwable t) { 
                    System.out.println( "[CacheResourceCleaner] " + t.getMessage() ); 
                } 
            } 
            
            try { 
                bakdir.delete(); 
            } catch( Throwable t ) {
                System.out.println( "[CacheResourceCleaner] " + t.getMessage() ); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ReportModelImpl ">  
    
    private static class ReportModelImpl extends ReportModel {

        private String name;
        private Object data; 
        
        ReportModelImpl( String name, Object data ) {
            this.name = name; 
            this.data = data; 
        }
        
        public Object getReportData() { 
            return data; 
        }

        public String getReportName() {
            return name; 
        }
        
    }
    
    // </editor-fold>
}
