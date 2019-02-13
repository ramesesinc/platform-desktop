package com.rameses.rcp.draw.handles;

import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;


public class RotateHandle extends AbstractHandle{
    public RotateHandle(double offsetX, double offsetY){
        super(offsetX, offsetY);
    }
    
    public RotateHandle(Figure owner, double offsetX, double offsetY){
        super(owner, offsetX, offsetY);
    }
    
    @Override
    public void doStep(int dx, int dy, MouseEvent e){
    }
    
    public static Handle northEast(Figure owner){
        return new RotateNorthEastHandle(owner);
    }
    
    public static Handle southEast(Figure owner){
        return new RotateSouthEastHandle(owner);
    }
    
    public static Handle southWest(Figure owner){
        return new RotateSouthWestHandle(owner);
    }
    
    public static Handle northWest(Figure owner){
        return new RotateNorthWestHandle(owner);
    }
}

class RotateNorthEastHandle extends RotateHandle{

    public RotateNorthEastHandle(Figure owner) {
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


class RotateSouthEastHandle extends RotateHandle{

    public RotateSouthEastHandle(Figure owner) {
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

class RotateSouthWestHandle extends RotateHandle{

    public RotateSouthWestHandle(Figure owner) {
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

class RotateNorthWestHandle extends RotateHandle{

    public RotateNorthWestHandle(Figure owner) {
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



