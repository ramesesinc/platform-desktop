/*
 * OSView.java
 *
 * Created on November 1, 2013, 7:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris3.platform;

/**
 *
 * @author wflores
 */
public interface OSView 
{
    WindowContainer getWindowContainer();
    String getId();
    String getType(); 
    void requestFocus();
    void closeView();
}
