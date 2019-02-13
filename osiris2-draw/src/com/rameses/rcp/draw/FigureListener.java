package com.rameses.rcp.draw;

import java.util.EventListener;

public interface FigureListener extends EventListener{
    public void propertyChanged(FigureEvent e);
    public void attributeChanged(FigureEvent e);
    public void figureChanged(FigureEvent e);
}
