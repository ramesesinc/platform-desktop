package com.rameses.rcp.framework;

import com.rameses.rcp.ui.ControlContainer;
import com.rameses.rcp.ui.UIControl;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.Beans;
import javax.swing.JPanel;

/**
 *
 * @author jaycverg
 */
public class UIViewPanel extends JPanel implements ContainerListener {
    
    protected Binding binding;
    
    
    public UIViewPanel() {
        super();
        super.setOpaque(false);
        super.setLayout(new BorderLayout());
        this.binding = new Binding(this);
        
        if( !Beans.isDesignTime()) {
            initComponents();
        }
    }
    
    public void setLayout(LayoutManager mgr) {;}
    
    private void initComponents() {
        addContainerListener(this);
    }
    
    
    public void bindComponents( Container cont ) {
        for( Component c: cont.getComponents()) {
            if( c instanceof UIControl ) {
                UIControl uic = (UIControl)c;
                binding.bind( uic ); 
                binding.register(uic);
                
                if( c instanceof ControlContainer && ((ControlContainer) c).isHasNonDynamicContents() && c instanceof Container )
                    bindComponents( (Container)c);
                
            } else if( c instanceof Container ) {
                bindComponents( (Container)c);
            }
        }
    }
    
    public void componentAdded(ContainerEvent e) {
        Component comp = e.getChild();
        if( comp instanceof UIControl ) {
            UIControl uic = (UIControl)comp;
            binding.bind( uic ); 
            binding.register(uic);
            
            if( comp instanceof ControlContainer && ((ControlContainer) comp).isHasNonDynamicContents() && comp instanceof Container )
                bindComponents( (Container)comp);
            
        } else if( comp instanceof Container ) {
            bindComponents( (Container)comp );
        }
    }
    
    public void componentRemoved(ContainerEvent e) {
        Component comp = e.getChild();
        if( comp instanceof UIControl ) {
            UIControl c = (UIControl)comp;
            c.setBinding(null);
            binding.unregister(c);
        }
    }
    
    public Binding getBinding() {
        return binding;
    }
    
    public void refresh() { 
        binding.refresh(); 
    }
        
    // <editor-fold defaultstate="collapsed" desc=" CustomLayout ">

    private class CustomLayout implements LayoutManager {
        
        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        private Dimension getLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) 
            {
                Insets margin = parent.getInsets(); 
                int w = margin.left + margin.right;
                int h = 0;
                Component[] comps = parent.getComponents();
                for (int i=0; i<comps.length; i++) {
                    Dimension dim = comps[i].getPreferredSize(); 
                    h = Math.max(h, dim.height); 
                }
                h += (margin.top + margin.bottom);
                return new Dimension(w,h); 
            }
        }
        
        public Dimension preferredLayoutSize(Container parent) {
            return getLayoutSize(parent); 
        }

        public Dimension minimumLayoutSize(Container parent) {
            return getLayoutSize(parent); 
        }

        public void layoutContainer(Container parent) 
        {
            synchronized (parent.getTreeLock()) 
            {
                Insets margin = parent.getInsets(); 
            }
        }        
    } 
    
    // </editor-fold>
}
