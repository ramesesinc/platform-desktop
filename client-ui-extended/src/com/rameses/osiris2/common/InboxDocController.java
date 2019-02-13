/*
 * InboxDocController.java
 *
 * Created on August 23, 2013, 1:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.ExpressionResolver;
import com.rameses.common.PropertyResolver;
import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.osiris2.client.WorkUnitUIController;
import com.rameses.rcp.annotations.Controller;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.TreeNodeModel;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.util.ControlSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wflores
 */
public class InboxDocController 
{    
    @com.rameses.rcp.annotations.Invoker
    protected com.rameses.osiris2.Invoker invoker;
    
    @Controller
    protected WorkUnitUIController controller;
    
    private String context; 
    private String defaultFileType;
    private String serviceName;
    
    private TreeNodeModel nodeModel; 
    private Node selectedNode;   
    private ExplorerViewService service;
    
    private List<Action> formActions; 
    private ActionsProvider actionsProvider;
    private Helper helper; 
    private Opener opener;
    
    public InboxDocController() {
        actionsProvider = new ActionsProvider(); 
        helper = new Helper(); 
        init(); 
    }

    public void init() {        
        opener = new Opener();
        opener.setOutcome("blankpage"); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Settters ">
    
    public String getTitle() { 
        return (invoker == null? "": invoker.getCaption()); 
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
        
    public List getFormActions() { 
        if (formActions == null) {
            formActions = new ArrayList(); 
            formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true)); 
            
            String filetype = getDefaultFileType();
            if (filetype != null && filetype.length() > 0) {
                List<Invoker> invokers = actionsProvider.getInvokers(null, filetype.toLowerCase()+":formActions");
                for (Invoker invoker: invokers) { 
                    formActions.add(new ActionInvoker(invoker)); 
                } 
            }        
        }
        return formActions; 
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
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new IllegalStateException("Please specify a serviceName"); 
            
        if (service == null) {
            service = (ExplorerViewService) InvokerProxy.getInstance().create(name, ExplorerViewService.class);
        }
        return service;
    }     
        
    public boolean isRootVisible() { return false; } 
    public String getIcon() { return null; }
    
    public Object getOpener() { return opener; } 
      
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc=" TreeNodeModel helper and utilities ">
    
    private Node _selectedOpenNode;
    
    public final Node getSelectedOpenNode() { return _selectedOpenNode; } 
    
    private class TreeNodeModelImpl extends TreeNodeModel 
    {
        InboxDocController root = InboxDocController.this;
        
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
            
            return nodes; 
        }
        
        public void initChildNodes(Node[] nodes) {
            if (nodes == null) return;
            
            for (Node node: nodes) {
                Object o = node.getItem();
                String sfolder = helper.getString(o, "folder");
                if (sfolder == null || sfolder.length() == 0) node.setLeaf(true); 
                
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

            System.out.println("openNode-> " + node.getItem());  
            _selectedOpenNode = node; 
            updateView();
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
            
            if (wuprops == null) { 
                if (controller == null) 
                    return new HashMap();
                else 
                    wuprops = new HashMap();     
            }
        } 
        return wuprops;
    }
    
    private void showOpener(Opener opener) {
        if (opener == null) return;
        
        Binding obinding = (Binding) getNodeModel().getBinding(); 
        if (obinding == null) return;
        
        this.opener = opener;
        obinding.refresh("selectedOpenNode|formActions|opener");
    }
        
    private void updateView() { 
        buildActions(); 

        Node selNode = getSelectedOpenNode(); 
        String filetype = helper.getFiletype(selNode); 
        if (filetype == null || filetype.length() == 0) 
            filetype = getDefaultFileType();

        Invoker invoker = null;
        if (filetype != null && filetype.length() > 0) {
            String invtype = filetype.toLowerCase() + ":open"; 
            invoker = actionsProvider.getInvoker(selNode, invtype);
        }

        Opener opener = null;
        if (invoker != null) {
            Map params = new HashMap();
            params.put("inboxHandler", new InboxDocHandler());
            params.put("node", selNode.getItem()); 
            params.put("entity", selNode.getItem()); 
            opener = actionsProvider.toOpener(invoker, params, selNode); 
        } else {
            opener = new Opener();
            opener.setOutcome("blankpage");
        }
        showOpener(opener);
    } 
        
    private void buildActions() {
        formActions.clear();
        formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true)); 
        
        Node node = getSelectedNode();
        if (node == null) {
            String filetype = getDefaultFileType();
            if (filetype != null && filetype.length() > 0) {
                String invtype = filetype.toLowerCase()+":formActions";
                List<Invoker> invokers = actionsProvider.getInvokers(null, invtype);
                for (Invoker invoker: invokers) { 
                    formActions.add(new ActionInvoker(invoker)); 
                } 
            }
            ///exit
            return; 
        }
        
        //load extended actions for the node
        List<Invoker> list = node.getPropertyList("Invoker.formActions"); 
        if (list == null) {
            List<String> filetypes = new ArrayList(); 
            String sval = getDefaultFileType();
            if (sval != null && sval.length() > 0) {
                filetypes.add(sval.toLowerCase());
            }                 
            sval = helper.getFiletype(node); 
            if (sval != null && sval.length() > 0) {
                filetypes.remove(sval.toLowerCase());
                filetypes.add(sval.toLowerCase());
                
                sval = sval + ":" + node.getPropertyString("name"); 
                filetypes.remove(sval.toLowerCase());
                filetypes.add(sval.toLowerCase());
            } 
            
            list = new ArrayList(); 
            for (String filetype: filetypes) {
                String invtype = filetype + ":formActions";
                list.addAll(actionsProvider.getInvokers(node, invtype)); 
            }                            
            node.setProperty("Invoker.formActions", list);            
        } 
        for (Invoker invoker: list) { 
            formActions.add(new ActionInvoker(invoker)); 
        } 
    }     
    
    protected Action createAction(String name, String caption, String icon, String shortcut, char mnemonic, String visibleWhen, boolean immediate) 
    {
        Action a = new Action(name, caption, icon, mnemonic);
        if (visibleWhen != null) a.setVisibleWhen(visibleWhen); 
        if (shortcut != null) a.getProperties().put("shortcut", shortcut);    
        
        a.setImmediate( immediate );
        a.setShowCaption(true); 
        return a;
    }     
    
    public void reload() {
        Node selNode = getSelectedOpenNode(); 
        if (selNode == null) {
            getNodeModel().reloadTree(); 
        } else {
            selNode.refresh();
            selNode.open(); 
        }
    }
        
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" ActionsProvider (class) "> 
    
    private class ActionsProvider 
    {
        private ExpressionResolver resolver = ExpressionResolver.getInstance(); 

        List<Invoker> getInvokers(Node node, String invokerType) {
            try { 
                return InvokerUtil.lookup(invokerType);
            } catch(Throwable t) {
                System.out.println("[WARN] error lookup '"+invokerType+"' caused by " + t.getMessage());
                return new ArrayList(); 
            }   
        }
        
        Invoker getInvoker(Node node, String invokerType) {
            List<Invoker> list = getInvokers(node, invokerType);
            return (list.isEmpty()? null: list.get(0)); 
        }           
                
        Opener toOpener(Invoker invoker, Map params, Node node) {
            Object nodeTitle = invoker.getProperties().get("formTitle");
            if (nodeTitle != null) params.put("nodeTitle", nodeTitle); 
            
            Opener a = InvokerUtil.createOpener(invoker, params); 
            String target = a.getTarget()+"";
            if (!target.matches("window|popup|process|_window|_popup|_process")) {
                a.setTarget("popup"); 
            } 
            return a;
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionInvoker (class) "> 
    
    private class ActionInvoker extends Action 
    {
        InboxDocController root = InboxDocController.this;
        private Invoker invoker;
        
        ActionInvoker(Invoker invoker) {
            this.invoker = invoker; 
            
            this.setName(invoker.getAction()); 
            this.setCaption(invoker.getCaption()); 
            this.setIcon(getString("icon"));
            this.setVisibleWhen(getString("visibleWhen"));            
            this.setMnemonic(getChar("mnemonic"));
            this.setTooltip(getString("tooltip"));
            
            Boolean bool = getBoolean("immediate"); 
            if (bool != null) this.setImmediate(bool.booleanValue());
            
            bool = getBoolean("showCaption"); 
            if (bool != null) this.setShowCaption(bool.booleanValue());
            
            this.getProperties().putAll(invoker.getProperties()); 
        }

        public Object execute() { 
            Node node = root.getSelectedNode(); 
            Map map = new HashMap(); 
            map.put("inboxHandler", new InboxDocHandler());             
            map.put("node", (node == null? null: node.getItem())); 
            Opener opener = actionsProvider.toOpener(invoker, map, node); 
            String target = invoker.getProperties().get("target")+"";
            if (!target.matches("window|popup|process|_window|_popup|_process")) {
                root.showOpener(opener); 
                return null; 
            } else { 
                return opener; 
            } 
        } 
        
        private String getString(String name) {
            if (invoker == null || invoker.getProperties() == null) return null;
            
            Object ov = invoker.getProperties().get(name); 
            return (ov == null? null: ov.toString()); 
        }
        
        private char getChar(String name) {
            String sv = getString(name);
            if (sv == null || sv.trim().length() == 0) return '\u0000';
            
            return sv.trim().charAt(0); 
        } 
        
        private Boolean getBoolean(String name) {
            String sv = getString(name);
            if (sv == null || sv.length() == 0) return null;
            
            return "true".equalsIgnoreCase(sv); 
        }         
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Helper (class) "> 
    
    private class Helper {
        
        private String getString(Object bean, String name) {
            if (bean instanceof Map) 
                return getString((Map)bean, name); 
            else 
                return null; 
        }
        
        private String getString(Map data, String name) {
            Object o = (data == null? null: data.get(name));
            return (o == null? null: o.toString()); 
        }        
        
        private String getFiletype(Map data) {
            if (data == null) return null;
            
            String filetype = getString(data, "filetype"); 
            if (filetype == null || filetype.length() == 0) 
                filetype = getString(data, "_filetype"); 
            if (filetype == null || filetype.length() == 0) 
                return null; 
            else 
                return filetype.toLowerCase(); 
        }
        
        private String getFiletype(Node node) {
            if (node == null) return null;
            
            String filetype = node.getPropertyString("filetype");
            if (filetype == null || filetype.length() == 0) 
                filetype = node.getPropertyString("_filetype");
            if (filetype == null || filetype.length() == 0) 
                return null;
            else 
                return filetype.toLowerCase(); 
        }   
        
        private String getFiletype(Node node, String defaultFiletype) {
            if (node == null) return null;
            
            String filetype = node.getPropertyString("filetype");
            if (filetype == null || filetype.length() == 0) 
                filetype = node.getPropertyString("_filetype");
            if (filetype == null || filetype.length() == 0) 
                filetype = defaultFiletype; 
            if (filetype == null || filetype.length() == 0) 
                return null; 
            else 
                return filetype + ":" + node.getPropertyString("name"); 
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" InboxDocHandler "> 
    
    public class InboxDocHandler 
    {
        InboxDocController root = InboxDocController.this; 
        
        public void refresh() {
            Node selNode = root.getSelectedOpenNode(); 
            if (selNode != null) selNode.refresh(); 
        }
        
        public void reload() {
            Node selNode = root.getSelectedOpenNode(); 
            if (selNode != null) selNode.reload();
        }
        
        public void reloadTreeModel() {
            root.getNodeModel().reloadTree();
        } 
        
        public void update(Object data) {
            Node selNode = root.getSelectedOpenNode(); 
            if (selNode == null) return;
            
            selNode.setItem(data); 
            selNode.refresh(); 
        }
        
        public void remove() {
            Node selNode = root.getSelectedOpenNode(); 
            if (selNode == null) return;
            
            selNode.remove();
            selNode = root.getNodeModel().getSelectedNode(); 
            if (selNode == null) clearView(); 
        } 
        
        public void clearView() {
            Opener opener = new Opener();
            opener.setOutcome("blankpage"); 
            root.showOpener(opener);             
        }
    }
    
    // </editor-fold>
}
