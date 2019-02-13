package com.rameses.rcp.common;

import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.components.DrawComponentModel;
import com.rameses.rcp.draw.interfaces.Connector;
import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;


public class DrawModel {
    private DrawComponentModel componentModel;
    private Editor editor;
    private boolean readonly = false;
    
    
    public void setComponentModel(DrawComponentModel componentModel){
        this.componentModel = componentModel;
    }
    
    public Editor getEditor(){
        return editor;
    }
    
    public void setEditor(Editor editor){
        this.editor = editor;
    }
    
    public Object fetchCategories(){
        return null;
    }
    
    public Object fetchData(Object o){
        return null;
    }
    
    public Object open(Object o){
        return null;
    }
    
    public List showMenu(Object o){
        return new ArrayList();
    }

    public DrawComponentModel getComponentModel() {
        return componentModel;
    }

    public void onAddItem(Object o){
    }
    
    public boolean beforeRemove(Object o){
        return true;
    }
    
    public void afterRemove(Object deletedItems){
    }
    
    public void connectionChanged(Connector c, Figure fromFigure, Figure toFigure) {
    }
    
    public List<Figure> getFigures(){
        return editor.getDrawing().getFigures();
    }
    
    public void reload(){
        editor.getDrawing().clearFigures();
        editor.setReadonly(isReadonly());
        editor.loadDrawing(this);
        refresh();
    }
    
    public void refresh(){
        editor.setReadonly(isReadonly());
        editor.getCanvas().refresh();
        editor.setDefaultTool();
        componentModel.getBinding().refresh();
        componentModel.getCallerBinding().refresh();
    }
    
    public boolean showCategories() {
        return true;
    }
    
    public boolean isReadonly(){
        return readonly;
    }
    
    public final void setReadonly(boolean readonly){
        this.readonly = readonly;
        if (getEditor() != null){
            getEditor().setReadonly(readonly);
            refresh();
        }
    }
    
    public boolean showHandles(){
        return true;
    }
    
    public boolean showToolbars(){
        return true;
    }
    
    public boolean showDrawTools(){
        return true;
    }
    
    public boolean showEditTools(){
        return true;
    }
    
    public String getXml(){
        return getEditor().getDrawing().getXml();
    }

    public Map getData(){
        if (getEditor() != null){
            return getEditor().getDrawing().getData();
        }
        return new HashMap();
    }
    
    public Image getImage(){
        return getImage(false);
    }
    
    public Image getImage(boolean crop){
        return getImage(true, "PNG");
    }
    
    public Image getImage(boolean crop, String imageType){
        return getEditor().getImage(crop, imageType);
    }


}
