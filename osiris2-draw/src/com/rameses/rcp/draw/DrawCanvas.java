package com.rameses.rcp.draw;

import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.control.border.XLineBorder;
import com.rameses.rcp.draw.interfaces.Canvas;
import com.rameses.rcp.draw.interfaces.Drawing;
import com.rameses.rcp.draw.interfaces.Editor;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.commands.Commands;
import com.rameses.rcp.draw.utils.DrawUtil;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


public class DrawCanvas extends JPanel implements Canvas{
    private Editor editor;
    private boolean readonly;
    private MenuProxy menu;
    private Rectangle selectionArea;
    
    private CanvasMouseAdapter  canvasMouseAdapter;
    // private CanvasKeyListener canvasKeyListener;
    private Commands keyHandlers;
    
    
    public DrawCanvas(){
        setLayout(new BorderLayout());
        setOpaque(true);
        setReadonly(false);
        setFocusable(true);
        addMouseListeners();
        //addKeyListener();
        setBackground(Color.WHITE);
        setBorder(createLineBorder());
        createKeyBindings();
    }
        
    @Override
    public Editor getEditor(){
        return editor;
    }
    
    @Override
    public void setEditor(Editor editor){
        this.editor = editor;
        this.editor.setCanvas(this);
//        if (keyHandlers == null && editor != null){
//            keyHandlers = new Commands(editor.getCanvas());
//            keyHandlers.buildCommands();
//        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (getDrawing() != null){
            getDrawing().draw(g);
        }
        drawSelectionArea(g);
    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    public void revalidateRect(Rectangle area){
        scrollRectToVisible(area);
        Rectangle pf = new Rectangle();
        Rectangle r = getDrawing().getBounds();
        pf.add(new Point(r.x + r.width + 10, r.y +r.height + 10));
        setPreferredSize(pf.getSize());
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        DrawUtil.setHDRenderingHints(g);
        draw((Graphics2D)g);
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }
    
    @Override
    public void setReadonly(boolean readonly){
        this.readonly = readonly;
    }

    @Override
    public Container getContainer() {
        return (Container)this;
    }

    @Override
    public void showMenu(int sx, int sy, List menus) {
        if (menu != null){
            menu.setVisible(false);
        }
        menu = new MenuProxy(menus);
        Rectangle r = getBounds();
        menu.setLocation(sx, sy);
        menu.setVisible(true);
    }

    @Override
    public void hideMenu(){
        if (menu != null && menu.isVisible()){
            menu.setVisible(false);
        }
    }

    @Override
    public void setSelectionArea(Rectangle selectionArea) {
        this.selectionArea = selectionArea;
    }
    
    
    
    
    @Override
    public Drawing getDrawing() {
        if (getEditor() == null){
            return null;
        }
        return getEditor().getDrawing();
    }
    
    public Tool getTool(){
        if (getEditor() == null){
            return null;
        }
        return getEditor().getCurrentTool();
    }
        
   protected void handleMouseClicked(int x, int y, MouseEvent e){
        if (getTool() != null){
            getTool().mouseClicked(x, y, e);
        }
        hideMenu();
    }
   
    protected void handleRightButton(int x, int y, int sx, int sy, MouseEvent e){
        if (getTool() != null){
            getTool().showMenu(x, y, sx, sy, e);
        }
    }
    
    protected void handleMousePressed(int x, int y, MouseEvent e){
        if (getTool() != null){
            getTool().mousePressed(x, y, e);
        }
    }
    
    protected void handleMouseReleased(int x, int y, MouseEvent e){
        if (getTool() != null){
            getTool().mouseReleased(x, y, e);
            repaint();
        }
    }
    
    
    protected void handleMouseDrag(int x, int y, MouseEvent e){
        if (getTool() != null){
            getTool().mouseDrag(x, y, e);
            repaint();
        }
    }
    
    protected void handleMouseMoved(int x, int y, MouseEvent e){
        if (getTool() != null){
            getTool().mouseMoved(x, y, e);
            repaint();
        }
    }
//    
//    protected void addKeyListener(){
//        if (canvasKeyListener == null){
//            canvasKeyListener = new CanvasKeyListener();
//        }
//        addKeyListener(canvasKeyListener);
//    }
    
    private Border createLineBorder() {
        XLineBorder b = new XLineBorder();
        b.setLineColor(Color.LIGHT_GRAY);
        return b;
    }
//    
//    class CanvasKeyListener extends KeyAdapter{
//
//        @Override
//        public void keyPressed(KeyEvent e) {
//            keyHandlers.execute(e);
//        }
//
//        private boolean isControlPressed(KeyEvent e) {
//            return (e.getModifiers() & KeyEvent.CTRL_MASK) != 0;
//        }
//        
//        private boolean isAltPressed(KeyEvent e) {
//            return (e.getModifiers() & KeyEvent.ALT_MASK) != 0;
//        }
//        
//    }
    
    private void addMouseListeners(){
        if (canvasMouseAdapter == null){
            canvasMouseAdapter = new CanvasMouseAdapter();
        }
        addMouseListener(canvasMouseAdapter);
        addMouseMotionListener(canvasMouseAdapter);
    }
    
    private void removeMouseListeners(){
        removeMouseListener(canvasMouseAdapter);
        removeMouseMotionListener(canvasMouseAdapter);
    }
    
    class CanvasMouseAdapter extends MouseAdapter{
        @Override
        public void mouseDragged(MouseEvent e){
            if (!readonly){
                handleMouseDrag(e.getX(), e.getY(), e);
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e){
            if (!readonly){
                handleMouseMoved(e.getX(), e.getY(), e);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)){
                handleRightButton(e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e);
            }else {
                handleMouseClicked(e.getX(), e.getY(), e);
            }
            if (!isFocusOwner()){
                requestFocus();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!readonly){
                handleMousePressed(e.getX(), e.getY(), e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!readonly){
                handleMouseReleased(e.getX(), e.getY(), e);
            }
        }
    }
    


    
    class MenuProxy extends JPopupMenu {
        public MenuProxy(List<Opener> menus){
            for (Opener inv : menus){
                add(new MenuItemAction(this, inv));
            }
        }
    }
    
    class MenuItemAction extends JMenuItem implements ActionListener {
        private MenuProxy menu;
        private final Opener opener;
        
        public MenuItemAction(MenuProxy menu, Opener opener) {
            this.menu = menu;
            this.opener = opener;
            setText(opener.getCaption());
            setActionCommand(opener.getAction());
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            InvokerUtil.invoke(opener);
            menu.setVisible(false);
        }
        
    }
    
    private void drawSelectionArea(Graphics2D g) {
        if (selectionArea == null) return;
        
        Graphics2D g2 = null;
        try{
            g2 = (Graphics2D)g.create();
            Stroke dashedStroke = new BasicStroke(1.0f,
                    BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER,
                    10.0f, new float[]{5f, 5f, 5f, 5f}, 5.0f);
            g2.setStroke(dashedStroke);
            g2.setXORMode(getBackground());
            g2.setColor(Color.BLACK);
            g2.drawRect(selectionArea.x, selectionArea.y, selectionArea.width, selectionArea.height);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            if (g2 != null){
                g2.dispose();
            }
        }
    }
    
    private void createKeyBindings(){
        Commands c = new Commands(this);
        c.registerCommands();
    }
    
    
            
}
