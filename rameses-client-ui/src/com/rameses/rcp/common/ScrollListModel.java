package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ScrollListModel extends AbstractListDataProvider implements ListPageModel 
{
    public final static int PAGE_FIRST         = 0;
    public final static int PAGE_BACK          = 1;
    public final static int PAGE_NEXT          = 2;
    public final static int PAGE_LAST          = 3;
    public final static int PAGE_BACK_RECORD   = 4;
    public final static int PAGE_NEXT_RECORD   = 5;    
    
    protected int pageIndex = 1;
    protected int pageCount = 1;
    
    //this indicates the absolute row pos;
    protected int toprow;
    protected int maxRows = -1;
    
    //-1 means it has not been initialized yet
    protected int minlimit = 0;
    protected int maxlimit = 0;
    protected String searchtext;
    
    private Map query = new HashMap(); 
    private boolean hasMoreRecords;
    private int preferredRows;
    private int fetchedRows;
    private int pageMode = PAGE_FIRST;
    
    private int maxRecordCount;
    private int maxPageCount;
    
    public Map getQuery() { return query; } 
    
    public String getSearchtext() {  return searchtext; }     
    public void setSearchtext(String searchtext) 
    { 
        this.searchtext = searchtext; 
        getQuery().put("searchtext", searchtext); 
    }    
    
    public Object createItem() { 
        return new HashMap(); 
    } 
    
    public ListItemStatus createListItemStatus(ListItem oListItem) 
    {
        ListItemStatus stat = super.createListItemStatus(oListItem); 
        stat.setPageIndex(pageIndex); 
        stat.setPageCount(maxPageCount);
        stat.setIsLastPage(isLastPage()); 
        stat.setHasNextPage(fetchedRows > preferredRows); 
        return stat;
    }    
    
    public void load() 
    {
        toprow = 0;
        minlimit = 0;
        maxlimit = 0;
        maxRows = -1;
        pageIndex = 1;
        pageCount = 1;
        maxRecordCount = 0;
        maxPageCount = 0;
        fetchedRows = 0;
        preferredRows = 0;
        super.load();
    }
    
    public int getRows() { return 10; }    
    
    protected void onbeforeFetchList(Map params) 
    {
        Map qry = getQuery();
        if (qry != null) params.putAll(qry); 
    }
        
    protected void fetch(boolean forceLoad) 
    {
        ListItem selItem = getSelectedItem();
        int selIndex = (selItem == null? 0: selItem.getIndex());
        int selRownum = (selItem == null? 0: selItem.getRownum());
        
        if (getDataList() == null || forceLoad) 
        {
            int _preferredRows = getRows() * 3;            
            int _minlimit = toprow - getRows();
            if (_minlimit < 0) _minlimit = 0;
            
            Map params = new HashMap();
            onbeforeFetchList(params);
            
            params.put("_toprow", toprow);
            params.put("_start", _minlimit);
            params.put("_rowsize", _preferredRows+1);
            params.put("_limit", _preferredRows+1);
            List resultList = fetchList(params); 
            if (resultList == null) resultList = new ArrayList();
            
            onafterFetchList(resultList); 
            fetchedRows = resultList.size(); 
            preferredRows = _preferredRows;
            minlimit = _minlimit;
            hasMoreRecords = false; 
            if (resultList.size() > preferredRows) 
            {
                hasMoreRecords = true;                
                resultList.remove(resultList.size()-1); 
            } 
            
            setDataList(resultList); 
            
            // calculate the maximum number of rows first.
            int tmpMaxRows = minlimit + resultList.size()-1;
            if (isAllocNewRow()) tmpMaxRows = tmpMaxRows + 1;
            if (tmpMaxRows > maxRows) maxRows = tmpMaxRows;
            
            //calculate the maximum limit to trigger next fetch.
            maxlimit = toprow + (getRows()*2)-1;
            if (maxlimit > maxRows) maxlimit = maxRows;
            
            //determine total page count. add extra page if not yet last page.
            pageCount = ((maxRows+1)/getRows()) + ( ((maxRows+1)%getRows())>0?1:0 ); 
        }

        int tmpTotalRows = minlimit + Math.min(fetchedRows, preferredRows); 
        maxRecordCount = Math.max(maxRecordCount, tmpTotalRows); 
        maxPageCount = Math.max(maxPageCount, (tmpTotalRows/getRows())+1); 
        pageIndex = (toprow/getRows())+1; 
        
        if (toprow==0 && minlimit==0) {
            fillListItems(getDataList(), minlimit);  
        } 
        else //if (isLastItem(selItem))
        {
            int baseIndex = ((toprow-minlimit)-getRows())+1; 
            if (selIndex == 0) baseIndex = (toprow-minlimit);            
            
            baseIndex = (toprow - Math.max(selIndex, 0)) - minlimit; 
            if (baseIndex < 0) baseIndex = 0;

            List sublist = subList(getDataList(), baseIndex, getRows()); 
            fillListItems(sublist, minlimit+baseIndex);  
        }
        
        if (selItem != null) setSelectedItem(selIndex);
        if (selItem == null) setSelectedItem(0); 
    } 
    
    private List subList(List source, int start, int length) 
    {
        List list = new ArrayList();
        if (source == null || source.isEmpty()) return list;
        
        for (int i=start, len=start+length; i<len; i++) 
        {
            try { 
                list.add(source.get(i));
            } catch(Exception ex) {;} 
        } 
        return list;
    }

    
    /**
     * for moveNextPage,moveBackPage we need to force the loading.
     * for moveNextRecord and moveBackRecord, we shouls not force the load.
     * if maxRows < 0 meaning the maxRows was not determined.
     */
    public void moveNextRecord() {
        moveNextRecord(true); 
    }
    
    public void moveNextRecord(boolean includesEmptyItem) 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 

        int newToprow = getSelectedItem().getRownum()+1;
        if (isTopRowVisible(newToprow))
        {
            ListItem li = getListItemByRownum(newToprow); 
            setSelectedItem(li.getIndex()); 
            fireFocusSelectedItem(); 
        } 
        else {  
            moveTopRowUp(newToprow); 
        }
    }
    
    public void moveBackRecord() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 
        
        int newToprow = getSelectedItem().getRownum()-1;
        if (isTopRowVisible(newToprow))
        {
            ListItem li = getListItemByRownum(newToprow); 
            setSelectedItem(li.getIndex()); 
            fireFocusSelectedItem(); 
        }         
        else { 
            moveTopRowDown(newToprow); 
        }
    } 
    
    public void moveNextPage() 
    { 
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 
        if (isLastPage()) return;
        
        int newToprow = toprow + getRows();
        if (newToprow >= (minlimit+preferredRows) && fetchedRows > preferredRows) 
        {
            this.toprow = newToprow;
            refresh(true);
        } 
        else  
        {
            this.toprow = newToprow;
            refresh();            
        }
    }
    
    public void moveBackPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        if (getSelectedItem() == null) return; 
        
        int newToprow = toprow - getRows();
        if (newToprow < 0) 
        {
            newToprow = 0;
            setSelectedItem(0); 
        }
        
        this.toprow = newToprow;
        if (newToprow < minlimit && minlimit > 0)  
            refresh(true);
        else 
            refresh();
    }
    
    public void moveFirstPage() 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        toprow = 0;
        refresh();
    }
    
    public void moveLastPage() {}
    
    /**
     * this method sets the top row.
     * check first if the top row is possible.
     * if the toprow value is not possible,
     * do nothing. toprow is possible only if
     * it it does not exceed getRowCount() - getRows()
     */
    public int getTopRow() { return toprow; }    
    public void setTopRow( int toprow ) 
    {
        //do not scroll when there are error in validation
        if (getMessageSupport().hasErrorMessages()) return;
        
        //if the toprow is current do not proceed.
        if (this.toprow == toprow) return;
        if (isTopRowVisible(toprow))
        {
            ListItem li = getListItemByRownum(toprow); 
            setSelectedItem(li.getIndex()); 
            fireFocusSelectedItem(); 
        }
        else if (toprow > this.toprow) {
            moveTopRowUp(toprow);
        }
        else if (toprow < this.toprow) {
            moveTopRowDown(toprow);
        }
    }
    
    private void moveTopRowUp(int newToprow) 
    {
        int xlimit = (minlimit + preferredRows);
        if (newToprow >= xlimit && fetchedRows > preferredRows) 
        {
            this.toprow = newToprow;
            refresh(true);
        }
        else if (newToprow <= (minlimit + fetchedRows))
        {
            this.toprow = newToprow; 
            refresh();
        }
    }
    
    private void moveTopRowDown(int newToprow) 
    {
        //System.out.println("moveTopRowDown... " + newToprow);
        this.toprow = Math.max(newToprow, 0);        
        if (newToprow < minlimit && minlimit > 0) { 
            //System.out.println("moveTopRowDown: newToprow="+newToprow + ", minlimit="+minlimit);
            refresh(true); 
        } else { 
            refresh(); 
        } 
    }   
    
    private int getPageViewIndex(int toprow) 
    {
        int index = toprow / getRows(); 
        int rem = toprow % getRows(); 
        return (rem > 0? index+1: index); 
    }
    

    protected void onselectedItemChanged(ListItem li) { 
        this.toprow = (li == null? 0: li.getRownum());  
    } 
      
    public final void doSearch() {
        load();
    }
    
    public int getPageIndex() { return pageIndex; }
    
    public int getRowCount() 
    {
        if (isAllocNewRow()) return maxRows - 1;

        return maxRows;
    }
    
    public boolean isLastPage() 
    {
        if (pageIndex < pageCount) 
            return false; 
        else if (fetchedRows <= preferredRows)
            return true; 
        else if (pageIndex > pageCount) 
            return true; 
        else 
            return false;
    }
    
    /**
     * This function is used internally to check if we need to allocate
     * a new row for new item. If true, this will add an extra row in the
     * list.
     */
    private Boolean allocNewRow;
    protected boolean isAllocNewRow() 
    {
        if (allocNewRow == null) 
        {
            if (createItem() != null) 
                allocNewRow = new Boolean(true); 
            else 
                allocNewRow = new Boolean(false); 
        } 
        return allocNewRow.booleanValue();
    }
    
    public int getMaxRows() { return maxRows; }    
    public int getPageCount() { return pageCount; }
    
    private boolean isTopRowVisible(int toprow) 
    {
        ListItem firstLI = getFirstItem(); 
        if (firstLI == null) return false;
        
        ListItem lastLI = getLastItem(); 
        if (lastLI == null) return false;
        
        return (toprow >= firstLI.getRownum() && toprow <= lastLI.getRownum()); 
    }
}
