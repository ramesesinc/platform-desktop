/*
 * AbstractListDataProvider.java
 *
 * Created on May 14, 2013, 2:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.rameses.rcp.common;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.Column.TypeHandler;
import com.rameses.rcp.framework.ActionProvider;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.util.BreakException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author wflores
 */
public abstract class AbstractListDataProvider 
{
    public final static int FETCH_MODE_LOAD         = 0;
    public final static int FETCH_MODE_REFRESH      = 1;
    public final static int FETCH_MODE_RELOAD       = 2;
    public final static int FETCH_MODE_RELOAD_ALL   = 3;
    
    protected MultiSelectionSupport multiSelectionSupport;
    protected PropertyChangeSupport propertySupport; 
    protected TableModelSupport tableModelSupport; 
    protected MessageSupport messageSupport;
    
    private ListItemHandler listItemHandler;
    
    private Set checkedItems = new HashSet(); 
    private int fetchMode = FETCH_MODE_LOAD; 
    
    private String selectedColumn;    
    private Column[] columns;
    private List dataList;
    private int fetchedRows;
    private int totalRows; 
    private boolean processing;
    private boolean forceUpdate;
    
    private boolean autoResize = true; 
    private boolean multiSelect;
    private Object multiSelectHandler; 
    private String multiSelectFieldName; 

    private List<ListItem> itemList = new ArrayList<ListItem>(); 
    private ListItem selectedItem; 
    private Object selectedElement;

    private ListSelectionSupport selectionSupport;
    private ActionProvider actionProvider;
    
    public AbstractListDataProvider() {
        propertySupport = new PropertyChangeSupport();
        tableModelSupport = new TableModelSupport(); 
        listItemHandler = new ListItemHandler();        
        messageSupport = new MessageSupport(); 
    }
    
    public abstract List fetchList(Map params);
    
    public final ListSelectionSupport getSelectionSupport() {
        if (selectionSupport == null) { 
            selectionSupport = new ListSelectionSupport(); 
        } 
        return selectionSupport; 
    }    
    
    public List<Map> getColumnList() { return null; }
        
    public Column[] getColumns() { return columns; } 
    public void setColumns(Column[] columns) { 
        this.columns = columns; 
    } 
    
    public Column[] initColumns(Column[] columns) {
        return columns; 
    } 
    
    // the data for the popup menu
    public List<Map> getContextMenu(Object item, String columnName) { 
        return null; 
    }    
    // invoke when a menu item is selected
    public Object callContextMenu(Object item, Object menuItem) {
        return null; 
    } 
    
    public boolean isAutoResize() { return autoResize; } 
    public void setAutoResize( boolean autoResize ) {
        this.autoResize = autoResize; 
    }
    
    public boolean isForceUpdate() { return forceUpdate; } 
    public void setForceUpdate( boolean forceUpdate ) {
        this.forceUpdate = forceUpdate; 
    }
    
    public MultiSelectionSupport getMultiSelectionSupport() {
        if (multiSelectionSupport == null) {
            multiSelectionSupport = new MultiSelectionSupport(); 
        } 
        return multiSelectionSupport; 
    }

    public int getMultiSelectMode() { 
        return getMultiSelectionSupport().getSelectionMode(); 
    } 
    public void setMultiSelectMode( int multiSelectMode ) {
        getMultiSelectionSupport().setSelectionMode( multiSelectMode ); 
    }
    
    public boolean isMultiSelect() { return multiSelect; } 
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
    
    public Object getMultiSelectHandler() { return multiSelectHandler; } 
    public void setMultiSelectHandler(Object multiSelectHandler) {
        this.multiSelectHandler = multiSelectHandler; 
    }
        
    public String getMultiSelectFieldName() {
        return multiSelectFieldName; 
    }
    public void setMultiSelectFieldName( String multiSelectFieldName ) {
        this.multiSelectFieldName = multiSelectFieldName; 
    }
    
    public Column getPrimaryColumn() { return null; } 
    
    public void removeHandler(TableModelHandler handler) {
        tableModelSupport.remove(handler); 
    }
    public void addHandler(TableModelHandler handler) {
        tableModelSupport.add(handler); 
    }

    public void removeHandler(PropertyChangeHandler handler) {
        propertySupport.remove(handler); 
    }
    public void addHandler(PropertyChangeHandler handler) {
        propertySupport.add(handler); 
    }
        
    protected void fetch(boolean forceLoad) 
    {
        if (dataList == null || forceLoad) 
        {
            Map params = new HashMap();
            onbeforeFetchList(params);

            List resultList = fetchList(params);
            if (resultList == null) resultList = new ArrayList();

            onafterFetchList(resultList);
            setDataList(resultList); 
            fetchedRows = resultList.size();
        } 
        else {
            fetchedRows = (dataList == null? 0: dataList.size()); 
        }

        totalRows = fetchedRows; 
        fillListItems(dataList, 0); 

        if (selectedItem != null) setSelectedItem(selectedItem.getIndex());
        if (selectedItem == null) setSelectedItem(0);
    } 
    
    private void fetchImpl() {
        try { 
            propertySupport.firePropertyChange("loading", true);
            processing = true;
            boolean forceLoad = (fetchMode==FETCH_MODE_LOAD || fetchMode==FETCH_MODE_RELOAD || fetchMode==FETCH_MODE_RELOAD_ALL); 
            fetch(forceLoad); 
            if (fetchMode == FETCH_MODE_RELOAD_ALL) { 
                tableModelSupport.fireTableDataProviderChanged(); 
            } else { 
                tableModelSupport.fireTableDataChanged(); 
            } 
            int index = (selectedItem == null? 0: selectedItem.getIndex());
            tableModelSupport.fireTableRowSelected(index, false);            
            tableModelSupport.fireTableRowsUpdated(index, index); 
        } catch(RuntimeException re) {
            throw re;
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        } finally {
            processing = false;
            propertySupport.firePropertyChange("loading", false);        
        }
    }

    public final boolean isProcessing() { return processing; }
    
    protected void onbeforeFetchList(Map params) {}
    protected void onafterFetchList(List resultList) {}

    public int getTopRow() { return 0; }
    public void setTopRow(int topRow) {}  
            
    public int getMaxRows() { return getRowCount(); }     
    public int getRowCount() { return fetchedRows; }
    public int getRows() { return -1; }
    
    public boolean validRange(int index) { 
        return (index >= 0 && index < itemList.size()); 
    } 
    
    public int getListItemCount() { return itemList.size(); }    
    public List<ListItem> getListItems() { return itemList; }
    
    public Object getListItemData(int index) 
    {
        ListItem li = getListItem(index); 
        return (li == null? null: li.getItem()); 
    }
    
    public ListItem getListItem(int index) 
    {
        if (index >= 0 && index < itemList.size()) 
            return itemList.get(index); 
        else
            return null; 
    }
    
    public ListItem getListItemByRownum(int rownum) 
    {
        for (int i=0; i<itemList.size(); i++) 
        {
            if (itemList.get(i).getRownum() == rownum) 
                return itemList.get(i); 
        } 
        return null; 
    }
    
    public ListItem getSelectedItem() { return selectedItem; } 
    public void setSelectedItem(ListItem li) 
    {
        int index = (li == null? -1: li.getIndex()); 
        setSelectedItem(index); 
    }
    
    public Object getSelectedElement() { return selectedElement; } 
    public void setSelectedElement(Object selectedElement) {
        this.selectedElement = selectedElement; 
    }

    protected void onselectedItemChanged(ListItem li) {}        
    public void setSelectedItem(int index) 
    {  
        if (index >= 0 && index < itemList.size()) 
            selectedItem = itemList.get(index); 
        else 
            selectedItem = null; 
        
        onselectedItemChanged(selectedItem); 
        fireSelectedItemChanged();  
    } 
    
    protected ActionProvider getActionProvider() {
        if (actionProvider == null)
            actionProvider = ClientContext.getCurrentContext().getActionProvider();  
        
        return actionProvider; 
    }
       
    public boolean isAllowOpen() { return true; } 
    protected Object openItem( Object item, String columnName ) {
        return null; 
    }
    
    @Deprecated 
    protected Object onOpenItem(Object item, String columnName) { 
        return openItem( item, columnName ); 
    } 
    
    protected Map getOpenerParams(Object item) { return null; } 
    protected Object lookupOpener(String actionType, Object item) { 
        if (actionType == null || actionType.length() == 0) return null;
        
        ActionProvider aprovider = getActionProvider();        
        if (aprovider == null) return null;
            
        Map actionParams = new HashMap();
        Map udfParams = getOpenerParams(item);
        if (udfParams != null) actionParams.putAll(udfParams);
        
        actionParams.put("entity", item);
        Opener opener = aprovider.lookupOpener(actionType, actionParams); 
        if (opener == null) return null;
        
        String target = opener.getTarget(); 
        if (target == null) opener.setTarget("popup"); 
        
        return opener; 
    }
    
    public final Object openSelectedItem() 
    { 
        if ( !isAllowOpen() ) { return null; }
        
        Object item = (selectedItem == null? null: selectedItem.getItem());
        if (item == null) return null;
        
        if (item instanceof Map) {
            Object ov = ((Map)item).get("_filetype");
            String sv = (ov == null? null: ov.toString()); 
            if (sv != null && sv.length() > 0) {
                Object opener = lookupOpener(sv.toLowerCase()+":open", item); 
                if (opener != null) return opener; 
            }
        }
        
        return onOpenItem(item, selectedColumn); 
    } 
    
    public final void removeSelectedItem() 
    {
        if (selectedItem == null) return;        
        if (selectedItem.getState() == ListItem.STATE_EMPTY) return;
        
        int index = selectedItem.getIndex();
        messageSupport.removeErrorMessage(index);        
        
        if (selectedItem.getState() == ListItem.STATE_DRAFT) 
        {
            selectedItem.loadItem(null, ListItem.STATE_EMPTY); 
            tableModelSupport.fireTableRowsUpdated(index, index); 
        }
        else 
        {
            ListItem firstLI = getListItem(0); 
            int rownum = selectedItem.getRownum(); 
            dataList.remove(rownum); 
            itemList.remove(index); 
            
            rownum = (firstLI == null? 0: firstLI.getRownum()); 
            fillListItems(dataList, rownum); 
            
            selectedItem = null;
            tableModelSupport.fireTableDataChanged(); 
            tableModelSupport.fireTableRowSelected(index, true); 
        } 
    } 
    
    public final void removeListItem(int index) 
    {
        if (index >= 0 && index < itemList.size()) 
        {
            itemList.remove(index); 
            rebuildIndexes();
            tableModelSupport.fireTableRowsDeleted(index, index); 
        }
    }
        
    public final String getSelectedColumn() { return selectedColumn; }    
    public final void setSelectedColumn(String selectedColumn) {
        this.selectedColumn = selectedColumn; 
    } 
    
    protected void beforeLoad(){} 
    protected void afterLoad(){}
    protected void dataChanged(Object stat){}
    
    private void fireDataChanged() { 
        ListItemStatus stat = createListItemStatus(null);
        dataChanged(stat); 
    }
    
    private void loadImpl(int fetchModeOption) {
        beforeLoad();          
        checkedItems.clear(); 
        fetchMode = fetchModeOption;  
        selectionSupport = null; 
        totalRows = 0; 
        fetchImpl(); 
        fireDataChanged();
        afterLoad(); 
    } 
    
    public void load() { 
        loadImpl(FETCH_MODE_LOAD);  
    } 
    
    public void reload() { 
        refresh(true);  
    }
        
    public void reloadAll() {
        loadImpl(FETCH_MODE_RELOAD_ALL);  
    } 
        
    public void refresh() { 
        refresh(false); 
    } 

    public void refresh(boolean forceLoad) {
        refreshImpl(forceLoad? FETCH_MODE_RELOAD: FETCH_MODE_REFRESH); 
    } 
    
    private void refreshImpl(int fetchMode) {
        this.fetchMode = fetchMode;
        fetchImpl(); 
        fireDataChanged();
    } 
    
    public void fireStuctureChanged() { 
        tableModelSupport.fireTableStructureChanged(); 
    } 
    
    public final int getDataIndexByRownum(int rownum) {
        ListItem li = getListItemByRownum(rownum); 
        if (li == null || li.getItem() == null) return -1;
        
        return dataList.indexOf(li.getItem()); 
    }

    public final int getDataListSize() {
        return (dataList == null? 0: dataList.size()); 
    }
    
    public final List getDataList() { return dataList; }
    public final Object getData(int index) 
    {
        if (dataList != null && index >= 0 && index < dataList.size()) 
            return dataList.get(index); 
        else 
            return null; 
    } 
    
    protected final void setDataList(List dataList) {
        this.dataList = dataList; 
    }
    
    protected final void initDataListWhenNull() 
    {
        if (dataList == null) dataList = new ArrayList(); 
    }
    
    public void moveBackRecord()
    {
        //do not scroll when there are error in validation
        if (messageSupport.hasErrorMessages()) return;
        if (selectedItem == null) return; 
        
        int idx = itemList.indexOf(selectedItem);
        if (idx-1 >= 0) 
        {
            setSelectedItem(idx-1);
            refreshSelectedItem();                 
        }         
    }

    public void moveNextRecord() { 
        moveNextRecord(false);  
    } 
    
    public void moveNextRecord(boolean includesEmptyItem) 
    {
        //do not scroll when there are error in validation
        if (messageSupport.hasErrorMessages()) return;
        if (selectedItem == null) return; 
        
        int idx = itemList.indexOf(selectedItem); 
        if (idx >= 0 && idx+1 < itemList.size()) 
        {
            if (getListItemData(idx+1) != null || includesEmptyItem)
            {
                setSelectedItem(idx+1);
                refreshSelectedItem(); 
            }
        }        
    }
    
    
    protected void finalize() throws Throwable 
    {
        propertySupport.removeAll();
        tableModelSupport.removeAll();
        checkedItems.clear(); 
        itemList.clear(); 
        dataList = null; 
        onfinalize();
    }
    
    protected void onfinalize() throws Throwable {}
    
    public MessageSupport getMessageSupport() { return messageSupport; } 
    
    protected void onreplaceSelectedItem(Object oldItem, Object newItem) {}
    
    
    /*
     *  notify events
     */
    public void fireSelectedItemChanged() { 
        propertySupport.firePropertyChange("selectedItemChanged", selectedItem); 
    }
    
    public void fireFocusSelectedItem() {
        propertySupport.firePropertyChange("focusSelectedItem", selectedItem); 
    } 
    
    public boolean hasSelections() { 
        return getSelectionSupport().hasSelections(); 
    } 
    
    protected void beforeSelectItem( Object fact ) {}
    protected void beforeSelectItem(Object item, boolean selected, int rowIndex) {
        Map fact = new HashMap();
        fact.put("selected", selected); 
        fact.put("index", rowIndex); 
        fact.put("data", item); 
        beforeSelectItem( fact ); 
    }
    
    protected void afterSelectItem( Object fact ) {} 
    protected void afterSelectItem( Object item, boolean selected, int rowIndex ) {
        Map fact = new HashMap();
        fact.put("selected", selected); 
        fact.put("index", rowIndex); 
        fact.put("data", item); 
        afterSelectItem( fact ); 
    } 
    
    protected void beforeSelectionChange( Object o ) {} 
    protected void afterSelectionChange( Object o ) {} 
    protected Object buildSelectionKey( Object o ) { 
        String str = getMultiSelectFieldName(); 
        if ( str != null && str.trim().length() > 0) {
            String[] names = str.trim().split(",");
            if ( names.length == 0 ) return o; 
            
            PropertyResolver pr = PropertyResolver.getInstance(); 
            Map map = new HashMap();
            for ( String sname : names ) {
                sname = sname.trim(); 
                try { 
                    map.put(sname, pr.getProperty(o, sname)); 
                } catch(Throwable t) {
                    map.put(sname, null); 
                } 
            } 
            return map; 
        } else {
            return o; 
        }
    } 
    
    public final void fireBeforeSelectItem(Object item, boolean selected, int rowIndex) {
        beforeSelectItem(item, selected, rowIndex); 
    }
    public final void fireAfterSelectItem(Object item, boolean selected, int rowIndex) {
        afterSelectItem(item, selected, rowIndex); 
    } 

    // multi selection events 
    public void selectAll() {
        getMultiSelectionSupport().selectAll(); 
    }
    public void deselectAll() {
        getMultiSelectionSupport().deselectAll(); 
    }
    
    public boolean isColumnEditable(Object item, String columnName) { 
        return false;
    } 
        
    // <editor-fold defaultstate="collapsed" desc=" ListItem helper methods "> 
    
    public ListItemStatus createListItemStatus(ListItem oListItem) 
    { 
        ListItemStatus stat = new ListItemStatus(oListItem); 
        stat.setPageIndex(1);
        stat.setPageCount(1);
        stat.setIsLastPage(true);  
        stat.setHasNextPage(false); 
        stat.setTotalRows(totalRows); 
        return stat; 
    } 
    
    protected ListItem createCustomListItem() {
        return new ListItem();
    }
    
    public final ListItem createListItem() 
    { 
        ListItem li = createCustomListItem();
        li.addHandler(listItemHandler);
        return li;
    } 
    
    protected ListItem getFirstItem() 
    {
        try { 
            return itemList.get(0); 
        } catch(Exception ex) {
            return null; 
        }        
    }
    
    public boolean isFirstItem(ListItem li) 
    {
        if (li == null) return false; 
        
        return isFirstItem(li.getIndex()); 
    }
    
    public boolean isFirstItem(int index) { 
        return (index == 0); 
    } 
    
    protected ListItem getLastItem() 
    {
        try { 
            return itemList.get(itemList.size()-1); 
        } catch(Exception ex) {
            return null; 
        }
    }
    
    public boolean isLastItem(ListItem li) 
    {
        if (li == null) return false; 
        
        return isLastItem(li.getIndex()); 
    }
    
    public boolean isLastItem(int index) { 
        return (index >= 0 && index==itemList.size()-1); 
    } 
    
    public final void addEmptyItem() 
    {
        ListItem lastLI = getLastItem();
        int index = (lastLI == null? 0: lastLI.getIndex()+1);
        int rownum = (lastLI == null? 0: lastLI.getRownum()+1);
        ListItem li = createListItem(); 
        li.setIndex(index);
        li.setRownum(rownum);
        li.loadItem(null, ListItem.STATE_EMPTY); 
        itemList.add(li); 
        tableModelSupport.fireTableRowsInserted(index, index); 
    }
    
    protected final void buildListItems() 
    {
        boolean dynamic = (getRows() == -1);
        if (!dynamic && itemList.size() == getRows()) return;
        
        itemList.clear(); 
        int rowSize = getRows(); 
        if (dynamic) rowSize = getRowCount()+1;
        
        rowSize = Math.max(rowSize, 1); 
        for (int i=0; i<rowSize; i++) 
        {
            ListItem li = createListItem(); 
            li.setIndex(i); 
            li.setRownum(i);
            li.loadItem(null);             
            itemList.add(li);
        }
    }
    
    protected final void fillListItems(List list, int toprow) 
    {
        buildListItems();
        int dataSize = list.size();        
        int itemSize = itemList.size();
        for (int i=0; i<itemSize; i++) 
        {
            ListItem li = itemList.get(i);
            li.setIndex(i);
            li.setRownum(toprow+i);
            if ( i < dataSize ) 
            {
                Object item = list.get(i);
                li.loadItem( item );
                li.setState(ListItem.STATE_SYNC);
            } 
            else 
            {
                li.loadItem(null);
                li.setState(ListItem.STATE_EMPTY);
            }
        }
    } 
    
    public final void rebuildIndexes() 
    {
        for (int i=0; i<itemList.size(); i++) {
            itemList.get(i).setIndex(i); 
        }
    } 
    
    /*
     *  This is called by the ListSelectionSupport. You need to override this method 
     *  if you want to handle a custom routing check of an item. 
     */
    public boolean isItemSelected(Object item) { 
        ListSelectionSupport lss = getSelectionSupport();
        Object callback = getMultiSelectHandler();
        if ( callback != null ) {
            return lss.isItemCheckedFromHandler(callback, item); 
        }
        
        SelectionEvent se = new SelectionEvent( item, -1 );
        lss.resolveKey( se ); 
        int idx = lss.checkedItems.indexOf( se ); 
        se = (idx < 0 ? null : lss.checkedItems.get(idx)); 
        return (se == null ? false : se.isSelected()); 
    } 
    
    
    /*
    public boolean isSelected(Object item) {
        return checkedItems.contains(item);
    }    
    
    public Set getCheckedItems() { return checkedItems; }    
    public void checkItem( Object item, boolean checked ) 
    {   
        if (item == null) return;
        
        if (checked)
            checkedItems.add(item);
        else 
            checkedItems.remove(item);
    }*/   
    
    public void refreshSelectedItem() { 
        fireFocusSelectedItem(); 
    } 
    
    public void refreshItem( Object o ) { 
        refreshItem( o, -1 ); 
    }
    
    public void refreshItem( Object o, int colIndex ) { 
        if ( o == null ) return; 
        if ( getDataListSize() == 0 ) return; 
        
        if ( o instanceof Number ) {
            int idx = ((Number) o).intValue(); 
            Object data = getData(idx);
            if ( data == null ) return; 
            
            propertySupport.firePropertyChange("refreshItem", new Integer[]{ idx, colIndex }); 
        } else { 
            int idx = getDataList().indexOf( o ); 
            if ( idx < 0 ) return; 
            
            propertySupport.firePropertyChange("refreshItem", new Integer[]{ idx, colIndex });  
        } 
    }
    
    public final Object getSelectedValue() 
    {
        if (isMultiSelect()) 
            return getSelectionSupport().getSelectedValues(); 
            
        if (getSelectedItem() == null) 
            return null; 
        else
            return getSelectedItem().getItem(); 
    } 

    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" MessageSupport (Class) ">
    
    public class MessageSupport
    {
        private Map<ListItem, String> errors = new HashMap();  
        
        public boolean hasErrorMessages() {
            return !errors.isEmpty();
        }        
        
        public void addErrorMessage(int index, String message) 
        {
            ListItem li = getListItem(index);
            if (li == null || li.getItem() == null) return;

            errors.put(li.clone(), message);
        }

        public String getErrorMessage(int index) 
        {
            ListItem li = getListItem(index);
            if (li == null || li.getItem() == null) return null;

            return errors.get( li );
        }

        public void removeErrorMessage(int index) 
        {
            ListItem li = getListItem(index);
            if (li == null || li.getItem() == null) return;

            errors.remove( li );
        }

        public String getErrorMessages() 
        {
            if ( errors.isEmpty() ) return null;

            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Map.Entry<ListItem, String> me: errors.entrySet()) 
            {
                if ( !first ) sb.append("\n");
                else first = false; 
                
                sb.append("Row " + (me.getKey().getRownum()+1) + ": " + me.getValue());
            }
            return sb.toString();
        }         
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" ListItemHandler (Class) ">    
    
    private class ListItemHandler implements ListItem.Handler
    {
        public void setSelected(ListItem li, boolean selected) {
        }

        public void replaceSelectedItem(ListItem li, Object newData) {
        }

        public void refreshItemUpdated(ListItem li) {
        }        
    }
    
    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" ListSelectionSupport (Class) ">    
    
    public class SelectionEvent { 
        private Object key; 
        private Object data;
        private boolean selected;
        private int index;
        
        public SelectionEvent( Object data, int index ) {
            this( data, index, false ); 
        } 
        
        public SelectionEvent( Object data, int index, boolean selected ) {
            this.data = data; 
            this.index = index; 
            this.selected = selected;
        }
        
        public boolean isSelected() { return selected; } 
        public Object getKey() { return key; }
        public Object getData() { return data; } 
        public int getIndex() { return index; } 

        public boolean equals(Object o) {
            if ( super.equals(o)) return true; 
 
            boolean matched = false; 
            if ( o instanceof SelectionEvent ) {
                matched = isEquals(key, ((SelectionEvent) o).key); 
            } else {
                matched = isEquals(key, o); 
            }
            return matched; 
        }
        
        private boolean isEquals(Object o1, Object o2) {
            if ( o1 == null || o2 == null ) return true; 
            return (o1 != null && o2 != null && o1.equals(o2)); 
        }
    } 
    
    public class ListSelectionSupport {
        AbstractListDataProvider root = AbstractListDataProvider.this; 
        
        private ArrayList<SelectionEvent> checkedItems; 
        private CallbackHandlerProxy callbackProxy;
        
        public ListSelectionSupport() {
            checkedItems = new ArrayList();
            callbackProxy = new CallbackHandlerProxy(null); 
        }
        
        protected void finalize() throws Throwable { 
            checkedItems.clear(); 
        } 
        
        void resolveKey( SelectionEvent se ) {
            if ( se == null || se.key != null ) return;
            else if ( se.getData() == null ) return; 
            
            se.key = root.buildSelectionKey( se.getData() ); 
        }
        
        public boolean contains( Object data ) { 
            return contains( new SelectionEvent( data, -1 ) ); 
        }
        public boolean contains( SelectionEvent se ) { 
            if (se == null || se.getData() == null) { 
                return false; 
            } 
            
            resolveKey( se );  
            return checkedItems.contains( se ); 
        } 
                
        public boolean isItemChecked( Object data ) {
            return isItemChecked( new SelectionEvent( data, -1 )); 
        }
        public boolean isItemChecked( SelectionEvent se ) {
            if (se == null || se.getData() == null) { 
                return false; 
            } 

            resolveKey( se ); 
            
            boolean checked = false; 
            try {
                checked = root.isItemSelected( se.getData() );  
            } catch(Throwable t) { 
                t.printStackTrace(); 
            } 
            
            checkedItems.remove( se );
            if ( checked ) { 
                checkedItems.add( se ); 
                se.selected = checked;                 
            } 
            return checked; 
        }
        
        public boolean isItemCheckedFromHandler(Object itemData) {
            Object callback = root.getMultiSelectHandler();
            return isItemCheckedFromHandler(callback, itemData); 
        }  
        
        public boolean isItemCheckedFromHandler(Object callback, Object itemData) {
            try {
                if (callback == null) return false;
                
                Object res = callbackProxy.invoke(callback, itemData); 
                if (res instanceof Boolean) { 
                    return ((Boolean) res).booleanValue(); 
                } else { 
                    return "true".equals(res+""); 
                } 
            } catch(Throwable ex) {
                if (ClientContext.getCurrentContext().isDebugMode()) {
                    ex.printStackTrace(); 
                } 
                return false; 
            } 
        } 
        
        public synchronized void setItemChecked( Object data, boolean checked ) { 
            setItemChecked( data, checked, -1 ); 
        } 
        public synchronized void setItemChecked( Object data, boolean checked, int index ) { 
            setItemChecked( new SelectionEvent( data, index), checked ); 
        } 
        public synchronized void setItemChecked( SelectionEvent se, boolean checked ) { 
            setItemChecked( se, checked, true ); 
        } 
        public synchronized void setItemChecked( SelectionEvent se, boolean checked, boolean withEvents ) { 
            if ( se == null || se.getData() == null ) return; 

            resolveKey(se); 
            se.selected = checked; 
            
            if ( withEvents && se.getIndex() >= 0 ) { 
                try { 
                    beforeSelectionChange( se ); 
                } catch(BreakException be) { 
                    return; 
                } 
            } 
            
            checkedItems.remove( se ); 
            if ( checked ) { 
                checkedItems.add( se ); 
            } 
            
            if ( withEvents && se.getIndex() >= 0 ) { 
                try { 
                    afterSelectionChange( se ); 
                } catch(Throwable t) {;} 
            } 
        } 
        
        public List getSelectedValues() {
            List list = new ArrayList(); 
            SelectionEvent[] arr = checkedItems.toArray(new SelectionEvent[]{}); 
            for (int i=0; i<arr.length; i++) { 
                SelectionEvent se = arr[i]; 
                if ( se.isSelected() ) { 
                    list.add( se.getData() ); 
                }
            } 
            return list; 
        } 
        
        public Object getSelectedValue() {
            List values = getSelectedValues(); 
            if (values == null || values.isEmpty()) { 
                return null; 
            } else {
                return values.get(0); 
            }
        }
        
        public boolean hasSelections() {
            return (checkedItems.isEmpty() ? false : true); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" UIProvider interface and proxy methods ">        

    public static interface UIProvider {
        Object getBinding(); 
        Column getSelectedColumn(); 
    } 
    
    
    private UIProvider _uiprovider; 
    
    public void setUIProvider(UIProvider uiprovider) { 
        this._uiprovider = uiprovider; 
    }
    
    public final Object getBindingObject() {
        return (_uiprovider == null? null: _uiprovider.getBinding()); 
    }
    
    public final Column getSelectedColumnObject() {
        return (_uiprovider == null? null: _uiprovider.getSelectedColumn()); 
    }
    
    public final Map getSelectedColumnProperties() {
        Column column = getSelectedColumnObject(); 
        if (column == null) return null;
        
        Map map = new HashMap();
        map.putAll(column.getProperties());
        if (column.getName() != null) map.put("name", column.getName());
        if (column.getCaption() != null) map.put("caption", column.getCaption());
        if (column.getExpression() != null) map.put("expression", column.getExpression());
        if (column.getType() != null) map.put("type", column.getType());
        if (column.getEditableWhen() != null) map.put("editableWhen", column.getEditableWhen());
        if (column.getVisibleWhen() != null) map.put("visibleWhen", column.getVisibleWhen());
        
        TypeHandler thandler = column.getTypeHandler();
        if (thandler instanceof LookupColumnHandler) {
            LookupColumnHandler hnd = (LookupColumnHandler)thandler; 
            if (hnd.getExpression() != null) map.put("expression", hnd.getExpression());
            if (hnd.getHandler() != null) map.put("handler", hnd.getHandler());
            
        } else if (thandler instanceof OpenerColumnHandler) {
            OpenerColumnHandler hnd = (OpenerColumnHandler)thandler; 
            if (hnd.getExpression() != null) map.put("expression", hnd.getExpression());
            if (hnd.getHandler() != null) map.put("handler", hnd.getHandler());
            
        } else if (thandler instanceof ComboBoxColumnHandler) {
            ComboBoxColumnHandler hnd = (ComboBoxColumnHandler)thandler; 
            if (hnd.getExpression() != null) map.put("expression", hnd.getExpression());
            if (hnd.getItemKey() != null) map.put("itemKey", hnd.getItemKey());
            if (hnd.getItems() != null) map.put("items", hnd.getItems());
        }        
        return map;
    }    
    
    // </editor-fold>
    
}
