package com.rameses.rcp.draw.components;

import com.rameses.rcp.draw.figures.RectangleFigure;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.support.AttributeKeys;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class OutlineDashMenuItemRenderer extends MenuItemRenderer{

    public OutlineDashMenuItemRenderer(AttributeMenuItem menuItem) {
        super(menuItem);
    }

    @Override
    public void render(Graphics g) {
        Rectangle r = menuItem.getBounds();
        Graphics2D g2 = (Graphics2D)g.create();
        
        Figure f = new RectangleFigure();
        double[] val = (double[])menuItem.getValue();
        f.set(STROKE_DASHES, val);
        f.set(STROKE_WIDTH, 1.5d);
        
        g2.setStroke(AttributeKeys.getStroke(f));
        g2.drawLine(0, r.height / 2, r.width, r.height / 2);        
    }
}
