/*
 * DBReportModel.java
 *
 * Created on September 13, 2013, 4:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.reports.ReportModel;
import com.rameses.osiris2.reports.SubReport;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.service.jdbc.DBServiceDriver;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author Elmo
 */
public abstract class DBReportModel  {
    
    private Map params = new HashMap();
    private String mode = "init"; 
    private boolean dynamic; 

    private ReportModelImpl report = new ReportModelImpl();
    private FormPanelModelImpl fpModel = new FormPanelModelImpl();
            
    public abstract String getReportName();
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    public String getMode() { return mode; }
    public void setMode( String mode ) { 
        this.mode = mode;  
    }
    
    public SubReport[] getSubReports() {
        return null; 
    }
    public Map getQuery() {
        return params; 
    }

    
    public String getContext() {
        return null; 
    }
    public String getCluster() {
        return null; 
    }
    
    public String back() {
        setMode( "init" ); 
        return "default"; 
    }
    
    
    public String viewReport() {
        report.createReport(); 
        return "report";
    }
    
    public String preview() {
        String outcome = viewReport(); 
        setMode( "view" ); 
        return outcome; 
    }
    
    public void reload() { 
        report.reload(); 
    }    
    
    public void print() { 
        print( true ); 
    } 
    
    public void print( boolean withPrintDialog ) { 
        report.print( withPrintDialog ); 
    } 
    
    public Object getReport() {
        return report;
    }       
    
    public List getFormControls() { 
        return null; 
    }  
    
    public Object getFormControl(){
        return fpModel;
    }
    
    protected void afterLoadReportParams( Map conf ) {
        Map invparams = new HashMap(); 
        invparams.put("params", conf); 
        new ReportParameterLoader().load( invparams );         
    } 
    
    
    private ClientContext currentContext; 
    private ClientContext getCurrentContext() {
        if (currentContext == null) {
            currentContext = ClientContext.getCurrentContext(); 
        }
        return currentContext; 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" CustomReportClassLoader "> 
    
    public class CustomReportClassLoader extends ClassLoader {
        private String parentName;
        public  CustomReportClassLoader(String n){
            parentName = n;
            if(parentName.trim().length()>0) {
                parentName = parentName + "/";
            }
        }
        public URL getResource(String name) {
            return getClass().getClassLoader().getResource( parentName +  name );
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Proxy services "> 
    
    private ReportParamServiceProxy svcproxy;
    private ReportParamServiceProxy getServiceProxy() {
        if ( svcproxy == null ) {
            svcproxy = (ReportParamServiceProxy) InvokerProxy.getInstance().create("ReportParameterService", ReportParamServiceProxy.class); 
        } 
        return svcproxy; 
    }
    
    public static interface ReportParamServiceProxy {
        Map getStandardParameter();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" FormPanelModelImpl "> 
    
    private class FormPanelModelImpl extends com.rameses.rcp.common.FormPanelModel {
        
        DBReportModel root = DBReportModel.this; 

        public List<Map> getControlList() {  
            return root.getFormControls(); 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ReportModelImpl "> 
    
    private class ReportModelImpl extends ReportModel { 

        DBReportModel root = DBReportModel.this; 
        
        JReportInfo reportInfo; 
        JasperReport mainReport;
        JasperPrint reportOutput;
        
        public Object getReportData() {
            return null; 
        }
        public String getReportName() { 
            return root.getReportName(); 
        }
        public Map getParameters() { 
            return root.getQuery(); 
        } 
        public SubReport[] getSubReports() {
            return root.getSubReports(); 
        }
        public JasperPrint getReport() {
            return reportOutput;
        } 
        
        public void reload() { 
            if ( reportInfo == null ) {
                reportOutput = createReport(); 
            } else { 
                JasperReport jreport = loadMainReport(); 
                reportOutput = reportInfo.fillReport( jreport );  
            } 
            if ( provider != null) { 
                provider.reload(); 
            } 
        }            
        
        JasperPrint createReport() {
            try {
                Class.forName( DBServiceDriver.class.getName() );
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.getMessage(), ex); 
            }

            mainReport = loadMainReport(); 

            Map conf = new HashMap(); 
            loadSubReport( conf );                 
            loadReportParams( conf ); 

            String parentName = "";
            String rptName = getReportName();
            if ( rptName.indexOf("/") > 0 ) { 
                parentName = rptName.substring(0, rptName.lastIndexOf("/"));
            } 
            conf.put(JRParameter.REPORT_CLASS_LOADER, new CustomReportClassLoader(parentName));

            root.afterLoadReportParams( conf ); 

            Map env = ClientContext.getCurrentContext().getAppEnv(); 
            String appUrl = env.get("app.host")+""; 
            String appContext = root.getContext(); 
            String appCluster = root.getCluster(); 
            if ( appContext==null ) appContext = env.get("app.context")+""; 
            if ( appCluster==null ) appCluster = env.get("app.cluster")+""; 

            String surl = "jdbc:rameses://"+ appUrl +"/"+ appCluster + "/" + appContext;

            JReportInfo jrpt = new JReportInfo( conf, surl ); 
            JasperPrint jprint = jrpt.fillReport( mainReport ); 
            reportOutput = jprint; 
            reportInfo = jrpt; 
            return reportOutput; 
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" JReportInfo "> 
    
    private class JReportInfo {
        
        private Map params;
        private String connString;
        
        public JReportInfo( Map params, String connString ) {
            this.params = params; 
            this.connString = connString; 
        }
        
        public Map getParams() { return params; } 

        public JasperPrint fillReport( JasperReport report ) { 
            Connection conn = null;
            try { 
                conn = DriverManager.getConnection( connString );
                return JasperFillManager.fillReport( report, getParams(), conn );
            } catch (SQLException sqle) {
                sqle.printStackTrace(); 
                throw new RuntimeException(sqle.getMessage(), sqle); 
            } catch (JRException ex) {
                ex.printStackTrace(); 
                throw new RuntimeException(ex.getMessage(), ex); 
            } finally {
                try { conn.close(); } catch(Throwable t){;}
            }
        }
    }
    
    // </editor-fold>    
}
