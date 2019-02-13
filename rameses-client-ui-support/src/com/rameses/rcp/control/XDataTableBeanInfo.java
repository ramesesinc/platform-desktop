/*
 * XButtonBeanInfo.java
 *
 * Created on May 4, 2013, 11:00 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.beaninfo.ComponentBeanInfoSupport;
import com.rameses.beaninfo.editor.ColumnPropertyEditor;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 *
 * @author wflores
 */
public class XDataTableBeanInfo extends ComponentBeanInfoSupport
{
    private Class beanClass;
    
    public Class getBeanClass() {
        if (beanClass == null) { 
            beanClass = XDataTable.class;
        }
        return beanClass; 
    }
    
    protected void loadProperties(List<PropertyDescriptor> list) { 
        addBoolean( list, "autoResize" );
        addBoolean( list, "showHorizontalLines" );
        addBoolean( list, "showVerticalLines" );
        addBoolean( list, "editable" );
        addBoolean( list, "immediate" );
        addBoolean( list, "readonly" );
        addBoolean( list, "showRowHeader" );
        addBoolean( list, "showColumnHeader" );
        
        add( list, "columns", ColumnPropertyEditor.class ); 
        add( list, "borderColor" );
        add( list, "cellSpacing" );
        add( list, "errorBackground" );
        add( list, "errorForeground" );
        add( list, "evenBackground" );
        add( list, "evenForeground" );
        add( list, "gridColor" );
        add( list, "handler", true );
        add( list, "items", true );
        add( list, "id" );
        add( list, "varName" );
        add( list, "varStatus" );
        add( list, "multiSelectName" );
        add( list, "oddBackground" );
        add( list, "oddForeground" );
        add( list, "readonlyWhen" );
        add( list, "rowHeight" );
        add( list, "rowHeaderHeight" );
    }
}
