/*
 * XTileView.java
 *
 * Created on February 6, 2014, 9:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.common.ExpressionResolver;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.control.border.BorderProxy;
import com.rameses.rcp.control.border.XToolbarBorder;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ActionButtonSupport;
import com.rameses.rcp.support.ComponentSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.ui.UIComposite;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TileViewModel;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class XTileView extends JPanel implements UIComposite, MouseEventSupport.ComponentInfo  
{
    private List<TileViewItem> tiles;    
    private TileViewModel model;
    
    private Binding binding;
    private String[] depends;
    private boolean useToolBar;
    private boolean dynamic;
    private int spacing = 0;
    private int index;

    private Insets padding;
    private BorderProxy borderProxy;    
    private ComponentSupport componentSupport;
    
    //XButton target
    private String target;
    private String formName;
    
    private List<XButton> buttons = new ArrayList();
    private JComponent toolbarComponent;
    
    //flag
    private boolean dirty;
    
    //button template
    private XButton buttonTpl = new XButton();
    private String textAlignment = "CENTER_LEFT";
    private String textPosition = "CENTER_TRAILING";
    
    private int buttonCaptionOrientation = SwingConstants.CENTER;
    private boolean buttonTextInHtml;
    private boolean buttonAsHyperlink;
    private boolean showCaptions = true;
    
    public XTileView() {
        padding = new Insets(0, 0, 0, 0);        
        borderProxy = new BorderProxy();
        borderProxy.setBorder(new XToolbarBorder());
        super.setBorder(borderProxy);
        super.setLayout(new ContainerLayout());
        setUseToolBar(true);
        
        new MouseEventSupport(this).install(); 
        if(Beans.isDesignTime()) {
            buttonTpl.setText(getClass().getSimpleName());
        }
    }
    
    public void setLayout(LayoutManager mgr) {;}
    
    public Border getBorder() {
        return (borderProxy == null? null: borderProxy.getBorder());
    }
    
    public void setBorder(Border border) {
        if (border instanceof BorderProxy) {
            //do not accept BorderProxy class
        } else if (borderProxy != null) {
            borderProxy.setBorder(border);
        }
    }
    
    public ComponentSupport getComponentSupport() {
        if (componentSupport == null) {
            componentSupport = new ComponentSupport();
        }
        return componentSupport; 
    }
    
    public void refresh() {
        buildToolbar();
    }
    
    public void load() {
        buildButtons();
    }
    
    public void reload() {
        buildButtons();
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
    
    // <editor-fold defaultstate="collapsed" desc="  helper methods  ">
    
    private void buildButtons() {
        buttons.clear();
        
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
        this.tiles = buildTileViews(model);
        for (TileViewItem item : this.tiles) {
            XButton btn = createButton(item);
            btn.putClientProperty("Action.domain", item.getDomain());
            btn.putClientProperty("Action.role", item.getRole());                
            btn.putClientProperty("Action.permission", item.getPermission());

            if (!buttonTpl.isContentAreaFilled()) {
                btn.setBorder(null);
                btn.setOpaque(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            btn.setContentAreaFilled(buttonTpl.isContentAreaFilled());
            btn.setBorderPainted(buttonTpl.isBorderPainted());
            buttons.add(btn);
        }
        
        //set dirty flag to true
        dirty = true;
    }
    
    private XButton createButton(TileViewItem item) {
        XButton btn = new XButton() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOpenItem(e); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                }
            }
        };        
        ActionButtonSupport.getInstance().loadDefaults(btn, item.getName(), null, btn);
        btn.putClientProperty(TileViewItem.class, item);
        btn.setFocusable(false);
        btn.setMargin(new Insets(2, 2, 2, 2)); 
        if (!isUseToolBar()) btn.setMargin(new Insets(2, 7, 2, 7)); 
        
        //map properties from the button template
        btn.setName(item.getName());
        btn.setFont(buttonTpl.getFont());
        btn.setForeground(buttonTpl.getForeground());
        
        String caption = item.getCaption();
        if ("[no caption]".equalsIgnoreCase(caption+"")) caption = btn.getText();
        if (caption != null && caption.length() == 0) caption = null;
        
        if (!ValueUtil.isEmpty(caption)) {
            if ( isButtonAsHyperlink() ) {
                btn.setContentAreaFilled(false);
                btn.setBorderPainted(false);
                btn.setText("<html><a href='#'>" + caption + "</a></html>");
            } else if ( isButtonTextInHtml() ) {
                if ( (getTextAlignment()+"").toUpperCase().indexOf("CENTER") >= 0 )
                    btn.setText("<html><center>" + caption + "</center></html>");
                else
                    btn.setText("<html>" + caption + "</html>");
            } else {
                btn.setText(caption);
            }
        }
        
        getComponentSupport().alignText(btn, getTextAlignment());
        getComponentSupport().alignTextPosition(btn, getTextPosition());
        btn.setIndex(item.getIndex());
        btn.setUpdate(item.isUpdate());
        btn.setImmediate(item.isImmediate());
        
        String sicon = item.getIcon();
        if (sicon != null && sicon.length() > 0) { 
            ImageIcon icon = ImageIconSupport.getInstance().getIcon(sicon);
            btn.setIcon(icon);
        } 
        if (btn.getIcon() == null && toolbarComponent instanceof JToolBar) {
            ImageIcon icon = ImageIconSupport.getInstance().getIcon("com/rameses/rcp/icons/button-separator.png");
            btn.setIcon(icon); 
        }
                
        btn.putClientProperty("visibleWhen", item.getVisibleWhen());
        btn.setBinding(binding);
        
        Map props = new HashMap();
        Map xmap = item.getProperties();
        if (xmap != null) props.putAll(xmap);

        Object depends = props.get("depends");
        if (depends != null && !(depends instanceof Object[]))
            props.put("depends", new String[]{depends.toString()});
        
        if ( props.get("shortcut") != null ) btn.setAccelerator(props.remove("shortcut")+"");
        if ( props.get("target") != null ) btn.setTarget(props.remove("target")+"");
        if ( props.get("default") != null ) {
            String dfb = props.remove("default")+"";
            if ( dfb.equals("true")) btn.putClientProperty("default.button", true);
        }
        
        //map out other properties
        if ( !props.isEmpty() ) {
            PropertyResolver res = PropertyResolver.getInstance();
            for (Object entry : props.entrySet()) {
                Map.Entry me = (Map.Entry) entry;
                if ("action".equals(me.getKey())) continue;
                if ("type".equals(me.getKey())) continue;
                
                try {
                    res.setProperty( btn, (String) me.getKey(), me.getValue());
                } catch(Throwable e){;}
            }
        }
        
        boolean b = isShowCaptions();
        if (!b && btn.getIcon() == null) b = true;
        
        if (btn.getIcon() == null || (b && caption != null)) {
            String s = btn.getText();
            if (s == null) s = "";
            
            if (!s.trim().startsWith("<html>"))
                btn.setText("<html>"+ s +"</html>");
        } else {
            btn.setText("");
        }
        
        if (item.getTooltip() != null)
            btn.setToolTipText(item.getTooltip());
        else if (caption != null)
            btn.setToolTipText(caption);
        
        btn.setMnemonic(item.getMnemonic());  
        return btn;
    }
    
    private void buildToolbar() {
        if ( dirty ) toolbarComponent.removeAll();
        if (isDynamic()) buildButtons();

        boolean found = false;
        ExpressionResolver expResolver = ExpressionResolver.getInstance();
        for (XButton btn: buttons) {
            String domain = (String) btn.getClientProperty("Action.domain");
            String role = (String) btn.getClientProperty("Action.role");
            String permission = (String) btn.getClientProperty("Action.permission");
            boolean allowed = ControlSupport.isPermitted(domain, role, permission);
            if (!allowed) continue;
            
            String expression = (String) btn.getClientProperty("visibleWhen");
            if (expression != null && expression.trim().length() > 0) { 
                boolean result = false;
                try {
                    UIControlUtil.evaluateExprBoolean(binding.getBean(), expression);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                btn.setVisible(result); 
                
            } else { 
                if ( btn.getClientProperty("default.button") != null ) {
                    if ( getRootPane() != null )
                        getRootPane().setDefaultButton( btn );
                    else 
                        binding.setDefaultButton( btn );
                }
            }
            
            if ( dirty ) toolbarComponent.add(btn);
        }

        revalidate(); 
        repaint(); 
        dirty = false;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public String getTextAlignment() { return this.textAlignment; }
    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }
    
    public String getTextPosition() { return this.textPosition; }
    public void setTextPosition(String textPosition) {
        this.textPosition = textPosition;
    }
    
    public boolean isShowCaptions() { return showCaptions; }
    public void setShowCaptions(boolean showCaptions) { this.showCaptions = showCaptions; }
    
    public Insets getPadding() { return padding; }
    public void setPadding(Insets padding) { this.padding = padding; }
    
    public int getSpacing() { return spacing; }
    public void setSpacing(int spacing) { this.spacing = spacing; }
    
    public List<? extends UIControl> getControls() { return buttons; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public boolean isUseToolBar() { return useToolBar; }
    public void setUseToolBar(boolean useToolBar) {
        this.useToolBar = useToolBar;
        
        super.removeAll();
        if (useToolBar) {
            JToolBar tlb = new JToolBar();
            tlb.setFocusable(false);
            tlb.setFloatable(false);
            tlb.setRollover(true);
            toolbarComponent = tlb;
        } else {
            toolbarComponent = new JPanel();
        }
        
        toolbarComponent.setLayout(new FlowLayout());
        toolbarComponent.setName("toolbar");
        toolbarComponent.setOpaque(false);
        add(toolbarComponent);
        
        if (Beans.isDesignTime())
            toolbarComponent.add(buttonTpl);
    }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public int getHorizontalAlignment() { return 0; }
    public void setHorizontalAlignment(int horizontalAlignment) {}
        
    public boolean focusFirstInput() {
        return false;
    }
    
    //button template support
    private XButton getButtonTemplate() {
        if (buttonTpl == null) buttonTpl = new XButton();
        
        return buttonTpl;
    }
    
    public Font getButtonFont() {
        return getButtonTemplate().getFont();
    }
    public void setButtonFont(Font font) {
        getButtonTemplate().setFont(font);
    }
    
    public boolean getButtonBorderPainted() {
        return getButtonTemplate().isBorderPainted();
    }
    public void setButtonBorderPainted(boolean borderPainted) {
        getButtonTemplate().setBorderPainted(borderPainted);
    }
    
    public boolean getButtonContentAreaFilled() {
        return getButtonTemplate().isContentAreaFilled();
    }
    public void setButtonContentAreaFilled(boolean contentAreaFilled) {
        getButtonTemplate().setContentAreaFilled(contentAreaFilled);
    }
    
    public Dimension getButtonPreferredSize() {
        return getButtonTemplate().getPreferredSize();
    }
    public void setButtonPreferredSize(Dimension preferredSize) {
        getButtonTemplate().setPreferredSize(preferredSize);
    }
    
    public int getButtonCaptionOrientation() { return this.buttonCaptionOrientation; }
    public void setButtonCaptionOrientation(int orientation) {
        this.buttonCaptionOrientation = orientation;
        if( orientation == SwingConstants.TOP || orientation == SwingConstants.BOTTOM ) {
            getButtonTemplate().setVerticalTextPosition(orientation);
            getButtonTemplate().setHorizontalTextPosition(SwingConstants.CENTER);
        } else {
            getButtonTemplate().setVerticalTextPosition(SwingConstants.CENTER);
            getButtonTemplate().setHorizontalTextPosition(orientation);
        }
    }
    
    public boolean isButtonTextInHtml() { return buttonTextInHtml; }
    public void setButtonTextInHtml(boolean buttonTextInHtml) {
        this.buttonTextInHtml = buttonTextInHtml;
    }
    
    public Color getButtonForeground() {
        return getButtonTemplate().getForeground();
    }
    public void setButtonForeground(Color foreground) {
        getButtonTemplate().setForeground(foreground);
    }
    
    public boolean isButtonAsHyperlink() { return buttonAsHyperlink; }
    public void setButtonAsHyperlink(boolean buttonAsHyperlink) {
        this.buttonAsHyperlink = buttonAsHyperlink;
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" ContainerLayout (Class) ">
    
    private class ContainerLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                if (toolbarComponent == null) {
                    //do nothing
                } else if (toolbarComponent.getComponents().length > 0) {
                    Insets margin = parent.getInsets();
                    Dimension dim = toolbarComponent.getPreferredSize();
                    w = (margin.left + dim.width + margin.right);
                    h = (margin.top + dim.height + margin.bottom);
                    
                    Insets pads = getPadding();
                    if (pads != null) {
                        w += (pads.left + pads.right);
                        h += (pads.top + pads.bottom);
                    }
                }
                return new Dimension(w, h);
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                if (toolbarComponent != null) {
                    Insets margin = parent.getInsets();
                    int x = margin.left;
                    int y = margin.top;
                    int w = parent.getWidth() - (margin.left + margin.right);
                    int h = parent.getHeight() - (margin.top + margin.bottom);
                    
                    Insets pads = getPadding();
                    if (pads != null) {
                        x += pads.left;
                        y += pads.top;
                        w -= (pads.left + pads.right);
                        h -= (pads.top + pads.bottom);
                    }
                    
                    toolbarComponent.setBounds(x, y, w, h);
                }
            }
        }
    }
    
    //</editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" FlowLayout (Class) ">

    private class FlowLayout implements LayoutManager 
    {
        XTileView root = XTileView.this;
        private int CELL_SIZE = 100;

        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}

        public Dimension getLayoutSize(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                int w = (CELL_SIZE+20) + padding.left + padding.right;
                int h = (CELL_SIZE-20) + padding.top + padding.bottom;
                Insets margin = parent.getInsets(); 
                return new Dimension(w+margin.left+margin.right, w+margin.top+margin.bottom);
            }
        }

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public void layoutContainer(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                Insets margin = parent.getInsets();                
                int x = margin.left, y=margin.top, pw=parent.getWidth(), ph=parent.getHeight();
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);

                Component[] comps = parent.getComponents();
                if (comps.length == 0) return;

                int cellSize = (root.model == null? 0: root.model.getCellSize());
                if (cellSize <= 0) cellSize = CELL_SIZE;                
                int pcolWidth  = (cellSize >= 100? cellSize+20: cellSize);
                int pcolHeight = (cellSize >= 100? cellSize-20: cellSize);
                
                int colWidth  = pcolWidth + padding.left + padding.right;
                int colHeight = pcolHeight + padding.top + padding.bottom;
                int colCount = (root.model == null? 0: root.model.getCellCount());
                if (colCount <= 0) colCount = w / colWidth; 
                
                for (int i=0; i<comps.length; i++)
                {
                    int colIndex = 0;
                    boolean found = false;
                    for (int c=i; c<comps.length; c++) 
                    {
                        if (colIndex >= colCount) break;

                        i = c;                        
                        if (!comps[c].isVisible()) continue;

                        Component comp = comps[c];
                        Dimension dim = comp.getPreferredSize();
                        if (found) x += getSpacing();

                        comp.setBounds(x, y, colWidth, colHeight); 
                        x += colWidth;
                        colIndex += 1; 
                        found = true;                          
                    }

                    x = margin.left;
                    y += colHeight;
                    if (found) y += getSpacing();                    
                }
            }
        }
    }

    //</editor-fold>            
    
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
    
    private synchronized void onOpenItem(ActionEvent e) {         
        if (model == null) return;
        
        AbstractButton ab = (AbstractButton) e.getSource();
        TileViewItem item = (TileViewItem) ab.getClientProperty(TileViewItem.class);
        if (LOCKS.contains(item)) return;
        
        LOCKS.addElement(item);
        new Thread(new ActionProcess(ab, item)).start();
    }
    
    private class ActionProcess implements Runnable 
    {
        private AbstractButton button;
        private TileViewItem item;
        
        ActionProcess(AbstractButton button, TileViewItem item) {
            this.button = button;
            this.item = item;
        }
        
        public void run() {
            try { 
                if (item == null) return;
                
                Object result = model.onOpenItem(item.getUserObject()); 
                if (!(result instanceof Opener)) return;

                Opener opener = (Opener)result;
                String target = opener.getTarget()+"";
                if (!target.matches("process|_process|window|_window|popup|_popup")) {
                    opener.setTarget("window"); 
                }
                Binding binding = getBinding();
                if (binding != null) binding.fireNavigation(opener); 
                
            } catch(Throwable t) {
                MsgBox.err(t); 
                
            } finally { 
                LOCKS.remove(item); 
            }
        }
    }
    
    // </editor-fold>
}