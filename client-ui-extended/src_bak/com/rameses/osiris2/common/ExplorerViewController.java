/*
 * ExplorerViewController.java
 *
 * Created on July 30, 2013, 6:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.PropertyResolver;
import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.annotations.Binding;
import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.annotations.Invoker;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TreeNodeModel;
import com.rameses.rcp.util.ControlSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class ExplorerViewController 
{
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    protected com.rameses.rcp.framework.Binding binding;
    
    @Controller(onready="setupWorkunitProperties")
    protected Object controller;

    private String context; 
    private String defaultFileType;
    private String serviceName;
    
    private TreeNodeModel nodeModel; 
    private Node selectedNode;   
    private ExplorerViewService service;
    private Object serviceObject;
        
    public ExplorerViewController() {
    } 
    
    public void init(){
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Settters ">
    
    public String getTitle() { 
        return (invoker == null? null: invoker.getCaption()); 
    } 
    
    public String getContext() { 
        return wucontext; 
    }
    
    public String getDefaultFileType() { 
        return wudefaultFileType;
    } 
    
    public String getServiceName() { 
        return wuserviceName; 
    }
        
    public Node getSelectedNode() { return selectedNode; } 
    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode; 
    }
    
    public TreeNodeModel getNodeModel() { 
        if (nodeModel == null) {
            nodeModel = new TreeNodeModelImpl(); 
        }
        return nodeModel;
    }
        
    public ExplorerViewService getService() {
        if (service == null) { 
            String name = getServiceName();
            if (name == null || name.trim().length() == 0) { 
                throw new IllegalStateException("Please specify a serviceName"); 
            } 
            service = (ExplorerViewService) InvokerProxy.getInstance().create(name, ExplorerViewService.class); 
        } 
        return service; 
    } 
    
    public String getIcon() { 
        return null; 
    } 
    public boolean isRootVisible() { 
        return wurootVisible; 
    } 
    public boolean isAllowSearch() { 
        return wuallowSearch; 
    }
    public boolean isAutoSelect() {
        return wuautoSelect;
    }
    public boolean isAllowCreate() {
        return wuallowCreate; 
    }
    public boolean isAllowOpen() {
        return wuallowOpen;
    }
    
    protected void beforeNodes(Map params) {
        //this is invoke before invoking service.getNodes() 
    } 
    
    protected List<Map> getNodes(Map params) {
        beforeNodes(params); 
        return getService().getNodes(params); 
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" TreeNodeModel helper and utilities ">
      
    private class TreeNodeModelImpl extends TreeNodeModel 
    {
        ExplorerViewController root = ExplorerViewController.this;
        
        public boolean isRootVisible() { 
            return root.isRootVisible(); 
        } 
        
        public boolean isAutoSelect() {
            return root.isAutoSelect(); 
        }
        
        public String getIcon() { 
            String icon = root.getIcon();
            if (icon == null || icon.length() == 0) 
                return "Tree.closedIcon"; 
            else 
                return icon; 
        }
        
        public List<Map> getNodeList(Node node) {
            Map params = new HashMap(); 
            Object item = node.getItem(); 
            if (item instanceof Map) 
                params.putAll((Map) item); 
            else 
                params.put("item", node.getItem()); 
            
            params.put("root", (node.getParent() == null));
            params.put("caption", node.getCaption()); 
            List<Map> nodes = root.getNodes(params); 
            if (nodes == null) nodes = new ArrayList();
            
            if (node.getParent() == null && root.isAllowSearch()) {
                Map search = new HashMap();
                search.put("folder", false);                 
                search.put("name", "search");
                search.put("caption", "Search"); 
                search.put("icon", "images/doc-view16.png");
                if (nodes.isEmpty()) 
                    nodes.add(search); 
                else 
                    nodes.add(0, search); 
            }
            return nodes; 
        }
        
        public void initChildNodes(Node[] nodes) {
            if (nodes == null) return;
            
            for (Node node: nodes) {
                if (node.getIcon() != null) continue;
                
                String icon = node.getPropertyString("filetype");
                if (icon == null) icon = "default_folder"; 
                
                String path = "images/explorer/"+root.getContext();
                node.getProperties().put("iconpath", path); 
                
                String res = path+"/"+icon.toLowerCase()+".png";
                if (ControlSupport.isResourceExist(res)) { 
                    node.setIcon(res);  
                } else {
                    node.setIcon(getIcon());
                }
            }
        }
        
        public Object openFolder(Node node) {
            return openNodeImpl(node);
        }

        public Object openLeaf(Node node) {
            return openNodeImpl(node);
        }
        
        private Object openNodeImpl(Node node) {
            if (node == null) return null;
            
            String filetype = node.getPropertyString("filetype");
            if (filetype == null) {
                String childtypes = node.getPropertyString("childtypes"); 
                String[] values = (childtypes == null? null: childtypes.split(",")); 
                if (values != null && values.length > 0) filetype = values[0]; 
            }
            if (filetype == null) filetype = getDefaultFileType(); 
            
            _queryFormName = "queryform";
            
            String nodeName = (node == null? null: node.getPropertyString("name")); 
            if ("search".equals(nodeName+"")) {
                _showQueryForm = true; 
            } else { 
                String sallowSearch = (node == null? null: node.getPropertyString("allowSearch"));
                String sfiletype = (node == null? null: node.getPropertyString("filetype"));
                if (sfiletype != null) sfiletype = sfiletype.toLowerCase();
                
                _showQueryForm = "true".equals(sallowSearch); 
                if (_showQueryForm && containsPage(sfiletype+":queryform")) {
                    _queryFormName = sfiletype + ":queryform";
                } 
            } 
            getListHandler().setNode(node); 
            getListHandler().updateView(); 
            Object ob = this.getBinding();
            if (ob instanceof com.rameses.rcp.framework.Binding) {
                com.rameses.rcp.framework.Binding ab = (com.rameses.rcp.framework.Binding)ob;
                ab.refresh("nodechange"); 
                ab.refresh("listHandler.*");
            }
            return null; 
        }
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" workunit/controller support ">
    
    private Map wuprops;
    protected final Map getWorkunitProperties() {
        if (wuprops == null) { 
            try {
                PropertyResolver resolver = PropertyResolver.getInstance();
                wuprops = (Map) resolver.getProperty(controller, "workunit.workunit.properties"); 
            } catch(Throwable t) {;}
            
            if (wuprops == null) wuprops = new HashMap(); 
        } 
        return wuprops;
    }
    
    public final List<Action> lookupActions(String type) { 
        List<Action> actions = new ArrayList(); 
        try { 
            actions = InvokerUtil.lookupActions(type, new InvokerFilter() {
                public boolean accept(com.rameses.osiris2.Invoker o) { 
                    return o.getWorkunitid().equals(invoker.getWorkunitid()); 
                }
            }); 
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup actions caused by " + t.getMessage());
        } 
        
        for (int i=0; i<actions.size(); i++) {
            Action newAction = actions.get(i).clone();
            actions.set(i, newAction);
        } 
        return actions; 
    }     
    
    private boolean containsPage(String name) {
        try {
            PropertyResolver resolver = PropertyResolver.getInstance();
            Map pages = (Map) resolver.getProperty(controller, "workunit.workunit.pages"); 
            return pages.containsKey(name); 
        } catch(Throwable t) {
            return false; 
        }
    }

    private String wucontext;
    private String wudefaultFileType;
    private String wuserviceName; 
    private boolean wuallowSearch;
    private boolean wurootVisible;
    private boolean wuautoSelect;
    private boolean wuallowCreate;
    private boolean wuallowOpen;
    
    public void setupWorkunitProperties() {
        Map map = getWorkunitProperties(); 
        wucontext = getString(map, "context");
        if (wucontext == null) wucontext = "explorer";
        
        wudefaultFileType = getString(map, "defaultFileType"); 
        wuserviceName = getString(map, "serviceName"); 
        
        String sval = getString(map, "allowSearch");
        wuallowSearch = ("false".equals(sval)? false: true);
        wurootVisible = "true".equals(getString(map, "rootVisible")); 
        wuautoSelect = ("false".equals(getString(map, "autoSelect"))? false: true); 
        wuallowCreate = ("false".equals(getString(map, "allowCreate"))? false: true);
        wuallowOpen = ("false".equals(getString(map, "allowOpen"))? false: true);
    } 
    
    private String getString(Map map, String name) { 
        if (map == null || name == null) return null;
        
        Object ov = (map == null? null: map.get(name)); 
        return (ov == null? null: ov.toString()); 
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" list view support ">
    
    private boolean _showQueryForm;
    private String _queryFormName;
    private Opener _queryform; 
    private ExplorerViewListController listHandler; 
    
    public com.rameses.rcp.framework.Binding getBinding() {
        Object ob = (nodeModel == null? null: nodeModel.getBinding());
        if (ob instanceof com.rameses.rcp.framework.Binding) {
            return (com.rameses.rcp.framework.Binding) ob;
        } else {
            return null; 
        }
    }
    
    public ExplorerViewListController getListHandler() {
        if (listHandler == null) {
            listHandler = new ExplorerViewListController();
            listHandler.setParent(this);
        } 
        return listHandler; 
    }
    
    public boolean isQueryFormVisible() { 
        return _showQueryForm; 
    }
    
    public Opener getQueryForm() {
        if (!isAllowSearch()) {
            Object o = getNode();
            if (o instanceof Map) {
                Object value = ((Map) o).get("allowSearch"); 
                if (!"true".equals(value+"")) return null; 
            }
        }
        
        if (isQueryFormVisible()) {
            Opener o = new Opener();
            o.setOutcome(_queryFormName); 
            return o;
        } else { 
            return null; 
        } 
    }
    
    public Map getQuery() { 
        return getListHandler().getQuery(); 
    }
    
    public void search() { 
        getListHandler().search(); 
    } 
    
    public Object getNode() {
        Node node = getListHandler().getNode(); 
        return (node == null? null: node.getItem()); 
    }
    
    public Object getSelectedEntity() {
        return (listHandler==null? null: listHandler.getSelectedEntity()); 
    }
    
    public void beforeFetchList(Map params) {
    }
    
    // </editor-fold>

}
