import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;

class SysReportEditorModel {
    
    @Binding 
    def binding;

    def report; 
    def resources; 
    def outputdir;

    def context; 
    def classLoader;
    def customfolder; 
    
    void init() { 
        def paths = []; 
        paths << [path: toJrxml( report.reportName )]; 
        report.subReports?.each{ 
            paths << [path: toJrxml( it.reportname )]; 
        } 

        context = com.rameses.rcp.framework.ClientContext.currentContext; 
        classLoader = context.classLoader; 
        customfolder = context.appEnv['report.custom']; 
        if ( !customfolder ) customfolder = context.appEnv['app.custom']; 

        outputdir = report.getWorkspaceDir();         
        resources = [];        
        paths.each { 
            def targetpath = null; 
            def respath = getCustomPath( customfolder, it.path ); 
            if ( respath ) { 
                targetpath = respath; 
                
                def url = classLoader.getResource( respath ); 
                if ( url==null ) respath = it.path; 
            } else { 
                targetpath = it.path; 
                respath = it.path; 
            } 
            
            it.targetpath = targetpath; 
            it.path = respath;  
            resources << it; 
        } 
    } 

    String toJrxml( String name ) {
        if (name.endsWith(".jasper")) {
            return name.substring(0, name.lastIndexOf('.')) + '.jrxml'; 
        } else {
            return name; 
        } 
    } 

    def selectedItem;
    def listHandler = [
        fetchList: { o-> 
            return resources; 
        } 
    ] as ListPaneModel;

    def doCancel() {
        return '_close'; 
    } 

    def doBrowse() {
        outputdir = report.browseFolder(); 
        binding?.refresh(); 
    }

    def doExtract() {
        if ( !resources ) throw new Exception('No available resources to extract');

        resources.each { 
            mkdir( it.targetpath );
            
            def inp = classLoader.getResourceAsStream( it.path ); 
            def out = new java.io.FileOutputStream( new java.io.File(outputdir, it.targetpath) ); 
            com.rameses.io.IOStream.write(inp, out);  
        } 
        MsgBox.alert('Files has been successfully extracted');
        return '_close';
    } 

    void mkdir( String name ) {
        def parentdir = outputdir;
        name.split('/').each{ 
            if ( it.endsWith('.jrxml') ) return; 

            def fdir = new java.io.File( parentdir, it );
            if ( !fdir.exists() ) fdir.mkdir(); 

            parentdir = fdir; 
        } 
    } 

    def getCustomPath( customfolder, respath ) { 
        if ( !customfolder ) return null; 

        def filepath = getFilePath( respath ); 
        def folderpath = getFolderPath( respath ); 
        if ( folderpath ) {
            return folderpath +'/'+ customfolder +'/'+ filepath; 
        } else {
            return customfolder +'/'+ filepath; 
        } 
    }  
    def getFolderPath( respath ) {
        int idx = respath.lastIndexOf('/'); 
        if ( idx >= 0) { 
            return respath.substring(0, idx); 
        } else { 
            return ""; 
        } 
    }  
    def getFilePath( respath ) {
        int idx = respath.lastIndexOf('/'); 
        if ( idx >= 0 ) {
            return respath.substring( idx+1 ); 
        } else { 
            return respath; 
        } 
    } 
} 
