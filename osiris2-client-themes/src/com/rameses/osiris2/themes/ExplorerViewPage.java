/*
 * ExplorerViewPage2.java
 *
 * Created on April 24, 2013, 12:44 PM
 */

package com.rameses.osiris2.themes;

/**
 *
 * @author  wflores
 */
public class ExplorerViewPage extends javax.swing.JPanel {
    
    public ExplorerViewPage() {
        initComponents();
        pnlView.add(new ExplorerViewListPage());  
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlHeader = new javax.swing.JPanel();
        lblTitle = new com.rameses.rcp.control.XLabel();
        xSplitView1 = new com.rameses.rcp.control.XSplitView();
        pnlTree = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTree1 = new com.rameses.rcp.control.XTree();
        pnlView = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        pnlHeader.setLayout(new java.awt.BorderLayout());

        pnlHeader.setName("header");
        lblTitle.setBackground(new java.awt.Color(255, 255, 255));
        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideLeft(true);
        xEtchedBorder1.setHideRight(true);
        xEtchedBorder1.setHideTop(true);
        lblTitle.setBorder(xEtchedBorder1);
        lblTitle.setExpression("#{title}");
        lblTitle.setFontStyle("font-size:16; font-weight:bold;");
        lblTitle.setOpaque(true);
        lblTitle.setPadding(new java.awt.Insets(2, 7, 2, 5));
        pnlHeader.add(lblTitle, java.awt.BorderLayout.NORTH);

        add(pnlHeader, java.awt.BorderLayout.NORTH);

        xSplitView1.setDividerLocation(200);
        pnlTree.setLayout(new java.awt.BorderLayout());

        pnlTree.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 0));
        com.rameses.rcp.control.border.XLineBorder xLineBorder1 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder1.setLineColor(java.awt.SystemColor.controlShadow);
        jScrollPane1.setBorder(xLineBorder1);
        xTree1.setHandler("nodeModel");
        xTree1.setName("selectedNode");
        jScrollPane1.setViewportView(xTree1);

        pnlTree.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        xSplitView1.add(pnlTree);

        pnlView.setLayout(new java.awt.BorderLayout());

        pnlView.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 0, 2, 0));
        xSplitView1.add(pnlView);

        add(xSplitView1, java.awt.BorderLayout.CENTER);

    }// </editor-fold>//GEN-END:initComponents

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XLabel lblTitle;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTree;
    private javax.swing.JPanel pnlView;
    private com.rameses.rcp.control.XSplitView xSplitView1;
    private com.rameses.rcp.control.XTree xTree1;
    // End of variables declaration//GEN-END:variables
    
}
