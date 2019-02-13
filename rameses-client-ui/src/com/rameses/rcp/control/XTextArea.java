package com.rameses.rcp.control;

import com.rameses.rcp.common.PopupItem;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TextDocumentModel;
import com.rameses.rcp.common.TextEditorModel;
import com.rameses.rcp.common.TextWriter;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.TextComponentSupport;
import com.rameses.rcp.control.text.TextEditorPopupSelector;
import com.rameses.rcp.framework.ActionHandler;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.TimerManager;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author Windhel
 */
public class XTextArea extends JTextArea implements UIInput, Validatable, 
    ActiveControl, MouseEventSupport.ComponentInfo 
{
    final String ACTION_MAPPING_VK_SPACE = "ACTION_MAPPING_VK_SPACE";    
    final String ACTION_MAPPING_VK_ESC   = "ACTION_MAPPING_VK_ESCAPE";    
    final String ACTION_MAPPING_VK_DOWN   = "ACTION_MAPPING_VK_DOWN";    
    final String ACTION_MAPPING_VK_UP   = "ACTION_MAPPING_VK_UP"; 
    final String ACTION_MAPPING_VK_ENTER = "ACTION_MAPPING_VK_ENTER"; 
    
    private Color focusBackground;
    private Color disabledBackground;
    private Color enabledBackground;
    
    private Binding binding;
    private int index;
    private boolean readonly;    
    private boolean nullWhenEmpty = true;   
    private String[] depends;
    private String fontStyle;
    private Font sourceFont;
    private ControlProperty property = new ControlProperty();
    private ActionMessage actionMessage = new ActionMessage();
    
    private TextDocument textDocument = new TextDocument();
    private TrimSpaceOption trimSpaceOption = TrimSpaceOption.NONE;
    private ActionHandlerImpl actionHandler = new ActionHandlerImpl();
    
    private String handler;
    private TextWriter textWriterObject; 
    private TextDocumentModel handlerObject; 
    private TextEditorModel editorModel;    
    
    private String itemExpression;
    private String varName = "item";
    
    private String hint;
    private boolean showHint;
    private boolean autoScrollDown;
        
    private VerticalAdjustmentListener verticalAdjHandler;
    private TextWriterHandler textWriterHandler; 
    
    private int stretchWidth;
    private int stretchHeight;     
    private boolean exitOnTabKey;
    private String visibleWhen;
    
    public XTextArea() {
        super();
        initComponent();
    }

    private void initComponent() {
        TextComponentSupport.getInstance().installUIDefaults(this); 
        setColumns(5);
        setRows(2); 
        
        for (FocusListener l : getFocusListeners()) {
            removeFocusListener(l); 
        }
        new MouseEventSupport(this).install(); 
        
        if (Beans.isDesignTime()) return;
         
        verticalAdjHandler = new VerticalAdjustmentListener(); 
        textWriterHandler = new TextWriterHandler(); 
        
        KeyStroke vkspace = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK, true); 
        getInputMap().put(vkspace, ACTION_MAPPING_VK_SPACE); 
        getActionMap().put(ACTION_MAPPING_VK_SPACE, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                fireSearch();
            }
        }); 
        KeyStroke vkescape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0); 
        getInputMap().put(vkescape, ACTION_MAPPING_VK_ESC); 
        getActionMap().put(ACTION_MAPPING_VK_ESC, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                fireEscapeKeyEvent(); 
            }
        }); 
        
        addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_TAB: 
                        if ( isExitOnTabKey() && hasFocus() ) { 
                            if ( e.isControlDown() || e.isAltDown() ) {
                                //do nothing 
                            } else if ( e.isShiftDown() ) {
                                e.consume();
                                transferFocusBackward();
                            } else  { 
                                e.consume();
                                transferFocus(); 
                            }                           
                        }
                        break;
                        
                    case KeyEvent.VK_ESCAPE: break;
                    case KeyEvent.VK_DOWN: 
                        if (getPopup().isVisible() && getPopup().isShowing()) { 
                            e.consume();
                            getPopup().moveDown(); 
                        } 
                        break;
                        
                    case KeyEvent.VK_UP: 
                        if (getPopup().isVisible() && getPopup().isShowing()) { 
                            e.consume();
                            getPopup().moveUp(); 
                        }                         
                        break;
                                        
                    case KeyEvent.VK_ENTER: 
                        if (getPopup().isVisible() && getPopup().isShowing()) { 
                            e.consume();  
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    fireSelectItem();
                                }
                            });
                        }                        
                        break;
                        
                    default: 
                        try {
                            boolean ctrlDown = ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
                            if (ctrlDown && e.getKeyCode() == KeyEvent.VK_SPACE) {
                                e.consume(); 
                                return;
                            } 
                            
                            int pos = getCaretPosition()-1;
                            if (pos < 0) return;
                            
                            String str = e.getKeyChar()+"";
                            boolean whitespace = str.matches("\\s");
                            if (whitespace) {
                                getPopup().setVisible(false);     
                                return;
                            }
                            
                            if (getPopup().isVisible() && getPopup().isShowing() && !whitespace) { 
                                EventQueue.invokeLater(new Runnable() {
                                    public void run() { 
                                        fireSearch();
                                    }
                                });
                            } 

                        } catch(Throwable t) {
                            t.printStackTrace();
                        }
                }
            }
        });        
    }
    
    public void paint(Graphics origGraphics) {
        super.paint(origGraphics);
        
        if ( showHint && getDocument().getLength() == 0 ) {
            Graphics g = origGraphics.create();
            Font f = getFont();
            FontMetrics fm = g.getFontMetrics(f);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont( f );
            
            Insets margin = getInsets();
            int x = margin.left;
            int y = margin.top + fm.getAscent();
            g.drawString(" " + getHint(), x, y);
            g.dispose();
        }
    }
    
    public void refresh() {
        int oldCaretPos = getCaretPosition();
        try {
            //force to update component's status
            updateBackground();

            Object value = UIControlUtil.getBeanValue(this);
            if (textWriterObject != null) {
                value = textWriterObject.getText(); 
            } 
            setValue(value);
            
        } catch(Throwable e) {
            setText("");
            
            if (ClientContext.getCurrentContext().isDebugMode()) 
                e.printStackTrace();
        } 
        
        try {
            setCaretPosition(oldCaretPos); 
        } catch(Throwable ign){;} 
        
        
        if (textWriterObject != null) {
            setEditable(false); 
        } 
        
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
        setInputVerifier(UIInputUtil.VERIFIER);
        setDocument(textDocument);
        
        String shandler = getHandler();
        if (shandler != null) {
            Object obj = UIControlUtil.getBeanValue(getBinding(), shandler); 
            if (obj instanceof TextDocumentModel) {
                handlerObject = (TextDocumentModel) obj;
                handlerObject.setProvider(new DocumentProvider()); 
            } else {
                handlerObject = null; 
            }
            
            if (obj instanceof TextEditorModel) { 
                editorModel = (TextEditorModel)obj; 
            } else {
                editorModel = null; 
            }
            
            if (obj instanceof TextWriter) {
                textWriterObject = (TextWriter)obj;
                textWriterObject.setHandler(textWriterHandler); 
            } else {
                textWriterObject = null; 
            }
        } 
        
        if (getParent() instanceof JViewport) {
            if (getParent().getParent() instanceof JScrollPane) {
                JScrollPane jsp = (JScrollPane) getParent().getParent(); 
                jsp.getVerticalScrollBar().addAdjustmentListener(verticalAdjHandler);
            }
        }
    }
    
    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }    
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("focusAccelerator", getFocusAccelerator());
        map.put("handler", getHandler());
        map.put("exitOnTabKey", isExitOnTabKey()); 
        map.put("nullWhenEmpty", isNullWhenEmpty()); 
        map.put("required", isRequired());        
        return map;
    }       
    
    public void validateInput() 
    {
        actionMessage.clearMessages();
        property.setErrorMessage(null);
        if ( isRequired() && ValueUtil.isEmpty(getText()) ) 
        {
            actionMessage.addMessage("", "{0} is required", new Object[]{ getCaption() });
            property.setErrorMessage(actionMessage.toString());
        }
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
    
        
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  "> 

    public boolean isAutoScrollDown() { return autoScrollDown; } 
    public void setAutoScrollDown(boolean autoScrollDown) {
        this.autoScrollDown = autoScrollDown; 
    }
    
    public boolean isExitOnTabKey() { return exitOnTabKey; } 
    public void setExitOnTabKey(boolean exitOnTabKey) {
        this.exitOnTabKey = exitOnTabKey; 
    }
    
    public void setName(String name) 
    {
        super.setName(name);
        
        if (Beans.isDesignTime()) super.setText(name);
    }
    
    public Object getValue() 
    {
        String text = (textWriterObject == null? getText(): null); 
        if ( ValueUtil.isEmpty(text) && nullWhenEmpty ) return null;
        
        if ( trimSpaceOption != null ) text = trimSpaceOption.trim(text);
        
        return text;
    }
    
    public void setValue(Object value) {
        setText(value == null? "" : value.toString()); 
    }
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }    
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }
    
    public String[] getDepends() { return depends; }
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) 
    { 
        //detached the handler from the old binding
        if (this.binding != null) 
            this.binding.getActionHandlerSupport().remove(actionHandler); 
        
        this.binding = binding; 
        
        if (binding != null) 
            binding.getActionHandlerSupport().add(actionHandler); 
    }
        
    public String getCaption() {
        return property.getCaption();
    }    
    public void setCaption(String caption) {
        property.setCaption(caption);
    }
    
    public char getCaptionMnemonic() {
        return property.getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public boolean isRequired() {
        return property.isRequired();
    }    
    public void setRequired(boolean required) {
        property.setRequired(required);
    }
    
    public boolean isShowCaption() {
        return property.isShowCaption();
    }    
    public void setShowCaption(boolean show) {
        property.setShowCaption(show);
    }
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
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
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        if (sourceFont == null) {
            sourceFont = super.getFont();
        } else {
            super.setFont(sourceFont); 
        } 
        new FontSupport().applyStyles(this, fontStyle);
    }      
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }
    
    public ActionMessage getActionMessage() { return actionMessage; }
    
    public ControlProperty getControlProperty() { return property; }
    
    public boolean isImmediate() { return false; }
    
    public TextCase getTextCase() {
        return textDocument.getTextCase();
    }    
    public void setTextCase(TextCase textCase) {
        textDocument.setTextCase(textCase);
    }
    
    public TrimSpaceOption getTrimSpaceOption() {
        return trimSpaceOption;
    }    
    public void setTrimSpaceOption(TrimSpaceOption option) {
        this.trimSpaceOption = option;
    }

    public boolean isReadonly() { return readonly; }    
    public void setReadonly(boolean readonly) 
    {
        if (!isEnabled()) return;

        this.readonly = readonly;
        setEditable(!readonly);
        super.firePropertyChange("editable", readonly, !readonly);
    }
        
    public void setRequestFocus(boolean focus) {
        if ( focus ) requestFocus();
    }
    
    public String getHint() { return hint; }
    public void setHint(String hint) 
    {
        this.hint = hint;
        showHint = !ValueUtil.isEmpty(hint);
    }
    
    public String getHandler() { return handler; } 
    public void setHandler(String handler) { this.handler = handler; }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    public String getItemExpression() { return itemExpression; } 
    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression; 
    }  
    
    public String getVarName() { return varName; } 
    public void setVarName(String varName) {
        this.varName = varName; 
    }    
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods  ">
    
    public Color getFocusBackground() { return focusBackground; } 
    
    public Color getBackground() 
    {
        if (Beans.isDesignTime()) return super.getBackground();
        
        if (enabledBackground == null) 
            enabledBackground = UIManager.getLookAndFeelDefaults().getColor("TextField.background");
        if (disabledBackground == null)
            disabledBackground = UIManager.getLookAndFeelDefaults().getColor("TextField.disabledBackground");
        
        Color preferredColor = null;
        boolean enabled = isEnabled(); 
        if (enabled) 
        {
            if (hasFocus()) 
            {
                Color newColor = getFocusBackground();
                preferredColor = (newColor == null? enabledBackground: newColor);
            }
            else {
                preferredColor = enabledBackground; 
            } 
        } 
        else { 
            preferredColor = disabledBackground;
        } 
        
        return (preferredColor == null? super.getBackground(): preferredColor); 
    } 
    
    protected void updateBackground() 
    {
        if (enabledBackground == null) 
            enabledBackground = UIManager.getLookAndFeelDefaults().getColor("TextField.background");
        if (disabledBackground == null)
            disabledBackground = UIManager.getLookAndFeelDefaults().getColor("TextField.disabledBackground");
        
        Color newColor = getBackground(); 
        setBackground(newColor); 
        repaint();
    }
    
    protected void processFocusEvent(FocusEvent e) 
    {
        if (e.getID() == FocusEvent.FOCUS_GAINED) 
        {
            updateBackground();
        } 
        
        else if (e.getID() == FocusEvent.FOCUS_LOST) 
        { 
            if (!e.isTemporary()) updateBackground(); 
        } 
        
        super.processFocusEvent(e); 
    } 
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  DocumentProvider (class)  ">
    
    private class DocumentProvider implements TextDocumentModel.Provider 
    {
        private XTextArea root = XTextArea.this; 
        
        public String getText() { 
            return root.getText(); 
        }
        public void setText(String text) 
        {
            root.setText((text==null? "": text)); 
            repaint();
        }
        
        public void insertText(String text) 
        {
            if (text == null) return;

            int caretPos = root.getCaretPosition();
            try 
            {
                int caretCharPos = (text == null? -1: text.indexOf('|'));
                if (caretCharPos >= 0) 
                {
                    StringBuffer sb = new StringBuffer(); 
                    sb.append(text.substring(0, caretCharPos));
                    sb.append(' ');
                    sb.append(text.substring(caretCharPos+1));
                    text = sb.toString(); 
                }

                root.textDocument.insertString(caretPos, text, null);
                
                if (caretCharPos >= 0) root.setCaretPosition(caretPos + caretCharPos);
            } 
            catch (BadLocationException ex) {
                System.out.println("[XTextArea] failed to insert text at position " + caretPos + " caused by " + ex.getMessage());
            }
            finally {
                repaint();                 
            }
        } 
        
        public void appendText(String text) {
            root.append(text == null? "null": text); 
            root.verticalAdjHandler.setAllowScrollDown(true); 
            repaint(); 
        }
        
        public void requestFocus() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    root.requestFocus();
                    root.grabFocus();
                }
            }); 
        }
        
        public void load() { root.load(); }
        public void refresh() { root.refresh(); } 
        
        private void repaint() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    root.repaint(); 
                }
            }); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionHandlerImpl (class) ">   
    
    private class ActionHandlerImpl implements ActionHandler
    {
        XTextArea root = XTextArea.this;
        
        public void onBeforeExecute() {
        }

        /*
         *  This method is called once a button is clicked.
         */
        public void onAfterExecute() 
        {
            if (root.isReadonly() || !root.isEnabled() || !root.isEditable()) return;
            if (!root.textDocument.isDirty()) return;
            
            UIInputUtil.updateBeanValue(root); 
        } 
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc="  helper methods  ">
    
    protected List fetchList(Map params) {
        return (editorModel == null? null: editorModel.fetchList(params)); 
    }
    
    protected Object getFormattedText(Object item) {
        String expression = getItemExpression();
        if (expression == null || expression.length() == 0) {
            return item.toString(); 
        } else {
            Object exprBean = createExpressionBean(item); 
            return UIControlUtil.evaluateExpr(exprBean, expression); 
        }
    }
    
    protected String getTemplate(Object item) {
        Object o = (editorModel == null? null: editorModel.getTemplate(item));
        return (o == null? null: o.toString()); 
    }
    
    private Object createExpressionBean(Object itemBean) { 
        Object bean = getBinding().getBean();
        ExprBeanSupport beanSupport = new ExprBeanSupport(bean);
        beanSupport.setItem(getVarName(), itemBean); 
        return beanSupport.createProxy(); 
    } 
    
    private String getSearchtext() {
        try { 
            int caretpos = getCaretPosition(); 
            StringBuffer sb = new StringBuffer(); 
            for (int i=caretpos-1; i>=0; i--) {
                String str = getDocument().getText(i, 1); 
                if (str.matches("\\s")) break;
                
                if (sb.length() == 0) { 
                    sb.append(str); 
                } else {
                    sb.insert(0, str); 
                }
            } 
            return (sb.length() == 0? null: sb.toString()); 
        } catch(Throwable t) {
            return null; 
        }        
    }    
    
    private void fireSearch() {
        if (!isEnabled() || !isEditable()) return;
        
        Map params = new HashMap();
        params.put("searchtext", getSearchtext());         
        TimerManager.getInstance().schedule(new LookupTask(params), 300); 
    }
    
    private int getWhitespacePositionBefore(int pos) {
        try { 
            for (int i=pos-1; i>=0; i--) {
                String str = getDocument().getText(i, 1); 
                if (str.matches("\\s")) return i;
            } 
            return -1; 
        } catch(Throwable t) {
            return -1;
        } 
    }    
    
    private void fireSelectItem() { 
        PopupItem pi = getPopup().getSelectedItem(); 
        if (pi == null) return;
        
        String template = getTemplate(pi.getUserObject()); 
        if (template == null) template = "";
        
        Document doc = getDocument();
        int doclen = doc.getLength();
        int curpos = getCaretPosition();
        int startpos = getWhitespacePositionBefore(curpos)+1; 
        
        try { 
            doc.remove(startpos, curpos-startpos); 
            doc.insertString(startpos, template, null); 
            getPopup().setVisible(false); 
        } catch(Throwable t) {
            t.printStackTrace(); 
        }
    }    
    
    private void fireEscapeKeyEvent() {
        if (getPopup().isVisible() && getPopup().isShowing()) {
            getPopup().setVisible(false); 
        }
    }    
    
    protected void fireMoveDown() {
        if (getPopup().isVisible() && getPopup().isShowing()) { 
            getPopup().moveDown(); 
        }
    }
    
    protected void fireMoveUp() {
        if (getPopup().isVisible() && getPopup().isShowing()) { 
            getPopup().moveUp(); 
        } 
    }     
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LookupTask ">
        
    private class LookupTask implements Runnable 
    {
        XTextArea root = XTextArea.this;
        
        private String searchtext; 
        private Map params;
        
        LookupTask(Map params) {
            this.params = params; 
            this.searchtext = (params == null? null: (String)params.get("searchtext")); 
        }
        
        public void run() {
            String str = root.getSearchtext()+""; 
            if (!str.equals(searchtext+"")) return;
            
            List list = root.fetchList(params); 
            EventQueue.invokeLater(new ResultDataLoader(list)); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ResultDataLoader ">
    
    private TextEditorPopupSelector jpopup;
    
    private TextEditorPopupSelector getPopup() {
        if (jpopup == null) {
            jpopup = new TextEditorPopupSelector(this);
            jpopup.add(new TextEditorPopupSelector.SelectionListener(){
                public void onselect(PopupItem item) {
                    fireSelectItem(); 
                }
            });
        }
        return jpopup;
    }
    
    private void showPopup() {
        try { 
            Rectangle rect = modelToView(getCaretPosition()); 
            TextEditorPopupSelector popup = getPopup(); 
            popup.pack();
            popup.setSize(100, 20);
            popup.show(this, rect.x, rect.y+rect.height); 
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private class ResultDataLoader implements Runnable 
    {
        XTextArea root = XTextArea.this;
        private List result;
        
        ResultDataLoader(List result) {
            this.result = result;
        }
        
        public void run() {
            TextEditorPopupSelector popup = root.getPopup(); 
            if (result == null || result.isEmpty()) {
                popup.setVisible(false);
                return;
            }
            
            List<PopupItem> items = new ArrayList();
            for (int i=0; i<popup.getRowSize(); i++) { 
                try { 
                    Object o = result.get(i); 
                    Object caption = root.getFormattedText(o);
                    items.add(new PopupItem(o, (caption == null? "": caption.toString()))); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" VerticalAdjustmentListener ">
    
    private class VerticalAdjustmentListener implements AdjustmentListener {
        
        XTextArea root = XTextArea.this;
        private boolean allowScrollDown;
        
        public void setAllowScrollDown(boolean allowScrollDown) {
            this.allowScrollDown = allowScrollDown; 
        }
        
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (e.getValueIsAdjusting()) return; 
            if (allowScrollDown || root.isAutoScrollDown()) { 
                e.getAdjustable().setValue(e.getAdjustable().getMaximum()); 
                allowScrollDown = false; 
            } 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TextWriterHandler ">
    
    private class TextWriterHandler implements TextWriter.Handler 
    {
        XTextArea root = XTextArea.this;
        
        public void write(String str) {
            root.append(str == null? "null": str); 
            root.verticalAdjHandler.setAllowScrollDown(true); 
            repaint();
        }

        public void clear() {
            root.setText(""); 
            repaint();
        }
        
        private void repaint() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    root.repaint(); 
                }
            }); 
        }        
    }
    
    // </editor-fold>
}
