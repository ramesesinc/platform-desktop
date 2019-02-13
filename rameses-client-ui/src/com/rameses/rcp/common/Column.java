package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.constant.TextCase;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Column implements Serializable 
{    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String caption;
    private Column.TypeHandler typeHandler;
    
    private int width = 100;
    private int minWidth;
    private int maxWidth;
    private boolean required;
    private boolean resizable = true;
    private boolean nullWhenEmpty = true;
    private boolean editable;
    private String editableWhen;
    private TextCase textCase = TextCase.UPPER;
    
    private String type = "string";   
    private String expression;
    private String visibleWhen;
    private boolean visible = true;
        
    //checkbox support
    private Class fieldType;    
    private Object checkValue;
    private Object uncheckValue;

    //combobox support
    private Object items;
    
    //lookup support
    private Object handler;
        
    private String fieldname;
    private int rowheight;
    private boolean primary;
    private boolean htmlDisplay;
    private String format;
    
    private String alignment;
    
    //icon support
    private String iconVisibleWhen;
    
    //icon text field support
    private String icon;
    private String iconOrientation;
    
    //tree table support
    private String toggleIcon;
    private String headerIcon;
    
    private String validateExpression;
    private String category;
    
    
    //action text support
    private Object action;
    
    private boolean dynamic;
    
    
    private Map properties = new HashMap();
    
    public Column() {
    }
    
    public Column(String name, String caption) {
        this(name, caption, (Column.TypeHandler) null); 
    }

    public Column(String name, String caption, Column.TypeHandler typeHandler){
        this.name = name;
        this.caption = caption;
        this.typeHandler = typeHandler; 
    }
    
    public Column
    (
        String name, String caption, int width, int minWidth, int maxWidth, 
        boolean required, boolean resizable, boolean nullWhenEmpty, boolean editable, 
        String editableWhen, Column.TypeHandler typeHandler
    )
    {
        this.name = name;
        this.caption = caption;
        this.width = width;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.required = required;
        this.resizable = resizable;
        this.nullWhenEmpty = nullWhenEmpty;
        this.editable = editable;
        this.editableWhen = editableWhen; 
        this.typeHandler = typeHandler; 
        
        if (this.typeHandler != null) 
            this.typeHandler.setColumn(this);
    }
    
    public Column( String name, String caption, String type, Map props ) {
        this(name, caption);
        this.type = type;
        if( props != null) {
            this.setProperties(props);
        }
    }
    
    public Column( String name, String caption, String type, boolean editable, boolean required ) {
        this(name, caption, type, null, required);
        this.editable = editable;
    }
    
    public Column( String name, String caption, String type, Map props, boolean required ) {
        this(name, caption, type, props);
        this.required = required;
    }
    
    public Column( String name, String caption, String type, Map props, int width ) {
        this(name, caption, type, props);
        this.width = width;
    }
    
    public Column( Map data ) {
        init(data); 
    }
    
    public Column( Object[] dataArray ) { 
        init(dataArray); 
    }
    
    
    /**
     * Do not remove this. This is used by the client support in the ListColumn Property Editor
     */
    public Column clone() {
        Column col = new Column(getName(), getCaption());
        col.typeHandler = typeHandler;
        col.width = width;
        col.minWidth = minWidth;
        col.maxWidth = maxWidth;
        col.required = required;        
        col.resizable = resizable;
        col.nullWhenEmpty = nullWhenEmpty;
        col.editable = editable;
        col.editableWhen = editableWhen;
        col.properties = properties; 
        col.alignment = alignment;

        col.type = type;        
        col.visible = visible;
        col.textCase = textCase;        
        col.fieldname = fieldname;
        col.rowheight = rowheight;
        col.primary = primary;
        col.htmlDisplay = htmlDisplay;
        col.format = format;
        col.fieldType = fieldType;
        col.iconVisibleWhen = iconVisibleWhen;
        col.icon = icon;
        col.iconOrientation = iconOrientation;
        col.toggleIcon = toggleIcon;
        col.headerIcon = headerIcon;
        col.expression = expression;
        col.category = category;
        col.checkValue = checkValue;
        col.uncheckValue = uncheckValue;
        col.action = action;
        return col;
    }    
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
        
    public String getName() { return name; }    
    public void setName(String name) { this.name = name; }
    
    public String getCaption() { return caption; }    
    public void setCaption(String caption) { this.caption = caption; }
    
    public Column.TypeHandler getTypeHandler() { return typeHandler; } 
    public void setTypeHandler(Column.TypeHandler typeHandler) { 
        Column.TypeHandler oldTypeHandler = this.typeHandler;
        if (oldTypeHandler != null) oldTypeHandler.setColumn(null); 
        
        this.typeHandler = typeHandler; 
        if (this.typeHandler != null) { 
            this.typeHandler.setColumn(this);
            type = this.typeHandler.getType(); 
        } 
        if ( type == null ) {
            type = "string";   
        }
    } 
    
    public int getWidth() { return width; }    
    public void setWidth(int width) { 
        this.width = width; 
    }
        
    public int getMinWidth() { return minWidth; }    
    public void setMinWidth(int minwidth) { 
        this.minWidth = minwidth; 
    }
    
    public int getMaxWidth() { return maxWidth; }    
    public void setMaxWidth(int maxwidth) { 
        this.maxWidth = maxwidth; 
    }
    
    public boolean isRequired() { return required; }    
    public void setRequired(boolean required) { 
        this.required = required; 
    }
    
    public boolean isResizable() { return resizable; }    
    public void setResizable(boolean resizable) { 
        this.resizable = resizable; 
    }
        
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }     
    
    public boolean isEditable() { return editable; }    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public String getEditableWhen() { return editableWhen; }    
    public void setEditableWhen(String editableWhen) {
        this.editableWhen = editableWhen;
    }    
    
    public TextCase getTextCase() { return textCase; }
    public void setTextCase(TextCase textCase) { this.textCase = textCase; }          
    public void setTextCase(String stextCase) 
    { 
        try { 
            this.textCase = TextCase.valueOf(stextCase.toUpperCase()); 
        } catch(Exception ex) {
            this.textCase = TextCase.NONE; 
        }
    }
    
    public String getType() { return type; }    
    public void setType(String type) { this.type = type; } 
    
    public String getExpression() { return expression; }     
    public void setExpression(String expression) {
        this.expression = expression;
    }   
    
    public String getVisibleWhen() { return visibleWhen; }     
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }     
    
    public boolean isVisible() { return visible; }    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
        
    public Map getProperties() { return properties; }    
    public void setProperties(Map properties) {
        this.properties = properties;
    }
        
    public int getRowheight() { return rowheight; }    
    public void setRowheight(int rowheight) {
        this.rowheight = rowheight;
    }
    
    public String getFieldname() 
    {
        if (fieldname == null) return name;
        
        return fieldname;
    }
    
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
    
    public boolean isPrimary() { return primary; }    
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
    
    public boolean isHtmlDisplay() { return htmlDisplay; }    
    public void setHtmlDisplay(boolean htmlDisplay) {
        this.htmlDisplay = htmlDisplay;
    }
    
    public String getFormat() { return format; }    
    public void setFormat(String format) {
        this.format = format;
    }
        
    public Object getHandler() { return handler; }    
    public void setHandler(Object handler) {
        this.handler = handler;
    }
    
    public Object getItems() { return items; }    
    public void setItems(Object items) {
        this.items = items;
    }
    
    public Class getFieldType() { return fieldType; }    
    public void setFieldType(Class fieldType) {
        this.fieldType = fieldType;
    }
    
    public String getAlignment() { return alignment; }    
    public void setAlignment(String alignment) { 
        this.alignment = alignment; 
    }
    
    public String getIcon() { return icon; }    
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getHeaderIcon() { return headerIcon; }    
    public void setHeaderIcon(String headerIcon) {
        this.headerIcon = headerIcon;
    }
    
    public String getValidateExpression() { return validateExpression; } 
    public void setValidateExpression(String validateExpression) {
        this.validateExpression = validateExpression;
    }
    
    public String getCategory() { return category; }    
    public void setCategory(String category) {
        this.category = category;
    }

    public String getToggleIcon() { return toggleIcon; }
    public void setToggleIcon(String toggleIcon) {
        this.toggleIcon = toggleIcon;
    }

    public String getIconVisibleWhen() { return iconVisibleWhen; }
    public void setIconVisibleWhen(String iconVisibleWhen) {
        this.iconVisibleWhen = iconVisibleWhen;
    }

    public Object getUncheckValue() { return uncheckValue; }
    public void setUncheckValue(Object uncheckValue) {
        this.uncheckValue = uncheckValue;
    }

    public Object getCheckValue() { return checkValue; }
    public void setCheckValue(Object checkValue) {
        this.checkValue = checkValue;
    }

    public String getIconOrientation() { return iconOrientation; }
    public void setIconOrientation(String iconOrientation) {
        this.iconOrientation = iconOrientation;
    }

    public Object getAction() { return action; }
    public void setAction(Object action) {
        this.action = action;
    }

    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
       
    // </editor-fold>
    
    public final Column set(String name, Object value) {
        try {
            PropertyResolver.getInstance().setProperty(this, name, value); 
            return this;
        } catch(Exception ex) {
            System.out.println("Unable to set property value on '"+name+"' on Column object");
            return this; 
        }
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" init( data ) ">    
    
    private void init(Map data) {
        PropertyResolver res = PropertyResolver.getInstance(); 
        Set<Map.Entry<Object,Object>> entries = data.entrySet();
        for (Map.Entry entry: entries) {
            String key = entry.getKey().toString();
            Object val = entry.getValue(); 
            res.setProperty(this, key, val); 
        }
        getProperties().putAll(data); 
        
        try { 
            Object otextcase = data.get("textcase"); 
            setTextCase(otextcase == null? null: otextcase.toString());
        } catch(Throwable t){;} 
    }
    
    private void init(Object[] dataArray) {
        if (dataArray == null) return;
        
        PropertyResolver res = PropertyResolver.getInstance(); 
        for (Object o: dataArray) {
            try {
                Object[] values = (Object[]) o;    
                res.setProperty(this, values[0].toString(), values[1]); 
            } catch(Throwable t){;} 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TypeHandler (class) ">
    
    public static abstract class TypeHandler extends HashMap implements PropertySupport.PropertyInfo 
    {    
        private static final long serialVersionUID = 1L; 
        
        private Column column;
        
        public abstract String getType(); 

        public final Column getColumn() { return column; } 
        void setColumn(Column column) { 
            this.column = column; 
            columnChanged();
        } 
        
        protected void columnChanged(){}
        
        public boolean equals(Object o) {
            if (super.equals(o)) return true;
            
            return (getType() == o);
        }  
    }
    
    // </editor-fold>
}
