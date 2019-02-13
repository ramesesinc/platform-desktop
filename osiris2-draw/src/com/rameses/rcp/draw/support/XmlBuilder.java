package com.rameses.rcp.draw.support;

public class XmlBuilder  {
    private XmlElement root;
    
    
    public XmlBuilder(){
        this("Drawing");
    }
    
    public XmlBuilder(String tagName) {
        root = new XmlElement(tagName);
    }
    
    public XmlElement getRoot(){
        return root;
    }
    
    public void setRoot(XmlElement root){
        this.root = root;
    }
    
    public String toXml(){
        return root.toXml();
    }
}
