package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.handles.PolyLineHandle;
import com.rameses.rcp.draw.interfaces.Handle;
import com.rameses.rcp.draw.interfaces.LineDecoration;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.support.AttributeKeys;
import com.rameses.rcp.draw.utils.Geom;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.tools.PolyLineTool;
import com.rameses.rcp.draw.utils.DataUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolyLineFigure extends AbstractAttributedFigure {
    private LineDecoration startDecoration;
    private LineDecoration endDecoration;
    private List<Point> points;
    
    public PolyLineFigure(){
        points = new ArrayList<Point>();
    }
    
    public PolyLineFigure(List<Point> points){
        this.points = points;
    }
    
    
    @Override
    public String getToolCaption() {
        return "Free Form";
    }
    
        
    @Override
    public String getType(){
        return "freeformline";
    }
    
    @Override
    public Tool getTool() {
        return new PolyLineTool();
    }
    
    @Override
    public String getIcon() {
        return "images/draw/poly16.png";
    }

    @Override
    public Rectangle getBounds() {
        Rectangle r = null;
        for (Point pt : getPoints()){
            if (r == null){
                r = new Rectangle(pt);
            }else{
                r.add(pt);
            }
        }
        return r; 
    }
    
    
    public List<Point> getPoints(){
        return points;
    }
    
    
    public void addPoint(int x, int y){
        addPoint(new Point(x,y));
    }
    
    public void addPoint(Point pt){
        points.add(pt);
        updateDisplayBox();
    }
    
    /* Remove points close very close to one another */
    public void smoothenPoints() {
        if (getPoints().size() <= 1) {
            return;
        }
        
        int tolerance = 2;
        boolean pass = false;
        int idxToRemove = 0;
        
        while(!pass){
            pass = true;

            Point p1 = getPoints().get(0);
            for(int i = 1; i < getPoints().size(); i++){
                Point p2 = getPoints().get(i);
                if (Math.abs(p2.x - p1.x) <= tolerance && Math.abs(p2.y - p1.y) <= tolerance){
                    idxToRemove = i;
                    pass = false; 
                }
                p1 = p2;
            }
            if (!pass){
                getPoints().remove(idxToRemove);
            }
        }
    }
    
    
    @Override
    public boolean hitTest(int x, int y){
        if (getPoints().size() <= 1){
            return false;
        }
        
        Point p1 = getPoints().get(0);
        for (int i = 1; i < getPoints().size(); i++){
            Point p2 = getPoints().get(i);
            if (Geom.pointInLine(p1.x, p1.y, p2.x, p2.y, x, y)){
                return true;
            }
            p1 = p2;
        }
        return false;
    }
    
    @Override
    public void move(int dx, int dy, MouseEvent e) {
        for (Point p : getPoints()){
            p.x += dx;
            p.y += dy;
        }
        updateDisplayBox();
    }

    @Override
    public List<Handle> getHandles() {
        PolyLineHandle.addHandles(this);
        return super.getHandles();
    }
    
    @Override
    protected void drawFigure(Graphics2D g) {
        if (points.size() <= 1){
            return;
        }
        
        Color oldColor = g.getColor();
        g.setStroke(AttributeKeys.getStroke(this));
        g.setColor(get(STROKE_COLOR));
        Point p1 = points.get(0);
        
        for (int i = 1; i < points.size(); i++){
            Point p2 = points.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            p1 = p2;
        }
        g.setColor(oldColor);
    }
    
    @Override
    protected void drawIndex(Graphics2D g) {
    }
    
    @Override
    public boolean isConnectionAllowed() {
        return false;
    }
    
    protected void updateDisplayBox(){
        int x1 = Integer.MAX_VALUE;
        int y1 = Integer.MAX_VALUE;
        int x2 = Integer.MIN_VALUE;
        int y2 = Integer.MIN_VALUE;
        for (int i=0; i < points.size(); i++){
            Point p = points.get(i);
            x1 = Math.min(x1, p.x);
            y1 = Math.min(y1, p.y);
            x2 = Math.max(x2, p.x);
            y2 = Math.max(y2, p.y);
        }
        setDisplayBox(x1, y1, x2, y2);
    }
    
    public void updateStartPoint(int x, int y){
        if (!getPoints().isEmpty()){
            Point p = getPoints().get(0);
            p.x = x;
            p.y = y;
            updateDisplayBox();
        }
    }
    
    public void updateEndPoint(int x, int y){
        if (!getPoints().isEmpty()){
            Point p = getPoints().get(getPoints().size()-1);
            p.x = x;
            p.y = y;
            updateDisplayBox();
        }
    }
    
    public LineDecoration getStartDecoration(){
        return startDecoration;
    }
    
    public void setStartDecoration(LineDecoration decoration){
        this.startDecoration = decoration;
    }
    
    public LineDecoration getEndDecoration(){
        return endDecoration;
    }
    
    public void setEndDecoration(LineDecoration decoration){
        this.endDecoration = decoration;
    }
    
    /**
     * format points : "x1,y1|x2,y2|...|xn,yn"
     */
    private StringBuilder pointsToAttribute(){
        StringBuilder sb = new StringBuilder();

        for(Point p : getPoints()){
            sb.append(p.x + "," + p.y + "|");
        }
        
        //remove last |
        int len = sb.length();
        sb.replace(len - 1, len, "");
        
        return sb;
    }
    
    @Override
    public void readAttributes(Map prop){
        super.readAttributes(prop);
        Map ui = (Map) prop.get("ui");
        
        getPoints().clear();
        List<Point> points = DataUtil.decodePoints("points", ui);
        for(Point pt : points){
            addPoint(pt);
        }
    }
        
    @Override
    public Map toMap() {
        Map map = (Map)super.toMap();
        Map ui = (Map)map.get("ui");
        DataUtil.putValue(ui, "points", DataUtil.encodePoints(getPoints()));
        return map;
    }       
}
