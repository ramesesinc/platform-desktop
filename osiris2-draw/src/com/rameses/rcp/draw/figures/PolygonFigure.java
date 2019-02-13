package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.support.AttributeKeys;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.tools.PolyLineTool;
import com.rameses.rcp.draw.utils.DataUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

public class PolygonFigure extends PolyLineFigure {
    
    public PolygonFigure(){
        super();
    }
    
    public PolygonFigure(List<Point> points){
        super(points);
    }
        
    @Override
    public String getToolCaption() {
        return "Polygon";
    }
    
        
    @Override
    public String getType(){
        return "polygon";
    }
    
    @Override
    public Tool getTool() {
        return new PolyLineTool();
    }
    
    @Override
    public String getIcon() {
        return "images/draw/polygon16.png";
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
    
    @Override
    public void drawFigure(Graphics2D g) {
        g.setStroke(AttributeKeys.getStroke(this));
        
        List<Point> points = getPoints();
        
        if (getPoints().size() <= 1){
            return;
        }
        
        int[] xs = new int[points.size()];
        int[] ys = new int[points.size()];
        
        for (int i = 0; i < points.size(); i++){
            Point pt = points.get(i);
            xs[i] = pt.x;
            ys[i] = pt.y;
        }
        
        Color oldColor = g.getColor();
        g.setStroke(AttributeKeys.getStroke(this));
        
        g.setColor(get(FILL_COLOR));
        g.fillPolygon(xs, ys, points.size());
        
        g.setColor(get(STROKE_COLOR));
        g.drawPolygon(xs, ys, points.size());
        
        g.setColor(oldColor);
    }
    

    
    @Override
    public boolean hitTest(int x, int y){
        Rectangle r = null;
        for (Point pt : getPoints()){
            if (r == null){
                r = new Rectangle(pt);
            }else{
                r.add(pt);
            }
        }
        if (r != null && r.contains(x, y)){
            return true;
        }
        return false; 
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
