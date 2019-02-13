package com.rameses.rcp.draw.interfaces;

import com.rameses.rcp.draw.FigureListener;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

public interface Connector {
    public Figure getStartFigure();
    public void setStartFigure(Figure figure);
    public void setStartFigure(Figure figure, boolean updateConnectorPoint);
    public Figure getEndFigure();
    public void setEndFigure(Figure figure);
    public void setEndFigure(Figure figure, boolean updateConnectorPoint);
    
    public LineDecoration getStartDecoration();
    public void setStartDecoration(LineDecoration decoration);
    public LineDecoration getEndDecoration();
    public void setEndDecoration(LineDecoration decoration);
    
    public void draw(Graphics2D g);
    public void moveBy(int dx, int dy, MouseEvent e, Figure sourceFigure);
    public boolean allowChop(int x, int y);
    public Point chop(int x, int y, boolean split);
    public void addFigureListener(FigureListener listener);
    
    public List<Point> getPoints();
    
}
