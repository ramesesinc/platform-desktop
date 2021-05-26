/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.text;

import com.rameses.rcp.common.PopupItem;
import com.rameses.rcp.common.TextDocumentModel;
import com.rameses.rcp.common.TextWriter;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.util.TimerManager;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
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
 * @author wflores
 */
public class TextAreaImpl extends JTextArea {
    
    public final String ACTION_MAPPING_VK_SPACE = "ACTION_MAPPING_VK_SPACE";    
    public final String ACTION_MAPPING_VK_ESC   = "ACTION_MAPPING_VK_ESCAPE";    
    public final String ACTION_MAPPING_VK_DOWN   = "ACTION_MAPPING_VK_DOWN";    
    public final String ACTION_MAPPING_VK_UP   = "ACTION_MAPPING_VK_UP"; 
    public final String ACTION_MAPPING_VK_ENTER = "ACTION_MAPPING_VK_ENTER"; 
    
    private Color focusBackground;
    private Color disabledBackground;
    private Color enabledBackground;
    
    private TextDocument textDocument;
    private VerticalAdjustmentListener verticalAdjHandler;
    private TextWriterHandler textWriterHandler; 
        
    private String hint;
    private boolean allowShowHint;
    private boolean autoScrollDown;
    private boolean exitOnTabKey;
    private boolean readonly;
        
    private TrimSpaceOption trimSpaceOption;    
        
    public TextAreaImpl() {
        super();
        initComponent();
    }

    private void initComponent() {
        textDocument = new TextDocument();
        trimSpaceOption = TrimSpaceOption.NONE; 
        textWriterHandler = new TextWriterHandler(); 
        verticalAdjHandler = new VerticalAdjustmentListener(); 
        
        TextComponentSupport.getInstance().installUIDefaults(this); 
        
        for (FocusListener l : getFocusListeners()) {
            removeFocusListener(l); 
        }
        
        if (Beans.isDesignTime()) { 
            return;
        }
        
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
        
        addKeyListener(new KeyAdapterImpl()); 
    }
    
    public final TextDocument getTextDocument() {
        return textDocument; 
    }
        
    public void paint(Graphics origGraphics) {
        super.paint(origGraphics);
        
        if ( allowShowHint && getDocument().getLength() == 0 ) {
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
    }
    
    public void load() {
        setInputVerifier(UIInputUtil.VERIFIER); 
        setDocument(textDocument); 
    }
    
    public void configureHandlerObject( Object handerObject ) {
        if ( handerObject instanceof TextDocumentModel ) {
            ((TextDocumentModel) handerObject).setProvider(new DocumentProvider()); 
        }
        if ( handerObject instanceof TextWriter ) {
            ((TextWriter) handerObject).setHandler( textWriterHandler );
        } 
    }
    
        
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  "> 

    public TextCase getTextCase() {
        return textDocument.getTextCase();
    }    
    public void setTextCase(TextCase textCase) {
        textDocument.setTextCase(textCase);
    }
    
    public TrimSpaceOption getTrimSpaceOption() {
        return trimSpaceOption;
    }    
    public void setTrimSpaceOption(TrimSpaceOption trimSpaceOption) {
        this.trimSpaceOption = trimSpaceOption;
    }

    public String getHint() { 
        return hint; 
    }
    public void setHint(String hint) {
        this.hint = hint;
        allowShowHint = (hint != null && hint.trim().length() > 0);
    }
    
    public Color getFocusBackground() { 
        return focusBackground; 
    } 
    public void setFocusBackground( Color focusBackground ) {
        this.focusBackground = focusBackground;
    }
    
    public Color getDisabledBackground() {
        return disabledBackground; 
    }
    public void setDisabledBackground( Color disabledBackground ) {
        this.disabledBackground = disabledBackground; 
    }
    
    public Color getEnabledBackground() {
        return enabledBackground; 
    }
    public void setEnabledBackground( Color enabledBackground ) {
        this.enabledBackground = enabledBackground; 
    }
    
    public boolean isExitOnTabKey() {
        return exitOnTabKey; 
    }
    public void setExitOnTabKey( boolean exitOnTabKey ) {
        this.exitOnTabKey = exitOnTabKey;
    }

    public boolean isReadonly() { 
        return readonly; 
    } 
    public void setReadonly( boolean readonly ) {
        if (!isEnabled()) {
            return;
        }

        this.readonly = readonly;
        setEditable( !readonly ); 
        super.firePropertyChange("editable", readonly, !readonly);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods  ">
    
    public Color getBackground() {
        if (Beans.isDesignTime()) { 
            return super.getBackground();
        }
        
        Color encolor = getEnabledBackground();
        if (encolor == null) {
            encolor = UIManager.getLookAndFeelDefaults().getColor("TextField.background");
        }
        Color discolor = getDisabledBackground();
        if (discolor == null) {
            discolor = UIManager.getLookAndFeelDefaults().getColor("TextField.disabledBackground");
        }
        
        Color preferredColor = null;
        boolean enabled = isEnabled(); 
        if (enabled) {
            if (hasFocus()) {
                Color newColor = getFocusBackground();
                preferredColor = (newColor == null ? encolor: newColor);
            }
            else {
                preferredColor = encolor;
            } 
        } 
        else { 
            preferredColor = discolor;
        } 
        
        return (preferredColor == null ? super.getBackground() : preferredColor); 
    } 
    
    public void updateBackground() {
        Color encolor = getEnabledBackground();
        if (encolor == null) {
            encolor = UIManager.getLookAndFeelDefaults().getColor("TextField.background");
        }
        
        Color discolor = getDisabledBackground(); 
        if (discolor == null) {
            discolor = UIManager.getLookAndFeelDefaults().getColor("TextField.disabledBackground");
        }
        
        Color newColor = getBackground(); 
        setBackground(newColor); 
        repaint();
    }
    
    protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
            updateBackground();
        } 
        else if (e.getID() == FocusEvent.FOCUS_LOST) { 
            if (!e.isTemporary()) updateBackground(); 
        } 
        
        super.processFocusEvent(e); 
    } 

    @Override
    public void addNotify() {
        super.addNotify();
        
        if (getParent() instanceof JViewport) {
            if (getParent().getParent() instanceof JScrollPane) {
                JScrollPane jsp = (JScrollPane) getParent().getParent(); 
                jsp.getVerticalScrollBar().addAdjustmentListener( verticalAdjHandler );
            }
        }
    }
    
    // </editor-fold>    
        
    // <editor-fold defaultstate="collapsed" desc=" DocumentProvider ">
    
    private class DocumentProvider implements TextDocumentModel.Provider {
        
        final TextAreaImpl root = TextAreaImpl.this; 
        
        public String getText() { 
            return root.getText(); 
        }
        public void setText(String text) {
            root.setText((text==null? "": text)); 
            repaint();
        }
        
        public void insertText(String text) {
            if (text == null) return;

            int caretPos = root.getCaretPosition();
            try {
                int caretCharPos = (text == null? -1: text.indexOf('|'));
                if (caretCharPos >= 0) {
                    StringBuffer sb = new StringBuffer(); 
                    sb.append(text.substring(0, caretCharPos));
                    sb.append(' ');
                    sb.append(text.substring(caretCharPos+1));
                    text = sb.toString(); 
                }

                root.textDocument.insertString(caretPos, text, null);
                
                if (caretCharPos >= 0) {
                    root.setCaretPosition(caretPos + caretCharPos);
                }
            } 
            catch (BadLocationException ex) {
                System.out.println("[TextAreaImpl] failed to insert text at position " + caretPos + " caused by " + ex.getMessage());
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
        
        public int getWidth() {
            return 0; 
        }
        
        public int getHeight() {
            return 0;
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" KeyAdapterImpl ">   
    
    private class KeyAdapterImpl extends KeyAdapter {
        
        final TextAreaImpl root = TextAreaImpl.this;
        
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_TAB: 
                    if ( root.isExitOnTabKey() && root.hasFocus() ) { 
                        if ( e.isControlDown() || e.isAltDown() ) {
                            //do nothing 
                        } else if ( e.isShiftDown() ) {
                            e.consume();
                            root.transferFocusBackward();
                        } else  { 
                            e.consume();
                            root.transferFocus(); 
                        }                           
                    }
                    break;

                case KeyEvent.VK_ESCAPE: break;
                case KeyEvent.VK_DOWN: 
                    if (root.getPopup().isVisible() && root.getPopup().isShowing()) { 
                        e.consume();
                        root.getPopup().moveDown(); 
                    } 
                    break;

                case KeyEvent.VK_UP: 
                    if (root.getPopup().isVisible() && root.getPopup().isShowing()) { 
                        e.consume();
                        root.getPopup().moveUp(); 
                    }                         
                    break;

                case KeyEvent.VK_ENTER: 
                    if (root.getPopup().isVisible() && root.getPopup().isShowing()) { 
                        e.consume();  
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                root.fireSelectItem();
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

                        int pos = root.getCaretPosition()-1;
                        if (pos < 0) return;

                        String str = e.getKeyChar()+"";
                        boolean whitespace = str.matches("\\s");
                        if (whitespace) {
                            root.getPopup().setVisible(false);     
                            return;
                        }

                        if (root.getPopup().isVisible() && root.getPopup().isShowing() && !whitespace) { 
                            EventQueue.invokeLater(new Runnable() {
                                public void run() { 
                                    root.fireSearch();
                                }
                            });
                        } 

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }
            }
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">

    protected Object getFormattedText(Object item) {
        return item; 
    }
    
    protected String getTemplate(Object item) {
        return null; 
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

    protected List fetchList( Map params ) {
        return null; 
    }
    
    private class LookupTask implements Runnable {
        
        final TextAreaImpl root = TextAreaImpl.this;
        
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

    private class ResultDataLoader implements Runnable {
        
        final TextAreaImpl root = TextAreaImpl.this;
        
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
                } 
                catch(Throwable t) {
                    break; 
                } 
            }
            
            if (items.isEmpty()) {
                popup.setVisible(false);
                return;
            }
            
            popup.setData(items); 
            root.showPopup();
        }
    }
    
    // </editor-fold>       
    
    // <editor-fold defaultstate="collapsed" desc=" VerticalAdjustmentListener ">
    
    public boolean isAutoScrollDown() { 
        return autoScrollDown; 
    } 
    public void setAutoScrollDown(boolean autoScrollDown) {
        this.autoScrollDown = autoScrollDown; 
    }
    
    private class VerticalAdjustmentListener implements AdjustmentListener {
        
        final TextAreaImpl root = TextAreaImpl.this;
        
        boolean allowScrollDown;
        
        public void setAllowScrollDown(boolean allowScrollDown) {
            this.allowScrollDown = allowScrollDown; 
        }
        
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if ( e.getValueIsAdjusting()) {
                return;
            } 
            
            if ( allowScrollDown || root.isAutoScrollDown()) { 
                try {
                    e.getAdjustable().setValue(e.getAdjustable().getMaximum()); 
                } catch(Throwable t){;}
                
                allowScrollDown = false; 
            } 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TextWriterHandler ">
    
    private class TextWriterHandler implements TextWriter.Handler {
        
        final TextAreaImpl root = TextAreaImpl.this;
        
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
