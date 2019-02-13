/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control.image;

import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author wflores 
 */
public class ThumbnailItem {
    
    private Map data;
    private String caption;
    private ImageIcon icon;

    public Map getData() { return data; } 
    public void setData( Map data ) {
        this.data = data; 
    }
    
    public ImageIcon getIcon() { return icon; } 
    public void setIcon( ImageIcon icon ) {
        this.icon = icon;
    }
    
    public String getCaption() { return caption; } 
    public void setCaption( String caption ) {
        this.caption = caption;
    }
}
