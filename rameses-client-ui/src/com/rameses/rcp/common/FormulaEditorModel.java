/*
 * FormulaEditorModel.java
 *
 * Created on June 28, 2011, 7:27 PM
 *
 * Modified on June 24, 2013, 3:54 PM by wflores
 */

package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jaycverg
 */
public class FormulaEditorModel extends TextDocumentModel 
{
    private List<String> keywords = new ArrayList();
        
    public List<String> getKeywords() { return keywords; } 
}
