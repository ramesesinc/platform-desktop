/*
 * OpenerTextField.java
 *
 * Created on June 10, 2013, 5:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.platform.interfaces.Platform;
import com.rameses.rcp.common.DefaultCallbackHandler;
import com.rameses.rcp.common.LookupOpenerSupport;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.DefaultTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.framework.UIController;
import com.rameses.rcp.framework.UIControllerContext;
import com.rameses.rcp.framework.UIControllerPanel;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.UIInputVerifier;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.Beans;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.PanelUI;
import javax.swing.text.Document;

/**
 *
 * @author wflores
 */
public class XOpenerField extends DefaultTextField implements UIInput, 
    UIInputVerifier, Validatable, ActiveControl, MouseEventSupport.ComponentInfo  
{
    private Dimension minimumSize = new Dimension(0,0); 
    private BorderImpl borderImpl;
    private ButtonImpl buttonImpl;
    private Insets margin;
    
    private boolean allowSelectAll;

    private ControlProperty property = new ControlProperty();
    private ActionMessage actionMessage = new ActionMessage();    
    private Binding binding;    
    private Object handlerObject;    
    private String handler;
    private String expression;    
    private String varName = "item";
    private String inputFormat;  
    private String inputFormatErrorMsg;
    private String[] depends;    
    private boolean nullWhenEmpty = true;
    private int index;

    private ApproveCallbackHandler callbackHandler = new ApproveCallbackHandler();
    private TrimSpaceOption trimSpaceOption;
    private TextDocument document;    
    
    private Object oldValue;
    private Object value;
    
    private int stretchWidth;
    private int stretchHeight;     

    public XOpenerField() {
        super();
        initComponents();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents ">  
    
    private void initComponents() {
        getButtonImpl();        
        super.setDocument(document = new TextDocument());

        Dimension dim = getPreferredSize();
        minimumSize = new Dimension(50, dim.height);
        setPreferredSize(new Dimension(Math.max(dim.width,100), dim.height)); 

        document.setTextCase(TextCase.UPPER); 
        trimSpaceOption = TrimSpaceOption.NORMAL;
        
        addActionMapping(ACTION_MAPPING_KEY_ESCAPE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try { refresh(); } catch(Throwable t) {;} 
            }
        }); 
        new MouseEventSupport(this).install(); 
        new KeyboardAction().install(); 
    }
    
    private BorderImpl getBorderImpl() 
    {
        if (borderImpl == null) borderImpl = new BorderImpl(); 
        
        return borderImpl;
    }  
    
    private ButtonImpl getButtonImpl()
    {
        if (buttonImpl == null) 
        {
            buttonImpl = new ButtonImpl();
            buttonImpl.setText("...");
            buttonImpl.setMargin(new Insets(0,0,0,0)); 
            buttonImpl.setFocusable(false);    
            add(buttonImpl);            
        }
        return buttonImpl; 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters ">  

    public final void setLayout(LayoutManager mgr) {;} 
    public final void setDocument(Document document) {;} 
    
    public Insets getMargin() { return margin; } 
    public final void setMargin(Insets margin) 
    {
        if (margin == null) margin = new Insets(0, 0, 0, 0); 
        
        this.margin = margin;
        
        int width = getButtonImpl().getPreferredSize().width+2;
        Insets newMargin = new Insets(margin.top, margin.left, margin.bottom, margin.right); 
        newMargin.right = Math.max(newMargin.right, width); 
        super.setMargin(newMargin);
    } 
    
    public final void setBorder(Border border) 
    {
        getButtonImpl();
        
        if (border == null) border = BorderFactory.createEmptyBorder();
        
        Border compound = BorderFactory.createCompoundBorder(border, getBorderImpl()); 
        getBorderImpl().setSource(border);
        super.setBorder(compound); 
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
    }
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
    
    public String getInputFormat() { return inputFormat; }    
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }
    
    public String getInputFormatErrorMsg() { return inputFormatErrorMsg; }    
    public void setInputFormatErrorMsg(String inputFormatErrorMsg) {
        this.inputFormatErrorMsg = inputFormatErrorMsg;
    }    

    public void setEnabled(boolean enabled) 
    {
        super.setEnabled(enabled); 
        
        boolean readonly = isReadonly(); 
        setEditable(!readonly); 
        getButtonImpl().setEnabled(!readonly); 
    }
    
    public TextCase getTextCase() { 
        return document.getTextCase(); 
    } 
    public void setTextCase(TextCase textCase) { 
        document.setTextCase(textCase); 
    } 
    
    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }    
    public void setTrimSpaceOption(TrimSpaceOption trimSpaceOption) {
        this.trimSpaceOption = trimSpaceOption;
    } 
    
    public void setName(String name) 
    {
        super.setName(name);
        
        if (Beans.isDesignTime()) setText(name); 
    } 

    public void setUI(PanelUI ui) 
    {
        super.setUI(ui); 
                
        if ("true".equals(getClientProperty(JTable.class)+"")) 
            setBorder(null); 
    }
    
    protected InputVerifier getChildInputVerifier() {
        return UIInputUtil.VERIFIER;
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" owner, override methods ">  
    
    protected void onfocusGained(FocusEvent e) 
    {
        if ("false".equals(getClientProperty("allowSelectAll")+"")) {
            //do nothing
        }
        else if (allowSelectAll && callbackHandler.isReady()) { 
            selectAll(); 
        }
        allowSelectAll = true;
    }

    protected void onpropertyChange(String propertyName, boolean oldValue, boolean newValue) 
    {
        if ("Window.close".equals(propertyName))
            callbackHandler.onPopupClosed();
    }
        
    private void fireButtonClicked(ActionEvent e) 
    {
        try 
        {
            getInputVerifierProxy().setEnabled(false);
            allowSelectAll = false;
            onButtonClicked(e); 
        } 
        catch(Exception ex) {
            MsgBox.err(ex); 
        } 
        finally {
            getInputVerifierProxy().setEnabled(true);
        }
    }
    
    public void onButtonClicked(ActionEvent e) { 
        processHandler();
    }
    
    protected Opener getOpener() 
    {
        Object result = null;
        String handler = getHandler();
        if ( !ValueUtil.isEmpty(handler) ) 
        {
            if ( handler.matches(".+:.+") ) 
            {
                //handler is an invoker type name
                result = LookupOpenerSupport.lookupOpener(handler, new HashMap()); 
            }
            else 
            {
                //check if there is a binding object passed by the JTable
                Binding oBinding = (Binding) getClientProperty(Binding.class); 
                if (oBinding == null) oBinding = getBinding(); 
                
                result = UIControlUtil.getBeanValue(oBinding, handler);
            }
        } 
        else if ( handlerObject != null ) { 
            result = handlerObject; 
        } 
        
        return (Opener) result;
    }
    
    private void processHandler() 
    {
        if (Beans.isDesignTime()) return;
        if (isReadonly()) return;

        Opener opener = getOpener();
        if (opener == null) 
        {
            MsgBox.alert("No available opener handler specified");
            return;
        }
        
        opener = ControlSupport.initOpener(opener, getBinding().getController());
        
        UIController uic = opener.getController(); 
        if (uic == null) 
            throw new IllegalStateException("'"+opener.getName()+"' opener must have a controller");
        
        try {
            UIControlUtil.setBeanValue(uic.getCodeBean(), "handler", callbackHandler); 
        } catch(Exception ex) {
            System.out.println("Unable to set value for 'handler' property in " + uic.getCodeBean());
        } 
        
        try {
            UIControlUtil.setBeanValue(uic.getCodeBean(), "value", getValue()); 
        } catch(Exception ex) {
            System.out.println("Unable to set value for 'value' property in " + uic.getCodeBean());
        } 
        
        uic.setId(opener.getId());
        uic.setName(opener.getName());
        uic.setTitle(opener.getCaption());  
                
        UIControllerContext uicontext = new UIControllerContext(uic);
        String ctxId = uicontext.getId();
        if ( ctxId == null ) ctxId = getName() + handler;
        
        Platform platform = ClientContext.getCurrentContext().getPlatform();
        if ( platform.isWindowExists(ctxId) ) return;

        UIControllerPanel uipanel = new UIControllerPanel(uicontext);
        Map props = new HashMap();
        props.put("id", ctxId);
        props.put("title", uicontext.getTitle());

        try 
        {
            Map openerProps = opener.getProperties();
            props.put("width", openerProps.get("width"));
            props.put("height", openerProps.get("height"));
        } 
        catch(Exception ex){
        } 

        callbackHandler.setStatus(callbackHandler.READY);
        platform.showPopup(this, uipanel, props); 
    }
    
    private void fireValueChanged() 
    {
        Object newValue = null;
        
        try 
        {
            updateBackground();
            
            String expr = getExpression();
            if (expr == null || value instanceof String) 
            {
                newValue = value;
            }
            else 
            {
                try { 
                    newValue = UIControlUtil.evaluateExpr(createExpressionBean(), expr); 
                } catch(Exception ex) {;}
            }
        }
        catch(Exception ex) 
        {
            if (ClientContext.getCurrentContext().isDebugMode()) 
                ex.printStackTrace(); 
        }
        
        document.loadValue(newValue);
    } 
    
    private Object createExpressionBean() 
    {
        ExprBeanSupport beanSupport = new ExprBeanSupport(getBinding().getBean());
        Object itemBean = (value == null? new HashMap(): value);
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    }     
    
    public void registerKeyboardAction(ActionListener anAction, KeyStroke aKeyStroke, int aCondition) 
    {
        super.registerKeyboardAction(anAction, aKeyStroke, aCondition); 
        new KeyboardAction().install(); 
    }
    
    private void installKeyboardAction(ActionListener anAction, KeyStroke aKeyStroke, int aCondition) {
        super.registerKeyboardAction(anAction, aKeyStroke, aCondition); 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput implementation ">  
    
    public Object getValue() 
    {
        if (document.isDirty())
        {
            String text = getText();
            if (text == null || text.length() == 0)
                return (isNullWhenEmpty()? null: "");
            else 
                return text; 
        }
        else if (value == null) { 
            return (isNullWhenEmpty()? null: ""); 
        }
        else {
            return value;
        } 
    } 
    
    public void setValue(Object value) 
    {
        callbackHandler.setStatus(callbackHandler.READY); 
        
        if (value instanceof EventObject) 
        {
            if (value instanceof KeyEvent)
            {
                KeyEvent ke = (KeyEvent) value;
                setText( ke.getKeyChar()+"" );
                allowSelectAll = false; 
            }
        } 
        else {
            setText((value == null? "": value.toString()));
        } 
    }

    public boolean isNullWhenEmpty() { return nullWhenEmpty; }
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }

    public void setReadonly(boolean readonly) 
    {
        super.setReadonly(readonly);
        getButtonImpl().setEnabled(!readonly); 
    }

    public void setRequestFocus(boolean focus) {
        if (focus) requestFocus();
    }

    public boolean isImmediate() { return false; }

    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }

    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void load() {
    }
    
    public void refresh() 
    {
        updateBackground();
        
        try {
            this.value = UIControlUtil.getBeanValue(this);
        } catch(Exception e) {
            this.value = null; 
        }
        
        this.oldValue = this.value; 
        fireValueChanged(); 
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
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("handler", getHandler());
        map.put("handlerObject", getHandlerObject());
        map.put("expression", getExpression()); 
        map.put("varName", getVarName());
        map.put("focusAccelerator", getFocusAccelerator());
        map.put("nullWhenEmpty", isNullWhenEmpty());
        map.put("required", isRequired()); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" UIInputVerifier implementation ">  
    
    public boolean verify(JComponent input) 
    {
        if (callbackHandler.isCancelled()) 
            callbackHandler.setStatus(callbackHandler.READY); 

        updateStatus();
        return true; 
    }
        
    private void updateStatus() 
    {
        if (callbackHandler.isApproved() || document.isDirty()) 
            putClientProperty("updateBeanValue", true);
        else 
            putClientProperty("updateBeanValue", false);
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Validatable implementation ">  
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { 
        property.setCaption(caption);
    }

    public boolean isRequired() { return property.isRequired(); }
    public void setRequired(boolean required) {
        property.setRequired(required);
    }

    public ActionMessage getActionMessage() { return actionMessage; }
    
    public void validateInput() {
        validateInput( getActionMessage() );  
    }
    public void validateInput( ActionMessage am ) {
        am.clearMessages();
        property.setErrorMessage(null);
        
        Object value = getValue();
        String text = (value == null? null: value.toString()); 
        
        if (ValueUtil.isEmpty(text)) {
            if (isRequired()) {
                am.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
            }
            
        } else if (!ValueUtil.isEmpty(getInputFormat()) && !text.matches(getInputFormat()) ) {
            String msg = null;
            if ( inputFormatErrorMsg != null ) {
                msg = inputFormatErrorMsg; 
            } else {
                msg = "Invalid input format for {0}"; 
            } 
            am.addMessage(null, msg, new Object[]{ getCaption() });
        }
        
        if (am.hasMessages()) {
            property.setErrorMessage(am.toString());
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">  
    
    public ControlProperty getControlProperty() { return property; }
    
    public char getCaptionMnemonic() { 
        return property.getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char captionMnemonic) {
        property.setCaptionMnemonic(captionMnemonic);
    } 
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }    
    public void setCaptionWidth(int captionWidth) {
        property.setCaptionWidth(captionWidth);
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
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BorderImpl (class) ">  
    
    private class BorderImpl extends AbstractBorder 
    {
        XOpenerField root = XOpenerField.this;
        
        private Border source; 
        private Insets sourceMargin;
        
        void setSource(Border source) 
        {
            this.source = source;
            this.sourceMargin = null; 
        }
        
        public Insets getBorderInsets(Component c) 
        {
            if (sourceMargin == null && source != null) 
                sourceMargin = source.getBorderInsets(c); 

            return getBorderInsets(c, new Insets(0,0,0,0)); 
        }
        
        public Insets getBorderInsets(Component c, Insets insets) {
            return insets; 
        }

        public boolean isBorderOpaque() { return false; }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
        {
            if (buttonImpl.getParent() == null) root.add(buttonImpl); 
            
            Dimension dim = buttonImpl.getPreferredSize();
            int nX = root.getWidth()-dim.width-1;
                
            buttonImpl.setBounds(nX, 1, dim.width, root.getHeight()-2); 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ButtonImpl (class) ">  
    
    private class ButtonImpl extends JButton 
    {
        XOpenerField root = XOpenerField.this;
        
        protected void processMouseEvent(MouseEvent e) 
        {
            this.setCursor(Cursor.getDefaultCursor()); 
            
            if (!this.isEnabled()) return;
            
            if (e.getID() == MouseEvent.MOUSE_PRESSED) 
                root.getInputVerifierProxy().setEnabled(false);
            
            else if (e.getID() == MouseEvent.MOUSE_RELEASED) 
                root.getInputVerifierProxy().setEnabled(true);
            
            super.processMouseEvent(e); 
        }
        
        protected void fireActionPerformed(ActionEvent e) 
        {
            root.fireButtonClicked(e); 
            super.fireActionPerformed(e); 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ApproveCallbackHandler (class) ">          
    
    private class ApproveCallbackHandler extends DefaultCallbackHandler
    {
        int READY       = 1;
        int APPROVED    = 2;
        int CANCELLED   = 3;
        
        XOpenerField root = XOpenerField.this;
        private int status = READY;
        
        boolean isReady() { return status==READY; }
        boolean isApproved() { return status==APPROVED; } 
        boolean isCancelled() { return status==CANCELLED; } 
        
        void setStatus(int status) { this.status = status;  }
        
        void onPopupClosed() 
        {
            if (!isApproved()) setStatus(CANCELLED); 
        }
        
        public Object call(Object[] args) 
        {
            if (args == null || args.length == 0) return null; 
            
            setStatus(APPROVED); 
            root.value = args[0]; 
            root.fireValueChanged(); 
            return null; 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" KeyboardAction (class) ">          
    
    private class KeyboardAction implements ActionListener
    {
        XOpenerField root = XOpenerField.this;
        private JComponent component;
        
        void install() 
        {
            KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
            root.unregisterKeyboardAction(ks); 
            root.installKeyboardAction(this, ks, JComponent.WHEN_FOCUSED); 
        }
        
        public void actionPerformed(ActionEvent e) {
            fireButtonClicked(e);
        } 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" PropertyInfoWrapper (Class)  "> 
    
    private class PropertyInfoWrapper 
    {
        private PropertySupport.OpenerPropertyInfo property;
        private Map map = new HashMap(); 
        
        PropertyInfoWrapper(PropertySupport.PropertyInfo info) 
        {
            if (info instanceof Map) map = (Map)info;
            if (info instanceof PropertySupport.OpenerPropertyInfo)
                property = (PropertySupport.OpenerPropertyInfo) info;
        }
        
        public String getExpression() 
        {
            Object value = map.get("expression");
            if (value == null && property != null)
                value = property.getExpression();
            
            return (value == null? null: value.toString());
        }
        
        public Object getHandler() 
        {
            Object value = map.get("handler");
            if (value == null && property != null)
                value = property.getHandler();
            
            return value; 
        }
    }
    
    // </editor-fold>        
}
