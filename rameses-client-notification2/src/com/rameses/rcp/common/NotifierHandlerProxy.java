/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.http.HttpClient;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Encoder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wflores
 */
class NotifierHandlerProxy {
    
    private final static Object LOCK = new Object();
    
    private final static int INTERVAL = 30;
    
    private String id; 
    private NotifierManager mgr; 
    private HashMap<String,Wrapper> handlers; 
    private Map options;

    private boolean enableWS;     
    private Looper looper;
    private Subscriber subscriber;
    
    public NotifierHandlerProxy( String id, Map options, NotifierManager mgr ) {
        this.id = id; 
        this.mgr = mgr; 
        this.handlers = new HashMap(); 
        this.options = (options != null ? options : new HashMap());
        
        String str = "" + this.options.get("enableWS"); 
        this.enableWS = (str.toLowerCase().matches("false|0") ? false : true); 
    }
    
    void subscribe() {
        if ( subscriber == null ) {
            subscriber = new Subscriber();
            mgr.executor.submit( subscriber ); 
        }
        if ( looper == null ) {
            looper = new Looper();
            looper.tokenid = this.id; 
            mgr.scheduler.schedule( looper, INTERVAL, TimeUnit.SECONDS); 
        }
    }

    public NotifierSession add( Object handler ) {
        synchronized (LOCK) {
            if ( handler == null ) return null; 
            if ( handlers.containsValue(handler)) return null; 
            
            Wrapper wrapper = new Wrapper( id, handler ); 
            handlers.put(wrapper.getUID(), wrapper);
            return wrapper.getSession(); 
        }
    }
    
    public Object remove( String uid ) {
        synchronized (LOCK) {
            if ( uid == null ) return null; 
            return handlers.remove( uid ); 
        } 
    } 
    
    public void removeAll() {
        synchronized (LOCK) {
            while ( !handlers.isEmpty()) {
                String[] keys = handlers.keySet().toArray(new String[]{}); 
                for ( String skey : keys ) {
                    handlers.remove( skey ); 
                }
            }
        }
    }
    
    void invokeHandlers( boolean immediate ) {
        synchronized (LOCK) {
            looper.resched = true; 
            
            Map param = new HashMap(); 
            param.put("id", this.id); 
            param.put("options", this.options); 
            param.put("immediate", immediate); 
            
            Wrapper[] arrs = handlers.values().toArray(new Wrapper[]{}); 
            for ( Wrapper w : arrs ) { 
                invokeHandler( w, param ); 
            } 
            
            if ( immediate ) { 
                mgr.executor.submit(new Publisher());
            } 
        }
    }
    void invokeHandler( final Wrapper w, final Map param ) {
        try {
            w.notify( param ); 
        } 
        catch(Throwable t) {
            t.printStackTrace(); 
        }
    }
    
    void notify( Object message ) { 
        Map param = new HashMap();
        param.put("id", this.id); 
        param.put("options", this.options); 
        param.put("immediate", false); 
        param.put("message", message); 
        
        Wrapper[] arr = handlers.values().toArray(new Wrapper[]{}); 
        for ( Wrapper w : arr ) {
            invokeHandler(w, param); 
        } 
    } 
    
    private class Wrapper {

        private String uid;
        private Object handler; 
        
        Wrapper( String id, Object handler ) { 
            this.handler = handler; 
            this.uid = id +"::"+ Encoder.MD5.encode(new java.rmi.server.UID().toString()); 
        } 
        
        public String getUID() { return uid; } 
        public Object getHandler() { return handler; } 

        private NotifierSessionImpl sess;         
        public NotifierSession getSession() { 
            if ( sess == null ) {
                sess = new NotifierSessionImpl( this ); 
            } 
            return sess; 
        } 

        private boolean _closed;        
        void close() {
            Object o = NotifierHandlerProxy.this.remove( getUID()); 
            _closed = true; 
        }
        
        boolean isClosed() {
            return _closed; 
        }
        
        void notify( Object data ) {
            if (handler == null) return;

            Class clazz = handler.getClass();
            Method method = null; 
            try { 
                method = clazz.getMethod("call", new Class[]{Object.class}); 
                if ( method == null ) return; 
                
                method.invoke(handler, new Object[]{ data }); 
            } 
            catch(Throwable t) {
                t.printStackTrace(); 
            } 
        }
    }
    
    public class NotifierSessionImpl implements NotifierSession {

        private Wrapper wrapper;
        private boolean _closed;
        
        NotifierSessionImpl( Wrapper wrapper ) {
            this.wrapper = wrapper;
        }
        
        public String getUID() { 
            return wrapper.getUID(); 
        }

        public void close() { 
            wrapper.close(); 
        } 
        
        public boolean isClosed() {
            return wrapper.isClosed(); 
        }
    }

    private class Subscriber implements Runnable, WSClient.MessageHandler {

        NotifierHandlerProxy root = NotifierHandlerProxy.this; 
        WSClient wsclient;
        boolean cancelled;
        String keygen;
        
        Base64Cipher base64; 
        
        Subscriber() {
            keygen = Encoder.MD5.encode(new java.rmi.server.UID().toString()); 
            base64 = new Base64Cipher();
        }
        
        public void run() { 
            try {
                runImpl();
            } catch(Throwable t) {
                t.printStackTrace(); 
            }
        }
        
        public void cancel() {
            cancelled = true; 
        }
        
        private void runImpl() throws Exception {
            if ( !root.enableWS ) return; 
            
            String host = root.mgr.getHost();
            String basePath = root.mgr.getSubscriberPath();
            if ( host != null && host.length() > 0 && basePath != null && basePath.length() > 0 ) {
                URI uri = new URI( "ws://"+ host +"/"+ basePath +"/"+ root.id );
                wsclient = new WSClient( uri ); 
                wsclient.setHandler( this ); 
                wsclient.connect(); 
            }
        }
        
        public void handleMessage(String message) { 
            if ( message == null || message.length() == 0 ) return; 
            
            if ( base64.isEncoded( message )) {
                Object o = base64.decode( message ); 
                root.notify( o ); 
            }
            else {
                root.notify( message ); 
            }
        }
    }
    
    private class Looper implements Runnable {
        
        NotifierHandlerProxy root = NotifierHandlerProxy.this; 
        
        String tokenid;
        boolean resched;
        
        public void run() { 
            try { 
                if ( resched ) {  
                    resched = false; 
                } 
                else {
                    root.invokeHandlers( false ); 
                }
            } 
            catch(Throwable t) {
                System.out.println("NotifierHandlerProxy.Looper failed caused by "+ t.getClass().getName() +": "+ t.getMessage());
            } 
            finally {
                Looper req = new Looper(); 
                req.tokenid = tokenid; 
                root.mgr.scheduler.schedule(req, INTERVAL, TimeUnit.SECONDS); 
            }
        } 
    } 
    
    private class Publisher implements Runnable {

        NotifierHandlerProxy root = NotifierHandlerProxy.this; 
        
        public void run() { 
            try { 
                runImpl();
            } 
            catch(Throwable t) {
                System.out.println("NotifierHandlerProxy.Publisher failed caused by "+ t.getClass().getName() +": "+ t.getMessage());
            } 
        }
        
        void runImpl() throws Exception {
            String host = root.mgr.getHost();
            String action = root.mgr.getPublisherPath();
            if ( host != null && host.length() > 0 ) {
                HttpClient c = new HttpClient("http://" + host); 
                c.post(action, "{id:"+ root.id +"}"); 
            }
        }
    }
}
