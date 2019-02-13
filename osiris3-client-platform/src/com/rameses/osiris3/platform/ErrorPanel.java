/*
 * ErrorPanel.java
 *
 * Created on October 30, 2010, 11:41 AM
 * @author jaycverg
 */

package com.rameses.osiris3.platform;

import java.awt.EventQueue;

public class ErrorPanel extends javax.swing.JPanel 
{    
    public ErrorPanel() {
        initComponents();
    }
    
    public ErrorPanel(String details) {
        initComponents();
        lblDetails.setText(details);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        lblDetails = new javax.swing.JLabel();
        btnRetry = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setText("An error occured.  See details below.");

        lblDetails.setText("details here.");
        lblDetails.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        btnRetry.setText("Retry");
        btnRetry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRetryActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 284, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnRetry))
                    .add(lblDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnRetry)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblDetails, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRetryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRetryActionPerformed
        
        EventQueue.invokeLater(new Runnable() {
            public void run() { 
                try { 
                    OSManager.getInstance().retryUpdate();
                } catch(Throwable t) {
                    lblDetails.setText(getMessage(t)+""); 
                    lblDetails.revalidate();
                    lblDetails.repaint(); 
                }
            }
        });
        
    }//GEN-LAST:event_btnRetryActionPerformed
    
    private String getMessage(Throwable t) { 
        if (t == null) return null; 
        
        String msg = t.getMessage();
        Throwable cause = t.getCause();
        while (cause != null) { 
            String s = cause.getMessage();
            if (s != null) msg = s;
            
            cause = cause.getCause();
        } 
        return msg; 
    } 
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRetry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblDetails;
    // End of variables declaration//GEN-END:variables
    
}
