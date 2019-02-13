package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.PolyLineFigure;
import com.rameses.rcp.draw.interfaces.Editor;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class PolyLineTool extends AbstractTool {
    private PolyLineFigure createdFigure;
    private boolean processing;
    
    public PolyLineTool(){
        processing = false;
    }
    
    public PolyLineTool(Editor editor){
        this(editor, null);
    }
    
    public PolyLineTool(Editor editor, Class<PolyLineFigure> prototype){
        super(editor);
        setPrototype(prototype);
        processing = false;
    }
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        if (processing == false){
            super.mousePressed(x, y, e);
            createdFigure = createFigure();
            createdFigure.addPoint(x, y);
            createdFigure.addPoint(x, y);
            getEditor().addToDrawing(createdFigure);
            processing = true;
        }
    }   

    @Override
    public void mouseMoved(int x, int y, MouseEvent e) {
        if (processing){
            createdFigure.updateEndPoint(x,y);
        }
    }
    

    @Override
    public void mouseClicked(int x, int y, MouseEvent e) {
        createdFigure.addPoint(x, y);
        createdFigure.updateEndPoint(x, y);
        
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1){
            if (createdFigure.isEmpty()){
                getDrawing().removeFigure(createdFigure);
            }
            else{
                createdFigure.smoothenPoints();
                getEditor().figureAdded(createdFigure);
                getCanvas().revalidateRect(createdFigure.getDisplayBox());
            }
            processing = false;
        }
    }
    
    @Override
    public void cancel() {
        getDrawing().removeFigure(createdFigure);
        createdFigure = null;
        getEditor().setCurrentTool(getEditor().getDefaultTool());
        getCanvas().refresh();
    }
    
    private PolyLineFigure createFigure(){
        try{
            return (PolyLineFigure)getPrototype().newInstance();
        }
        catch(Throwable e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
}
