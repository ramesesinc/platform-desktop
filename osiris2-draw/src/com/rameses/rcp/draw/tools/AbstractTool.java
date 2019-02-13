package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Tool;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class AbstractTool implements Tool{
    private Class prototype;
    private Editor editor;
    private Point startPoint;
    private boolean active = true;
    
    public AbstractTool(){
    }
    
    public AbstractTool(Editor editor){
        this.editor = editor;
        setToolCursor();
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
    public Drawing getDrawing(){
        return getEditor().getDrawing();
    }
    
    @Override
    public Canvas getCanvas() {
        return getEditor().getCanvas();
    }

    @Override
    public Class getPrototype() {
        return prototype;
    }

    @Override
    public void setPrototype(Class prototype) {
        this.prototype = prototype;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void cancel() {
    }
    
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        startPoint = new Point(x,y);
    }
    
    @Override
    public void mouseReleased(int x, int y, MouseEvent e){
        
    }
    
    @Override
    public void mouseClicked(int x, int y, MouseEvent e){
        
    }

    @Override
    public void openFigure(int x, int y, MouseEvent e) {
        
    }

    @Override
    public void showMenu(int x, int y, int sx, int sy, MouseEvent e) {
    }
    
    @Override
    public void mouseDrag(int x, int y, MouseEvent e){
        
    }
    
    @Override
    public void mouseMoved(int x, int y, MouseEvent e){
        
    }
    
    @Override
    public Point getStartPoint(){
        return startPoint;
    }
    
    @Override
    public void setStartPoint(Point startPoint){
        this.startPoint = startPoint;
    }
    
    @Override
    public int getStartX(){
        return startPoint.x;
    }
    
    @Override
    public int getStartY(){
        return startPoint.y;
    }
    
    @Override
    public void setToolCursor(){
        if (getCanvas() != null){
            getCanvas().setCursor(getToolCursor());
        }
        
    }
    
    @Override
    public Cursor getToolCursor(){
        return new Cursor(Cursor.CROSSHAIR_CURSOR);
    }
    
    protected void setDefaultCursor(){
        if (getCanvas() != null){
            getCanvas().setCursor(getDefaultCursor());
        }
    }
    
    protected Cursor getDefaultCursor(){
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }
        
    protected boolean isCtrlPressed(MouseEvent e){
        if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0){
            return true;
        }
        return false;
    }
    
    protected boolean isShiftPressed(MouseEvent e){
        if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0){
            return true;
        }
        return false;
    }
    
}
