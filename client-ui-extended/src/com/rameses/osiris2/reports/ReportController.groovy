package com.rameses.osiris2.reports;

import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.reports.*;

public abstract class ReportController { 
        
    @Binding 
    def binding; 
    
    def mode = 'init';
    def entity = [:];
    
    public abstract def getReportData();
    public abstract String getReportName();
        
    SubReport[] getSubReports() { 
        return null; 
    }
    
    Map getParameters() { 
        return [:]; 
    }
    
    def getFormControl() { 
        return null; 
    } 
        
    def init() { 
        mode = 'init'; 
        return initReport(); 
    }
    
    def initReport() { 
        return 'default'; 
    } 
    
    void afterLoadReportParams( Map conf ) {
        new com.rameses.osiris2.common.ReportParameterLoader().load([ params: conf ]); 
    } 
    
    def getRoot() { return this; }
    
    boolean isDynamic() { return false; } 
    
    def report = [ 
        isDynamic     : { return root.isDynamic() }, 
        getReportName : { return root.getReportName() }, 
        getSubReports : { return root.getSubReports() }, 
        getReportData : { return root.getReportData() }, 
        getParameters : { return root.getParameters() },
        afterLoadReportParams: { a-> root.afterLoadReportParams(a) } 
    ] as ReportModel; 
    
    def preview() { 
        def outcome = report.viewReport(); 
        mode = 'view'; 
        return outcome; 
    } 
    
    void print() { 
        report.viewReport(); 
        ReportUtil.print( report.report, true ); 
    } 
            
    def back() { 
        mode = 'init'; 
        return 'default'; 
    } 
} 
