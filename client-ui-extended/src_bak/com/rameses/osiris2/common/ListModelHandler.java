/*
 * ListControllerHandler.java
 *
 * Created on April 28, 2013, 10:20 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

interface ListModelHandler 
{
    Object getSelectedEntity();
    
    void addItem(Object data);
    
    void updateItem(Object data); 
    
    void removeItem(Object data);
    
}
