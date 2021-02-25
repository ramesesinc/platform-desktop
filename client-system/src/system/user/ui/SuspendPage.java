package system.user.ui;

import com.rameses.rcp.constant.TextCase;
/*
 * LoginPage.java
 *
 * Created on August 5, 2010, 3:25 PM
 */

/**
 *
 * @author  rameses
 */
public class SuspendPage extends javax.swing.JPanel {
    
    /** Creates new form LoginPage */
    public SuspendPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jLabel2 = new javax.swing.JLabel();
        formPanel1 = new com.rameses.rcp.util.FormPanel();
        xPasswordField1 = new com.rameses.rcp.control.XPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        xButton2 = new com.rameses.rcp.control.XButton();
        xButton4 = new com.rameses.rcp.control.XButton();
        xButton3 = new com.rameses.rcp.control.XButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 50, 10));
        jPanel2.setLayout(new com.rameses.rcp.control.layout.YLayout());

        xLabel1.setExpression("Session Timeout");
        xLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0));
        xLabel1.setFontStyle("font-size:14; font-weight:bold;");
        xLabel1.setForeground(new java.awt.Color(60, 60, 60));
        xLabel1.setUseHtml(true);
        jPanel2.add(xLabel1);

        jLabel2.setText("<html>To resume your session, enter your password</html>");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 10, 0));
        jPanel2.add(jLabel2);

        formPanel1.setCaptionVAlignment(com.rameses.rcp.constant.UIConstants.CENTER);
        formPanel1.setCaptionWidth(90);

        xPasswordField1.setCaption("Password");
        xPasswordField1.setName("pwd"); // NOI18N
        xPasswordField1.setText("xPasswordField1");
        xPasswordField1.setCaptionWidth(100);
        xPasswordField1.setPreferredSize(new java.awt.Dimension(200, 24));
        xPasswordField1.setRequired(true);
        formPanel1.add(xPasswordField1);

        jPanel2.add(formPanel1);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0));

        xButton2.setMnemonic('R');
        xButton2.setName("resume"); // NOI18N
        xButton2.setDefaultCommand(true);
        xButton2.setMargin(new java.awt.Insets(5, 14, 5, 14));
        xButton2.setText("Resume");
        jPanel1.add(xButton2);

        xButton4.setMnemonic('L');
        xButton4.setName("logoff"); // NOI18N
        xButton4.setImmediate(true);
        xButton4.setMargin(new java.awt.Insets(5, 14, 5, 14));
        xButton4.setText("Log Off");
        xButton4.setEnabled(false); 
        jPanel1.add(xButton4);

        xButton3.setMnemonic('u');
        xButton3.setName("exit"); // NOI18N
        xButton3.setImmediate(true);
        xButton3.setMargin(new java.awt.Insets(5, 14, 5, 14));
        xButton3.setText("Shutdown");
        jPanel1.add(xButton3);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jSeparator1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.util.FormPanel formPanel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XButton xButton3;
    private com.rameses.rcp.control.XButton xButton4;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XPasswordField xPasswordField1;
    // End of variables declaration//GEN-END:variables
    
}
