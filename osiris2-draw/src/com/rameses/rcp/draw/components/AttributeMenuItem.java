package com.rameses.rcp.draw.components;

import com.rameses.rcp.draw.support.AttributeKey;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

public class AttributeMenuItem extends JMenuItem implements ActionListener {
    private AttributePickerModel model;
    private AttributeKey key;
    private Object value;
    private MenuItemRenderer renderer;
    
    public AttributeMenuItem(String text, AttributePickerModel model, AttributeKey key, Object value){
        setToolTipText(text);
        this.model = model;
        this.key = key;
        this.value = value;
        setHorizontalTextPosition(SwingConstants.CENTER);
        addActionListener(this);
        setPreferredSize(new Dimension(120, 20));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        model.getEditor().attributeChanged(key, value);
    }

    public Object getValue() {
        return value;
    }

    public MenuItemRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(MenuItemRenderer renderer) {
        this.renderer = renderer;
    }
   
    

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (renderer != null){
            renderer.render(g);
        }
    }
    
    
    
    
    
}