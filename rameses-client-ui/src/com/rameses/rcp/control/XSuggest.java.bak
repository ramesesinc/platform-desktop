/*
 * XSuggest.java
 *
 * Created on December 16, 2013, 8:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.ObjectProxy;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.SuggestModel;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.constant.UIConstants;
import com.rameses.rcp.control.suggest.SuggestItem;
import com.rameses.rcp.control.suggest.SuggestPopup;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.IconedTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.ErrorDialog;
import com.rameses.rcp.util.TimerManager;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author wflores
 */
public class XSuggest extends IconedTextField implements MouseEventSupport.ComponentInfo, 
    UIInput, ActiveControl, Validatable   
{
    public final String ACTION_MAPPING_VK_DOWN = "ACTION_MAPPING_VK_DOWN";
    public final String ACTION_MAPPING_VK_UP = "ACTION_MAPPING_VK_UP";
    
    private TrimSpaceOption trimSpaceOption;
    private TextDocument document;
    private InputVerifierImpl inputVerifier; 
            
    private String varName;
    private String handler;
    private Object handlerObject;
    private String expression;
    private String itemExpression;
    private String type;
    private String visibleWhen;
    private String origtext;
    
    private Object value;
    private boolean readonly;
    private Binding binding;
    private String[] depends;
    private int index; 
    private ControlProperty property; 
    
    private CallbackHandler onselectHandler;
    private CallbackHandler onemptyHandler;
    
    private SuggestModel model; 
    private boolean enable_search;
    private String currentTextValue;
    
    public XSuggest() {
        super("com/rameses/rcp/icons/dropdown.png");
        initComponent(); 
    }

    // <editor-fold defaultstate="collapsed" desc=" init components ">
    
    private void initComponent() {
        setOrientation( super.ICON_ON_RIGHT );  
        setVarName("item");
        type = UIConstants.SuggestTypes.BASIC; 
        trimSpaceOption = TrimSpaceOption.ALL; 
        document = new TextDocument();
        document.setTextCase(TextCase.NONE); 
        document.add(new TextDocument.DocumentListener() {
            public void onupdate() {
                onupdate_document();
            }
        }); 
        setDocument(document); 
        
        if (Beans.isDesignTime()) { 
            document.setTextCase(TextCase.NONE); 
            return;
        }  
        
        addActionMapping(ACTION_MAPPING_KEY_ESCAPE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try { onescape(); } catch(Throwable t) {;} 
            }
        });  
        new MouseEventSupport(this).install(); 
        addMouseListener(new MouseListenerImpl()); 
        putClientProperty(UIInputUtil.EventHandler.class, new EventHandlerImpl());
    }

    protected void initActionKeys(InputMap inputMap, ActionMap actionMap) {
        KeyStroke vkdown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0); 
        inputMap.put(vkdown, ACTION_MAPPING_VK_DOWN); 
        actionMap.put(ACTION_MAPPING_VK_DOWN, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onmoveDown();
            }
        }); 
        
        KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0); 
        inputMap.put(vkup, ACTION_MAPPING_VK_UP); 
        actionMap.put(ACTION_MAPPING_VK_UP, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                onmoveUp();
            }
        }); 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    protected InputVerifier getChildInputVerifier() {
        if (inputVerifier == null) {
            inputVerifier = new InputVerifierImpl();
        }
        return inputVerifier; 
    }
        
    public String getVarName() { return varName; } 
    public void setVarName(String varName) { this.varName = varName; }
        
    public String getHandler() { return handler; }    
    public void setHandler(String handler) { this.handler = handler; }
    
    public Object getHandlerObject() { return handlerObject; }    
    public void setHandlerObject(Object handlerObject) {
        this.handlerObject = handlerObject;
    }
    
    public String getItemExpression() { return itemExpression; } 
    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression; 
    }
    
    public String getExpression() { return expression; }    
    public void setExpression(String expression) {
        this.expression = expression;
        buildText(); 
    }
        
    public void setName(String name) {
        super.setName(name); 
        buildText();
    }

    public void setText(String text) {
        super.setText(text); 
        if (origtext == null) origtext = text; 
    }
    
    private void buildText() {
        if (!Beans.isDesignTime()) return;
        
        try { 
            String str = getExpression();
            if (str == null || str.length() == 0) str = getName();
            if (str == null || str.length() == 0) str = origtext;
            
            document.remove(0, document.getLength()); 
            document.insertString(0, (str == null? "": str), null); 
        } catch(Throwable t) {;} 
    }
        
    public TextCase getTextCase() { 
        return (document == null? null: document.getTextCase()); 
    } 
    public void setTextCase(TextCase textCase) {
        if (document != null) document.setTextCase(textCase);
    }
    
    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }    
    public void setTrimSpaceOption(TrimSpaceOption option) {
        this.trimSpaceOption = option;
    }   
    
    public String getType() { return type; } 
    public void setType(String type) {
        if (UIConstants.SuggestTypes.LOOKUP.equals(type)) {
            this.type = type; 
        } else {
            this.type = UIConstants.SuggestTypes.BASIC; 
        }
    }
               
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation ">
    
    public Map getInfo() {
        Map map = new HashMap();
        map.put("expression", getExpression()); 
        map.put("focusAccelerator", getFocusAccelerator());
        map.put("handler", getHandler());
        map.put("handlerObject", getHandlerObject());
        map.put("itemExpression", getItemExpression()); 
        map.put("readonly", isReadonly()); 
        map.put("varName", getVarName()); 
        return map;  
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" UIInput implementation ">
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen(String visibleWhen) {
        this.visibleWhen = visibleWhen; 
    }
    
    public Object getValue() { 
        if (value instanceof String) {
            return (value.toString().length()==0? null: value); 
        } else {
            return value; 
        } 
    }
    public void setValue(Object value) {
        this.value = value;
    }
    
    public boolean isNullWhenEmpty() { return true; } 
    
    public boolean isReadonly() { return readonly; } 
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
    
    public void setRequestFocus(boolean focus) {
        if (isFocusable() && isEnabled()) requestFocus(); 
    }
    
    public boolean isImmediate() { return true; } 
    
    public Binding getBinding() { return binding; } 
    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    
    public String[] getDepends() { return depends; } 
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }
        
    public int getIndex() { return index; } 
    public void setIndex(int index) {
        this.index = index; 
    }
    
    public void load() {
    }
    
    public void refresh() {
        try { 
            onselectHandler = null;  
            onemptyHandler = null;
            Object bean = getBinding().getBean();        
            Object handlerObj = getHandlerObject();
            if (handlerObj == null) {
                String handler = getHandler(); 
                if (handler != null) {
                    handlerObj = UIControlUtil.getBeanValue(bean, handler);
                }
            }

            if (handlerObj instanceof SuggestModel) {
                model = (SuggestModel) handlerObj; 
                ObjectProxy.MetaInfo meta = ObjectProxy.getMetaInfo(handlerObj);
                if (meta.containsMethod("onselect")) {
                    onselectHandler = new CallbackHandler(meta, "onselect"); 
                } 
                if (meta.containsMethod("onempty")) {
                    onemptyHandler = new CallbackHandler(meta, "onempty"); 
                }
            } else {
                model = new DefaultSuggestModel(); 
            }
            
            model.setProvider(new SuggestModelProvider()); 
            refreshTextValue(); 
        } catch(Throwable t) {
            ErrorDialog.show(t, this); 
        }
        
        try {
            String visibleWhen = getVisibleWhen(); 
            if (visibleWhen != null && visibleWhen.length() > 0) { 
                Object exprBean = createExpressionBean(getValue()); 
                boolean b = false; 
                try { 
                    b = UIControlUtil.evaluateExprBoolean(exprBean, visibleWhen);
                } catch(Throwable t) {
                    t.printStackTrace();
                } 
                setVisible(b); 
            }             
        } catch(Throwable t) {;} 
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    } 
    
    private Object createExpressionBean(Object itemBean) { 
        ExprBeanSupport beanSupport = new ExprBeanSupport(binding.getBean());
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation "> 
    
    public ControlProperty getControlProperty() {
        if (property == null) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font font) {
        getControlProperty().setCaptionFont(font);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
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
    public void setShowCaption(boolean showCaption) {
        getControlProperty().setShowCaption(showCaption);
    }
        
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }
    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Validatable implementation  ">
    
    private ActionMessage actionMessage;
    
    public ActionMessage getActionMessage() { 
        if (actionMessage == null) {
            actionMessage = new ActionMessage(); 
        }
        return actionMessage; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    } 
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
    } 

    public boolean isRequired() { 
        return getControlProperty().isRequired(); 
    } 
    public void setRequired(boolean required) {
        getControlProperty().setRequired(required);
    }

    public void validateInput() {
        getActionMessage().clearMessages();
        getControlProperty().setErrorMessage(null);
        Object value = getBeanValue();
        if (ValueUtil.isEmpty(value)) {
            if (isRequired()) { 
                getActionMessage().addMessage("1001", "{0} is required.", new Object[]{getCaption()});
            } 
        } 
        
        if (getActionMessage().hasMessages()) {
            getControlProperty().setErrorMessage(getActionMessage().toString());
        }        
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods ">
    
    protected void onfocusGained(FocusEvent e) {
        enable_search = true;
    }

    protected void onfocusLost(FocusEvent e) {
        if (e.isTemporary()) return; 

        enable_search = false; 
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                getPopup().setVisible(false); 
            }
        });
    }
    
    protected void onescape() {
        if (getPopup().isVisible()) { 
            getPopup().setVisible(false); 
        } 
    }
    
    protected void onmoveDown() {
        if (!enable_search) return;
        if (getPopup().isVisible() && getPopup().isShowing()) { 
            getPopup().moveDown(); 
        }
    }
    
    protected void onmoveUp() {
        if (!enable_search) return;
        if (getPopup().isVisible() && getPopup().isShowing()) { 
            getPopup().moveUp(); 
        } 
    }    

    protected void onactionPerformed(ActionEvent e) {
        if (!enable_search) return;
        if (model == null) return;
        
        if (getPopup().isVisible()) {
            SuggestItem si = getPopup().getSuggestedItem(); 
            onselectImpl(si); 
        } 
    } 
    
    private void onselectImpl(SuggestItem si) {
        try { 
            if (si == null) return;
            
            Object outcome = null;
            Object userobj = si.getUserObject(); 
            if (onselectHandler != null) { 
                outcome = onselectHandler.invoke(userobj);
            } 
            
            refreshTextValue(userobj, true); 
            getPopup().setVisible(false); 
            
            if (UIConstants.SuggestTypes.LOOKUP.equals(getType())) { 
                transferFocus(); 
            } 
            
            EventQueue.invokeLater(new ProcessOutcome(outcome));
        } catch(Throwable t) { 
            ErrorDialog.show(t, this); 
        } 
    }
    
    private Object getBeanValue() {
        Binding binding = getBinding();
        Object bean = (binding == null? null: binding.getBean());
        
        String name = getName();
        if (name != null && name.length() > 0) {
            return UIControlUtil.getBeanValue(bean, name); 
        } else { 
            return null; 
        }
    }
    
    private void refreshTextValue() {
        refreshTextValue(getBeanValue());
    }
        
    private void refreshTextValue(Object value) {
        refreshTextValue(value, false); 
    }
    
    private void refreshTextValue(Object value, boolean makeDirty) {
        setValue(value);
        
        if (value != null && value.getClass().getName().startsWith("java.lang.")) {
            //do nothing, no expression to apply 
        } else { 
            String expression = getExpression();
            if (expression != null && expression.length() > 0) {
                Object exprBean = createExpressionBean(value);
                value = UIControlUtil.evaluateExpr(exprBean, expression);
            }
        }
        document.loadValue(value, makeDirty); 
        currentTextValue = (value == null? null: value.toString()); 
    }
    
    private void onupdate_document() {
        if (Beans.isDesignTime()) return;
        if (!enable_search) return;
        if (!document.isDirty()) return;
        
        Map params = new HashMap();
        params.put("searchtext", getText()); 
        params.put("_start", 0);
        params.put("_limit", (model == null? 10: model.getRows())); 
        TimerManager.getInstance().schedule(new LookupTask(params), 300); 
    }
    
    // </editor-fold>    
 
    // <editor-fold defaultstate="collapsed" desc=" LookupTask ">
        
    private class LookupTask implements Runnable 
    {
        XSuggest root = XSuggest.this;
        
        private String searchtext; 
        private Map params;
        
        LookupTask(Map params) {
            this.params = params; 
            this.searchtext = (params == null? null: (String)params.get("searchtext")); 
        }
        
        public void run() {
            String str = root.getText();
            if (str == null || str.length() == 0) {
                getPopup().setVisible(false); 
                return;
            }
            if (!str.equals(searchtext)) return;
            
            List list = (root.model == null? null: root.model.fetchList(params)); 
            EventQueue.invokeLater(new ResultDataLoader(list)); 
        }
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultSuggestModel ">
    
    private class DefaultSuggestModel extends SuggestModel { 
        XSuggest root = XSuggest.this; 
    } 

    private class CallbackHandler {
        
        private ObjectProxy.MetaInfo meta; 
        private String name;
        
        CallbackHandler(ObjectProxy.MetaInfo meta, String name) {
            this.meta = meta; 
            this.name = name; 
        }
        
        Object invoke(Object value) {
            Method method = meta.getMethod(name, new Class[]{Object[].class}); 
            if (method != null) {
                Object[] args = (value==null? new Object[]{}: new Object[]{value});
                return meta.invoke(method, new Object[]{args}); 
            }
            
            method = meta.getMethod(name, new Class[]{Object.class}); 
            if (method != null) {
                return meta.invoke(method, new Object[]{value}); 
            }
            
            method = meta.getMethod(name, new Class[]{}); 
            if (method != null) {
                return meta.invoke(method, new Object[]{}); 
            }
            return null; 
        }
    }    
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" SuggestModelProvider ">
    
    private class SuggestModelProvider implements SuggestModel.Provider
    {
        XSuggest root = XSuggest.this;
        
        public Object getBinding() {
            return root.getBinding(); 
        } 
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" ResultDataLoader ">
    
    private SuggestPopup jpopup;
    
    private SuggestPopup getPopup() {
        if (jpopup == null) {
            jpopup = new SuggestPopup(this);
            jpopup.add(new SuggestPopup.SelectionListener(){
                public void onselect(SuggestItem item) {
                    onselectImpl(item); 
                }
            });
        }
        return jpopup;
    }
    
    private void showPopup() {
        if (!enable_search) return;
        
        Rectangle rect = getBounds();
        Point point = getLocationOnScreen(); 
        SuggestPopup popup = getPopup(); 
        popup.pack();
        popup.show(this, 0, rect.height); 
    }

    private class ResultDataLoader implements Runnable 
    {
        XSuggest root = XSuggest.this;
        
        private List result;
        
        ResultDataLoader(List result) {
            this.result = result;
        }
        
        public void run() {
            SuggestPopup popup = root.getPopup(); 
            if (result == null || result.isEmpty()) {
                popup.setVisible(false); 
                return; 
            } 
            
            String expression = getItemExpression();
            List<SuggestItem> items = new ArrayList();
            for (int i=0; i<popup.getRowSize(); i++) { 
                try { 
                    Object o = result.get(i); 
                    if (expression == null || expression.length() == 0) { 
                        items.add(new SuggestItem(o, o.toString())); 
                    } else { 
                        Object exprBean = createExpressionBean(o); 
                        Object caption = UIControlUtil.evaluateExpr(exprBean, expression); 
                        items.add(new SuggestItem(o, (caption == null? "": caption.toString()))); 
                    } 
                    
                } catch(Throwable t) {
                    break; 
                } 
            }
            if (items.isEmpty()) {
                popup.setVisible(false);
                return;
            }
            popup.setData(items); 
            showPopup();
        }
    }
    
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" MouseListenerImpl ">
    
    private class MouseListenerImpl implements MouseListener 
    {
        XSuggest root = XSuggest.this;

        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}        
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        
        public void mouseClicked(MouseEvent e) {
            if (!root.enable_search) return;
            if (!root.document.isDirty()) return;
            
            String text = root.getText();
            if (text == null || text.length() == 0) return; 
            
            Component[] comps = root.getPopup().getComponents(); 
            if (comps == null || comps.length == 0) return;
            
            root.showPopup(); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" InputVerifierImpl ">
    
    private class InputVerifierImpl extends InputVerifier 
    {
        XSuggest root = XSuggest.this;

        public boolean verify(JComponent input) {
            if (Beans.isDesignTime()) return true;
            
            if (root.document.isDirty()) {
                try {
                    boolean typeBasic = UIConstants.SuggestTypes.BASIC.equals(root.getType()); 
                    if (typeBasic) root.setValue(root.getText()); 

                    UIInputUtil.updateBeanValue(root, true, false); 
                } catch(Throwable t) { 
                    t.printStackTrace(); 
                } 
            } 
            
            try { 
                root.refreshTextValue(); 
            } catch(Throwable t) {;} 
            
            return true; 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" EventHandlerImpl ">
    
    private class EventHandlerImpl implements UIInputUtil.EventHandler 
    {
        XSuggest root = XSuggest.this;
        
        @Override
        public boolean allowCustomUpdate() {
            if (root.document.isDirty()) {
                if (root.onemptyHandler == null) return false; 
                
                String text = root.getText();
                if ((text == null || text.length() == 0)) {
                    return true; 
                } else {
                    return false; 
                }
            } else {
                return false; 
            } 
        }

        @Override
        public void customUpdate() {
            if (root.onemptyHandler == null) return;
            if (!root.document.isDirty()) return; 
            
            String text = root.getText();
            if (text == null || text.length() == 0) {
                onemptyHandler.invoke(null); 
            } 
        }        
        
        @Override
        public void afterUpdate(Object value) {
        }
    } 
    
    private class ProcessOutcome implements Runnable {
        
        XSuggest root = XSuggest.this;
        private Object result;
        
        ProcessOutcome(Object result) {
            this.result = result; 
        }
        
        @Override
        public void run() {
            if (result instanceof Opener) {
                Binding binding = root.getBinding(); 
                if (binding != null) {
                    Opener opener = (Opener)result;
                    String target = opener.getTarget();
                    if ((target+"").matches("window|_window|popup|_popup")) {
                        //do nothing 
                    } else {
                        opener.setTarget("popup");
                    }
                    binding.fireNavigation(opener);
                }
            }
        }
    }
    
    // </editor-fold>
    
}
