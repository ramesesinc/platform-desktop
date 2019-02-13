package com.rameses.osiris2.common;

import com.rameses.osiris2.client.InvokerProxy;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Column;
import com.rameses.rcp.common.ListItem;
import com.rameses.rcp.common.ListItemStatus;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ListController extends BasicListController implements PageListModelHandler 
{     
    private ListService service;
    private List formActions;
    private List navActions;
    private List<Map> contextMenuActions;
    private Map query = new HashMap(); 
    private String tag;
    private String formName;
    private int defaultRowSize = 20;
    
    public abstract String getServiceName();
    
    public String getEntityName() {
        if (wuentityName == null || wuentityName.length() == 0)
            throw new RuntimeException("Please provide an entityName");
        
        return wuentityName; 
    }
       
    // <editor-fold defaultstate="collapsed" desc=" Getter/Setter ">        
            
    public String getTag() { 
        return (wutag == null? tag: wutag); 
    } 
    public void setTag(String tag) { 
        this.tag = tag; 
    } 
    
    public String getFormTarget() { 
        if (wuformTarget == null || wuformTarget.length() == 0) { 
            return "popup"; 
        } else {
            return wuformTarget; 
        }
    } 
    
    public String getFormName() { 
        if (wuformName == null || wuformName.length() == 0) { 
            return formName; 
        } else { 
            return wuformName; 
        } 
    }     
    public void setFormName(String formName) { 
        this.formName = formName; 
    }
    
    public int getRows() {
        return (wurows == null? defaultRowSize: wurows.intValue()); 
    }
        
    public Map getQuery() { return query; }
            
    public Opener getQueryForm() 
    {
        if (isAllowSearch()) { 
            Opener o = new Opener();
            o.setOutcome("queryform");
            return o;
        } else {
            return null; 
        } 
    } 

    protected void beforeGetColumns(Map params){}
        
    public Column[] getColumns() { return null; }
        
    public List<Map> getColumnList() {
        Map params = new HashMap();
        String stag = getTag();
        if (stag != null) params.put("_tag", stag);
    
        beforeGetColumns(params); 
        return getService().getColumns(params); 
    }
    
    public List<Map> getContextMenu(Object item, String columnName) {
        if (contextMenuActions == null) {
            contextMenuActions = new ArrayList();
            List<Action> actions = lookupActions("contextMenu"); 
            while (!actions.isEmpty()) {
                Action a = (Action) actions.remove(0);                
                Map map = new HashMap();
                map.put("value", a.getCaption()); 
                map.put("action", a);
                contextMenuActions.add(map);
            }
        }
        return contextMenuActions;
    }

    public Object callContextMenu(Object item, Object menuItem) {
        if (menuItem instanceof Map) {
            Map map = (Map) menuItem;
            Object o = map.get("action"); 
            if (o instanceof Action || o instanceof Opener || o instanceof String) 
                return o;
        }
        return null; 
    } 
    
    // </editor-fold>    
 
    // <editor-fold defaultstate="collapsed" desc=" Options ">    
    
    public boolean isShowNavActions() { return true; } 
    public boolean isShowFormActions() { return true; } 
    
    public Map getCreatePermission() { return null; } 
    public Map getReadPermission() { return null; } 
    
    public boolean isAllowClose() { 
        if (wuallowClose == null) return true; 
        
        return wuallowClose.booleanValue();
    } 
    
    public boolean isAllowCreate() { 
        if (wuallowCreate == null) return true; 
        
        return wuallowCreate.booleanValue();
    } 
    
    public boolean isAllowOpen() { 
        if (wuallowOpen == null) return true; 
        
        return wuallowOpen.booleanValue();
    } 
    
    public boolean isAllowSearch() { 
        if (wuallowSearch == null) return true; 
        
        return wuallowSearch.booleanValue();
    } 

    public boolean isAllowColumnEditing() {
        if (wuallowColumnEditing == null) return false; 
        
        return wuallowColumnEditing.booleanValue(); 
    }
    
    public boolean isAllowAdvancedSearch() { return false; }
    public Object getAdvancedSearch() { return null; } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Action Methods ">        
    
    protected boolean onOpen(Map params) { return true; }
    protected boolean onCreate(Map params) { return true; }
    
    public Opener create() throws Exception 
    {
        Map params = createOpenerParams();
        if (!onCreate(params)) return null; 
        
        Opener opener = null; 
        try { 
            opener = InvokerUtil.lookupOpener(getEntityName()+":create", params);
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup opener caused by " + t.getMessage());
            MsgBox.alert("No access privilege for this item. Please contact your administrator.");
        } 
        
        if (opener == null) return null;
        
        String target = opener.getTarget()+"";
        if (!target.matches("window|popup|process|_window|_popup|_process")) { 
            opener.setTarget(getFormTarget()); 
        } 
        return opener;
    }
    
    protected String getEntityName(Map data) { return null; } 
    
    public Object open() throws Exception 
    {
        Map data = (Map) getSelectedEntity(); 
        if (data == null) return null;
        
        Map params = createOpenerParams();
        String filetype = (data.get("filetype")==null? null: data.get("filetype").toString());
        if (filetype != null && filetype.length() > 0) { 
            params.put("listModelHandler", new ListModelHandlerProxy()); 
        } else {
            filetype = getEntityName(); 
            String en = getEntityName(data); 
            if (en != null && en.length() > 0) filetype = en;
        }
        
        params.put("entity", data); 
        if (!onOpen(params)) return null; 
        
        Opener opener = null; 
        try {
            String invtype = filetype + ":open";
            opener = InvokerUtil.lookupOpener(invtype, params);
        } catch(Throwable t) {
            System.out.println("[WARN] error lookup opener caused by " + t.getMessage());
            MsgBox.alert("No access privilege for this item. Please contact your administrator.");            
        }
        
        if (opener == null) return null;
        
        String target = opener.getTarget()+"";
        if (!target.matches("window|popup|process|_window|_popup|_process")) { 
            opener.setTarget(getFormTarget()); 
        } 
        return opener;
    }    
    
    public Object close() {
        return "_close"; 
    }
    
    public void reset() 
    {
        query.clear(); 
        search(); 
    }
    
    public void search() {
        load(); 
    }    
    
    protected Map createOpenerParams() 
    {
        Map params = new HashMap();
        params.put("listModelHandler", this);
        return params;
    }
          
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Form and Navigation Actions ">  
    
    public List getNavActions() {
        if (navActions == null) 
        {
            if (!isShowNavActions()) return null;
            
            navActions = new ArrayList();
            navActions.add(createAction("moveFirstPage", "", "images/navbar/first.png", null, '\u0000', null, true)); 
            navActions.add(createAction("moveBackPage",  "", "images/navbar/previous.png", null, '\u0000', null, true)); 
            navActions.add(createAction("moveNextPage",  "", "images/navbar/next.png", null, '\u0000', null, true)); 
            navActions.add(createAction("moveLastPage",  "", "images/navbar/last.png", null, '\u0000', null, true)); 
        } 
        return navActions; 
    }
    
    public List getFormActions() {
        if (formActions == null) {
            if (!isShowFormActions()) return null;
            
            formActions = new ArrayList();
            if (isAllowClose()) 
                formActions.add(createAction("close", "Close", "images/toolbars/cancel.png", "ctrl C", 'c', null, true)); 
            
            if (isAllowCreate()) {
                Action a = createAction("create", "New", "images/toolbars/create.png", "ctrl N", 'n', null, true); 
                Map perm = getCreatePermission();
                loadPermission(a, perm); 
                formActions.add(a); 
            }
            if (isAllowOpen()) { 
                Action a = createAction("open", "Open", "images/toolbars/open.png", "ctrl O", 'o', "#{selectedEntity != null}", true);
                a.getProperties().put("depends", "selectedEntity");                
                
                Map perm = getReadPermission();
                loadPermission(a, perm);
                formActions.add(a); 
            }
            
            formActions.add(createAction("reload", "Refresh", "images/toolbars/refresh.png", "ctrl R", 'r', null, true)); 
            
            List extActions = lookupActions("formActions");
            formActions.addAll(extActions);
            extActions.clear(); 
        } 
        return formActions; 
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
    
    private void loadPermission(Action a, Map perm) {
        if (perm == null || perm.isEmpty()) return; 
        
        a.setDomain(getString(perm, "domain")); 
        a.setRole(getString(perm, "role")); 
        a.setPermission(getString(perm, "permission")); 
    }
    
    protected String getString(Map map, String name) {
        Object ov = (map == null? null: map.get(name)); 
        return (ov == null? null: ov.toString()); 
    }
    
    protected Integer getInteger(Map map, String name) {
        try {
            String sval = getString(map, name); 
            if (sval == null || sval.length() == 0) return null; 
            
            return new Integer(Integer.parseInt(sval)); 
        } catch(Throwable t) {
            return null; 
        }
    }
    
    protected Boolean getBoolean(Map map, String name) {
        try {
            String sval = getString(map, name); 
            if (sval == null || sval.length() == 0) return null; 
            if ("true".equals(sval)) return new Boolean(true); 
            else if ("false".equals(sval)) return new Boolean(false); 
            else return null; 
        } catch(Throwable t) {
            return null; 
        }
    }    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" handle workunit properties ">

    protected String wuserviceName;    
    private String wuformName;
    private String wuformTarget = "popup";    
    private String wutag;
    private Integer wurows;
    private Boolean wuallowClose;
    private Boolean wuallowCreate;
    private Boolean wuallowOpen;
    private Boolean wuallowSearch;
    private String wuentityName;
    private Boolean wuallowColumnEditing;
    
    protected void handleWorkunitProperties(Map props) {
        if (props == null) return;
        
        wuformName = getString(props, "formName");
        wurows = getInteger(props, "rows");
        wuallowClose = getBoolean(props, "allowClose");
        wuallowCreate = getBoolean(props, "allowCreate");
        wuallowOpen = getBoolean(props, "allowOpen");
        wuallowSearch = getBoolean(props, "allowSearch");
        wuserviceName = getString(props, "serviceName");
        wuentityName = getString(props, "entityName");
        
        wuformTarget = getString(props, "formTarget");
        if (wuformTarget == null || wuformTarget.length() == 0) {
            wuformTarget = "popup"; 
        }
        
        wutag = getString(props, "tag"); 
        wuallowColumnEditing = getBoolean(props, "allowColumnEditing");
    }
    
    // </editor-fold>
    
    @Deprecated
    protected void onbeforeFetchList(Map params) {
        beforeFetchList(params);
    }
    @Deprecated
    protected void onafterFetchList(List list) {
        afterFetchList(list);
    }
    
    protected void beforeFetchList(Map params) {}
    protected void afterFetchList(List list) {}
    
    public List fetchList(Map m) {
        String stag = getTag();
        if (stag != null) m.put("_tag", stag);
        
        Map qrymap = getQuery();
        if (qrymap != null) m.putAll(qrymap); 
               
        List list = getService().getList(m); 
        return list; 
    }
    
    protected ListService getService(){
        String name = getServiceName();
        if (name == null || name.trim().length() == 0)
            throw new RuntimeException("No service name specified"); 
            
        if (service == null) {
            service = (ListService) InvokerProxy.getInstance().create(name, ListService.class);
        }
        return service;
    } 
    
    private StringBuffer recordCountInfo;
    private StringBuffer pageCountInfo;
    
    protected void dataChanged(Object stat) {
        recordCountInfo = new StringBuffer();
        pageCountInfo = new StringBuffer(); 
        if (stat instanceof ListItemStatus) {
            ListItemStatus lis = (ListItemStatus)stat;
            recordCountInfo.append( lis.getTotalRows() ); 
            recordCountInfo.append(" Record(s)    ");
            
            pageCountInfo.append("Page  " + lis.getPageIndex() + "  of  ");
            if (lis.isHasNextPage()) {
                if (lis.getPageIndex() < lis.getPageCount()) 
                    pageCountInfo.append(lis.getPageCount()); 
                else 
                    pageCountInfo.append("?"); 
            } else {
                pageCountInfo.append(lis.getPageCount()); 
            }
        }
    }
    
    public Object getRecordCountInfo() { return recordCountInfo; }
    public Object getPageCountInfo() { return pageCountInfo; }    

    /*
     *  ListControllerHandler implementation
     */ 
    // <editor-fold defaultstate="collapsed" desc=" ListControllerHandler implementation "> 
    
    public void addItem(Object data) 
    {
        if (data == null) return;
        
        int idx = -1; 
        try { 
            idx = getListItems().indexOf(getSelectedItem()); 
        } catch(Exception ign) {;} 
        
        List dataList = getDataList();
        if (idx >= 0 && idx < dataList.size()) { 
            dataList.add(idx, data); 
        } 
        else {
            dataList.add(data); 
            idx = dataList.size()-1;
        }
        
        refresh(); 
        setSelectedItem(idx); 
    }
    
    public void updateItem(Object data) 
    {
        ListItem item = getSelectedItem(); 
        if (item == null) return;
        if (data != null) 
        {
            List dataList = getDataList();
            if (item.getIndex() >= 0 && item.getIndex() < dataList.size()) 
            {
                dataList.set(item.getIndex(), data); 
                item.loadItem(data);
                refreshSelectedItem(); 
            } 
        }
    }

    public void removeItem(Object data) 
    {
        if (getSelectedEntity() == null) return;
        
        int idx = getListItems().indexOf(getSelectedItem()); 
        if (idx >= 0) 
        {
            getDataList().remove(idx); 
            refresh();
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ListModelHandlerProxy (class) "> 
    
    private class ListModelHandlerProxy implements ListModelHandler {
        
        ListController root = ListController.this; 
        
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
    
}
