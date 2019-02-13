
package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.handles.TextHandles;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.support.AttributeKey;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.support.RotatedRectangle;
import com.rameses.rcp.draw.tools.TextTool;
import com.rameses.rcp.draw.utils.DataUtil;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class TextFigure extends AbstractAttributedFigure{
    private String text;
    private BufferedImage rotatedText; 
    private RotatedRectangle rotatedRect = new RotatedRectangle();
    private Figure parentFigure;
    
    
    public TextFigure(){
    }
    
    public TextFigure(String text, int x, int y){
        this(text, x, y, 100, 20);
    }
    
    public TextFigure(String text, int x, int y, int width, int height){
        setDisplayBox(x, y, x + width, y + height);
        setText(text);
    }
    
    @Override
    public String getToolCaption() {
        return "Text";
    }
    
        
    @Override
    public String getType(){
        return "text";
    }

    @Override
    public Tool getTool() {
        return new TextTool();
    }
    
    @Override
    public String getIcon() {
        return "images/draw/text16.png";
    }

    @Override
    public List<Handle> getHandles() {
        TextHandles.addHandles(this);
        return super.getHandles();
    }

    public Figure getParentFigure() {
        return parentFigure;
    }

    public void setParentFigure(Figure parentFigure) {
        this.parentFigure = parentFigure;
    }
    
    public RotatedRectangle getRotatedRectangle(){
        return rotatedRect;
    }

    public Point[] getBoundPoints(){
        return rotatedRect.getPoints();
    }
        

    @Override
    public boolean isAllowResize() {
        return false;
    }
    
    public String getText(){
        return text;
    }
    
    public void setText(String text){
        this.text = text;
        updateDisplayBox();
    }
        
    @Override
    public void drawFigure(Graphics2D g) {
        if (getText() == null){
            return;
        }
        
        g.setFont(get(FONT_FACE));
        g.setColor(get(TEXT_COLOR));
        
        int padding = 2;
        if (get(ROTATION_ANGLE) == 0){
            Rectangle r = getDisplayBox();
            g.drawString(getText(), r.x + padding, r.y + r.height - padding);
            updateRotatedRect(r);
        }else{
            g.drawImage(rotatedText, padding, padding, null);
            rotateText(g);
        }
        
    }
    
    @Override
    protected void drawIndex(Graphics2D g) {
    }
    
    @Override
    protected void drawCaption(Graphics2D g) {
    }

    
    @Override
    public boolean isEmpty() {
        if (text == null || text.trim().length() == 0){
            return true;
        }
        return false;
    }
 
    @Override
    public void readAttributes(Map prop){
        super.readAttributes(prop);
        Map ui = (Map)prop.get("ui");
        
        setText(DataUtil.decodeString("text", prop));
        Point pos = DataUtil.decodePoint("pos", ui);
        Dimension size = DataUtil.decodeSize("size", ui);
        setDisplayBox(pos.x, pos.y, pos.x + size.width, pos.y + size.height);
        updateDisplayBox();
    }

    @Override
    public Map toMap() {
        Map map = (Map)super.toMap();
        DataUtil.putValue(map, "text", getText());
        RotatedRectangle rr = getRotatedRectangle();
        Rectangle r = getDisplayBox();
        
        return map;
    }     
    
    @Override
    public <T> void set(AttributeKey<T> key, T newValue) {
        super.set(key, newValue);
        if (getText() != null && "fontFace".equalsIgnoreCase(key.getKey())){
            Rectangle r = getDisplayBox();
            Dimension size = DrawUtil.getTextSize(getText(), get(FONT_FACE));
            r.width = size.width;
            r.height = size.height;
            TextHandles.addHandles(this);
        }
    }    

    private void rotateText(Graphics2D g) {
        if (get(ROTATION_ANGLE) == 0){
            Rectangle r = getDisplayBox();
            Point[] pts = new Point[]{
                new Point(r.x, r.y), 
                new Point(r.x + r.width, r.y),
                new Point(r.x + r.width, r.y + r.height), 
                new Point(r.x, r.y + r.height)
            };
            rotatedRect.setPoints(pts);
            rotatedRect.setSize(new Dimension(r.width, r.height));
            rotatedRect.setCenter(new Point((int)r.getCenterX(), (int)r.getCenterY()));
            TextHandles.addHandles(this);
        }else{
            rotatedText = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
            Graphics2D offscreen = rotatedText.createGraphics();
            offscreen.setFont(g.getFont());
            offscreen.setColor(g.getColor());
            DrawUtil.setHDRenderingHints(offscreen);

            Rectangle r = getDisplayBox();
            double cx = r.x + r.width / 2;
            double cy = r.y + r.height / 2;
            offscreen.rotate(Math.toRadians(-get(ROTATION_ANGLE)), cx, cy);

            FontMetrics metrics = offscreen.getFontMetrics();
            int ws = metrics.stringWidth(text);
            int hs = metrics.getDescent();
            float ux = (float)(cx - ws/2.0);
            float uy = (float)(cy + hs);
            offscreen.drawString(text, ux, uy);
            rotateRect(g);
        }
    }
    
    private void rotateRect(Graphics g){
        Rectangle r = getDisplayBox();
        int cx = r.x + (r.width / 2);
        int cy = r.y + (r.height / 2);
        
        Point[] pts = new Point[4];
        pts[0] = rotatePoint(cx, cy, r.x, r.y);
        pts[1] = rotatePoint(cx, cy, r.x + r.width, r.y);
        pts[2] = rotatePoint(cx, cy, r.x + r.width, r.y + r.height);
        pts[3] = rotatePoint(cx, cy, r.x, r.y + r.height);

        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int maxy = Integer.MIN_VALUE;
        
        for (Point pt : pts){
            if (pt.x < minx){
                minx = pt.x;
            }
            if (pt.x > maxx){
                maxx = pt.x;
            }
            if (pt.y < miny){
                miny = pt.y;
            }
            if (pt.y > maxy){
                maxy = pt.y;
            }
        }
        
        int rcx = minx + (Math.abs(maxx) - Math.abs(minx)) / 2 ;
        int rcy = miny + (Math.abs(maxy) - Math.abs(miny)) / 2 ;
        int rw = (maxx - minx);
        int rh = (maxy - miny);
        int dx = cx - maxx + rw / 2;
        int dy = cy - maxy + rh / 2;
        
        for (Point pt : pts){
            pt.x += dx;
            pt.y += dy;
        }
        
        rotatedRect.setSize(new Dimension(rw, rh));
        rotatedRect.setPoints(pts);
        rotatedRect.setCenter( new Point(rcx, rcy));
        TextHandles.addHandles(this);
//        
//        for (int i=0; i<3; i++){
//            Point p1 = pts[i];
//            Point p2 = pts[i+1];
//            g.drawLine(p1.x, p1.y, p2.x, p2.y);
//        }
//        g.drawLine(pts[0].x, pts[0].y, pts[3].x, pts[3].y);
//        return pts;
    }
    
    private Point rotatePoint(int cx, int cy, int x, int y){
        double rad = Math.toRadians(get(ROTATION_ANGLE));
        double rx = x * Math.cos(rad) + y * Math.sin(rad);
        double ry = -x * Math.sin(rad) + y * Math.cos(rad);
        return new Point((int) rx, (int)ry);
    }    

    @Override
    public void setRotation(int degree) {
        if (degree != get(ROTATION_ANGLE)){
            set(ROTATION_ANGLE, new Double(degree));
        }
    }
    

    @Override
    protected void move(int dx, int dy, MouseEvent e) {
        if (parentFigure != null && get(CENTER_TEXT)){
            return;
        }
        
        super.move(dx, dy, e);
        for (Point pt : getRotatedRectangle().getPoints()){
            if (pt != null){
                pt.x += dx;
                pt.y += dy;
            }
        }
        TextHandles.addHandles(this);
    }
    
    public void moveInner(int dx, int dy) {
        super.move(dx, dy, null);
        for (Point pt : getRotatedRectangle().getPoints()){
            if (pt != null){
                pt.x += dx;
                pt.y += dy;
            }
        }
        TextHandles.addHandles(this);
    }
        
    private void updateDisplayBox(){
        if (getText() == null) {
            return;
        }
        
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setFont(get(FONT_FACE));
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(getText());
        int h = fm.getHeight();
        Rectangle r = getDisplayBox();
        r.width = w;
        r.height = h;
        rotateText(g2);
        g2.dispose();
    }

    public void updateRotatedRect() {
        updateRotatedRect(getDisplayBox());
    }
    
    public void updateRotatedRect(Rectangle r) {
        rotatedRect.getPoints()[0] = new Point(r.x, r.y);
        rotatedRect.getPoints()[1] = new Point(r.x + r.width, r.y);
        rotatedRect.getPoints()[2] = new Point(r.x + r.width, r.y + r.height);
        rotatedRect.getPoints()[3] = new Point(r.x, r.y + r.height);
    }
    
    
}
