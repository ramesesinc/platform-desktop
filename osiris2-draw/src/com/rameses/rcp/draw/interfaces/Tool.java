package com.rameses.rcp.draw.interfaces;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;


public interface Tool {
    public Editor getEditor();
    public void setEditor(Editor editor);
    
    public Drawing getDrawing();
    public Canvas getCanvas();
    
    
    public Point getStartPoint();
    public void setStartPoint(Point startPoint);
    public int getStartX();
    public int getStartY();
    
    public void mousePressed(int x, int y, MouseEvent e);
    public void mouseReleased(int x, int y, MouseEvent e);
    public void mouseClicked(int x, int y, MouseEvent e);
    public void mouseDrag(int x, int y, MouseEvent e);
    public void mouseMoved(int x, int y, MouseEvent e);
    public void openFigure(int x, int y, MouseEvent e);
    public void showMenu(int x, int y, int sx, int sy, MouseEvent e);
    
    
    public Class getPrototype();
    public void setPrototype(Class prototype);
    public void setToolCursor();
    public Cursor getToolCursor();

    public boolean isActive();
    public void setActive(boolean active);
    public void cancel();

}

