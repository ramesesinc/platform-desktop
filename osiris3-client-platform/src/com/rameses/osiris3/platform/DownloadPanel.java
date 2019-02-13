/*
 * DownloadPanel.java
 *
 * Created on October 30, 2010, 11:48 AM
 * @author jaycverg
 */

package com.rameses.osiris3.platform;

import java.util.Map;
import javax.swing.UIManager;

public class DownloadPanel extends javax.swing.JPanel {
    
    public DownloadPanel() {
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();

        jProgressBar1.setIndeterminate(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setText("Downloading Modules ... Please wait.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(223, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    void startDownload() {
        OSManager osm = OSManager.getInstance();
        try {
            OSPlatformLoader.DownloadResult result = OSPlatformLoader.downloadUpdates();
            Map env = result.getEnv();

            try {
                String plaf = (String) env.get("plaf");
                if (plaf != null && plaf.trim().length() > 0) {
                    UIManager.setLookAndFeel(plaf);
                } else if (System.getProperty("os.name","").toLowerCase().indexOf("windows") >= 0) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } else {
                    //plaf = "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel";
                }
            } catch(Throwable ign) {;} 

            OSPlatform platform = osm.getPlatform();
            result.getAppLoader().load(result.getClassLoader(), env, platform); 
            osm.getMainWindow().restoreMainPanel();
        } catch(Throwable e) {
            ErrorPanel panel = new ErrorPanel(e.getClass().getName() + ": " + e.getMessage());
            osm.getMainWindow().setContent(panel); 
            e.printStackTrace(); 
        }                 
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables

}
