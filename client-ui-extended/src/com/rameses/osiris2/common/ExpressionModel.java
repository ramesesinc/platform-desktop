/*
 * ExpressionModel.java
 *
 * Created on October 3, 2013, 10:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import java.util.List;

/**
 *
 * @author Elmo
 */
public abstract class ExpressionModel {
    
   public abstract String getValue();
   public abstract void setValue( String txt );
   
   //returns name, datatype, description
   public abstract List getVariables(String type);
    
}
