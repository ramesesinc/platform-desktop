package com.rameses.rcp.framework;

import com.rameses.common.ExpressionResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.platform.interfaces.SubWindowListener;
import com.rameses.rcp.common.StyleRule;
import com.rameses.rcp.control.XButton;
import com.rameses.rcp.ui.NonStylable;
import com.rameses.rcp.ui.UIComposite;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.common.MethodResolver;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.ViewContext;
import com.rameses.rcp.annotations.Close;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.Validator;
import com.rameses.rcp.common.ValidatorEvent;
import com.rameses.rcp.common.ViewHandler;
import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.UIFocusableContainer;
import com.rameses.util.BreakException;
import com.rameses.util.BusinessException;
import com.rameses.util.ValueUtil;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 *
 * @author jaycverg
 */
public class Binding 
{
    private static final String CHANGE_LOG_PREFIX_KEY = "CHANGE_LOG_PREFIX_KEY";
    
    public final Helper Utils = new Helper();  
    
    private Object bean;
    
    //reference to the owner panel(UIViewPanel)
    private UIViewPanel owner;
    
    /**
     * this is used when referencing controller properties
     * such as controller name, id, and title
     */
    private UIController controller;
    
    /**
     * index of all controls in this binding
     * this is used when finding a control by name
     */
    private Map<String, UIControl> controlsIndex = new Hashtable();
    
    /**
     * index of all controls in this binding which names duplicate (i.e) radio button controls
     * this is used when finding controls by name
     */
    private Map<String, List> controlsListIndex = new Hashtable();
    
    /**
     * - reference of all controls that can aquire default focus
     *   when the window is shown or during page navigation
     * - this reference contains UIInput and UIFocusableContainer only
     */
    private List<UIControl> focusableControls = new ArrayList();
    
    private List<UIControl> controls = new ArrayList();
    private Map<String, Set<UIControl>> depends = new Hashtable();
    private List<Validatable> validatables = new ArrayList();
    private List<BindingListener> listeners = new ArrayList();
    private ChangeLog changeLog = new ChangeLog();
    private XButton defaultButton;
    private StyleRule[] styleRules;
    
    //page binding flags
    private List<UIControl> _depends = new ArrayList();
    private boolean _initialized = false;
    
    private KeyListener changeLogKeySupport = new ChangeLogKeySupport();
    
    
    //annotation support
    private boolean annotationScanIsDone;
    private Field bindingField;
    private Field changeLogField;
    
    private String closeMethod;
    private String activateMethod; 
            
    private List<Validator> validators = new ArrayList();
    
    /**
     * can be used by UIControls to store values
     * related to this Binding context
     */
    private Map properties = new HashMap();
    
    /**
     * EventManager is used to register any event listener for a specified UIControl name
     */
    private EventManager eventManager = new EventManager();
    
    /**
     * Reference to the ViewContext
     */
    private ViewContext viewContext;
    
    //focus flag
    private String focusComponentName;
    
    /*
     *  Stores the ActionHandler interfaces and notifies the handlers 
     *  everytime a command button is being executed. This is very useful 
     *  for editor components like TextArea and FormulaEditor. 
     */
    private ActionHandlerSupport actionSupport; 
    
    public Binding() { 
        this(null);
    }
    
    public Binding(UIViewPanel owner) {
        setOwner(owner);
    }
    
    public UIViewPanel getOwner() { return owner; }    
    public void setOwner(UIViewPanel owner) { 
        this.owner = owner; 
    }
    
    public void addValidator(Validator validator) 
    {
        if (validators.contains(validator))
            validators.add(validator);
    }
    
    public boolean removeValidator(Validator validator) {
        return validators.remove(validator);
    }
    
    public EventManager getEventManager() { return eventManager; }
    
    public ActionHandlerSupport getActionHandlerSupport() 
    {
        if (actionSupport == null) actionSupport = new ActionHandlerSupport();
        
        return actionSupport; 
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
        if (this.controlsIndex != null) this.controlsIndex.clear();
        if (this.controlsListIndex != null) this.controlsListIndex.clear();
        if (this._depends != null) this._depends.clear();
        if (this.depends != null) this.depends.clear();
        if (this.focusableControls != null) this.focusableControls.clear();
        if (this.listeners != null) this.listeners.clear();
        if (this.viewHandlers != null) this.viewHandlers.clear(); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc="  control binding  ">
    
    private ControlEventSupport support = new ControlEventSupport();
    
    public void register( UIControl control ) 
    {
        controls.add( control );
        if ( control.getDepends() != null ) _depends.add( control );
        
        Validatable vd = (Validatable) control.getClientProperty(Validatable.class); 
        if ( vd != null ) { 
            validatables.add(vd);
        } else if ( control instanceof Validatable ) { 
            validatables.add( (Validatable) control );
        } 
        
        if ( control instanceof XButton && defaultButton == null ) {
            XButton btn = (XButton) control;
            if ( btn.isDefaultCommand() ) defaultButton = btn;
        } 
        
        if ( !ValueUtil.isEmpty(control.getName()) ) {
            String cname =  control.getName();
            if ( controlsIndex.containsKey(cname) ) 
            {
                List list = controlsListIndex.get(cname);
                if ( list == null ) 
                {
                    list = new ArrayList();
                    list.add(controlsIndex.get(cname));
                    controlsListIndex.put(cname, list);
                }
                list.add(control);
            }
            controlsIndex.put(cname, control);
        }
        
        //for control event management support
        if ( control instanceof Component ) {
            Component c = (Component) control;
            c.addMouseListener(support);
            c.addKeyListener(support);
        }
    }
    
    public void unregister( UIControl control ) {
        controls.remove( control );
        if( control instanceof Validatable ) {
            validatables.remove( (Validatable)control );
        }
        if( !ValueUtil.isEmpty(control.getName()) ) {
            String cname = control.getName();
            controlsIndex.remove(cname);
            if( controlsListIndex.containsKey(cname) ) {
                List list = controlsListIndex.get(cname);
                list.remove(control);
                if( list.size() == 0 ) controlsListIndex.remove(cname);
            }
        }
        
        //for control event management support
        if ( control instanceof Component ) {
            Component c = (Component) control;
            c.removeMouseListener(support);
            c.removeKeyListener(support);
        }
    }
    
    public void updateDepends( UIControl uic, String[] vals ) {
        if ( controls.contains( uic )) {
            Iterator<Set<UIControl>> itr = depends.values().iterator(); 
            while (itr.hasNext()) { 
                itr.next().remove( uic ); 
            } 
            
            if ( vals == null ) return;
            
            for ( String skey : vals ) {
                Set<UIControl> sets = depends.get( skey );
                if ( sets == null ) {
                    sets = new HashSet<UIControl>(); 
                    depends.put( skey, sets ); 
                }
                sets.add( uic ); 
            } 
        } 
    }
    
    public void init() 
    {
        if ( _initialized ) return;
        
        _initialized = true;
        Collections.sort( controls );
        Collections.sort( validatables );
        for ( UIControl u : controls ) {
            //index all default focusable controls
            if (u instanceof UIInput || u instanceof UIFocusableContainer) {
                focusableControls.add( u );
            }
            
            String cname = u.getName();
            if (cname == null || cname.trim().length() == 0) continue;
            
            for (UIControl c : _depends) {
                if ( u == c ) continue;
                
                for (String s : c.getDepends()) {
                    if (cname.matches(s)) {
                        if (!depends.containsKey(cname)) { 
                            depends.put(cname, new HashSet<UIControl>());
                        } 
                        depends.get(cname).add(c);
                    }
                }
            } 
        }
        
        //verify all dependency controls have been registered
        for (UIControl c : _depends) {
            String cname = c.getName();
            for (String s : c.getDepends()) {
                if (s.equals(cname)) continue;
                
                if (!depends.containsKey(s)) { 
                    depends.put(s, new HashSet<UIControl>());
                } 
                Set<UIControl> sets = depends.get(s);
                if (!sets.contains(c)) sets.add(c);
            }
        }        
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  control update/refresh  "> 

    public void notifyDepends( final UIControl u ) {
        notifyDepends(u, u.getName()); 
    }

    public void notifyDepends( final UIControl u, final String name ) {
        notifyDepends(u, name, true); 
    }
    
    public void notifyDepends( final UIControl u, final String name, boolean invokeLater ) 
    {
        Runnable process = new Runnable() {
            public void run() {
                try {
                    doNotifyDepends(u, name);
                } catch(Exception e) { 
                    if (ClientContext.getCurrentContext().isDebugMode()) 
                        e.printStackTrace();
                }
            }
        };
        
        if (invokeLater)
            EventQueue.invokeLater(process);
        else 
            process.run(); 
    } 
    
    public void notifyDepends( String name ) {
        if (name == null || name.length() == 0) return;
        
        Set<UIControl> refreshed = new HashSet();
        if (depends.containsKey(name)) {
            for (UIControl uic : depends.get(name)) {
                _doRefresh(uic, refreshed);
            }
        }
        refreshed.clear();
        refreshed = null;        
        
        //find which control 
        UIControl selUI = null; 
        for (UIControl uic: controls) {
            if (name.equals(uic.getName())) {
                selUI = uic;
                break; 
            } 
        } 
        
        if (selUI != null) {
            for (BindingListener bl : listeners) {
                bl.notifyDepends(selUI, this);
            }
        }
    } 
        
    private void doNotifyDepends( UIControl u, String name ) 
    {
        Set<UIControl> refreshed = new HashSet();
        if ( !ValueUtil.isEmpty(name) && depends.containsKey(name) ) {
            UIControl[] arrs = depends.get(name).toArray(new UIControl[]{});
            for ( UIControl uu : arrs ) {  
                if ( uu == null ) { continue; } 
                
                _doRefresh( uu, refreshed );
            } 
        }
        refreshed.clear();
        refreshed = null;
        
        for (BindingListener bl : listeners) {
            bl.notifyDepends(u, this);
        }
        
        //focus component specified
        if( focusComponentName != null ) {
            UIControl c = controlsIndex.get(focusComponentName);
            if ( c != null ) {
                Component comp = (Component) c;
                if (comp.isEnabled() && comp.isFocusable()) 
                    comp.requestFocusInWindow(); 
            }
            focusComponentName = null;
        } 
        
        JComponent jcomp = getOwner();
        if ( jcomp != null ) {
            jcomp.revalidate();
            jcomp.repaint(); 
        }
    }
    
    /**
     *  refreshes all UIControls in this binding
     */
    public void refresh() {
        refresh(null);
    }
    
    /**
     *  accepts regex expression of filednames
     *  sample usage: refresh("field1|field2|entity.*")
     */
    public void refresh(String regEx) {
        refresh(regEx, false);
    }
    
    public void refresh(String regEx, boolean async) {
        if (async) {
            new AsyncRefreshWorker(regEx).run();
        } else {
            refreshImpl(regEx); 
        }
    }
    
    private void refreshImpl(String regEx) {
        Set<UIControl> refreshed = new HashSet();
        for ( int i=0, len=controls.size(); i<len; i++ ) {
            UIControl uu = getUIControl( i ); 
            if ( uu == null ) { continue; } 
            
            String name = uu.getName();
            if ( regEx != null && name != null && !name.matches(regEx) ){
                continue;
            }
            
            _doRefresh( uu, refreshed );
        }
        refreshed.clear();
        refreshed = null;
        
        for (BindingListener bl : listeners) {
            bl.refresh(regEx);
        }
        
        if ( viewContext instanceof UIControllerPanel ) {
            ((UIControllerPanel) viewContext).attachDefaultButton();
        }
        
        //focus component specified
        if( focusComponentName != null ) {
            UIControl c = controlsIndex.get(focusComponentName);
            if ( c != null ) {
                Component comp = (Component) c;
                comp.requestFocusInWindow();
            }
            focusComponentName = null;
        }        
    }
    
    private UIControl getUIControl( int index ) {
        try {
            return controls.get(index); 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    private void _doRefresh( UIControl u, Set refreshed ) { 
        _doRefresh(u, refreshed, u.getName()); 
    } 
    
    private void _doRefresh( UIControl u, Set refreshed, String name ) 
    {
        if ( refreshed.add(u) ) {
            if ( u instanceof UIComposite ) {
                UIComposite comp = (UIComposite)u;
                if ( comp.isDynamic() ) {
                    JComponent jc = (JComponent) comp;
                    //do not reload on first refresh since load is first called
                    //this should only be called on the next refresh
                    if ( jc.getClientProperty(getClass() + "REFRESHED") != null ) { 
                        comp.reload(); 
                    } else { 
                        jc.putClientProperty(getClass() + "REFRESHED", true); 
                    } 
                } 
                
                //apply style rules to children
                for (UIControl uic: comp.getControls()) {
                    applyStyle(uic);
                }
                //apply style rules to parent
                applyStyle(u); 
            } else { 
                applyStyle(u); 
            } 
            
            u.refresh(); 
            
            try { 
                Object o = u.getClientProperty(UIHandler.class); 
                if ( o instanceof UIHandler ) { 
                    ((UIHandler) o).refresh( new UIEvent(u, u.getBinding(), u.getName()) ); 
                } 
            } catch(Throwable t) { 
                System.out.println("[ERROR] " + t.getMessage()); 
                if (ClientContext.getCurrentContext().isDebugMode()) { 
                    t.printStackTrace(); 
                } 
            } 
            
            if ( !ValueUtil.isEmpty(name) && depends.containsKey(name) ) { 
                UIControl[] arrs = depends.get(name).toArray(new UIControl[]{});
                for ( UIControl uu : arrs ) {  
                    if ( uu != null ) { 
                        _doRefresh( uu, refreshed ); 
                    } 
                } 
            } 
        } 
    }
    
    public final void applyStyle(UIControl u) {
        if ( styleRules == null ) return;
        if ( u instanceof NonStylable ) return;
        
        String name = u.getName();
        if( name == null ) name = "_any_name";
        
        //apply style rules
        for(StyleRule r : styleRules) {
            String pattern = r.getPattern();
            String rule = r.getExpression();
            
            //test expression
            boolean applyStyles = false;
            if ( rule!=null && name.matches(pattern) )
            {
                try {
                    applyStyles = ExpressionResolver.getInstance().evalBoolean(rule, getBean());
                } catch (Exception ign){
                    System.out.println("STYLE RULE ERROR: " + ign.getMessage());
                }
            }
            if ( applyStyles ) {
                ControlSupport.setStyles( r.getProperties(), (Component) u );
            }
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  utility methods  ">
    
    public void validate() {
        ActionMessage am = new ActionMessage();
        validate(am);
        
        if (am.hasMessages()) {
            if (am.getSource() != null) { 
                am.getSource().requestFocusInWindow();
            } 
            throw new BusinessException(am.toString());
        }

        getValidatorSupport().fireBeanValidators( getBean() ); 
        
        ValidatorEvent evt = new ValidatorEvent(this);
        validateBean(evt);
        
        if (evt.hasMessages()) {
            if (evt.getSource() != null) 
                evt.getSource().requestFocusInWindow();
            
            throw new BusinessException(evt.toString());
        }
    }
    
    public void validate(ActionMessage actionMessage) 
    {
        UIControlUtil.validate(validatables, actionMessage);
        
        for (BindingListener bl: listeners) bl.validate(actionMessage, this);
    }
    
    public void validateBean(ValidatorEvent evt) 
    {
        for (Validator v: validators) v.validate(evt);         
        for (BindingListener bl: listeners) bl.validateBean(evt); 
    }
    
    public void formCommit() 
    {
        for ( UIControl u: focusableControls ) 
        {
            Component comp = (Component) u;
            if ( !comp.isEnabled() || !comp.isShowing() ) continue;
            if ( u instanceof UIInput && ((UIInput) u).isReadonly() ) continue;
            if ( u instanceof JTextComponent && !((JTextComponent) u).isEditable() ) continue;
            
            if ( u instanceof UIComposite ) 
            {
                UIComposite uc = (UIComposite) u;
                for (UIControl uu: uc.getControls()) doCommit(uu);
                
            } 
            else {
                doCommit(u);
            }
        }
        
        for (BindingListener bl : listeners) bl.formCommit();
    }
    
    private void doCommit(UIControl u) 
    {
        if ( !(u instanceof UIInput) || ValueUtil.isEmpty(u.getName()) ) return;
        
        UIInput ui = (UIInput) u;
        if ( ui.isImmediate() || ui.isReadonly() ) return;
        
        Component c = (Component) ui;
        if ( !c.isEnabled() || !c.isFocusable() || !c.isShowing() ) return;
        
        //do not validate components which are hidden
        //and not yet attached to a panel
        if ( c.getParent() == null ) return;
        
        Object compValue = ui.getValue();
        Object beanValue = UIControlUtil.getBeanValue(ui);
        if ( !ValueUtil.isEqual(compValue, beanValue) ) 
            UIInputUtil.updateBeanValue(ui);
    }
    
    public void update() 
    {
        //clear changeLog
        if ( changeLog != null ) changeLog.clear();
        
        for (BindingListener bl : listeners) bl.update();
    }
    
    public void addBindingListener(BindingListener listener) 
    {
        if (listener != null && !listeners.contains(listener))
            listeners.add(listener);
    }
    
    public void removeListener(BindingListener listener) {
        if (listener != null) listeners.remove(listener);
    }
    
    public void activate() {        
        try {
            if (activateMethod == null) { return; } 
            
            MethodResolver mr = MethodResolver.getInstance();
            mr.invoke(bean, activateMethod, new Class[]{}, new Object[]{});
        } catch(Throwable e) { 
            e.printStackTrace(); 
        } 
    } 
    
    public boolean close() {
        if (closeMethod == null) return true;
        
        try {
            MethodResolver mr = MethodResolver.getInstance();
            Object o = mr.invoke(bean, closeMethod, new Class[]{}, new Object[]{});
            if ( "false".equals(o+"") ) return false;
        } catch(Throwable e) {
            e.printStackTrace();
        } 
        
        return true;
    }
    
    public boolean focusFirstInput() {
        //focus first UIInput that is not disabled/readonly
        for (UIControl u: focusableControls ) {
            if ( u instanceof UIFocusableContainer ) {
                UIFocusableContainer uis = (UIFocusableContainer) u;
                if ( uis.focusFirstInput() ) return true;
                
            } else if ( u instanceof UIInput ) {
                UIInput ui = (UIInput) u;
                Component comp = (Component) ui;
                if (comp.isEnabled() && comp.isFocusable()) { 
                    return comp.requestFocusInWindow();
                }
            }
        }
        return false;
    }
    
    /**
     * fireNavigation can be used to programmatically trigger the navigation handler
     * from the controller's code bean
     */
    public void fireNavigation(Object outcome) {
        fireNavigation(outcome, "parent");
    }
    
    public void fireNavigation(final Object outcome, final String target) {
        fireNavigation(outcome, target, true); 
    }
    
    public void fireNavigation(final Object outcome, final String target, boolean invokeLater) {
        Runnable process = new Runnable() {
            public void run() { 
                doFireNavigation(outcome, target); 
            } 
        };
        if (invokeLater)
            EventQueue.invokeLater(process); 
        else 
            process.run(); 
    }
    
    private void doFireNavigation(Object outcome, String target) { 
        JComponent jcomp = null; 
        try { 
            if (outcome == null) return;

            jcomp = getOwner(); 
            if ((outcome instanceof String) || (outcome instanceof Opener)) {
                // valid outcome value 
            } else {
                throw new Exception("outcome must be a String or Opener"); 
            }
            
            
            ClientContext ctx = ClientContext.getCurrentContext();
            NavigationHandler handler = ctx.getNavigationHandler();
            if (handler != null) { 
                NavigatablePanel navPanel = UIControlUtil.getParentPanel(jcomp, target);
                if (navPanel == null) {
                    Object viewctx = getViewContext(); 
                    if (viewctx instanceof NavigatablePanel) { 
                        navPanel = (NavigatablePanel) viewctx; 
                    } 
                } 
                
                handler.navigate(navPanel, null, outcome);
            }
        } catch(BreakException be) {
            //do nothing 
        } catch(Exception e) {
            ClientContext.getCurrentContext().getPlatform().showError(jcomp, e);
        }
    }
    
    /**
     * focuses a UIControl from a code bean
     * This is helpful when you do the validation from the code and
     * you want to focus a control after displaying an error message.
     * This mehod just keep the control's name to be focused which is
     * fired after all the controls had been refreshed
     */
    public void focus(String name) {
        focusComponentName = name;
    } 
    
    public void requestFocus(String name) {
        if (name == null) return; 

        UIControl c = controlsIndex.get(name);
        if (c == null) return;

        Component comp = (Component) c;
        if (comp.isEnabled() && comp.isFocusable()) 
            comp.requestFocusInWindow(); 
    } 
    
    /**
     * returns the UIControl w/ the specified name
     */
    public UIControl find(String name) {
        if ( name == null ) return null;
        
        return controlsIndex.get(name);
    }
    
    /**
     * returns the List of UIControls w/ the same name as specified
     */
    public List<UIControl> findList(String name) {
        List list = controlsListIndex.get(name);
        if( list != null ) return list;
                
        list = new ArrayList();
        UIControl u = controlsIndex.get(name);
        if( u != null ) list.add(u);
        return list;
    }
    
    /**
     *
     */
    public void setTitle(String title) {
        if ( viewContext != null && viewContext.getSubWindow() != null )
            viewContext.getSubWindow().setTitle(title);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Helper ">
    
    public void bind( UIControl uic ) {
        if ( uic != null ) { 
            uic.setBinding( this ); 
            
            try { 
                Object o = uic.getClientProperty(UIHandler.class); 
                if ( o instanceof UIHandler ) { 
                    ((UIHandler) o).bind( new UIEvent(uic, uic.getBinding(), uic.getName()) ); 
                } 
            } catch(Throwable t) { 
                System.out.println("[ERROR] " + t.getMessage()); 
                if (ClientContext.getCurrentContext().isDebugMode()) { 
                    t.printStackTrace(); 
                } 
            } 
        } 
    } 
    public void unbind( UIControl uic ) {
        if ( uic != null ) { 
            try { 
                uic.setBinding( null ); 
            } catch(Throwable t) {
                //do nothing 
            }
            
            try { 
                Object o = uic.getClientProperty(UIHandler.class); 
                if ( o instanceof UIHandler ) { 
                    ((UIHandler) o).unbind( new UIEvent(uic, uic.getBinding(), uic.getName()) ); 
                } 
            } catch(Throwable t) { 
                System.out.println("[ERROR] " + t.getMessage()); 
                if (ClientContext.getCurrentContext().isDebugMode()) { 
                    t.printStackTrace(); 
                } 
            } 
        }
    }
    
    private class RequestFocusTask implements Runnable {

        Component comp; 
        
        RequestFocusTask( Component comp ) { 
            this.comp = comp; 
        }
        
        public void run() { 
            if ( comp != null && comp.isEnabled() && comp.isFocusable() ) {
                comp.requestFocusInWindow(); 
            }
        }
        
    }    
    
    public class Helper { 
        Binding root = Binding.this;
        
        public void bindControls( Container cont, Binding binding ) {
            if ( cont == null ) return; 

            for ( Component c: cont.getComponents()) { 
                if ( c instanceof UIControl ) { 
                    UIControl uic = (UIControl)c; 
                    if ( binding == null ) {
                        uic.setBinding( null ); 
                        binding.unregister( uic );
                    } else { 
                        binding.bind( uic ); 
                        binding.register( uic ); 
                    }

                    if( c instanceof ControlContainer && ((ControlContainer) c).isHasNonDynamicContents() && c instanceof Container ) {
                        bindControls( (Container)c, binding );
                    } 
                } else if( c instanceof Container ) {
                    bindControls( (Container)c, binding );
                }
            }
        }        
        
        public void focusComponent( Component comp ) {  
            EventQueue.invokeLater(new RequestFocusTask( comp )); 
        }
        
        public boolean isChild( Container cont, Component child ) {
            if ( cont == null || child == null ) return false; 
            
            boolean found = false; 
            Container parent = child.getParent(); 
            while ( parent != null ) {
                if ( cont.equals( parent )) { 
                    found = true;
                    break; 
                    
                } else {
                    parent = parent.getParent(); 
                }
            } 
            return found; 
        }
        
        public void validateInput( ActionMessage am ) { 
            UIControlUtil.validate( validatables, am ); 
            for ( BindingListener bl: listeners ) {
                bl.validate( am, root ); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  getters/setters  ">
    
    public boolean isInitialized() {
        return _initialized;
    }
    
    public UIController getController() {
        return controller;
    }
    
    public void setController(UIController controller) {
        this.controller = controller;
//        if ( bean == null ) {
//            setBean( controller.getCodeBean() );
//        }
    }
    
    public Object getBean() {
        return bean;
    }
    
    //-- this is called the first time the bean is injected
    public void setBean(Object bean) {
        this.bean = bean;
        initAnnotatedFields( bean, bean.getClass() );
        //initAnnotatedMethods( bean, bean.getClass() );        
        Method m = findAnnotatedMethod(bean.getClass(), com.rameses.rcp.annotations.Close.class); 
        if ( m != null ) { closeMethod = m.getName(); } 
        
        m = findAnnotatedMethod(bean.getClass(), com.rameses.rcp.annotations.Activate.class); 
        if ( m != null ) { activateMethod = m.getName(); } 
        
        _load();
    }
    
    //-- this is called when the controller changes page
    //-- (after a Navigation handler fires navigation)
    public void reinjectAnnotations() {
        initAnnotatedFields( bean, bean.getClass() );
    }
    
    private void _load() {
        for ( UIControl c: controls ) {
            c.load();
        }
    }
    
    public ChangeLog getChangeLog() {
        return changeLog;
    }
    
    public void setChangeLog(ChangeLog changeLog) {
        this.changeLog = changeLog;
    }
    
    public XButton getDefaultButton() {
        return defaultButton;
    }
    
    public void setDefaultButton(XButton defaultButton) {
        this.defaultButton = defaultButton;
    }
    
    public StyleRule[] getStyleRules() {
        return styleRules;
    }
    
    public void setStyleRules(StyleRule[] styleRules) {
        this.styleRules = styleRules;
    }
    
    public Map getProperties() {
        return properties;
    }
    
    public void setProperties(Map properties) {
        this.properties = properties;
    }
    
    public ViewContext getViewContext() {
        return viewContext;
    }
    
    public void setViewContext(ViewContext viewContext) {
        this.viewContext = viewContext;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  helper methods  ">
    
    private DependencyInjector injector;    
    private DependencyInjector getInjector() {
        if (injector == null) {
            injector = new DependencyInjector();
        }
        return injector;
    }
    
    private void initAnnotatedFields( Object o, Class clazz ) 
    {
        if (o == null) return;
        
        if (annotationScanIsDone) {
            boolean accessible;
            if (bindingField != null) {
                accessible = bindingField.isAccessible();
                bindingField.setAccessible(true);
                try {
                    bindingField.set(o, Binding.this );
                } catch(Exception ex) {
                    System.out.println("ERROR injecting @Binding "  + ex.getMessage() );
                }
                bindingField.setAccessible(accessible);
            }
            
            if (changeLogField != null) {
                accessible = changeLogField.isAccessible();
                changeLogField.setAccessible(true);
                try {
                    ChangeLog cl = Binding.this.getChangeLog();
                    String[] prefixes = (String[]) getProperties().get(CHANGE_LOG_PREFIX_KEY);
                    if (prefixes != null) {
                        for (String s: prefixes) {
                            cl.getPrefix().add(s); 
                        } 
                    }
                } catch(Throwable ex) {
                    System.out.println("ERROR injecting @Binding "  + ex.getMessage() );
                }
                changeLogField.setAccessible(accessible);
            }
            return;
        }
        
        //check for field annotations
        for( Field f: clazz.getDeclaredFields() ) {
            boolean accessible = f.isAccessible();
            if (f.isAnnotationPresent(com.rameses.rcp.annotations.Binding.class)) {
                com.rameses.rcp.annotations.Binding b = f.getAnnotation(com.rameses.rcp.annotations.Binding.class);
                String[] values = b.validators();
                PropertyResolver res = PropertyResolver.getInstance();
                for (String s: values) {
                    try {
                        Object v = res.getProperty(getBean(), s);
                        if ( v instanceof Validator ) 
                            validators.add( (Validator) v );
                    } catch(Throwable e) {
                        e.printStackTrace();
                    }
                }
                
                f.setAccessible(true);
                try {
                    f.set(o, Binding.this );
                } catch(Throwable t) {
                    System.out.println("ERROR injecting @Binding caused by " + t.getClass().getName() + ": " + t.getMessage() );
                }
                
                f.setAccessible(accessible);                
                bindingField = f; 
                
                //execute onactivate method if available
                String onactivate = b.onactivate(); 
                if (onactivate != null && onactivate.length() > 0) { 
                    try { 
                        MethodResolver.getInstance().invoke(getBean(), onactivate, new Object[]{}); 
                    } catch(Throwable t) {
                        System.out.println("ERROR invoking '"+onactivate+"' method caused by "  + t.getMessage()); 
                        if (t instanceof RuntimeException) 
                            throw (RuntimeException)t;
                        else 
                            throw new RuntimeException(t.getMessage(), t);
                    } 
                } 
            } 
            else if (f.isAnnotationPresent(com.rameses.rcp.annotations.ChangeLog.class)) {
                f.setAccessible(true);                
                //check first if the controllers change log is not yet set.
                //The change log used will be the first one found.
                try {
                    com.rameses.rcp.annotations.ChangeLog annot = (com.rameses.rcp.annotations.ChangeLog)f.getAnnotation(com.rameses.rcp.annotations.ChangeLog.class);
                    String[] prefixes = annot.prefix();
                    getProperties().put(CHANGE_LOG_PREFIX_KEY, prefixes);
                    ChangeLog cl = Binding.this.getChangeLog();
                    if ( prefixes != null) {
                        for (String s : prefixes) cl.getPrefix().add(s);
                    }                    
                    f.set(o, cl );
                } catch(Throwable t) {
                    System.out.println("ERROR injecting @ChangeLog caused by " + t.getClass().getName() + ": " + t.getMessage() );
                }
                
                f.setAccessible(accessible); 
                changeLogField = f;
            }
            else if (f.isAnnotationPresent(com.rameses.rcp.annotations.PropertyChangeListener.class)) {
                f.setAccessible(true);
                try {
                    Map map = (Map) f.get(getBean());
                    getValueChangeSupport().addExtendedHandler(map); 
                } catch(Throwable t) {
                    System.out.println("ERROR injecting @PropertyChangeListener caused by " + t.getClass().getName() + ": " + t.getMessage() );
                } 
                f.setAccessible(accessible); 
            }
            else if (f.isAnnotationPresent(com.rameses.rcp.annotations.FieldValidator.class)) {
                f.setAccessible(true);
                try {
                    Object obj = f.get(getBean()); 
                    getValidatorSupport().addFieldValidator( obj ); 
                } catch(Throwable t) { 
                    System.out.println("ERROR injecting @FieldValidator caused by " + t.getClass().getName() + ": " + t.getMessage() );
                } 
                f.setAccessible(accessible); 
            } 
            else if (f.isAnnotationPresent(com.rameses.rcp.annotations.BeanValidator.class)) {
                f.setAccessible(true);
                try {
                    Object obj = f.get(getBean());
                    getValidatorSupport().addBeanValidator( obj );
                } catch(Throwable t) { 
                    System.out.println("ERROR injecting @BeanValidator caused by " + t.getClass().getName() + ": " + t.getMessage() );
                } 
                f.setAccessible(accessible); 
            } 
            else if (f.isAnnotationPresent(com.rameses.rcp.annotations.SubWindow.class)) {
                f.setAccessible(true);
                try {
                    f.set(o, new SubWindowAdapter());
                } catch(Throwable t) {
                    System.out.println("ERROR injecting @SubWindow caused by " + t.getClass().getName() + ": " + t.getMessage() );
                }                
                f.setAccessible(accessible); 
            }
            else {
                f.setAccessible(true);
                try {
                    for (Annotation anno: f.getDeclaredAnnotations()) {
                        Object resource = getInjector().getResource(anno, this);
                        if (resource != null) f.set(o, resource);
                    } 
                } catch(Throwable t) {
                    System.out.println("ERROR injecting caused by " + t.getClass().getName() + ": " + t.getMessage() );
                }                
                f.setAccessible(accessible);                 
            }
        }
        
        Class superClass = clazz.getSuperclass();
        if (superClass != null) initAnnotatedFields(o, superClass);
        
        annotationScanIsDone = true;
    }
    
    private void initAnnotatedMethods( Object o, Class clazz ) {
        for(Method m: clazz.getDeclaredMethods()) {
            if ( m.isAnnotationPresent(Close.class) ) {
                closeMethod = m.getName();
                return;
            } 
        }
        Class superClazz = clazz.getSuperclass();
        if ( superClazz != null ) {
            initAnnotatedMethods( o, superClazz );
        }
    }
    
    private Method findAnnotatedMethod( Class beanClass, Class annoClass ) {
        for ( Method m : beanClass.getDeclaredMethods() ) {
            if ( m.isAnnotationPresent( annoClass ) ) {
                return m; 
            } 
        }
        Class superClazz = beanClass.getSuperclass();
        if ( superClazz == null ) {
            return null; 
        } else {
            return findAnnotatedMethod( superClazz, annoClass );
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ViewHandler helper methods ">
    
    private List<ViewHandler> viewHandlers = new ArrayList();
    
    public void addViewHandler(ViewHandler handler) {
        if (handler == null) return;
        
        viewHandlers.remove(handler); 
        viewHandlers.add(handler); 
    } 
    
    public void removeViewHandler(ViewHandler handler) {
        if (handler != null) viewHandlers.remove(handler); 
    }
    
    void fireActivatePage(Object name) {
        Object bean = getBean();
        if (bean instanceof ViewHandler) { 
            addViewHandler((ViewHandler)bean); 
        } 
        //notify handlers
        for (ViewHandler handler: viewHandlers) {
            handler.activatePage(this, name); 
        }
    }
    
    void fireAfterRefresh(Object name) {
        //notify handlers
        for (ViewHandler handler: viewHandlers) {
            handler.afterRefresh(this, name); 
        } 
    }    
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" ValueChangeSupport helper methods "> 
    
    private ValueChangeSupport valueChangeSupport;
    
    public ValueChangeSupport getValueChangeSupport() 
    {
        if (valueChangeSupport == null) 
            valueChangeSupport = new ValueChangeSupport(); 
        
        return valueChangeSupport; 
    } 
    
    public void addValueListener(String property, Object callbackListener) {
        getValueChangeSupport().add(property, callbackListener); 
    } 
    
    public void removeValueListener(String property, Object callbackListener) {
        getValueChangeSupport().remove(property, callbackListener); 
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" ValidatorSupport helper methods "> 
    
//    private BeanValidatorSupport beanValidatorSupport;
//    public BeanValidatorSupport getBeanValidatorSupport() 
//    {
//        if (beanValidatorSupport == null) 
//            beanValidatorSupport = new BeanValidatorSupport(); 
//        
//        return beanValidatorSupport; 
//    } 
    
    private BindingValidatorSupport validatorSupport;

    public BindingValidatorSupport getValidatorSupport() {
        if (validatorSupport == null) {
            validatorSupport = new BindingValidatorSupport();             
        }
        return validatorSupport; 
    }
    
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ChangeLogKeySupport (class) ">
    
    private class ChangeLogKeySupport implements KeyListener {
        
        public void keyTyped(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {}
        
        public void keyReleased(KeyEvent e) {
            if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z ) {
                if ( changeLog.hasChanges() ) {
                    ChangeLog.ChangeEntry ce = changeLog.undo();
                    if( ce != null ) {
                        focus( ce.getFieldName() );
                    }
                    refresh();
                }
            }
        }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ControlEventSupport (class) ">
    
    private class ControlEventSupport implements MouseListener, KeyListener {
        
        public void mouseClicked(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        
        public void mouseReleased(MouseEvent e) {
            if ( e.getButton() == MouseEvent.BUTTON1 ) {
                ControlEvent evt = createControlEvent(e);
                evt.setEventName(ControlEvent.LEFT_CLICK);
                //eventManager.notify( evt.getSource(), evt);
                
            } else if ( e.getButton() == MouseEvent.BUTTON3 ) {
                ControlEvent evt = createControlEvent(e);
                evt.setEventName(ControlEvent.RIGHT_CLICK);
                //eventManager.notify( evt.getSource(), evt);
            }
        }
        
        public void keyTyped(KeyEvent e) {}
        public void keyPressed(KeyEvent e) {}
        public void keyReleased(KeyEvent e) {}
        
        private ControlEvent createControlEvent(ComponentEvent e) {
            Component c = (Component) e.getSource();
            ControlEvent evt = new ControlEvent();
            evt.setSource( c.getName() );
            evt.setSourceEvent(e);
            return evt;
        }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionHandlerSupport (class) ">
    
    public class ActionHandlerSupport 
    {
        private List<ActionHandler> handlers = new ArrayList(); 
        
        public void add(ActionHandler handler) 
        {
            if (handler == null) return;
            
            //try to remove it first, to prevent from duplicating the entries
            handlers.remove(handler);
            handlers.add(handler); 
        }
        
        public void remove(ActionHandler handler) 
        {
            if (handler != null) handlers.remove(handler); 
        }
        
        public void fireBeforeExecute() 
        {
            for (ActionHandler handler: handlers) {
                handler.onBeforeExecute(); 
            }
        }

        public void fireAfterExecute() 
        {
            for (ActionHandler handler: handlers) {
                handler.onAfterExecute(); 
            }            
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" SubWindowAdapter (class) ">
    
    public class SubWindowAdapter implements SubWindow
    {
        Binding root = Binding.this;
        
        private SubWindow getCurrentWindow() {
            ViewContext ctx = root.getViewContext();
            return (ctx == null? null: ctx.getSubWindow());
        }
        
        public void close() { closeWindow(); } 
        
        public void closeWindow() { 
            SubWindow win = getCurrentWindow();
            if (win != null) win.closeWindow();
        }
        
        public String getName() { 
            SubWindow win = getCurrentWindow();
            return (win == null? null: win.getName());        
        } 

        public String getTitle() {
            SubWindow win = getCurrentWindow();
            return (win == null? null: win.getTitle());
        } 
        
        public void setTitle(String title) {
            SubWindow win = getCurrentWindow();
            if (win != null) win.setTitle(title);
        } 
        
        public void setListener(SubWindowListener listener) {}
        
        public void update() 
        {
            SubWindow win = getCurrentWindow();
            if (win == null) return;
            
            Map props = new HashMap(); 
            Object bean = getBean(); 
            loadFormPropertiesFromAnnotation(props, bean, bean.getClass()); 
            update(props); 
        } 
        
        public void update(Map windowAttributes) {
            SubWindow win = getCurrentWindow();
            if (win != null) win.update(windowAttributes);             
        }        
        
        private void loadFormPropertiesFromAnnotation(Map props, Object bean, Class beanClass) 
        {
            for (Field f: beanClass.getDeclaredFields()) {
                boolean accessible = f.isAccessible();
                try {
                    if (f.isAnnotationPresent(com.rameses.rcp.annotations.FormId.class)) {
                        f.setAccessible(true);
                        props.put("id", f.get(bean));
                    } 
                    else if (f.isAnnotationPresent(com.rameses.rcp.annotations.FormTitle.class)) {
                        f.setAccessible(true);
                        props.put("title", f.get(bean));
                    } 
                } catch(Throwable ex) {;}
                
                f.setAccessible(accessible); 
            }
            
            for (Method m: beanClass.getDeclaredMethods()) {
                boolean accessible = m.isAccessible();                
                try {
                    if (m.isAnnotationPresent(com.rameses.rcp.annotations.FormId.class)) {
                        m.setAccessible(true);
                        props.put("id", m.invoke(bean, new Object[]{}));
                    } 
                    else if (m.isAnnotationPresent(com.rameses.rcp.annotations.FormTitle.class)) {
                        m.setAccessible(true);
                        props.put("title", m.invoke(bean, new Object[]{}));
                    } 
                } catch(Throwable ex) {;}
                
                m.setAccessible(accessible); 
            }            
            
            Class superClass = beanClass.getSuperclass();
            if (superClass != null) loadFormPropertiesFromAnnotation(props, bean, superClass);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" AsyncRefreshWorker ">
    
    private class AsyncRefreshWorker 
    {
        private String regex; 
        
        AsyncRefreshWorker(String regex) {
            this.regex = regex; 
        }
        
        void run() {
            new Thread(new Runnable() {
                public void run() {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            runImpl(); 
                        }
                    });
                }
            }).start(); 
        }
        
        private void runImpl() {
            Binding.this.refreshImpl(regex); 
        }
    }
    
    // </editor-fold>
}
