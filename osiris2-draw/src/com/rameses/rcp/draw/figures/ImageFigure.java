
package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.handles.BoxHandle;
import com.rameses.rcp.draw.interfaces.Tool;
import com.rameses.rcp.draw.tools.ImageTool;
import com.rameses.rcp.draw.utils.DataUtil;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageFigure extends AbstractAttributedFigure{
    private transient Image image;
    private String imageFile;
    
    public ImageFigure(){
        BoxHandle.addHandles(this);
    }
    
    @Override
    public String getToolCaption() {
        return "Image";
    }
    
    @Override
    public String getType(){
        return "image";
    }
        
    @Override
    public Tool getTool() {
        return new ImageTool();
    }

    @Override
    public String getIcon() {
        return "images/draw/image16.png";
    }

    @Override
    public void setDisplayBox(int x1, int y1, int x2, int y2) {
        if (isAllowResize()){
            super.setDisplayBox(x1, y1, x2, y2);
        }
        else{
            Dimension dm = getSize();
            super.setDisplayBox(new Rectangle(x1 - dm.width / 2, y1 - dm.height / 2, (int)dm.getWidth(), (int)dm.getHeight()));
        }
    }

    public Image getImage(){
        return image;
    }
    
    public void setImage(Image image){
        this.image = image;
    }
    
    public void setImage(String imageFile){
        this.imageFile = imageFile;
        setImage(DataUtil.decodeImage(imageFile));
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
    
    
    public Dimension getSize(){
        return getImageDimension();
    }
    
    
    @Override
    protected void drawFigure(Graphics2D g) {
        Rectangle r = getDisplayBox();
        if (image == null){
            g.drawRect(r.x, r.y, r.width, r.height);
        }else{
            Image scaledImage = getScaledImage(r);
            g.drawImage(scaledImage, r.x, r.y, null);
        }
    }

    @Override
    public boolean isEmpty() {
        Rectangle r = getDisplayBox();
        if (image == null || r.width <= 2 || r.height <= 2 ) {
            return true;
        }
        return false;
    }
    
    
    
    
    
    private Image getScaledImage(Rectangle r){
        if (image != null && r.width > 0 && r.height > 0){
            BufferedImage bi = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, r.width, r.height, null);
            g.dispose();
            return bi;
        }
        return null;
    }

    private Dimension getImageDimension(){
        Dimension d = null;
        if(image != null){
            BufferedImage img = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            d = new Dimension(img.getWidth(), img.getHeight());
        }
        else {
            d = new Dimension(24,24);
        }
        return d;
    }
    
    
    @Override
    public void readAttributes(Map prop){
        super.readAttributes(prop);
        Map ui = (Map) prop.get("ui");
        setImage(DataUtil.decodeImage("image", ui));
        Point pos = DataUtil.decodePoint("pos", ui);
        Dimension size = DataUtil.decodeSize("size", ui);
        if (size == null){
            size = getSize();
        }
        setDisplayBox(pos.x, pos.y, pos.x + size.width, pos.y + size.height);
        recalcLocation(pos);
    }    
    
    @Override
    public Map toMap() {
        Map map = (Map)super.toMap();
        Map ui = (Map)map.get("ui");
        
        if (imageFile != null){
            ui.put("image", imageFile);
        }else if (image != null){
            ui.put("image", DataUtil.encode(image));
        }
        
        return map;
    }  
    
    private void recalcLocation(Point pos){
        Rectangle r = getDisplayBox();
        r.x = pos.x;
        r.y = pos.y;
    }
    
}
