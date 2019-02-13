/*
 * XFileBrowser.java
 *
 * Created on July 21, 2010, 2:01 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class XFileBrowser extends AbstractIconedTextField {
    
    private JFileChooser fchooser;
    
    private boolean listFiles;
    private boolean multiSelect;
    private boolean selectFilesOnly;
    
    private String dialogType;
    private String customFilter;
    private String fileNamePattern;    
    private String expression;
    private String varName; 
    
    public XFileBrowser() {
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents "> 
    
    private void initComponents() {
        dialogType = "open";
        fchooser = new JFileChooser();
        
        setListFiles( true ); 
        setMultiSelect( false ); 
        setSelectFilesOnly( true ); 
        setIcon("com/rameses/rcp/icons/folder_open.png");
        setOrientation( super.ICON_ON_RIGHT );
        setHint("Browse File");
        super.setEditable(false);
        setVarName("item"); 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public void setEditable(boolean b) {;}
    public void setValue(Object value) {;}
    public boolean isNullWhenEmpty() { return true; }
    public boolean isImmediate() { return true; }
    
    public Object getValue() {
        return (isMultiSelect() ? fchooser.getSelectedFiles() : fchooser.getSelectedFile());
    }
        
    public String getCustomFilter() { return customFilter; }
    public void setCustomFilter(String customFilter) {
        this.customFilter = customFilter;
    }
    
    public boolean isListFiles() { return listFiles; }
    public void setListFiles(boolean listFiles) {
        this.listFiles = listFiles;
    }
    
    public String getFileNamePattern() { return fileNamePattern; }
    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    } 
    
    public boolean isMultiSelect() { return multiSelect; }
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
    
    public boolean isSelectFilesOnly() { return selectFilesOnly; }
    public void setSelectFilesOnly(boolean selectFilesOnly) {
        this.selectFilesOnly = selectFilesOnly;
    }
    
    public String getDialogType() { return dialogType; }
    public void setDialogType(String dialogType) {
        this.dialogType = dialogType;
    }
    
    public String getExpression() { return expression; } 
    public void setExpression(String expression) {
        this.expression = expression;
    }
    
    public String getVarName() { return varName; } 
    public void setVarName( String varName ) {
        this.varName = varName; 
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }     
    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc=" refresh/load ">
    
    public void refresh() {
        try {
            if( !isReadonly() && !isFocusable() ) setFocusable(true);
            
            Object[] arr = null; 
            Object value = UIControlUtil.getBeanValue( this ); 
            if ( value == null ) {
                arr = new Object[0];
            } else if ( value.getClass().isArray() ) {
                arr = (Object[]) value; 
            } else {
                arr = new Object[]{ value }; 
            } 
                        
            if ( arr.length==0 ) {
                setText("");
                if ( multiSelect ) {
                    fchooser.setSelectedFiles(null);
                } else {
                    fchooser.setSelectedFile(null);
                }
            } else {
                String exprStr = getExpression(); 
                StringBuilder sb = new StringBuilder(); 
                ExprBeanSupport beanSupport = new ExprBeanSupport(getBinding().getBean());                
                boolean hasExpr = (exprStr != null && exprStr.length() > 0); 
                for ( int i=0; i < arr.length; ++i ) {
                    if ( sb.length() > 0 ) sb.append(", ");
                    if ( arr[i] == null ) { 
                        sb.append("null");
                    } else if ( hasExpr ) {
                        beanSupport.setItem(getVarName(), arr[i]);
                        sb.append(UIControlUtil.evaluateExpr(beanSupport.createProxy(), exprStr)+"");
                    } else {
                        sb.append(arr[i].toString()); 
                    }
                }                
                setText( sb.toString() ); 
            } 
        } catch(Throwable e) {
            //block the input if name is null
            setText("");
            setFocusable(false);
            
            if( ClientContext.getCurrentContext().isDebugMode() ) {
                e.printStackTrace();
            }
        }
    }
    
    public void load() {

    } 
    
    // </editor-fold>
    
    public void actionPerformed() {
        if ( !isFocusable() || !isEnabled() ) { return; }

        FileFilterImpl filter = new FileFilterImpl(); 
        filter.init(); 
        
        if ( isSelectFilesOnly() ) {
            fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else {
            fchooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }

        fchooser.setMultiSelectionEnabled( isMultiSelect() );
        fchooser.setFileFilter( filter );
        fchooser.setSelectedFile(null); 
        fchooser.setSelectedFiles(null);  

        int resp = 0;
        if ( "open".equals(dialogType) ) {
            resp = fchooser.showOpenDialog(null);
        } else {
            resp = fchooser.showSaveDialog(null);
        }
        
        if ( resp == JFileChooser.APPROVE_OPTION ) {
            
            UIInputUtil.updateBeanValue(this);
        }
        refresh();
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" FileFilterImpl ">
    
    private class FileFilterImpl extends FileFilter {
        
        private String _OldPattern;
        private String _NewPattern; 
        private boolean _IsListFiles;
        
        private void init() { 
            String str = getFileNamePattern(); 
            if ( str != null && str.trim().length() > 0 ) {
                Object value = null; 
                try { 
                    value = UIControlUtil.getBeanValue(getBinding(), str.trim());
                    str = value.toString(); 
                } catch( Throwable t ){;} 
            }
            
            if ( str == null ) str = "*"; 
            
            _OldPattern = str; 
            _NewPattern = str.replaceAll("[\\s]{1,}","")
                             .replaceAll("\\*",".*")
                             .replaceAll(",","|")
                             .toLowerCase();
            _IsListFiles = isListFiles(); 
        } 
        
        public boolean accept ( File f ) { 
            if ( f.isDirectory() ) return true;
            if ( !_IsListFiles ) return false;
            
            return f.getName().toLowerCase().matches( _NewPattern ); 
        } 
        
        public String getDescription() { 
            if ( _NewPattern.equals(".*") ) {
                return "All Files"; 
            } else { 
                return _OldPattern; 
            } 
        } 
    } 
    // </editor-fold>
    
}
