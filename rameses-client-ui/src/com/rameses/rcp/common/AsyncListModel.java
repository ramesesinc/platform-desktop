package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 * This is a PageListModel similar to subitem model, however
 * the list model builds its list from a background thread.
 */
public abstract class AsyncListModel extends ScrollListModel implements Runnable, ProgressInfo 
{
    private Thread thread;
    private boolean cancelled;
    private int batchNo;
        
    public AsyncListModel() {
    }
    
    public void init() 
    {
        if (isAutoStart()) start();

        refresh();
    }
    
    public void start() 
    {
        if (thread != null) return;
        
        toprow = 0;
        pageIndex = 1;
        pageCount = 1;
        thread = new Thread(this);
        thread.start();
    }
    
    protected int getDelay() { return 2000; }
  
    
    /**
     * The fetch portion occurs when the user navigates like
     * nextPage, nextRecord, etc.
     */
    protected void fetch(boolean forceLoad) 
    {
        List dataList = getDataList();
        if (dataList == null) return;
        if (toprow > dataList.size()) return;
        
        synchronized(dataList) 
        {
            List subList = new ArrayList();
            if ( maxRows > 0 ) 
            {
                int tail = toprow + getRows();
                if (tail > dataList.size()) tail = dataList.size();
                
                subList = dataList.subList(toprow, tail);
            }
            
            fillListItems(subList,toprow);
            if (getSelectedItem() != null) 
                pageIndex = (getSelectedItem().getRownum()/ getRows())+1;
            else 
                setSelectedItem(0);
        }
    }
    
    public void cancel() {
        cancelled = true;
    }
    
    
    public void run() 
    {
        toprow = 0;
        batchNo = 1;
        getListItems().clear();
        
        cancelled = false;
        
        int rowsize = getRows();
        while (!cancelled) 
        {
            int startrow = (batchNo-1) * rowsize;
            Map params = new HashMap();
            onbeforeFetchList(params);
            
            params.put("_start", startrow);
            params.put( "_rowsize", rowsize+1);
            params.put( "_limit", rowsize+1);
            params.put( "_batch", batchNo);
            
            List subList = fetchList(params);
            if (subList == null) subList = new ArrayList(); 
            
            onafterFetchList(subList);
            boolean firstTime = false;
            List dataList = getDataList();
            if (dataList == null) 
            {
                firstTime = true;
                dataList = new ArrayList();
            }
            
            setDataList(dataList);
            synchronized (dataList) 
            {
                //if this is the first time, we need to referesh the items
                for (Object o: subList) { 
                    if (dataList.indexOf(o) < 0) dataList.add(o);
                } 
                
                //recalculate the new size of the list and the page count.
                maxRows = dataList.size() -1 ;
                
                //the extra row should be added only once during the first pass.
                if (isAllocNewRow()) maxRows = maxRows + 1;
                
                pageCount = ((maxRows+1) / getRows()) + (((maxRows+1) % getRows()>0)?1:0);
            }
            
            
            if (firstTime) 
                refresh();
            //else 
            //    super.refreshSelectedItem();
            
            //check also if sub list size is 0. If 0 then exit
            //this can be overridden by the cancel feedback from the client
            //during fetchList event.
            
            if (subList.size() == 0) cancelled = true;
                        
            //if a cancel signal exists, it will override the default
            //cancelled behavior
            if (params.get("cancel") != null) 
            {
                try {
                    cancelled =  Boolean.valueOf(params.get("cancel")+"");
                } catch(Exception ign){;}
            }
           
            //exit if cancelled
            if (cancelled) break;
            
            try {
                Thread.sleep(getDelay());
            } catch (InterruptedException ex) {
                ;
            }
            batchNo++;
        }
        thread = null;
    }
        
    public int getEstimatedMaxSize() {
        return maxRows;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public boolean isStarted() {
        return (thread!=null);
    }
    
    protected boolean isAutoStart() {
        return false;
    }

    protected void onfinalize() throws Throwable {
        cancelled = true;
    }
}
