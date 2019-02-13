package com.rameses.rcp.draw.commands;

import com.rameses.rcp.draw.interfaces.Canvas;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;



public class Commands {
    private List<Command> commands = new ArrayList<Command>();
    private Canvas canvas;
    
    public Commands(Canvas canvas){
        this.canvas = canvas;
    }
    
    public void registerCommands(){
        buildCommands();
        JComponent comp = (JComponent)canvas;
        
        for (Command c : commands){
            comp.getInputMap().put(c.getKeyStroke(), c.getName());
            comp.getActionMap().put(c.getName(), c);
        }
    }
    
    private void buildCommands(){
        commands.add(new DeleteCommand(canvas));
        commands.add(new CancelAddFigureCommand(canvas));
        commands.add(new ArrangeForwardCommand(canvas));
        commands.add(new ArrangeToFrontCommand(canvas));
        commands.add(new ArrangeBackwardCommand(canvas));
        commands.add(new ArrangeToBackCommand(canvas));
        commands.add(new MoveNorthCommand(canvas));
        commands.add(new MoveSouthCommand(canvas));
        commands.add(new MoveEastCommand(canvas));
        commands.add(new MoveWestCommand(canvas));
        commands.add(new ShowIndexCommand(canvas));
        commands.add(new ReindexCommand(canvas));
        commands.add(new UndoCommand(canvas));
        commands.add(new RedoCommand(canvas));
        commands.add(new CenterTextCommand(canvas));
    }
    
}

