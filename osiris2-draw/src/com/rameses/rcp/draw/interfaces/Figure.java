package com.rameses.rcp.draw.interfaces;

import com.rameses.rcp.draw.FigureListener;
import com.rameses.rcp.draw.support.AttributeKey;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public interface Figure {
    public String getId();
    public void setId(String id);
    public String getCategory();
    public String getType();
    public void setType(String type);
    public String getName();
    public void setName(String name);
    public String getCaption();
    public void setCaption(String caption);
    public int getIndex();
    public void setIndex(int index);
    public boolean isSystem();
    public void setSystem(boolean system);
    public String getTooltip();
    public void setTooltip(String tooltip);
    public Rectangle getDisplayBox();
    public void setDisplayBox(Rectangle displayBox);
    public void setDisplayBox(int x1, int y1, int x2, int y2);
    public Rectangle getBounds();
    public Point getLocation();
    public void setLocation(Point pt);
    public void center(Point pt);
    public int getX();
    public int getY();
    public boolean getSelected();
    public void setSelected(boolean selected);
    public void toggleShowIndex();
    public Map getInfo();
    
    public void moveBy(int dx, int dy, MouseEvent e);
    public void draw(Graphics2D g);
    public void drawHandles(Graphics2D g);
    public void drawConnectors(Graphics2D g);
    public void clearHandles();
    
    public List<Handle> getHandles();
    public void addHandle(Handle handle);
    public Point getCenter();
    public boolean hitTest(int x, int y);
    public boolean isEmpty();
    public boolean isAllowResize();
    public boolean isShowIndex();
    public void setAllowResize(boolean allowResize);
    
    //connector support 
    public boolean isConnectionAllowed();
    public void setConnectionAllowed(boolean allowed);
    public boolean isStartConnectionAllowed();
    public void setStartConnectionAllowed(boolean allowed);
    public boolean isEndConnectionAllowed();
    public void setEndConnectionAllowed(boolean allowed);
    public void addConnector(Connector connector);
    public void removeConnector(Connector connector);
    public List<Connector> getConnectors();
    
    public Point intersect(Point p);
    
    
    public Map<AttributeKey, Object> getAttributes();
    public void setAttributes(Map<AttributeKey, Object> map);
    public void readAttributes(Map map);
    public <T> void set(AttributeKey<T> key, T value);
    public <T> T get(AttributeKey<T> key);

    public Tool getTool();
    public String getIcon();
    public int getToolIndex();
    public String getToolCaption();

    public void showHandles(boolean showHandle);

    public Map toMap();
    
    public void setRotation(int degree);
    public Figure getInnerText();
    
    public void addFigureListener(FigureListener listener);
    public void removeFigureListener(FigureListener listener);
    
}
