package com.rameses.rcp.common;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTreeNodeModel {

    private static final long serialVersionUID = 1L;
    private TreeNodeModel.Provider provider;
    private Node selectedNode;

    public abstract Node[] fetchNodes(Node node);

    public void setProvider(TreeNodeModel.Provider provider) {
        this.provider = provider;
    }

    public String getIcon() {
        return null;
    }

    public boolean isRootVisible() {
        return true;
    }

    public boolean isAllowOpenOnSingleClick() {
        return true;
    }

    public boolean isAutoSelect() {
        return false;
    }

    public Node getRootNode() {
        return new Node("root", "All");
    }

    public Node getSelectedNode() {
        return (provider == null ? null : provider.getSelectedNode());
    }

    public Object openLeaf(Node node) {
        return null;
    }

    public Object openFolder(Node node) {
        return null;
    }

    public Object openSelected() {
        if (selectedNode == null) {
            return null;
        } else if (selectedNode.isLeaf()) {
            return openLeaf(selectedNode);
        } else {
            return openFolder(selectedNode);
        }
    }

    public final Node findNode(NodeFilter filter) {
        return (provider == null ? null : provider.findNode(filter));
    }

    public final List<Node> findNodes(NodeFilter filter) {
        return (provider == null ? new ArrayList() : provider.findNodes(filter));
    }

    public final List<Node> children() {
        return (provider == null ? new ArrayList() : provider.children());
    }

    public final Object getBinding() {
        return (provider == null ? null : provider.getBinding());
    }

    public final void reloadTree() {
        if (provider != null) {
            provider.reloadTree();
        }
    }

    public final void reloadSelectedNode() {
        if (provider != null) {
            provider.reloadSelectedNode();
        }
    }

    public final Node getRoot() {
        return (provider == null ? null : provider.getRoot());
    }

    public static interface Provider {

        Object getBinding();

        Node getSelectedNode();

        Node getRoot();

        Node findNode(NodeFilter filter);

        List<Node> findNodes(NodeFilter filter);

        List<Node> children();

        void refresh();

        void reloadTree();

        void reloadSelectedNode();
    }
}
