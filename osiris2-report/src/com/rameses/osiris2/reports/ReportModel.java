/*
 * ReportModel.java
 *
 * Created on November 25, 2009, 2:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports;

import com.rameses.common.PropertyResolver;
import com.rameses.osiris2.client.Inv;
import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.URLStreamHandlers;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author elmo
 */
public abstract class ReportModel {
    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;

    private boolean dynamic = false;    
    private boolean allowSave = true;
    private boolean allowPrint = true;
    private boolean allowBack = false;
    private boolean ignorePagination;
    
    protected final PropertyResolver propertyResolver = PropertyResolver.getInstance();
    
    public abstract Object getReportData();
    public abstract String getReportName();
    
    public boolean isDynamic() { return dynamic; } 
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic; 
    }
    
    public boolean isIgnorePagination() { return ignorePagination; }
    public void setIgnorePagination( boolean ignorePagination ) {
        this.ignorePagination = ignorePagination; 
    }
    
    public boolean isAllowSave() { return allowSave; }
    public void setAllowSave(boolean allowSave) {
        this.allowSave = allowSave;
    }
    
    public boolean isAllowPrint() { return allowPrint; }
    public void setAllowPrint(boolean allowPrint) {
        this.allowPrint = allowPrint;
    }
        
    public boolean isAllowBack() { return allowBack; } 
    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack; 
    }
    
    public boolean isAllowEdit() { 
        return ReportUtil.isDeveloperMode(); 
    } 
    
    public SubReport[] getSubReports() { return null; }
    
    public Map getParameters() { return null; }
    
    protected void afterLoadReportParams( Map conf ) {
        //do nothing 
    }

    protected void afterReportData( Object data ) {
        //do nothing 
    }
    
    public boolean beforePrint() { 
        return true; 
    } 
    public void afterPrint() { 
        //do nothing 
    }
    
    
    private JasperPrint reportOutput;
    private JReportInfo reportInfo;
    private JasperReport mainReport;
    
    public JasperReport getMainReport() {
        return null; 
    }
    
    protected JasperReport loadMainReport() { 
        if (ReportUtil.isDeveloperMode()) { 
            mainReport = null; 
            updateWorkspaceDir(); 
        } 
        
        JasperReport jrpt = getMainReport(); 
        if ( jrpt != null ) { 
            mainReport = jrpt;  
        } else if (mainReport == null || isDynamic()) { 
            mainReport = ReportUtil.getJasperReport( getReportName() );
        } 
        return mainReport; 
    }
    
    protected void loadSubReport( Map conf ) {
        SubReport[] subReports = getSubReports();
        if (subReports != null) {
            for (SubReport sr: subReports) {
                conf.put( sr.getName(), sr.getReport() );
            }
        }
    }
    
    protected void loadReportParams( Map conf ) { 
        ReportParamServiceProxy reportParamProxy = getServiceProxy(); 
        if ( reportParamProxy != null ) {
            Map params = reportParamProxy.getStandardParameter(); 
            if ( params != null ) { 
                conf.putAll( params ); 
            } 
        }
        
        Map params = getParameters(); 
        if ( params != null ) { 
            conf.putAll( params ); 
        } 
        
        Map appenv = ClientContext.getCurrentContext().getAppEnv(); 
        Iterator keys = appenv.keySet().iterator(); 
        while ( keys.hasNext() ) { 
            Object key = keys.next(); 
            conf.put("ENV_"+ key.toString().toUpperCase().replace('.','_'), appenv.get(key)); 
        } 
        
        JRParameter[] jrparams = mainReport.getParameters(); 
        if ( jrparams != null ) {
            for ( JRParameter jrp : jrparams ) {
                String pname = jrp.getName(); 
                try { 
                    if ( jrp.isSystemDefined() ) { continue; }
                    if ( pname.indexOf('.') <= 0 ) { continue; } 

                    Object pvalue = propertyResolver.getProperty(conf, pname); 
                    conf.put( pname, pvalue ); 
                } catch(Throwable t) {
                    System.out.println("Error on parameter [" + pname  + "] caused by " + t.getMessage());
                }
            }
        } 
        
        conf.put("REPORT_UTIL", new ReportDataUtil());
        conf.put("REPORTHELPER", new ReportDataSourceHelper()); 
        
        String reportPath = "";
        String rptName = getReportName();
        if ( rptName != null && rptName.indexOf("/") > 0 ) { 
            reportPath = rptName.substring(0, rptName.lastIndexOf("/"));
        } 
        conf.put(JRParameter.REPORT_CLASS_LOADER, new CustomReportClassLoader(reportPath));
        conf.put(JRParameter.REPORT_URL_HANDLER_FACTORY, URLStreamHandlers.getFactory()); 
        if ( isIgnorePagination()) conf.put(JRParameter.IS_IGNORE_PAGINATION, true); 
    } 
    
    private JasperPrint createReport() {
        loadMainReport(); 

        Object data = getReportData(); 
        afterReportData( data );
        
        Map conf = new HashMap();
        loadSubReport( conf );
        loadReportParams( conf );
        afterLoadReportParams( conf ); 
        
        JReportInfo jrpt = new JReportInfo( data, conf ); 
        JasperPrint jprint = jrpt.fillReport( mainReport ); 
        reportInfo = jrpt; 
        return jprint; 
    }
    
    public String viewReport() {
        reportOutput = createReport();
        return "report";
    }

    public void exportToPDF() throws Exception { 
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode( JFileChooser.FILES_ONLY ); 
        jfc.setMultiSelectionEnabled( false ); 
            
        int opt = JFileChooser.CANCEL_OPTION;
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 
        if (win instanceof Frame) {
            opt = jfc.showSaveDialog((Frame)win); 
        } else if (win instanceof Dialog ) {
            opt = jfc.showSaveDialog((Dialog)win); 
        } else {
            opt = jfc.showSaveDialog((Frame) null); 
        }

        if ( opt == JFileChooser.APPROVE_OPTION ) {
            exportToPDF( jfc.getSelectedFile() ); 
        }
    }  
    
    public void exportToPDF( File file ) throws Exception { 
        FileOutputStream fos = null; 
        try { 
            JasperPrint jprint = createReport(); 
            fos = new FileOutputStream( file ); 
            JasperExportManager.exportReportToPdfStream(jprint, fos); 
        } finally { 
            try { fos.close(); }catch(Throwable t){;} 
        } 
    } 
    
    public void reload() { 
        reportOutput = createReport(); 
        if ( provider != null) { 
            provider.reload(); 
        } 
    } 
    
    public JasperPrint getReport() {
        return reportOutput;
    }
    
    public void print() { 
        print( true ); 
    } 
    
    public void printNoDialog() { 
        print( false ); 
    }

    public void print( boolean withPrintDialog ) { 
        try { 
            ReportUtil.print( createReport(), withPrintDialog ); 
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) { 
            throw new RuntimeException(e.getMessage(), e); 
        } 
    }     

    public List getReportActions() {
        List list = new ArrayList();
        list.add( new Action("_close", "Close", null));
        
        List<Action> xactions = lookupActions("reportActions");
        while (!xactions.isEmpty()) {
            Action a = xactions.remove(0);
            if ( ! containsAction(list, a)){
                list.add(a);
            }
        }
        return list;
    }
    
    private boolean containsAction(List<Action> list, Action a){
        for (Action aa : list){
            if (aa.getName().equals(a.getName()))
                return true;
        }
        return false;
    }
    
    protected final List<Action> lookupActions(String type) {
        List<Action> actions = new ArrayList();
        try {
            actions = InvokerUtil.lookupActions(type, new InvokerFilter() {
                public boolean accept(com.rameses.osiris2.Invoker o) {
                    return o.getWorkunitid().equals(invoker.getWorkunitid());
                }
            });
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup actions caused by " + t.getMessage());
        }
        
        for (int i=0; i<actions.size(); i++) {
            Action newAction = actions.get(i).clone();
            actions.set(i, newAction);
        }
        return actions;
    }
    
    //this method is invoked by the back button
    public Object back() { return "_close"; }
    public Object edit() { 
        try { 
            Map params = new HashMap(); 
            params.put("report", this); 
            return Inv.lookupOpener("sysreport:edit", params); 
        } catch(Throwable t) { 
            return null; 
        } 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" CustomReportClassLoader "> 
    
    class CustomReportClassLoader extends ClassLoader {
        
        private String basepath;
        
        public  CustomReportClassLoader(String basepath ){
            this.basepath = basepath;
            if ( basepath != null && basepath.trim().length() > 0 ) { 
                this.basepath = basepath + "/"; 
            } else { 
                this.basepath = "/"; 
            } 
        }
        public URL getResource(String name) { 
            URL url = ReportUtil.getResource( this.basepath +  name ); 
            if ( url != null ) return url; 
            
            return getClass().getClassLoader().getResource( this.basepath +  name );
        }
    } 
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Provider "> 
    
    public static interface Provider { 
        Object getBinding(); 
        File browseFolder(); 
        void reload(); 
    }
    
    
    protected Provider provider; 
    public void setProvider( Provider provider ) {
        this.provider = provider; 
    } 
    
    public Object getBinding() { 
        return (provider == null? null: provider.getBinding()); 
    }  
    
    private File workspacedir;  
    public File getWorkspaceDir() { 
        return workspacedir; 
    } 
    public void setWorkspaceDir( File workspacedir ) {
        if ( workspacedir == null ) { 
            workspacedir = ReportUtil.getDefaultCustomFolder(); 
        } 
        this.workspacedir = workspacedir; 
        ReportUtil.setCustomFolder( workspacedir ); 
    }
    public void updateWorkspaceDir() {
        if ( getWorkspaceDir() == null ) { 
            // set null to load the defaults 
            setWorkspaceDir( null ); 
        } 
        ReportUtil.setCustomFolder( getWorkspaceDir() ); 
    }
    public File browseFolder() { 
        File newdir = null; 
        if ( provider != null ) { 
            newdir = provider.browseFolder(); 
        } 
        setWorkspaceDir( newdir ); 
        return ReportUtil.getCustomFolder(); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" JReportInfo "> 
    
    private class JReportInfo {
        
        private Object data;
        private Map params;
        
        public JReportInfo( Object data, Map params ) {
            this.data = data; 
            this.params = params; 
        }
        
        public Object getData() { return data; } 
        public Map getParams() { return params; } 
        
        public JRDataSource createDataSource() {
            if ( data == null ) { 
                return new JREmptyDataSource(); 
            } else {
                return new ReportDataSource( data );
            }            
        }   
        
        public JasperPrint fillReport( JasperReport report ) {
            JRDataSource ds = createDataSource(); 
            try { 
                return JasperFillManager.fillReport( report, getParams(), ds );
            } catch (JRException ex) {
                ex.printStackTrace(); 
                throw new IllegalStateException(ex.getMessage(), ex); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Proxy services "> 
    
    public String getReportParameterServiceName() {
        return "ReportParameterService"; 
    }
    
    private ReportParamServiceProxy svcproxy;
    private ReportParamServiceProxy getServiceProxy() {
        if ( svcproxy == null ) { 
            String sname = getReportParameterServiceName(); 
            if ( sname == null ) return null; 
            
            svcproxy = (ReportParamServiceProxy) InvokerProxy.getInstance().create("ReportParameterService", ReportParamServiceProxy.class); 
        } 
        return svcproxy; 
    }
    
    public static interface ReportParamServiceProxy {
        Map getStandardParameter();
    }
    
    // </editor-fold>    
}
