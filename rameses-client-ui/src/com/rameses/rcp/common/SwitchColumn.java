/*
 * SwitchColumn.java
 *
 * Created on May 21, 2013, 5:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wflores
 */
public class SwitchColumn extends Column 
{
    private static final long serialVersionUID = 1L;
    private List conditions = new ArrayList();
    
    public SwitchColumn(String name, String caption) {
        super(name, caption); 
    }
    
    private class Condition 
    {
        
    }
}
