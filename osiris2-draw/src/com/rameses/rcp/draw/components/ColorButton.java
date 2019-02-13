package com.rameses.rcp.draw.components;

import com.rameses.rcp.draw.support.AttributeKey;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;


public class ColorButton extends JButton implements ActionListener{
        private Color color;
        private AttributePickerModel model;
        private AttributeKey key;

        public ColorButton(AttributePickerModel model, Color color){
            this(model, null, color);
        }
        
        public ColorButton(AttributePickerModel model, AttributeKey key, Color color){
            this.model = model;
            this.color = color;
            this.key = key;
            setPreferredSize(new Dimension(12, 12));
            addActionListener(this);
        }

        @Override
        public void paint(Graphics g) {
            Color oldColor = g.getColor();
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(oldColor);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (key != null){
                model.getEditor().attributeChanged(key, color);
            }else {
                model.getEditor().attributeChanged(model.getAttributeKey(), color);
            }
        }

    }