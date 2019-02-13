package com.rameses.rcp.draw.components;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class OutlineWeightMenuItemRenderer extends MenuItemRenderer{

    public OutlineWeightMenuItemRenderer(AttributeMenuItem menuItem) {
        super(menuItem);
    }

    @Override
    public void render(Graphics g) {
        Rectangle r = menuItem.getBounds();
        double val = (Double)menuItem.getValue();

        Graphics2D g2 = (Graphics2D)g.create();
        g2.setStroke(new BasicStroke((float)val));
        g2.drawLine(0, r.height / 2, r.width, r.height / 2);
    }
    
}
