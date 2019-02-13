/*
 * ListItemStatus.java
 *
 * Created on June 5, 2013, 5:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores
 */
public class ListItemStatus 
{
    private int index;
    private int rownum;
    private String status;    
    
    private int pageIndex;
    private int pageCount;
    private int recordCount;
    private int totalRows;
    private boolean isLastPage;
    private boolean hasNextPage;
    
    public ListItemStatus(ListItem oListItem) 
    {
        if (oListItem == null) return;

        this.index = oListItem.getIndex();
        this.rownum = oListItem.getRownum();

        int state = oListItem.getState(); 
        if (state == ListItem.STATE_DRAFT) this.status = "DRAFT";
        else if (state == ListItem.STATE_EDIT) this.status = "EDIT";
        else if (state == ListItem.STATE_EMPTY) this.status = "EMPTY";
        else if (state == ListItem.STATE_SYNC) this.status = "SYNC";

        init(oListItem);         
    }
    
    public int getIndex() { return index; } 
    public int getRownum() { return rownum; } 
    public String getStatus() { return status; } 

    protected void init(ListItem oListItem) {}  
    
    public int getPageIndex() { return pageIndex; }
    public void setPageIndex(int pageIndex) { 
        this.pageIndex = pageIndex; 
    }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getRecordCount() { return recordCount; }
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public boolean isIsLastPage() { return isLastPage; }
    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }
    
    public boolean isHasNextPage() { return hasNextPage; } 
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage; 
    }
    
    public int getTotalRows() { return totalRows; } 
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
}
