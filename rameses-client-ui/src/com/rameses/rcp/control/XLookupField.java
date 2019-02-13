package com.rameses.rcp.control;

import com.rameses.common.PropertyResolver;
import com.rameses.platform.interfaces.Platform;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.rcp.common.LookupDataSource;
import com.rameses.rcp.common.LookupHandler;
import com.rameses.rcp.common.LookupModel;
import com.rameses.rcp.common.LookupOpenerSupport;
import com.rameses.rcp.common.LookupSelector;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.SimpleLookupDataSource;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.IconedTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UILookup;
import com.rameses.rcp.ui.UISelector;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author wflores
 */
public class XLookupField extends IconedTextField implements UILookup, UISelector, 
    Validatable, ActiveControl, ActionListener, LookupSelector, MouseEventSupport.ComponentInfo   
{
    protected ControlProperty property = new ControlProperty();
    protected ActionMessage actionMessage = new ActionMessage();

    private LookupHandlerProxy lookupHandlerProxy = new LookupHandlerProxy();
    private LookupInputSupport inputSupport = new LookupInputSupport();
    private TrimSpaceOption trimSpaceOption = TrimSpaceOption.ALL;
    private TextDocument document = new TextDocument();
        
    private Binding binding;
    private String[] depends;
    private int index;
    
    private String varName = "item";
    private String handler;    
    private Object handlerObject;    
    private Object selectedValue;
    private String expression;
    private String returnFields;
    private String disableWhen;
    private String visibleWhen;
    private boolean transferFocusOnSelect = true;    
    private boolean dirty;
    private boolean loaded;
    private boolean nullWhenEmpty = true; 
    
    private int stretchWidth;
    private int stretchHeight;     
    
    public XLookupField() 
    {
        super("com/rameses/rcp/icons/search.png");
        initComponent(); 
    }    
    
    private void initComponent() {
        setOrientation( super.ICON_ON_RIGHT );  
        
        document.setTextCase(TextCase.UPPER); 
        setDocument(document); 
        
        if (Beans.isDesignTime()) { 
            document.setTextCase(TextCase.NONE); 
            return;
        } 
        
        addActionMapping(ACTION_MAPPING_KEY_ESCAPE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try { refresh(); } catch(Throwable t) {;} 
            }
        });  
        new MouseEventSupport(this).install(); 
    }

    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters ">
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) { 
        this.nullWhenEmpty = nullWhenEmpty; 
    }
    
    public String getHandler() { return handler; }    
    public void setHandler(String handler) { this.handler = handler; }
    
    public Object getHandlerObject() { return handlerObject; }    
    public void setHandlerObject(Object handlerObject) {
        this.handlerObject = handlerObject;
    }
    
    public String getExpression() { return expression; }    
    public void setExpression(String expression) {
        this.expression = expression;
        super.setText(expression);
    }
        
    public String getText() 
    {
        if ( Beans.isDesignTime() ) 
        {
            if ( !ValueUtil.isEmpty(expression) )
                return expression;
            else if ( !ValueUtil.isEmpty(getName()) )
                return getName();
            else
                return super.getText();
        } 
        else { 
            return super.getText();
        }
    }
    
    public boolean isTransferFocusOnSelect() { return transferFocusOnSelect; }    
    public void setTransferFocusOnSelect(boolean transerFocusOnSelect) {
        this.transferFocusOnSelect = transerFocusOnSelect;
    }    
    
    public TextCase getTextCase() { return document.getTextCase(); }    
    public void setTextCase(TextCase textCase) {
        document.setTextCase(textCase);
    }
    
    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }    
    public void setTrimSpaceOption(TrimSpaceOption option) {
        this.trimSpaceOption = option;
    }    
    
    private UIInputUtil.Support getInputSupport() 
    {
        if (inputSupport.delegate == null)
        {
            Object o = getClientProperty(UIInputUtil.Support.class); 
            if (o != null) inputSupport.delegate = (UIInputUtil.Support) o;
        }
        return inputSupport;
    }     
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) 
    {
        if (info == null) return;
        
        PropertyInfoWrapper pi = new PropertyInfoWrapper(info); 
        setExpression(pi.getExpression()); 
        
        Object handler = pi.getHandler();
        if (handler instanceof String) 
        {
            setHandler(handler.toString()); 
            setHandlerObject(null);
        }
        else 
        {
            setHandler(null);
            setHandlerObject(handler); 
        }
    } 
    
    public String getReturnFields() { return returnFields; } 
    public void setReturnFields(String returnFields) { 
        this.returnFields = returnFields; 
    } 
    
    public String getDisableWhen() { return disableWhen; } 
    public void setDisableWhen(String disableWhen) {
        this.disableWhen = disableWhen;
    }
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen;
    }    
   
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="  Override methods ">
    
    protected InputVerifier getChildInputVerifier() { 
        return inputSupport; 
    }        

    protected void onprocessKeyEvent(KeyEvent e) 
    {
        if (e.isActionKey() || e.isAltDown() || e.isControlDown()) return; 
        
        dirty = true; 
    }        
    
    public boolean requestFocusInWindow() { 
        if (!isEnabled()) { 
            transferFocus(); 
            return false; 
        } 
        
        return super.requestFocusInWindow();
    }
    
    // </editor-fold>
       
    // <editor-fold defaultstate="collapsed" desc="  UILookup implementation  ">
    
    public boolean focusFirstInput() { 
        getBinding().Utils.focusComponent( this ); 
        return true; 
    }    
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public void refresh() {
        //force to update the component status
        updateBackground(); 
        
        Object itemBean = null; 
        boolean hasBindingName = !ValueUtil.isEmpty(getName()); 
        if ( hasBindingName ) { 
            try {
                itemBean = UIControlUtil.getBeanValue(this);
            } catch(NullPointerException npe) {
                npe.printStackTrace();
            } catch(Throwable t) {
                System.out.println("[WARN] error caused by " + t.getMessage());
            } 
        }

        Object expval = null;
        Object bean = binding.getBean(); 
        if ( bean != null ) {
            Object exprBean = createExpressionBean(itemBean); 
            String sval = getDisableWhen();
            if (sval != null && sval.length() > 0) {
                try { 
                    setEnabled(!UIControlUtil.evaluateExprBoolean(exprBean, sval)); 
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                if (!isEnabled() && hasFocus()) transferFocus(); 
            }
            
            sval = getExpression();
            if (sval != null && sval.length() > 0) {
                expval = UIControlUtil.evaluateExpr(exprBean, expression); 
            }
            
            sval = getVisibleWhen();
            if (sval != null && sval.length() > 0) {
                try {
                    setVisible(UIControlUtil.evaluateExprBoolean(exprBean, sval)); 
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                if (!isVisible() && hasFocus()) transferFocus(); 
            }
        }
        
        if ( hasBindingName && (itemBean == null || itemBean.toString().trim().length()==0)) { 
            setText( null ); 
        } else {
            setText((expval == null? null: expval.toString()));
        }
    }

    public void load() 
    {
        dirty = false; 
    }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);        
    }
    
    private Object createExpressionBean(Object itemBean) 
    {
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    } 
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("disableWhen", getDisableWhen());
        map.put("expression", getExpression()); 
        map.put("focusAccelerator", getFocusAccelerator());
        map.put("handler", getHandler());
        map.put("handlerObject", getHandlerObject());
        map.put("returnFields", getReturnFields()); 
        map.put("varName", getVarName()); 
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
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  UISelector implementation ">
    
    public void setValue(Object value) 
    {
        if ( value instanceof EventObject ) {
            if (value instanceof KeyEvent) {
                setText(((KeyEvent) value).getKeyChar()+"");    
            }
        } else {
            if ( value != null )
                setText(value.toString());
            else
                setText("");
        }         
        this.dirty = false; 
    } 
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc="  Validatable implementation  ">
    
    public String getCaption() { return property.getCaption(); }    
    public void setCaption(String caption) { 
        property.setCaption(caption); 
    } 

    public boolean isRequired() { return property.isRequired(); }    
    public void setRequired(boolean required) {
        property.setRequired(required);
    }
    
    public char getCaptionMnemonic() { 
        return property.getCaptionMnemonic();
    }
    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
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
    public void setCaptionFont(Font font) {
        property.setCaptionFont(font);
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }
    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }    

    public ActionMessage getActionMessage() { return actionMessage; } 
    
    public void validateInput() {
        validateInput( actionMessage ); 
    } 
    public void validateInput( ActionMessage am ) {
        am.clearMessages();
        property.setErrorMessage(null);
        if ( ValueUtil.isEmpty(getText()) ) {
            if (isRequired()) {
                am.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
            }
        }         
        if ( am.hasMessages() ) {
            property.setErrorMessage( am.toString() );
        }        
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  ActiveControl implementation  "> 
    
    public ControlProperty getControlProperty() {
        return property; 
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="  ActionListener/LookupSelector implementation  ">
    
    private int selectionOption = JOptionPane.CANCEL_OPTION;
    
    protected void onactionPerformed(ActionEvent e) {
        fireLookup(); 
    }       
    
    private void fireLookup() {
        if (Beans.isDesignTime()) return;
        if (isReadonly()) return;

        try {
            getInputVerifierProxy().setEnabled(false); 
            Object obj = loadHandler();
            loaded = true;
            
            if (obj instanceof Exception) {
                MsgBox.err((Exception)obj);
                getInputVerifierProxy().setEnabled(true);
                return; 
            }
            
            if (lookupHandlerProxy.getModel() == null) { 
                MsgBox.alert("No available lookup model found. Please check.");
                return;
            } 

            selectionOption = JOptionPane.CANCEL_OPTION;
            lookupHandlerProxy.getModel().setSelector(this); 
            lookupHandlerProxy.getModel().setReturnFields(getReturnFields()); 
            boolean show = lookupHandlerProxy.getModel().show( getText() ); 
            if ( show ) {
                Object oModel = lookupHandlerProxy.getModel();
                if (oModel instanceof LookupModel) {
                    ((LookupModel) oModel).setSelectedItem(-1);
                }

                UIController c =  lookupHandlerProxy.getController(); 
                if ( c == null ) return; //should use a default lookup handler
                
                UIControllerContext uic = new UIControllerContext(c);
                Platform platform = ClientContext.getCurrentContext().getPlatform();
                String conId = uic.getId();
                if ( conId == null ) conId = getName() + handler;
                if ( platform.isWindowExists(conId) ) return;
                
                UIControllerPanel lookupPanel = new UIControllerPanel(uic);
                new WindowSupport().install(lookupPanel); 
                
                Map props = new HashMap();
                props.put("id", conId);
                props.put("title", uic.getTitle());
                
                try {
                    Map openerProps = lookupHandlerProxy.opener.getProperties(); 
                    props.put("width", openerProps.get("width"));
                    props.put("height", openerProps.get("height"));
                } catch(Exception ex) {;} 
                
                platform.showPopup(this, lookupPanel, props);
            }
        } catch( Exception e ) {
            MsgBox.err(e); 
            getInputVerifierProxy().setEnabled(true); 
        } 
    }    
    
    public Object select(Object value) {
        selectedValue = value; 
        selectionOption = JOptionPane.OK_OPTION;
        LookupDataSource lds = lookupHandlerProxy.getModel();
        String flds = lds.getReturnFields();
        if (flds != null && flds.length() > 0) {
            selectedValue = new ResultFieldsMapper().parse(flds, value); 
            
        } else {
            String itemKey = lds.getReturnItemKey();
            String itemVal = lds.getReturnItemValue();
            selectedValue = new ResultKeyValueMapper().parse(itemKey, itemVal, value);
        }
        
        Object outcome = getInputSupport().setValue(getName(), selectedValue);         
        putClientProperty("updateBeanValue", true); 
        getInputVerifierProxy().setEnabled(true);        
        if (lookupHandlerProxy.hasOnselectCallback()) { 
            putClientProperty("cellEditorValue", "no_updates"); 
        }
        if (outcome instanceof Opener || "_close".equals(outcome)) {
            return outcome; 
        } 
        
        if ( transferFocusOnSelect ) { 
            this.transferFocus(); 
        } else {
            this.requestFocus();    
        } 
        return null; 
    }

    public void cancelSelection() 
    {
        putClientProperty("updateBeanValue", false); 
        getInputVerifierProxy().setEnabled(true);
        this.requestFocus();         
    }    
    
    private Object loadHandler()
    {
        Object o = null;
        if ( !ValueUtil.isEmpty(handler) ) {
            if (handler.matches(".+:.+")) {
                //handler is a module:workunit name                
                o = LookupOpenerSupport.lookupOpener(handler, new HashMap()); 
            } else {
                //check if there is a binding object passed by the JTable
                Binding oBinding = (Binding) getClientProperty(Binding.class); 
                if (oBinding == null) oBinding = getBinding(); 

                o = UIControlUtil.getBeanValue(oBinding, handler);
                if (o instanceof Exception) return o;
            }
        } else if ( handlerObject != null ) { 
            o = handlerObject;
        } 

        if (o == null) return null;
        
        if (o instanceof LookupHandler) { 
            lookupHandlerProxy.setHandler((LookupHandler) o); 
            
        } else if (o instanceof Opener) { 
            Binding oBinding = (Binding) getClientProperty(Binding.class); 
            if (oBinding == null) oBinding = getBinding(); 

            Opener opener = (Opener) o; 
            opener = ControlSupport.initOpener( opener, oBinding.getController() );
            lookupHandlerProxy.setOpener(opener); 
        }
        return null;
    } 

    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="  LookupHandlerProxy (Class)  ">
    
    private class LookupHandlerProxy implements LookupHandler 
    {
        private Object codeBean;
        private LookupDataSource model;
        private LookupHandler handler;
        private Opener opener;
        private Object onselectCallback;
        private Object onemptyCallback;
        
        LookupDataSource getModel() { return model; }
        
        UIController getController() { 
            return (opener == null? null: opener.getController()); 
        } 
        
        boolean hasOnselectCallback() { return (onselectCallback != null); } 
        boolean hasOnemptyCallback() { return (onemptyCallback != null); } 
        
        void setHandler(LookupHandler handler) { this.handler = handler; }
        void setOpener(Opener opener) 
        { 
            if (opener != null) {
                if (opener.getParams() != null) {
                    onselectCallback = opener.getParams().get("onselect");
                    onemptyCallback = opener.getParams().get("onempty");
                }

                UIController controller = opener.getController(); 
                if (controller == null) 
                    throw new IllegalStateException("'"+opener.getName()+"' opener must have a controller");

                model = null;                 
                codeBean = controller.getCodeBean();
                if ( codeBean instanceof LookupDataSource ) {
                    model = (LookupDataSource) codeBean; 
                } else if ( codeBean instanceof SimpleLookupDataSource ) {
                    model = new LookupDataSourceProxy((SimpleLookupDataSource) codeBean); 
                } else {
                    throw new IllegalStateException("'"+opener.getName()+"' opener controller must be an instance of LookupDataSource");
                }
                
                controller.setId(opener.getId()); 
                controller.setName(opener.getName()); 
                controller.setTitle(opener.getCaption());

                Object callback = model.getOnselect();
                if (callback != null) onselectCallback = callback;

                callback = model.getOnempty();
                if (callback != null) onemptyCallback = callback;
            } else {
                model = null; 
            }
            
            this.opener = opener; 
        }
        
        public Object getOpener() 
        {
            if (handler != null) return handler.getOpener(); 
            
            return opener;
        }

        public void onselect(Object item) 
        {
            if (handler != null) handler.onselect(item);
        }
        
        Object invokeOnempty(Object value) {
            return invokeHandler(onemptyCallback, value);
        }
        
        Object invokeOnselect(Object item) {
            return invokeHandler(onselectCallback, item);
        }        
        
        private Object invokeHandler(Object handler, Object item){
            if (handler == null) return null;

            Method method = null; 
            Class clazz = handler.getClass();
            try { method = clazz.getMethod("call", new Class[]{Object.class}); }catch(Exception ign){;} 

            try 
            {
                if (method != null) { 
                    return method.invoke(handler, new Object[]{item}); 
                } else {
                    return null; 
                }
            } catch (RuntimeException re) {
                re.printStackTrace();
                throw re;
            } catch (Exception ex) {
                ex.printStackTrace(); 
                throw new IllegalStateException(ex.getMessage(), ex); 
            }
        }        
    }
    
    // </editor-fold> 
            
    // <editor-fold defaultstate="collapsed" desc="  LookupInputSupport (Class)  ">
    
    private class LookupInputSupport extends InputVerifier implements UIInputUtil.Support 
    {
        UIInputUtil.Support delegate;
        
        public boolean verify(JComponent input) 
        {
            if (!dirty) return true; 
            
            if (isReadonly() || !isEnabled() || !isEditable()) return true;
            
            /*
             *  workaround fix when called by the JTable
             */
            try { 
                if ( !loaded ) { 
                    loadHandler(); 
                    loaded = true; 
                } 
            } catch(Throwable t) { 
                System.out.println("[XLookupField] error on field "+ XLookupField.this.getName() );
                t.printStackTrace(); 
                return true; 
            } 
            
            JComponent jcomp = XLookupField.this;
            jcomp.putClientProperty("UIControl.value", null); 
            
            String text = getText();             
            if (!ValueUtil.isEmpty(getExpression()) && ValueUtil.isEmpty(text)) 
            {
                Object value = isNullWhenEmpty()? null: new HashMap(); 
                if (lookupHandlerProxy.hasOnemptyCallback()) {
                    lookupHandlerProxy.invokeOnempty(value); 
                } else {
                    updateBeanValue(input.getName(), value); 
                }
                jcomp.putClientProperty("UIControl.value", new Object[]{value}); 
                selectedValue = null;
            } 
            
            publishUpdates(); 
            return true; 
        }  
        
        public Object setValue(String name, Object value) {
            return setValue(name, value, null); 
        }   
        
        public Object setValue(String name, Object value, JComponent jcomp){
            JComponent xlkp = XLookupField.this;
            xlkp.putClientProperty("UIControl.value", null); 
            
            Object outcome = null;
            if (lookupHandlerProxy.hasOnselectCallback()) { 
                outcome = lookupHandlerProxy.invokeOnselect(value); 
            } else { 
                updateBeanValue(name, value); 
            }

            xlkp.putClientProperty("UIControl.value", new Object[]{value}); 
            publishUpdates(); 
            dirty = false; 
            return outcome;
        }
        
        private void updateBeanValue(String name, Object value) 
        {
            if (name == null) return;
            
            if (delegate == null) {
                //handle the updating of the bean
                Object bean = binding.getBean();
                if (bean != null) {
                    PropertyResolver resolver = PropertyResolver.getInstance();
                    resolver.setProperty(binding.getBean(), name, value); 
                    binding.getValueChangeSupport().notify(name, value);
                } 
            } 
            else {
                //delegate the updating of the bean
                delegate.setValue(name, value, XLookupField.this); 
            }
        }
        
        private void publishUpdates() {
            if (delegate == null) {
                //only do refresh and notifications when no delegator is set
                refresh();
                binding.notifyDepends(XLookupField.this);
            }             
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  WindowSupport (Class)  ">    
    
    private class WindowSupport implements AncestorListener, ActionListener, PropertyChangeListener 
    {
        private JComponent component;
        
        void install(JComponent comp) 
        { 
            this.component = comp;
            comp.addAncestorListener(this);  
            comp.addPropertyChangeListener("Window.close", this); 
        } 
        
        public void ancestorAdded(AncestorEvent e) 
        {        
            JRootPane rootPane = component.getRootPane();
            if (rootPane == null) return;
            
            rootPane.putClientProperty("Window.closeAction", this); 
        }

        public void ancestorRemoved(AncestorEvent e) {}
        public void ancestorMoved(AncestorEvent e) {}        

        public void actionPerformed(ActionEvent e) 
        {
            JRootPane rootPane = component.getRootPane();
            if (rootPane == null) return;
            
            Container parent = rootPane.getParent(); 
            if (parent instanceof SubWindow) 
                ((SubWindow) parent).closeWindow(); 
        } 

        public void propertyChange(PropertyChangeEvent evt) 
        {
            if (selectionOption == JOptionPane.CANCEL_OPTION)
                cancelSelection();                 
        }
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyInfoWrapper (Class)  "> 
    
    private class PropertyInfoWrapper 
    {
        private PropertySupport.LookupPropertyInfo property;
        private Map map = new HashMap();
        
        PropertyInfoWrapper(PropertySupport.PropertyInfo info) 
        {
            if (info instanceof Map) map = (Map) info;
            if (info instanceof PropertySupport.LookupPropertyInfo)
                property = (PropertySupport.LookupPropertyInfo) info;
        }
        
        public Object getHandler() 
        {
            Object value = map.get("handler");
            if (value == null && property != null)
                value = property.getHandler(); 
            
            return value;
        }

        public String getExpression() 
        {
            Object value = map.get("expression");
            if (value == null && property != null)
                value = property.getExpression();
            
            return (value == null? null: value.toString());
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ResultKeyValueMapper (class) ">  

    private class ResultKeyValueMapper 
    {      
        PropertyResolver res = PropertyResolver.getInstance();

        public Object parse(String itemKey, String itemVal, Object value) 
        {
            if (value == null) return null;
            if (itemKey == null && itemVal == null) return value;  
            
            if (itemKey != null && itemVal == null) { 
                itemVal = itemKey; 
            } else if (itemKey == null && itemVal != null) {
                itemKey = itemVal;
            }

            if (value instanceof List) {                
                List results = new ArrayList();                 
                for (Object o : (List)value) {
                    Object xo = extract(itemKey, itemVal, o);
                    if (xo != null) results.add(xo); 
                } 
                return results;
                
            } else if (value != null) {
                return extract(itemKey, itemVal, value); 
                
            } else { 
                return null; 
            } 
        }

        private Object extract(String itemKey, String itemVal, Object o) {
            Map map = new HashMap(); 
            if (itemKey != null) 
                map.put("key", res.getProperty(o, itemKey)); 
            if (itemVal != null) 
                map.put("value", res.getProperty(o, itemVal)); 

            return map; 
        } 
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ResultFieldsMapper (class) ">  
    
    private class ResultFieldsMapper 
    {       
        PropertyResolver res = PropertyResolver.getInstance();
        
        public Object parse(String fields, Object value) 
        {
            if (value == null || fields == null) return value;
            
            String[] fldnames = fields.split(",");
            
            if (value instanceof List) {
                List results = new ArrayList();                 
                for (Object o : (List)value) {
                    Object xo = extract(fldnames, o);
                    if (xo != null) results.add(xo); 
                } 
                return results;
                
            } else if (value != null) {
                return extract(fldnames, value); 
                
            } else {
                return null; 
            }
        }
        
        private Object extract(String[] fldnames, Object o) {
            Map map = new HashMap(); 
            for (String name : fldnames) { 
                if (name == null || name.length() == 0) continue;
                
                map.put(name, res.getProperty(o, name)); 
            }
            return map; 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" LookupDataSourceProxy ">
    
    private class LookupDataSourceProxy implements LookupDataSource {

        SimpleLookupDataSource source; 
        LookupSelector selector;
        
        LookupDataSourceProxy( SimpleLookupDataSource source ) {
            this.source = source; 
        }
        
        public Object getOnselect() { return null; }
        public Object getOnempty() { return null; }

        public LookupSelector getSelector() {
            return selector; 
        }
        public void setSelector(LookupSelector selector) {
            this.selector = selector; 
            source.setSelector( selector ); 
        }
        
        public boolean show(String searchtext) {
            source.setSearchText( searchtext );  
            return true; 
        } 
        
        public String getReturnItemKey() { return null; }
        public void setReturnItemKey(String returnItemKey) {}

        public String getReturnItemValue() { return null; }
        public void setReturnItemValue(String returnItemValue) {}

        public String getReturnFields() { return null; }
        public void setReturnFields(String returnFields) {}        
    }
    
    // </editor-fold>
}
