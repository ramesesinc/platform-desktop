package com.rameses.rcp.control;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.MsgBox; 
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.support.ImageIconSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.util.UICommandUtil;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.UICommand;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.ValueUtil;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 *
 * @author jaycverg
 */
public class XButton extends JButton implements UICommand, ActionListener, 
    ActiveControl, MouseEventSupport.ComponentInfo 
{
    private int index;
    private String[] depends;
    private Binding binding;
    private boolean immediate;
    private boolean update;
    private boolean defaultCommand;
    private boolean autoRefresh = true;
    private String expression;
    private String target;
    private ControlProperty property = new ControlProperty();
    private Map params = new HashMap();
    private String permission;
    private String visibleWhen;
    private String disableWhen;
    private String iconResource; 
    
    private String accelerator;
    private KeyStroke acceleratorKS;
    private MouseEventSupport mouseSupport; 
    
    private int stretchWidth;
    private int stretchHeight;
        
    public XButton() {
        setOpaque(false);
        addActionListener(this);
        mouseSupport = new MouseEventSupport(this);
        mouseSupport.install(); 
    }
    
        
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public boolean isAutoRefresh() { return autoRefresh; } 
    public void setAutoRefresh( boolean autoRefresh ) {
        this.autoRefresh = autoRefresh; 
    }
    
    public String getAccelerator() { return accelerator; }
    public void setAccelerator(String accelerator) {
        this.accelerator = accelerator;
        
        try {
            setAcceleratorKey( KeyStroke.getKeyStroke(accelerator) );
        } catch(Throwable ign) {;}
    }
    
    public void setAcceleratorKey( KeyStroke ks  ) {
        if (acceleratorKS != null) { 
            unregisterKeyboardAction(acceleratorKS);
        }
        acceleratorKS = ks; 
        if (acceleratorKS != null) { 
            registerKeyboardAction(this, acceleratorKS, JComponent.WHEN_IN_FOCUSED_WINDOW); 
        } 
    }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public Binding getBinding() { return binding; }
    public void setBinding(Binding binding) { 
        this.binding = binding; 
    }
    
    public String getActionName() { return getName(); }
    
    public boolean isImmediate() { return immediate; }
    public void setImmediate(boolean immediate) { this.immediate = immediate; }
    
    public ControlProperty getControlProperty() { return property; }
    
    public boolean isShowCaption() { 
        return property.isShowCaption(); 
    }
    public void setShowCaption(boolean show) { 
        property.setShowCaption(show); 
    }
    
    public String getCaption() { return property.getCaption(); }
    public void setCaption(String caption) { 
        property.setCaption(caption); 
    }
    
    public int getCaptionWidth() { return property.getCaptionWidth(); }
    public void setCaptionWidth(int width) { 
        property.setCaptionWidth(width); 
    }
    
    public Font getCaptionFont() { return property.getCaptionFont(); }    
    public void setCaptionFont(Font f) { 
        property.setCaptionFont(f); 
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() { return property.getCellPadding(); }    
    public void setCellPadding(Insets padding) { 
        property.setCellPadding(padding);
    }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { 
        this.target = target; 
    }
    
    public boolean isUpdate() { return update; }
    public void setUpdate(boolean update) { this.update = update; }
    
    public boolean isDefaultCommand() { return defaultCommand; }
    public void setDefaultCommand(boolean defaultCommand) {
        this.defaultCommand = defaultCommand;
    }
    
    public String getExpression() { return expression; } 
    public void setExpression(String expression) { 
        this.expression = expression; 
        setText(expression); 
    } 
    
    public Map getParams() { return params; }
    public void setParams(Map params) { this.params = params; }
        
    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
    
    public String getVisibleWhen() { return visibleWhen; }
    public void setVisibleWhen(String visibleWhen) { this.visibleWhen = visibleWhen; }
    
    public String getDisableWhen() { return disableWhen; }
    public void setDisableWhen(String disableWhen) { this.disableWhen = disableWhen; }
    
    public String getIconResource() { return iconResource; } 
    public void setIconResource(String iconResource) {
        this.iconResource = iconResource; 
        setIcon(ImageIconSupport.getInstance().getIcon(iconResource)); 
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {}  
    
    public void setMnemonic(char mnemonic) {
        super.setMnemonic(mnemonic);
        resolveDisplayMnemonic();
    } 
    
    public void setText(String text) {
        super.setText(text); 
        resolveDisplayMnemonic();
    }
    
    private void resolveDisplayMnemonic() {
        char mnemonic = (char) getMnemonic();
        if (mnemonic == '\u0000') return;
        
        String text = getText();
        if (text == null) return;
                
        String stext = text.toLowerCase();
        if (!stext.trim().matches("<html>.*</html>")) return;
        
        char cval = Character.toLowerCase(mnemonic);        
        Pattern p = Pattern.compile("<.*?>");
        Matcher m = p.matcher(text); 
        int startindex = 0;
        int locIndex = -1;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            if (start > startindex) {
                char[] chars = stext.substring(startindex, start).toCharArray();
                for (int i=0; i<chars.length; i++) {
                    if (chars[i] == cval) {
                        locIndex = startindex+i; 
                        break; 
                    } 
                } 
            } 

            if (locIndex >= 0) break; 

            startindex = end;
        } 

        if (locIndex < 0) return;

        StringBuffer sb = new StringBuffer(); 
        sb.append(text.substring(0, locIndex));
        sb.append("<u>" + text.charAt(locIndex) + "</u>"); 
        sb.append(text.substring(locIndex+1));
        super.setText(sb.toString());          
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UICommand implementation ">
    
    public void refresh() { 
        String exprstr = getExpression();
        if (!ValueUtil.isEmpty(exprstr)) {
            Object result = UIControlUtil.evaluateExpr(binding.getBean(), expression);
            setText((result==null? "": result.toString()));
        }
        
        exprstr = getVisibleWhen(); 
        if (!ValueUtil.isEmpty(exprstr)) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(binding.getBean(), visibleWhen);
            } catch(Throwable t) {
                //t.printStackTrace();
            }
            
            if (!result) {
                setVisible(false);
            } else if (!isVisible()) {
                setVisible(true);
            }
        }
        
        exprstr = getDisableWhen(); 
        if (!ValueUtil.isEmpty(exprstr)) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(binding.getBean(), disableWhen);
            } catch(Throwable t) {
                //t.printStackTrace();
            } 
            
            if (!result) {
                setEnabled(true);
            } else if (isEnabled()) {
                setEnabled(false);
            }
        } 
    }
    
    public void load() {}
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void actionPerformed(ActionEvent e) {
        boolean ctrlDown = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
        boolean shiftDown = ((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK);
        if (ctrlDown && shiftDown) {
            mouseSupport.showComponentInfo(); 
            return; 
        }
        
        final Object outcome = UICommandUtil.processAction(this); 
        if (outcome instanceof PopupMenuOpener) {
            PopupMenuOpener menu = (PopupMenuOpener) outcome;
            List items = menu.getItems();
            if (items == null || items.isEmpty()) return;
            
            if (items.size() == 1 && menu.isExecuteOnSingleResult()) { 
                Object o = menu.getFirst(); 
                if (o instanceof Opener) 
                    UICommandUtil.processAction(XButton.this, getBinding(), (Opener)o); 
                else 
                    ((Action)o).execute(); 
            } 
            else { 
                EventQueue.invokeLater(new Runnable() {
                    public void run() { 
                        show((PopupMenuOpener) outcome); 
                    } 
                }); 
            }
        }     
    }   
        
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("accelerator", getAccelerator());
        map.put("mnemonic", (char) getMnemonic());
        map.put("defaultCommand", isDefaultCommand()); 
        map.put("disableWhen", getDisableWhen());
        map.put("expression", getExpression()); 
        map.put("immediate", isImmediate()); 
        map.put("target", getTarget());
        map.put("visibleWhen", getVisibleWhen()); 
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
            sourceFont = getFontSupport().applyStyles(sourceFont, attrs); 
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
        font = getFontSupport().applyStyles(font, attrs); 
        
        super.setFont(font); 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" PopupMenu Support ">    

    private JPopupMenu popup;
    
    protected void show(PopupMenuOpener menu) { 
        if (popup == null) 
            popup = new JPopupMenu(); 
        else 
            popup.setVisible(false); 
        
        popup.removeAll(); 
        for (Object o: menu.getItems()) {
//            String expr = getVisibleWhen( o ); 
//            if ( !ValueUtil.isEmpty(expr) ) {
//                boolean b = false; 
//                try { 
//                    b = UIControlUtil.evaluateExprBoolean(getBinding().getBean(), expr);
//                } catch(Throwable t) {;}
//                
//                if ( !b ) continue; 
//            }
            
            ActionMenuItem ami = null;             
            if (o instanceof Opener) {
                ami = new ActionMenuItem((Opener) o); 
            } else {
                ami = new ActionMenuItem((Action) o);
            }
            
            Dimension dim = ami.getPreferredSize();
            ami.setPreferredSize(new Dimension(Math.max(dim.width, 100), dim.height)); 
            popup.add(ami); 
        } 
        popup.pack();
        
        Rectangle rect = XButton.this.getBounds();
        popup.show(XButton.this, 0, rect.height); 
        popup.requestFocus(); 
    } 
    
    private String getVisibleWhen( Object item ) {
        if (item instanceof Opener) {
            Opener op = (Opener) item; 
            Object val = op.getProperties().get("visibleWhen");
            return (val == null ? null: val.toString()); 
        } else if ( item instanceof Action ) {
            Action act = (Action) item;
            return act.getVisibleWhen(); 
        } else {
            return null; 
        }
        
    }
    
    private class ActionMenuItem extends JMenuItem 
    {
        XButton root = XButton.this;
        private Object source;
        
        ActionMenuItem(Opener anOpener) {
            this.source = anOpener;
            setText(anOpener.getCaption());            
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    invokeOpener(e);
                }
            });
            
            Object ov = anOpener.getProperties().get("mnemonic");
            if (ov != null && ov.toString().trim().length() > 0) 
                setMnemonic(ov.toString().trim().charAt(0));
            
            ov = anOpener.getProperties().get("icon");
            if (ov != null && ov.toString().length() > 0) 
                setIcon(ImageIconSupport.getInstance().getIcon(ov.toString()));
        }
        
        ActionMenuItem(Action anAction) {
            this.source = anAction;
            setText(anAction.getCaption());            
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    invokeAction(e);
                }
            });
            
            setMnemonic(anAction.getMnemonic()); 
            
            String sicon = anAction.getIcon();
            if (sicon != null && sicon.length() > 0) 
                setIcon(ImageIconSupport.getInstance().getIcon(sicon));
        }        
        
        void invokeOpener(ActionEvent e) {
            try {
                UICommandUtil.processAction(root, root.getBinding(), (Opener) source); 
            } catch(Exception ex) { 
                MsgBox.err(ex); 
            } 
        } 
        
        void invokeAction(ActionEvent e) {
            try { 
                Object outcome = ((Action) source).execute(); 
                if (outcome instanceof Opener) 
                    UICommandUtil.processAction(root, root.getBinding(), (Opener)outcome); 
            } catch(Exception ex) { 
                MsgBox.err(ex); 
            } 
        }         
    }
    
    // </editor-fold>    
    
}
