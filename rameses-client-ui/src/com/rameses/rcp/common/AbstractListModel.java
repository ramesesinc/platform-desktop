/*
 * AbstractListModel.java
 *
 * Created on January 14, 2010, 8:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

/**
 *
 * @author elmo
 */
public abstract class AbstractListModel extends AbstractListDataProvider 
{
    private Column primaryColumn;
    private boolean primaryColChecked;
    
    /*
     *  abstract methods
     */
    public abstract void moveNextRecord();
    public abstract void moveBackRecord();
    public abstract void moveFirstPage();
    public abstract void moveNextPage();
    public abstract void moveBackPage();
    
    public abstract void setTopRow(int row);
    public abstract int getTopRow();
    public abstract int getMaxRows();
    
    
    /**
     * This method is only called once. when initiating.
     * most do not have an implementation. Most notably used
     * by AsyncListModel.
     */
    public void init() {}        
}
