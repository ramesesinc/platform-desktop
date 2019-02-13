package com.rameses.rcp.control.table;

import com.rameses.rcp.common.AbstractListDataProvider;
import com.rameses.rcp.common.MsgBox;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollBar;


public class ListScrollBar extends JScrollBar implements AdjustmentListener 
{
    private AbstractListDataProvider dataProvider;
    private boolean visibleAlways;
        
    public ListScrollBar() 
    {
        super.setVisibleAmount(0);
        super.setMinimum(0);
        super.setMaximum(0);
        super.setVisible(visibleAlways);
        
        addMouseWheelListener(new MouseWheelListener() 
        {
            public void mouseWheelMoved(MouseWheelEvent e) 
            {
                int rotation = e.getWheelRotation();
                if (rotation == 0) return;
                if (dataProvider == null) return;
                
                if ( rotation < 0 )
                    dataProvider.moveBackRecord();
                else
                    dataProvider.moveNextRecord(true);
            }
        });
    }
    
    public void setDataProvider(AbstractListDataProvider dataProvider) {
        this.dataProvider = dataProvider; 
    }
    
    public void adjustValues() 
    {
        super.removeAdjustmentListener(this);
        super.setValue(dataProvider.getTopRow());
        super.setMaximum(dataProvider.getMaxRows());
        super.setMinimum(0);
        super.setVisibleAmount(0);
        
        if ( !visibleAlways ) 
        {
            int rowCount = dataProvider.getRowCount();            
            int rows = dataProvider.getRows();
            if (rows == -1) rows = rowCount;
            
            if (rowCount > rows) 
            {
                super.setVisible(true);
                super.firePropertyChange("visible", false, true);
            } 
            else 
            {
                super.setVisible(false);
                super.firePropertyChange("visible", true, false);
            }
        }         
        super.addAdjustmentListener(this);
    }
    
    public void adjustmentValueChanged(AdjustmentEvent e) 
    {
        try {
            dataProvider.setTopRow(e.getValue());
        } catch(Exception ex) {
            MsgBox.err(ex); 
        }
    }

    public boolean isVisibleAlways() {
        return visibleAlways;
    }

    public void setVisibleAlways(boolean visibleAlways) {
        this.visibleAlways = visibleAlways;
        
        boolean oldValue = super.isVisible();
        super.setVisible( visibleAlways || super.isVisible() );
        super.firePropertyChange("visible", oldValue, super.isVisible());
        revalidate();
    }
    
    public final boolean isDynamicallyVisible() {
        if (dataProvider == null) return false;
        
        int rowCount = dataProvider.getRowCount();            
        int rows = dataProvider.getRows();
        if (rows == -1) rows = rowCount;

        return (rowCount > rows);
    }
}
