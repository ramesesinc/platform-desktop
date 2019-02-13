package com.rameses.rcp.draw;

import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.interfaces.Handle;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StdDrawing implements Drawing{
    private List<Figure> figures;
    private List<Figure> selections;
    private List<Connector> connectors;
    
    private boolean showHandles;
    

    public StdDrawing() {
        figures = new ArrayList<Figure>();
        selections = new ArrayList<Figure>();
        connectors = new ArrayList<Connector>();
        showHandles = false;
    }

    
    @Override
    public void draw(Graphics2D g){
        for(Connector c : getConnectors()){
            c.draw(g);
        }
        
        for(Figure f : getFigures()){
            f.draw(g);
        }
        
        for(Figure f : getSelections()){
            f.drawHandles(g);
        }
    }

    @Override
    public void setShowHandles(boolean showHandles) {
        this.showHandles = showHandles;
        for (Figure f : getFigures()){
            f.showHandles(showHandles);
        }
    }
    
    
    
    @Override
    public List<Figure> getFigures(){
        return figures;
    }

    @Override
    public Figure addFigure(Figure figure) {
        if (!figures.contains(figure)) {
            figures.add(figure);
        }
        return figure;
    }

    @Override
    public void removeFigure(Figure figure) {
        if (figures.contains(figure)) {
            for(Connector c : figure.getConnectors()){
                removeConnector(c);
            }
            figures.remove(figure);
            connectors.remove(figure);
        }
    }
    
    @Override
    public List<Figure> getSelections() {
        return selections;
    }
    
    @Override
    public boolean hasMultipleSelections(){
        return selections.size() > 1;
    }

    @Override
    public void clearSelections() {
        for (Figure figure : selections){
            figure.setSelected(false);
        }
        selections.clear();
    }

    @Override
    public void clearFigures() {
        figures.clear();
        clearSelections();
        clearConnectors();
    }
    
    
    
    @Override
    public void addSelection(Figure figure) {
        if (figure != null){
            if (!selections.contains(figure)) {
                figure.setSelected(true);
                selections.add(figure);
            }
        }
    }

    @Override
    public void toggleSelection(Figure figure) {
        if (isFigureSelected(figure)){
            removeSelection(figure);
        }
        else {
            addSelection(figure);
        }
    }

    @Override
    public void removeSelection(Figure figure) {
        if (selections.contains(figure)) {
            figure.setSelected(false);
            selections.remove(figure);
        }
    }
    

    @Override
    public Figure figureAt(int x, int y) {
        return figureAt(x, y, null);
    }
    
    @Override
    public Figure figureAt(int x, int y, Figure exclude){
        if (figures.isEmpty()){
            return null;
        }
        for (int i = figures.size() - 1; i >= 0; i--){
            Figure f = figures.get(i);
            if (f != exclude && f.hitTest(x, y)){
                return f;
            }
        }
        return null;
    }

    @Override
    public Figure innerFigureAt(int x, int y) {
        if (figures.isEmpty()){
            return null;
        }
        for (int i = figures.size() - 1; i >= 0; i--){
            Figure f = figures.get(i);
            Figure inner = f.getInnerText();
            if (inner != null && inner.hitTest(x, y)){
                removeSelection(f);
                return inner;
            }
        }
        return null;
    }
    
    

    @Override
    public Figure figureById(String id){
        if (id != null){
            for (Figure f : getFigures()){
                if (f.getId().equalsIgnoreCase(id)){
                    return f;
                }
            }
        }
        return null;
    }
    
    @Override
    public Figure figureByName(String name){
        if (name != null){
            for (Figure f : getFigures()){
                if (name.equalsIgnoreCase(f.getName())){
                    return f;
                }
            }
        }
        return null;
    }
    

    @Override
    public Handle handleAt(int x, int y) {
        for(Figure f : getSelections()){
            for (Handle h : f.getHandles()){
                if (h.locate(x, y)){
                    return h;
                }
            }
        }
        return null;
    }
    
    @Override
    public Connector connectorAt(int x, int y) {
        for(Connector c : getConnectors()){
            if ( ((Figure)c).hitTest(x, y) ){
                return c;
            }
        }
        return null;
    }
    

    @Override
    public boolean isFigureSelected(Figure figure){
        for(Figure f : selections){
            if (figure.equals(f)){
                return true;
            }
        }
        return false;
    }

    
    @Override
    public List<Connector> getConnectors(){
        return connectors;
    }
    
    @Override
    public void addConnector(Connector connector){
        if (connector == null) {
            return;
        }
        
        if (!connectors.contains(connector)){
            connectors.add(connector);
            addFigure((Figure)connector);
        }
    }
    
    @Override
    public void removeConnector(Connector connector){
        if (connector == null) {
            return;
        }
        
        if (connectors.contains(connector)){
            connectors.remove(connector);
            removeFigure((Figure)connector);
        }
    }

    @Override
    public String getXml() {
        //TODO: build xml data from Map
        // Map data = getData();
        // String xml = XxmlBuilder.toXml(data);
        
        
//        XmlBuilder builder = new XmlBuilder();
//        XmlElement parent = new XmlElement("Figures");
//        builder.getRoot().addChildElement(parent);
//        
//        for(Figure f : getFigures()){
//            f.buildXml(parent);
//        }
//        
//        return builder.toXml();
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Map getData() {
        List<Map> figures = new ArrayList<Map>();
        List<Map> connectors = new ArrayList<Map>();
        for (Figure f: getFigures()){
            if (f instanceof Connector){
                connectors.add(f.toMap());
            } else{
                figures.add(f.toMap());
            }
        }
        Map data = new HashMap();
        data.put("figures", figures);
        data.put("connectors", connectors);
        return data;
    }

    @Override
    public Rectangle getBounds() {
        Rectangle r = null;
        
        for(Figure f : getFigures()){
            if (r == null){
                r = new Rectangle(f.getBounds());
            }else{
                r.add(f.getDisplayBox());
            }
        }
        return r;
    }
    
    @Override
    public List<Figure>deleteSelections() {
        List<Figure> deletedItems = new ArrayList<Figure>(getSelections());
        for (Figure f: getSelections()){
            if (!f.isSystem()){
                for (Connector c : f.getConnectors()){
                     if (!deletedItems.contains(c)){
                         deletedItems.add((Figure)c);
                     }
                }
                removeFigure(f);
            }
        }
        clearSelections();
        return deletedItems;
    }
    
    protected final void clearConnectors(){
        connectors.clear();
    }
    
    
}
