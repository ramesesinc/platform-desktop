package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * This class makes use of a cached list.
 */
public abstract class SubListModel extends ScrollListModel 
{
    protected void fetch(boolean forceLoad) 
    {
        List dataList = getDataList();
        if (dataList == null) 
        {
            Map params = new HashMap();
            onbeforeFetchList(params);
            
            dataList = fetchList(params);
            if (dataList == null) dataList = new ArrayList();
            
            onafterFetchList(dataList);
            maxRows = dataList.size()-1;
            if (isAllocNewRow()) maxRows = maxRows + 1;
        }
                
        //reset the force load.
        List subList = new ArrayList();
        if ( dataList.size() > 0 ) 
        {
            int tail = toprow + getRows();
            if ( tail > dataList.size() ) tail = dataList.size();
            
            subList = dataList.subList(toprow, tail);
        } 
        
        fillListItems(subList, toprow);
        if (getSelectedItem() != null) {
            pageIndex = (getSelectedItem().getRownum()/ getRows())+1;
        } 
        else 
        {
            pageIndex = 1;
            setSelectedItem(0);
        }
        pageCount = ((maxRows+1) / getRows()) + (((maxRows+1) % getRows()>0)?1:0);
    }

    public void moveLastPage() {
    }
    
}
