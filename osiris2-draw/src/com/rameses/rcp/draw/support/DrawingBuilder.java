package com.rameses.rcp.draw.support;

import com.rameses.rcp.draw.figures.FigureFactory;
import com.rameses.rcp.draw.StdDrawing;
import com.rameses.rcp.draw.figures.LineConnector;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.utils.DataUtil;
import groovy.util.slurpersupport.NodeChild;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DrawingBuilder {
    private Drawing drawing;
    private XmlDataExtractor xde;
    
    public DrawingBuilder() {
        this.drawing = new StdDrawing();
    }
    
    public Drawing buildDrawing(File file){
        return buildDrawing(loadXml(file));
    }
    
    public Drawing buildDrawing(String xml){
        String gpath = "Drawing.Figures.Figure.collect{it}";
        xde = new XmlDataExtractor(xml, "Drawing");
        
        List<NodeChild> figures = (List<NodeChild>)xde.extract(gpath);
        for (NodeChild figureNode : figures){
            Map prop = figureNode.attributes();
            Figure figure = FigureFactory.createFigure(prop);
            if (figure != null){
                processAttributeKeys(figure, figureNode);
                processConnector(figure, prop);
                drawing.addFigure(figure);
                
                
            }
        }
        xde = null;
        return drawing;
    }
    
    private void processAttributeKeys(Figure figure, NodeChild figureNode){
        String gpath = "Drawing.Figures.Figure.find{it.'@id' == " + figure.getId() + "}.attributes.collect{it.attributes()}";
        List<Map>attributes = (List<Map>)xde.extract(gpath);
        
        for (Map attr : attributes){
            Iterator itr = attr.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) itr.next();
                AttributeKey ak = AttributeKeys.findByKey(entry.getKey());
                if (ak != null){
                    figure.set(ak, ak.decode(entry.getValue(), ak.getAttributeClass()));
                }
            }
        }
    }
    
    private String loadXml(File file){
        FileReader reader = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try{
            reader = new FileReader(file);
            br = new BufferedReader(reader);
            String s; 
            while((s = br.readLine()) != null){
                sb.append(s);
            }
            return sb.toString();
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally{
            if (reader != null){
                try{
                    reader.close();
                }
                catch(Exception ignore){
                    //
                }
            }
        }
    }

    private void processConnector(Figure figure, Map prop) {
        if (! (figure instanceof LineConnector)){
            return;
        }
        
        LineConnector connector = (LineConnector) figure;
        String startId = prop.get("startFigureId").toString();
        String endId = prop.get("endFigureId").toString();
        connector.setStartFigure(drawing.figureById(startId));
        connector.setEndFigure(drawing.figureById(endId));
    }
}
