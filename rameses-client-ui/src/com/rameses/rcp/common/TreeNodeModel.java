package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeNodeModel extends AbstractTreeNodeModel
{
    private static final long serialVersionUID = 1L;
    
    public TreeNodeModel() {
    }

    public List<Map> getNodeList(Node node) { return null; } 
    
    public Node[] fetchNodes(Node node) { 
        List<Map> list = getNodeList(node); 
        if (list == null || list.isEmpty()) return null; 
        
        List<Node> nodes = new ArrayList(); 
        for (Map data: list) { 
            if (data != null && !data.isEmpty()) { 
                Node childNode = new Node(data); 
                nodes.add(childNode);  
            } 
        } 
        return nodes.toArray(new Node[]{}); 
    } 
    
    // called by the XTree before adding it as children
    public void initChildNodes(Node[] nodes) {} 
    
}
