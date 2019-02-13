/*
 * UILayoutPanel.java
 *
 * Created on July 25, 2013, 3:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.framework;

import javax.swing.JComponent;

/**
 *
 * @author wflores
 */
public interface UIContentPanel {
    
    void setContent(JComponent content, String name);
    
    void clearContent();
    
}
