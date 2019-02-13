/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;


public class ShowIndexCommand extends Command{
    
    public ShowIndexCommand(Canvas canvas){
        super("draw_showindex", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Figure f : getDrawing().getFigures()){
            f.toggleShowIndex();
        }
        getCanvas().refresh();
    }
}