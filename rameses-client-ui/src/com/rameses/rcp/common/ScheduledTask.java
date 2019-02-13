/*
 * ScheduledTask.java
 *
 * Created on April 10, 2010, 4:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

/**
 *
 * @author elmo
 */
public abstract class ScheduledTask extends Task 
{
    private long expiryTime;
    
    public ScheduledTask() {
        if(isImmediate()) {
            expiryTime = System.currentTimeMillis();
        }
        else {
            restart();
        }
    }
    
    public abstract long getInterval();
    
    public boolean accept() {
        return System.currentTimeMillis() >= expiryTime;
    }
    
    public boolean isEnded() {
        restart();
        return false;
    }
    
    public boolean isImmediate() {
        return false;
    }
    
    public void restart() {
        expiryTime = System.currentTimeMillis() + getInterval();
    }
} 
