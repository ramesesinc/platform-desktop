/*
 * XReportPanel.java
 *
 * Created on November 25, 2009, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.reports.ui;

import com.rameses.osiris2.reports.ReportModel;
import com.rameses.osiris2.reports.ReportUtil;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.BreakException;
import com.rameses.util.ValueUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

/**
 *
 * @author elmo
 */
public class XReportPanel extends JPanel implements UIControl {
    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private int stretchWidth;
    private int stretchHeight;
    private String visibleWhen;
        
    private ReportModel model; 
    
    public XReportPanel() {
        super.setLayout(new BorderLayout());
        if (Beans.isDesignTime()) { 
            super.setPreferredSize(new Dimension(40, 40));
            super.setOpaque(true); 
            super.setBackground(Color.LIGHT_GRAY); 
        } 
    } 

    public void setLayout(LayoutManager mgr) {;}
        
    public void setStyle(Map props) {
    }
        
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public void load() {
    }
    
    public void refresh() { 
        render(); 
        
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(getBinding().getBean(), whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        } 
    } 
    
    public int getStretchWidth() { return stretchWidth; }
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; }
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight; 
    }    
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }    
    
    private void render() {
        if ( ValueUtil.isEmpty(getName()) ) 
            throw new IllegalStateException("Report Panel name must be provided");
        
        Object value = UIControlUtil.getBeanValue(this);
        model = (value instanceof ReportModel? (ReportModel)value: null);
        
        JasperPrint jasperPrint = null; 
        if (model != null) { 
            model.setProvider( getProviderImpl() );
            jasperPrint = model.getReport(); 
        } else if (value instanceof JasperPrint) { 
            jasperPrint = (JasperPrint) value; 
        } 
        
        if (jasperPrint == null) { 
            throw new IllegalStateException("No report found at " + getName());
        } 
        
        loadViewer( jasperPrint, value ); 
    }  
    
    private void loadViewer( JasperPrint jp, Object value ) {
        JRViewer jrv = new JRViewer( jp ); 
        new Customizer(jrv, value).customize(); 
        
        removeAll(); 
        add(jrv); 
        SwingUtilities.updateComponentTreeUI(this); 
    }
    
    private void doReload() {
        if ( model != null ) {
            model.reload(); 
        } 
    } 
        
    private void doBack() {
        try {
            Object outcome = (model == null? null: model.back()); 
            if (outcome == null) return;
            
            getBinding().fireNavigation(outcome); 
        } catch(Throwable t) {
            MsgBox.alert(t); 
        }
    }    
    
    private void doEdit() {
        try {
            Object outcome = (model == null? null: model.edit()); 
            if (outcome == null) return; 
            
            getBinding().fireNavigation(outcome); 
        } catch(Throwable t) {
            MsgBox.alert(t); 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl ">
    
    private class ProviderImpl implements ReportModel.Provider { 
        XReportPanel root = XReportPanel.this; 
        
        JFileChooser jfc = null; 
        
        public Object getBinding() { 
            return root.getBinding();  
        } 

        public File browseFolder() { 
            if ( jfc == null) { 
                File fdir = ReportUtil.getCustomFolder();
                jfc = new JFileChooser( fdir ); 
                jfc.setDialogTitle("Select Report Folder"); 
                jfc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ); 
            } 
            int result = jfc.showOpenDialog( root );  
            if ( result == JFileChooser.APPROVE_OPTION ) { 
                return jfc.getSelectedFile(); 
            } else { 
                return null; 
            } 
        }
        
        public void reload() { 
            if ( root.model == null ) { return; } 
        
            loadViewer( root.model.getReport(), root.model ); 
        } 
    }
    
    ProviderImpl provider;
    ProviderImpl getProviderImpl() {
        if (provider == null) {
            provider = new ProviderImpl();
        }
        return provider; 
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Customizer "> 
    
    private JButton btnBack; 
    private JButton getBackButton() {
        if (btnBack == null) {
            btnBack = new JButton(); 
            btnBack.setMargin(new Insets(2,2,2,2)); 
            btnBack.setMnemonic('b');
            btnBack.setToolTipText("Go back");
            btnBack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    doBack(); 
                }          
            });
            
            try { 
                URL url = XReportPanel.class.getResource("images/arrow_left.png");
                btnBack.setIcon(new ImageIcon(url)); 
            } catch(Throwable t){;} 
        }
        return btnBack; 
    }
    
    private JButton btnEdit;
    private JButton getEditButton() {
        if (btnEdit == null) {
            btnEdit = new JButton(); 
            btnEdit.setMargin(new Insets(2,2,2,2)); 
            btnEdit.setMnemonic('e');
            btnEdit.setToolTipText("Edit Report");
            btnEdit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    doEdit(); 
                }
            });
            
            try { 
                URL url = XReportPanel.class.getResource("images/edit.png");
                btnEdit.setIcon(new ImageIcon(url)); 
            } catch(Throwable t){;} 
        }
        return btnEdit; 
    }

    private JButton btnSync;
    private JButton getSyncButton() {
        if (btnSync == null) {
            btnSync = new JButton(); 
            btnSync.setMargin(new Insets(2,2,2,2)); 
            btnSync.setMnemonic('r');
            btnSync.setToolTipText("Reload Report");
            btnSync.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    doReload(); 
                }
            });
            
            try { 
                URL url = XReportPanel.class.getResource("images/sync.png");
                btnSync.setIcon(new ImageIcon(url)); 
            } catch(Throwable t){;} 
        }
        return btnSync; 
    }
    
    private class Customizer { 
        
        private JRViewer jviewer;
        private ReportModel model; 
        private boolean allowSave = false; 
        private boolean allowPrint = true;
        private boolean allowEdit = false; 
        private boolean allowBack = false; 

        Customizer(JRViewer jviewer, Object value) { 
            this.jviewer = jviewer;             
            if ( value instanceof ReportModel ) {
                this.model = (ReportModel)value;
                this.allowPrint = this.model.isAllowPrint(); 
                this.allowSave = this.model.isAllowSave();
                this.allowEdit = this.model.isAllowEdit(); 
                this.allowBack = this.model.isAllowBack(); 
            } 
        } 
        
        void customize() {
            if (jviewer == null) return;
            
            LayoutManager lm = jviewer.getLayout();
            if (lm instanceof BorderLayout) {
                Component comp = ((BorderLayout) lm).getLayoutComponent(BorderLayout.NORTH);                 
                if (comp instanceof Container) {
                    Container con = (Container) comp;
                    if (con.getLayout() instanceof CustomLayout) {
                        //already customized. exit right away
                        return;
                    } 
                    
                    Component sysbtnback = getBackButton();
                    sysbtnback.setVisible(allowBack);
                    sysbtnback.setName("sysbtnback"); 
                    
                    Component sysbtnedit = getEditButton(); 
                    sysbtnedit.setVisible(allowEdit); 
                    sysbtnedit.setName("sysbtnedit"); 

                    Component sysbtnsync = getSyncButton(); 
                    sysbtnsync.setVisible(allowEdit); 
                    sysbtnsync.setName("sysbtnsync"); 
                    
                    Component spacer = Box.createHorizontalStrut(10); 
                    spacer.setVisible( allowEdit );
                    spacer.setName( "sysspacer" ); 
                    
                    CustomLayout clayout = new CustomLayout(); 
                    clayout.allowSave = allowSave;
                    clayout.allowPrint = allowPrint; 
                    clayout.printHandler = new PrintActionHandler( model ); 
                    clayout.setSystemComponents( new Component[]{sysbtnback, sysbtnedit, sysbtnsync, spacer} );
                    clayout.setComponents( con.getComponents() );
                    con.add( sysbtnback ); 
                    con.add( sysbtnedit ); 
                    con.add( sysbtnsync ); 
                    con.add( spacer ); 
                    con.setLayout(clayout); 
                    if (con instanceof JComponent) {
                        ((JComponent)con).setBorder(BorderFactory.createEmptyBorder(2,2,2,0)); 
                    }
                }
            }
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" CustomLayout "> 
    
    private class CustomLayout implements LayoutManager 
    {
        private Component[] systemComponents; 
        private Component[] components; 
        
        private PrintActionHandler printHandler;
        
        private boolean allowSave = false;
        private boolean allowPrint = true;
        
        void setComponents(Component[] components) {
            this.components = components;             
            if (components != null && components.length > 0) {
                Component c0 = components[0];
                c0.setVisible(allowSave);
                c0.setEnabled(allowSave); 
                setMnemonic(c0, 's');
                
                if (components.length > 1) {
                    Component c1 = components[1]; 
                    c1.setVisible(allowPrint);
                    c1.setEnabled(allowPrint); 
                    setMnemonic(c1, 'p');
                    
                    if ( c1 instanceof AbstractButton && printHandler != null ) { 
                        AbstractButton btn = (AbstractButton) c1; 
                        printHandler.install( btn );  
                    } 
                }
            }
        }
        
        void setSystemComponents(Component[] systemComponents) {
            this.systemComponents = systemComponents; 
        }
        
        void setMnemonic(Component c, char key) {
            if (c == null || key == '\u0000') return;
            if (!(c instanceof JButton)) return;
            
            ((JButton)c).setMnemonic(key); 
        }
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) { 
                int w=0, h=0;
                if (systemComponents != null) {
                    for (int i=0; i<systemComponents.length; i++) {
                        Component c = systemComponents[i];
                        if (c == null || !c.isVisible()) { continue; } 
                        
                        Dimension dim = c.getPreferredSize(); 
                        w += dim.width;
                        h = Math.max(h, dim.height); 
                    }
                }
                
                if (components != null) {
                    for (int i=0; i<components.length; i++) {
                        Component c = components[i];
                        if (!c.isVisible()) continue;
                        
                        Dimension dim = c.getPreferredSize(); 
                        w += dim.width;
                        h = Math.max(h, dim.height); 
                    }
                }
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom);
                return new Dimension(w,h);
            }
        }
        
        public void layoutContainer(Container parent) { 
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth(), ph = parent.getHeight();
                int x = margin.left, y = margin.top; 
                int w = pw - (margin.left + margin.right); 
                int h = ph - (margin.top + margin.bottom);
                
                if (systemComponents != null) {
                    for (int i=0; i<systemComponents.length; i++) {
                        Component c = systemComponents[i];
                        if (c == null || !c.isVisible()) { continue; } 
                        
                        Dimension dim = c.getPreferredSize(); 
                        c.setBounds(x, y, dim.width, h); 
                        x += dim.width; 
                    }
                }
                
                if (components != null) {
                    for (int i=0; i<components.length; i++) {
                        Component c = components[i];
                        if (c == null || !c.isVisible()) continue;
                        
                        Dimension dim = c.getPreferredSize(); 
                        c.setBounds(x, y, dim.width, h); 
                        x += dim.width; 
                    }
                }                
            }
        } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" PrintActionHandler "> 
    
    private class PrintActionHandler implements ActionListener {

        private ReportModel model;
        private AbstractButton button; 
        private List<ActionListener> actions = new ArrayList();
        
        PrintActionHandler( ReportModel model ) {
            this.model = model; 
        }
        
        void install( AbstractButton button ) {
            this.button = button; 
            this.actions = new ArrayList(); 
            
            if ( button != null ) {
                ActionListener[] values = button.getActionListeners(); 
                for ( ActionListener al : values ) { 
                    if ( al == null ) { continue; }                     
                    if ( al instanceof PrintActionHandler ) {
                        button.removeActionListener( al ); 
                    } else {
                        button.removeActionListener( al ); 
                        actions.add( al ); 
                    }
                }
                
                button.addActionListener( this ); 
            } 
        }
        
        boolean beforeExecute() { 
            return ( model == null? true: model.beforePrint()); 
        }
        void afterExecute() { 
            if ( model != null ) {
                model.afterPrint(); 
            } 
        }
        
        public final void actionPerformed(ActionEvent e) { 
            try {
                if ( !beforeExecute() ) { return; }
            } catch( BreakException be ) {
                return; 
            }
            
            for ( ActionListener al : this.actions ) {
                al.actionPerformed( e ); 
            } 
            
            try { 
                afterExecute();  
            } catch( BreakException be ) {
                //do nothing 
            }
        } 
    }
    
    // </editor-fold>    
    
}
