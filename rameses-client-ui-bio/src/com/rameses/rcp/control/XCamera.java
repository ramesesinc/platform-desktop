/*
 * XCamera.java
 *
 * Created on December 4, 2013, 8:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control;

import com.rameses.rcp.camera.WebcamViewer;
import com.rameses.rcp.common.CallbackHandler;
import com.rameses.rcp.common.CallbackHandlerProxy;
import com.rameses.rcp.common.CameraModel;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UICommand;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author wflores
 */
public class XCamera extends JButton implements MouseEventSupport.ComponentInfo, 
    UICommand, ActionListener, ActiveControl
{
    private MouseEventSupport mouseSupport; 
    
    private String accelerator;
    private KeyStroke acceleratorKS;    
    private String expression;
    private String handler;
    private String iconResource; 
    
    private String target;
    private String permission;
    private boolean immediate;
    private boolean update;
    private boolean defaultCommand;
    
    private Binding binding;
    private String[] depends;
    private int index; 
    
    private int stretchWidth;
    private int stretchHeight;        
    private String visibleWhen;
    
    public XCamera() {
        mouseSupport = new MouseEventSupport(this);
        mouseSupport.install(); 
        addActionListener(this); 
        setIconResource("images/toolbars/camera.png");
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getAccelerator() { return accelerator; }
    public void setAccelerator(String accelerator) {
        this.accelerator = accelerator;
        
        try {
            if (acceleratorKS != null) unregisterKeyboardAction(acceleratorKS);
            
            acceleratorKS = KeyStroke.getKeyStroke(accelerator);
            if (acceleratorKS != null) { 
                registerKeyboardAction(this, acceleratorKS, JComponent.WHEN_IN_FOCUSED_WINDOW);
            } 
        } catch(Exception ign) {;}
    }
    
    public String getExpression() { return expression; } 
    public void setExpression(String expression) {
        this.expression = expression; 
    }
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) {
        this.handler = handler; 
    } 
    
    public String getIconResource() { return iconResource; } 
    public void setIconResource(String iconResource) {
        this.iconResource = iconResource;         
        setIcon(ImageIconSupport.getInstance().getIcon(iconResource)); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Font support implementation ">
    
    private FontSupport fontSupport;
    private Font sourceFont;     
    private String fontStyle; 
    
    private FontSupport getFontSupport() {
        if (fontSupport == null) 
            fontSupport = new FontSupport();
        
        return fontSupport; 
    }    
    
    public void setFont(Font font) { 
        sourceFont = font; 
        if (sourceFont != null) {
            Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
            if (!attrs.isEmpty()) sourceFont = sourceFont.deriveFont(attrs);
        }
        
        super.setFont(sourceFont); 
    }     
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        
        if (sourceFont == null) sourceFont = super.getFont(); 
        
        Font font = sourceFont;
        if (font == null) return;
        
        Map attrs = getFontSupport().createFontAttributes(getFontStyle()); 
        if (!attrs.isEmpty()) font = font.deriveFont(attrs); 
        
        super.setFont(font); 
    } 
    
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc=" MouseEventSupport.ComponentInfo implementation ">
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("accelerator", getAccelerator());
        map.put("mnemonic", (char) getMnemonic());
        map.put("defaultCommand", isDefaultCommand()); 
        map.put("handler", getHandler()); 
        map.put("immediate", isImmediate()); 
        map.put("target", getTarget());
        return map;
    }   
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UICommand implementation ">

    public String getActionName() { return null; } 

    public boolean isAutoRefresh() { return false; }
    
    public String getTarget() { return target; } 
    public void setTarget(String target) { 
        this.target = target; 
    }
    
    public boolean isImmediate() { return immediate; } 
    public void setImmediate(boolean immediate) { 
        this.immediate = immediate; 
    }
    
    public boolean isUpdate() { return update; } 
    public void setUpdate(boolean update) {
        this.update = update;
    }
    
    public boolean isDefaultCommand() { return defaultCommand; } 
    public void setDefaultCommand(boolean defaultCommand) {
        this.defaultCommand = defaultCommand;
    }
    
    public String getPermission() { return permission; } 
    public void setPermission(String permission) {
        this.permission = permission; 
    }
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) {
        this.binding = binding; 
    }
    
    public String[] getDepends() { return depends; } 
    public void setDepends(String[] depends) {
        this.depends = depends; 
    }
        
    public String getName() { return super.getName(); }
    public void setName(String name) { 
        super.setName(name); 
    } 
    
    public int getIndex() { return index; } 
    public void setIndex(int index) {
        this.index = index; 
    }
    
    public void load() {
    }

    public void refresh() {
        String expression = getExpression();
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        if (expression != null && expression.length() > 0 && bean != null) { 
            Object result = UIControlUtil.evaluateExpr(bean, expression); 
            setText((result == null? "": result.toString())); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" ActionListener implementation ">
    
    public final void actionPerformed(ActionEvent e) {
        boolean ctrlDown = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
        boolean shiftDown = ((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK);
        if (ctrlDown && shiftDown) {
            mouseSupport.showComponentInfo(); 
            return; 
        }
        
        processAction();
    }
    
    protected void processAction() {
        String handler = getHandler();
        Object bean = getBinding().getBean();
        Object ohandler = UIControlUtil.getBeanValue(bean, handler); 
        
        CameraModel model = null;        
        if (ohandler instanceof CameraModel) {
            model = (CameraModel) ohandler; 
        } else {
            model = new DefaultCameraModel(ohandler); 
        }
        model.setProvider(new DefaultCameraModelProvider()); 
        openViewer(model);         
    } 
    
    private void openViewer(CameraModel model) {
        WebcamViewer.open(model); 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">
    
    private ControlProperty property;
    
    public ControlProperty getControlProperty() {
        if (property == null) {
            property = new ControlProperty(); 
        }
        return property; 
    }
    
    public boolean isShowCaption() { 
        return getControlProperty().isShowCaption(); 
    }
    public void setShowCaption(boolean show) { 
        getControlProperty().setShowCaption(show); 
    }
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }
    public void setCaption(String caption) { 
        getControlProperty().setCaption(caption); 
    }
    
    public int getCaptionWidth() { 
        return getControlProperty().getCaptionWidth(); 
    }
    public void setCaptionWidth(int width) { 
        getControlProperty().setCaptionWidth(width); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultCameraModel ">
    
    private class DefaultCameraModel extends CameraModel 
    {
        private Object callback;
        private CallbackHandlerProxy proxy;
        
        DefaultCameraModel(Object callback) {
            if (callback != null) {
                this.proxy = new CallbackHandlerProxy(callback); 
            } 
        }

        public void onselect(byte[] bytes) {
            if (proxy == null) return;
            
            proxy.call(bytes); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultCameraModelProvider ">
    
    private class DefaultCameraModelProvider implements CameraModel.Provider
    {
        XCamera root = XCamera.this;
        
        public Object getBinding() { 
            return root.getBinding(); 
        } 

        public void showDialog(CameraModel model) {
            root.openViewer(model); 
        } 
    }
    
    //  </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BuiltinSelectHandler ">
    
    private class BuiltinSelectHandler implements CallbackHandler
    {
        private CameraModel model; 
        
        BuiltinSelectHandler(CameraModel model) {
            this.model = model; 
        }
        
        public Object call(Object[] args) { return null; }  
        public Object call() { return null; } 
        public Object call(Object arg) { 
            model.onselect((byte[]) arg); 
            return null; 
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" BuiltinCloseHandler ">
    
    private class BuiltinCloseHandler implements CallbackHandler
    {
        private CameraModel model; 
        
        BuiltinCloseHandler(CameraModel model) {
            this.model = model; 
        }
        
        public Object call(Object[] args) { return null; }  
        public Object call(Object arg) { return null; } 
        public Object call() { 
            model.onclose(); 
            return null; 
        } 
    }
    
    // </editor-fold>     
} 
