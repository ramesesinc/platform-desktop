package com.rameses.rcp.draw.figures;

public class WorkflowConnector extends LineConnector  {

    public WorkflowConnector() {
        super();
    }
    
    @Override
    public String getCategory() {
        return "workflow";
    }
        
    @Override
    public String getIcon() {
        return "images/draw/connector16.png";
    }
    
        
    @Override
    public String getType(){
        return "connector";
    }
    
    @Override
    public String getToolCaption() {
        return "Connector";
    }
}
