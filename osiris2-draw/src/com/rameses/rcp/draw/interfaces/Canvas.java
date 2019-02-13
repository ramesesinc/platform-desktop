package com.rameses.rcp.draw.interfaces;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;

public interface Canvas {
    public Editor getEditor();
    public void setEditor(Editor editor);
    
    public boolean isReadonly();
    public void setReadonly(boolean readonly);
    
    public void draw(Graphics2D g);
    public void refresh();
    
    public Cursor getCursor();
    public void setCursor(Cursor cursor);

    public Container getContainer();

    public void showMenu(int sx, int sy, List menus);

    public Rectangle getBounds();
    
    public void revalidateRect(Rectangle rect);

    public void requestFocus();
    
    public Drawing getDrawing();

    public void hideMenu();

    public Graphics getGraphics();

    public Color getBackground();

    public void setSelectionArea(Rectangle selectedArea);

}
