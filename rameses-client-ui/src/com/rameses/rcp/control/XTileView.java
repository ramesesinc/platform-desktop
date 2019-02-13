/*
 * XTileView.java
 *
 * Created on February 6, 2014, 9:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ComponentSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TileViewModel;
import com.rameses.rcp.control.panel.TilePanel;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.util.BreakException;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores
 */
public class XTileView extends TilePanel implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo  
{
    private TileViewModel model;
    private List<TileViewItem> tiles;
    
    private Binding binding;
    private String[] depends;
    private boolean dynamic;
    private int index;

    private ComponentSupport componentSupport;
    
    private String target;
    private String formName;
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen;
            
    public XTileView() {
        super();
        new MouseEventSupport(this).install(); 
    }
        
    public ComponentSupport getComponentSupport() {
        if (componentSupport == null) {
            componentSupport = new ComponentSupport();
        }
        return componentSupport; 
    }
    
    public void refresh() {
        refreshItems(false);
        
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
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
    
    public void load() {
        if (!isDynamic()) { 
            buildItems(); 
        } 
    } 
    
    public void reload() {
        refreshItems(true);
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic());
        map.put("showCaptions", isShowCaptions()); 
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
    
    // <editor-fold defaultstate="collapsed" desc="  helper methods  ">
    
    private void refreshItems(boolean reload) {
        if (isDynamic() || reload) {
            buildItems(); 
        } 
        buildControls(); 
    }
    
    private void buildItems() {
        removeAll();
        
        TileViewModel model = null; 
        Object value = null;
        try {
            value = UIControlUtil.getBeanValue(this);
        } catch(Exception e) {;}
        
        if (value == null) {
            model = new TileViewModelImpl();
        } else if (value instanceof TileViewModel) {     
            model = (TileViewModel) value;
        } else { 
            List items = new ArrayList();
            if (value.getClass().isArray()) { 
                for (Object item: (Object[]) value) { 
                    items.add(item); 
                } 
            } else if (value instanceof Collection) {
                items.addAll((Collection) value);
            } 
            model = new TileViewModelImpl(items); 
        } 

        this.model = model; 
        tiles = buildTileViews(model); 
    } 
    
    private void buildControls() {
        removeAll();
        
        if (tiles != null) {
            for (TileViewItem item : tiles) {
                String permission = item.getPermission();            
                String domain = item.getDomain();
                String role = item.getRole();
                boolean allowed = ControlSupport.isPermitted(domain, role, permission);
                if (!allowed) continue;

                boolean visible = true; 
                String expression = item.getVisibleWhen(); 
                if (expression != null && expression.trim().length() > 0) { 
                    try { 
                        visible = UIControlUtil.evaluateExprBoolean(binding.getBean(), expression); 
                    } catch(Throwable t) { 
                        visible = false; 
                        t.printStackTrace();
                    } 
                } 

                if (visible) {
                    ImageIcon icon = null;
                    Object obj = item.getProperty("icon"); 
                    if ( obj instanceof byte[]) {
                        icon = new ImageIcon((byte[]) obj); 
                    } else {
                        icon = ImageIconSupport.getInstance().getIcon(item.getIcon()); 
                    }
                    addItem(item.getCaption(), item, icon); 
                }
            } 
        }
        revalidate();
        repaint(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
        
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public int getHorizontalAlignment() { return 0; }
    public void setHorizontalAlignment(int horizontalAlignment) {}
        
    public boolean focusFirstInput() {
        return false;
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public String getTarget() { return target; } 
    public void setTarget(String target) { 
        this.target = target; 
    } 
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" TileViewModelImpl ">
    
    private class TileViewModelImpl extends TileViewModel 
    {
        private List items;
        private List<TileViewItem> tiles;
        
        TileViewModelImpl() {
            this(null);
        }
        
        TileViewModelImpl(List items) {
            this.items = (items == null? new ArrayList(): items);
        }
        
        public List getList() {  return items; }                
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TileViewItem ">
 
    static interface TileViewItem 
    {
        public int getIndex();
        public String getName();
        public String getCaption();
        public String getIcon();
        public Object getUserObject();
        public Object getProperty(String key);
        public String getDomain();
        public String getRole();
        public String getPermission();
        public boolean isUpdate();
        public boolean isImmediate();
        public String getVisibleWhen();
        public Map getProperties();
        public String getTooltip();
        public char getMnemonic();
    }
    
    private List<TileViewItem> buildTileViews(TileViewModel model) {
        Map params = new HashMap();
        List<TileViewItem> tiles = new ArrayList();
        List list = (model == null? null: model.fetchList(params));
        if (list == null) return tiles;
        
        for (Object item : list) {
            if (item instanceof Map) {
                tiles.add(new TileViewMap((Map) item));
            } else if (item instanceof Action) {
                tiles.add(new TileViewAction((Action) item));
            }
        }

        Collections.sort(tiles, new Comparator<TileViewItem>(){
            public int compare(XTileView.TileViewItem o1, XTileView.TileViewItem o2) {
                int i1 = o1.getIndex();
                int i2 = o2.getIndex();
                if (i1 < i2) return -1;
                else if (i1 > i2) return 1;
                else return 0;
            }
        });
        return tiles;
    }
    
    public class TileViewMap implements TileViewItem
    {
        Map item;
        
        TileViewMap(Map item) {
            this.item = (item == null? new HashMap(): item);
        }
        
        public int getIndex() {
            try { 
                return Integer.parseInt(getProperty("index").toString()); 
            } catch(Throwable t) { 
                return 0; 
            } 
        }
        public String getName() {
            return getString("name");
        }
        public String getCaption() {
            return getString("caption");
        }
        public String getIcon() {
            return getString("icon");
        }
        public Object getUserObject() {
            return item;
        } 
        public Object getProperty(String key) {
            return item.get(key); 
        }
        public String getDomain() {
            return getString("domain");
        }
        public String getRole() {
            return getString("role");
        }
        public String getPermission() {
            return getString("permission");
        }
        public boolean isUpdate() {
            return getBoolean("update");
        }
        public boolean isImmediate() {
            return getBoolean("immediate");
        }    
        public String getVisibleWhen() {
            return getString("visibleWhen"); 
        }
        public Map getProperties() {
            Object value = (item == null? null: item.get("properties"));
            return (Map) value; 
        }
        public String getTooltip() {
            return getString("tooltip");
        }
        public char getMnemonic() {
            try {
                return getString("mnemonic").charAt(0);
            } catch(Throwable t) {
                return '\u0000';
            }
        }  
        private String getString(Object key) {
            Object value = (item == null? null: item.get(key));
            return (value == null? null: value.toString()); 
        }
        private boolean getBoolean(Object key) {
            Object value = (item == null? null: item.get(key));
            return ("true".equals(value+""));
        } 
    }
    
    public class TileViewAction implements TileViewItem
    {
        Action item;
        
        TileViewAction(Action item) {
            this.item = item;
        }
        
        public int getIndex() {
            try { 
                return Integer.parseInt(getProperty("index").toString()); 
            } catch(Throwable t) { 
                return 0; 
            } 
        }
        public String getName() {
            return (item == null? null: item.getName()); 
        }        
        public String getCaption() {
            return (item == null? null: item.getCaption());
        }
        public String getIcon() {
            return (item == null? null: item.getIcon()); 
        }
        public Object getUserObject() {
            return item;
        } 
        public Object getProperty(String key) {
            return (item == null? null: item.getProperties().get(key));
        }
        public String getDomain() {
            return (item == null? null: item.getDomain());
        }
        public String getRole() {
            return (item == null? null: item.getRole());
        }
        public String getPermission() {
            return (item == null? null: item.getPermission());
        }    
        public boolean isUpdate() {
            return (item == null? null: item.isUpdate());
        }
        public boolean isImmediate() {
            return (item == null? null: item.isImmediate());
        }     
        public String getVisibleWhen() {
            return (item == null? null: item.getVisibleWhen());
        }
        public Map getProperties() {
            return (item == null? new HashMap(): item.getProperties());
        }    
        public String getTooltip() {
            return (item == null? null: item.getTooltip());
        }
        public char getMnemonic() {
            return (item == null? null: item.getMnemonic());
        }          
    }    
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionProcess ">
    
    private Vector LOCKS = new Vector();
    
    protected void onselect(Object obj) {
        if (obj instanceof TileViewItem) {
            if (model == null) return;
        
            TileViewItem item = (TileViewItem)obj;
            if (LOCKS.contains(item)) return;

            LOCKS.addElement(item);
            new Thread(new ActionProcess(item)).start();
        }
    }    
    
    private class ActionProcess implements Runnable 
    {
        private TileViewItem item;
        
        ActionProcess(TileViewItem item) {
            this.item = item;
        }
        
        public void run() {
            try { 
                if (item == null) return;
                
                Object result = model.onOpenItem(item.getUserObject()); 
                if (!(result instanceof Opener)) return;

                Opener opener = (Opener)result;
                String target = opener.getTarget()+"";
                if (!target.matches("process|_process|window|_window|popup|_popup|self|_self")) {
                    opener.setTarget("window"); 
                }
                Binding binding = getBinding();
                if (binding != null) binding.fireNavigation(opener); 
                
            } catch(BreakException be) {
                be.printStackTrace(); 
                //do nothing 
            } catch(Throwable t) {
                t.printStackTrace(); 
                MsgBox.err(t); 
                
            } finally { 
                LOCKS.removeElement(item); 
            }
        }
    }
    
    // </editor-fold>
}