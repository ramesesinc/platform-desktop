/*
 * DefaultReportModel.java
 *
 * Created on August 6, 2013, 6:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.client.reports;

import com.rameses.osiris2.reports.ReportModel;

/**
 *
 * @author Elmo
 */
public class DefaultReportModel extends ReportModel {
    
    private Object reportData;
    private String reportName;
    
    /** Creates a new instance of DefaultReportModel */
    public DefaultReportModel() {
    }

    public Object getReportData() {
        return reportData;
    }

    public void setReportData(Object reportData) {
        this.reportData = reportData;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    
}
