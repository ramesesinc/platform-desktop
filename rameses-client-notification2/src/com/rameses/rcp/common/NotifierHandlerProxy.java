/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

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
            mgr.scheduler.schedule( looper, 10, TimeUnit.SECONDS); 
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
    
    void invokeHandlers() {
        synchronized (LOCK) {
            Wrapper[] arrs = handlers.values().toArray(new Wrapper[]{}); 
            for ( Wrapper w : arrs ) { 
                invokeHandler( w ); 
            } 
        }
    }
    void invokeHandler( final Wrapper w ) {
        final Map param = new HashMap(); 
        param.put("id", this.id); 
        param.put("options", this.options); 
        w.notify( param ); 
        /*
        Runnable run = new Runnable() {
            public void run() { 
                w.notify( param ); 
            } 
        }; 
        this.mgr.executor.submit( run ); 
        */
    }
    
    void notify( Object param ) {
        Wrapper[] arr = handlers.values().toArray(new Wrapper[]{}); 
        for ( Wrapper w : arr ) {
            w.notify( param ); 
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
            
            String basePath = root.mgr.getUriString(); 
            if ( basePath != null && basePath.length() > 0 ) {
                URI uri = new URI( basePath +"/"+ root.id );
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
    
    public interface INotifierService { 
        Object getNotified( Object param ); 
    }
    
    private class Looper implements Runnable {
        
        NotifierHandlerProxy root = NotifierHandlerProxy.this; 
        
        String tokenid;
        
        public void run() { 
            try {
                root.invokeHandlers(); 
            } 
            catch(Throwable t) {
                System.out.println("NotifierHandlerProxy.Looper failed caused by "+ t.getClass().getName() +": "+ t.getMessage());
            } 
            finally {
                Looper req = new Looper(); 
                req.tokenid = tokenid; 
                root.mgr.scheduler.schedule(req, 10, TimeUnit.SECONDS); 
            }
        }
        
        /*
        void runImpl() throws Exception {
            ClientContext cctx = ClientContext.getCurrentContext(); 
            Map appenv = cctx.getAppEnv();
            Map newenv = new HashMap();
            newenv.put("app.host",    getConf(appenv, "app.host", "localhost:8070"));
            newenv.put("app.context", getConf(appenv, "app.context", "notification"));
            newenv.put("app.cluster", getConf(appenv, "app.cluster", null)); 
            newenv.put("readTimeout", getConf(appenv, "readTimeout", "60000")); 
            
            ScriptServiceContext ssc = new ScriptServiceContext(newenv); 
            INotifierService svc = ssc.create("NotifierService", cctx.getHeaders(), INotifierService.class); 
            Map param = new HashMap();
            param.put("id", root.id);
            param.put("tokenid", tokenid); 
            svc.getNotified( param ); 
        }
        
        private String getConf(Map map, String name, Object defaultValue) {
            Object value = (map == null ? null : map.get(name)); 
            if (value == null) {
                return (defaultValue == null ? null : defaultValue.toString()); 
            } else { 
                return value.toString(); 
            }
        }
        */
    }
}
