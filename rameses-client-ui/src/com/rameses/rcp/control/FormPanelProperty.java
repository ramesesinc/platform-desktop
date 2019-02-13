/*
 * FormPanelProperty.java
 *
 * Created on May 27, 2013, 4:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertyChangeSupport;
import java.awt.Insets;

/**
 *
 * @author wflores
 */
public interface FormPanelProperty 
{
    int getCellspacing();
    Insets getCellpadding();
    
    boolean isShowCategory();    
    PropertyChangeSupport getPropertySupport(); 
}
