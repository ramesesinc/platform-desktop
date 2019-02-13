package com.rameses.rcp.draw.tools;

import com.rameses.rcp.draw.figures.ImageFigure;
import com.rameses.rcp.draw.interfaces.Editor;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class ImageTool extends AbstractTool {
    
    public ImageTool(){
    }
    
    public ImageTool(Editor editor){
        this(editor, null);
    }
    
    public ImageTool(Editor editor, Class prototype){
        super(editor);
        setPrototype(prototype);
    }

    @Override
    public void mouseClicked(int x, int y, MouseEvent e) {
        JFileChooser jfc = new JFileChooser();
        int retval = jfc.showOpenDialog(null);
        
        if (retval == JFileChooser.CANCEL_OPTION){
            resetTool();
            return;
        }
        
        try{
            File file  = jfc.getSelectedFile();
            BufferedImage img = ImageIO.read(file);
            ImageFigure figure = new ImageFigure();
            figure.setImage(img);
            figure.setDisplayBox(x, y, x + img.getWidth(), y + img.getHeight());
            getEditor().addToDrawing(figure);
        }
        catch(Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading image file.");
        }
        finally{
            resetTool();
        }
    }
    
    private void resetTool(){
        getEditor().setCurrentTool(getEditor().getDefaultTool());
    }
    
}
