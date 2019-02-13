package com.rameses.rcp.draw.components;

import com.rameses.common.MethodResolver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

class MenuItem extends JMenuItem {
    private AttributePickerModel model;
    private final com.rameses.rcp.common.ComponentBean bean;
    private final String methodName;
    
    public MenuItem(String text, final String methodName, final com.rameses.rcp.common.ComponentBean bean, AttributePickerModel model){
        super(text);
        this.methodName = methodName;
        this.bean = bean;
        this.model = model;
        
        addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Object outcome = MethodResolver.getInstance().invoke(bean, methodName, new Object[]{});
                    bean.getCallerBinding().fireNavigation(outcome);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }
            
        });
    }    
}