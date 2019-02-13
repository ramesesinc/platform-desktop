package com.rameses.rcp.draw.support;

public class XmlAttribute {

    private String key;
    private Object value;

    public XmlAttribute(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String toXml() {
        return key + "=\"" + value.toString() + "\" ";
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof XmlAttribute)) {
            return false;
        }

        XmlAttribute other = (XmlAttribute) obj;
        return this.hashCode() == other.hashCode();
    }
}
