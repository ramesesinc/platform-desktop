package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class SelectAreaTool extends AbstractTool {

    private Rectangle selectedArea;
    private Color color;

    public SelectAreaTool() {
    }

    public SelectAreaTool(Editor editor) {
        super(editor);
        color = Color.BLACK;
    }

    @Override
    public void mousePressed(int x, int y, MouseEvent e) {
        super.mousePressed(x, y, e);
        marquee(getStartX(), getStartY(), getStartX(), getStartY());
    }

    @Override
    public void mouseDrag(int x, int y, MouseEvent e) {
        super.mouseDrag(x, y, e);
        clearMarquee();
        marquee(getStartX(), getStartY(), x, y);
    }

    @Override
    public void mouseReleased(int x, int y, MouseEvent e) {
        clearMarquee();
        selectGroup(e.isShiftDown());
        getCanvas().setSelectionArea(null);
        super.mouseReleased(x, y, e);
    }

    @Override
    public Cursor getToolCursor() {
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }

    private void marquee(int x1, int y1, int x2, int y2) {
        selectedArea = new Rectangle(new Point(x1, y1));
        selectedArea.add(new Point(x2, y2));
        getCanvas().setSelectionArea(selectedArea);
    }

    private void clearMarquee() {
        getCanvas().setSelectionArea(selectedArea);
    }

    private void selectGroup(boolean toggle) {
        for (Figure figure : getDrawing().getFigures()) {
            Rectangle displayBox = figure.getDisplayBox();
            if (selectedArea.contains(displayBox)) {
                if (toggle) {
                    getDrawing().toggleSelection(figure);
                } else {
                    getDrawing().addSelection(figure);
                }
            }
        }
    }
}
