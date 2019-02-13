/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.swing;

import com.rameses.rcp.common.DocViewModel;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.ColorUtil;
import com.rameses.rcp.support.FontSupport;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author wflores
 */
public class HtmlViewPane extends JEditorPane 
{
    private String fontStyle;
    private Point mousePoint; 
    private DocViewModel docModel; 
    
    private Font templateFont;
    
    private ArrayList<ActionParam> actionParams;
    
    public HtmlViewPane() {
        super(); 
        super.setEditable(false); 
        super.setContentType("text/html");
        
        actionParams = new ArrayList(); 
        CompEditorKit editorKit = new CompEditorKit(); 
        Document doc = editorKit.createDefaultDocument(); 
        super.setEditorKit( editorKit );
        super.setDocument( doc ); 
        
        addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                //if( e.getEventType() == EventType.ENTERED ) {
                    //SimpleAttributeSet red = new SimpleAttributeSet( );
                //    try {
                //        System.out.println("elementy is " + e.getSourceElement().getElement(0).getName());
                        //int startPos = e.getSourceElement().getStartOffset();
                        //int endPos = e.getSourceElement().getEndOffset();
                        //System.out.println("text is " + e.getSourceElement().getDocument().getText( startPos, endPos));    
                //    }catch(Exception ex){;}
                //}
                //else if( e.getEventType() == EventType.EXITED ) {
                //    System.out.println("exiting..."+e.getSourceElement().getName());
                //}
                hyperlinkUpdateImpl(e); 
            }
        });
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                mouseClickedImpl(e);
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                mousePoint = e.getPoint(); 
            } 
        }); 
        
        try { 
            templateFont = UIManager.getLookAndFeelDefaults().getFont("TextField.font"); 
            templateFont = new Font( templateFont.getFontName(), templateFont.getStyle(), templateFont.getSize()+1);  
            super.setFont( templateFont ); 
            updateStyleSheet();
        } catch(Throwable t) {;} 
        
        setMargin(new Insets(5,5,5,5));
        
        if (Beans.isDesignTime()) {
            setPreferredSize(new Dimension(100,50));
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters / Setters "> 
    
    protected Point getMousePoint() { return mousePoint; } 
    
    public String getFontStyle() { return fontStyle; } 
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
        new FontSupport().applyStyles(this, fontStyle);
        updateStyleSheet(); 
    }

    public void setDocument(Document doc) {
        try { 
            if (doc instanceof HTMLDocument) {
                ClassLoader cloader = ClientContext.getCurrentContext().getClassLoader();
                URL url = cloader.getResource("images"); 
                if (url != null) ((HTMLDocument) doc).setBase(url); 
            } 
        } catch(Throwable t) {;}
        
        super.setDocument(doc); 
    }
    
    public void setVisible(boolean visible) {
        Container parent = getParent(); 
        if (parent instanceof JViewport) {
            parent = parent.getParent(); 
            if (parent instanceof JScrollPane) {
                parent.setVisible(visible); 
                return;
            }
        }
        super.setVisible(visible); 
    }
    
    protected StyleSheet getStyleSheet() {
        HTMLDocument doc = (HTMLDocument) getDocument();
        return doc.getStyleSheet();
    }    
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" helper methods "> 
    
    protected void processAction(String name, Map params) {
    }
    
    private void hyperlinkUpdateImpl(HyperlinkEvent e) {
        if (!(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)) return;

        Map params = new HashMap(); 
        AttributeSet aset = e.getSourceElement().getAttributes();
        Enumeration en = aset.getAttributeNames();
        while (en.hasMoreElements()) {
            Object k = en.nextElement();
            Object v = aset.getAttribute(k);
            if (v instanceof AttributeSet) {
                AttributeSet vset = (AttributeSet) v; 
                Enumeration ven = vset.getAttributeNames();
                while (ven.hasMoreElements()) {
                    Object vk = ven.nextElement();
                    Object vv = vset.getAttribute(vk); 
                    params.put(vk.toString(), vv); 
                }
            }
        } 
        
        Object href = params.get("href");
        if (href == null) return;
        
        String shref = href.toString();
        if (!shref.matches("[a-zA-Z0-9_]{1,}")) return;
        
        actionParams.add( new ActionParam(shref, params)); 
    }
    
    private void mouseClickedImpl(MouseEvent e) {
        if ( e != null && e.getClickCount() == 1 && !actionParams.isEmpty()) {
            ActionParam ap = actionParams.get(0); 
            actionParams.clear(); 
            
            processAction( ap.name, ap.param ); 
        }
        actionParams.clear();         
    }
    
    private class ActionParam {
        
        private String name;
        private Map param;
        
        ActionParam(String name, Map param) {
            this.name = name; 
            this.param = param;
        }
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" CompEditorKit "> 
    
    private void updateStyleSheet() {
        EditorKit kit = getEditorKit();
        if ( kit instanceof HTMLEditorKit ) {
            HTMLEditorKit htmlkit = (HTMLEditorKit) kit;
            StyleSheet ss = htmlkit.getStyleSheet(); 
            
            Font font = getFont(); 
            if ( font != null ) {
                ss.addRule("html {font-family:"+ font.getFontName() +"; font-size:"+ font.getSize() +";}"); 
                ss.addRule("body {font-family:"+ font.getFontName() +"; font-size:"+ font.getSize() +";}"); 
            }
        }
    }
    
    class CompEditorKit extends HTMLEditorKit {
        public ViewFactory getViewFactory() {
            return new CompFactory();
        } 
    }   
    
    class CompFactory extends HTMLEditorKit.HTMLFactory {
        public CompFactory() {
            super();
        }
        
        @Override
        public View create(Element element) {
            AttributeSet attrs = element.getAttributes();
	    Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
	    Object o = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
	    if (o instanceof HTML.Tag) {  
                HTML.Tag tag = (HTML.Tag)o;
                if (tag == HTML.Tag.INPUT) {
                    TagInfo taginfo = new TagInfo(element);
                    taginfo.parse();
                    
                    if ("button".equals(taginfo.getString("type"))) {
                        return new ButtonView(element, taginfo); 
                    }
                }
            }
            return super.create(element);
        }
    }
    
    class ButtonView extends ComponentView implements ActionListener
    {
        private TagInfo taginfo;
        
        public ButtonView(Element element, TagInfo taginfo) {
            super(element);
            this.taginfo = taginfo;
        }
        
        @Override
        protected Component createComponent() {
            JButton button = new JButton(); 
            button.setText(taginfo.getString("value")); 
            button.addActionListener(this);

            StyleSheet sheet = getStyleSheet();
            Font font = sheet.getFont(getAttributes()); 
            if (font != null) { button.setFont(font); }

            CSSInfo css = taginfo.getCSSInfo();
            Integer border = css.getInteger("border", 1);
            if (border == 0) {
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
            } else {
                button.setContentAreaFilled(true);
                button.setBorderPainted(true);
            }

            Insets padding = css.getInsets("padding");
            if (padding != null) { button.setMargin(padding); }
            
            Color color = css.getColor(); 
            if (color != null) { button.setForeground(color); }
            
            color = css.getBackground();
            if (color != null) { button.setBackground(color); }
            return button;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Map params = taginfo.getProperties(); 
            Object obj = params.get("onclick");
            processAction(obj==null? null: obj.toString(), params);
        }
    }
    
    class TagInfo 
    {
        private Element elem;
        private Map attrs;
        private Map css;
        
        private CSSInfo cssinfo;
        
        public TagInfo(Element elem) {
            this.elem = elem; 
        }
        
        void parse() {
            attrs = new HashMap();
            css = new HashMap();
            
            AttributeSet aset = elem.getAttributes();
            Enumeration en = aset.getAttributeNames();
            while (en.hasMoreElements()) {
                Object k = en.nextElement();
                Object v = aset.getAttribute(k);
                if (v == null) continue;
                
                //System.out.println(k + ", " + k.getClass().getName() + ", "+ v + ", " + v.getClass().getName()); 
                if (k instanceof HTML.Attribute) {
                    attrs.put(k.toString(), v.toString()); 
                } else if (k instanceof CSS.Attribute) {
                    css.put(k.toString(), v.toString()); 
                } else {
                    attrs.put(k.toString(), v.toString());
                } 
            } 
            cssinfo = new CSSInfo(elem, css); 
        }       
        
        CSSInfo getCSSInfo() { return cssinfo; }
        
        Map getProperties() { return attrs; }
        
        Object get(Object name) {
            return (attrs == null? null: attrs.get(name)); 
        }
        
        String getString(Object name) {
            Object value = get(name);
            return (value == null? null: value.toString()); 
        }  
        
        Integer getInteger(Object name) {
            return getInteger(name, null); 
        } 
        Integer getInteger(Object name, Integer defaultValue) {
            try { 
                return new Integer(get(name).toString()); 
            } catch(Throwable t) {
                return defaultValue; 
            }
        }
    }
    
    class CSSInfo 
    {
        private Element elem;
        private Map map; 
        
        CSSInfo(Element elem, Map map) {
            this.elem = elem;
            this.map = map; 
        } 
        
        boolean containsKey(Object name) {
            return map.containsKey(name); 
        }
        Object get(Object name) {
            return (map == null? null: map.get(name)); 
        }
        String getString(Object name) {
            Object value = get(name);
            return (value == null? null: value.toString()); 
        }  
        Integer getInteger(Object name) {
            return getInteger(name, null); 
        } 
        Integer getInteger(Object name, Integer defaultValue) {
            try { 
                return new Integer(get(name).toString()); 
            } catch(Throwable t) {
                return defaultValue; 
            }
        }         
        Insets getInsets(Object name) {
            Integer top = getInteger(name+"-top"); 
            Integer left = getInteger(name+"-left"); 
            Integer bottom = getInteger(name+"-bottom"); 
            Integer right = getInteger(name+"-right"); 
            if (top == null && left == null && bottom == null && right == null) {
                return null; 
            } else {
                if (top == null) {top = 0;} 
                if (left == null) {left = 0;}
                if (bottom == null) {bottom = 0;}
                if (right == null) {right = 0;}
                return new Insets(top.intValue(), left.intValue(), bottom.intValue(), right.intValue()); 
            }
        }
        Color getColor() {
            try { 
                Object o = elem.getAttributes().getAttribute(CSS.Attribute.COLOR); 
                return ColorUtil.decode(o.toString()); 
            } catch(Throwable t) {
                return null; 
            }
        }
        Color getBackground() {
            try { 
                Object o = elem.getAttributes().getAttribute(CSS.Attribute.BACKGROUND_COLOR); 
                return ColorUtil.decode(o.toString()); 
            } catch(Throwable t) {
                return null; 
            }
        }        
        int getTextAlign() {
            Object o = elem.getAttributes().getAttribute(CSS.Attribute.TEXT_ALIGN);
            if (o == null) { return -1; }
            
            String sval = o.toString();
            if (sval.equals("left")) {
                return SwingConstants.LEFT; 
            } else if (sval.equals("center")) {
                return SwingConstants.CENTER; 
            } else if (sval.equals("right")) {
                return SwingConstants.RIGHT; 
            } else {
                return -1; 
            }
        }
        int getVerticalAlign() {
            Object o = elem.getAttributes().getAttribute(CSS.Attribute.VERTICAL_ALIGN);
            if (o == null) { return -1; }
            
            String sval = o.toString();
            if (sval.equals("top")) {
                return SwingConstants.TOP;
            } else if (sval.equals("center")) {
                return SwingConstants.CENTER; 
            } else if (sval.equals("bottom")) {
                return SwingConstants.BOTTOM; 
            } else {
                return -1; 
            }
        }
        int getWidth() {
            try { 
                Object o = elem.getAttributes().getAttribute(CSS.Attribute.WIDTH); 
                return Integer.parseInt(o.toString()); 
            } catch(Throwable t) {
                return -1; 
            }
        }
        int getHeight() {
            try { 
                Object o = elem.getAttributes().getAttribute(CSS.Attribute.HEIGHT); 
                return Integer.parseInt(o.toString()); 
            } catch(Throwable t) {
                return -1; 
            }
        }
    }
    
    // </editor-fold>
}
