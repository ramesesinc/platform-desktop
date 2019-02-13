package com.rameses.osiris2.reports;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.reports.*;
import com.rameses.common.*;

public abstract class AsyncReportController {
    
    final def clientContext = com.rameses.rcp.framework.ClientContext.currentContext;
    
    @Binding
    def binding;
    
    @Service('DateService')
    def dtSvc;
    
    def mode;
    def reportdata;
    def entity = [:];
    
    def data;
    def asyncHandler;
    def has_result_preview = false; 
    
    
    abstract String getReportName();
    abstract void buildReportData(entity, asyncHandler);
    
    def getFormControl(){
        return null;
    }
    
    SubReport[] getSubReports(){
        return null;
    }
    
    Map getParameters(){
        return [:]
    }
    
    
    def initReport(){
        return 'default'
    }
    
    def init() {
        def parsedate = dtSvc.parseCurrentDate();
        entity.year = parsedate.year;
        entity.qtr  = parsedate.qtr;
        entity.month = getMonthsByQtr().find{it.index == parsedate.month}
        mode = 'init'
        return initReport();
    }
    
    void buildResult( data ) {
    }
    
    def preview() {
        asyncHandler = [
            onError: {o-> 
                println o;
                MsgBox.err(o?.message + ""); 
                back();
                binding.refresh(); 
            }, 
            onTimeout: {
                asyncHandler.retry(); 
            },
            onCancel: {
                //binding.fireNavigation( back() );
            }, 
            onMessage: {o-> 
                if (o == com.rameses.common.AsyncHandler.EOF) {
                    if (!has_result_preview) {
                        back();
                        binding.refresh(); 
                    } 
                    
                } else if (o instanceof Throwable) { 
                    MsgBox.err(o.message); 
                    asyncHandler.cancel();
                    back();
                    binding.refresh();
                    
                } else {
                    if (o instanceof Map) {
                        data = o; 
                    } else { 
                        data = [reportdata: o]; 
                    }
                    
                    has_result_preview = true; 
                    buildResult( data ); 
                    if ( data.reportdata == null ) {
                        MsgBox.err( "Please specify reportdata on your result" ); 
                    } else {
                        binding.fireNavigation( buildReport( data.reportdata ) ); 
                    }
                }
            } 
        ] as com.rameses.common.AbstractAsyncHandler 
        
        has_result_preview = false; 
        buildReportData(entity, asyncHandler); 
        mode = 'processing'; 
        return null; 
    } 
        
    void print() {
        asyncHandler = [
            onError: {o-> 
                MsgBox.err(o.message); 
                back();
                binding.refresh(); 
            }, 
            onTimeout: {
                asyncHandler.retry(); 
            },
            onCancel: {
                //back();
                //binding.refresh(); 
            }, 
            onMessage: {o-> 
                if (o == com.rameses.common.AsyncHandler.EOF) {
                    if (!has_result_preview) {
                        back();
                        binding.refresh(); 
                    } 
                    
                } else if (o instanceof Throwable) { 
                    MsgBox.err(o.message); 
                    asyncHandler.cancel();
                    back();
                    binding.refresh(); 
                    
                } else {
                    data = o;                
                    has_result_preview = true; 
                    buildResult( data ); 
                    
                    if ( data.reportdata == null ) {
                        MsgBox.err( "Please specify reportdata on your result" ); 
                    } else { 
                        printReport( data.reportdata ); 
                        //back(); 
                    }                         
                    binding.refresh(); 
                }
            } 
        ] as com.rameses.common.AbstractAsyncHandler 
        
        has_result_preview = false; 
        buildReportData(entity, asyncHandler); 
        mode = 'processing'; 
    } 
        
    def buildReport( data ) {
        reportdata = data; 
        report.viewReport();
        mode = 'view'; 
        return 'preview'; 
    }

    final def getRoot() {
        return this; 
    }
    
    def report = [
        getReportName : { return getReportName(); }, 
        getSubReports : { return getSubReports(); },
        getReportData : { return reportdata; },
        getParameters : { return getRoot().getParameters(); }
    ] as ReportModel;
    
    def back() {
        mode = 'init'
        return 'default' 
    }
    
    List getQuarters() {
        return [1,2,3,4];
    }
        
    List getMonthsByQtr() {
        return dtSvc.getMonthsByQtr( entity.qtr );
    }
    
    List getMonths(){
        return getMonthsByQtr();
    }

    def cancel() {
        asyncHandler?.cancel(); 
        return back(); 
    }         
}
