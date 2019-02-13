package com.rameses.rcp.framework;

import com.rameses.platform.interfaces.ContentPane;
import com.rameses.platform.interfaces.SubWindow;
import com.rameses.platform.interfaces.SubWindowListener;
import com.rameses.platform.interfaces.ViewContext;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.control.layout.CenterLayout;
import com.rameses.rcp.control.panel.DisabledPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author jaycverg
 */
public class UIControllerPanel extends JPanel 
    implements NavigatablePanel, ViewContext, ContentPane.View, ContentLayer  
{    
    private boolean defaultBtnAdded;
    private InnerPanel contentPane;
    private Stack<UIControllerContext> controllers;
    
    private SubWindow parent;
    private String id;
    
    public UIControllerPanel() {
        initComponent();
    } 
    
    public UIControllerPanel(UIControllerContext controller) {
        initComponent();
        controllers.push( controller ); 
        _build(); 
    }
    
    // <editor-fold defaultstate="collapsed" desc=" initComponent ">
    
    private void initComponent() { 
        controllers = new Stack();
        super.setLayout(new BorderLayout());
        setName("root");
        
        //attach the default button when this panel is already
        //attached to its rootpane
        addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {
                if ( getDefaultButton() != null && !defaultBtnAdded ) {
                    attachDefaultButton();
                }
            }
            
            public void ancestorMoved(AncestorEvent event) {}
            public void ancestorRemoved(AncestorEvent event) {}
        }); 
        
        contentPane = new InnerPanel(); 
        add( contentPane ); 
    } 
    
    public final void setLayout(LayoutManager mgr) {;}

    public Component add(Component comp) { 
        if ( comp instanceof InnerPanel ) { 
            return super.add(comp); 
        } 
        
        contentPane.add( comp );
        return comp;
    }

    public void add(Component comp, Object constraints) {
        if ( comp instanceof InnerPanel ) { 
            super.add( comp, constraints ); 
        } else { 
            contentPane.add( comp, constraints ); 
        }
    }

    public Component add(Component comp, int index) {
        if ( comp instanceof InnerPanel ) { 
            return super.add(comp, index);
        } 
        
        contentPane.add(comp, index); 
        return comp;
    }

    public Component add(String name, Component comp) {
        if ( comp instanceof InnerPanel ) { 
            return super.add(name, comp); 
        }
        
        contentPane.add( name, comp ); 
        return comp;
    }

    public void add(Component comp, Object constraints, int index) {
        if ( comp instanceof InnerPanel ) { 
            super.add(comp, constraints, index); 
        }
        
        contentPane.add( comp, constraints, index ); 
    }

    protected void addImpl(Component comp, Object constraints, int index) { 
        if ( comp instanceof InnerPanel ) { 
            removeAll(); 
            super.addImpl(comp, constraints, index); 
        } 
    } 
    
    // </editor-fold> 
    
    //visible in the package
    void attachDefaultButton() {
        JRootPane rp = getRootPane();
        JButton btn = getDefaultButton();
        if ( btn != null && rp != null && rp.getDefaultButton() != btn ) {
            rp.setDefaultButton( btn );
            defaultBtnAdded = true;
        } else {
            defaultBtnAdded = false;
        }
    }
    
    private JButton getDefaultButton() {
        UIControllerContext current = getCurrentController();
        if ( current == null ) return null;
        if ( current.getCurrentView() == null ) return null;
        
        return current.getCurrentView().getBinding().getDefaultButton();
    }
    
    private void _build() {
        UIControllerContext current = getCurrentController();
        contentPane.removeAll();

        UIViewPanel view = null; 
        if ( current != null ) {
            view = current.getCurrentView();
            Binding binding = view.getBinding();
            binding.setViewContext( this ); 
            binding.setController( current.getController()); 
            
            Object viewname = view.getClientProperty("View.name"); 
            boolean activated = "true".equals(view.getClientProperty("UIViewPanel.activated")+"");
            if (!activated) {
                binding.fireActivatePage( viewname ); 
                view.putClientProperty("UIViewPanel.activated", "true"); 
            } 
                        
            contentPane.add( view ); 
            view.requestFocusInWindow(); 
            view.refresh(); 
            binding.focusFirstInput(); 
            binding.fireAfterRefresh( viewname ); 
        } 
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    public Stack<UIControllerContext> getControllers() {
        return controllers;
    }
    
    public void setControllers(Stack<UIControllerContext> controllers) {
        this.controllers = controllers;
        _build();
    }
    
    public UIControllerContext getCurrentController() {
        if ( !controllers.empty() ) {
            return (UIControllerContext) controllers.peek();
        }
        return null;
    }
    
    public void renderView() {
        _build();
    }
    
    public boolean close() {
        try {
            return getCurrentController().getCurrentView().getBinding().close();
        } catch(Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    
    public void display() {
        UIControllerContext current = getCurrentController();
        if ( current != null ) {
            UIViewPanel p = current.getCurrentView();
            p.getBinding().focusFirstInput();
        }
    }
    
    public void setSubWindow(SubWindow subWindow) {
        this.parent = subWindow;
    }
    
    public SubWindow getSubWindow() {
        return parent;
    }  
    
    public void activate() {
        try {
            getCurrentController().getCurrentView().getBinding().activate(); 
        } catch( Throwable t ) {
            t.printStackTrace();
        }
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" ContentPane.View ">
    
    public Map getInfo() {
        UIControllerContext current = getCurrentController(); 
        if (current == null) return null;
        
        UIController uic = current.getController(); 
        return (uic == null? null: uic.getInfo()); 
    }
    
    public void showInfo() {
        UIControllerContext current = getCurrentController(); 
        if (current == null) return;
        
        Map info = getInfo();
        if (info == null || info.isEmpty()) return;
        
        OpenerProvider op = ClientContext.getCurrentContext().getOpenerProvider(); 
        if (op == null) return;
        
        Map params = new HashMap(); 
        params.put("info", info); 
        Opener opener = null; 
        try { 
            opener = op.lookupOpener("workunit-info:show", params);
            opener.setTarget("popup"); 
        } catch(Throwable t) {;} 
        
        if (opener == null) return;
            
        UIViewPanel uiv = current.getCurrentView();
        Binding binding = (uiv == null? null: uiv.getBinding());
        if (binding == null) return;
        
        binding.fireNavigation(opener); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ContentLayer ">

    public void show(UIControllerPanel uic, Map props) { 
        ModalView view = new ModalView( uic, props );
        uic.putClientProperty(SubWindow.class, view); 
        contentPane.addView( view ); 
        contentPane.refresh();
    }

    // </editor-fold>     
    
    // <editor-fold defaultstate="collapsed" desc=" InnerPanel ">

    private class InnerPanel extends JLayeredPane {
        
        public InnerPanel() {
            super.setLayout(new InnerLayout()); 
        }
        
        public final void setLayout(LayoutManager mgr) {
        }

        public Component add(Component comp) { 
            return comp; 
        }
        
        void add( Container con ) {
            if ( con == null ) return; 
            
            removeAll();
            if ( !(con instanceof DisabledPanel )) {
                con = new DisabledPanel(con); 
            }
            super.add( con, DEFAULT_LAYER ); 
        }
        
        void addView( ModalView view ) {
            Component[] comps = getComponents(); 
            removeAll();
            super.add( view, MODAL_LAYER );             
            for (int i=0; i<comps.length; i++) {
                if ( comps[i] instanceof ModalView ) {
                    super.add( comps[i], MODAL_LAYER ); 
                } else if ( comps[i] instanceof DisabledPanel ) { 
                    ((DisabledPanel) comps[i]).setEnabled( false ); 
                    super.add( comps[i], DEFAULT_LAYER ); 
                } 
            }
        }

        public void remove(Component comp) {
            super.remove(comp);
            Component[] comps = getComponents(); 
            ModalView mv = findModalView( comps ); 
            if ( mv == null ) {
                for (int i=0; i<comps.length; i++) {
                    if ( comps[i] instanceof DisabledPanel ) {
                        ((DisabledPanel) comps[i]).setEnabled( true ); 
                    } 
                } 
                
                DisabledPanel dp = findDisabledPanel( comps ); 
                if ( dp != null ) {
                    Container con = dp.getSourceContainer(); 
                    if ( con != null ) {
                        con.requestFocus();
                        con.requestFocusInWindow();
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(); 
                    }
                }
            }
        }
        
        void refresh() {
            contentPane.revalidate(); 
            contentPane.repaint(); 
        }
        
        ModalView findModalView( Component[] comps ) { 
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof ModalView) {
                    return (ModalView) comps[i]; 
                } 
            }
            return null; 
        }
        DisabledPanel findDisabledPanel( Component[] comps ) { 
            for (int i=0; i<comps.length; i++) {
                if (comps[i] instanceof DisabledPanel) {
                    return (DisabledPanel) comps[i]; 
                } 
            }
            return null; 
        }
    }
    
    private class InnerLayout implements LayoutManager {

        public void addLayoutComponent(String name, Component comp) {
        }
        public void removeLayoutComponent(Component comp) {
        }

        public Dimension minimumLayoutSize(Container parent) {
            synchronized( parent.getTreeLock()) {
                Insets margin = parent.getInsets();
                int w = (margin.left + margin.right);
                int h = (margin.top + margin.bottom); 
                return new Dimension( w, h ); 
            }
        }

        public Dimension preferredLayoutSize(Container parent) {
            synchronized( parent.getTreeLock()) {
                int w=0; int h=0;
                Component[] comps = getVisibleComponents( parent ); 
                for (int i=0; i<comps.length; i++ ) {
                    Dimension dim = comps[i].getPreferredSize();
                    w = Math.max( w, dim.width); 
                    h = Math.max( h, dim.height); 
                }
                
                Insets margin = parent.getInsets();
                w += (margin.left + margin.right);
                h += (margin.top + margin.bottom); 
                return new Dimension( w, h ); 
            }
        }
        
        private Component[] getVisibleComponents(Container parent) {
            ArrayList<Component> list = new ArrayList(); 
            Component[] comps = parent.getComponents(); 
            for (int i=0; i<comps.length; i++ ) {
                if (comps[i].isVisible()) {
                    list.add( comps[i]); 
                }
            }
            return list.toArray(new Component[]{}); 
        }

        public void layoutContainer(Container parent) {
            synchronized( parent.getTreeLock()) {
                Insets margin = parent.getInsets(); 
                int pw = parent.getWidth(); 
                int ph = parent.getHeight();
                int x = margin.left; 
                int y = margin.top; 
                int w = pw - (margin.left + margin.right);
                int h = ph - (margin.top + margin.bottom);
                Component[] comps = getVisibleComponents( parent ); 
                for (int i=0; i<comps.length; i++ ) {
                    comps[i].setBounds(x, y, w, h);
                }
            }
        }
    }
    
    private class ModalView extends JPanel implements SubWindow { 
        
        UIControllerPanel root = UIControllerPanel.this;
        
        private String id;
        private JInternalFrame frame;
        
        ModalView( Container con, Map props ) { 
            setLayout( new CenterLayout() ); 
            setOpaque( false ); 

            frame = new JInternalFrame("", false, false, false, false); 
            frame.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
            frame.setContentPane( con );  
            frame.setFrameIcon(null); 
            frame.setVisible(true); 
            frame.pack(); 
            update( props );
            add( frame ); 
        }
        
        public void update( Map props ) {
            if ( props == null ) return; 
            
            id = getString( props, "id");
            
            String title = getString(props, "title"); 
            if ( title != null ) frame.setTitle( title ); 
            
            Integer width = getInt(props, "width"); 
            Integer height = getInt(props, "height"); 
            Dimension dim = frame.getContentPane().getPreferredSize(); 
            if ( width != null ) dim.width = width.intValue(); 
            if ( height != null ) dim.height = height.intValue(); 
            frame.setPreferredSize(new Dimension( dim.width, dim.height )); 
        } 
        
        String getString( Map props, Object key ) {
            Object o = ( props == null ? null : props.get(key)); 
            return (o == null ? null : o.toString()); 
        }
        Integer getInt( Map props, Object key ) {
            String str = getString( props, key ); 
            try {
                return new Integer( str ); 
            } catch(Throwable t) {
                return null; 
            }
        }
        ViewContext getViewContext() {
            if ( frame.getContentPane() instanceof ViewContext ) {
                return (ViewContext) frame.getContentPane();
            } else {
                return null;                 
            }
        }

        public String getName() { 
            if ( id == null ) {
                return super.getName();
            } else {
                return id; 
            }
        }
        
        public String getTitle() {
            return frame.getTitle(); 
        }
        public void setTitle(String title) { 
            frame.setTitle( title == null ? "" : title ); 
        }

        public void closeWindow() {
            ViewContext vctx = getViewContext(); 
            if ( vctx != null && vctx.close()) {
                frame.dispose(); 
                root.contentPane.remove( this ); 
                root.contentPane.refresh(); 
            } 
        }

        public void setListener(SubWindowListener listener) {
        }
    }    
    
    // </editor-fold>
}
