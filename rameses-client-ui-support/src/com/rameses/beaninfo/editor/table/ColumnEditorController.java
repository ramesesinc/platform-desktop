/*
 * ColumnEditorController.java
 *
 * Created on May 20, 2013, 10:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.beaninfo.editor.table;

import com.rameses.rcp.common.Column;
import com.rameses.rcp.swingx.IComponent;
import com.rameses.rcp.swingx.IComponentView;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *
 * @author wflores
 */
public class ColumnEditorController 
{
    private Map<String,JComponent> extended = new HashMap<String,JComponent>();    
    private List<JComponent> components = new ArrayList<JComponent>();     
    private Column column;
        
    public ColumnEditorController() {
    }
    
    void registerComponents(JComponent comp) {
        registerFromPage(comp);
    } 
    
    JComponent getExtendedPage(String type) {
        return extended.get(type); 
    }
    
    void addExtendedPage(String type, JComponent page) 
    {
        if (type != null && page != null) 
        {   
            extended.put(type, page);
            registerFromPage(page);
        } 
    }
    
    private void registerFromPage(JComponent page) 
    {
        boolean extended = (page instanceof IExtendedPage); 
        Component[] comps = page.getComponents(); 
        for (Component c : comps) 
        {
            if (!(c instanceof JComponent)) continue;
            if (c instanceof JTable) continue; 

            JComponent jc = (JComponent)c;
            if (extended) jc.putClientProperty("PAGE", page);
            
            if (jc instanceof JTextComponent) register(jc); 
            else if (jc instanceof JComboBox) register(jc); 
            else if (jc instanceof JCheckBox) register(jc);  
            else if (jc.getComponentCount()>0) registerFromPage(jc); 
        }
    }
    
    private void register(JComponent comp) 
    {
        if (comp.getName()==null || comp.getName().length()==0) return;

        if (!components.contains(comp)) 
        {
            components.add(comp);
            if (comp instanceof JTextComponent) 
                comp.setInputVerifier(new InputVerifierImpl()); 
            else if (comp instanceof JCheckBox) 
                ((JCheckBox)comp).addItemListener(new CheckItemHandler()); 
            else if (comp instanceof JComboBox) 
                ((JComboBox)comp).addItemListener(new ComboItemHandler());
        }         
    }
    
    public Column getColumn() { return column; } 
    public void setColumn(Column column) { this.column = column; }
        
    public void setEnableComponents(boolean enable) {
        for (JComponent jc : components) {
            jc.setEnabled(enable); 
        }        
    } 
    
    public void refresh() 
    {
        for (JComponent jc : components) 
        {
            String name = jc.getName(); 
            Object value = getValue(column, name); 
            if (jc instanceof IComponent) 
            {
                IComponent icomp = (IComponent) jc;
                if (icomp.getClientProperty("PAGE") == null) 
                {
                    icomp.setValue(value);
                    notifyDepends(jc, name, value); 
                } 
            }
        } 
    }
    
    void refresh(JComponent page, Object bean) 
    {
        if (page == null) return;
        
        for (JComponent jc : components) 
        {
            JComponent jpage = (JComponent) jc.getClientProperty("PAGE");
            if (jpage != page) continue; 
            
            String name = jc.getName(); 
            Object value = getValue(bean, name);
            if (jc instanceof IComponent)
            {
                ((IComponent) jc).setValue(value);                         
                notifyDepends(jc, name, value); 
            }
        } 
    }  
    
    void notifyDepends(JComponent source, String name, Object value) 
    {
        if (name == null) return;
        
        for (JComponent jc : components) 
        {
            DependHandler handler = (DependHandler) jc.getClientProperty(DependHandler.class); 
            if (handler == null || handler.getName() == null) continue;
            
            if (handler.getName().equals(name)) 
            {
                handler.setSource(source);
                handler.valueChanged(name, value);
            } 
        }
    }
    
    void focusComponent(String name) 
    {
        for (JComponent jc : components) 
        {
            if (jc.getName() != null && jc.getName().equals(name)) 
            {
                jc.requestFocus();
                jc.grabFocus(); 
                break;
            }
        }
    }
    
    private Object getValue(Object source, String name) 
    {
        if (source == null) return null;
        
        Method m = findGetterMethod(source, name); 
        if (m == null) return null;
        
        try {            
            return m.invoke(source, new Object[]{}); 
        } catch (Exception ex) {
            System.out.println("[ERROR] getValue: " + ex.getMessage());
            return null; 
        } 
    }
    
    private void setValue(Object source, String name, Object value) 
    {
        if (source == null || name == null) return;
                
        Method gm = findGetterMethod(source, name);
        Class[] paramTypes = (gm == null? new Class[]{Object.class}: new Class[]{gm.getReturnType()});
        
        Method  m = findSetterMethod(source, name, paramTypes); 
        if (m == null) return;
        
        try 
        {
            Class objClass = source.getClass();       
            Method getter = toGetterMethod(objClass, name);
            Class valueType = (getter == null? Object.class: getter.getReturnType()); 
            if (valueType == Object.class) {
                //do nothing 
            }
            else if (valueType == boolean.class || valueType.isAssignableFrom(Boolean.class)) 
                value = toBoolean(value); 
            else if (valueType == int.class || valueType.isAssignableFrom(Integer.class)) 
                value = toInt(value); 
            else if (valueType == double.class || valueType.isAssignableFrom(Double.class)) 
                value = toDouble(value); 

            //m.invoke(source, new Object[]{value}); 
            Method setter = toSetterMethod(objClass, name);
            if (setter == null) return;
            
            setter.invoke(source, new Object[]{value});
            onvalueChanged(name);  
        } 
        catch(Throwable ex) {
            System.out.println("[ERROR] setValue: ("+name+") " + ex.getMessage());
            //ex.printStackTrace();
        }
    }
    
    private Method toGetterMethod(Class objClass, String name) {
        if (name == null || name.length() == 0) return null;
        
        try { 
            String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1); 
            return objClass.getMethod(methodName, new Class[0]); 
        } catch(Throwable t) {;}
        
        try { 
            String methodName = "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1); 
            return objClass.getMethod(methodName, new Class[0]); 
        } catch(Throwable t) {
            return null; 
        }        
    } 
    
    private Method toSetterMethod(Class objClass, String name) {
        if (name == null || name.length() == 0) return null;
        
        try { 
            Method getter = toGetterMethod(objClass, name); 
            Class returnType = (getter == null? Object.class: getter.getReturnType()); 
            String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1); 
            return objClass.getMethod(methodName, new Class[]{ returnType }); 
        } catch(Throwable t) {
            return null; 
        }
    }    
    
    protected void onvalueChanged(String name) {}
    
    private boolean toBoolean(Object value) 
    {
        try {
            if (value instanceof Boolean)
                return ((Boolean) value).booleanValue();
            else 
                return Boolean.parseBoolean(value.toString()); 
        } 
        catch(Exception ex) {
            return false; 
        }
    }
    
    private boolean isBoolean(Object value) {
        return ("true".equals(value+"") || "false".equals(value+"")); 
    }
    
    private int toInt(Object value) 
    {
        try {
            if (value instanceof Number)
                return ((Number) value).intValue();
            
            return Integer.parseInt(value.toString()); 
        } 
        catch(Exception ex) {
            return 0; 
        }
    }    
    
    private double toDouble(Object value) 
    {
        try {
            if (value instanceof Number)
                return ((Number) value).doubleValue();
            
            return Double.parseDouble(value.toString()); 
        } 
        catch(Exception ex) {
            return 0.0; 
        }
    } 
    
    private boolean isInteger(Object value) {
        try 
        {
            Integer.parseInt(value.toString()); 
            return true; 
        } 
        catch(Exception ex) {
            return false; 
        }
    }
    
    private Method findGetterMethod(Object source, String name) 
    {
        if (name == null || name.length() == 0) return null; 

        String methodName = "get" + name.substring(0,1).toUpperCase() + name.substring(1); 
        Method[] methods = source.getClass().getMethods(); 
        for (Method m : methods) 
        { 
            if (m.getName().equals(methodName) && (m.getParameterTypes() == null || m.getParameterTypes().length==0)) 
                return m;
        } 
        
        methodName = "is" + name.substring(0,1).toUpperCase() + name.substring(1); 
        for (Method m : methods) 
        { 
            if (m.getName().equals(methodName) && (m.getParameterTypes() == null || m.getParameterTypes().length==0)) 
                return m;
        }         
        return null;
    } 

    private Method findSetterMethod(Object source, String name, Class[] paramTypes) 
    {
        if (name == null || name.length() == 0) return null; 
        if (paramTypes == null) paramTypes = new Class[]{};

        String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1); 
        Method[] methods = source.getClass().getMethods(); 
        for (Method m : methods) 
        { 
            if (m.getName().equals(methodName)) 
            {
                Class[] sourceTypes = m.getParameterTypes();
                if (sourceTypes == null) sourceTypes = new Class[]{};                
                if (sourceTypes.length != paramTypes.length) continue; 

                for (int i=0; i<sourceTypes.length; i++) {
                    if (sourceTypes[i] != paramTypes[i]) continue; 
                }                
                return m; 
            }
        } 
        return null;
    }      
    
    
    private class TextFieldSupport extends InputVerifier
    {
        private JTextComponent editor;
        private Class valueType;
        
        TextFieldSupport(JTextComponent editor, Class valueType) {
            this.editor = editor;
            this.valueType = valueType;
        }

        public boolean verify(JComponent input) 
        {
            Object value = null;
            if (input instanceof JTextComponent) 
                value = ((JTextComponent) input).getText();
            else if (input instanceof JComboBox) 
                value = ((JComboBox) input).getSelectedItem();
            
            if (valueType.isAssignableFrom(Integer.class)) 
            {
                Number num = null;
                try { 
                    num = Integer.parseInt(value.toString()); 
                } catch(Exception ex) {;}
                
                if (valueType.isPrimitive() && num == null) num = 0;
                
                value = num;
            }
            else if (valueType.isAssignableFrom(Double.class)) 
            {
                Number num = null;
                try { 
                    num = Double.parseDouble(value.toString()); 
                } catch(Exception ex) {;}
                
                if (valueType.isPrimitive() && num == null) num = 0.0;
                
                value = num;
            }
            
            JComponent jpage = (JComponent) input.getClientProperty("PAGE");
            Object source = (jpage == null? column: column.getTypeHandler());             
            setValue(source, input.getName(), value); 
            return true;            
        }
    } 
    
    private class InputVerifierImpl extends InputVerifier
    {
        public boolean verify(JComponent input) 
        {
            if (input instanceof IComponentView) return true;
            
            JComponent jpage = (JComponent) input.getClientProperty("PAGE");
            Object source = (jpage == null? column: column.getTypeHandler());     
            if (input instanceof IComponent) 
            {
                IComponent icomp = (IComponent) input;
                if (icomp.isUpdateable())
                    setValue(source, icomp.getName(), icomp.getValue()); 
            }
            return true;
        } 
    }
    
    private class CheckItemHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e) 
        {
            JComponent jcomp = (JComponent) e.getSource(); 
            if (jcomp instanceof IComponentView) return; 
            
            if (jcomp instanceof IComponent)
            {
                JComponent jpage = (JComponent) jcomp.getClientProperty("PAGE");
                Object source = (jpage == null? column: column.getTypeHandler()); 
                
                IComponent icomp = (IComponent) jcomp; 
                if (icomp.isUpdateable()) 
                    setValue(source, icomp.getName(), icomp.getValue()); 
            }
        }
    }    
    
    private class ComboItemHandler implements ItemListener
    {
        public void itemStateChanged(ItemEvent e) 
        {
            JComponent jcomp = (JComponent)e.getSource(); 
            if (jcomp instanceof IComponentView) return; 
            
            if (jcomp instanceof IComponent) 
            {
                JComponent jpage = (JComponent) jcomp.getClientProperty("PAGE");
                Object source = (jpage == null? column: column.getTypeHandler()); 
                
                IComponent icomp = (IComponent) jcomp;                
                if (icomp.isUpdateable()) 
                    setValue(source, icomp.getName(), icomp.getValue()); 
            }
        }
    } 
    
    public static class DependHandler 
    {
        private String name;
        private JComponent editor;
        private JComponent source; 
        
        public DependHandler(String name, JComponent editor) 
        {
            this.name = name;
            this.editor = editor;
        }
        
        public void setSource(JComponent source) { this.source = source; }        
        public String getName() { return name; } 
        
        public void valueChanged(String name, Object value) 
        {
            if (source instanceof JCheckBox) 
            {
                boolean b = "true".equals(value+"");
                editor.setEnabled(b); 
            }
        }
    }     
    
}
