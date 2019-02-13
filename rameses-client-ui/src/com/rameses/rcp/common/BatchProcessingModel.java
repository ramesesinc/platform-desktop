/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.util.BreakException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class BatchProcessingModel implements BatchProcessingHandler {
    
    private final static Object START_LOCKED = new Object();
    private final static int DEFAULT_ROW_SIZE = 10;
    
    private final static int MODE_INIT       = 0; 
    private final static int MODE_PROCESSING = 1; 
    private final static int MODE_CANCELLED  = 2; 
    private final static int MODE_FINISHED   = 3; 
    private final static int MODE_ERROR      = 4; 
    
    private ArrayList<BatchProcessingHandler> handlers = new ArrayList();
    private VarStat stat = new VarStat();
    private RunProc proc;

    public abstract int getTotalCount();
    public abstract List fetchList(Map params); 
    public abstract void processItem( Object item ); 
    
    public void onFinished() {} 
    public void onError( Object stat ) {} 
    public void onRefresh( Object stat ) {} 
    
    public void beforeStart() {}
    public void beforeCancel() {}
    public void afterCancel() {}
    
    public ProgressStatus getStatus() {
        return new ProgressStatus(); 
    }
    
    public boolean isAsync() { 
        return true; 
    } 
    
    public int getRows() { 
        return DEFAULT_ROW_SIZE; 
    }
    
    public void start() {
        synchronized( START_LOCKED ) {
            if ( proc != null ) { 
                System.out.println("batch processing has already started.");
                return; 
            }

            try {
                beforeStart();
                
                proc = new RunProc(); 
                proc.init();             
            } catch(BreakException be) {
                proc = null; 
                return; 
            } catch(RuntimeException re) {
                proc = null; 
                throw re; 
            } catch(Exception e) { 
                proc = null; 
                throw new RuntimeException(e); 
            } 
            
            if ( isAsync() ) {
                new Thread( proc ).start(); 
            } else { 
                proc.run(); 
            } 
        }
    }
    
    public void cancel() { 
        try {
            beforeCancel(); 
        } catch(BreakException be) {
            return; 
        }
        
        if ( proc != null ) {
            proc.cancel(); 
        } 
        proc = null; 
        afterCancel();
    }
    
    
    public void add( BatchProcessingHandler handler ) {
        if ( handler != null && !handlers.contains( handler )) {
            handlers.add( handler );
        }
    }
    public void remove( BatchProcessingHandler handler ) {
        if ( handler != null ) {
            handlers.remove( handler ); 
        }
    }
    public void removeHandlers() {
        handlers.clear(); 
    }
    
    private void notifyOnRefresh( Object stat ) {
        onRefresh( stat ); 
        for ( BatchProcessingHandler h : handlers ) {
            h.onRefresh( stat ); 
        } 
    } 
    private void notifyOnError( Object stat ) {
        onError( stat ); 
        for ( BatchProcessingHandler h : handlers ) {
            h.onError( stat ); 
        } 
    } 
    private void notifyOnFinished() {
        onFinished(); 
        for ( BatchProcessingHandler h : handlers ) {
            h.onFinished(); 
        } 
    } 

    
    public class ProgressStatus {
        
        BatchProcessingModel root = BatchProcessingModel.this;
        
        public int getValue() {
            return root.stat.startRow; 
        }
        
        public int getMaxValue() {
            return root.stat.totalCount; 
        }
        
        public String getProgressValue() {
            Number value = getValue();
            Number maxvalue = getMaxValue();
            Number num = (value.doubleValue() / maxvalue.doubleValue()) * 100.0; 
            return (""+ num.intValue() +"%"); 
        }
        
        public String getLabel() { 
            int mode = root.stat.mode; 
            if ( mode == MODE_CANCELLED ) {
                return "Operation cancelled.";
            } else if( mode == MODE_INIT ) {
                return "Press Start to begin";
            } else if ( mode == MODE_PROCESSING ) { 
                int value = getValue();
                int maxvalue = getMaxValue();
                return "Processing Data...  ("+ value +" of "+ maxvalue +")"; 
            } else if ( mode == MODE_ERROR ) {
                return "Processed with errors..."; 
            } else if ( mode == MODE_FINISHED ) {
                return "Successfully processed "+ getValue() +" items"; 
            } else {
                return ""; 
            }
        }
        
        public String getMode() {
            int mode = root.stat.mode; 
            if ( mode == MODE_CANCELLED ) {
                return "cancelled";
            } else if( mode == MODE_INIT ) {
                return "init";
            } else if ( mode == MODE_PROCESSING ) { 
                return "processing"; 
            } else if ( mode == MODE_ERROR ) {
                return "error"; 
            } else if ( mode == MODE_FINISHED ) {
                return "finish"; 
            } else { 
                return null; 
            }
        }
        
        public Throwable getError() {
            return root.stat.error; 
        }
        
        public String getErrorMessage() {
            Throwable t = getError(); 
            return ( t == null ? null : t.getMessage()); 
        }        
    }
        
    private class VarStat {
        int mode;
        int startRow;
        int rowLimit;
        int totalCount;
        Throwable error; 
        
        void init() { 
            mode = MODE_INIT;
            startRow = 0;
            rowLimit = 0;
            totalCount = 0;
            error = null; 
        }
    }
    
    private class RunProc implements Runnable {

        BatchProcessingModel root = BatchProcessingModel.this;
        
        Object[] items;
        boolean cancelled; 
        
        void init() {
            items = null; 
            cancelled = false;
            root.stat.init(); 
        }
        
        void cancel() {
            cancelled = true; 
            root.stat.mode = MODE_CANCELLED; 
        }
        
        public void run() { 
            VarStat stat = root.stat;
            ProgressStatus pg = new ProgressStatus();
            try {
                items = null; 
                runImpl( pg );
            } catch(Throwable t) {
                stat.mode = MODE_ERROR; 
                stat.error = t; 
                root.notifyOnRefresh( pg ); 
                root.notifyOnError( pg ); 
            }
        }
        
        void runImpl( ProgressStatus pg ) throws Exception { 
            VarStat stat = root.stat;
            stat.mode = MODE_PROCESSING;
            stat.rowLimit = root.getRows();
            if ( stat.rowLimit <= 0 ) {
                stat.rowLimit = DEFAULT_ROW_SIZE;
            } 

            stat.totalCount = root.getTotalCount(); 
            while ( fetch() ) { 
                boolean hasmore = (items.length > stat.rowLimit); 
                int count = (hasmore ? stat.rowLimit : items.length); 
                for (int i=0; i<count; i++) {
                    if ( cancelled ) break; 
                    if ( stat.startRow >= stat.totalCount ) break; 
                    
                    stat.startRow += 1; 
                    root.processItem( items[i] ); 
                    root.notifyOnRefresh( pg ); 
                } 
                
                if ( cancelled ) break; 
                if ( stat.startRow >= stat.totalCount ) break; 
            }

            if ( cancelled ) {
                stat.mode = MODE_CANCELLED; 
                root.notifyOnRefresh( pg ); 
                return; 
            }
            
            items = null; 
            stat.mode = MODE_FINISHED; 
            root.notifyOnRefresh( pg ); 
            root.notifyOnFinished(); 
        }
        
        boolean fetch() { 
            if ( cancelled ) return false; 
            
            VarStat stat = root.stat;
            HashMap param = new HashMap();
            param.put("_start", stat.startRow);
            param.put("_limit", stat.rowLimit+1);
            
            List list = root.fetchList( param ); 
            items = (list == null ? null : list.toArray()); 
            return (items != null && items.length > 0); 
        }
    }
}
