package com.rameses.rcp.draw.components;

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.Inv;
import com.rameses.rcp.draw.utils.DrawUtil;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import java.awt.Font;
import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.Color
import java.awt.Image
import java.awt.image.RenderedImage
import javax.swing.JFileChooser

public class FontPickerComponentModel extends ComponentBean {
    def model;
    def entity;
    def currentFont;
    boolean bold;
    boolean italic;
    boolean underlined;
    
    def fontSizes = [7,8,9,10,11,12,14,16,20,24,28,32,36,40,44,48,54,60,66,72];
    
    
    @PropertyChangeListener
    def listener = [
        'entity.(family|size|color)':{
            updateFont();
        }
    ]
    
    void init(){
        entity = [:];
        
        bold = false;
        italic = false;
        underlined = false;
        
        def figure = model.editor.drawing.selections.first();
        currentFont = figure.get(FONT_FACE);
        entity.family = currentFont.family;
        entity.size = currentFont.size;
        entity.rotation = figure.get(ROTATION_ANGLE);
        entity.color = figure.get(TEXT_COLOR);
        
        bold = currentFont.isBold();
        italic = currentFont.isItalic();
    }
    
    public def getRotation(){
        return entity.rotation;
    }
    
    
    public void setModel(model){
        this.model = model;
        init();
    }
    
    public def getColor(){
        return entity.color;
    }
    
    
    void bold(){
        bold = !bold;
        updateFont();
    }
    
    void italic(){
        italic = !italic;
        updateFont();
    }
    
    void underline(){
        underlined = !underlined;
        updateFont();
    }
    
    
    public void black(){
        update(Color.BLACK);
    }
    
    public void blue(){
        update(Color.BLUE);
    }
    
    public void cyan(){
        update(Color.CYAN);
    }
    
    public void gray(){
        update(Color.GRAY);
    }
    
    public void green(){
        update(Color.GREEN);
    }
    
    public void lightgray(){
        update(Color.LIGHT_GRAY);
    }
    
    public void magenta(){
        update(Color.MAGENTA);
    }
    
    public void orange(){
        update(Color.ORANGE);
    }
    
    public void pink(){
        update(Color.PINK);
    }
    
    public void red(){
        update(Color.RED);
    }
    
    public void white(){
        update(Color.WHITE);
    }
    
    public void yellow(){
        update(Color.YELLOW);
    }
    
    private void update(Color color){
        model.getEditor().attributeChanged(model.getAttributeKey(), color);
    }    
    
    def getFontFamilies(){
        return DrawUtil.getFontFamilies();
    }
    
    void updateFont(){
        if (entity.family == null){
            return;
        }
        
        int style = Font.PLAIN;
        if (bold){
            style = style | Font.BOLD;
        }
        if (italic){
            style = style | Font.ITALIC;
        }
        
        def family = (entity.family ? entity.family : currentFont.family);
        def size = (entity.size ? entity.size : currentFont.size);
        def newfont = new Font(family, style, size);
            
        model.editor.attributeChanged(FONT_FACE, newfont);
    }
    
    def showColorChoosers(){
        def colorModel = new AttributePickerModel(model.editor, TEXT_COLOR);
        return Inv.lookupOpener("colorchooser",  [model:colorModel]);
    }
    
    
}