package com.rameses.rcp.control;

import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 *
 * @author jaycverg
 */
public class XInfo extends JButton implements UIControl, ActionListener 
{
    private Binding binding;
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen;
    
    public XInfo() {
        setOpaque(false);
        setText("Info");
        addActionListener(this); 
    }
    
    public void refresh() { 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        }
    }
    
    public void load() {}
    
    
    public void actionPerformed(ActionEvent e) {
        MsgBox.alert(binding.getController().getInfo()); 
    }

    public String[] getDepends() {
        return null;
    }

    public int getIndex() {
        return 0;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public Binding getBinding() {
        return binding;
    }

    public int compareTo(Object o) {
        return 0;
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }     
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }    
}
