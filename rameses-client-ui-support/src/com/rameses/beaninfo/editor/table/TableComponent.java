/*
 * TableComponent.java
 *
 * Created on June 6, 2013, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.Column;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author wflores
 */
public class TableComponent extends JTable 
{
    private ColumnEditorController controller;

    public ColumnEditorController getController() { return controller; } 
    public void setController(ColumnEditorController controller) {
        this.controller = controller;
    }
    
    public ColumnEditorModel getEditorModel() {
        return (ColumnEditorModel) getModel(); 
    }
    
    public void changeSelection1(int rowIndex, int columnIndex, boolean toggle, boolean extend) 
    {
        int oldRowIndex = getSelectedRow();
        
        super.changeSelection(rowIndex, columnIndex, toggle, extend); 
        putClientProperty("selectionPoint", new Point(columnIndex, rowIndex)); 
        
        if (oldRowIndex != rowIndex) onrowChanged();
    }    
    
    private void onrowChanged() 
    {
        ColumnEditorModel model = getEditorModel(); 
        Column col = model.getItem(getSelectedRow()); 
        refresh(col);
    }     
    
    public void refresh(Column oColumn) 
    {
        try 
        {
            controller.setColumn(oColumn);                
            controller.setEnableComponents((oColumn==null? false: true)); 
            controller.refresh();                
        } 
        catch(Exception ex) {
            ex.printStackTrace();
            showError(ex); 
        }            
    }
    
    private void showError(Throwable t) {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Window window = kfm.getActiveWindow();
        String errmsg = t.getClass().getName() + ": " + t.getMessage();
        JOptionPane.showMessageDialog(window, errmsg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }    
}
