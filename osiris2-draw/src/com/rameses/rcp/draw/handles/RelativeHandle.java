package com.rameses.rcp.draw.handles;

import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import com.rameses.rcp.draw.undo.UndoableResize;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;






public class RelativeHandle extends AbstractHandle{
    private Rectangle prevBounds;
    
    public RelativeHandle(double offsetX, double offsetY){
        super(offsetX, offsetY);
    }
    
    public RelativeHandle(Figure owner, double offsetX, double offsetY){
        super(owner, offsetX, offsetY);
    }

    @Override
    public void doStart(int dx, int dy, MouseEvent e) {
        super.doStart(dx, dy, e);
        this.prevBounds = getOwner().getBounds();
    }
    
    @Override
    public void doStep(int dx, int dy, MouseEvent e){
    }

    @Override
    public void doEnd(int dx, int dy, MouseEvent e) {
        super.doEnd(dx, dy, e);
        if (getEditor() != null){
            logResizeUndoEdit();
        }
    }
    
    
    
    public static Handle north(Figure owner){
        return new RelativeNorthHandle(owner);
    }
    
    public static Handle northEast(Figure owner){
        return new RelativeNorthEastHandle(owner);
    }
    
    public static Handle east(Figure owner){
        return new RelativeEastHandle(owner);
    }
    
    public static Handle southEast(Figure owner){
        return new RelativeSouthEastHandle(owner);
    }
    
    public static Handle south(Figure owner){
        return new RelativeSouthHandle(owner);
    }
    
    public static Handle southWest(Figure owner){
        return new RelativeSouthWestHandle(owner);
    }
    
    public static Handle west(Figure owner){
        return new RelativeWestHandle(owner);
    }
    
    public static Handle northWest(Figure owner){
        return new RelativeNorthWestHandle(owner);
    }

    private void logResizeUndoEdit() {
        UndoableResize edit = new UndoableResize(getOwner(), prevBounds, getOwner().getBounds());
        getEditor().getUndoRedoManager().addEdit(edit);
    }
}


class RelativeNorthHandle extends RelativeHandle{

    public RelativeNorthHandle(Figure owner) {
        super(owner, 0.5, 0.0);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = r.x;
        int y1 = y;
        int x2 = r.x + r.width;
        int y2 = r.y + r.height;
        if (y < y2){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }

    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.N_RESIZE_CURSOR);
    }
}

class RelativeNorthEastHandle extends RelativeHandle{

    public RelativeNorthEastHandle(Figure owner) {
        super(owner, 1.0, 0.0);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = r.x;
        int y1 = y;
        int x2 = x;
        int y2 = r.y + r.height;
        if (x > x1 && y < y2){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.NE_RESIZE_CURSOR);
    }
}

class RelativeEastHandle extends RelativeHandle{

    public RelativeEastHandle(Figure owner) {
        super(owner, 1.0, 0.5);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = r.x;
        int y1 = r.y;
        int x2 = x;
        int y2 = r.y + r.height;
        if (x > r.x){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.E_RESIZE_CURSOR);
    }
}

class RelativeSouthEastHandle extends RelativeHandle{

    public RelativeSouthEastHandle(Figure owner) {
        super(owner, 1.0, 1.0);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = r.x;
        int y1 = r.y;
        int x2 = x;
        int y2 = y;
        if (x > r.x && y > r.y){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.SE_RESIZE_CURSOR);
    }
}

class RelativeSouthHandle extends RelativeHandle{

    public RelativeSouthHandle(Figure owner) {
        super(owner, 0.5, 1.0);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = r.x;
        int y1 = r.y;
        int x2 = r.x + r.width;
        int y2 = y;
        if (y > r.y){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.S_RESIZE_CURSOR);
    }
}

class RelativeSouthWestHandle extends RelativeHandle{

    public RelativeSouthWestHandle(Figure owner) {
        super(owner, 0.0, 1.0);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = x;
        int y1 = r.y;
        int x2 = r.x + r.width;
        int y2 = y;
        if (x < x2 && y > y1){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
        
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.SW_RESIZE_CURSOR);
    }
}

class RelativeWestHandle extends RelativeHandle{

    public RelativeWestHandle(Figure owner) {
        super(owner, 0.0, 0.5);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = x;
        int y1 = r.y;
        int x2 = r.x + r.width;
        int y2 = r.y + r.height;
        if (x < x2){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.W_RESIZE_CURSOR);
    }
}

class RelativeNorthWestHandle extends RelativeHandle{

    public RelativeNorthWestHandle(Figure owner) {
        super(owner, 0.0, 0.0);
    }
    
    @Override
    public void doStep(int x, int y, MouseEvent e){
        Rectangle r = getOwner().getDisplayBox();
        int x1 = x;
        int y1 = y;
        int x2 = r.x + r.width;
        int y2 = r.y + r.height;
        if (x < x2 && y < y2){
            getOwner().setDisplayBox(x1, y1, x2, y2);
        }
    }
    
    @Override
    public Cursor getCursor() {
        return new Cursor(Cursor.NW_RESIZE_CURSOR);
    }
}



