package system.explorer;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import system.explorer.*;

public abstract class AbstractTreeExplorer {

    def selection;
    def rootNode;
    //def defaultIcon = 'images/doc-view16.png';
    def defaultIcon = 'images/tree/diamond.png';

    boolean rootVisible = true;

    public abstract void init();

    void doInit( root ) { 
        rootNode = new Node(root); 
    }

    boolean loadChildren( root ) {
        def folders = OsirisContext.session.getFolders( root.folder ? root.folder : root.id );
        if (!folders) return false;

        root.children = [];
        folders.each { 
            def item = [id:it.id, caption:it.caption, folder:it];
            if (it.invoker != null) { 
                item.invoker = it.invoker;                
                item.id = it.invoker.workunitid;
                item.caption = it.invoker.caption; 
                item.icon = it.invoker.properties?.icon? it.invoker.properties.icon: defaultIcon;                
                item.leaf = true;
                root.children.add(item);
            } 
            else if (loadChildren(item)) {
                root.children.add(item);
            }
        }
        return root.children.size() > 0
    } 


    def tree = [
        isAllowOpenOnSingleClick: { return false; },  
        isRootVisible: { return rootVisible },
        getRootNode : { return rootNode; },

        getNodeList: {node-> 
            if (node.item == null) return null; 
        
            loadChildren(node.item); 
            return node.item.children; 
        }, 
        
        initChildNode: {cnode, pnode-> 
            if (pnode?.parent == null && cnode.icon == null) {
                cnode.icon = 'images/pyramid.png';
            } 
        }, 
                
        openLeaf: {node->
            def invoker = node?.item?.invoker;
            if (invoker != null) InvokerUtil.invoke(invoker);
            
            return null; 
        }, 
                
        openFolder: {node->
            return null;
        }                
    ] as ExplorerTreeNodeModel;
}