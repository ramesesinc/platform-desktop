package com.rameses.rcp.draw.interfaces;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public interface LineDecoration {
    public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2);
}
