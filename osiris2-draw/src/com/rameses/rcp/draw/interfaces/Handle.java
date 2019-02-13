package com.rameses.rcp.draw.interfaces;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public interface Handle {
    public Editor getEditor();
    public void setEditor(Editor editor);
    public Figure getOwner();
    public void setOwner(Figure owner);
    public void moveBy(int x, int y, MouseEvent e);
    public Rectangle getDisplayBox();
    public void draw(Graphics g);
    
    //center point of the handler
    public Point getCenter();

    // returns true if (x,y) is contained within the handles displayBox;
    public boolean locate(int x, int y);
    
    public Cursor getCursor();

    public void doStart(int dx, int dy, MouseEvent e);
    public void doStep(int dx, int dy, MouseEvent e);
    public void doEnd(int dx, int dy, MouseEvent e);
    
}
