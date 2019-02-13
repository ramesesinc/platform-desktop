package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

public class HandleTool extends AbstractTool {
    private Handle handle;
    
    public HandleTool(Editor editor, Handle handle){
        super(editor);
        this.handle = handle;
    }
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        super.mousePressed(x, y, e);
        handle.doStart(x, y, e);
    }   
    
    
    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        super.mouseDrag(x, y, e);
        if (handle.getOwner().isAllowResize()){
            handle.doStep(x, y, e);
        }
    }

    @Override
    public void mouseMoved(int x, int y, MouseEvent e) {
        if (handle.getOwner().isAllowResize()){
            getCanvas().setCursor(handle.getCursor());
        }
        else{
            getCanvas().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        super.mouseReleased(x, y, e);
        handle.doEnd(x, y, e);
    }
    
    protected Handle getHandle(){
        return handle;
    }
    
    protected Figure getOwner(){
        return handle.getOwner();
    }
    
}
