package com.rameses.rcp.control;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.FormControl;
import com.rameses.rcp.common.FormPanelModel;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertyChangeSupport;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.ValidatorEvent;
import com.rameses.rcp.constant.UIConstants;
import com.rameses.rcp.control.border.XUnderlineBorder;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.BindingListener;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.UIComposite;
import com.rameses.rcp.ui.UIFocusableContainer;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.UILookup;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

/**
 * @author jaycverg
 */
public class XFormPanel extends JPanel implements FormPanelProperty, UIComposite, 
    ControlContainer, Validatable, ActiveControl, UIConstants, MouseEventSupport.ComponentInfo 
{    
    private int cellspacing = 2;
    private Insets cellpadding = new Insets(0,0,0,0);
    private String orientation = UIConstants.VERTICAL;
    private String visibleWhen;
    private Insets padding;
    
    //caption options
    private int captionWidth = 80;
    private String captionVAlignment = UIConstants.TOP;
    private String captionHAlignment = UIConstants.LEFT;
    private String captionOrientation = UIConstants.LEFT;
    private String captionFontStyle;
    private Font captionFont;
    private Color captionForeground;
    private Border captionBorder = new XUnderlineBorder();
    private Insets captionPadding = new Insets(0,1,0,5);
    private boolean addCaptionColon = true;
    private boolean showCategory;
    
    private Binding binding;
    private String[] depends;
    private int index;
    private List<UIControl> controls = new ArrayList();
    private boolean dynamic;
    private ControlProperty property = new ControlProperty();
    private ActionMessage actionMessage = new ActionMessage();
    
    private PropertyChangeSupport propertySupport; 
    private List<UIControl> nonDynamicControls = new ArrayList();
        
    //-- internal flags
    //used to determine dynamically and non-dynamically added controls
    private boolean loaded;
    //used to determine if the dynamically controls were reloaded
    private boolean reloaded;
    //used to determine if non-dynamic controls were removed temporarily
    private boolean dynamicControlsRemoved;
    
    private String viewType;
    private String oldViewType;
    private boolean viewTypeSet;
    
    private LayoutManager layout;
    private JEditorPane htmlPane;
    
    private String emptyWhen;
    private String emptyText;
    private XLabel emptyLbl;
    
    private FormPanelModel model;
    private FormPanelModel.Listener  defaultListener;
    private JLabel lblFont = new JLabel();
        
    private int stretchWidth;
    private int stretchHeight;
    
    public XFormPanel() { 
        initComponent(); 
    } 
    
    private void initComponent() {
        propertySupport = new PropertyChangeSupport();
        super.setLayout(layout = new Layout());
        setPadding(new Insets(0,5,5,5));
        setOpaque(false);
        
        new MouseEventSupport(this).install(); 
                
        Font font = lblFont.getFont().deriveFont(Font.PLAIN); 
        setFont(font); 
        setCaptionFont(font); 
        setCaptionForeground(UIManager.getColor("Label.foreground")); 
        setBorder( getInnerBorder() ); 
    }

    // <editor-fold defaultstate="collapsed" desc=" FormPanel implementations ">
    
    public final void setLayout(LayoutManager mgr) {;}

    public PropertyChangeSupport getPropertySupport() { return propertySupport; } 
    
    private FormItemPanel getLastItem() 
    {
        int index = super.getComponentCount()-1;
        if (index < 0 ) return null; 
        
        Component c = super.getComponent(index);
        if (c instanceof FormItemPanel)
            return (FormItemPanel) c; 
        else 
            return null;
    }
    
    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp instanceof FormItemPanel) {
            super.addImpl(comp, constraints, index); 
            return;
        } 

        ItemPanel p = null;
        Component control = comp;        
        if (comp instanceof ActiveControl) {
            p = new ItemPanel(this, comp);
            
        } else if (comp instanceof JScrollPane) {
            control = ((JScrollPane) comp).getViewport().getView();
            if ( control instanceof ActiveControl ) 
                p = new ItemPanel(this, control, comp);
        }
                
        if (p != null) {
            if ( !loaded && control instanceof UIControl ) { 
                nonDynamicControls.add( (UIControl) control );
            } 
            
            FormItemPanel form = getLastItem(); 
            if (form == null) { 
                super.addImpl(p, constraints, index); 
            } else { 
                form.add(p); 
            } 
        }
    }
    
    public void remove(Component comp) 
    {
        if (comp == null) return;
        
        Container parent = comp.getParent();
        if (parent == null) return;
        
        if (parent == this) 
            super.remove(comp); 
        
        else if (parent instanceof ItemPanel) 
        {
            Container ipc = parent.getParent();
            ipc.remove(parent);             
        } 
    }
    
    private int indexOf(Component comp) {
        for (int i=0; i<getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c == comp) return i;
            
            if (c instanceof ItemPanel) {
                ItemPanel p = (ItemPanel) c;
                if (p.getEditorComponent() == comp) return i;
            }
        }
        return -1;
    }
    
    private Component resolveComponent(Component comp) 
    {
        for (int i=0; i<getComponentCount(); i++) 
        {
            Component c = getComponent(i);
            if (c == comp) return comp;
            if (c instanceof FormItemPanel)
            { 
                Component[] ficomps = ((FormItemPanel) c).getComponents(); 
                for (int ii=0; ii<ficomps.length; ii++) 
                {
                    Component cc = ficomps[ii];  
                    if (cc == comp) return cc;                 
                    if (cc instanceof ItemPanel) 
                    {
                        ItemPanel ipc = (ItemPanel) cc;
                        if (ipc.getEditorComponent() == cc) return ipc;
                        if (ipc.getEditorWrapper() == cc) return ipc;
                    }
                }
            } 
        }
        return null;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" control support properties ">
    
    public boolean isShowCategory() { return showCategory; }
    public void setShowCategory(boolean showCategory) {
        this.showCategory = showCategory; 
        propertySupport.firePropertyChange("showCategory", showCategory); 
    }
    
    public List<? extends UIControl> getControls() { return controls; }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public boolean isDynamic() { return dynamic; }
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void validateInput() {
        validateInput( actionMessage ); 
    }
    public void validateInput( ActionMessage am ) {
        am.clearMessages();
        
        //do not validate if in html view
        if ( ValueUtil.isEqual(viewType, HTML_VIEW) ) return;
        
        for (UIControl c: controls) {
            if ( !(c instanceof Validatable) ) continue;
            
            Validatable v = (Validatable) c;
            UIControlUtil.validate(v, am);
        }
    }
    
    public ActionMessage getActionMessage() { return actionMessage; }
    
    public void requestFocus() {
        focusFirstInput();
    }
    
    public boolean focusFirstInput() 
    {
        List<UIControl> allControls = new ArrayList();
        if ( !nonDynamicControls.isEmpty() ) { 
            allControls.addAll( nonDynamicControls );
        } 
        allControls.addAll(controls);
        
        try {
            for (UIControl c: allControls) {
                if ( actionMessage.hasMessages() ) {
                    if( !(c instanceof Validatable) ) continue;
                    
                    Validatable v = (Validatable) c;
                    v.validateInput();
                    if ( v.getActionMessage().hasMessages() ) {
                        ((Component) v).requestFocus();
                        return true;
                    }
                }  
                else if ( c instanceof UIFocusableContainer ) {
                    UIFocusableContainer uis = (UIFocusableContainer) c;
                    if ( uis.focusFirstInput() ) return true;
                } 
                else if ( c instanceof UIInput ) {
                    UIInput u = (UIInput) c;
                    JComponent jc = (JComponent) c;
                    if ( jc.isEnabled() && jc.isFocusable() ) { 
                        jc.requestFocus();
                        jc.requestFocusInWindow();
                        return true; 
                    }
                }
            }
        } catch(Throwable e) {
            //do nothing 
        } finally {
            allControls = null;
        }
        return false;
    }
    
    public boolean isHasNonDynamicContents() {
        return !nonDynamicControls.isEmpty();
    }
    
    public UIControl find(String name) {
        for(UIControl uic : controls) {
            if( ValueUtil.isEqual(name, uic.getName()) )
                return uic;
        }
        return null;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" refresh/load ">
    
    public void refresh() { 
        if ( isDynamic()) { 
            build(); 
        } 
        
        if ( reloaded || (viewTypeSet && !ValueUtil.isEqual(oldViewType, viewType))) {
            refreshForm();
            oldViewType = viewType;
            reloaded = false;
        } else if ( ValueUtil.isEqual(viewType, HTML_VIEW)) {
            refreshHtml();
        }
        
        try { 
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                Object bean = getBinding().getBean();
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(bean, visibleWhen);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                setVisible(b); 
            } 
        } catch(Throwable t) {;}         
        
        revalidate();
        repaint();
    }
    
    public void load() {
        binding.addBindingListener(new FormPanelBindingListener()); 
        if ( !isDynamic()) {
            build(); 
        }
        loaded = true;
        reloaded = true;
    }
    
    public void reload() {      
        build();
        reloaded = true;
    }
    
    public Map getInfo() { 
        return null; 
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper method ">
    
    private Component getClosestFormItemPanel(JComponent jc) {
        if (jc == null) return null;
        
        Container con = jc.getParent();
        while (true) {
            if (con == null) break;
            if (con instanceof FormItemPanel) return con; 

            con = con.getParent(); 
        }
        return null; 
    }
    
    private void build() {
        if ( ValueUtil.isEmpty(getName()) ) return;
        
        //remove only dynamic controls
        for (UIControl u: controls) { 
            JComponent jc = (JComponent)u;
            Component c = getClosestFormItemPanel(jc);
            if (c == null) { 
                remove(jc); 
            } else {
                remove(c); 
            } 
            u = null;
        }
        
        controls.clear();
        property.setRequired(false);
        boolean htmlView = HTML_VIEW.equals(viewType);
        
        List<FormControl> list = getFormControls();
        FormControlUtil fcUtil = FormControlUtil.getInstance(); 
        EditorInputSupport inputSupport = new EditorInputSupport();
        
        for (FormControl fc: list) {
            UIControl uic = fcUtil.getControl(fc);
            if (uic == null) continue;
            
            if (fc instanceof FormControlMap) {
                FormControlMap fcm = (FormControlMap) fc; 
                uic.putClientProperty("UIControl.userObject", fcm.getData()); 
            } 
            
            uic.putClientProperty(UIInputUtil.Support.class, inputSupport); 
            uic.setBinding(binding);
            uic.load();
            
            if ( uic instanceof Validatable && ((Validatable) uic).isRequired() )
                property.setRequired(true);            
            if (uic instanceof JComponent)
                ((JComponent) uic).putClientProperty(FormControl.class, fc); 
            
            controls.add( uic );
        }
    }
    
    private void refreshForm() 
    {
        //check if view is html
        boolean htmlView = HTML_VIEW.equals(viewType);
        
        if ( !htmlView && htmlPane != null ) 
        {
            remove( htmlPane );
            htmlPane = null;
        }
        
        if ( htmlView ) 
        {
            //remove controls
            for (UIControl u : nonDynamicControls) 
            {
                //u.refresh();
                remove((Component)u);
            }
            dynamicControlsRemoved = true;
            
            for (UIControl u: controls) 
            {
                //u.refresh();
                remove((Component) u);
            }
            
            if ( htmlPane == null ) initHtmlPane();
        } 
        else {
            super.setLayout(layout);
        }
        
        boolean empty = false;
        
        //visibility and empty text support
        if ( controls.size() == 0 && nonDynamicControls.size() == 0 && !ValueUtil.isEmpty(emptyText) ) {
            empty = true;
        } else {
            if ( !ValueUtil.isEmpty(emptyWhen) ) {
                try { 
                    empty = UIControlUtil.evaluateExprBoolean(binding.getBean(), emptyWhen); 
                } catch(Throwable t){
                    t.printStackTrace();
                } 
            }
        }
        
        if ( !empty ) {
            if ( emptyLbl != null ) {
                remove(emptyLbl);
                emptyLbl = null;
            }
            
            if ( htmlView ) {
                FormControlUtil fcUtil = FormControlUtil.getInstance();
                htmlPane.setText( fcUtil.renderHtml(getAllControls(), this) );
                
            } else { 
                super.setLayout(layout); 
                //attach again the nonDynamicControls
                //if they were removed temporarily
                if ( dynamicControlsRemoved ) {
                    for (UIControl u : nonDynamicControls) {
                        add((Component)u);
                        u.refresh();
                    }
                    dynamicControlsRemoved = false;
                }
                
                FormItemPanel formItemPanel = null;
                Map<String,String> categories = new WeakHashMap(); 
                for (UIControl u : controls) {
                    //add component if form panel is reloaded
                    //this happends if the form panel is dynamic
                    if ( reloaded && u instanceof JComponent ) {
                        JComponent jc = (JComponent) u; 
                        if (model != null && isShowCategory()) { 
                            FormControl fc = (FormControl) jc.getClientProperty(FormControl.class); 
                            String newCategoryid = (fc == null? null: fc.getCategoryid()); 
                            String oldCategoryid = (formItemPanel == null? null: formItemPanel.getId()); 
                            if (formItemPanel == null || !(newCategoryid+"").equals(oldCategoryid+"")) { 
                                formItemPanel = new FormItemPanel(newCategoryid); 
                                formItemPanel.setFormPanelProperty(this); 
                                add(formItemPanel); 
                            }                                     

                            String fiCaption = formItemPanel.getCaption();
                            if (fiCaption == null || fiCaption.length() == 0) { 
                                String s = model.getCategory(newCategoryid); 
                                if (s != null) formItemPanel.setCaption(s); 

                                //if (ov != null) ov = model.getCategory(ov.toString()); 
                                //if (ov != null) formItemPanel.setCaption(ov.toString()); 
                            }                            
                        }
                        add(jc); 
                    } 
                    u.refresh(); 
                }
                categories.clear();
            }
        } else {
            if ( htmlView ) {
                Font f = getFont();
                
                StringBuffer sb = new StringBuffer()
                .append("<html>")
                .append("<head>")
                .append("<style> body, td, div, span { ")
                .append("  font-family: \"" + f.getFamily() + "\"; ")
                .append("  font-size: " + f.getSize())
                .append("}</style>")
                .append("</head>")
                .append("<body>")
                .append( emptyText==null? "" : emptyText )
                .append("</body>")
                .append("</html>");
                
                htmlPane.setText( sb.toString() );
                htmlPane.setCaretPosition(0);
            } else {
                if ( emptyText != null ) {
                    if ( emptyLbl == null ) {
                        emptyLbl = new XLabel();
                        emptyLbl.setShowCaption(false);
                    }
                    emptyLbl.setExpression(emptyText);
                    add( emptyLbl );
                }
            }
        }
        
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private void refreshHtml() 
    {
        boolean empty = false;
        
        //visibility and empty text support
        if ( controls.size() == 0 && nonDynamicControls.size() == 0 && !ValueUtil.isEmpty(emptyText) ) {
            empty = true;
        } 
        else 
        {
            if ( !ValueUtil.isEmpty(emptyWhen) ) 
            {
                Object result = UIControlUtil.evaluateExpr(binding.getBean(), emptyWhen);
                empty = !"false".equals(result+"");
            }
        }
        
        if ( !empty ) 
        {
            List<UIControl> allControls = getAllControls();
            for(UIControl c : allControls) c.refresh();
            
            FormControlUtil fcUtil = FormControlUtil.getInstance();
            htmlPane.setText( fcUtil.renderHtml(allControls, this) );
            htmlPane.setCaretPosition(0);
        } 
        else 
        {
            Font f = getFont();
            String html = "<font face='"+f.getFamily()+"' size='"+f.getSize()+"pt'>"+(emptyText==null?"":emptyText)+"</font>";
            htmlPane.setText(html);
        }
    }
    
    private void initHtmlPane() 
    {
        XEditorPane editorPane = new XEditorPane();
        editorPane.setBinding(binding);
        super.setLayout(new BorderLayout());
        super.addImpl( editorPane, null, 0 );
        htmlPane = editorPane;
    }
    
    private List getFormControls() 
    {
        Object value = null;
        try {
            value = UIControlUtil.getBeanValue(this);
        } catch(Throwable t) {;}

        FormPanelModel formModel = null;
        if (value instanceof FormPanelModel) { 
            formModel = (FormPanelModel) value;
        } else {
            DefaultFormPanelModel dpm = new DefaultFormPanelModel();  
            dpm.setValue(value); 
            formModel = dpm;
        }
        
        if (defaultListener == null) 
            defaultListener = new ModelListener(); 
        
        value = formModel.getFormControls();
        if (value == null) value = formModel.getControlList();
        
        List list = new ArrayList();    
        if (value == null) {
            //do nothing
        } else if (value.getClass().isArray()) {
            for (Object o: (Object[]) value) {
                if (o != null) list.add(toFormControl(o));
            }
        } else if (value instanceof Collection) {
            for (Object o: (Collection) value) {
                if (o != null) list.add(toFormControl(o));
            }
        } 
        
        FormPanelModel oldModel = this.model;
        if (oldModel != null) {
            oldModel.setListener(null);
            oldModel.setProvider(null);
        } 

        formModel.setListener(defaultListener);
        formModel.setProvider(new ModelProviderSupport()); 
        this.model = formModel; 
        return list;
    }
    
    private FormControl toFormControl(Object value) {
        if (value instanceof FormControl) 
            return (FormControl) value;
        else if (value instanceof Map) 
            return new FormControlMap((Map) value);

        throw new IllegalStateException("The form controls must be an instance of FormControl or Map"); 
    }
    
    private String lookupAlignment( String alignment, String defaultvalue, boolean vertical ) {
        if ( vertical ) {
            String str = (alignment==null? defaultvalue: alignment).toLowerCase(); 
            if (str.equals("top")) {
                return UIConstants.TOP; 
            } else if (str.equals("bottom")) {
                return UIConstants.BOTTOM; 
            } else {
                return UIConstants.CENTER; 
            }
        } else {
            String str = (alignment==null? defaultvalue: alignment).toLowerCase(); 
            if (str.equals("right")) {
                return UIConstants.RIGHT; 
            } else if (str.equals("center")) {
                return UIConstants.CENTER; 
            } else {
                return UIConstants.LEFT; 
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public ControlProperty getControlProperty() {
        return property;
    }
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { property.setCaption(caption); }
    
    public char getCaptionMnemonic() { return property.getCaptionMnemonic(); }
    public void setCaptionMnemonic(char c) { property.setCaptionMnemonic(c); }
    
    public boolean isShowCaption() { return property.isShowCaption(); }
    public void setShowCaption(boolean show) { property.setShowCaption(show); }
    
    public int getCellspacing() { return cellspacing; }
    public void setCellspacing(int cellspacing) { 
        this.cellspacing = cellspacing; 
        propertySupport.firePropertyChange("cellSpacing", cellspacing); 
    }
        
    public Insets getPadding() { return padding; }
    public void setPadding(Insets padding) {
        this.padding = padding;
    }
    
    public int getCaptionWidth() { return captionWidth; }
    public void setCaptionWidth(int captionWidth) { 
        this.captionWidth = captionWidth; 
        updateLabelComponent();
    }
    
    public String getCaptionVAlignment() { return captionVAlignment; }
    public void setCaptionVAlignment(String captionVAlignment) {
        this.captionVAlignment = lookupAlignment(captionVAlignment, UIConstants.TOP, true); 
        updateLabelComponent();
    }
    
    public String getCaptionHAlignment() { return captionHAlignment; }
    public void setCaptionHAlignment(String captionHAlignment) { 
        this.captionHAlignment = lookupAlignment(captionHAlignment, UIConstants.LEFT, false); 
        updateLabelComponent();
    } 
    
    public String getCaptionOrientation() { return captionOrientation; }
    public void setCaptionOrientation(String captionOrientation) {
        if ( captionOrientation == null ) {
            this.captionOrientation = UIConstants.LEFT; 
        } else {
            this.captionOrientation = captionOrientation.toUpperCase(); 
        }   
        updateLabelComponent();
    }
    
    public String getOrientation() { return orientation; }
    public void setOrientation(String orientation) {
        if ( orientation == null ) {
            this.orientation = UIConstants.VERTICAL;
        } else { 
            this.orientation = orientation.toUpperCase(); 
        } 
    }
    
    public Insets getCaptionPadding() { return captionPadding; }
    public void setCaptionPadding(Insets captionPadding) { 
        this.captionPadding = captionPadding; 
        updateLabelComponent();
    }
    
    public Insets getCellpadding() { return cellpadding; }
    public void setCellpadding(Insets cellpadding) 
    {
        this.cellpadding = (cellpadding == null? new Insets(0,0,0,0): cellpadding);
        propertySupport.firePropertyChange("cellPadding", this.cellpadding);         
    }
    
    public boolean isAddCaptionColon() { return addCaptionColon; }
    public void setAddCaptionColon(boolean addCaptionColon) {
        this.addCaptionColon = addCaptionColon;
        updateLabelComponent();
    }
    
    private FontSupport fontSupport; 
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }     
    
    public Font getCaptionFont() { return captionFont; }
    public void setCaptionFont(Font captionFont) {
        this.captionFont = captionFont;
        if (captionFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getCaptionFontStyle()); 
            captionFont = getFontSupport().applyStyles(captionFont, attrs);
        } else {
            updateLabelsFont(captionFont);
        }
    } 
    
    public String getCaptionFontStyle() { return captionFontStyle; } 
    public void setCaptionFontStyle(String captionFontStyle) {
        this.captionFontStyle = captionFontStyle; 
        
        Font font = getCaptionFont(); 
        if (font == null) font = super.getFont(); 
        
        Map attrs = getFontSupport().createFontAttributes(getCaptionFontStyle()); 
        font = getFontSupport().applyStyles(font, attrs); 
        
        updateLabelsFont(font); 
    } 
    
    private void updateLabelComponent() {
        for(Component c: getComponents()) {
            if ( c instanceof ItemPanel ) {
                ItemPanel ip = (ItemPanel)c; 
                ip.updateLabelComponent(); 
                ip.revalidate(); 
            } 
        } 
    } 
        
    private void updateLabelsFont(Object fontObj) {
        if (fontObj == null) return;
        
        FontSupport fontSupport = new FontSupport(); 
        for (Component c: getComponents()) {
            if (c instanceof ItemPanel) { 
                ItemPanel ip = (ItemPanel)c; 
                if (fontObj instanceof Font) { 
                    ip.updateLabelFont((Font) fontObj);
                } else if (fontObj instanceof String) {
                    ip.updateLabelFont(fontSupport, (String) fontObj); 
                } 
                ip.updateLabelComponent(); 
                ip.revalidate(); 
            }
        }
    }
    
    public Color getCaptionForeground() { return captionForeground; }
    public void setCaptionForeground(Color captionForeground) {
        this.captionForeground = captionForeground;
        updateLabelComponent(); 
    }
    
    public Border getCaptionBorder() { return captionBorder; }
    public void setCaptionBorder(Border captionBorder) {
        this.captionBorder = captionBorder;
        updateLabelComponent();
    }
    
    public void setRequired(boolean required) {}
    public boolean isRequired() {
        return property.isRequired();
    }
    
    public String getViewType() { return viewType; }
    public void setViewType(String viewType) {
        if( !viewTypeSet )
            oldViewType = viewType;
        else
            oldViewType = this.viewType;
        
        this.viewType = viewType;
        viewTypeSet = true;
    }
    
    public boolean isChildrenAcceptStyles() {
        return false;
    }
    
    public String getEmptyText()               { return emptyText; }
    public void setEmptyText(String emptyText) { this.emptyText = emptyText; }
    
    public List<UIControl> getAllControls() {
        List<UIControl> allControls = new ArrayList();
        allControls.addAll(nonDynamicControls);
        allControls.addAll(controls);
        return allControls;
    }
    
    public String getEmptyWhen() {
        return emptyWhen;
    }
    
    public void setEmptyWhen(String emptyWhen) {
        this.emptyWhen = emptyWhen;
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }
    
    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Layout ">
    
    private class Layout implements LayoutManager 
    {        
        private Logger logger = Logger.getLogger(getClass().getName()); 
        
        public void addLayoutComponent(String name, Component comp) {;}
        public void removeLayoutComponent(Component comp) {;}
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                if (UIConstants.HORIZONTAL.equals(orientation)) {
                    layoutContainerHorizontal( parent ); 
                } else {
                    layoutContainerVertical( parent ); 
                }
            }
        }
        
        private int relayoutBounds(Container parent, Component[] comps) {
            Insets margin = parent.getInsets(); 
            int x = margin.left;
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                Rectangle rect = c.getBounds();
                int spacing = (i > 0? getCellspacing(): 0);
                
                x += spacing + cellpadding.left; 
                c.setBounds(x, rect.y, rect.width, rect.height); 
                x += rect.width + cellpadding.right; 
            } 
            
            int pw = parent.getWidth() - margin.right;
            return Math.max(pw - x, 0); 
        }
        
        private void layoutContainerHorizontal(Container parent) { 
            Insets margin = parent.getInsets();
            int x = margin.left;
            int y = margin.top;
            
            List<Component> list = new ArrayList();
            Component[] comps = getVisibleComponents(parent);
            for (int i=0; i<comps.length; i++) { 
                Component c = comps[i]; 
                Dimension dim = c.getPreferredSize(); 
                int spacing = (i > 0? getCellspacing(): 0); 

                FormItemProperty fip = (FormItemProperty)c; 
                int sw = fip.getStretchWidth(); 
                if (sw > 0) list.add( c ); 
                
                x += spacing + cellpadding.left; 
                c.setBounds(x, y, dim.width, dim.height); 
                x += dim.width + cellpadding.right; 
            } 
            
            if ( list.isEmpty() ) { return; } 

            int w = parent.getWidth() - (margin.left + margin.right);
            int extrawidth = Math.max(w - x, 0);
            if (extrawidth == 0) { return; }
            
            int[] widths = new int[list.size()]; 
            for (int i=0; i<list.size(); i++) {
                Component c = list.get(i); 
                FormItemProperty fip = (FormItemProperty)c; 
                int sw = fip.getStretchWidth(); 
                double d0 = (double) extrawidth; 
                double d1 = sw / 100.0; 
                double d2 = d0 * d1; 
                int xw = new BigDecimal(d2).setScale(0, RoundingMode.HALF_UP).intValue(); 
                widths[i] = xw; 
            }
            while (sum(widths) > extrawidth) {
                for (int i=0; i<widths.length; i++) { 
                    if (sum(widths)-1 < extrawidth) { break; } 
                    
                    widths[i] -= 1; 
                } 
            } 
            for (int i=0; i<widths.length; i++) {
                Component c = list.get(i); 
                Rectangle rect = c.getBounds();
                Dimension dim = c.getPreferredSize(); 
                c.setBounds(rect.x, rect.y, dim.width + widths[i], rect.height); 
            }
            relayoutBounds(parent, comps); 
            if (comps.length > 0) {
                Component c = comps[comps.length-1];
                FormItemProperty fip = (FormItemProperty)c; 
                if (fip.getStretchWidth() > 0) {
                    Rectangle rect = c.getBounds();
                    int px = parent.getWidth() - margin.right - cellpadding.right;                     
                    int cx = rect.x + rect.width; 
                    if (cx < px) {
                        int w0 = Math.max(px-cx, 0);
                        c.setBounds(rect.x, rect.y, rect.width+w0, rect.height);
                    }
                }
            }
        } 
        
        private int sum(int[] values) {
            int result = 0;
            for (int i=0; i<values.length; i++) {
                result += values[i]; 
            }
            return result; 
        }
        
        private void layoutContainerVertical(Container parent) { 
            Insets margin = parent.getInsets();
            int x = margin.left;
            int y = margin.top;
            int w = parent.getWidth() - (margin.left + margin.right);
            int h = parent.getHeight() - (margin.top + margin.bottom);

            int preferredMaxWidth = 0;
            Component[] comps = getVisibleComponents(parent);
            for (int i=0; i<comps.length; i++) {
                Dimension dim = comps[i].getPreferredSize(); 
                preferredMaxWidth = Math.max(preferredMaxWidth, dim.width); 
            }
            
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i]; 
                Dimension dim = c.getPreferredSize(); 
                FormItemProperty fip = (FormItemProperty)c; 

                if (i > 0) {
                    y += getCellspacing();
                    if (isShowCategory()) { y += 10; } 
                } 

                int dw = dim.width; 
                int sw = fip.getStretchWidth(); 
                if ( isShowCategory() && sw==0 ) {
                    sw = 100; 
                }
                
                if (sw > 0) { 
                    double d0 = (double) w; 
                    if (w < preferredMaxWidth) { 
                        d0 = (double) preferredMaxWidth; 
                    } 
                    double d1 = sw / 100.0; 
                    double d2 = d0 * d1; 
                    dw = new BigDecimal(d2).setScale(0, RoundingMode.HALF_UP).intValue(); 
                    if (dw < dim.width) { dw = dim.width; } 
                } 
                
                y += cellpadding.top;
                c.setBounds(x, y, dw, dim.height);
                y += dim.height + cellpadding.bottom;
            }
        }
        
        private Component[] getVisibleComponents(Container parent) {
            List<Component> results = new ArrayList();
            Component[] comps = parent.getComponents();
            for (int i=0; i<comps.length; i++) {
                if (!comps[i].isVisible()) { continue; } 
                if (comps[i] instanceof FormItemProperty) {
                    results.add( comps[i] ); 
                } else {
                    comps[i].setBounds(0, 0, 0, 0);
                }
            }
            return results.toArray(new Component[]{}); 
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension dim = new Dimension(0,0);
                Component[] comps = parent.getComponents();
                if (UIConstants.HORIZONTAL.equals(orientation)) {
                    dim = getHorizontalLayoutSize(comps);
                } else {
                    dim = getVerticalLayoutSize(comps);
                }
                
                Insets margin = parent.getInsets();
                int w = (dim.width + margin.left + margin.right);
                int h = (dim.height + margin.top + margin.bottom);
                return new Dimension(w, h); 
            }
        }
        
        private Dimension getHorizontalLayoutSize(Component[] comps) {
            int w=0, h=0;
            boolean hasVisibleComponents = false;
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i];
                if (!c.isVisible()) { continue; } 
                if (!(c instanceof FormItemProperty)) { continue; } 

                if (hasVisibleComponents) {
                    w += getCellspacing();
                } 
                
                Dimension dim = c.getPreferredSize();
                h = Math.max(h, dim.height + cellpadding.top + cellpadding.bottom);
                w += dim.width + cellpadding.left + cellpadding.right;
                hasVisibleComponents = true;
            }
            return new Dimension(w, h); 
        } 
        
        private Dimension getVerticalLayoutSize(Component[] comps) {
            int w=0, h=0;
            boolean hasVisibleComponents = false;
            for (int i=0; i<comps.length; i++) {
                Component c = comps[i]; 
                if (!c.isVisible()) { continue; } 
                if (!(c instanceof FormItemProperty)) { continue; } 

                if (hasVisibleComponents) {
                    h += getCellspacing();
                    if (isShowCategory()) { 
                        h += 10;
                    }
                } 

                Dimension dim = c.getPreferredSize();
                w = Math.max(w, dim.width + cellpadding.left + cellpadding.right);
                h += dim.height + cellpadding.top + cellpadding.bottom;
                hasVisibleComponents = true;
            }
            return new Dimension(w, h); 
        }        
        
        public void addLayoutComponent(Component comp, Object constraints) {
        }

        public Dimension maximumLayoutSize(Container parent) {
            return getLayoutSize(parent); 
        }

        public float getLayoutAlignmentX(Container parent) {
            return 0.0f;
        } 

        public float getLayoutAlignmentY(Container parent) {
            return 0.0f;
        }

        public void invalidateLayout(Container parent) {
            layoutContainer(parent); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" FormPanelBindingListener (class) ">
    
    private class FormPanelBindingListener implements BindingListener 
    {        
        public void notifyDepends(UIControl u, Binding parent) {
            notifyDepends(u, parent, u.getName()); 
        }
        
        public void notifyDepends(UIControl u, Binding parent, String name) 
        {
            if ( ValueUtil.isEmpty(name) ) return;
            
            //if view type is HTML_VIEW do not refresh the control
            //the html renderer also refresh the items before rendering
            if ( ValueUtil.isEqual(viewType, HTML_VIEW)) 
            {
                boolean shouldRefresh = false;
                for ( UIControl control : controls ) 
                {
                    if ( !isDependent( name, control ) ) continue;
                    
                    shouldRefresh = true;
                }
                
                if ( shouldRefresh ) refreshHtml();
            } 
            else 
            {
                Set<UIControl> refreshed = new HashSet();
                for ( UIControl control : controls ) 
                {
                    if ( !isDependent( name, control ) ) continue;
                    
                    _doRefresh( control, refreshed );
                }
                refreshed.clear();
                refreshed = null;
            }
        }
        
        private boolean isDependent( String parentName, UIControl child ) 
        {
            if ( child.getDepends() != null ) {
                for(String s : child.getDepends()) {
                    if ( parentName.matches(s) ) return true;
                }
            }
            return false;
        }
        
        public void refresh(String regEx) 
        {
            //if view type is HTML_VIEW do not refresh
            //the html renderer also refresh the items before rendering
            if ( ValueUtil.isEqual(viewType, HTML_VIEW)) return;
            
            Set<UIControl> refreshed = new HashSet();
            for ( UIControl uu : controls ) 
            {
                String name = uu.getName();
                if ( regEx != null && name != null && !name.matches(regEx) ) continue;
                
                _doRefresh( uu, refreshed );
            }
            refreshed.clear();
            refreshed = null;
        }
        
        private void _doRefresh( UIControl u, Set refreshed ) 
        {
            if ( refreshed.add(u) ) u.refresh();
        }
        
        public void validate(ActionMessage actionMessage, Binding parent) {}
        public void validateBean(ValidatorEvent evt) {}
        public void formCommit() {}
        public void update() {}        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ModelListener (class) ">
    
    private class ModelListener implements FormPanelModel.Listener 
    {        
        public void onPropertyUpdated(String name, Object value) 
        {
            XFormPanel handle = XFormPanel.this;
            PropertyResolver res = PropertyResolver.getInstance();
            try {
                res.setProperty(handle, name, value);
            } catch(Exception e){;}
        }
        
        public String getHtmlFormat(boolean partial) 
        {
            FormControlUtil fcUtil = FormControlUtil.getInstance();
            return fcUtil.renderHtml(getAllControls(), XFormPanel.this, partial);
        }
        
        public void onReload() 
        {
            reload();
            refresh();
        }        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" DefaultFormPanelModel (class) ">
    
    private class DefaultFormPanelModel extends FormPanelModel {
        
        XFormPanel root = XFormPanel.this;
        private List<FormControl> controls = new ArrayList();
                       
        void setValue(Object value) { 
            controls.clear(); 
            if (value == null) {
                //do nothing
            } else if (value.getClass().isArray()) {
                for (Object o: (Object[]) value) {
                    if (o != null) controls.add(root.toFormControl(o));
                }
            } else if (value instanceof Collection) {
                for (Object o: (Collection) value) {
                    if (o != null) controls.add(root.toFormControl(o));
                }
            }            
        }

        public Object getFormControls() { return controls; }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" FormControlMap (class) ">    

    private class FormControlMap extends FormControl 
    {
        private Map data;

        FormControlMap(Map data) {
            this.data = data; 

            Map props = new HashMap();
            if (data != null) props.putAll(data); 

            String type = (String) data.get("type");
            String categoryid = (String) data.get("categoryid");
            init(type, props, categoryid);      
        }

        Map getData() { return data; } 
    } 

    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" EditorInputSupport (class) ">
    
    private class EditorInputSupport implements UIInputUtil.Support 
    {       
        XFormPanel root = XFormPanel.this;
        PropertyResolver propertyResolver = PropertyResolver.getInstance();
        
        public Object setValue(String name, Object value) {
            return setValue(name, value, null); 
        } 
        
        public Object setValue(String name, Object value, JComponent jcomp) {
            if (root.model == null) {
                System.out.println("[WARN] No available FormPanelModel attached"); 
                return null; 
            }
            
            boolean forceUpdate = (jcomp==null? false: jcomp.getClientProperty("UIControl.forceUpdate")==Boolean.TRUE); 
            boolean noBeanUpdate = (jcomp==null? false: jcomp.getClientProperty("UIControl.noBeanUpdate")==Boolean.TRUE); 
            
            UIControl uic = null;
            if ( forceUpdate && (jcomp instanceof UIControl)) {
                uic = (UIControl)jcomp;
            } else if (jcomp instanceof UIInput) { 
                uic = (UIControl)jcomp;
            } else if (jcomp instanceof UILookup) { 
                uic = (UIControl)jcomp; 
            } else { 
                System.out.println("[WARN] EditorInputSupport does not support this type of component"); 
                return null;
            }
            
            Binding binding = uic.getBinding();
            Object userObj = uic.getClientProperty("UIControl.userObject"); 
            if ( !noBeanUpdate ) { 
                try { propertyResolver.setProperty(userObj, "name", name); } catch(Throwable t){;} 
                try { propertyResolver.setProperty(userObj, "value", value); } catch(Throwable t){;} 
            }

            boolean has_value_changed = false; 
            try { 
                Object bean = (binding == null ? null : binding.getBean());
                Object oldvalue = (bean == null ? null: propertyResolver.getProperty(bean, name));
                if ( ValueUtil.isEqual(oldvalue, value)) {
                    //do nothing
                } else { 
                    has_value_changed = true; 
                    root.model.updateBean(name, value, userObj); 
                } 
            } catch(Throwable t){;} 
            
            //fire onchange handle on the item if available 
            try { 
                if ( has_value_changed ) {
                    fireOnChangeEvent(userObj, value); 
                }
            } catch(Throwable t) { 
                MsgBox.err(t); 
            } 
            
            if ( has_value_changed ) {
                binding.getValueChangeSupport().notify(name, value);  
            }
            
            if (jcomp instanceof JTextComponent) {
                JTextComponent jtxt = (JTextComponent) jcomp;
                int oldCaretPos = jtxt.getCaretPosition(); 

                try { 
                    uic.refresh(); 
                } catch(RuntimeException re) {
                    throw re; 
                } catch(Exception e) {
                    throw new RuntimeException(e.getMessage(), e); 
                } finally {
                    try {
                        jtxt.setCaretPosition(oldCaretPos); 
                    } catch(Exception ign) {;} 
                }

                jtxt.putClientProperty("CaretPosition", oldCaretPos); 
            }

            binding.notifyDepends(uic);  
            return null; 
        } 
        
        private void fireOnChangeEvent(Object userObj, Object value) {
            Object onchangeHandler = null; 
            try { 
                onchangeHandler = propertyResolver.getProperty(userObj, "onchange"); 
            } catch(Throwable t){;} 
            
            if (onchangeHandler == null) return;
            
            new CallbackHandlerProxy(onchangeHandler).call(value); 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ModelProviderSupport (class) ">
    
    private class ModelProviderSupport implements FormPanelModel.Provider 
    {
        XFormPanel root = XFormPanel.this; 
        
        public Object getBinding() {
            return root.getBinding(); 
        }

        public Object getBindingBean() {
            Binding binding = root.getBinding(); 
            return (binding == null? null: binding.getBean()); 
        }        
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InnerBorder ">
    
    private InnerBorder innerBorder; 
    private InnerBorder getInnerBorder() {
        if ( innerBorder == null ) {
            innerBorder = new InnerBorder(); 
        }
        return innerBorder; 
    }
    
    private class InnerBorder extends AbstractBorder {
        
        XFormPanel root = XFormPanel.this; 
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        }
        
        public Insets getBorderInsets( Component c )       {
            return getBorderInsets();
        }
        public Insets getBorderInsets(Component c, Insets ins) { 
            Insets pad = getBorderInsets(); 
            if ( ins == null ) {
                ins = new Insets(0,0,0,0); 
            }
            ins.top = pad.top;
            ins.left = pad.left;
            ins.bottom = pad.bottom;
            ins.right = pad.right;
            return ins;
        }
        
        public boolean isBorderOpaque() { return false; }
        
        public Insets getBorderInsets() { 
            Insets pad = root.getPadding(); 
            if ( pad == null ) {
                pad = new Insets(0,0,0,0); 
            } 
            return new Insets( pad.top, pad.left, pad.bottom, pad.right );
        }
        
    }
    
    // </editor-fold>
    
}
