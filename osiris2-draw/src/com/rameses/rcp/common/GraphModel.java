package com.rameses.rcp.common;

import com.rameses.rcp.draw.figures.WorkflowNode;
import com.rameses.rcp.draw.interfaces.Connector;
import com.rameses.rcp.draw.interfaces.Figure;
import java.util.ArrayList;
import java.util.List;

public class GraphModel extends DrawModel {
           
    public final List getNodes(){
        List nodes = new ArrayList();
        for (Figure f : getEditor().getDrawing().getFigures()){
            if (f instanceof WorkflowNode){
                nodes.add(f);
            }
        }
        return nodes;
    }
    
    public final List getConnectorsFrom(Object node){
        List connectors = new ArrayList();
        if (node instanceof WorkflowNode){
            WorkflowNode wn = (WorkflowNode)node;
            for (Connector c : getEditor().getDrawing().getConnectors()){
                if (c.getStartFigure() == wn){
                    connectors.add(c);
                }
            }
        }
        return connectors;
    }
    
    public final List getConnectorsTo(Object node){
        List connectors = new ArrayList();
        if (node instanceof WorkflowNode){
            WorkflowNode wn = (WorkflowNode)node;
            for (Connector c : getEditor().getDrawing().getConnectors()){
                if (c.getEndFigure() == wn){
                    connectors.add(c);
                }
            }
        }
        return connectors;
    }    

}
