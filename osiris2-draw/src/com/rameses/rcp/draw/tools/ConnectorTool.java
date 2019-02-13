package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class ConnectorTool extends AbstractTool {
    private Figure selectedFigure;
    private LineConnector connector;
    private LineConnector selectedConnector;
    private boolean connecting;
    private boolean allowChop;
    private boolean dragging = false;
    private Point chopPoint; 
    
    private static Cursor splitCursor;
    private static Cursor combineCursor;
    
    private Tool toolDelegate;
    
    public ConnectorTool(){
        connecting = false;
        allowChop = false;
        dragging = false;
    }
    
    public ConnectorTool(Editor editor){
        super(editor);
        connecting = false;
        allowChop = false;
        dragging = false;
    }

    @Override
    public void mousePressed(int x, int y, MouseEvent e) {
        super.mousePressed(x, y, e);
        
        Handle handle = getDrawing().handleAt(x, y);
        if (!isCtrlPressed(e) && handle != null){
            toolDelegate = new ConnectorHandleTool(getEditor(), handle);
        }else if (!connecting){
            selectedFigure = getDrawing().figureAt(x, y);
            if (selectedFigure != null && isConnector(selectedFigure)){
                selectedConnector = (LineConnector)selectedFigure;
                getEditor().addToSelections(selectedConnector);
            }
        }
    }
    
    @Override
    public void mouseMoved(int x, int y, MouseEvent e) {
        if (toolDelegate != null){
            return;
        }
        
        if (connecting){
            handleConnection(x, y, e);
        }
        else{
            selectedConnector = (LineConnector)getDrawing().connectorAt(x, y);
            handleConnector(x, y, e);
        }
    }

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        if (toolDelegate != null){
            toolDelegate.mouseDrag(x, y, e);
        }
        else if (isConnector(selectedFigure)){
            if (!dragging){
                selectedConnector = (LineConnector)selectedFigure;
                getDrawing().addSelection(selectedFigure);
                chopConnector(x, y, e);
                dragging = true;
            }
            else if(chopPoint != null){
                chopPoint.x = x;
                chopPoint.y = y;
            }
        }
    }
    
    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        if (toolDelegate != null){
            toolDelegate.mouseReleased(x, y, e);
            toolDelegate = null;
            return;
        }
        if (selectedConnector != null){
            Handle handle = getDrawing().handleAt(x, y);
            if (handle != null && isCtrlPressed(e)){
                selectedConnector.chop(x, y, false);
                getCanvas().revalidateRect(selectedConnector.getDisplayBox());
            }
        }
        
        selectedFigure = getDrawing().figureAt(x, y, connector);
        if (isConnectionAllowed(selectedFigure)){
            if (!connecting){
                connectStartFigure(x, y, e);
            }
            else{
                connectEndFigure(x, y, e);
                if (selectedConnector != null){
                    getEditor().figureAdded(selectedConnector);
                    getCanvas().revalidateRect(selectedConnector.getDisplayBox());
                }
            }
        } else if( connecting == true && connector != null) {
            connector.addPoint(x, y);
        }
        dragging = false;
    }
    
    
    
    
    
    private void connectStartFigure(int x, int y, MouseEvent e){
        connector = new LineConnector();
        connector.setStartFigure(selectedFigure);
        connector.updateStartPoint(x, y);
        connector.updateEndPoint(x, y);
        getEditor().addToConnector(connector);
        selectedFigure = null;
        connecting = true;
    }
    
    private void connectEndFigure(int x, int y, MouseEvent e){
        connector.setEndFigure(selectedFigure);
        if (checkDuplicateConnector()){
            getEditor().getDrawing().removeConnector(connector);
            connector = null;
        }
        
        setDefaultCursor();
        connecting = false;
        selectedFigure = null;
    }

    
    private boolean isConnectionAllowed(Figure figure){
        if (figure == null || isConnector(figure)){
            return false;
        }
        if (figure.isConnectionAllowed() == false ){
            return false;
        }
        else if (!connecting && figure.isStartConnectionAllowed() == false){
            return false;
        }
        else if (connecting && figure.isEndConnectionAllowed() == false){
            return false;
        }
        else if (connecting && connector.getStartFigure() == figure){
            return false;
        }
        else if (connector != null && connector.getEndFigure() != null && connector.getEndFigure() == figure){
            return false;
        }
        return true;
    }
    
    private boolean isConnector(Figure figure){
        if (figure == null){
            return false;
        }
        return (figure instanceof Connector);
    }
    
    
    private void handleConnection(int x, int y, MouseEvent e){
        selectedFigure = getDrawing().figureAt(x, y);
        if (isConnectionAllowed(selectedFigure)) {
            setTargetCursor();
        }
        else {
            selectedFigure = null;
            setDefaultCursor();
        }
        if (connecting){
            connector.updateEndPoint(x, y);
        }    
    }
    
    /**
     * Support for adding points to the connector.
     *   = adding new point, press ctrl and drag  
     *   = remove point, press ctrl+shift and click
     */
    private void handleConnector(int x, int y, MouseEvent e){
        if (selectedConnector != null){
            Handle handle = getDrawing().handleAt(x, y);
            if (isCtrlPressed(e) && handle != null){
                getCanvas().setCursor(getCombineCusor());
            }
            else if (selectedConnector.allowChop(x,y)){
                getCanvas().setCursor(getSplitCusor());
            }
        }
        else {
            setToolCursor();
        }
    }
    
    private void chopConnector(int x, int y, MouseEvent e){
        if (isCtrlPressed(e)){
            
        }
        else {
            allowChop = selectedConnector.allowChop(x,y);
            if(allowChop){
                chopPoint = selectedConnector.chop(x, y, true);
                getCanvas().refresh();
            }
        }
    }
    
    @Override
    public Cursor getToolCursor(){
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }
    
    private Cursor getCombineCusor(){
        if (combineCursor == null){
            combineCursor = DrawUtil.createCustomCursor("delete-cursor16.png");
        }
        return combineCursor;
    }
    
    private Cursor getSplitCusor() {
        if (splitCursor == null){
            splitCursor = DrawUtil.createCustomCursor("add-cursor16.png");
        }
        return splitCursor;
    }
    
    private void setTargetCursor(){
        getCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void cancel() {
        if (connector.getStartFigure() != null){
            connector.getStartFigure().removeConnector(connector);
        }
        if (connector.getEndFigure() != null){
            connector.getEndFigure().removeConnector(connector);
        }
        
        getDrawing().removeConnector(connector);
        selectedFigure = null;
        selectedConnector = null;
        connector = null;
        connecting = false;
        dragging = false;
        getEditor().setCurrentTool(getEditor().getDefaultTool());
        getCanvas().refresh();
    }

    /* Duplicate connector has the same start and end figuure */
    private boolean checkDuplicateConnector() {
        for (Connector c : getDrawing().getConnectors()){
            if (c.getStartFigure() == connector.getStartFigure() &&
                c.getEndFigure() == connector.getEndFigure() &&
                 c != connector){
                return true;
            }
        }
        return false;
    }

}
