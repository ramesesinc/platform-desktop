/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.menu.views;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author wflores
 */
@Template(FormPage.class)
public class FXMenuCategoryPage extends javax.swing.JPanel {

    /**
     * Creates new form FXMenuCategoryPage
     */
    public FXMenuCategoryPage() {
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

        xWebView1 = new com.rameses.rcp.control.XWebView();

        setLayout(new java.awt.BorderLayout());

        xWebView1.setBorder(null);
        xWebView1.setName("menuHtml"); // NOI18N
        add(xWebView1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XWebView xWebView1;
    // End of variables declaration//GEN-END:variables
}
