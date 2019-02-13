/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

public class CancelAddFigureCommand extends Command{
    
    public CancelAddFigureCommand(Canvas canvas){
        super("draw_camceladd", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getEditor().getCurrentTool().isActive()){
            getEditor().getCurrentTool().cancel();
        }
    }
}