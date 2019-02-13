/*
 * ExpressionModel.java
 *
 * Created on October 3, 2013, 10:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.rcp.common.Opener;
import java.util.List;

/**
 *
 * @author Elmo
 */
public abstract class ValueRangeModel {
    
    //returns name, datatype, description
    public abstract List getVars();
    public abstract Object getVar();
    public abstract void setVar(Object o);
    public abstract Opener getExpressionEditor(Object val);
    public abstract List getValue();
    public abstract void setValue(List list);
    
}
