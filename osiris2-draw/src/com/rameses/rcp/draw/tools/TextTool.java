package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.FloatingTextField;
import com.rameses.rcp.draw.figures.TextFigure;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.event.MouseEvent;

public class TextTool extends AbstractTool{
    private FloatingTextField floatingText;
    private TextFigure createdFigure;
    private TextFigure selectedFigure;
    private Figure prototype;
    private boolean newlyCreated;
    
    public TextTool(){
    }
    
    public TextTool(Editor editor){
        this(editor, null);
    }
    
    public TextTool(Editor editor, TextFigure prototype){
        super(editor);
        this.prototype = prototype;
    }
    

    @Override
    public void mousePressed(int x, int y, MouseEvent e) {
        Figure figure  = getDrawing().figureAt(x, y);
        if (figure == null && floatingText != null){
            endEdit();
            return;
        }
        
        if (figure instanceof TextFigure){
            selectedFigure = (TextFigure)figure;
        }
        
        if (selectedFigure != null){
            if (floatingText != null){
                endEdit();
            }
            beginEdit(selectedFigure);
        }
        else if (floatingText == null){
            createdFigure = createFigure(x, y);
            beginEdit(createdFigure);
        }
        else if (createdFigure != null || selectedFigure != null ){
            endEdit();
        }
    }
    
    public final void beginEdit(TextFigure f){
        floatingText = new FloatingTextField();
        floatingText.createOverlay(this, getCanvas(), f);
    }
    
    public final void endEdit(){
        floatingText.endOverlay();
        if (createdFigure != null && !createdFigure.isEmpty()){
            getEditor().addToDrawing(createdFigure);
            getEditor().figureAdded(createdFigure);
            getCanvas().revalidateRect(createdFigure.getDisplayBox());
        }
        floatingText = null;
    }
    
    
    
    private boolean isEditableFigure(){
        if (selectedFigure == null || !(selectedFigure instanceof TextFigure)){
            return false;
        }
        return true;
    }
    
    private TextFigure createFigure(int x, int y){
        try{
            return new TextFigure(null, x, y);
        }
        catch(Throwable e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void cancel() {
        getDrawing().removeFigure(createdFigure);
        createdFigure = null;
        if (floatingText != null){
            floatingText.endOverlay();
            floatingText = null;
        }
        getEditor().setCurrentTool(getEditor().getDefaultTool());
        getCanvas().refresh();
    }
}

