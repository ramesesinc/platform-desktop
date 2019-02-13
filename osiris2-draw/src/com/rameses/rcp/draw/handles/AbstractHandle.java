package com.rameses.rcp.draw.handles;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class AbstractHandle implements Handle{
    public static int HANDLE_SIZE = 8;
    public static Color HANDLE_BORDER_COLOR = new Color(150,150,150);
    public static Color HANDLE_FILL_COLOR = new Color(202, 234, 237);
    
    private Editor editor;
    private Figure owner;
    private double offsetX;
    private double offsetY;
    protected int startX;
    protected int startY;
    
    public AbstractHandle(){
        
    }
    
    public AbstractHandle(double offsetX, double offsetY){
        this(null, offsetX, offsetY);
    }
    
    public AbstractHandle(Figure owner, double offsetX, double offsetY){
        this.owner = owner;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public Editor getEditor() {
        return editor;
    }

    @Override
    public void setEditor(Editor editor) {
        this.editor = editor;
    }
    
    @Override
    public Figure getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Figure owner) {
        this.owner = owner;
    }
    

    @Override
    public void moveBy(int dx, int dy, MouseEvent e) {
        owner.moveBy(dx, dy, e);
    }
    
    @Override
    public void doStart(int dx, int dy, MouseEvent e){
        startX = dx;
        startY = dy;
    }

    @Override
    public void doStep(int dx, int dy, MouseEvent e) {
    }

    @Override
    public void doEnd(int dx, int dy, MouseEvent e) {
    }
    
    

    @Override
    public Rectangle getDisplayBox() {
        Point center = getCenter();
        int offset = HANDLE_SIZE / 2;
        Point pt = new Point(center.x - offset, center.y - offset);
        return new Rectangle(pt, new Dimension(HANDLE_SIZE, HANDLE_SIZE));
    }
    
    @Override
    public Point getCenter() {
        Rectangle r = owner.getDisplayBox();
        int x = (int)(r.x + r.width * offsetX) ;
        int y = (int)(r.y + r.height * offsetY);
        return new Point(x, y);
    }

    @Override
    public boolean locate(int x, int y) {
        Rectangle r = getDisplayBox();
        return r.contains(x, y);
    }

    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }
    
    
    

    @Override
    public void draw(Graphics g) {
        Color oldColor = g.getColor();
        drawHandle(g);
        drawBorder(g);
        g.setColor(oldColor);
    }
    
    protected void drawHandle(Graphics g){
        g.setColor(HANDLE_FILL_COLOR);
        Rectangle r = getDisplayBox();
        g.fillRect(r.x, r.y, r.width, r.height);
    }
    
    protected void drawBorder(Graphics g){
        g.setColor(HANDLE_BORDER_COLOR);
        Rectangle r = getDisplayBox();
        g.drawRect(r.x, r.y, r.width, r.height);
    }
    
    
}
