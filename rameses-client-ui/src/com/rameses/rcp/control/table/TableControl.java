/*
 * TableControl.java
 *
 * Created on January 31, 2011, 11:17 AM
 * @author jaycverg
 */

package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.framework.Binding;
import java.awt.Color;
import javax.swing.table.TableModel;

public interface TableControl 
{
    String getId();
    String getName();
    String getVarName();
    
    Binding getBinding();
    TableModel getModel();
    AbstractListDataProvider getDataProvider(); 
    
    Binding getItemBinding();
    
    Object createExpressionBean(Object bean);
    
    Color getEvenBackground();
    Color getOddBackground();
    Color getErrorBackground();
    Color getEvenForeground();
    Color getOddForeground();
    Color getErrorForeground();   
    
    void invokeAction( String name, Object[] args ) throws Exception; 
}
