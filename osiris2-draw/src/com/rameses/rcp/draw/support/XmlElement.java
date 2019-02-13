package com.rameses.rcp.draw.support;

import java.util.ArrayList;
import java.util.List;

public class XmlElement {
    private String tagName;
    private List<XmlElement> children;
    private List<XmlAttribute> attributes;
    
    public XmlElement() {
        this(null);
    }

    public XmlElement(String tagName) {
        this.tagName = tagName;
        children = new ArrayList<XmlElement>();
        attributes = new ArrayList<XmlAttribute>();
    }

    public void addAttribute(String key, Object value) {
        if (value != null){
            XmlAttribute attr = new XmlAttribute(key, value);
            if (attributes.contains(attr)) {
                int idx = attributes.indexOf(attr);
                attributes.set(idx, attr);
            } else {
                attributes.add(attr);
            }
        }
    }
    
    public void addChildElement(XmlElement element){
        children.add(element);
    }
    
    public void removeChildElement(XmlElement element){
        children.remove(element);
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<XmlAttribute> getAttributes() {
        return attributes;
    }
    
    public List<XmlElement> getChildren() {
        return children;
    }
    
    public String toXml(){
        StringBuilder sb = new StringBuilder();
        sb.append("<" + getTagName());
        sb.append(attributesToXml());
        if (getChildren().isEmpty()){
            sb.append(" />\n");
        }
        else{
            sb.append(">\n");
            for(XmlElement e : children){
                sb.append(e.toXml());
            }
            sb.append("</" + getTagName() + ">\n");
        }
        return sb.toString();
    }
    
    protected String attributesToXml(){
        if (getAttributes().isEmpty()){
            return "";
        }
        else{
            StringBuilder sb = new StringBuilder(" ");
            for(XmlAttribute attr : getAttributes()){
                sb.append(attr.toXml());
            }
            return sb.toString();
        }
    }
}
