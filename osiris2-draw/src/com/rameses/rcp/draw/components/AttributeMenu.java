package com.rameses.rcp.draw.components;

import com.rameses.rcp.control.menu.VMenu;

public class AttributeMenu extends VMenu {
    private final AttributePickerModel model;
    private final com.rameses.rcp.common.ComponentBean bean;

    public AttributeMenu(String text, final com.rameses.rcp.common.ComponentBean bean, final AttributePickerModel model) {
        super(text);
        setEnabled(true);
        this.bean = bean;
        this.model = model;
    }
}
