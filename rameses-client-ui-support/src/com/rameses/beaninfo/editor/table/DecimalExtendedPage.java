/*
 * DecimalExtendedPage.java
 *
 * Created on May 20, 2013, 12:42 PM
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.DecimalColumnHandler;

/**
 *
 * @author  wflores
 */
public class DecimalExtendedPage extends javax.swing.JPanel implements IExtendedPage 
{
    private DecimalColumnHandler typeHandler;
    
    public DecimalExtendedPage() {
        initComponents();
    }
    
    public Column.TypeHandler getTypeHandler() { return typeHandler; }
    public void setTypeHandler(Column.TypeHandler typeHandler) 
    {
        this.typeHandler = new DecimalColumnHandler();
        if (typeHandler instanceof DecimalColumnHandler)
        {
            DecimalColumnHandler old = (DecimalColumnHandler) typeHandler;
            this.typeHandler.setFormat(old.getFormat());
            this.typeHandler.setMinValue(old.getMinValue());
            this.typeHandler.setMaxValue(old.getMaxValue()); 
            this.typeHandler.setUsePrimitiveValue(old.isUsePrimitiveValue()); 
            this.typeHandler.setScale(old.getScale()); 
        }
    }      
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        txtFormat = new com.rameses.rcp.swingx.TextField();
        chkPrimitive = new com.rameses.rcp.swingx.CheckField();
        integerField1 = new com.rameses.rcp.swingx.IntegerField();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setText("Format:");

        txtFormat.setName("format");

        chkPrimitive.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkPrimitive.setText(" Primitive Value?");
        chkPrimitive.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkPrimitive.setName("usePrimitiveValue");

        integerField1.setText("integerField1");
        integerField1.setName("scale");

        jLabel2.setText("Scale:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(29, 29, 29)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkPrimitive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(integerField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(integerField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkPrimitive, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.swingx.CheckField chkPrimitive;
    private com.rameses.rcp.swingx.IntegerField integerField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private com.rameses.rcp.swingx.TextField txtFormat;
    // End of variables declaration//GEN-END:variables
    
}
