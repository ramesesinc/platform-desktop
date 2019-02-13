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

public class ArrangeBackwardCommand extends Command {

    public ArrangeBackwardCommand(Canvas canvas) {
        super("draw_arrange_backward", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.CTRL_DOWN_MASK, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getDrawing().getSelections().isEmpty()) {
            return;
        }

        for (Figure f : getDrawing().getSelections()) {
            int idx = getDrawing().getFigures().indexOf(f);
            if (idx != 0) {
                Figure tmpf = getDrawing().getFigures().get((idx - 1));
                getDrawing().getFigures().set((idx - 1), f);
                getDrawing().getFigures().set(idx, tmpf);
            }
        }
    }
}