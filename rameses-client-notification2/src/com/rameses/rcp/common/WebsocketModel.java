/*
 * WebsocketModel.java
 *
 * Created on January 15, 2014, 4:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author wflores 
 */
public class WebsocketModel 
{
    public final static int DEFAULT_MAX_CONNECTION  = 35000;
    public final static int MAX_IDLE_TIME           = 60000;    
    public final static int RECONNECT_DELAY         = 2000;

    public WebsocketModel() {
    }
    
    public String getProtocol() {
        throw new NullPointerException("Please specify protocol");
    }
    
    public String getHost() {
        throw new NullPointerException("Please specify host");
    }
    
    public int getMaxConnection() { return DEFAULT_MAX_CONNECTION; }    
    public int getReconnectDelay() { return RECONNECT_DELAY; }
    public int getMaxIdleTime() { return MAX_IDLE_TIME; } 
    
    public void onstart() {
    }
    
    public void onmessage(Object data) {
    }
    
    public void onclose() {
    }         
}
