/*
 * ItemPanel.java
 *
 * Created on October 18, 2010, 1:03 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.rcp.constant.UIConstants;
import com.rameses.rcp.support.FontSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UISubControl;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


public class ItemPanel extends JPanel implements FormItemProperty {
    
    /**
     * wrapper is usually JScrollPane
     */
    private XLabel label;
    private XFormPanel formPanel;    
    private ControlProperty property;
    private Component editorWrapper;
    private Component editor; 
    private Insets padding; 
    private UIModel uimodel;
    
    private String captionVAlignment = UIConstants.TOP; 
    private String captionHAlignment = UIConstants.LEFT;
    
    public ItemPanel(XFormPanel parent, Component editor) {
        JScrollPane container = null;
        if ( editor instanceof JTextArea || editor instanceof JEditorPane ) {
            container = new JScrollPane();
            container.setViewportView(editor);
        }
        initComponents(parent, editor, container);
        //setBorder( BorderFactory.createLineBorder(Color.BLUE)); 
    }
    
    public ItemPanel(XFormPanel parent, Component editor, Component container) {
        initComponents(parent, editor, container);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents ">
    
    private void initComponents(XFormPanel parent, Component editor, Component container) 
    {
        this.formPanel = parent;
        this.editor = editor;
        this.editorWrapper = container;
        this.uimodel = new UIModel();
        
        if( container instanceof JScrollPane && !container.isPreferredSizeSet() ) {
            JScrollPane jsp = (JScrollPane) container;
            JViewport view = jsp.getViewport();
            Dimension d = view.getViewSize();
            
            Insets i = view.getInsets();
            d.width += i.left + i.right;
            d.height += i.top + i.bottom;
            
            i = jsp.getInsets();
            d.width += i.left + i.right;
            d.height += i.top + i.bottom;
                        
            container.setPreferredSize(d);
        }

        ActiveControl con = (ActiveControl) editor;
        property = con.getControlProperty();
        
        setOpaque(false);
        setLayout(new ItemPanelLayout(property));
        
        if( property.getCellPadding() != null ) {
            padding = property.getCellPadding();
        }
        
        label = new XLabel(true);
        label.setLabelFor(editor);
        label.setAddCaptionColon(parent.isAddCaptionColon());
        
        if( property.getCaptionFont() != null ) {
            label.setFont( property.getCaptionFont() );
        } else {
            label.setFont(parent.getCaptionFont());
        }
                
        label.setForeground(parent.getCaptionForeground());
        
        if ( !ValueUtil.isEmpty(label.getText()) )
            label.setBorder(parent.getCaptionBorder());
        
        new FontSupport().applyStyles(label, property.getCaptionFontStyle()); 
        
        add(label, "label");
        
        if ( container != null ) {
            add(container, "editor");
        } else {
            add(editor, "editor");
        }
        
        PropertyChangeListener pcl = new ControlPropetyListener(property);
        property.addPropertyChangeListener(pcl);
        
        editor.addComponentListener(new ComponentListener() {
            public void componentMoved(ComponentEvent e) {}
            public void componentResized(ComponentEvent e) {}
            
            public void componentHidden(ComponentEvent e) {
                ItemPanel.this.setVisible(false);
            }
            
            public void componentShown(ComponentEvent e) {
                ItemPanel.this.setVisible(true);
            }
        });
        
        //super.setBorder(BorderFactory.createLineBorder(Color.RED)); 
    }
    
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" helper methods ">
    
    public void updateLabelComponent() {
        if (label == null) return;

        label.setForeground( formPanel.getCaptionForeground() ); 
        label.setPadding( formPanel.getCaptionPadding() );
        label.setBorder( formPanel.getCaptionBorder() );         
        label.setAddCaptionColon( formPanel.isAddCaptionColon() );         
        label.setCaptionWidth( formPanel.getCaptionWidth() ); 
    }
    
    public void updateLabelFont( Font font ) {
        if (label == null || font == null ) return; 
        
        label.setFont( font ); 
    }
    public void updateLabelFont( FontSupport fs, String style ) {
        if (label == null || fs == null || style == null) return; 
        
        fs.applyStyles(label, style); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public boolean match(Component editor) {
        if (editor == null) return false;
        
        return (this.editor == editor);
    }
    
    public Component getEditorComponent() { return editor; }
    public Component getEditorWrapper() { return editorWrapper; }
    public XLabel getLabelComponent() { return label; }
    public ControlProperty getControlProperty() { return property; }
    
    public Insets getInsets() {
        Insets insets = new Insets(0,0,0,0);
        Insets i = super.getInsets();
        if( i != null ) {
            insets.top += i.top;
            insets.left += i.left;
            insets.bottom += i.bottom;
            insets.right += i.right;
        }
        
        if( padding != null ) {
            insets.top += padding.top;
            insets.left += padding.left;
            insets.bottom += padding.bottom;
            insets.right += padding.right;
        }
        
        return insets;
    }
    
    public Insets getInsets(Insets insets) {
        Insets i = this.getInsets();
        
        if( insets == null ) return i;
        
        insets.top = i.top;
        insets.left = i.left;
        insets.bottom = i.bottom;
        insets.right = i.right;
        
        return insets;
    } 
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" FormItemProperty ">
    
    public int getStretchWidth() {
        return uimodel.getStretchWidth(); 
    }

    public int getStretchHeight() {
        return uimodel.getStretchHeight(); 
    }    
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ControlPropetyListener ">
    
    private class ControlPropetyListener implements PropertyChangeListener 
    {
        ItemPanel panel = ItemPanel.this;
        private ControlProperty property;
        private Font sourceFont;
        
        ControlPropetyListener(ControlProperty property) {
            this.property = property;
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            String propName = evt.getPropertyName();
            Object value = evt.getNewValue();
            
            if ("caption".equals(propName)) {
                Border b = panel.getLabelComponent().getBorder();
                if ( ValueUtil.isEmpty(value) && !(b instanceof EmptyBorder)) {
                    panel.getLabelComponent().setBorder(BorderFactory.createEmptyBorder());
                } else if ( !ValueUtil.isEqual(b, formPanel.getCaptionBorder()) ) {
                    panel.getLabelComponent().setBorder(formPanel.getCaptionBorder());
                }
            
            } else if ("captionFont".equals(propName)) {
                if (sourceFont == null) 
                    sourceFont = panel.getLabelComponent().getFont();
                
                if (value instanceof Font) {
                    panel.getLabelComponent().setFont((Font) value);
                } else { 
                    panel.getLabelComponent().setFont(sourceFont); 
                } 
                
            } else if ("captionFontStyle".equals(propName)) {
                String fontStyle = (value == null? null: value.toString()); 
                panel.getLabelComponent().setFontStyle(fontStyle); 
                
            } else if ("captionMnemonic".equals(propName)) { 
                char ch = '\u0000';
                String sval = (value == null? null: value.toString()); 
                if (sval == null || sval.length() == 0) ch = sval.charAt(0);
                
                panel.getLabelComponent().setDisplayedMnemonic(ch); 
            
            } else if ("captionWidth".equals(propName) ) {
                panel.revalidate();
                
            } else if ("showCaption".equals(propName)) {
                panel.revalidate();
                
            } else if ("cellPadding".equals(propName)) {
                Insets padding = (value instanceof Insets? (Insets)value: null); 
                if (padding == null) padding = new Insets(0,0,0,0); 
                
                panel.padding = padding; 
                panel.revalidate();
            }
        }
        
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIModel ">
    
    private class UIModel {
        Component label;
        Component editor;
        UIControl uicontrol;
        ControlProperty property;
        
        void setEditor(Component editor) {
            this.editor = editor;
            this.uicontrol = null; 
            this.property = null;
            
            Component comp = editor; 
            if (editor instanceof JScrollPane) { 
                JScrollPane jsp = (JScrollPane)editor; 
                comp = jsp.getViewport().getView(); 
            }
            if (comp instanceof UIControl) { 
                uicontrol = (UIControl)comp; 
            } 
            if (comp instanceof ActiveControl) {
                property = ((ActiveControl)comp).getControlProperty(); 
            }
        }
        
        void setLabel(Component label) {
            this.label = label; 
        }
        
        boolean isEditorVisible() {
            return (editor != null && editor.isVisible());
        } 
        Dimension getEditorPreferredSize() {
            return (editor == null? new Dimension(0,0): editor.getPreferredSize()); 
        } 
        int getStretchWidth() {
            int sw = 0;
            if (editor == null) {
                //do nothing 
            } else {
                sw = (uicontrol == null? 0: uicontrol.getStretchWidth()); 
                if (sw > 100) { sw = 100; } 
            }
            
            if ( sw == 0 ) {
                if ( editor instanceof UISubControl ) {
                    sw = 100; 
                } else if ( editor != null && editor.getPreferredSize().width == 0 ) {
                    sw = 100; 
                } 
            }
            return sw; 
        } 
        int getStretchHeight() {
            return (uicontrol == null? 0: uicontrol.getStretchHeight());
        }     
                
        boolean isShowCaption() {
            return (property == null? false: property.isShowCaption());
        }       
        boolean isLabelVisible() {
            return (label != null && label.isVisible() && isShowCaption()); 
        } 
        int getPreferredCaptionWidth() {
            int cw = (property == null? 0: property.getCaptionWidth()); 
            if (cw <= 0) {
                cw = (formPanel == null? 0: formPanel.getCaptionWidth());
            }  
            return cw; 
        } 
        
        void hideLabel() {
            if (label != null) { 
                label.setBounds(0, 0, 0, 0); 
            } 
        }
        
        void updateLabelStyles() {
            if (label == null || formPanel == null) return; 
            
            if (label instanceof JLabel) {
                JLabel jlabel = (JLabel)label;
                //vertical alignment
                String valign = formPanel.getCaptionVAlignment();
                if ( UIConstants.CENTER.equals(valign) ) { 
                    jlabel.setVerticalAlignment(SwingConstants.CENTER); 
                } else if ( UIConstants.BOTTOM.equals(valign) ) { 
                    jlabel.setVerticalAlignment(SwingConstants.BOTTOM); 
                } else { 
                    jlabel.setVerticalAlignment(SwingConstants.TOP); 
                } 
                //horizontal alignment
                String halign = formPanel.getCaptionHAlignment();
                if ( UIConstants.CENTER.equals(halign) ) { 
                    jlabel.setHorizontalAlignment(SwingConstants.CENTER);
                } else if ( UIConstants.RIGHT.equals(halign) ) { 
                    jlabel.setHorizontalAlignment(SwingConstants.RIGHT);
                } else { 
                    jlabel.setHorizontalAlignment(SwingConstants.LEFT);
                } 
            }
            if ( label instanceof XLabel ) {
                XLabel xlabel = (XLabel)label;
                Insets captionPadding = formPanel.getCaptionPadding(); 
                if ( captionPadding != null ) { 
                    xlabel.setPadding(captionPadding); 
                } 
            } 
        } 
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ItemPanelLayout ">
    
    private class ItemPanelLayout implements LayoutManager {
        
        Component label;
        Component editor;
        ControlProperty property;
        
        ItemPanelLayout(ControlProperty property) {
            this.property = property;
        }
        
        public void addLayoutComponent(String name, Component comp) {
            if ("label".equals(name)) {
                label = comp;
                uimodel.setLabel(label);
            } else if ("editor".equals(name)) {
                editor = comp;
                uimodel.setEditor(editor);
            }
        }
        
        public void removeLayoutComponent(Component comp) {
            if (comp == null); 
            else if (label == comp) { 
                label = null; 
                uimodel.setLabel(null);
            } else if (editor == comp) {
                editor = null;
                uimodel.setEditor(null); 
            }
        }
        
        public Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Dimension dim = new Dimension(0,0);
                String orient = formPanel.getCaptionOrientation();
                if ( UIConstants.LEFT.equals(orient) ) {
                    dim = getLayoutSizeHorizontal(parent); 
                } else if ( UIConstants.TOP.equals(orient) ) {
                    dim = getLayoutSizeVertical(parent); 
                } else if ( UIConstants.BOTTOM.equals(orient) ) {
                    dim = getLayoutSizeVertical(parent); 
                } else {
                    dim = getLayoutSizeHorizontal(parent); 
                }
                
                Insets margin = parent.getInsets();
                int w = (dim.width + margin.left + margin.right);
                int h = (dim.height + margin.top + margin.bottom);
                return new Dimension(w, h);
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent);
        }
        
        public Dimension minimumLayoutSize(Container parent) {
            Dimension dim = getLayoutSize(parent);
            return new Dimension(100, dim.height);
        }
        
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                String orient = formPanel.getCaptionOrientation();
                if ( UIConstants.LEFT.equals(orient) ) {
                    layoutContainerHorizontal(parent); 
                } else if ( UIConstants.TOP.equals(orient) ) {
                    layoutContainerVertical(parent, false); 
                } else if ( UIConstants.BOTTOM.equals(orient) ) {
                    layoutContainerVertical(parent, true); 
                } else {
                    layoutContainerHorizontal(parent); 
                }
            }
        }
                
        private Dimension getLayoutSizeHorizontal(Container parent) {
            int w=0, h=0;
            if ( uimodel.isEditorVisible() ) {
                if ( uimodel.isLabelVisible() ) {
                    uimodel.updateLabelStyles(); 
                    Dimension dim = label.getPreferredSize(); 
                    w = uimodel.getPreferredCaptionWidth(); 
                    h = dim.height; 
                } 
                
                Dimension dim = editor.getPreferredSize(); 
//                System.out.println("editor dim -> "+ dim);
//                System.out.println("  stretch width -> "+ uimodel.getStretchWidth() );
                
                if ( editor instanceof JPanel ) { 
                    JPanel panel = (JPanel)editor; 
                    Dimension ldim = panel.getLayout().preferredLayoutSize( panel ); 
//                    System.out.println("  layout dim -> "+ ldim ); 
                }
                h = Math.max(h, dim.height); 
                w += dim.width;                 
            } 
            return new Dimension(w, h);
        }
        private Dimension getLayoutSizeVertical(Container parent) {
            int w=0, h=0;
            if ( uimodel.isEditorVisible() ) {
                if ( uimodel.isLabelVisible() ) {
                    uimodel.updateLabelStyles(); 
                    Dimension dim = label.getPreferredSize(); 
                    w = Math.max(uimodel.getPreferredCaptionWidth(), 0); 
                    h = dim.height; 
                } 
                
                Dimension dim = editor.getPreferredSize(); 
                w = Math.max(w, dim.width); 
                h += dim.height; 
            } 
            return new Dimension(w, h);
        }
        private void layoutContainerHorizontal(Container parent) {
            Insets margin = parent.getInsets();
            int x = margin.left;
            int y = margin.top;
            int h = parent.getHeight() - (margin.top + margin.bottom);
            
            if ( uimodel.isEditorVisible() ) {
                int cw = uimodel.getPreferredCaptionWidth();
                if ( uimodel.isLabelVisible() ) {
                    if (cw <= 0);
                    
                    label.setBounds(x, y, cw, h); 
                    x += cw;
                } else if ( uimodel.label != null ) {
                    label.setBounds(-1, -1, 0, 0); 
                }

                cw = Math.max(parent.getWidth()-x-margin.right, 0); 
                
                Object userObject = null; 
                if ( editor instanceof JComponent ) {
                    userObject = ((JComponent) editor).getClientProperty("UIControl.userObject"); 
                }
                
                Dimension dim = editor.getPreferredSize(); 
                int sw = uimodel.getStretchWidth(); 
                int pw = dim.width; 
                if ( sw <= 0 && pw==0 ) { 
                    //do nothing 
                } else if ( sw > 0 && userObject == null ) {
                    //do nothing 
                } else if ( sw > 0 ) {
                    double d0 = (double) cw; 
                    if ( pw == 0 ) { pw = 1; } 
                    if ( cw < pw ) { d0 = (double)pw; }
                    
                    double d1 = sw / 100.0;
                    double d2 = d0 * d1; 
                    int dw = new BigDecimal(d2).setScale(0, RoundingMode.HALF_UP).intValue(); 
                    if (dw < pw) { dw = pw; }
                    
                    cw = dw; 
                } else if ( pw > 0 ) {
                    cw = pw; 
                }
                editor.setBounds(x, y, cw, h); 
            } 
        }
        private void layoutContainerVertical(Container parent, boolean reverse) {
            Insets margin = parent.getInsets();
            int x = margin.left;
            int y = margin.top;
            int w = parent.getWidth() - (margin.left + margin.right);
            
            if ( uimodel.isEditorVisible() ) {
                Dimension labeldim = new Dimension(0, 0);
                if ( uimodel.isLabelVisible() ) {
                    int cw = uimodel.getPreferredCaptionWidth();
                    if (cw <= 0) cw = w; 

                    Dimension dim = label.getPreferredSize();
                    labeldim = new Dimension(cw, dim.height); 
                }
                
                if ( reverse ) {
                    Dimension dim = editor.getPreferredSize(); 
                    int sw = uimodel.getStretchWidth(); 
                    int dw = dim.width; 
                    if (sw > 0) { dw = w; } 
                    if (dw > 0 && dw < w) { w = dw; } 
                    
                    editor.setBounds(x, y, w, dim.height); 
                    y += dim.height;
                    
                    if ( uimodel.isLabelVisible() ) {
                        label.setBounds(x, y, labeldim.width, labeldim.height); 
                    }
                    
                } else { 
                    if ( uimodel.isLabelVisible() ) { 
                        label.setBounds(x, y, labeldim.width, labeldim.height); 
                        y += labeldim.height; 
                    } 
                    
                    Dimension dim = editor.getPreferredSize(); 
                    int sw = uimodel.getStretchWidth(); 
                    int dw = dim.width; 
                    if (sw > 0) { dw = w; } 
                    if (dw > 0 && dw < w) { w = dw; } 
                    
                    int ch = Math.max(parent.getHeight()-y-margin.bottom, 0);                     
                    editor.setBounds(x, y, w, ch); 
                } 
            }
        }      
    }
    
    // </editor-fold>
}