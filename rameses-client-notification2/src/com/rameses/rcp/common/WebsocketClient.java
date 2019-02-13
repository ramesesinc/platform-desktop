/*
 * WebsocketClient.java
 *
 * Created on January 15, 2014, 4:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.NotificationHandler;
import com.rameses.rcp.framework.NotificationManager;
import com.rameses.util.Base64Cipher;
import com.rameses.util.MessageObject;
import java.net.URI;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;

/**
 *
 * @author wflores
 */
public class WebsocketClient 
{
    private final static ExecutorService THREAD_POOL = Executors.newFixedThreadPool(100); 
    private final static int MAX_BINARY_MESSAGE_SIZE = 16384;    
    
    public static WebsocketClient.Handle open(Map options) {
        return new WebsocketClient(options).openImpl();
    }    
    
    public static WebsocketClient.Handle open(WebsocketModel model) {
        return new WebsocketClient(model).openImpl();
    }        
    

    private String connectionid;
    private WebsocketModel model;
    private WebsocketConnectionImpl wsconn;
    private DesktopServiceImpl serviceImpl;

    private WebsocketClient() {
        this(new WebsocketModel()); 
    }
    
    private WebsocketClient(WebsocketModel model) {
        this.model = (model == null? new WebsocketModel(): model); 
        init();
        
    }

    private WebsocketClient(Map options) {
        this.model = new WebsocketModelProxy(options); 
        init();
    } 
    
    private void init() { 
        connectionid = "rcpws"+ new UID();
        wsconn = new WebsocketConnectionImpl(); 
        serviceImpl = new DesktopServiceImpl(); 
    }
    
    private WebsocketClient.Handle openImpl() {
        wsconn.initialize(); 
        return new Handle(this); 
    } 
    
    public void close() {
        if (wsconn == null) { 
            throw new RuntimeException("This instance has already been closed. Try opening another websocket client."); 
        } 
        wsconn.close(); 
        wsconn = null;
        model = null; 
    }
    
        
    // <editor-fold defaultstate="collapsed" desc=" WebsocketModelProxy "> 
    
    private class WebsocketModelProxy extends WebsocketModel 
    {
        private Map options; 
        private String host;
        private String protocol;
        private Integer maxConnection;
        private Integer reconnectDelay;
        private Integer maxIdleTime;

        private CallbackHandlerProxy onstartHandler;
        private CallbackHandlerProxy onmessageHandler;
        private CallbackHandlerProxy onreadHandler;
        private CallbackHandlerProxy oncloseHandler;
        
        WebsocketModelProxy(Map options) {
            this.options = options;
            
            host = getString(options, "host"); 
            protocol = getString(options, "protocol"); 
            maxConnection = getInt(options, "maxConnection"); 
            reconnectDelay = getInt(options, "reconnectDelay"); 
            maxIdleTime = getInt(options, "maxIdleTime"); 
            
            Object source = get(options, "onmessage"); 
            if (source != null) onmessageHandler = new CallbackHandlerProxy(source); 
            
            source = get(options, "onread"); 
            if (source != null) onreadHandler = new CallbackHandlerProxy(source);             
            
            source = get(options, "onclose"); 
            if (source != null) oncloseHandler = new CallbackHandlerProxy(source); 
            
            source = get(options, "onstart"); 
            if (source != null) onstartHandler = new CallbackHandlerProxy(source);             
        }
        
        public String getHost() { 
            if (host != null && host.length() > 0) {
                return host; 
            } else { 
                return super.getHost();
            } 
        }
        
        public String getProtocol() { 
            if (protocol != null && protocol.length() > 0) {
                return protocol;
            } else { 
                return super.getProtocol();
            } 
        } 
        
        public int getMaxConnection() { 
            if (maxConnection != null) {
                return maxConnection.intValue();
            } else { 
                return super.getMaxConnection();
            } 
        }    
        
        public int getReconnectDelay() { 
            if (reconnectDelay != null) {
                return reconnectDelay.intValue();
            } else { 
                return super.getReconnectDelay();
            } 
        }  
        
        public int getMaxIdleTime() { 
            if (maxIdleTime != null) {
                return maxIdleTime.intValue();
            } else { 
                return super.getMaxIdleTime();
            } 
        }                
        
        public void onmessage(Object data) {
            if (onmessageHandler == null) return;
            
            onmessageHandler.call(data); 
        } 
        
        public void onread(Object data) {
            if (onreadHandler == null) return;
            
            onreadHandler.call(data); 
        }         

        public void onclose() {
            if (oncloseHandler == null) return;
            
            oncloseHandler.call(); 
        } 

        public void onstart() {
            if (onstartHandler == null) return;
            
            onstartHandler.call(); 
        } 
        
        private Integer getInt(Map map, String name) {
            try {
                return (Integer) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        }

        private String getString(Map map, String name) {
            try {
                Object o = map.get(name);
                return (o == null? null: o.toString()); 
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Boolean getBool(Map map, String name) {
            try {
                return (Boolean) map.get(name);
            } catch(Throwable t) { 
                return null; 
            }
        } 
        
        private Object get(Map map, String name) {
            return (map == null? null: map.get(name)); 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" WebsocketConnectionImpl "> 
    
    private class WebsocketConnectionImpl implements WebSocket.OnTextMessage, WebSocket.OnBinaryMessage 
    {
        WebsocketClient root = WebsocketClient.this; 
        
        private WebSocketClientFactory factory;
        private WebSocketClient wsclient;
        private WebSocket.Connection connection;
        private String protocol;
        private boolean has_started;
        private boolean cancelled;
        
        void initialize() {
            initFactory();            
            root.serviceImpl.register(); 
            
            Runnable runnable = new Runnable() {
                public void run() { 
                    connect(); 
                } 
            };
            new Thread(runnable).start();
        } 
        
        void close() {
            cancelled = true; 
            if (connection != null) { 
                try { 
                    connection.close(); 
                } catch(Throwable t) {
                    System.out.println("[WebsocketClient] connection close error caused by " + t.getClass().getName() + ": " + t.getMessage()); 
                } finally { 
                    connection = null; 
                } 
                
                try { 
                    factory.stop(); 
                } catch(Throwable t) {
                    System.out.println("[WebsocketClient] factory stop error caused by " + t.getClass().getName() + ": " + t.getMessage()); 
                } finally {
                    factory = null;
                    wsclient = null;
                }
            }             
            if (root.model != null) {
                WebsocketModel old = root.model;
                root.model = null;                 
                old.onclose();
            }
            if (root.serviceImpl != null) {
                DesktopServiceImpl impl = root.serviceImpl;
                root.serviceImpl = null;                
                impl.unregister();
            }
        }
        
        private void initFactory() {
            try {
                Map headers = new HashMap();
                headers.put("connectionid", connectionid);
                String encstr = new Base64Cipher().encode(headers); 
                
                protocol = root.model.getProtocol();
                factory = new WebSocketClientFactory();
                factory.start();
                wsclient = factory.newWebSocketClient();
                wsclient.setProtocol(protocol + ";" + encstr);
                wsclient.setMaxBinaryMessageSize(MAX_BINARY_MESSAGE_SIZE); 
            } catch(RuntimeException re) {
                throw re; 
            } catch(Exception e) { 
                throw new RuntimeException(e.getMessage(), e); 
            } 
        }
        
        private void connect() {
            try {
                String host = root.model.getHost(); 
                if (!host.startsWith("ws")) host = "ws://"+ host;
                int maxConnection = root.model.getMaxConnection();  

                wsclient.open(new URI(host), this, maxConnection, TimeUnit.MILLISECONDS);
                if (!has_started) {
                    has_started = true;
                    invokeOnstart();
                }
            } catch(InterruptedException ie) {
                System.out.println("[WebsocketClient] failed to open caused by " + ie.getClass() + ": " + ie.getMessage()); 
                
            } catch(Exception e) {
                System.out.println("[WebsocketClient] failed to open caused by " + e.getClass() + ": " + e.getMessage()); 
                
                try {
                    Thread.sleep(root.model.getReconnectDelay()); 
                } catch(InterruptedException ie){;}
                
                connect();
            }
        }

        public void onOpen(WebSocket.Connection connection) {
            this.connection = connection;
            connection.setMaxIdleTime(root.model.getMaxIdleTime());
        }

        public void onClose(int closeCode, String message) {
            if (connection != null) { 
                connection.close(); 
                connection = null; 
                
                if (closeCode == 1006) { 
                    try { 
                        factory.stop(); 
                    } catch(Throwable t){;} 
                    
                    try { 
                        initFactory(); 
                    } catch(Throwable t) {
                        t.printStackTrace(); 
                    }
                    //changed the closeCode to 1000 to reconnect
                    closeCode = 1000;
                }
                
                //reconnect if max idle time reached
                if(closeCode == 1000) { 
                    try { 
                        if (!cancelled) connect(); 
                    } catch(Throwable t){ 
                        t.printStackTrace(); 
                    }
                }
            } 
        }
        
        public void onMessage(String data) {
            notify(data);
        }

        public void onMessage(byte[] bytes, int offset, int length) { 
            try {
                MessageObject mo = new MessageObject().decrypt(bytes, offset, length); 
                //if the sender and receiver uses the same connection, do not process
                if (connectionid.equals(mo.getConnectionId())) return; 
                
                Object data = mo.getData();
                if (data != null) notify(data); 
            } catch(Throwable t) {
                System.out.println("[WebsocketClient, "+ protocol + "] onMessage error caused by " + t.getClass().getName() + ": " + t.getMessage()); 
                t.printStackTrace();
            }             
        } 
        
        private void notify(Object data) { 
            try { 
                root.model.onmessage(data); 
            } catch( Throwable t) { 
                t.printStackTrace();
            } 
            
            List<NotificationHandler> list = new ArrayList();
            try { 
                list = NotificationManager.getHandlers(); 
            } catch(Throwable t) {
                t.printStackTrace(); 
                return; 
            }
            
            for (int i=0; i<list.size(); i++) { 
                try { 
                    THREAD_POOL.submit(new SendMessageProcess(list.get(i), data)); 
                } catch( Throwable t ) { 
                    continue; 
                } 
            }
        }
        
        private void invokeOnstart() { 
            try { 
                root.model.onstart(); 
            } catch(Throwable t) {
                System.out.println("onstart error caused by " + t.getClass().getName() + ": " + t.getMessage());
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DesktopServiceImpl "> 
    
    private class DesktopServiceImpl implements ClientContext.DesktopService 
    {
        WebsocketClient root = WebsocketClient.this; 
        
        void register() { 
            ClientContext.getCurrentContext().getServices().add(this); 
        }
        
        void unregister() { 
            ClientContext.getCurrentContext().getServices().remove(this); 
        }        
        
        public void start() {
        }

        public void stop() {
            if (root.wsconn != null) root.close(); 
        }
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" Handle "> 
    
    public final static class Handle 
    {
        private WebsocketClient wsclient;
        
        Handle(WebsocketClient wsclient) {
            this.wsclient = wsclient; 
        }
        
        public void close() { 
            if (wsclient != null) wsclient.close(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SendMessageProcess "> 
    
    private class SendMessageProcess implements Runnable {
        
        private Object data;
        private NotificationHandler handler; 
        
        SendMessageProcess( NotificationHandler handler, Object data ) {
            this.handler = handler; 
            this.data = data; 
        }

        public void run() {
            if ( handler != null && data != null ) {
                handler.onMessage( data ); 
            } 
        } 
    } 
    
    // </editor-fold>
}
