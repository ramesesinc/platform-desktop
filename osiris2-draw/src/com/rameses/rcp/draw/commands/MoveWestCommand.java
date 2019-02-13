/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;

public class MoveWestCommand extends Command{
    
    public MoveWestCommand(Canvas canvas){
        super("draw_move_west", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getEditor().moveSelections(MoveDirection.WEST);
    }

}


