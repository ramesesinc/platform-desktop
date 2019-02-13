/*
 * This facility provides threadin tasks for the client.
 * Clients/Tasks are registered to this thread
 * and the execute method is called if the task is not
 * processing.
 */
package com.rameses.rcp.framework;

import com.rameses.rcp.common.ScheduledTask;
import com.rameses.rcp.common.Task;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskManager {
    
    private final static Object CACHE_LOCKED = new Object(); 
    private Map<Object, RunProc> cache = new HashMap(); 
    private ScheduledExecutorService scheduler; 
    
    public TaskManager() {
    }

    public boolean isStarted() {
        return (scheduler != null);
    }

    public void start() { 
        if ( scheduler == null ) {
            scheduler = Executors.newScheduledThreadPool(100); 
        } 
    }
    
    public void stop() { 
        synchronized (CACHE_LOCKED) { 
            try { 
                scheduler.shutdown();
            } catch(Throwable t){
                // do nothing 
            } finally {
                scheduler = null; 
            } 
            
            Object[] values = cache.values().toArray(); 
            if ( values == null ) values = new Object[0]; 
            
            for ( Object o : values ) { 
                RunProc proc = (RunProc) o; 
                try { 
                    proc.cancel(); 
                } catch(Throwable t){;} 
            } 
            
            cache.clear(); 
        }
    } 

    public void addTask( Runnable o ) {
        synchronized (CACHE_LOCKED) { 
            if ( o == null ) return; 
            
            RunProc proc = new RunProc( o ); 
            proc.future = scheduler.schedule(proc, 100, TimeUnit.MILLISECONDS ); 
        }
    }
    public void addTask( Callable o ) {
        synchronized (CACHE_LOCKED) { 
            if ( o == null ) return; 
            
            CallProc proc = new CallProc( o ); 
            proc.future = scheduler.schedule(proc, 100, TimeUnit.MILLISECONDS ); 
        }
    }
    public void addTask( Task task ) { 
        synchronized (CACHE_LOCKED) { 
            if ( task == null ) return; 

            long delay = 0;
            long interval = 0;
            TaskProc proc = new TaskProc( task ); 
            if ( task instanceof ScheduledTask ) { 
                ScheduledTask st = (ScheduledTask) task; 
                interval = st.getInterval(); 
                delay = ( st.isImmediate() ? 0 : interval ); 
            } 

            if ( interval > 0 ) { 
                delay = ( delay > 0 ? delay : 100 ); 
                interval = ( interval > 0 ? interval : 100 ); 
                proc.future = scheduler.scheduleAtFixedRate(proc, delay, interval, TimeUnit.MILLISECONDS ); 
                cache.put( task, proc );  
                
            } else { 
                proc.future = scheduler.schedule(proc, 100, TimeUnit.MILLISECONDS ); 
            }             
        } 
    } 
    
    public void removeTask( Object task ) { 
        synchronized (CACHE_LOCKED) { 
            if ( task == null ) return; 
            
            RunProc proc = cache.remove( task ); 
            if ( proc != null ) proc.cancel(); 
        } 
    }

    private class RunProc implements Runnable {

        TaskManager root = TaskManager.this; 

        Future future; 
        
        private Runnable task; 
        private boolean cancelled; 
        
        RunProc(){ 
        } 
        
        RunProc( Runnable task ) {
            this.task = task; 
        }
        
        public boolean isCancelled() {
            return this.cancelled; 
        }
        public void cancel() { 
            this.cancelled = true; 
        } 
        
        public void run() { 
            try {
                if ( task != null && !isCancelled()) {
                    task.run(); 
                }
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } finally {
                remove(); 
            }
        } 
        
        void remove() {
            try {
                root.removeTask( task ); 
            } catch(Throwable t){;} 
        }
    }

    private class CallProc extends RunProc {

        private Callable task; 
        
        CallProc( Callable task ) {
            this.task = task; 
        }
        
        public void run() { 
            try {
                if ( task != null && !isCancelled()) {
                    task.call(); 
                } 
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } finally {
                remove(); 
            }
        }        
    }
    
    private class TaskProc extends RunProc {

        private Task task; 
        private Future future; 
        
        TaskProc( Task task ) {
            this.task = task; 
        }
        
        public void cancel() { 
            super.cancel(); 
            
            if ( task != null ) {
                try {
                    task.setCancelled( isCancelled()); 
                } catch(Throwable t){;} 
            }
        } 
        
        public void run() { 
            try {
                if ( task.isCancelled() ) {
                    try { 
                        future.cancel( true ); 
                    } catch(Throwable t) {;} 
                
                    task = null; 
                    remove(); 
                    
                } else if ( isCancelled()) {
                    try { 
                        future.cancel( true ); 
                    } catch(Throwable t) {;} 

                    try { 
                        task.setCancelled( true ); 
                    } catch(Throwable t) {;} 
                    
                    task = null; 
                    remove(); 

                } else {
                    task.start(); 
                    if ( task.accept() ) { 
                        task.execute(); 
                    } 
                    if ( task.isEnded() ) { 
                        task.end();
                    } 
                } 
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } 
        } 
    }     
}
