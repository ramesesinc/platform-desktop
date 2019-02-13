/*
 * InboxController.java
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
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.ListItemStatus;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Node;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PopupMenuOpener;
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
public class InboxController extends ListController 
{
    private String context; 
    private String defaultFileType;
    private String serviceName;
    
    private TreeNodeModel nodeModel; 
    private Node selectedNode;   
    private ExplorerViewService service;
    
    private List<Action> formActions; 
    private ActionsProvider actionsProvider;
    private Helper helper; 
    private Object itemStat;
    
    private List<Invoker> defaultCreateInvokers;
    private Invoker defaultCreateInvoker;
    private Invoker defaultOpenInvoker;
    
    public InboxController() {
        actionsProvider = new ActionsProvider(); 
        helper = new Helper(); 
    }

    public void init() {
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Settters ">
    
    public String getContext() { 
        if (wucontext == null || wucontext.length() == 0) { 
            wucontext = "explorer"; 
        } 
        return context; 
    }
    
    public String getDefaultFileType() { 
        return wudefaultFileType; 
    } 
    
    public String getServiceName() { 
        return wuserviceName;
    }
    
    public String getEntityName() { 
        return wuentityName;
    }  
    
    public boolean isDynamicColumns() { 
        if (wudynamicColumns == null) return true; 
        
        return wudynamicColumns.booleanValue();
    }
    
    public boolean isAutoSelect() {
        if (wuautoSelect == null) return true; 
        
        return wuautoSelect.booleanValue(); 
    }

    public int getRows() { return 20; } 
    public List getFormActions() 
    {
        if (formActions == null) {
            formActions = new ArrayList(); 
            formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true));             
            if (!isAllowCreate()) return formActions; 

            List<Invoker> invokers = getDefaultCreateInvokers();
            if (!invokers.isEmpty()) {
                Action a = createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', null, true); 
                formActions.add(a); 
            }
            
            Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null && openButtonVisible==true}", true); 
            a.getProperties().put("depends", "selectedEntity"); 
            formActions.add(a); 
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
        
    public boolean isRootVisible() { return false; } 
    public String getIcon() { return null; }
    
    public String getListTitle() {
        Node node = getSelectedOpenNode();
        String s = (node == null? null: node.getCaption()); 
        return (s == null? " ": s); 
    }
    
    public Object getItemStat() { return itemStat; } 
    public void setItemStat(Object itemStat) { this.itemStat = itemStat; }
      
    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc=" TreeNodeModel helper and utilities ">
    
    private Node _selectedOpenNode;
    
    public final Node getSelectedOpenNode() { return _selectedOpenNode; } 
    
    private class TreeNodeModelImpl extends TreeNodeModel 
    {
        InboxController root = InboxController.this;
        
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

            _selectedOpenNode = node; 
            updateView();
            return null; 
        }
    }
    
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" ListController override methods ">
    
    public boolean isAllowSearch() { return true; }     
    
    public final boolean isOpenButtonVisible() {
        Node node = getSelectedNode();
        if (node != null) {
            List invokers = node.getPropertyList("Invoker.openlist"); 
            if (invokers != null && !invokers.isEmpty()) return true; 
        }
                
        Object item = getSelectedEntity();
        if (item instanceof Map) {
            String filetype = helper.getFiletype((Map)item);
            if (filetype != null && filetype.length() > 0) return true; 
            if (defaultOpenInvoker != null) return true;
        }
        
        return false; 
    }
    
    protected Map createOpenerParams() {
        Map params = super.createOpenerParams(); 
        params.put("listModelHandler", null); 
        return params; 
    } 
    
    protected void beforeGetColumns(Map params) {
        Node node = getSelectedNode();
        Object item = (node == null? null: node.getItem()); 
        if (item instanceof Map) params.putAll((Map) item);         
    }
    
    protected void onbeforeFetchList(Map params) {
        Node node = getSelectedNode();
        Object item = (node == null? null: node.getItem());             
        if (item instanceof Map) params.putAll((Map) item); 
    } 
    
    public Object open() throws Exception {
        Map item = (Map) getSelectedEntity(); 
        if (item == null) return null;

        Node node = getSelectedNode();        
        /*
         *  if the selected item has a filetype, then use this as our primary handler 
         */
        String itemfiletype = helper.getFiletype(item); 
        if (itemfiletype != null && itemfiletype.length() > 0) {
            String invtype = itemfiletype.toLowerCase()+":open";
            Invoker invoker = actionsProvider.getInvoker(node, invtype);  
            if (invoker == null) { 
                System.out.println("No available file type handler ("+invtype+")");
                MsgBox.alert("No access privilege for this item. Please contact your administrator.");
                return null; 
            } 
            
            Map map = createOpenerParams(); 
            map.put("node", (node==null? null: node.getItem())); 
            map.put("entity", item); 
            return actionsProvider.toOpener(invoker, map, node); 
        }        
        
        if (node != null) { 
            List<Invoker> list = node.getPropertyList("Invoker.openlist");
            if (list != null && !list.isEmpty()) {
                Map map = createOpenerParams();
                map.put("node", node.getItem());
                map.put("entity", item); 
                return actionsProvider.toOpener(list.get(0), map, node); 
            }
        } 
        
        /*
         *  use the default file type handler if there is...
         */        
        String filetype = getDefaultFileType();
        if (filetype != null && filetype.length() > 0) {
            String invtype = filetype.toLowerCase()+":open";
            Invoker invoker = actionsProvider.getInvoker(null, invtype); 
            if (invoker != null) {
                Map map = createOpenerParams();
                map.put("node", null);
                return actionsProvider.toOpener(invoker, map, node); 
            } 
            System.out.println("No available file type handler ("+invtype+")");
            MsgBox.alert("No access privilege for this item. Please contact your administrator.");
            return null; 
        } 
        
        return null; 
    }    
    
    public Opener create() throws Exception {
        List<Invoker> invokers = null;
        Node node = getSelectedNode(); 
        if (node != null) 
            invokers = node.getPropertyList("Invoker.createlist");
        
        if (invokers == null || invokers.isEmpty()) 
            invokers = getDefaultCreateInvokers();
        
        if (invokers != null && !invokers.isEmpty()) {
            Map map = createOpenerParams();
            map.put("node", (node == null? null: node.getItem()));
            
            PopupMenuOpener menu = new PopupMenuOpener(); 
            menu.setExecuteOnSingleResult(true);
            for (Invoker inv: invokers) {
                menu.add(actionsProvider.toOpener(inv, map, node)); 
            } 
            return menu;             
        }
        
        System.out.println("No available file type handler"); 
        return null; 
    } 

    private StringBuffer footerInfo;
    
    protected void dataChanged(Object stat) {
        footerInfo = new StringBuffer();
        if (stat instanceof ListItemStatus) {
            ListItemStatus lis = (ListItemStatus)stat;
//            System.out.println("pageindex="+lis.getPageIndex() + ", pagecount="+lis.getPageCount());
//            int pagecount = lis.getPageCount()-1;
//            int totalrows = Math.max(pagecount,0) * getRows(); 
//            if (lis.isIsLastPage()) {
//                totalrows -= Math.max(getRows(), 0); 
//                totalrows += getDataListSize(); 
//            }
//            
//            footerInfo.append( totalrows ); 
//            footerInfo.append(" Record(s)    ");
//            footerInfo.append("Page  " + lis.getPageIndex() + "  of  ");
//            if (lis.isIsLastPage()) 
//                footerInfo.append(lis.getPageCount()); 
//            else 
//                footerInfo.append("?");            
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
        
    private void updateView() { 
        getQuery().clear(); 
        
        buildActions(); 
        Binding obinding = (Binding) getNodeModel().getBinding(); 
        if (obinding != null) obinding.refresh("selectedOpenNode|formActions"); 
        
        if (isDynamicColumns())
            reloadAll(); 
        else 
            reload(); 
    } 
    
    private List<Invoker> getDefaultInvokers(Node node, String mode) {
        String filetype = getDefaultFileType();
        if (filetype == null || filetype.length() == 0) 
            return new ArrayList();
        
        String invtype = filetype.toLowerCase() + ":"+mode;
        return actionsProvider.getInvokers(node, invtype); 
    }
    
    private List<Invoker> getDefaultCreateInvokers() {
        if (defaultCreateInvokers == null) {
            defaultCreateInvokers = new ArrayList();             
            
            String dfiletype = getDefaultFileType();
            if (dfiletype == null || dfiletype.length() == 0) 
                return defaultCreateInvokers;
            
            String invtype = dfiletype.toLowerCase() + ":create";
            defaultCreateInvokers.addAll(actionsProvider.getInvokers(null, invtype));             
        }
        return defaultCreateInvokers;
    }
    
    private void buildActions() {
        if (formActions == null) { 
            formActions = new ArrayList(); 
        } else { 
            formActions.clear(); 
        } 
        
        formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true)); 
        
        boolean allowCreate = isAllowCreate();
        Node node = getSelectedNode();
        if (node == null && allowCreate) {
            List<Invoker> invokers = getDefaultCreateInvokers();
            if (!invokers.isEmpty()) { 
                Action a = createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', "#{allowCreate==true}", true); 
                formActions.add(a); 
            } 
            //exit
            return;
        } 
        
        if (allowCreate) { 
            List<Invoker> list = node.getPropertyList("Invoker.createlist"); 
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
                }
                
                list = new ArrayList(); 
                for (String filetype: filetypes) {
                    String invtype = filetype + ":create";
                    list.addAll(actionsProvider.getInvokers(node, invtype)); 
                }                
                node.setProperty("Invoker.createlist", list);
            }
            
            if (!list.isEmpty()) { 
                Action a = createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', "#{allowCreate==true}", true); 
                formActions.add(a); 
            }
        }   
        
        if (isAllowOpen()) { 
            List<Invoker> list = node.getPropertyList("Invoker.openlist"); 
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
                } 
                
                list = new ArrayList(); 
                for (String filetype: filetypes) {
                    String invtype = filetype + ":open";
                    list.addAll(actionsProvider.getInvokers(node, invtype)); 
                }                
                node.setProperty("Invoker.openlist", list); 
            } 
            
            Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null && openButtonVisible==true}", true); 
            a.getProperties().put("depends", "selectedEntity"); 
            formActions.add(a); 
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
        InboxController root = InboxController.this;
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
    
    // <editor-fold defaultstate="collapsed" desc=" handle workunit properties ">

    private String wucontext;
    private String wudefaultFileType;
    private String wuserviceName;
    private String wuentityName;
    private Boolean wudynamicColumns;
    private Boolean wuautoSelect;
    
    protected void handleWorkunitProperties(Map props) {
        if (props == null) return;
        
        super.handleWorkunitProperties(props);        
        wucontext = getString(props, "context");
        wudefaultFileType = getString(props, "defaultFileType");
        wuserviceName = getString(props, "serviceName");
        wuentityName = getString(props, "entityName");
        wudynamicColumns = getBoolean(props, "dynamicColumns"); 
        wuautoSelect = getBoolean(props, "autoSelect"); 
    }
    
    // </editor-fold>
}
