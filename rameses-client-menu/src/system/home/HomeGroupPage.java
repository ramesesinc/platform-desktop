/*
 * HomeGroupPage.java
 *
 * Created on August 16, 2013, 5:51 PM
 */

package system.home;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  wflores
 */
@Template(FormPage.class)
public class HomeGroupPage extends javax.swing.JPanel {
    
    public HomeGroupPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        xTileView1 = new com.rameses.rcp.control.XTileView();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        xTileView1.setBackground(new java.awt.Color(255, 255, 255));
        xTileView1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xTileView1.setName("model"); // NOI18N
        xTileView1.setPadding(new java.awt.Insets(10, 20, 10, 0));
        jScrollPane1.setViewportView(xTileView1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XTileView xTileView1;
    // End of variables declaration//GEN-END:variables
    
}