/*
 * TextAlignmentSupport.java
 *
 * Created on May 2, 2013, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.support;

import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 *
 * @author wflores
 */
public class ComponentSupport 
{
    private Map<String,int[]> alignment_options = new HashMap<String,int[]>();
    
    public ComponentSupport() 
    {
        alignment_options.put("CENTER", new int[]{ SwingConstants.CENTER, SwingConstants.CENTER });            
        alignment_options.put("LEFT", new int[]{ SwingConstants.CENTER, SwingConstants.LEFT });
        alignment_options.put("RIGHT", new int[]{ SwingConstants.CENTER, SwingConstants.RIGHT }); 
        alignment_options.put("TOP", new int[]{ SwingConstants.TOP, SwingConstants.LEFT });
        alignment_options.put("TOP_LEFT", new int[]{ SwingConstants.TOP, SwingConstants.LEFT });
        alignment_options.put("TOP_CENTER", new int[]{ SwingConstants.TOP, SwingConstants.CENTER });
        alignment_options.put("TOP_RIGHT", new int[]{ SwingConstants.TOP, SwingConstants.RIGHT});
        alignment_options.put("BOTTOM", new int[]{ SwingConstants.BOTTOM, SwingConstants.LEFT });
        alignment_options.put("BOTTOM_LEFT", new int[]{ SwingConstants.BOTTOM, SwingConstants.LEFT });
        alignment_options.put("BOTTOM_CENTER", new int[]{ SwingConstants.BOTTOM, SwingConstants.CENTER });
        alignment_options.put("BOTTOM_RIGHT", new int[]{ SwingConstants.BOTTOM, SwingConstants.RIGHT });        
    }
    
    public void alignText(JComponent comp, String textAlignment) 
    {
        if (comp == null || textAlignment == null) return;
        
        int[] values = alignment_options.get(textAlignment.toUpperCase()); 
        if (values == null) return;
        
        if (comp instanceof AbstractButton) {
            AbstractButton jc = (AbstractButton) comp; 
            jc.setVerticalAlignment(values[0]);
            jc.setHorizontalAlignment(values[1]); 
            
        } else if (comp instanceof JLabel) {
            JLabel jc = (JLabel) comp;
            jc.setVerticalAlignment(values[0]);
            jc.setHorizontalAlignment(values[1]); 
            
        } else if (comp instanceof JTextField) {
            JTextField jc = (JTextField) comp; 
            jc.setHorizontalAlignment(values[1]); 
        } 
    }

    public void alignTextPosition(JComponent comp, String textPosition) 
    {
        if (comp == null) return;
        
        int[] values = alignment_options.get(textPosition.toUpperCase()); 
        if (values == null) return;
        
        if (comp instanceof AbstractButton)
        {
            AbstractButton jc = (AbstractButton) comp; 
            jc.setVerticalTextPosition(values[0]);
            jc.setHorizontalTextPosition(values[1]); 
        }
        else if (comp instanceof JLabel)
        {
            JLabel jc = (JLabel) comp;
            jc.setVerticalTextPosition(values[0]);
            jc.setHorizontalTextPosition(values[1]); 
        } 
    }
    
    public Border createEmptyBorder(Insets margin) 
    {
        if (margin == null) margin = new Insets(0, 0, 0, 0);
        
        return BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right);
    }
    
    public void setEmptyBorder(JComponent comp, Insets margin) 
    {
        if (comp != null) comp.setBorder(createEmptyBorder(margin)); 
    }
}
