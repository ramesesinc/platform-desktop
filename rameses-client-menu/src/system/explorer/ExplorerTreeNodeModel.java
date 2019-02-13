/*
 * ExplorerTreeNodeModel.java
 *
 * Created on August 16, 2013, 9:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package system.explorer;

import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.TreeNodeModel;

/**
 *
 * @author wflores
 */
public class ExplorerTreeNodeModel extends TreeNodeModel {

    public boolean isAllowOpenOnSingleClick() { return false; }
    
    public void initChildNodes(Node[] nodes) {
        if (nodes == null) return;
        
        for (Node node: nodes) {
            initChildNode(node, getSelectedNode()); 
        }
    }   
    
    public void initChildNode(Object childNode, Object parentNode){
    }
}
