/*
 * XTree.java
 *
 * Created on August 2, 2010, 10:27 AM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.rcp.common.MapObject;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.NodeFilter;
import com.rameses.rcp.common.NodeListener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TreeNodeModel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.util.ControlSupport;
import com.rameses.rcp.framework.NavigatablePanel;
import com.rameses.rcp.framework.NavigationHandler;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class XTree extends JTree implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo 
{
    private DefaultProvider provider = new DefaultProvider(); 
    private TreeEventSupport eventSupport = new TreeEventSupport();
    
    private Binding binding;
    private String[] depends;
    private String handler;    
    private Object handlerObject;
    private boolean dynamic;
    private int index;
    
    private NodeTreeRenderer renderer;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;
    private TreeNodeModel nodeModel;
    
    private int stretchWidth;
    private int stretchHeight;    
    private String visibleWhen;
            
    public XTree() { 
        initComponents(); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" initComponents ">
    
    private void initComponents() 
    {
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //setCellRenderer(renderer=new NodeTreeRenderer()); 
        setCellRenderer(new TreeCellRendererImpl()); 

        //install listeners
        super.addTreeSelectionListener(eventSupport); 
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openNode");
        getActionMap().put("openNode", new AbstractAction(){             
            public void actionPerformed(ActionEvent e) {
                fireOpenSelectedNode(true);
            } 
        });
        new MouseEventSupport(this).install(); 
        setBorder(BorderFactory.createEmptyBorder(3, 2, 0, 0)); 
        addAncestorListener(new AncestorListener() {
            private boolean inited;
            
            public void ancestorAdded(AncestorEvent event) 
            {
                if (inited) return;
                
                inited = true; 
                JComponent owner = XTree.this;
                Container parent = owner.getParent(); 
                if (parent instanceof JViewport) 
                {
                    JViewport jv = (JViewport) parent;
                    jv.setBackground(owner.getBackground()); 
                }
            }
            public void ancestorMoved(AncestorEvent event) {
            }
            public void ancestorRemoved(AncestorEvent event) {
            }
        });
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Getters and Setters ">
    
    public boolean isDynamic() { return dynamic; }    
    public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
    
    public String getHandler() { return handler; }    
    public void setHandler(String handler) { this.handler = handler; }
    
    public Object getHandlerObject() { return handlerObject; }
    public void setHandlerObject(Object handlerObject) {
        this.handlerObject = handlerObject; 
    }

    protected void fireValueChanged(TreeSelectionEvent e) {
        TreePath newpath = e.getNewLeadSelectionPath(); 
        if (newpath != null) {
            XTree.DefaultNode newnode = (XTree.DefaultNode) newpath.getLastPathComponent(); 
            if (newnode.request_to_open && nodeModel != null && nodeModel.isAllowOpenOnSingleClick()) { 
                fireOpenSelectedNode(true, newnode); 
            } 
        }
        
        super.fireValueChanged(e); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIControl implementation ">
    
    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) { this.binding = binding; }
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) { this.depends = depends; }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) { this.index = index; }

    public int compareTo(Object o) { 
        return UIControlUtil.compare(this, o);
    }
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {}
    
    public void refresh() {
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }    
    }
    
    public void load() 
    {
        try 
        {
            Object obj = getHandlerObject(); 
            if (obj instanceof String) 
                obj = UIControlUtil.getBeanValue(this, obj.toString());

            String shandler = getHandler(); 
            if (shandler != null) 
                obj = UIControlUtil.getBeanValue(this, shandler);

            if (obj == null) throw new Exception("A handler must be provided");

            nodeModel = (TreeNodeModel) obj;
        }
        catch(Exception ex) 
        {
            nodeModel = new DummyTreeNodeModel();
            
            if (ClientContext.getCurrentContext().isDebugMode()) 
                ex.printStackTrace(); 
        }
        
        nodeModel.setProvider(provider); 

        Node rootNode = nodeModel.getRootNode(); 
        if (rootNode == null) 
        {
            rootNode = new Node("root", "");
            setRootVisible(false); 
        } 
        else {
            setRootVisible(nodeModel.isRootVisible()); 
        }
        
        root = new DefaultNode(rootNode); 
        model = new DefaultTreeModel(root, true);
        //treat items w/ no children as folders unless explicitly defined as leaf
        model.setAsksAllowsChildren(true);         
        setModel(model); 
        if (nodeModel.isAutoSelect()) {
            try { 
                setSelectionRow(0); 
            } catch(Throwable t) {;} 
            
            fireOpenSelectedNode(true); 
        } 
    }
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("dynamic", isDynamic()); 
        map.put("handler", getHandler());
        map.put("handlerObject", getHandlerObject()); 
        return map;
    }       

    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property; 
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Owned and helper methods ">

    public void removeTreeSelectionListener(TreeSelectionListener handler) {
        eventSupport.remove(handler); 
    }
    public void addTreeSelectionListener(TreeSelectionListener handler) {
        eventSupport.add(handler); 
    }
    
    protected void processMouseEvent(MouseEvent me) 
    {
        if (me.getID() == MouseEvent.MOUSE_PRESSED) {
            //process only single-click 
            if (SwingUtilities.isLeftMouseButton(me) && me.getClickCount() == 1) {
                TreePath newpath = getPathForLocation(me.getX(), me.getY()); 
                if (newpath == null) return; 

                XTree.DefaultNode newnode = (XTree.DefaultNode) newpath.getLastPathComponent(); 
                newnode.request_to_open = true;
            }
        } else if (me.getID() == MouseEvent.MOUSE_CLICKED) {
            //process only double-click
            if (SwingUtilities.isLeftMouseButton(me) && me.getClickCount() == 2) {
                TreePath newpath = getPathForLocation(me.getX(), me.getY()); 
                if (newpath == null) return;
                
                XTree.DefaultNode newnode = (XTree.DefaultNode) newpath.getLastPathComponent(); 
                if (newnode.request_to_open) {
                    fireOpenSelectedNode(true, newnode); 
                }
            } 
        }
        super.processMouseEvent(me); 
    }
    
    private void fireOpenSelectedNode(boolean forcely) 
    {   
        DefaultNode selNode = getSelectedNode(); 
        fireOpenSelectedNode(forcely, selNode); 
    }    
    
    private void fireOpenSelectedNode(boolean forcely, final XTree.DefaultNode selNode) { 
        if (selNode == null) return;
        if (!selNode.hasChanged && !forcely) return;

        selNode.hasChanged = false;        
        selNode.request_to_open = false; 
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try { 
                    openNode(selNode.getNode());
                } catch(Exception ex) {
                    MsgBox.err(ex); 
                } finally {
                    
                }
            }
        });
    }
        
    private XTree.DefaultNode getSelectedNode() 
    {
        TreePath treePath = getSelectionPath();
        if (treePath == null) return null; 
        
        return (DefaultNode) treePath.getLastPathComponent(); 
    }
    
    private void openNode(Node node) 
    {
        Object retVal = null;
        if (node == null) {
            //do nothing
        } else if (node.isLeaf()) {
            retVal = nodeModel.openLeaf(node);
        } else {
            retVal = nodeModel.openFolder(node);
        }
        
        if (retVal == null) return;
        
        NavigationHandler handler = ClientContext.getCurrentContext().getNavigationHandler();
        NavigatablePanel panel = UIControlUtil.getParentPanel(this, null);
        handler.navigate(panel, this, retVal);
    }
    
    private Node doFindNode(DefaultNode parent, NodeFilter filter) 
    {
        for (int i = 0; i < parent.getChildCount(); i++) 
        {
            DefaultNode child = (DefaultNode) parent.getChildAt(i);
            Node n = child.getNode();
            if (filter.accept(n)) return n;
            
            if (n.isLoaded() && child.getChildCount() > 0) 
            {
                Node nn = doFindNode(child, filter);
                if (nn != null) return nn;
            }
        }
        return null;
    }
    
    private void doCollectNodeList(DefaultNode parent, NodeFilter filter, List nodes) 
    {
        for (int i=0; i < parent.getChildCount(); i++) 
        {
            DefaultNode oChild = (DefaultNode) parent.getChildAt(i);
            Node oNode = oChild.getNode();
            if (filter.accept(oNode)) nodes.add(oNode);
            
            if (oNode.isLoaded() && oChild.getChildCount() > 0) 
                doCollectNodeList(oChild, filter, nodes);
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultNode (class) ">
    
    public class DefaultNode extends DefaultMutableTreeNode implements NodeListener 
    {        
        XTree root = XTree.this;
        private Node node;
        private boolean hasChanged;
        private Node[] nodes;

        private boolean request_to_open;
        
        public DefaultNode(String n) {
            super(n); 
        }
        
        public DefaultNode(Node node) { 
            this(node, null); 
        } 
        
        public DefaultNode(Node node, Node parent) {
            super(node.getCaption(), !node.isLeaf());
            this.node = node;
            this.node.setParent(parent);
            this.node.addListener(this); 
            this.node.setProvider(new DefaultNodeProvider(this));
        } 
        
        public Node getNode() { return node; }
        
        public int size() { return super.getChildCount(); } 
                
        public int getChildCount() {
            if (!node.isLoaded()) {
                synchronized(this) {
                    node.setLoaded(true);
                    hasChanged = true;
                    loadChildren(); 
                }
            }
            return super.getChildCount();
        }
        
        public void loadChildren() {
            loadChildren(false);
        }
        
        void loadChildren(boolean reload) {
            Node pnode = getNode();
            if (pnode != null && pnode.isLeaf()) return;
            
            try { 
                if (nodes == null || reload) {
                    nodes = nodeModel.fetchNodes(pnode); 
                }
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } finally {
                super.removeAllChildren(); 
            }
            
            if (nodes == null) return; 
            
            nodeModel.initChildNodes(nodes); 
            for (Node n: nodes) { 
                if (n == null) continue;

                boolean passed = true;
                Object item = n.getItem();
                if (item instanceof Map) {
                    Map map = (Map)item;
                    String domain = getString(map, "domain"); 
                    String role = getString(map, "role"); 
                    String permission = getString(map, "permission"); 
                    passed = ControlSupport.isPermitted(domain, role, permission); 
                } 
                
                if (passed) this.add(new DefaultNode(n, pnode));
            } 
        } 
        
        private String getString(Map data, String name) {
            Object ov = (data == null? null: data.get(name)); 
            return (ov == null? null: ov.toString()); 
        }
        
        public void reload() {
            if (!node.isLoaded()) return;
            
            synchronized(this) {
                loadChildren();
                root.model.reload(this);
            }
        } 
        
        List<Node> getItems() {
            List<Node> nodes = new ArrayList();
            Enumeration en = super.children();
            while (en.hasMoreElements()) { 
                Object item = en.nextElement(); 
                XTree.DefaultNode dNode = (XTree.DefaultNode) item; 
                nodes.add(dNode.getNode()); 
            } 
            return nodes; 
        } 

        public void insert(MutableTreeNode newChild, int childIndex) {
            super.insert(newChild, childIndex);
        }

        public void remove(int childIndex) {
            super.remove(childIndex);
        }
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultNodeProvider (class) ">
    
    private class DefaultNodeProvider implements Node.Provider 
    {
        XTree root = XTree.this;
        private DefaultNode treeNode;
        private Node userNode;
        
        DefaultNodeProvider(DefaultNode treeNode) {
            this.treeNode = treeNode;
            this.userNode = treeNode.getNode();
            if (this.userNode != null)
                this.userNode.setProvider(this);
        }
        
        public int getIndex() {
            TreeNode parent = treeNode.getParent(); 
            return (parent == null? -1: parent.getIndex(treeNode)); 
        }
        
        public boolean hasItems() {
            Enumeration en = treeNode.children();
            return (en == null? false: en.hasMoreElements()); 
        } 
        
        public void loadItems() { 
            treeNode.loadChildren(); 
            root.model.nodeStructureChanged(treeNode); 
        }         
        
        public void reloadItems() { 
            treeNode.loadChildren(true); 
            root.model.nodeStructureChanged(treeNode); 
        } 
        
        public List<Node> getItems() { 
            return treeNode.getItems(); 
        } 

        public void select() {
            TreeNode[] treeNodes = treeNode.getPath();
            if (treeNodes == null || treeNodes.length == 0) return;
            
            treeNode.loadChildren();
            root.setSelectionPath(new TreePath(treeNodes)); 
        }

        public Object open() {
            select();
            
            if (userNode == null) 
                return null; 
            else if (userNode.isLeaf())
                return nodeModel.openLeaf(userNode);
            else 
                return nodeModel.openFolder(userNode); 
        } 
        
        public void refresh() {
            MapObject mo = new MapObject(treeNode.getNode().getItem()); 
            String caption = mo.getString("caption"); 
            if (caption != null) {
                treeNode.getNode().setCaption(caption);            
                treeNode.setUserObject(caption); 
            } 
            root.model.nodeChanged(treeNode); 
        }
        
        public void reload() {
            treeNode.loadChildren(true); 
            root.model.nodeStructureChanged(treeNode);             
        }
        
        public void remove() {
            TreeNode parent = treeNode.getParent();
            if (parent == null) return; 

            XTree.DefaultNode parentTreeNode = (DefaultNode)parent; 
            TreeNode tn = parentTreeNode.getChildAfter(treeNode); 
            if (tn == null) tn = parentTreeNode.getChildBefore(treeNode); 
            
            parentTreeNode.remove(treeNode); 
            root.model.nodeStructureChanged(parentTreeNode); 
            
            if ( tn instanceof XTree.DefaultNode ) {
                ((XTree.DefaultNode) tn).getNode().select(); 
            } else {
                parentTreeNode.getNode().select(); 
            } 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" NodeTreeRenderer (class) ">
    
    private class NodeTreeRenderer extends DefaultTreeCellRenderer 
    {  
        XTree root = XTree.this;
        private Icon defaultIcon;

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) 
        {
            super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
            super.setText(value+"");
            super.setToolTipText(value+"");
            super.setBorder( BorderFactory.createEmptyBorder(2,2,2,5) );
            if (root.nodeModel == null) return this;
            
            if (defaultIcon == null) 
                defaultIcon = lookupIcon(root.nodeModel.getIcon());
            
            if (defaultIcon != null) setIcon(defaultIcon);
            
            if (value != null && (value instanceof DefaultNode)) {
                Node n = ((DefaultNode)value).getNode();
                if (n != null) {
                    if (n.getIcon() != null) {
                        Icon oIcon = lookupIcon(n.getIcon());
                        if (oIcon != null) super.setIcon(oIcon);
                    }
                    
                    if (n.getTooltip() !=null) 
                        super.setToolTipText(n.getTooltip());
                }
            }
            return this;
        }   
        
        private Icon lookupIcon(String name) {
            try { 
                Icon icon = ControlSupport.getImageIcon(name);
                if (icon == null) icon = UIManager.getIcon(name);
                
                return icon;
            } catch(Throwable t) {
                return null;
            }
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TreeCellRendererImpl (class) ">
    
    private class TreeCellRendererImpl implements TreeCellRenderer 
    {  
        XTree root = XTree.this;
        private Icon defaultIcon;
        private Border defaultBorder;
        private Border selectionBorder;
        
        private JPanel panel;
        private JLabel lblIcon;
        private JLabel lblContent;
        private Icon leafIcon;
        private Icon closedIcon;
        private Icon openIcon;
        private Color selectionForeground;
        private Color textForeground;
        private Color selectionBackground;
        private Color textBackground;
        private Color selectionBorderColor; 
        
        private boolean _inited;
        
        TreeCellRendererImpl() {      
            defaultBorder = BorderFactory.createEmptyBorder(2,2,2,5);
            
            lblContent = new JLabel("Content"); 
            lblIcon = new JLabel();
            lblIcon.setPreferredSize(new Dimension(16,16));
            lblIcon.setMinimumSize(new Dimension(16,16));
            lblIcon.setMaximumSize(new Dimension(16,16));
            
            JLabel separator = new JLabel();
            separator.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
            
            JPanel box = new JPanel(new BorderLayout());
            box.add(lblIcon, BorderLayout.WEST); 
            box.add(separator, BorderLayout.EAST);
            box.setOpaque(false);
            
            panel = new JPanel(new BorderLayout());
            panel.add(box, BorderLayout.WEST);
            panel.add(lblContent, BorderLayout.EAST); 
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createEmptyBorder(1,1,1,1)); 
            
            leafIcon = UIManager.getIcon("Tree.leafIcon");
            closedIcon = UIManager.getIcon("Tree.closedIcon");
            openIcon = UIManager.getIcon("Tree.openIcon");
            selectionForeground = UIManager.getColor("Tree.selectionForeground");
            textForeground = UIManager.getColor("Tree.textForeground");
            selectionBackground = UIManager.getColor("Tree.selectionBackground");
            textBackground = UIManager.getColor("Tree.textBackground");
            selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
        }
        
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if (!_inited) {
                _inited = true;                
                lblContent.setFont(tree.getFont());   
                panel.setFont(lblContent.getFont());
                tree.setRowHeight(panel.getPreferredSize().height);
                
                Border bin = BorderFactory.createEmptyBorder(1,1,1,4);
                Border bout = BorderFactory.createLineBorder(selectionBorderColor,1);
                selectionBorder = BorderFactory.createCompoundBorder(bout, bin);  
                if (root.nodeModel != null) 
                    defaultIcon = lookupIcon(root.nodeModel.getIcon());
            }
            
            lblContent.setText((value == null? "": value.toString()));
            if (root.nodeModel == null) return panel;
            
            if (selected) {
                lblContent.setForeground(selectionForeground);
                lblContent.setBackground(selectionBackground);
                lblContent.setOpaque(true);                 
            } else {
                lblContent.setForeground(textForeground);
                lblContent.setBackground(textBackground);
                lblContent.setOpaque(false); 
            }

            if (hasFocus) {
                lblContent.setBorder(selectionBorder);
            } else {
                lblContent.setBorder(defaultBorder); 
            }
            
            lblContent.setEnabled(tree.isEnabled());
            lblIcon.setEnabled(tree.isEnabled());
            if (leaf) lblIcon.setIcon(leafIcon);
            else if (expanded) lblIcon.setIcon(openIcon);
            else lblIcon.setIcon(closedIcon);
            
            if (defaultIcon != null) lblIcon.setIcon(defaultIcon);
            
            if (value != null && (value instanceof DefaultNode)) {
                Node n = ((DefaultNode) value).getNode();
                if (n != null) {
                    if (n.getIcon() != null) {
                        Icon oIcon = (Icon) n.getProperties().get(Icon.class);
                        if (oIcon == null) { 
                            oIcon = lookupIcon(n.getIcon());
                            n.getProperties().put(Icon.class, oIcon); 
                        }
                        if (oIcon != null) lblIcon.setIcon(oIcon);
                    } 
                    
                    if (n.getTooltip() !=null) 
                        lblContent.setToolTipText(n.getTooltip());
                } 
            } 
            return panel;
        }

        private Icon lookupIcon(String name) {
            try { 
                Icon icon = ControlSupport.getImageIcon(name);
                if (icon == null) icon = UIManager.getIcon(name);
                
                return icon;
            } catch(Throwable t) {
                return null;
            }
        }        
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DummyTreeNodeModel (class) "> 
    
    private class DummyTreeNodeModel extends TreeNodeModel 
    {
        public Node[] fetchNodes(Node node) { return null; }

        public Node getRootNode() { 
            return new Node("root", "Default");
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultProvider (class) "> 
    
    private class DefaultProvider implements TreeNodeModel.Provider 
    {
        XTree root = XTree.this; 
        
        public Object getBinding() { 
            return root.getBinding(); 
        } 
        
        public Node getSelectedNode() {
            DefaultNode defNode = root.getSelectedNode(); 
            return (defNode == null? null: defNode.getNode()); 
        }
        
        public Node getRoot() {
            XTree.DefaultNode rdn = (DefaultNode) root.root; 
            return (rdn == null? null: rdn.getNode()); 
        }
        
        public Node findNode(NodeFilter filter) 
        {
            DefaultNode parent = (DefaultNode) root.model.getRoot();
            Node n = parent.getNode();
            if (filter.accept(n)) return n;

            return root.doFindNode(parent, filter);
        }

        public List<Node> findNodes(NodeFilter filter) 
        {
            List<Node> nodes = new ArrayList();
            DefaultNode parent = (DefaultNode) root.model.getRoot();

            Node n = parent.getNode();
            if (filter.accept(n)) nodes.add(n);

            root.doCollectNodeList(parent, filter, nodes);      
            return nodes;
        }      
        
        public List<Node> children() {
            DefaultNode defNode = root.getSelectedNode(); 
            if (defNode == null) return null; 

            List<Node> list = new ArrayList();            
            Enumeration en = defNode.children(); 
            while (en.hasMoreElements()) {
                DefaultNode dn = (DefaultNode) en.nextElement(); 
                list.add(dn.getNode()); 
            }
            return list; 
        }
        
        public void refresh() {
            XTree.DefaultNode selNode = root.getSelectedNode(); 
            if (selNode == null) { 
                XTree.DefaultNode rootNode = (DefaultNode) root.root; 
                rootNode.loadChildren(true); 
            } else {
                root.model.nodeChanged(selNode);
            }
        }
        
        public void reloadTree() {
            XTree.DefaultNode rootNode = (DefaultNode) root.root;
            rootNode.loadChildren(true);
            root.model.nodeStructureChanged(rootNode); 
        }
        public void reloadSelectedNode() {
            DefaultMutableTreeNode mtn = root.getSelectedNode(); 
            if ( mtn == null ) mtn = root.root; 
            if ( mtn instanceof DefaultNode ) {
                DefaultNode defNode = (DefaultNode) mtn; 
                defNode.loadChildren( true ); 
                root.model.nodeStructureChanged(defNode); 
            }
        }        
    }
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" TreeEventSupport (class) "> 
   
    private class TreeEventSupport implements TreeSelectionListener 
    {
        XTree root = XTree.this; 
        
        private Node oldNode;
        private List<TreeSelectionListener> selectionHandlers = new ArrayList(); 
        
        void add(TreeSelectionListener handler) 
        {
            if (handler != null && !selectionHandlers.contains(handler)) 
                selectionHandlers.add(handler); 
        }
        
        void remove(TreeSelectionListener handler) 
        {
            if (handler != null) selectionHandlers.remove(handler); 
        }
        
        public void valueChanged(final TreeSelectionEvent evt) 
        {
            try {
                if (root.getName() != null) {
                    boolean nodeHasChanged = false;
                    XTree.DefaultNode selNode = getSelectedNode(); 
                    Node node = (selNode == null? null: selNode.getNode()); 
                    if (oldNode != null && node != null && oldNode.equals(node)) 
                        nodeHasChanged = false; 
                    else 
                        nodeHasChanged = true;
                    
                    if (selNode != null && !selNode.hasChanged) 
                        selNode.hasChanged = nodeHasChanged;
                    
                    UIControlUtil.setBeanValue(root.getBinding(), root.getName(), node); 
                    oldNode = node;                    
                    EventQueue.invokeLater(new Runnable(){
                        public void run() {
                            fireChangeNode(evt); 
                        }    
                    });
                }
            }
            catch(Exception ex) { 
                MsgBox.err(ex);  
            }            
        } 
        
        private void fireChangeNode(TreeSelectionEvent evt) {
            //notify dependencies that the node has changed
            root.getBinding().notifyDepends(root, root.getName(), false); 
            //fire onChangeNode on the TreeNodeModel
            //Node node = root.nodeModel.getSelectedNode(); 
            //Object result = root.nodeModel.onChangeNode(node); 
            //if (result != null) root.getBinding().fireNavigation(result, null, false); 
            
            for (TreeSelectionListener handler : selectionHandlers) {
                handler.valueChanged(evt); 
            }
        } 
    }
    
    // </editor-fold>     
    
}
