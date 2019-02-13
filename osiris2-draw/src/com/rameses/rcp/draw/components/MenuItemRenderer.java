package com.rameses.rcp.draw.components;

import java.awt.Graphics;


public abstract class MenuItemRenderer {
    protected AttributeMenuItem menuItem;
    
    public MenuItemRenderer(AttributeMenuItem menuItem){
        this.menuItem = menuItem;
    }
    
    public abstract void render(Graphics g);
}
