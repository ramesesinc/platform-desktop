/*
 * XComboBox.java
 *
 * Created on June 26, 2010, 1:37 PM
 * @author jaycverg
 */
package com.rameses.rcp.control;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.ComboBoxEditorSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.util.ValueUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;

public class XComboBox extends JComboBox
    implements UIInput, Validatable, ActiveControl, MouseEventSupport.ComponentInfo {

    protected Binding binding;
    private int index;
    private String[] depends;
    private String fontStyle;
    private String caption;
    private String varName;
    private String items;
    private String itemKey;
    private String expression;
    private String disableWhen;
    private String visibleWhen;
    private String emptyText = "-";
    private Object itemsObject;
    private boolean immediate;
    private boolean dynamic;
    private boolean allowNull = true;
    private boolean readonly;
    private ControlProperty property = new ControlProperty();
    private ActionMessage actionMessage = new ActionMessage();
    private Font sourceFont;
    protected ComboBoxModelImpl model;
    private Class fieldType;
    private int stretchWidth;
    private int stretchHeight;
    
    private boolean autoDefaultValue = true;
    
    public XComboBox() {
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="  initComponents method  ">
    
    private void initComponents() {
        model = new ComboBoxModelImpl();
        if (Beans.isDesignTime()) {
            model.addItem("Item 1", "Item 1");
            super.setModel(model);
        }

        setVarName("item");
        new MouseEventSupport(this).install();

        UIManager.put("ComboBox.disabledForeground", getForeground());
        ComboBoxEditorSupport.install(this);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public boolean isAutoDefaultValue() { return autoDefaultValue; } 
    public void setAutoDefaultValue( boolean autoDefaultValue ) {
        this.autoDefaultValue = autoDefaultValue; 
    }
    
    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);

        if (Beans.isDesignTime()) {
            model.removeAllElements();
            model.addElement(name);
        }
    }

    public Object getValue() {
        if (Beans.isDesignTime()) {
            return null;
        }

        Object item = getSelectedItem(); 
        if ( item == null ) { return null; } 

        Object value = ((ComboItem) item).getValue();
        if ( value == null ) { return null; } 
        
        String fkey = getItemKey(); 
        if (!ValueUtil.isEmpty(fkey)) {
            PropertyResolver res = PropertyResolver.getInstance();
            value = res.getProperty(value, fkey);
        }
        return value;
    }

    public void setValue(Object value) {
        if (Beans.isDesignTime()) { return; }

        if (value instanceof KeyEvent) {
            processKeyEventValue((KeyEvent) value);
            return;
        }

        ComboItem selObj = (ComboItem) getSelectedItem();
        if (isSelected(selObj, value)) { return; }

        if (value == null && !isAllowNull()) {
            model.nopublish = true; 
            try { 
                setSelectedIndex(0); 
            } catch(Throwable t) {
                //do nothing 
            } finally { 
                model.nopublish = false; 
            }
            updateBeanValue();

        } else {
            Object selitem = null;
            for (int i = 0; i < getItemCount(); i++) {
                ComboItem ci = (ComboItem) getItemAt(i);
                if (!isSelected(ci, value)) { continue; }

                selitem = ci; 
                break;
            }

            model.nopublish = true; 
            try {
                if ( selitem != null ) {
                    model.setSelectedItem( selitem ); 
                } else if (getItemCount() > 0) {
                    setSelectedIndex(0); 
                } else { 
                    setSelectedItem( null ); 
                } 
            } catch(Throwable t) {
                //do nothing 
            } finally {
                model.nopublish = false; 
            }

            updateBeanValue();
        }
    }

    private void processKeyEventValue(KeyEvent evt) {
        KeySelectionManager ksm = getKeySelectionManager();
        if (ksm != null) {
            int idx = ksm.selectionForKey(evt.getKeyChar(), model);
            if (idx >= 0) {
                model.setSelectedItem(model.getElementAt(idx));
            }
        }
    }

    protected boolean isSelected(ComboItem ci, Object value) {
        if (value != null && !ValueUtil.isEmpty(itemKey)) {
            if (ci.getValue() == null) {
                return false;
            }

            PropertyResolver res = PropertyResolver.getInstance();
            Object key = res.getProperty(ci.getValue(), itemKey);
            return key != null && value.equals(key);
        } else {
            ComboItem c = new ComboItem(value);
            return (ci == null ? false : ci.equals(c));
        }
    }

    public boolean isNullWhenEmpty() {
        return true;
    }

    public String[] getDepends() {
        return depends;
    }

    public void setDepends(String[] depends) {
        this.depends = depends;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDisableWhen() {
        return disableWhen;
    }

    public void setDisableWhen(String disableWhen) {
        this.disableWhen = disableWhen;
    }

    public String getVisibleWhen() {
        return visibleWhen;
    }

    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public String getCaption() {
        return property.getCaption();
    }

    public void setCaption(String caption) {
        property.setCaption(caption);
    }

    public char getCaptionMnemonic() {
        return property.getCaptionMnemonic();
    }

    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }

    public boolean isRequired() {
        return property.isRequired();
    }

    public void setRequired(boolean required) {
        property.setRequired(required);
    }

    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }

    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return property.isShowCaption();
    }

    public void setShowCaption(boolean showCaption) {
        property.setShowCaption(showCaption);
    }

    public Font getCaptionFont() {
        return property.getCaptionFont();
    }

    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }

    public String getCaptionFontStyle() {
        return property.getCaptionFontStyle();
    }

    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle);
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } else {
            super.setFont(sourceFont);
        }
        new FontSupport().applyStyles(this, fontStyle);
    }

    public Insets getCellPadding() {
        return property.getCellPadding();
    }

    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }

    public void validateInput() {
        validateInput(actionMessage);
    }

    public void validateInput(ActionMessage am) {
        am.clearMessages();
        property.setErrorMessage(null);
        if (isRequired() && ValueUtil.isEmpty(getValue())) {
            am.clearMessages();
            am.addMessage("1001", "{0} is required.", new Object[]{getCaption()});
            property.setErrorMessage(am.toString());
        }
    }

    public ActionMessage getActionMessage() {
        return actionMessage;
    }

    public ControlProperty getControlProperty() {
        return property;
    }

    public Class getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }

    public void setRequestFocus(boolean focus) {
        if (focus) {
            requestFocus();
        }
    }

    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public Object getItemsObject() {
        return itemsObject;
    }

    public void setItemsObject(Object itemsObject) {
        this.itemsObject = itemsObject;
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
        if (info == null) {
            return;
        }

        PropertyInfoWrapper pi = new PropertyInfoWrapper(info);
        setExpression(pi.getExpression());
        setItemKey(pi.getItemKey());

        Object items = pi.getItems();
        if (items instanceof String) {
            setItems(items.toString());
            setItemsObject(null);
        } else {
            setItems(null);
            setItemsObject(items);
        }
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        super.setEnabled(!readonly);
        super.firePropertyChange("enabled", readonly, !readonly);
        repaint();
    }

    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  helper methods  ">
    
    private Binding getCurrentBinding() {
        if ("true".equals(getClientProperty(JTable.class) + "")) {
            if (getVarName() == null) {
                setVarName("item");
            }

            Object o = getClientProperty(Binding.class);
            if (o instanceof Binding) {
                return (Binding) o;
            }
        }
        return getBinding();
    }

    private Collection fetchItems() {
        Collection list = null;
        try {
            Class type = null;
            Binding oBinding = getCurrentBinding();
            Object beanItems = UIControlUtil.getBeanValue(oBinding, getItems());
            if (beanItems != null) {
                type = beanItems.getClass();
                if (type.isArray()) {
                    list = Arrays.asList((Object[]) beanItems);
                } else if (beanItems instanceof Collection) {
                    list = (Collection) beanItems;
                }
            } else {
                if (fieldType != null) {
                    type = fieldType;
                } else {
                    type = UIControlUtil.getValueType(this, getName());
                }

                //if type is null, happens when the source is a Map key and no fieldType supplied
                //try to use the classtype of the value if it is not null
                if (type == null) {
                    Object value = UIControlUtil.getBeanValue(this);
                    if (value != null) {
                        type = value.getClass();
                    }
                }

                if (type != null && type.isEnum()) {
                    list = Arrays.asList(type.getEnumConstants());
                }
            }
        } catch (Throwable e) {;}

        if (itemsObject != null) {
            Collection col = null;
            if (itemsObject instanceof Collection) {
                col = (Collection) itemsObject;
            } else if (itemsObject.getClass().isArray()) {
                col = Arrays.asList((Object[]) itemsObject);
            }

            if (list == null) {
                list = col;
            } else {
                list.addAll(col);
            }
        }
        return list;
    }

    private void buildList(ComboBoxModelImpl model) {
        Collection list = fetchItems();
        if (list == null) { return; }

        ExpressionResolver er = ExpressionResolver.getInstance();
        for (Object o : list) {
            Object caption = null;
            if (!ValueUtil.isEmpty(expression)) {
                Object exprBean = createExpressionBean(o);
                caption = UIControlUtil.evaluateExpr(exprBean, expression);
            }

            if (caption == null) {
                caption = o;
            }

            model.addItem(o, (caption==null ? "" : caption.toString()));
        }
    }

    private Object createExpressionBean(Object itemBean) {
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean);
        return beanSupport.createProxy();
    }

    // </editor-fold> 
        
    public void load() {
        ComboBoxModelImpl newModel = createModelImpl();
        if (!isDynamic()) {
            buildList( newModel );
            newModel.setSelectedItem( null ); 
        } 
        super.setModel( newModel ); 
        model = newModel; 
        
        if (!immediate) {
            //super.addItemListener(this);
        } else {
            super.setInputVerifier(new InputVerifier() {
                public boolean verify(JComponent input) {
                    if (isPopupVisible()) {
                        return true;
                    }

                    return UIInputUtil.VERIFIER.verify(input);
                }
            });
        }
    }

    public void refresh() { 
        try {
            if (isDynamic()) {
                ComboBoxModelImpl newModel = createModelImpl();
                buildList( newModel );
                loadBeanValue( newModel ); 
                
                super.setModel( newModel ); 
                model = newModel; 
                model.nopublish = true; 
                
            } else {
                model.nopublish = true; 
                loadBeanValue( model ); 
            }
            
            if ( isAutoDefaultValue()) {
                if ( getSelectedIndex() < 0 ) {
                    try { 
                        setSelectedIndex(0); 
                    } catch(Throwable t){;} 
                }
                
                updateBeanValue(); 
            }
            
            String whenExpr = getVisibleWhen();
            if (whenExpr != null && whenExpr.length() > 0) {
                boolean result = false;
                try {
                    result = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                setVisible(result);
            }

            if (isEnabled()) {
                setReadonly(isReadonly());
            }

            whenExpr = getDisableWhen();
            if (whenExpr != null && whenExpr.length() > 0) {
                boolean disabled = false;
                try {
                    disabled = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                setEnabled(!disabled);
            }
        } catch (Exception e) {
            setEnabled(false);
            setFocusable(false);

            if (ClientContext.getCurrentContext().isDebugMode()) {
                e.printStackTrace();
            }
        } finally {
            model.nopublish = false; 
        }
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }

    public Map getInfo() {
        Map map = new HashMap();
        map.put("allowNull", isAllowNull());
        map.put("dynamic", isDynamic());
        map.put("emptyText", getEmptyText());
        map.put("expression", getExpression());
        map.put("fieldType", getFieldType());
        map.put("immediate", isImmediate());
        map.put("itemKey", getItemKey());
        map.put("items", getItems());
        map.put("itemsObject", getItemsObject());
        map.put("required", isRequired());
        map.put("varName", getVarName());
        return map;
    }

    public int getStretchWidth() {
        return stretchWidth;
    }

    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth;
    }

    public int getStretchHeight() {
        return stretchHeight;
    }

    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }

    protected void onItemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) { 
            updateBeanValue(); 
        }
    }

    protected final void fireItemStateChanged(ItemEvent e) {
        if (isReadonly()) { return; }
        else if (model != null && model.nopublish) { return; }

        onItemStateChanged(e);
        super.fireItemStateChanged(e);
    }

    // <editor-fold defaultstate="collapsed" desc=" ComboBoxModel ">
    
    private ComboBoxModelImpl createModelImpl() { 
        ComboBoxModelImpl model = new ComboBoxModelImpl(); 
        if (isAllowNull()) { 
            model.addItem(null, getEmptyText()); 
        } 
        return model; 
    } 
    private void loadBeanValue(ComboBoxModel model) { 
        if ( model == null ) { return; }
        
        Object value = UIControlUtil.getBeanValue(this); 
        model.setSelectedItem( findItem(model, value) ); 
    }
    private void updateBeanValue() {
        Object newValue = getValue(); 
        Object oldValue = UIControlUtil.getBeanValue(this); 

        boolean matched = false; 
        if ( oldValue == null && newValue == null ) {
            matched = true; 
        } else if ( oldValue != null && newValue != null && oldValue.equals(newValue)) {
            matched = true; 
        }

        if ( !matched ) {
            UIInputUtil.updateBeanValue(this);
        }
    }
    private Object findItem(ComboBoxModel model, Object value) {
        String itemkey = getItemKey(); 
        boolean hasItemKey = (itemkey != null && itemkey.trim().length() > 0); 
        for (int i=0; i<model.getSize(); i++) {
            ComboItem item = (ComboItem) model.getElementAt(i); 
            Object itemval = item.value; 
            if ( hasItemKey ) {
                itemval = PropertyResolver.getInstance().getProperty(itemval, itemkey); 
            }
            
            if ( isEqual(value, itemval)) {
                return item; 
            } 
        }
        return null; 
    }
    private boolean isEqual( Object o1, Object o2 ) {
        if (o1 == null && o2 == null) {
            return true; 
        } else if (o1 != null && o2 == null ) {
            return false; 
        } else if (o1 == null && o2 != null ) {
            return false;
        } else { 
            return ( o1 != null && o2 != null && o1.equals(o2)); 
        } 
    }
    
    
    private class ComboBoxModelImpl extends DefaultComboBoxModel {

        XComboBox root = XComboBox.this;
        boolean nopublish;

        void addItem(Object item) {
            addItem(item, (item == null ? "" : item.toString())); 
        }
        void addItem(Object item, String caption) { 
            addElement(new ComboItem(item, caption)); 
        }

        public void setSelectedItem(Object item) {
            if ( item == null ) {
                super.setSelectedItem( null ); 
            } else if ( item instanceof ComboItem ) {
                super.setSelectedItem( item ); 
            } else {
                super.setSelectedItem(new ComboItem( item ));
            } 
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ComboItem ">
    
    public class ComboItem {

        XComboBox root = XComboBox.this;
        private String text;
        private Object value;

        public ComboItem(Object value) { 
            this(value, (value == null ? "" : value.toString())); 
        }

        public ComboItem(Object value, String text) {
            this.value = value; 
            this.text = (ValueUtil.isEmpty(text) ? "" : text); 
        }

        public String toString() {
            return text;
        }

        public Object getValue() {
            return value;
        }

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ComboItem)) {
                return false;
            }

            ComboItem ci = (ComboItem) o;
            return root.isEqual(value, ci.value); 
        }
    }

    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyInfoWrapper "> 
    
    private class PropertyInfoWrapper {

        private PropertySupport.ComboBoxPropertyInfo property;
        private Map map = new HashMap();

        PropertyInfoWrapper(PropertySupport.PropertyInfo info) {
            if (info instanceof Map) {
                map = (Map) info;
            }
            if (info instanceof PropertySupport.ComboBoxPropertyInfo) {
                property = (PropertySupport.ComboBoxPropertyInfo) info;
            }
        }

        public String getExpression() {
            Object value = map.get("expression");
            if (value == null && property != null) {
                value = property.getExpression();
            }

            return (value == null ? null : value.toString());
        }

        public String getItemKey() {
            Object value = map.get("itemKey");
            if (value == null && property != null) {
                value = property.getItemKey();
            }

            return (value == null ? null : value.toString());
        }

        public Object getItems() {
            Object value = map.get("items");
            if (value == null && property != null) {
                value = property.getItems();
            }

            return value;
        }
    }
    // </editor-fold>        
}
