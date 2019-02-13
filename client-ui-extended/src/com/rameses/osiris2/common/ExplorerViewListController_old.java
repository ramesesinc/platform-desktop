/*
 * ExplorerViewListController.java
 *
 * Created on July 31, 2013, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2.common;

import com.rameses.common.ExpressionResolver;
import com.rameses.osiris2.Invoker;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.IconColumnHandler;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PopupMenuOpener;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.ImageIconSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author wflores
 */
public class ExplorerViewListController_old extends ListController implements ExplorerViewHandler {
    
    private Node node; 
    private ExplorerViewService service;
    private List<Invoker> defaultInvokers; 
    private List<Action> formActions = new ArrayList(); 
    private List<Action> nodeActions = new ArrayList();
    private Object extendedParams;
    
    private boolean _showQueryForm = false;
    
    public ExplorerViewListController_old() {
    }
        
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public String getTitle() {
        Node node = getNode();
        return (node == null? null: node.getCaption()); 
    }
    
    public String getServiceName() {
        throw new IllegalStateException("Please specify a serviceName");
    }
    
    public String getEntityName() {
        throw new IllegalStateException("Please specify an entityName");
    }
        
    public List getFormActions() { return formActions; }  
    public List getNodeActions() { return nodeActions; } 
    public int getRows() { return 20; } 
    
    public boolean isQueryFormVisible() { 
        return _showQueryForm; 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ExplorerViewHandler implementation ">

    private DefaultListService defaultService = new DefaultListService();    
    private ActionsProvider actionsProvider = new ActionsProvider();     
    private ExplorerViewController_old parentController;
    
    public void setParent(ExplorerViewController_old parentController) {
        this.parentController = parentController; 
    }
        
    public Node getNode() { return node; } 
    public void setNode(Node node) { this.node = node; }
        
    protected ListService getService() { 
        if (parentController == null) return defaultService;

        return parentController.getService(); 
    }
        
    public void updateView() { 
        Node node = getNode();
        String nodeName = (node == null? null: node.getPropertyString("name")); 
        if ("search".equals(nodeName+"")) {
            _showQueryForm = true; 
            
        } else {
            String _allowSearch = (node == null? null: node.getPropertyString("allowSearch"));
            _showQueryForm = "true".equals(_allowSearch); 
        } 
                
        getQuery().clear(); 
        
        buildActions(); 
        Binding binding = getBinding();
        if (binding != null) {
            binding.refresh("formActions");
        } 
        reloadAll(); 
    }
    
    private Node getChildNode(String filetype) {
        Node node = getNode();
        if (node == null || filetype == null) return null;
        
        List<Node> cnodes = node.getItems();
        for (Node cnode: cnodes) {
            String cfiletype = cnode.getPropertyString("filetype")+"";
            if (cfiletype.equalsIgnoreCase(filetype)) return cnode;
        } 
        return null; 
    }
    
    private void buildActions() {
        formActions.clear();
        nodeActions.clear();
        
        Node node = getNode();
        if (node == null) return;
                
        String filetype = node.getPropertyString("filetype");
        //load node actions
        if (filetype != null) {
            Invoker invoker = (Invoker) node.getProperty("Invoker.edit"); 
            if (invoker == null) {
                String invtype = filetype.toLowerCase() + ":open";
                invoker = actionsProvider.getInvoker(node, invtype);
                node.setProperty("Invoker.edit", invoker); 
            }            
            if (invoker != null) { 
                Action a = createAction("edit", "", "images/toolbars/edit.png", "ctrl E", 'e', null, true); 
                nodeActions.add(a);
            } 
        }

        List children = node.getPropertyList("children");
        if (children == null) children = new ArrayList(); 
        
        if (isAllowCreate()) { 
            List<Invoker> list = node.getPropertyList("Invoker.createlist"); 
            if (list == null) {
                list = new ArrayList();                 
                if (!children.isEmpty()) {
                    for (Object o: children) {
                        if (!(o instanceof Map)) continue;
                        
                        Map citem = (Map) o;
                        String cfiletype = getString(citem, "filetype");
                        if (cfiletype == null || cfiletype.trim().length() == 0) continue;
                        
                        String invtype = cfiletype.toLowerCase() + ":create";
                        Invoker invoker = actionsProvider.getInvoker(node, invtype); 
                        if (invoker != null) {
                            invoker = invoker.clone(); 
                            invoker.getProperties().put("Node.fileTypeObject", citem);
                            
                            String scaption = getString(citem, "caption");
                            if (scaption != null) invoker.setCaption(scaption);
                            
                            list.add(invoker);
                        }
                    }
                }                
                node.setProperty("Invoker.createlist", list);
            } 
            if (list.isEmpty()) {
                String cfiletype = parentController.getDefaultFileType();
                if (cfiletype != null && cfiletype.length() > 0) { 
                    String invtype = cfiletype.toLowerCase() + ":create";
                    Invoker invoker = actionsProvider.getInvoker(node, invtype); 
                    if (invoker != null) {
                        invoker = invoker.clone(); 
                        Map citem = new HashMap();
                        citem.put("filetype", cfiletype); 
                        invoker.getProperties().put("Node.fileTypeObject", citem);
                        list.add(invoker);
                    }  
                } 
            }            
            if (!list.isEmpty()) { 
                Action a = createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', null, true); 
                formActions.add(a); 
            }
        }   
        
        if (isAllowOpen()) { 
            List<Invoker> list = node.getPropertyList("Invoker.openlist"); 
            if (list == null) {
                list = new ArrayList();
                if (!children.isEmpty()) {
                    for (Object o: children) {
                        if (!(o instanceof Map)) continue;
                        
                        Map citem = (Map) o;
                        String cfiletype = getString(citem, "filetype");
                        if (cfiletype == null || cfiletype.trim().length() == 0) continue;
                        
                        String invtype = cfiletype.toLowerCase() + ":open";
                        Invoker invoker = actionsProvider.getInvoker(node, invtype); 
                        if (invoker != null) {
                            invoker = invoker.clone(); 
                            invoker.getProperties().put("Node.fileTypeObject", citem);
                            
                            String scaption = getString(citem, "caption");
                            if (scaption != null) invoker.setCaption(scaption);
                            
                            list.add(invoker);
                        }
                    }
                } 
                node.setProperty("Invoker.openlist", list); 
            } 
            
            Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null}", true); 
            a.getProperties().put("depends", "selectedEntity"); 
            formActions.add(a); 
        }
        
        formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true)); 
        
        //load extended actions for the node
        List<Action> actionlist = parentController.lookupActions("formActions");
        if (actionlist != null) formActions.addAll(actionlist); 
        
        List<Invoker> list = node.getPropertyList("Invoker.formActions"); 
        if (list == null) {
            if (filetype == null) {
                filetype = parentController.getDefaultFileType(); 
            } 
            if (filetype != null) {
                String invtype = filetype.toLowerCase() + ":formActions";
                list = actionsProvider.getInvokers(node, invtype);
                node.setProperty("Invoker.formActions", list);
            }
        }         
        if (list != null && !list.isEmpty()) {
            for (Invoker invoker: list) { 
                formActions.add(new ActionInvoker(invoker)); 
            } 
        } 
    } 
    
    private void buildActionsForSearchNode() {
        formActions.clear();
        nodeActions.clear();
        
        Node node = getNode();
        if (node == null) return;
        
        String dfiletype = (parentController==null? null: parentController.getDefaultFileType()); 
        if (dfiletype == null || dfiletype.length() == 0) return;
                
        formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true));         
    }     
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" overrides/helper/utility methods ">
    
    public Opener getQueryForm() {
        if (!isAllowSearch()) return null;
        if (_showQueryForm) { 
            return super.getQueryForm(); 
        } else { 
            return null; 
        } 
    }
        
    protected void beforeGetColumns(Map params) {
        Node node = getNode(); 
        Object item = (node == null? null: node.getItem()); 
        if (item instanceof Map) params.putAll((Map) item);         
    }
    
    protected void onbeforeFetchList(Map params) {
        Node node = getNode(); 
        Object item = (node == null? null: node.getItem());             
        if (item instanceof Map) params.putAll((Map) item); 
    }
    
    public Column[] initColumns(Column[] columns) { 
        if (columns == null || columns.length == 0) return columns;
                
        Column[] newColumns = new Column[columns.length+1]; 
        for (int i=0; i<columns.length; i++) {
            newColumns[i+1] = columns[i]; 
        } 
        
        Column aColumn = new Column(null, "");
        aColumn.setTypeHandler(new DefaultIconColumnHandler()); 
        aColumn.setResizable(false);
        aColumn.setWidth(25);
        newColumns[0] = aColumn; 
        return newColumns; 
    }
    
    public Object open() throws Exception {
        Map item = (Map) getSelectedEntity(); 
        if (item == null) return null;
        
        Node node = getNode();
        if (node == null) return null;
                
        /*
         *  if the selected item has a filetype, then use this as our primary handler 
         */
        String itemfiletype = getString(item, "_filetype"); 
        if (itemfiletype == null || itemfiletype.length() == 0) { 
            itemfiletype = getString(item, "filetype"); 
        } 
        if (itemfiletype != null && itemfiletype.length() > 0) {
            String invtype = itemfiletype.toLowerCase()+":open";
            Invoker invoker = actionsProvider.getInvoker(node, invtype);  
            if (invoker == null) { 
                System.out.println("No available file type handler ("+invtype+")");
                MsgBox.alert("No access privilege for this item. Please contact your administrator.");
                return null; 
            } 
            
            Map map = createOpenerParams(); 
            map.put("node", node.getItem()); 
            map.put("entity", item); 
            return actionsProvider.toOpener(invoker, map, node); 
        }
        
        /*
         *  return a dropdown menu if there are several actions that were hooked.
         */
        List<Invoker> list = node.getPropertyList("Invoker.openlist");
        if (list != null && !list.isEmpty()) {
            Map map = createOpenerParams();
            map.put("node", node.getItem());
            map.put("entity", item); 
            return actionsProvider.toOpener(list.get(0), map, node); 
            
            /*
            PopupMenuOpener opener = new PopupMenuOpener();   
            for (Invoker invoker: list) {
                Map map = createOpenerParams();
                map.put("node", node.getItem());
                map.put("entity", item);
                opener.add(actionsProvider.toOpener(invoker, map, node));
            } 
            return opener;
            */
        }
        
        /*
         *  use the default file type handler if there is...
         */
        String dfiletype = parentController.getDefaultFileType();        
        Invoker invoker = (Invoker) node.getProperty("Invoker.defaultOpen"); 
        if (invoker == null && dfiletype != null && dfiletype.length() > 0) {
            String invtype = dfiletype.toLowerCase()+":open";
            invoker = actionsProvider.getInvoker(node, invtype);  
            if (invoker == null) { 
                System.out.println("No available file type handler ("+invtype+")");
                MsgBox.alert("No access privilege for this item. Please contact your administrator.");
                return null; 
            } 
            node.setProperty("Invoker.defaultOpen", invoker);
        } 
        if (invoker != null) {
            Map map = createOpenerParams();
            map.put("node", node.getItem()); 
            map.put("entity", item);
            return actionsProvider.toOpener(invoker, map, node); 
        }
        
        
        /*
         *  last option is the item's objid
         */ 
        if (!node.isLeaf() && getString(item,"objid") != null) {
            node.loadItems(); 

            Node selNode = null; 
            String srcid = getString(item, "objid");
            List<Node> nodes = node.getItems();
            for (Node cnode: nodes) { 
                Object citem = cnode.getItem();
                if (!(citem instanceof Map)) continue;

                String oid = getString((Map) citem, "objid");
                if (oid != null && oid.equals(srcid)) {
                    selNode = cnode;
                    break; 
                }
            }            
            if (selNode != null) {
                selNode.open();
            }            
        }        
        //always return a null value for this line
        return null; 
    }
    
    public Opener create() throws Exception {
        Node node = getNode();
        if (node == null) return null;
        
        List<Invoker> list = node.getPropertyList("Invoker.createlist");
        if (list == null || list.isEmpty()) {
            System.out.println("No available file type handler"); 
            return null; 
        }
        
        PopupMenuOpener opener = new PopupMenuOpener();  
        opener.setExecuteOnSingleResult(true); 
        for (Invoker invoker: list) {
            Map map = createOpenerParams();
            map.put("node", node.getItem());
            
            Object fileTypeObject = invoker.getProperties().get("Node.fileTypeObject"); 
            if (fileTypeObject != null) map.put("filetype", fileTypeObject); 
            
            opener.add(actionsProvider.toOpener(invoker, map, node));
        }
        return opener;        
    }
    
    public Object edit() throws Exception {
        Node node = getNode();
        if (node == null) return null;

        Invoker invoker = (Invoker) node.getProperty("Invoker.edit");
        if (invoker == null) {
            System.out.println("No available file type handler");
            return null;            
        }
        
        Map map = createOpenerParams();
        map.put("node", node.getItem());
        map.put("entity", node.getItem());
        return actionsProvider.toOpener(invoker, map, node); 
    }
    
    public void refresh() {         
        super.refresh(); 
        
        Node node = getNode();
        if (node != null) node.reloadItems();
    } 
    
//    private String getString(Map data, String name) {
//        Object o = (data == null? null: data.get(name));
//        return (o == null? null: o.toString()); 
//    }

    protected Map createOpenerParams() {
        Map params = super.createOpenerParams(); 
        params.put("listModelHandler", new ListModelHandlerImpl()); 
        return params; 
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
        ExplorerViewListController_old root = ExplorerViewListController_old.this;
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
            Node node = root.getNode();
            Map map = root.createOpenerParams();
            map.put("node", node.getItem());
            map.put("entity", (Map) root.getSelectedEntity());
            return actionsProvider.toOpener(invoker, map, node); 
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
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultListService (class) "> 
    
    private class DefaultListService implements ListService 
    {
        public List<Map> getColumns(Map params) {
            List<Map> columns = new ArrayList();
            Map map = new HashMap();
            map.put("name", "caption");
            map.put("caption", "Name");
            columns.add(map); 
            return columns;
        }

        public List getList(Map params) {
            return new ArrayList(); 
        }        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ListModelHandlerImpl (class) "> 
    
    private class ListModelHandlerImpl implements ListModelHandler {
        
        ExplorerViewListController_old root = ExplorerViewListController_old.this; 
        
        public Object getSelectedEntity() { 
            return root.getSelectedEntity(); 
        }

        public void addItem(Object data) { 
            root.addItem(data); 
        }

        public void updateItem(Object data) {
            root.updateItem(data); 
        }

        public void removeItem(Object data) {
            root.removeItem(data); 
        } 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" DefaultIconColumnHandler (class) "> 
    
    private class DefaultIconColumnHandler extends IconColumnHandler {
        
        ExplorerViewListController_old root = ExplorerViewListController_old.this;
        
        public Object getValue(Object rowValue, Object columnValue) {
            if (rowValue instanceof Map) {
                Map map = (Map) rowValue; 
                String filetype = root.getString(map, "filetype");
                if (filetype == null) return "default_file";
                
                String context = (root.parentController == null? null: root.parentController.getContext()); 
                String iconpath = "images/explorer/"+context+"/"+filetype+".png";
                Object anIcon = ImageIconSupport.getInstance().getIcon(iconpath.toLowerCase()); 
                if (anIcon == null) {
                    try {
                        anIcon = UIManager.getLookAndFeelDefaults().getIcon("Tree.closedIcon"); 
                    } catch(Throwable t){;} 
                }
                return anIcon; 
            } else { 
                return null; 
            }
        }
    }
    
    // </editor-fold>
}
