/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.figures.TextFigure;
import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.support.AttributeKeys;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.KeyStroke;


public class CenterTextCommand extends Command{
    
    public CenterTextCommand(Canvas canvas){
        super("draw_cente_text", canvas);
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Figure> selections = getEditor().getDrawing().getSelections();
        if (!selections.isEmpty()){
            Figure figure = selections.get(0);
            boolean newValue = !figure.get(CENTER_TEXT);
            getEditor().attributeChanged(AttributeKeys.CENTER_TEXT, newValue);
            
            if (newValue){
                for (Figure f : selections){
                    if (f instanceof TextFigure){
                        Figure parent = ((TextFigure)f).getParentFigure();
                        if (parent != null){
                            parent.setDisplayBox(parent.getDisplayBox());
                        }
                    }
                }
            }
        }
    }
    
}