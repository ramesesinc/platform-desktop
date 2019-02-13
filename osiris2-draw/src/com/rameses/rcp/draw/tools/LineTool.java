package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.LineFigure;
import com.rameses.rcp.draw.interfaces.Editor;
import java.awt.event.MouseEvent;

public class LineTool extends AbstractTool {
    private LineFigure createdFigure;
    
    public LineTool(){
    }
    
    public LineTool(Editor editor){
        this(editor, null);
    }
    
    public LineTool(Editor editor, Class<LineFigure> prototype){
        super(editor);
        setPrototype(prototype);
        
    }
    
    
    @Override
    public void mousePressed(int x, int y, MouseEvent e){
        super.mousePressed(x, y, e);
        createdFigure = createFigure(x, y);
        getEditor().addToDrawing(createdFigure);
    }   

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        createdFigure.updateEndPoint(x, y);
    }
        
    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        if (createdFigure.isEmpty()){
            getDrawing().removeFigure(createdFigure);
        }else {
            getEditor().figureAdded(createdFigure);
            getCanvas().revalidateRect(createdFigure.getDisplayBox());
        }
    }
    

    
    private LineFigure createFigure(int x, int y){
        try{
            LineFigure f = new LineFigure();
            f.addPoint(x, y);
            f.addPoint(x, y);
            return f;
        }
        catch(Throwable e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
}
