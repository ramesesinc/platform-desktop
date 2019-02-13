/*
 * ColumnEditorPage.java
 *
 * Created on August 21, 2013, 3:55 PM
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.beaninfo.editor.table.ColumnEditorController.DependHandler;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.swingx.ComboItem;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Comparator;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

/**
 *
 * @author  wflores
 */
public class ColumnEditorPage2 extends javax.swing.JPanel {
    
    private PropertyEditor propertyEditor;
    private ColumnEditorController controller;
    private ColumnEditorModel model; 

    public ColumnEditorPage2() {
        initComponents();
        
        model = new ColumnEditorModel();        
        tblcolumn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        tblcolumn.setEditorModel(model); 
        
        controller = new ColumnEditorController(){
            protected void onvalueChanged(String name) 
            {
                int row = tblcolumn.getSelectedRow();
                if (row >= 0) model.fireTableRowsUpdated(row, row); 
                
                if (propertyEditor != null)
                    propertyEditor.setValue(model.getColumns()); 
            }
        }; 
        tblcolumn.setEditorController(controller); 
        controller.registerComponents(this);
        controller.addExtendedPage("text", new TextExtendedPage()); 
        controller.addExtendedPage("integer", new IntegerExtendedPage()); 
        controller.addExtendedPage("checkbox", new CheckExtendedPage()); 
        controller.addExtendedPage("combobox", new ComboExtendedPage()); 
        controller.addExtendedPage("date", new DateExtendedPage()); 
        controller.addExtendedPage("double", new DecimalExtendedPage()); 
        controller.addExtendedPage("decimal", new DecimalExtendedPage()); 
        controller.addExtendedPage("label", new LabelExtendedPage()); 
        controller.addExtendedPage("lookup", new LookupExtendedPage()); 
        controller.addExtendedPage("opener", new OpenerExtendedPage()); 
        controller.addExtendedPage("button", new ButtonExtendedPage()); 
        controller.addExtendedPage("icon", new IconExtendedPage()); 
        controller.setEnableComponents(false); 
        
        cboTextcase.setItems(new ComboItem[]{
           new ComboItem("(Default)", null), 
           new ComboItem("UPPER", TextCase.UPPER), 
           new ComboItem("LOWER", TextCase.LOWER), 
           new ComboItem("NONE", TextCase.NONE)  
        });
        

        cboAlignment.setItems(new ComboItem[]{
            new ComboItem("(Default)", null), new ComboItem("LEFT"), 
            new ComboItem("CENTER"), new ComboItem("RIGHT")
        });
        cboAlignment.setUpdateable(true);
        cboAlignment.setComparator(new Comparator() {
            public boolean equals(Object obj) { return false; }
            public int compare(Object o1, Object o2) {
                String s1 = o1+"";
                String s2 = o2+"";
                return s1.equalsIgnoreCase(s2)? 1: 0;
            } 
        });
        
        cbotype.setItems(new ComboItem[]{
           new ComboItem("text"),
           new ComboItem("integer"),
           new ComboItem("checkbox"),
           new ComboItem("combobox"),
           new ComboItem("date"),
           new ComboItem("double"),
           new ComboItem("decimal"),
           new ComboItem("label"),
           new ComboItem("lookup"),
           new ComboItem("opener"),
           new ComboItem("button"),
           new ComboItem("icon")
        });
        cbotype.setUpdateable(false); 
        cbotype.addItemListener(new TypeHandler());    
        
        chkEditable.setSelected(false); 
        chkEditable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                txtEditableWhen.setEnabled(e.getStateChange()==ItemEvent.SELECTED); 
            }
        });
        txtEditableWhen.putClientProperty(DependHandler.class, new DependHandler("editable", txtEditableWhen)); 
        
        chkVisible.setSelected(true); 
        chkVisible.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                txtVisibleWhen.setEnabled(e.getStateChange()==ItemEvent.SELECTED); 
            }
        });
        txtVisibleWhen.putClientProperty(DependHandler.class, new DependHandler("visible", txtVisibleWhen)); 
    }
    
    public void setPropertyEditor(PropertyEditor propertyEditor) 
    {
        this.propertyEditor = propertyEditor;         
        if (propertyEditor != null) 
        {
            propertyEditor.addPropertyChangeListener(new PropertyChangeListener() 
            {
                private boolean enabled = true;
                
                public void propertyChange(PropertyChangeEvent evt) 
                {
                    if (!enabled) return;
                    
                    enabled = false; 
                    onpropertyChange(evt);                    
                }
            });
        }
    }
    
    protected void onpropertyChange(PropertyChangeEvent e) 
    {
        Column[] columns = (Column[]) (propertyEditor==null? null: propertyEditor.getValue());
        model.setColumns(columns);
        model.fireTableDataChanged(); 
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLeft = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblcolumn = new com.rameses.beaninfo.editor.table.ColumnDataTable();
        toolbarpanel = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        pnlbody = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlInfo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtname = new com.rameses.rcp.swingx.TextField();
        txtcaption = new com.rameses.rcp.swingx.TextField();
        txtwidth = new com.rameses.rcp.swingx.IntegerField();
        txtwidth3 = new com.rameses.rcp.swingx.IntegerField();
        txtwidth4 = new com.rameses.rcp.swingx.IntegerField();
        chkRequired = new com.rameses.rcp.swingx.CheckField();
        chkResizable = new com.rameses.rcp.swingx.CheckField();
        chkNWE = new com.rameses.rcp.swingx.CheckField();
        chkEditable = new com.rameses.rcp.swingx.CheckField();
        txtEditableWhen = new com.rameses.rcp.swingx.TextField();
        chkVisible = new com.rameses.rcp.swingx.CheckField();
        txtVisibleWhen = new com.rameses.rcp.swingx.TextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboTextcase = new com.rameses.rcp.swingx.ComboField();
        jLabel11 = new javax.swing.JLabel();
        cboAlignment = new com.rameses.rcp.swingx.ComboField();
        jLabel12 = new javax.swing.JLabel();
        txtExpression = new com.rameses.rcp.swingx.TextField();
        cbotype = new com.rameses.rcp.swingx.ComboField();
        pnlExtended = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        tblcolumn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblcolumn);

        btnAdd.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        btnAdd.setText("+");
        btnAdd.setMargin(new java.awt.Insets(1, 7, 1, 7));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        btnRemove.setText("-");
        btnRemove.setMargin(new java.awt.Insets(1, 7, 1, 7));
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnUp.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        btnUp.setText("Up");
        btnUp.setMargin(new java.awt.Insets(1, 5, 1, 5));
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });

        btnDown.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        btnDown.setText("Down");
        btnDown.setMargin(new java.awt.Insets(1, 5, 1, 5));
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout toolbarpanelLayout = new org.jdesktop.layout.GroupLayout(toolbarpanel);
        toolbarpanel.setLayout(toolbarpanelLayout);
        toolbarpanelLayout.setHorizontalGroup(
            toolbarpanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolbarpanelLayout.createSequentialGroup()
                .add(btnAdd)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnRemove)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 47, Short.MAX_VALUE)
                .add(btnUp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnDown))
        );
        toolbarpanelLayout.setVerticalGroup(
            toolbarpanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolbarpanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btnAdd)
                .add(btnRemove)
                .add(btnDown)
                .add(btnUp))
        );

        org.jdesktop.layout.GroupLayout pnlLeftLayout = new org.jdesktop.layout.GroupLayout(pnlLeft);
        pnlLeft.setLayout(pnlLeftLayout);
        pnlLeftLayout.setHorizontalGroup(
            pnlLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlLeftLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, toolbarpanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlLeftLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(toolbarpanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        add(pnlLeft, java.awt.BorderLayout.WEST);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Column Information ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        pnlInfo.setLayout(null);

        jLabel1.setText("Name:");
        pnlInfo.add(jLabel1);
        jLabel1.setBounds(18, 14, 52, 18);

        jLabel2.setText("Caption:");
        pnlInfo.add(jLabel2);
        jLabel2.setBounds(18, 40, 52, 18);

        jLabel6.setText("Width:");
        pnlInfo.add(jLabel6);
        jLabel6.setBounds(18, 68, 52, 18);

        jLabel7.setText("Min Width:");
        pnlInfo.add(jLabel7);
        jLabel7.setBounds(154, 68, 62, 18);

        jLabel8.setText("Max Width:");
        pnlInfo.add(jLabel8);
        jLabel8.setBounds(298, 68, 62, 18);

        jLabel3.setText("Handler:");
        pnlInfo.add(jLabel3);
        jLabel3.setBounds(20, 270, 64, 22);

        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlInfo.add(jLabel4);
        jLabel4.setBounds(10, 96, 422, 2);

        jLabel5.setText("jLabel5");
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlInfo.add(jLabel5);
        jLabel5.setBounds(10, 190, 422, 2);

        txtname.setName("name"); // NOI18N
        pnlInfo.add(txtname);
        txtname.setBounds(70, 14, 352, 20);

        txtcaption.setName("caption"); // NOI18N
        pnlInfo.add(txtcaption);
        txtcaption.setBounds(70, 40, 352, 20);

        txtwidth.setText("integerField1");
        txtwidth.setName("width"); // NOI18N
        pnlInfo.add(txtwidth);
        txtwidth.setBounds(70, 68, 62, 20);

        txtwidth3.setText("integerField1");
        txtwidth3.setName("minWidth"); // NOI18N
        pnlInfo.add(txtwidth3);
        txtwidth3.setBounds(216, 68, 62, 20);

        txtwidth4.setText("integerField1");
        txtwidth4.setName("maxWidth"); // NOI18N
        pnlInfo.add(txtwidth4);
        txtwidth4.setBounds(360, 68, 62, 20);

        chkRequired.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkRequired.setText("Required");
        chkRequired.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkRequired.setName("required"); // NOI18N
        pnlInfo.add(chkRequired);
        chkRequired.setBounds(70, 108, 76, 18);

        chkResizable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkResizable.setText("Resizable");
        chkResizable.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkResizable.setName("resizable"); // NOI18N
        pnlInfo.add(chkResizable);
        chkResizable.setBounds(154, 108, 76, 18);

        chkNWE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkNWE.setText("Null When Empty");
        chkNWE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkNWE.setName("resizable"); // NOI18N
        pnlInfo.add(chkNWE);
        chkNWE.setBounds(236, 108, 186, 18);

        chkEditable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkEditable.setText("Visible");
        chkEditable.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkEditable.setName("visible"); // NOI18N
        pnlInfo.add(chkEditable);
        chkEditable.setBounds(70, 157, 76, 18);

        txtEditableWhen.setName("visibleWhen"); // NOI18N
        pnlInfo.add(txtEditableWhen);
        txtEditableWhen.setBounds(154, 157, 268, 20);

        chkVisible.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkVisible.setText("Editable");
        chkVisible.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkVisible.setName("editable"); // NOI18N
        pnlInfo.add(chkVisible);
        chkVisible.setBounds(70, 132, 76, 18);

        txtVisibleWhen.setName("editableWhen"); // NOI18N
        pnlInfo.add(txtVisibleWhen);
        txtVisibleWhen.setBounds(154, 132, 268, 20);

        jLabel9.setText("jLabel5");
        jLabel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlInfo.add(jLabel9);
        jLabel9.setBounds(10, 260, 422, 2);

        jLabel10.setText("Text Case:");
        pnlInfo.add(jLabel10);
        jLabel10.setBounds(20, 200, 62, 22);

        cboTextcase.setName("textCase"); // NOI18N
        pnlInfo.add(cboTextcase);
        cboTextcase.setBounds(80, 200, 108, 22);

        jLabel11.setText("Alignment:");
        pnlInfo.add(jLabel11);
        jLabel11.setBounds(220, 200, 62, 22);

        cboAlignment.setName("alignment"); // NOI18N
        pnlInfo.add(cboAlignment);
        cboAlignment.setBounds(290, 200, 130, 22);

        jLabel12.setText("Expression:");
        pnlInfo.add(jLabel12);
        jLabel12.setBounds(20, 230, 66, 18);

        txtExpression.setName("expression"); // NOI18N
        pnlInfo.add(txtExpression);
        txtExpression.setBounds(80, 230, 332, 20);

        cbotype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "text", "integer", "checkbox", "combobox", "date", "double", "decimal", "label", "lookup", "opener", "icon" }));
        cbotype.setName("typeHandler"); // NOI18N
        pnlInfo.add(cbotype);
        cbotype.setBounds(80, 270, 108, 22);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(pnlInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
        );

        pnlExtended.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Extended Information ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        pnlExtended.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout pnlbodyLayout = new org.jdesktop.layout.GroupLayout(pnlbody);
        pnlbody.setLayout(pnlbodyLayout);
        pnlbodyLayout.setHorizontalGroup(
            pnlbodyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlbodyLayout.createSequentialGroup()
                .add(pnlbodyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlExtended, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20))
        );
        pnlbodyLayout.setVerticalGroup(
            pnlbodyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlbodyLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlExtended, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(pnlbody, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        int index = tblcolumn.getSelectedRow();
        if (model.moveItemDown(index)) {
            tblcolumn.setRowSelectionInterval(index+1, index+1); 
            tblcolumn.grabFocus(); 

            if (propertyEditor != null) 
                propertyEditor.setValue(model.getColumns()); 
        }
    }//GEN-LAST:event_btnDownActionPerformed

    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        int index = tblcolumn.getSelectedRow();
        if (model.moveItemUp(index)) {
            tblcolumn.setRowSelectionInterval(index-1, index-1);
            tblcolumn.grabFocus();
            
            if (propertyEditor != null)
                propertyEditor.setValue(model.getColumns());
        }
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int selIndex = tblcolumn.getSelectedRow();
        if (model.removeItem(selIndex)) {
            Point selPoint = (Point) tblcolumn.getClientProperty("selectionPoint");
            if (selPoint == null) selPoint = new Point();
            
            int rowcount = model.getRowCount();
            if (rowcount == 0) {
                tblcolumn.refresh(null);
            } else if (selIndex >= 0 && selIndex < rowcount) {
                tblcolumn.changeSelection(selIndex, selPoint.x, false, false);
            } else if (selIndex >= rowcount && rowcount > 0) {
                tblcolumn.changeSelection(rowcount-1, selPoint.x, false, false);
            }
            
            if (propertyEditor != null)
                propertyEditor.setValue(model.getColumns());
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        controller.setColumn(model.addRow());
        
        boolean enable = (controller.getColumn() != null);
        if (enable) {
            int row = model.getRowCount()-1;
            if (row >= 0) tblcolumn.changeSelection(row, 0, false, false);
        }
        
        controller.setEnableComponents(enable);
        controller.refresh();
        controller.focusComponent("name");
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    controller.focusComponent("name");
                } catch(Exception e) {;}
            }
        });
    }//GEN-LAST:event_btnAddActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUp;
    private com.rameses.rcp.swingx.ComboField cboAlignment;
    private com.rameses.rcp.swingx.ComboField cboTextcase;
    private com.rameses.rcp.swingx.ComboField cbotype;
    private com.rameses.rcp.swingx.CheckField chkEditable;
    private com.rameses.rcp.swingx.CheckField chkNWE;
    private com.rameses.rcp.swingx.CheckField chkRequired;
    private com.rameses.rcp.swingx.CheckField chkResizable;
    private com.rameses.rcp.swingx.CheckField chkVisible;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlExtended;
    private javax.swing.JPanel pnlInfo;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlbody;
    private com.rameses.beaninfo.editor.table.ColumnDataTable tblcolumn;
    private javax.swing.JPanel toolbarpanel;
    private com.rameses.rcp.swingx.TextField txtEditableWhen;
    private com.rameses.rcp.swingx.TextField txtExpression;
    private com.rameses.rcp.swingx.TextField txtVisibleWhen;
    private com.rameses.rcp.swingx.TextField txtcaption;
    private com.rameses.rcp.swingx.TextField txtname;
    private com.rameses.rcp.swingx.IntegerField txtwidth;
    private com.rameses.rcp.swingx.IntegerField txtwidth3;
    private com.rameses.rcp.swingx.IntegerField txtwidth4;
    // End of variables declaration//GEN-END:variables
    
    // <editor-fold defaultstate="collapsed" desc=" TypeHandler ">          
    
    private void showError(Throwable t) {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Window window = kfm.getActiveWindow();
        String errmsg = t.getClass().getName() + ": " + t.getMessage();
        JOptionPane.showMessageDialog(window, errmsg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }    
    
    private class TypeHandler implements ItemListener 
    {
        public void itemStateChanged(ItemEvent e) 
        {
            if (e.getStateChange() != ItemEvent.SELECTED) return;
            
            pnlExtended.removeAll();
            
            JComponent page = controller.getExtendedPage(e.getItem()+""); 
            if (page != null) pnlExtended.add(page);
            
            pnlExtended.revalidate();
            pnlExtended.repaint();
            try 
            {
                if (page != null && controller.getColumn() != null) 
                {
                    if (page instanceof IExtendedPage) 
                    {
                        IExtendedPage xpage = (IExtendedPage) page;
                        xpage.setTypeHandler(controller.getColumn().getTypeHandler());         
                        controller.getColumn().setTypeHandler(xpage.getTypeHandler()); 
                        
                        if (propertyEditor != null) 
                            propertyEditor.setValue(model.getColumns()); 
                    }
                    controller.refresh(page, controller.getColumn().getTypeHandler());
                }
            } 
            catch(Exception ex) {
                ex.printStackTrace();
                showError(ex); 
            }
        } 
    }
    
    // </editor-fold>    
    
}
