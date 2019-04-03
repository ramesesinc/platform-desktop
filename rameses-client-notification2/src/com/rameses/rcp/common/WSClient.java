/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 *
 * @author wflores
 */
@ClientEndpoint
public class WSClient {
    
    private Session sess;
    private MessageHandler handler;
    private WebSocketContainer wc; 
    private URI uri;

    LinkedBlockingQueue q;

    public WSClient( URI uri ) {
        this.uri = uri;
        this.q = new LinkedBlockingQueue();
    }

    public void connect() {
        if ( wc == null ) {
            wc = ContainerProvider.getWebSocketContainer(); 
        }
        
        while ( true ) {
            try { 
                wc.connectToServer(this, uri); 
                break; 
            } 
            catch (Throwable t) { 
                System.out.println("[wsclient] failed to connect... "+ uri +" caused by "+ t.getClass().getName() +": "+ t.getMessage());
                //t.printStackTrace(); 

                try { 
                    System.out.println("[wsclient] reconnecting after 3 secs...");
                    q.poll(3, TimeUnit.SECONDS); 
                } catch(Throwable ign) {;}
            } 
        }
    }

    @OnOpen
    public void onOpen(Session sess) {
        this.sess = sess;
        afterOpen();
    }

    @OnClose
    public void onClose(Session sess, CloseReason reason) {
        this.sess = null;
        afterClose();
        connect();
    }        

    @OnMessage
    public void onMessage(String message) {
        handleMessage( message ); 
    } 
    
    public void handleMessage( String message ) {
        if ( this.handler != null ) {
            this.handler.handleMessage( message ); 
        }
    }

    public void afterClose() {}
    public void afterOpen() {}

    public boolean isConnected() {
        return (sess != null); 
    }

    public void setHandler(MessageHandler handler) {
        this.handler = handler;
    }

    public void sendMessage(String message) {
        this.sess.getAsyncRemote().sendText(message);
    }        
    
    public static interface MessageHandler {
        public void handleMessage(String message);
    }        
}
