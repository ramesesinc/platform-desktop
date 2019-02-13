package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.interfaces.Canvas;
import static com.rameses.rcp.draw.support.AttributeKeys.*;
import com.rameses.rcp.draw.tools.TextTool;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JTextField;


public class FloatingTextField implements KeyListener, ActionListener{
    private TextTool tool;
    private Canvas canvas; 
    private JTextField textField;
    private TextFigure editedFigure;
    
    public FloatingTextField() {
        textField = new JTextField(10);
    }

    public void createOverlay(TextTool tool, Canvas canvas, TextFigure figure) {
        canvas.getContainer().add(textField, 0);
        this.tool = tool;
        this.canvas = canvas;
        textField.setText(figure.getText());
        textField.selectAll();
        textField.setVisible(true);
        textField.requestFocus();
        textField.addKeyListener(this);
        textField.addActionListener(this);
        editedFigure = figure;
        updateWidget();
    }
    
    public void setBounds(Rectangle r, String text){
        textField.setText(text);
        textField.setLocation(r.x, r.y);
        textField.setSize(r.width, r.height);
        textField.setVisible(true);
        textField.selectAll();
        textField.requestFocus();
    }
    
    protected void updateWidget() {
        Rectangle r = editedFigure.getDisplayBox();
        Font font = editedFigure.get(FONT_FACE);
        textField.setFont(font);
        textField.setForeground(editedFigure.get(TEXT_COLOR));
        textField.setBackground(editedFigure.get(TEXT_BACKGROUND));

        Dimension ps = textField.getPreferredSize();
        Insets tfInsets = textField.getInsets();
        // float fontBaseline = textField.getGraphics().getFontMetrics(font).getMaxAscent();

        textField.setBounds(
            r.x - tfInsets.left,
            r.y - tfInsets.top,
            Math.max(r.width + tfInsets.left + tfInsets.right, ps.width),
            Math.max(r.height + tfInsets.top + tfInsets.bottom, ps.height)
        );
    }
    
    
    public String getText(){
        return textField.getText();
    }
    
    public void setText(String text){
        textField.setText(text);
    }
    
    public Dimension getPreferredSize(int cols){
        textField.setColumns(cols);
        return textField.getPreferredSize();
    }
    
    public void endOverlay(){
        canvas.getContainer().requestFocus();
        if (textField != null){
            editedFigure.setText(textField.getText());
            adjustFigureDisplayBox();
            textField.setVisible(false);
            canvas.getContainer().remove(textField);
            textField = null;
        }
    }
    
    private void adjustFigureDisplayBox(){
        if (getText() == null){
            return;
        }
        
        Font font = editedFigure.get(FONT_FACE);
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setFont(font);
        
        FontMetrics metrics = g2.getFontMetrics(font);
        int w = metrics.stringWidth(getText());
        int h = metrics.getHeight();
        Rectangle r = editedFigure.getDisplayBox();
        r.width = w;
        r.height = h;
        r.grow(2,4);
        editedFigure.setDisplayBox(r);
        g2.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int id = e.getID();
        
        if (id != KeyEvent.KEY_TYPED && e.getKeyCode() == KeyEvent.VK_ESCAPE){
            tool.cancel();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        tool.endEdit();
    }    
    
}
