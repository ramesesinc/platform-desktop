package com.rameses.rcp.draw.interfaces;

import com.rameses.rcp.draw.figures.LineConnector;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;


public interface Drawing {
    public void draw(Graphics2D g);
    public List<Figure> getFigures();
    public Figure addFigure(Figure figure);
    public void removeFigure(Figure figure);    
    
    public List<Figure> getSelections();
    public boolean hasMultipleSelections();
    public void clearSelections();
    public void addSelection(Figure figure);
    public void toggleSelection(Figure figure);
    public void removeSelection(Figure figure);
    public Figure figureAt(int x, int y);
    public Figure figureAt(int x, int y, Figure exclude);
    public Figure innerFigureAt(int x, int y);
    public Figure figureById(String id);
    public Figure figureByName(String targetName);
    public Handle handleAt(int x, int y);
    public boolean isFigureSelected(Figure figure);
    
    public List<Connector> getConnectors();
    public void addConnector(Connector connector);
    public void removeConnector(Connector connector);
    public Connector connectorAt(int x, int y);
    
    public String getXml();
    public Map getData();
    
    public List<Figure> deleteSelections();
    public void clearFigures();
    public void setShowHandles(boolean showHandles);
    public Rectangle getBounds();
    
}
