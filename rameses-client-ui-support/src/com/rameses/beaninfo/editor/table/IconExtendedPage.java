/*
 * IconExtendedPage.java
 *
 * Created on August 22, 2013, 5:43 PM
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.IconColumnHandler;

/**
 *
 * @author  wflores
 */
public class IconExtendedPage extends javax.swing.JPanel implements IExtendedPage
{    
    private IconColumnHandler typeHandler;
    
    public IconExtendedPage() {
        initComponents();
    }
    
    public Column.TypeHandler getTypeHandler() { return typeHandler; }
    public void setTypeHandler(Column.TypeHandler typeHandler) {
        this.typeHandler = new IconColumnHandler(); 
    } 
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 367, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 54, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
