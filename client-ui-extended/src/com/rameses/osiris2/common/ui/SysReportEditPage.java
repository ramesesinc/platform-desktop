/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.osiris2.common.ui;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author wflores
 */
@Template(FormPage.class)
public class SysReportEditPage extends javax.swing.JPanel {

    /**
     * Creates new form SysReportEditPage
     */
    public SysReportEditPage() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xButton3 = new com.rameses.rcp.control.XButton();
        jPanel1 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xList1 = new com.rameses.rcp.control.XList();
        xButton1 = new com.rameses.rcp.control.XButton();
        xButton2 = new com.rameses.rcp.control.XButton();

        jPanel3.setLayout(new java.awt.BorderLayout());

        xTextField1.setCaption("Output Directory");
        xTextField1.setName("outputdir.absolutePath"); // NOI18N
        xTextField1.setFocusable(false);
        xTextField1.setPreferredSize(new java.awt.Dimension(100, 25));
        xTextField1.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        jPanel3.add(xTextField1, java.awt.BorderLayout.CENTER);

        xLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        xLabel2.setFontStyle("font-weight:bold;");
        xLabel2.setText("Output Directory :");
        jPanel3.add(xLabel2, java.awt.BorderLayout.NORTH);

        xButton3.setMnemonic('b');
        xButton3.setName("doBrowse"); // NOI18N
        xButton3.setImmediate(true);
        xButton3.setText("Browse...");
        xButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xButton3ActionPerformed(evt);
            }
        });
        jPanel3.add(xButton3, java.awt.BorderLayout.LINE_END);

        jPanel1.setLayout(new java.awt.BorderLayout());

        xLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        xLabel1.setText("Resources :");
        xLabel1.setFontStyle("font-weight:bold;");
        jPanel1.add(xLabel1, java.awt.BorderLayout.PAGE_START);

        xList1.setExpression("#{item.path}");
        xList1.setHandler("listHandler");
        xList1.setName("selectedItem"); // NOI18N
        xList1.setFixedCellHeight(18);
        jScrollPane1.setViewportView(xList1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        xButton1.setMnemonic('e');
        xButton1.setText("Extract");
        xButton1.setImmediate(true);
        xButton1.setName("doExtract"); // NOI18N
        xButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xButton1ActionPerformed(evt);
            }
        });

        xButton2.setMnemonic('c');
        xButton2.setText("Cancel");
        xButton2.setImmediate(true);
        xButton2.setName("doCancel"); // NOI18N
        xButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 494, Short.MAX_VALUE)
                .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void xButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xButton1ActionPerformed

    private void xButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xButton2ActionPerformed

    private void xButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XButton xButton3;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XList xList1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
}
