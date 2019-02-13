package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Editor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public abstract class Command extends AbstractAction {
    private String name;
    private Canvas canvas;

    public Command(String name, Canvas canvas) {
        this.name = name;
        this.canvas = canvas;
    }
    
    
    @Override
    public abstract void actionPerformed(ActionEvent e);
    
    public abstract KeyStroke getKeyStroke();

    
    public String getName(){
        return name;
    }
    
    public Canvas getCanvas(){
        return canvas;
    }
    
    public Drawing getDrawing(){
        return canvas.getDrawing();
    }
    
    public Editor getEditor(){
        return canvas.getEditor();
    }
}
