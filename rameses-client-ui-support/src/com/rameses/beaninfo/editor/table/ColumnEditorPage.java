/*
 * ColumnEditorPage.java
 *
 * Created on May 20, 2013, 9:30 AM
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.beaninfo.editor.table.ColumnEditorController.DependHandler;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.swingx.CheckField;
import com.rameses.rcp.swingx.ComboField;
import com.rameses.rcp.swingx.ComboItem;
import com.rameses.rcp.swingx.IntegerField;
import com.rameses.rcp.swingx.TextField;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Comparator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

/**
 *
 * @author  wflores
 */
public class ColumnEditorPage extends javax.swing.JPanel 
{
    private PropertyEditor propertyEditor;
    private ColumnEditorController controller;
    private ColumnEditorModel model; 
    private JLabel lblAlignment;
    private ComboField cboAlignment;
    private JLabel lblTextcase;
    private ComboField cboTextcase;
    
    public ColumnEditorPage() 
    {
        initComponents();
        
        model = new ColumnEditorModel();        
        tblcolumn.setModel(model); 
        tblcolumn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        
        controller = new ColumnEditorController(){
            protected void onvalueChanged(String name) 
            {
                int row = tblcolumn.getSelectedRow();
                if (row >= 0) model.fireTableRowsUpdated(row, row); 
                
                if (propertyEditor != null)
                    propertyEditor.setValue(model.getColumns()); 
            }
        }; 
        
        lblAlignment = new JLabel("Alignment");        
        lblAlignment.setBounds(188, 195, 58, 18);
        jPanel6.add(lblAlignment); 
        
        cboAlignment = new ComboField();
        cboAlignment.setName("alignment");
        cboAlignment.setBounds(244, 195, 176, cboAlignment.getPreferredSize().height); 
        jPanel6.add(cboAlignment); 
        
        jLabel9.setVisible(false); 
        txtEditableWhen.setBounds(138, 132, 284, 19);
        chkEditable.setBounds(70, 132, 82, 19);
        
        lblTextcase = new JLabel("Text Case:");
        lblTextcase.setBounds(70, 158, 68, 20);
        jPanel6.add(lblTextcase); 
        
        cboTextcase = new ComboField();
        cboTextcase.setName("textCase");
        cboTextcase.setBounds(138, 158, 100, cboTextcase.getPreferredSize().height); 
        cboTextcase.setItems(new ComboItem[]{
           new ComboItem("(Default)", null), 
           new ComboItem("UPPER", TextCase.UPPER), 
           new ComboItem("LOWER", TextCase.LOWER), 
           new ComboItem("NONE", TextCase.NONE)  
        });
        cboAlignment.setUpdateable(true);        
        jPanel6.add(cboTextcase); 
        
        controller.registerComponents(this);
        controller.addExtendedPage("text", new TextExtendedPage()); 
        controller.addExtendedPage("integer", new IntegerExtendedPage()); 
        controller.addExtendedPage("checkbox", new CheckExtendedPage()); 
        controller.addExtendedPage("combobox", new ComboExtendedPage()); 
        controller.addExtendedPage("date", new DateExtendedPage()); 
        controller.addExtendedPage("double", new DecimalExtendedPage()); 
        controller.addExtendedPage("decimal", new DecimalExtendedPage()); 
        controller.addExtendedPage("lookup", new LookupExtendedPage()); 
        controller.addExtendedPage("opener", new OpenerExtendedPage()); 
        
        controller.setEnableComponents(false); 
        cbotype.setItems(new ComboItem[]{
           new ComboItem("text"),
           new ComboItem("integer"),
           new ComboItem("checkbox"),
           new ComboItem("combobox"),
           new ComboItem("date"),
           new ComboItem("double"),
           new ComboItem("decimal"),
           new ComboItem("lookup"),
           new ComboItem("opener")
        });
        cbotype.setUpdateable(false); 
        cbotype.addItemListener(new TypeHandler());

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
        
        chkEditable.setSelected(false); 
        chkEditable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                txtEditableWhen.setEnabled(e.getStateChange()==ItemEvent.SELECTED); 
            }
        });
        txtEditableWhen.putClientProperty(DependHandler.class, new DependHandler("editable", txtEditableWhen)); 
        
        btnDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                int index = tblcolumn.getSelectedRow();
                if (model.moveItemDown(index)) 
                {
                    tblcolumn.setRowSelectionInterval(index+1, index+1); 
                    tblcolumn.grabFocus(); 
                    
                    if (propertyEditor != null) 
                        propertyEditor.setValue(model.getColumns()); 
                }
            }
        });
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
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlbody = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtname = new TextField();
        jLabel2 = new javax.swing.JLabel();
        txtcaption = new TextField();
        jLabel6 = new javax.swing.JLabel();
        txtwidth = new IntegerField();
        jLabel7 = new javax.swing.JLabel();
        txtwidth3 = new IntegerField();
        jLabel8 = new javax.swing.JLabel();
        txtwidth4 = new IntegerField();
        jLabel3 = new javax.swing.JLabel();
        cbotype = new ComboField();
        chkNullWhenEmpty = new CheckField();
        chkResizable = new CheckField();
        chkNWE = new CheckField();
        jLabel9 = new javax.swing.JLabel();
        txtEditableWhen = new TextField();
        chkEditable = new CheckField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pnlExtended = new javax.swing.JPanel();
        pnlLeft = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblcolumn = new TableImpl();
        toolbarpanel = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        setPreferredSize(new java.awt.Dimension(723, 510));
        pnlbody.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 1, 1, 1));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Column Information ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11)));
        jPanel6.setLayout(null);

        jLabel1.setText("Name:");
        jPanel6.add(jLabel1);
        jLabel1.setBounds(18, 14, 52, 18);

        txtname.setName("name");
        jPanel6.add(txtname);
        txtname.setBounds(70, 14, 352, 19);

        jLabel2.setText("Caption:");
        jPanel6.add(jLabel2);
        jLabel2.setBounds(18, 40, 52, 18);

        txtcaption.setName("caption");
        jPanel6.add(txtcaption);
        txtcaption.setBounds(70, 40, 352, 19);

        jLabel6.setText("Width:");
        jPanel6.add(jLabel6);
        jLabel6.setBounds(18, 68, 52, 18);

        txtwidth.setName("width");
        jPanel6.add(txtwidth);
        txtwidth.setBounds(70, 68, 62, 19);

        jLabel7.setText("Min Width:");
        jPanel6.add(jLabel7);
        jLabel7.setBounds(154, 68, 62, 18);

        txtwidth3.setName("minWidth");
        jPanel6.add(txtwidth3);
        txtwidth3.setBounds(216, 68, 62, 19);

        jLabel8.setText("Max Width:");
        jPanel6.add(jLabel8);
        jLabel8.setBounds(298, 68, 62, 18);

        txtwidth4.setName("maxWidth");
        jPanel6.add(txtwidth4);
        txtwidth4.setBounds(360, 68, 62, 19);

        jLabel3.setText("Type:");
        jPanel6.add(jLabel3);
        jLabel3.setBounds(18, 196, 50, 18);

        cbotype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "string", "integer", "boolean", "checkbox", "combo", "date", "double", "decimal", "lookup" }));
        cbotype.setName("typeHandler");
        jPanel6.add(cbotype);
        cbotype.setBounds(70, 195, 84, 22);

        chkNullWhenEmpty.setText("Required");
        chkNullWhenEmpty.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkNullWhenEmpty.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkNullWhenEmpty.setName("required");
        jPanel6.add(chkNullWhenEmpty);
        chkNullWhenEmpty.setBounds(70, 108, 82, 18);

        chkResizable.setText("Resizable");
        chkResizable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkResizable.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkResizable.setName("resizable");
        jPanel6.add(chkResizable);
        chkResizable.setBounds(154, 108, 82, 18);

        chkNWE.setText("Null When Empty");
        chkNWE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkNWE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkNWE.setName("nullWhenEmpty");
        jPanel6.add(chkNWE);
        chkNWE.setBounds(236, 108, 114, 18);

        jLabel9.setText("Expression:");
        jPanel6.add(jLabel9);
        jLabel9.setBounds(87, 152, 68, 14);

        txtEditableWhen.setName("editableWhen");
        jPanel6.add(txtEditableWhen);
        txtEditableWhen.setBounds(155, 150, 266, 19);

        chkEditable.setText("Editable");
        chkEditable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkEditable.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkEditable.setName("editable");
        jPanel6.add(chkEditable);
        chkEditable.setBounds(70, 132, 82, 15);

        jLabel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.add(jLabel4);
        jLabel4.setBounds(10, 96, 422, 2);

        jLabel5.setText("jLabel5");
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel6.add(jLabel5);
        jLabel5.setBounds(10, 184, 422, 2);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
        );

        pnlExtended.setLayout(new java.awt.BorderLayout());

        pnlExtended.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Extended Information ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11)));

        org.jdesktop.layout.GroupLayout pnlbodyLayout = new org.jdesktop.layout.GroupLayout(pnlbody);
        pnlbody.setLayout(pnlbodyLayout);
        pnlbodyLayout.setHorizontalGroup(
            pnlbodyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlbodyLayout.createSequentialGroup()
                .add(pnlbodyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlExtended, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlbodyLayout.setVerticalGroup(
            pnlbodyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlbodyLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlExtended, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addContainerGap())
        );
        add(pnlbody, java.awt.BorderLayout.CENTER);

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
        jScrollPane1.setViewportView(tblcolumn);

        btnAdd.setFont(new java.awt.Font("Monospaced", 1, 12));
        btnAdd.setText("+");
        btnAdd.setMargin(new java.awt.Insets(1, 7, 1, 7));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setFont(new java.awt.Font("Monospaced", 1, 12));
        btnRemove.setText("-");
        btnRemove.setMargin(new java.awt.Insets(1, 7, 1, 7));
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnUp.setFont(new java.awt.Font("Monospaced", 0, 12));
        btnUp.setText("Up");
        btnUp.setMargin(new java.awt.Insets(1, 5, 1, 5));
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });

        btnDown.setFont(new java.awt.Font("Monospaced", 0, 12));
        btnDown.setText("Down");
        btnDown.setMargin(new java.awt.Insets(1, 5, 1, 5));

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlLeftLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, toolbarpanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlLeftLayout.setVerticalGroup(
            pnlLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlLeftLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toolbarpanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        add(pnlLeft, java.awt.BorderLayout.WEST);

    }// </editor-fold>//GEN-END:initComponents
	
    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed

        int index = tblcolumn.getSelectedRow();
        if (model.moveItemUp(index)) 
        { 
            tblcolumn.setRowSelectionInterval(index-1, index-1);
            tblcolumn.grabFocus(); 
            
            if (propertyEditor != null) 
                propertyEditor.setValue(model.getColumns());       
        }
                
    }//GEN-LAST:event_btnUpActionPerformed
        
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed

        int selIndex = tblcolumn.getSelectedRow();
        if (model.removeItem(selIndex))
        {
            Point selPoint = (Point) tblcolumn.getClientProperty("selectionPoint"); 
            if (selPoint == null) selPoint = new Point();
            
            int rowcount = model.getRowCount();
            if (rowcount == 0) {
                tblcolumn.refresh(null); 
            }
            else if (selIndex >= 0 && selIndex < rowcount) {
                tblcolumn.changeSelection(selIndex, selPoint.x, false, false);
            }
            else if (selIndex >= rowcount && rowcount > 0) {
                tblcolumn.changeSelection(rowcount-1, selPoint.x, false, false);
            }
            
            if (propertyEditor != null) 
                propertyEditor.setValue(model.getColumns()); 
        }
        
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed

        controller.setColumn(model.addRow());
        
        boolean enable = (controller.getColumn() != null); 
        if (enable) 
        {
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
    
    // <editor-fold defaultstate="collapsed" desc=" Variables ">      
    // Variables declaration - do not modify                                          
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUp;
    private ComboField cbotype;
    private CheckField chkEditable;
    private CheckField chkNWE;
    private CheckField chkNullWhenEmpty;
    private CheckField chkResizable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlExtended;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlbody;
    private TableImpl tblcolumn;
    private javax.swing.JPanel toolbarpanel;
    private TextField txtEditableWhen;
    private TextField txtcaption;
    private TextField txtname;
    private IntegerField txtwidth;
    private IntegerField txtwidth3;
    private IntegerField txtwidth4;
    // End of variables declaration                                          
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TableImpl ">          
    
    private void showError(Throwable t) {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Window window = kfm.getActiveWindow();
        String errmsg = t.getClass().getName() + ": " + t.getMessage();
        JOptionPane.showMessageDialog(window, errmsg, "ERROR", JOptionPane.ERROR_MESSAGE);
    }    
    
    private class TableImpl extends JTable 
    {
        public void selectRow(int rowIndex) 
        {
            if (rowIndex >= 0 && rowIndex < model.getRowCount()) 
            {
                Point selPoint = (Point) getClientProperty("selectionPoint");
                if (selPoint == null) selPoint = new Point(); 
                
                changeSelection(rowIndex, selPoint.x, false, false); 
            }
        }
        
        public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) 
        {
            int oldRowIndex = getSelectedRow();
            super.changeSelection(rowIndex, columnIndex, toggle, extend); 
            putClientProperty("selectionPoint", new Point(columnIndex, rowIndex)); 
            
            if (oldRowIndex != rowIndex) onrowChanged();
        }
        
        private void onrowChanged() 
        {
            Column col = model.getItem(getSelectedRow()); 
            refresh(col);
        }
        
        public void refresh(Column col) 
        {
            try 
            {
                controller.setColumn(col);                
                controller.setEnableComponents((col==null? false: true)); 
                controller.refresh();                
            } 
            catch(Exception ex) {
                ex.printStackTrace();
                showError(ex); 
            }            
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TypeHandler ">          
    
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
