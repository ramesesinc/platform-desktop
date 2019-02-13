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

public class ArrangeToFrontCommand extends Command {

    public ArrangeToFrontCommand(Canvas canvas) {
        super("draw_arrange_tofront", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.CTRL_DOWN_MASK, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getDrawing().getSelections().isEmpty()) {
            return;
        }
        for (int i = 0; i < getDrawing().getSelections().size(); i++) {
            Figure f = getDrawing().getSelections().get(i);

            //get index from figures and remove 
            int idx = getDrawing().getFigures().indexOf(f);
            Figure tmpf = getDrawing().getFigures().remove(idx);

            //insert in current i location
            getDrawing().getFigures().add(tmpf);
            getCanvas().refresh();
        }

    }
}
