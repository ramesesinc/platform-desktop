package com.rameses.rcp.draw.components;

import com.rameses.osiris2.client.Inv;
import com.rameses.osiris2.client.InvokerAction;
import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.ComponentBean;
import com.rameses.rcp.common.DrawModel;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.draw.DrawingEditor;
import com.rameses.rcp.draw.StdDrawing;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.EditorListener;
import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.rcp.draw.actions.FigureAction;
import com.rameses.rcp.draw.actions.FontSelectionAction;
import com.rameses.rcp.draw.actions.SelectionAction;
import com.rameses.rcp.draw.figures.FigureCache;
import com.rameses.rcp.draw.support.AttributeKeys;
import com.rameses.rcp.draw.actions.AttributePickerAction;
import com.rameses.rcp.draw.actions.CenterCaptionAction;
import com.rameses.rcp.draw.actions.ReIndexAction;
import com.rameses.rcp.draw.actions.ShowIndexAction;
import com.rameses.rcp.draw.interfaces.Connector;
import java.util.ArrayList;
import java.util.List;


public class DrawComponentModel extends ComponentBean implements EditorListener {
    private String category;
    private Editor editor;
    private Drawing drawing;
    private DrawModel handler;

    public DrawComponentModel() {
        editor = new DrawingEditor();
        editor.setDrawing(createDrawing());
        editor.addListener(this);
    }

    public Editor getEditor() {
        return editor;
    }
    
    public Drawing getDrawing(){
        return drawing;
    }
    
    public void setDrawing(Drawing drawing){
        this.drawing = drawing;
    }
    
    public DrawModel getHandler(){
        return handler;
    }

    public void setHandler(DrawModel handler) {
        this.handler = handler;
        if (handler != null) {
            this.handler.setComponentModel(this);
            this.handler.setEditor(editor);
            this.handler.reload();
        }
    }
    
    public List getDrawTools() {
        List actions = new ArrayList();
        if (category != null) {
            for(Figure f : FigureCache.getInstance().getFigures(category)){
                actions.add( createFigureAction(f));
            }
        }
        return actions;
    }
    
    
    public List getEditTools() {
        List actions = new ArrayList();
        actions.add(new SelectionAction(editor));
        actions.add(new FontSelectionAction(editor));
        actions.add(new AttributePickerAction(editor, AttributeKeys.FILL_COLOR, "Fill Color", "images/draw/shapefill16.png"));
        actions.add(new AttributePickerAction(editor, AttributeKeys.STROKE_COLOR, "Stroke Color", "images/draw/strokecolor16.png"));
        actions.add(new ShowIndexAction(editor));
        actions.add(new ReIndexAction(editor));
        actions.add(new CenterCaptionAction(editor));
        return actions;
    }    
    

    public List getCategories() {
        List list = new ArrayList();
        if (handler != null){
            Object o = handler.fetchCategories();
             if (o instanceof List){
                 list = (List)o;
             }else if (o instanceof String){
                 String regex = o.toString();
                 list = FigureCache.getInstance().getCategories(regex);
             }
        }
        if (list == null || list.isEmpty()){
            list = FigureCache.getInstance().getCategories();
        }
        return list;
    }
    
    public String getCategory(){
        return category;
    }
    
    public void setCategory(String category){
        this.category = category;
    }
    
    public boolean isShowcategories(){
        boolean show = true;
        if (handler != null){
            show = handler.showCategories();
        }else if (getCategories().size() == 1){
            show = false;
        }
        return show;
    }
    
    private Drawing createDrawing() {
        return new StdDrawing();
    }

    private Action createFigureAction(Figure figure) {
        FigureAction action = new FigureAction(editor, figure);
        action.setIcon(figure.getIcon());
        if (action.getIcon() != null) {
            action.setTooltip(figure.getToolCaption());
        } else {
            action.setCaption(figure.getToolCaption());
        }
        return action;
    }
    
    
    
    /*==========================================================
     * Listeners
     ==========================================================*/
    
    @Override
    public void openFigure(Figure figure){
        if (handler == null) {
            return;
        }
        
        Object val = handler.open(figure);
        if (val instanceof Opener){
            Inv.invoke((Opener)val);
        }else if (val instanceof InvokerAction){
            Inv.invokeAction((InvokerAction)val);
        }else if (val instanceof Action){
            ((Action)val).execute();
        }else if (val instanceof String){
            super.getBinding().fireNavigation(val.toString());
        }
    }
    
    @Override
    public void figureAdded(Figure figure){
        if (handler != null){
            handler.onAddItem(figure);
        }
    }
    
    @Override
    public boolean beforeRemoveFigures(List<Figure> figures){
        if (handler != null){
            try{
                return handler.beforeRemove(figures);
            }
            catch(Exception ex){
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void afterRemoveFigures(List<Figure> deletedItems){
        if (handler != null){
            handler.afterRemove(deletedItems);
        }
    }

    @Override
    public void connectionChanged(Connector c, Figure fromFigure, Figure toFigure) {
        if (handler != null){
            handler.connectionChanged(c, fromFigure, toFigure);
        }
    }
    
    
    
    
    @Override
    public List showMenu(Figure figure){
        List menus = new ArrayList();
        if (handler != null){
            List items = handler.showMenu(figure);
            if (!items.isEmpty()){
                menus.addAll(items);
            }
        }
        return menus;
    }
    
    
    public boolean isShowToolbars(){
        if (handler.isReadonly()){
            return false;
        }
        
        if (handler != null){
            return handler.showToolbars();
        }
        return true;
    }
    
    public boolean isShowDrawTools(){
        if (handler.isReadonly()){
            return false;
        }
        
        if (handler != null){
            return handler.showDrawTools();
        }
        return true;
    }
    
    public boolean isShowEditTools(){
        if (handler.isReadonly()){
            return false;
        }
        
        if (handler != null){
            return handler.showEditTools();
        }
        return true;
    }
}
