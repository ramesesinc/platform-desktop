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
public class ExplorerViewController_old {
    
    @Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Binding
    protected com.rameses.rcp.framework.Binding binding;
    
    @Controller 
    protected Object controller;

    private String context; 
    private String defaultFileType;
    private String serviceName;
    
    private TreeNodeModel nodeModel; 
    private Node selectedNode;   
    private ExplorerViewService service;
    private Opener openerObject; 
    private Opener defaultViewOpener; 
    
    public ExplorerViewController_old() {
        try {
            openerObject = InvokerUtil.lookupOpener("explorer-view-intro", new HashMap()); 
        } catch(Throwable t){;} 
    } 
    
    public void init(){
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Settters ">
    
    public String getTitle() { 
        return (invoker == null? null: invoker.getCaption()); 
    } 
    
    public String getContext() { 
        if (context == null) {
            Object o = getWorkunitProperties().get("context");
            context = (o == null? "explorer": o.toString()); 
        } 
        return context; 
    }
    
    public String getDefaultFileType() { 
        if (defaultFileType == null) {
            Object o = getWorkunitProperties().get("defaultFileType");
            defaultFileType = (o == null? null: o.toString()); 
        } 
        return defaultFileType;     
    } 
    
    public String getServiceName() { 
        if (serviceName == null) {
            Object o = getWorkunitProperties().get("serviceName");
            serviceName = (o == null? null: o.toString()); 
        }
        return serviceName; 
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
    
    public ExplorerViewService getService() 
    {
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new IllegalStateException("Please specify a serviceName"); 
            
        if (service == null) {
            service = (ExplorerViewService) InvokerProxy.getInstance().create(name, ExplorerViewService.class);
        }
        return service;
    }     
    
    public Opener getOpenerObject() { return openerObject; } 
    
    public boolean isRootVisible() { return false; } 
    public boolean isAllowSearch() { return true; }  
    public String getIcon() { return null; }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" TreeNodeModel helper and utilities ">
      
    private class TreeNodeModelImpl extends TreeNodeModel 
    {
        ExplorerViewController_old root = ExplorerViewController_old.this;
        
        public boolean isRootVisible() { 
            return root.isRootVisible(); 
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
            List<Map> nodes = getService().getNodes(params); 
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
            
//            String filetype = node.getPropertyString("filetype");
//            if (filetype == null) {
//                String childtypes = node.getPropertyString("childtypes"); 
//                String[] values = (childtypes == null? null: childtypes.split(",")); 
//                if (values != null && values.length > 0) filetype = values[0]; 
//            }
//            if (filetype == null) filetype = getDefaultFileType(); 
            
            if (defaultViewOpener == null) {
                String name = "explorer-view-handler";   
                defaultViewOpener = InvokerUtil.lookupOpener(name, new HashMap()); 
            }
            
            //System.out.println("openNode-> " + node.getItem());            
            ExplorerViewHandler handler = (ExplorerViewHandler) defaultViewOpener.getHandle(); 
            handler.setParent(root); 
            handler.setNode(node); 
            handler.updateView();
            openerObject = defaultViewOpener; 
            if (binding != null) binding.refresh("subform"); 

            return null; 
        }
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" override / helper methods ">
    
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
    
    // </editor-fold>

}
