package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.FigureEvent;
import com.rameses.rcp.draw.FigureListener;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.support.AttributeKey;
import com.rameses.rcp.draw.utils.Geom;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.tools.CreationTool;
import com.rameses.rcp.draw.utils.DataUtil;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractFigure implements Figure{
    private String id;
    private String type;
    private String name;
    private String caption;
    private String tooltip;
    private Rectangle displayBox;
    private List<Handle> handles;
    private boolean selected;
    private int index = 0;
    private boolean showIndex = false;
    private boolean system = false;
    
    private boolean allowResize;
    private boolean showHandle;
    private boolean connectionAllowed;
    private boolean startConnectionAllowed;
    private boolean endConnectionAllowed;
    private List<Connector> connectors;
    private TextFigure innerText;
    private Map info;
    private List<FigureListener> listeners;
    
    public AbstractFigure(){
        id = this.hashCode()+"";
        displayBox = new Rectangle(0,0);
        handles = new ArrayList<Handle>();
        allowResize = true;
        showHandle = true;
        connectionAllowed = true;
        startConnectionAllowed = connectionAllowed;
        endConnectionAllowed = connectionAllowed;
        connectors = new ArrayList<Connector>();
        info = new HashMap();
        listeners = new ArrayList<FigureListener>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    
    @Override
    public void setType(String type){
        this.type = type;
    }

    @Override
    public String getCategory() {
        return "drawing";
    }
    
    @Override
    public String getCaption(){
        return caption;
    }

    @Override
    public void setCaption(String caption) {
        String oldCaption = this.caption;
        
        this.caption = caption;
        if (caption != null ){
            if (innerText == null){
                innerText = new TextFigure(getCaption(), 0, 0);
                innerText.setParentFigure(this);
            }else{
                innerText.setText(getCaption());
            }
            centerInnerText();
        }else{
            innerText = null;
        }
        
        firePropertyChanged("caption", String.class, oldCaption, caption);
        
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean isSystem() {
        return system;
    }

    @Override
    public void setSystem(boolean system) {
        this.system = system;
    }
    
    @Override
    public boolean getSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    @Override
    public int getToolIndex() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
    
    @Override
    public Tool getTool() {
        return new CreationTool();
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public void showHandles(boolean showHandle) {
        this.showHandle = showHandle;
    }

    @Override
    public void toggleShowIndex() {
        this.showIndex = !this.showIndex;
    }

    @Override
    public boolean isShowIndex() {
        return showIndex;
    }
    
    
    @Override
    public boolean isAllowResize() {
        return allowResize;
    }

    @Override
    public void setAllowResize(boolean allowResize) {
        this.allowResize = allowResize;
    }
    
    @Override
    public Rectangle getDisplayBox() {
        return displayBox;
    }

    @Override
    public Rectangle getBounds() {
        return displayBox;
    }
    
    @Override
    public Figure getInnerText(){
        return innerText;
    }

    @Override
    public Point getLocation() {
        return new Point(getX(), getY());
    }

    @Override
    public void setDisplayBox(Rectangle displayBox) {
        this.displayBox = displayBox;
        updateConnectors();
        centerInnerText();
    }
    
    @Override 
    public void setDisplayBox(int x1, int y1, int x2, int y2){
        Rectangle rect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        setDisplayBox(rect);
        updateConnectors();
        centerInnerText();
    }

    @Override
    public int getX() {
        return getDisplayBox().x;
    }

    @Override
    public int getY() {
        return getDisplayBox().y;
    }
    
    @Override
    public Point getCenter() {
        Rectangle r = getDisplayBox();
        int x = r.x + (r.width / 2);
        int y = r.y + (r.height / 2);
        return new Point(x, y);
    }

    /*Centers the figure on the specified point*/
    @Override
    public void center(Point pt) {
        Rectangle r = getDisplayBox();
        int dx = r.width / 2;
        int dy = r.height / 2;
        r.x = pt.x - dx;
        r.y = pt.y - dy;
    }
    
    @Override
    public boolean hitTest(int x, int y){
        return getDisplayBox().contains(x, y);
    }
    
    
    @Override
    public void setLocation(Point pt) {
        Rectangle r = getDisplayBox();
        r.x = pt.x;
        r.y = pt.y;
    }
    
    @Override
    public final void moveBy(int dx, int dy, MouseEvent e) {
        move(dx, dy, e);
        moveInnerText(dx, dy, e);
    }
    
    protected void move(int dx, int dy, MouseEvent e){
        int x = displayBox.x + dx;
        int y = displayBox.y + dy;
        displayBox.setLocation(x, y);

        for(Connector c : connectors){
            c.moveBy(dx, dy, e, this);
        }
    }
    
    @Override
    public List<Handle> getHandles() {
        if (showHandle){
            return handles;
        }
        return new ArrayList<Handle>();
    }

    @Override
    public void clearHandles() {
        handles.clear();
    }
    
    
    
    @Override
    public void addHandle(Handle handle) {
        if (!handles.contains(handle)){
            handles.add(handle);
        }
    }

    @Override
    public void drawHandles(Graphics2D g) {
        for(Handle h : getHandles()){
            h.draw(g);
        }
    }
    
    @Override
    public void drawConnectors(Graphics2D g){
        for (Connector c : connectors){
            c.draw(g);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Color oldColor = g.getColor();
        Stroke oldStroke = g.getStroke();
        Font oldFont = g.getFont();

        drawFigure(g);
        drawCaption(g);
        if (showIndex){
            drawIndex(g);
        }
        
        g.setFont(oldFont);
        g.setStroke(oldStroke);
        g.setColor(oldColor);
    }

    @Override
    public final Map getInfo() {
        return info;
    }
    
    
    protected abstract void drawFigure(Graphics2D g);
    
    protected void drawCaption(Graphics2D g) {
        if (innerText != null){
//            boolean b = get(CENTER_TEXT);
//            if (b){
//                centerInnerText();
//            }
            innerText.drawFigure(g);
        }
    }
    
    protected void drawIndex(Graphics2D g) {
        if (showIndex){
            Rectangle r = getDisplayBox();
            Dimension ts = DrawUtil.getTextSize(g, getIndex()+"");
            int x = r.x + (r.width - ts.width) / 2;
            int y = r.y + r.height + ts.height;
            g.setColor(Color.RED);
            g.drawString(getIndex()+"", x, y);
        }
    }

    @Override
    public void setRotation(int degree) {
    }
    
    

    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Figure)){
            return false;
        }
        Figure other = (Figure) obj;
        return this.hashCode() == other.hashCode();
    }

    @Override
    public boolean isEmpty() {
        if (getDisplayBox().getWidth() < get(FIGURE_MINIMUM_WIDTH) || getDisplayBox().getHeight() <= get(FIGURE_MINIMUM_HEIGHT)){
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isConnectionAllowed(){
        return connectionAllowed;
    }
    
    @Override
    public void setConnectionAllowed(boolean allowed){
        this.connectionAllowed = allowed;
    }
    
    @Override
    public boolean isStartConnectionAllowed(){
        return startConnectionAllowed;
    }
    
    @Override
    public void setStartConnectionAllowed(boolean allowed){
        this.startConnectionAllowed = allowed;
    }
        
    @Override
    public boolean isEndConnectionAllowed(){
        return endConnectionAllowed;
    }
    
    @Override
    public void setEndConnectionAllowed(boolean allowed){
        this.endConnectionAllowed = allowed;
    }
        
    @Override 
   public List<Connector> getConnectors(){
        return connectors;
    }
    
    @Override
    public void addConnector(Connector connector){
        if (connector != null && !connectors.contains(connector)){
            connectors.add(connector);
        }
    }
    
    @Override
    public void removeConnector(Connector connector){
        if (connector != null && connectors.contains(connector)){
            connectors.remove(connector);
        }
    }
    
    /*============================================================
    * Returns the intersection point between a figure
    * and a line from the center of the figure to the given point
    * Default implementation is the displayBox.
    * Subclass should override for specific implementation
    * 
    * ============================================================*/
    @Override
    public Point intersect(Point p){
        Rectangle r = getDisplayBox();
        List<Point> points = new ArrayList<Point>();
        points.add(Geom.northWest(r));
        points.add(Geom.northEast(r));
        points.add(Geom.southEast(r));
        points.add(Geom.southWest(r));
        points.add(Geom.northWest(r));
        
        Point center = getCenter();
        
        Point p1 = points.get(0);
        for(int i = 1; i < points.size(); i++){
            Point p2 = points.get(i);
            Point intersect = Geom.intersect(p1.x, p1.y, p2.x, p2.y, p.x, p.y, center.x, center.y);
            if (intersect != null){
                return intersect;
            }
            p1 = p2;
        }
        return null;
    }

    @Override
    public void readAttributes(Map prop){
        setId(DataUtil.decodeString("id", prop));
        setName(DataUtil.decodeString("name", prop));
        setIndex(DataUtil.decodeInt("index", prop));
        setTooltip(DataUtil.decodeString("tooltip", prop));
        
        Object o = prop.get("info");
        if (o != null && o instanceof Map){
            info = (Map)o;
        }
        
        Map ui = (Map) prop.get("ui");
        Point pos = DataUtil.decodePoint("pos", ui);
        Dimension size = DataUtil.decodeSize("size", ui);
        setDisplayBox(pos.x, pos.y, pos.x + size.width, pos.y + size.height);
        setCaption(DataUtil.decodeString("caption", prop));
        
        Map inner = (Map)prop.get("inner");
        if (inner != null && innerText != null){
            innerText.readAttributes(inner);
        }
    }

    @Override
    public Map toMap() {
        Map map = new HashMap();
        DataUtil.putValue(map, "id", getId());
        DataUtil.putValue(map, "name", getName());
        DataUtil.putValue(map, "index", getIndex());
        DataUtil.putValue(map, "caption", getCaption());
        DataUtil.putValue(map, "tooltip", getTooltip());
        map.put("info", getInfo());
        
        Rectangle r = getDisplayBox();
        Map ui = new HashMap();
        DataUtil.putValue(ui, "type", getType());
        DataUtil.putValue(ui, "pos", DataUtil.encodePos(r));
        DataUtil.putValue(ui, "size", DataUtil.encodeSize(r));
        if (innerText != null){
            map.put("inner", innerText.toMap());
        }
        map.put("ui", ui);
        return map;
    }

    private void updateConnectors() {
        if (getConnectors().isEmpty()){
            return;
        }
        
        for (Connector connector : getConnectors()){
            Figure figure;
            Point pt;
            int idx; 
            
            if (connector.getStartFigure() == this){
                figure = connector.getStartFigure();
                pt = figure.intersect(connector.getPoints().get(1));
                updatePoint(connector.getPoints().get(0), pt);
            }else if (connector.getEndFigure() == this){
                idx = connector.getPoints().size() - 2;
                figure = connector.getEndFigure();
                pt = figure.intersect(connector.getPoints().get(idx));
                updatePoint(connector.getPoints().get(idx+1), pt);
            }
        }
    }

    protected void moveInnerText(int dx, int dy, MouseEvent e) {
        if (innerText != null){
            innerText.moveInner(dx, dy);
        }
    }

    protected void centerInnerText() {
        if (innerText != null && get(CENTER_TEXT)){
            Rectangle pr = getDisplayBox();
            Rectangle ir = innerText.getDisplayBox();
            int dx = (pr.width - ir.width) / 2;
            int dy = (pr.height - ir.height) / 2;
            ir.x = pr.x + dx;
            ir.y = pr.y + dy;
            innerText.updateRotatedRect();
        }
    }
    
    private void updatePoint(Point pt, Point newPt) {
        pt.x = newPt.x;
        pt.y = newPt.y;
    }    

    @Override
    public void addFigureListener(FigureListener listener) {
        if (!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    @Override
    public void removeFigureListener(FigureListener listener) {
        if (listeners.contains(listener)){
            listeners.remove(listener);
        }
    }
    
    protected void fireAttributedChanged(AttributeKey key, Object oldValue, Object newValue){
        if (!listeners.isEmpty() && (oldValue == null || newValue == null || !oldValue.equals(newValue))){
            FigureEvent event = new FigureEvent(this, key, oldValue, newValue);
            for (FigureListener listener : listeners){
                listener.attributeChanged(event);
            }
        }
    }

    private void firePropertyChanged(String propertyName, Class type, String oldValue, String newValue) {
        if (!listeners.isEmpty() && (oldValue == null || newValue == null || !oldValue.equals(newValue))){
            FigureEvent event = new FigureEvent(this, propertyName, type, oldValue, newValue);
            for (FigureListener listener : listeners){
                listener.propertyChanged(event);
            }
        }
    }
    
    
}
