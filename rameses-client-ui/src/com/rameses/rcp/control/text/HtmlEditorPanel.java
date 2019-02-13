/*
 * HtmlEditorPanel.java
 *
 * Created on April 4, 2014, 10:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.common.PopupItem;
import com.rameses.rcp.common.Task;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.util.TimerManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.Beans;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author wflores
 */
public class HtmlEditorPanel extends JPanel 
{
    final String ACTION_MAPPING_VK_SPACE = "ACTION_MAPPING_VK_SPACE";    
    final String ACTION_MAPPING_VK_ESC   = "ACTION_MAPPING_VK_ESCAPE";    
    final String ACTION_MAPPING_VK_DOWN   = "ACTION_MAPPING_VK_DOWN";    
    final String ACTION_MAPPING_VK_UP   = "ACTION_MAPPING_VK_UP"; 
    final String ACTION_MAPPING_VK_ENTER = "ACTION_MAPPING_VK_ENTER"; 
    
    private JPanel toolbar;
    private JTextPane editor;    
    private JScrollPane scrollpane;
    private HTMLEditorKit htmlkit;
    private HTMLDocument htmldoc;
    
    private String fontStyle;
    private boolean toolbarVisible;
    
    public HtmlEditorPanel() {
        initComponent();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent "> 
    
    private void initComponent() {
        super.setLayout(new BorderLayout()); 

        JTextPane ed = getEditor();
        ed.setDocument(new HTMLDocument());  
        ed.setEditable(true); 
        
        if (!Beans.isDesignTime()) { 
            ed.setContentType("text/html");
            htmldoc = (HTMLDocument) ed.getDocument();
            htmlkit = (HTMLEditorKit) ed.getEditorKit();
            htmlkit.getStyleSheet().addRule("A { color: #0000ff; }"); 
        } 

        JPanel toolbar = getToolbar(); 
        toolbar.setLayout(new ToolbarLayout()); 
        toolbar.add(new BoldActionButton()); 
        toolbar.add(new ItalicActionButton()); 
        toolbar.add(new UnderlineActionButton()); 
        toolbar.add(new StrikeThroughActionButton()); 
        toolbar.add(Box.createHorizontalStrut(10)); 
        toolbar.add(new BulletActionButton()); 
        toolbar.add(new NumberListActionButton()); 
        toolbar.add(Box.createHorizontalStrut(10)); 
        toolbar.add(new LinkActionButton()); 
        toolbar.add(new TestActionButton()); 
        add(toolbar, BorderLayout.NORTH); 
        toolbarVisible = true;        
        
        JScrollPane scrollpane = new JScrollPane(ed); 
        add(scrollpane); 
        
        KeyStroke vkspace = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK, true); 
        ed.getInputMap().put(vkspace, ACTION_MAPPING_VK_SPACE); 
        ed.getActionMap().put(ACTION_MAPPING_VK_SPACE, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                fireSearch();
            }
        }); 
        KeyStroke vkescape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0); 
        ed.getInputMap().put(vkescape, ACTION_MAPPING_VK_ESC); 
        ed.getActionMap().put(ACTION_MAPPING_VK_ESC, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                fireEscapeKeyEvent(); 
            }
        }); 
//        KeyStroke vkdown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0); 
//        ed.getInputMap().put(vkdown, ACTION_MAPPING_VK_DOWN); 
//        ed.getActionMap().put(ACTION_MAPPING_VK_DOWN, new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("fire move down");
//                fireMoveDown();
//            }
//        }); 
//        KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0); 
//        ed.getInputMap().put(vkup, ACTION_MAPPING_VK_UP); 
//        ed.getActionMap().put(ACTION_MAPPING_VK_UP, new AbstractAction() {
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("fire move up");
//                fireMoveUp();
//            }
//        }); 
        ed.addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
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
                            //System.out.println("keycode: " + e.getKeyCode() + ", keychar: " + e.getKeyChar());
                            int pos = editor.getCaretPosition()-1;
                            if (pos < 0) return;
                            
                            String str = e.getKeyChar()+"";
                            boolean whitespace = str.matches("\\s");
                            //System.out.println("str=" + str + ", whitespace="+whitespace);
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
    
    private JTextPane getEditor() {
        if (editor == null) {
            editor = new JTextPane();
        }
        return editor; 
    }
    
    private JPanel getToolbar() {
        if (toolbar == null) {
            toolbar = new JPanel();
        }
        return toolbar;
    }
    
    private Style defaultStyle;
    private Style getDefaultStyle() {
        if (defaultStyle == null) { 
            defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE); 
        } 
        return defaultStyle; 
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters "> 
    
    public void setName(String name) {
        super.setName(name);
        if (Beans.isDesignTime()) {
            setText(name == null? "": name);
        }
    }
    
    public String getText() {
        String sval = (editor == null? null: editor.getText()); 
        if (sval == null || sval.length() == 0) return null; 
        
        return sval;
    }
    public void setText(String text) {
        if (editor != null) {
            editor.setText(text == null? "": text);
        } 
    }
    
    public Object getValue() {
        return getText(); 
    }
    
    public void setValue(Object value) {
        try { 
            URL url = null; 
            if (value == null) {
                editor.setText("");
                editor.setCaretPosition(0);
            } else if (value instanceof URL) {
                url = (URL) value;
            } else if (value.toString().toLowerCase().matches("^[a-zA-Z0-9]{1,}://.*$")) {
                url = new URL(value.toString()); 
            } else { 
                editor.setText(value.toString()); 
                editor.setCaretPosition(0); 
            } 

            if (url != null) { 
                URLWorkerTask uwt = new URLWorkerTask(url);
                ClientContext.getCurrentContext().getTaskManager().addTask(uwt); 
            } 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    protected HTMLDocument getDocument() {
        return htmldoc;
    }
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
    }
    
    public void setFont(Font font) {
        super.setFont(font);
        getEditor().setFont(font); 
    }
    
    public void setEnabled(boolean enabled) { 
        super.setEnabled(enabled); 
        if (isToolbarVisible()) toolbar.setVisible(enabled);
    } 
    
    public boolean isEditable() {
        return editor.isEditable(); 
    }
    public void setEditable(boolean editable) {
        editor.setEditable(editable);
        if (isToolbarVisible()) toolbar.setVisible(editable); 
    }
    
    public boolean isToolbarVisible() { 
        return toolbarVisible;
    }
    public void setToolbarVisible(boolean toolbarVisible) {
        this.toolbarVisible = toolbarVisible;
        getToolbar().setVisible(toolbarVisible);
    }
    
    public void setRequestFocus(boolean focus) {
        if (editor.isEnabled()) editor.requestFocus();  
    } 
    
    protected Object getEditorClientProperty(Object key) {
        return editor.getClientProperty(key); 
    }
    protected void setEditorClientProperty(Object key, Object value) {
        editor.putClientProperty(key, value);
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    private String getSearchtext() {
        try { 
            int caretpos = editor.getCaretPosition(); 
            StringBuffer sb = new StringBuffer(); 
            for (int i=caretpos-1; i>=0; i--) {
                String str = htmldoc.getText(i, 1); 
                if (str.matches("\\s")) break;
                
                if (sb.length() == 0) { 
                    sb.append(str); 
                } else {
                    sb.insert(0, str); 
                }
            } 
            return (sb.length() == 0? null: sb.toString()); 
        } catch(Throwable t) {
            t.printStackTrace();
            return null; 
        }        
    }
    
    private void fireSearch() {
        String searchtext = getSearchtext();
        Map params = new HashMap();
        params.put("searchtext", searchtext); 
        List results = fetchList(params); 
        if (results == null) return;
        
        TimerManager.getInstance().schedule(new LookupTask(params), 300); 
    }
    
    private int getWhitespacePositionBefore(int pos) {
        try { 
            for (int i=pos-1; i>=0; i--) {
                String str = htmldoc.getText(i, 1); 
                if (str.matches("\\s")) return i;
            } 
            return -1; 
        } catch(Throwable t) {
            return -1;
        } 
    }
    
    private void fireSelectItem() { 
        PopupItem pi = getPopup().getSelectedItem(); 
        String template = getTemplate(pi.getUserObject()); 
        if (template == null) template = "";
        
        int doclen = htmldoc.getLength();
        int curpos = editor.getCaretPosition();
        int startpos = getWhitespacePositionBefore(curpos)+1; 
        
        try { 
            htmldoc.replace(startpos, curpos-startpos, "", null); 
            htmldoc.insertString(startpos, template, null); 
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
    
    protected List fetchList(Map params) {
        return null; 
    }
    
    protected Object getFormattedText(Object item) {
        return (item == null? null: item.toString()); 
    }
    
    protected String getTemplate(Object item) {
        return (item == null? null: item.toString());
    }
    
    protected void setEditorInputVerifier(InputVerifier inputVerifier) {
        getEditor().setInputVerifier(inputVerifier); 
    }
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" BoldActionButton ">
    
    private ImageIcon getImageIcon(String name) { 
        try {
            ClassLoader loader = null;
            if ( Beans.isDesignTime() ) 
                loader = getClass().getClassLoader();
            else 
                loader = ClientContext.getCurrentContext().getClassLoader();

            URL url = loader.getResource(name);
            return new ImageIcon(url);
        } catch (Throwable ex) {
            System.out.println("[WARN] failed to load icon caused by " + ex.getClass().getName() + ": " + ex.getMessage()); 
            return null; 
        } 
    } 
    
    private class BoldActionButton extends JButton 
    {
        BoldActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            setToolTipText("Bold");
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/bold.png")); 
            addActionListener(new StyledEditorKit.BoldAction());
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ItalicActionButton ">    
    
    private class ItalicActionButton extends JButton 
    {
        ItalicActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("Italic");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/italic.png")); 
            addActionListener(new StyledEditorKit.ItalicAction());
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UnderlineActionButton ">    
    
    private class UnderlineActionButton extends JButton 
    {
        UnderlineActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("Underline");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/underline.png")); 
            addActionListener(new StyledEditorKit.UnderlineAction());
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" StrikeThroughActionButton ">    
    
    private class StrikeThroughActionButton extends JButton implements ActionListener
    {
        StrikeThroughActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("StrikeThrough");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/strikethrough.png")); 
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (!editor.isEnabled()) return;
            if (!editor.hasFocus()) editor.grabFocus(); 

            MutableAttributeSet attr = htmlkit.getInputAttributes();
            boolean toggle = (StyleConstants.isStrikeThrough(attr)? false : true);
            SimpleAttributeSet sas = new SimpleAttributeSet();
            StyleConstants.setStrikeThrough(sas, toggle);
            setCharacterAttributes(sas, false);            
        }
        
        private void setCharacterAttributes(AttributeSet attr, boolean replace) {
	    int p0 = editor.getSelectionStart();
	    int p1 = editor.getSelectionEnd();
	    if (p0 != p1) {
		htmldoc.setCharacterAttributes(p0, p1 - p0, attr, replace);
	    }
            
	    MutableAttributeSet inputAttributes = htmlkit.getInputAttributes();
	    if (replace) inputAttributes.removeAttributes(inputAttributes);

	    inputAttributes.addAttributes(attr);
	}        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" BulletActionButton ">    
    
    private class BulletActionButton extends JButton 
    {
        BulletActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            
            String html = "<ul><li></li></ul>";
            HTMLEditorKit.InsertHTMLTextAction action = new HTMLEditorKit.InsertHTMLTextAction(
                "Bullets",  html, HTML.Tag.UL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.UL
            );
            setAction(action);
            setText("");
            setToolTipText("Bullet list");
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/bullet.png")); 
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NumberListActionButton ">    
    
    private class NumberListActionButton extends JButton 
    {
        NumberListActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            
            String html = "<ol><li></li></ol>";
            HTMLEditorKit.InsertHTMLTextAction action = new HTMLEditorKit.InsertHTMLTextAction(
                "NumberedList",  html, HTML.Tag.OL, HTML.Tag.LI, HTML.Tag.BODY, HTML.Tag.OL
            );
            setAction(action);
            setText("");
            setToolTipText("Number list");
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/numberlist.png")); 
        }
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LinkActionButton ">    
    
    private class LinkActionButton extends JButton implements ActionListener
    {
        LinkActionButton() {
            super();
            setMargin(new Insets(1,3,1,3)); 
            setToolTipText("Hyperlink");
            setFocusable(false); 
            setIcon(getImageIcon("com/rameses/rcp/icons/editor/link.png")); 
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (!editor.isEnabled()) return;
            if (!editor.hasFocus()) editor.grabFocus(); 

            StyledDocument doc = editor.getStyledDocument();
            int startpos = editor.getSelectionStart();
            String seltext = editor.getSelectedText();
            if (seltext == null || seltext.length() == 0) { 
                //do nothing
            } else { 
                Object initialValue = null;
                MutableAttributeSet attr = htmlkit.getInputAttributes();  
                Object anchor = attr.getAttribute(HTML.Tag.A);
                if (anchor instanceof SimpleAttributeSet) {
                    SimpleAttributeSet aset = (SimpleAttributeSet)anchor;
                    initialValue = aset.getAttribute(HTML.Attribute.HREF);
                }
                
                HtmlLinkDialog dialog = new HtmlLinkDialog();
                dialog.setCaption("Enter a URL: ");
                dialog.setValue(initialValue);
                if (dialog.open(HtmlEditorPanel.this) != HtmlLinkDialog.APPROVE_OPTION) return; 
                
                //String surl = JOptionPane.showInputDialog(HtmlEditorPanel.this, "Enter a URL: ", (initialValue==null? null: initialValue.toString())); 
                //if (surl == null) return;
                
                String surl = dialog.getValue(); 
                if (surl == null || surl.length() == 0) {
                    if (anchor == null) return;
                    
                    attr.removeAttribute(HTML.Tag.A);
                    try {
                        int len = seltext.length();
                        htmldoc.remove(startpos, len);
                        htmldoc.insertString(startpos, seltext, attr); 
                        editor.select(startpos, startpos+len);
                    } catch(Throwable t) {;}
                } else { 
                    SimpleAttributeSet ahref = new SimpleAttributeSet();
                    ahref.addAttribute(HTML.Attribute.HREF, surl);

                    SimpleAttributeSet alink = new SimpleAttributeSet();
                    alink.addAttribute(HTML.Tag.A, ahref); 
                    setCharacterAttributes(alink, false); 
                } 
            } 
        } 
        
        private void setCharacterAttributes(AttributeSet attr, boolean replace) {
	    int p0 = editor.getSelectionStart();
	    int p1 = editor.getSelectionEnd();
	    if (p0 != p1) {
		htmldoc.setCharacterAttributes(p0, p1 - p0, attr, replace);
	    }
            
	    MutableAttributeSet inputAttributes = htmlkit.getInputAttributes();
	    if (replace) inputAttributes.removeAttributes(inputAttributes);

	    inputAttributes.addAttributes(attr);
	} 
        
        private boolean isStyleApplied(StyledDocument doc, String seltext, int startpos) {
            int len = (seltext == null? 0: seltext.length());
            if (len == 0) return false;

            for (int i=0; i<len; i++) { 
                AttributeSet attrs = doc.getCharacterElement(startpos+i).getAttributes();
                if (!StyleConstants.isStrikeThrough(attrs)) return false; 
            } 
            return true;
        }        
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TestActionButton ">    
    
    private class TestActionButton extends JButton implements ActionListener
    {
        private Style style;
        
        TestActionButton() { 
            super("Test"); 
            setMargin(new Insets(1,3,1,3)); 
            setFocusable(false); 
            addActionListener(this); 
        } 
        
        public void actionPerformed(ActionEvent e) {
            //System.out.println(editor.getText());
            Document doc = editor.getDocument();
            String text = "";
            try {
                text = doc.getText(0, doc.getLength());
                //System.out.println(text);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            
            StringBuffer sb = new StringBuffer(); 
            int len = doc.getLength();
            StyledDocument sdoc = editor.getStyledDocument();
            for (int i=0; i<len; i++) {
                
            }
        } 
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ToolbarLayout ">
    
    private class ToolbarLayout implements LayoutManager
    {
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                int w=0, h=0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    w += dim.width;
                    h = Math.max(dim.height, h); 
                }
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom); 
                return new Dimension(w, h); 
            }
        }

        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int pw = parent.getWidth();
                int ph = parent.getHeight(); 
                int x = margin.left;
                int y = margin.top;
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Component c = comps[i];
                    if (!c.isVisible()) continue;
                    
                    Dimension dim = c.getPreferredSize();
                    c.setBounds(x, y, dim.width, h); 
                    x += dim.width;
                }
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" URLWorkerTask (class) "> 
    
    private class URLWorkerTask extends Task 
    { 
        private URL url;
        private boolean done;
        
        URLWorkerTask(URL url) {
            this.url = url; 
        }
        
        public boolean accept() {
            return (done? false: true); 
        }

        public void execute() {
            try {
                editor.setText("<html><body><b>Loading...</b></body></html>");
                if (url != null) editor.setPage(url); 
                
                editor.setCaretPosition(0);
            } catch(Throwable t) { 
                editor.setText("<html><body color=\"red\">error caused by "+t.getMessage()+"</body></html>"); 
                
                if (ClientContext.getCurrentContext().isDebugMode()) t.printStackTrace(); 
            } finally { 
                done = true;                 
            } 
        }    
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" LookupTask ">
        
    private class LookupTask implements Runnable 
    {
        HtmlEditorPanel root = HtmlEditorPanel.this;
        
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
    
    private HtmlEditorPopup jpopup;
    
    private HtmlEditorPopup getPopup() {
        if (jpopup == null) {
            jpopup = new HtmlEditorPopup(editor);
            jpopup.add(new HtmlEditorPopup.SelectionListener(){
                public void onselect(PopupItem item) {
                    fireSelectItem(); 
                }
            });
        }
        return jpopup;
    }
    
    private void showPopup() {
        try { 
            Rectangle rect = editor.modelToView(editor.getCaretPosition()); 
            HtmlEditorPopup popup = getPopup(); 
            popup.pack();
            popup.setSize(100, 20);
            popup.show(editor, rect.x, rect.y+rect.height); 
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private class ResultDataLoader implements Runnable 
    {
        HtmlEditorPanel root = HtmlEditorPanel.this;
        private List result;
        
        ResultDataLoader(List result) {
            this.result = result;
        }
        
        public void run() {
            HtmlEditorPopup popup = root.getPopup(); 
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
    
}
