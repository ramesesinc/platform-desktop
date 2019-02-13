/*
 * ColumnDataTable.java
 *
 * Created on August 21, 2013, 10:19 PM
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
 * @author compaq
 */
public class ColumnDataTable extends JTable {
        
    private ColumnEditorModel model;
    private ColumnEditorController controller;

    public ColumnDataTable() {
    }
    
    public ColumnEditorModel getEditorModel() { return model; } 
    public void setEditorModel(ColumnEditorModel model) {
        this.model = model; 
        super.setModel(model); 
    }
    
    public ColumnEditorController getEditorController() {
        return controller;
    }
    public void setEditorController(ColumnEditorController controller) {
        this.controller = controller; 
    }
    
    public void selectRow(int rowIndex) 
    {
        if (rowIndex >= 0 && rowIndex < model.getRowCount()) 
        {
            Point selPoint = (Point) getClientProperty("selectionPoint");
            if (selPoint == null) selPoint = new Point(); 

            changeSelection(rowIndex, selPoint.x, false, false); 
        }
    }

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) 
    {
        int oldRowIndex = getSelectedRow();
        super.changeSelection(rowIndex, columnIndex, toggle, extend); 
        putClientProperty("selectionPoint", new Point(columnIndex, rowIndex)); 

        if (oldRowIndex != rowIndex) onrowChanged();
    }

    private void onrowChanged() 
    {
        Column col = model.getItem(getSelectedRow()); 
        refresh(col);
    }

    public void refresh(Column col) 
    {
        try 
        {
            controller.setColumn(col);                
            controller.setEnableComponents((col==null? false: true)); 
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
