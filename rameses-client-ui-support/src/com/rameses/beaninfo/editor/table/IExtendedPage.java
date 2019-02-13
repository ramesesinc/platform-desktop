/*
 * IExtendedPage.java
 *
 * Created on May 28, 2013, 1:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.Column;

/**
 *
 * @author wflores
 */
public interface IExtendedPage 
{
    Column.TypeHandler getTypeHandler();
    void setTypeHandler(Column.TypeHandler typeHandler); 
}
