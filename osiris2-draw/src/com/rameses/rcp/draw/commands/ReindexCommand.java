/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.figures.WorkflowEndNode;
import com.rameses.rcp.draw.figures.WorkflowStartNode;
import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;


public class ReindexCommand extends Command{
    
    public ReindexCommand(Canvas canvas){
        super("draw_reindex", canvas);
    }
    
    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, true);
    }    

    @Override
    public void actionPerformed(ActionEvent e) {
        clearIndexes();
        
        int idx = 0;
        Figure f = findStartFigure();
        if (f == null){
            return;
        }
        reindex(f, idx);
        getCanvas().refresh();
    }
    
    private void reindex(Figure f, int idx){
        if (f.getIndex() != 0 && f.getIndex() < idx){
            return ;
        }
        
        idx += 1;
        f.setIndex(idx);
        for (Connector c : f.getConnectors()){
            if (c.getStartFigure() == f){
                reindex(c.getEndFigure(), idx);
            }
        }
    }
    
    private Figure findStartFigure(){
        for (Figure f : getDrawing().getFigures()){
            if (f instanceof WorkflowStartNode){
                return f;
            }
        }
        return null;
    }
    
    private void clearIndexes() {
        for (Figure f : getDrawing().getFigures()){
            if (!(f instanceof Connector)){
                f.setIndex(0);
            }
        }
    }
    
}