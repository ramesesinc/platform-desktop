/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.reports;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author wflores
 */
public abstract class BasicReportModel extends ReportModel {

    public abstract Object getReportData();
    public abstract JasperReport getMainReport(); 

    public String getReportName() { 
        return null; 
    }
}
