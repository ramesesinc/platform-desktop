/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;


public class UndoCommand extends Command{
    
    public UndoCommand(Canvas canvas){
        super("draw_undo", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getCanvas().getEditor().getUndoAction().actionPerformed(e);
    }
    
}