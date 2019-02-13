/*
 * XTabbedList.java
 *
 * Created on September 16, 2013, 4:51 PM
 * @author wflores
 */

package com.rameses.rcp.control; 

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener; 
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TabbedListModel;
import com.rameses.rcp.control.tabbedpane.TabbedItemPanel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.Warning;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class XTabbedList extends JTabbedPane implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo 
{    
    private Binding binding;
    private String[] depends;
    private int index;
    
    private boolean dynamic;
    private String disableWhen;
    
    private int oldIndex;
    private List<Map> items = new ArrayList();
    
    private TabbedListModel model;
    private boolean noSelectionAllowed;
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen; 
        
    public XTabbedList() 
    {
        super();
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents ">
    
    private void initComponents() 
    {
        model = new TabbedListModel();
        if (Beans.isDesignTime()) {
            addTab("Tab 1", new JLabel());
            addTab("Tab 2", new JLabel());
            setPreferredSize(new Dimension(100,30)); 
        }
        new MouseEventSupport(this).install(); 
    }
    
    //</editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public String getDisableWhen() { return disableWhen; } 
    public void setDisableWhen(String disableWhen) {
        this.disableWhen = disableWhen;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">    
    
    public void setBinding(Binding binding) { this.binding = binding; }
    public Binding getBinding() { return binding; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
        
    public void load() { 
        try {
            if ( !dynamic ) loadTabs();
        } catch(Throwable t) {
            System.out.println("[WARN] error loading tabs caused by " + t.getMessage());
        }
    }
    
    public void refresh() {
        boolean dynamic = isDynamic();
        try {
            if (dynamic) loadTabs();
        } catch(Throwable t) {
            System.out.println("[WARN] error loading tabs caused by " + t.getMessage()); 
        } 

        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        ExpressionResolver er = ExpressionResolver.getInstance();
        String expr = getDisableWhen();
        if (expr != null && expr.length() > 0 && bean != null) {
            try {
                boolean b = er.evalBoolean(expr, bean); 
                setEnabled(!b); 
            } catch(Throwable t){;} 
        } 
        
        if (!dynamic) {
            Component comp = getSelectedComponent(); 
            if (comp instanceof TabbedItemPanel) {
                TabbedItemPanel itemPanel = (TabbedItemPanel)comp;
                itemPanel.refreshContent(); 
            }
        }
        
        int tabCount = getTabCount();
        for (int i=0; i<tabCount; i++) {
            Component c = getComponentAt(i);
            if (!(c instanceof TabbedItemPanel)) continue;
            
            Opener op = ((TabbedItemPanel) c).getOpener(); 
            Object ov = op.getProperties().get("disableWhen");
            String disableWhen = (ov == null? null: ov.toString()); 
            if (disableWhen == null || disableWhen.length() == 0) continue;
            
            try {
                boolean b = er.evalBoolean(disableWhen, bean); 
                setEnabledAt(i, !b); 
            } catch(Throwable t){;} 
        } 
        
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }        
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        map.put("disableWhen", getDisableWhen());
        return map;
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
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property;
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    public void setSelectedIndex(int index) { 
        if (Beans.isDesignTime()) {
            super.setSelectedIndex(index);
            return; 
        }
        
        if (noSelectionAllowed) return; 
        
        try { 
            Component c = getComponentAt(index);
            Object item = items.get(index);
            if (model.beforeSelect(item, index)) {
                model.onselect(item);    
            } else {
                return; 
            }             
        } catch(Throwable t) {
            if (t instanceof Warning) {
                MsgBox.alert(t.getMessage()); 
            } else { 
                MsgBox.err(t); 
            } 
            return;
        }
        
        this.oldIndex = getSelectedIndex();
        super.setSelectedIndex(index);
    } 
    
    private void loadTabs() {
        loadItems();
        removeAll();
                
        ExpressionResolver expRes = ExpressionResolver.getInstance();
        for (Map op: items) {
            Object ov = op.get("visibleWhen");
            String sv = (ov == null? null: ov.toString()); 
            boolean allowed = true;
            if (sv != null && sv.length() > 0) {
                try {
                    allowed = expRes.evalBoolean(sv, getBinding().getBean()); 
                } catch(Throwable t){;} 
            }
            if (!allowed) continue;

            String caption = op.get("caption")+"";            
            try {
                noSelectionAllowed = true;
                JLabel lbl = new JLabel("");
                lbl.setPreferredSize(new Dimension(10,10));
                lbl.setMaximumSize(new Dimension(10,10));
                super.addTab(caption, getItemIcon(op), lbl); 
            } catch(Throwable t) {
                //do nothing 
            } finally {
                noSelectionAllowed = false;
            }            
        }
        
        if (getTabCount() > 0) setSelectedIndex(0);        
    }
    
    private Icon getItemIcon(Map o) {
        Object ov = o.get("icon");
        if (ov != null) { 
            return ImageIconSupport.getInstance().getIcon(ov.toString());
        } else { 
            return null; 
        }
    }
    
    private void loadItems() {
        items.clear();
        
        Object value = null;
        String name = getName();
        if (name != null && name.length() > 0) {
            try { 
                value = UIControlUtil.getBeanValue(this); 
            } catch(Throwable t){;}
        } 
        
        TabbedListModel newModel = null;
        if (value instanceof TabbedListModel) {
            newModel = (TabbedListModel)value; 
            newModel.setProvider(getProviderImpl()); 
        } else {
            newModel = new TabbedListModel(); 
        }

        value = newModel.getList();        
        if (value == null) {
            //do nothing
        } else if (value.getClass().isArray()) {
            Map[] arrays = (Map[]) value;
            for (Map o: arrays) items.add(o); 
            
        } else if (value instanceof Collection) {
            items.addAll((Collection) value);
        } 
        
        this.model = newModel; 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ProviderImpl ">
    
    private ProviderImpl providerImpl; 
    
    private ProviderImpl getProviderImpl() {
        if (providerImpl == null) {
            providerImpl = new ProviderImpl();
        } 
        return providerImpl;
    }
    
    private class ProviderImpl implements TabbedListModel.Provider 
    {
        XTabbedList root = XTabbedList.this;
        
        public Object getBinding() {
            return root.getBinding(); 
        }        

        public void reload() { 
            Component comp = root.getSelectedComponent(); 
        }
        
        public void refresh() { 
            Component comp = root.getSelectedComponent(); 
        }        
    } 
    
    // </editor-fold> 

}
