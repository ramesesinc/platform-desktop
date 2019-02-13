package com.rameses.rcp.draw.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;

public class DrawUtil {

    private static String cursorPath = "com/rameses/rcp/draw/cursors/";
    private static String iconPath = "images/draw/";

    public static ImageIcon loadIcon(String iconName) {
        Image img = getImage(iconPath + iconName);
        if (img != null) {
            return new ImageIcon(img);
        }
        return null;
    }

    public static Object loadClass(String className) {
        try {
            Class clz = Class.forName(className);
            return clz.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }

    }

    public static Cursor createCustomCursor(String imageName) {
        return createCustomCursor(imageName, Cursor.CROSSHAIR_CURSOR);
    }

    public static Cursor createCustomCursor(String imageName, int cursorType) {
        Cursor cursor = null;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = DrawUtil.class.getClassLoader().getResource(cursorPath + imageName);
        if (url != null) {
            try {
                Image img = toolkit.getImage(url);
                cursor = toolkit.createCustomCursor(img, new Point(16, 16), "arrow-plus");
            } catch (Exception ex) {
                cursor = new Cursor(cursorType);
            }
        }
        return cursor;
    }

    public static Image getImage(String fileName) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = DrawUtil.class.getClassLoader().getResource(fileName);
        if (url != null) {
            try {
                return toolkit.getImage(url);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    public static Dimension getTextSize(String text, Font font){
        BufferedImage bi = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setFont(font);
        return getTextSize(g, text);
    }

    public static Dimension getTextSize(Graphics2D g, String text) {
        Dimension size = new Dimension(0, 0);

        FontMetrics fm = g.getFontMetrics();
        size.height = fm.getHeight() + 2;
        size.width = fm.stringWidth(text) + 2;
        return size;
    }

    public static BufferedImage cropImage(BufferedImage image) {
        int minY = 0, maxY = 0, minX = Integer.MAX_VALUE, maxX = 0;
        boolean isBlank, minYIsDefined = false;
        
        Raster raster = image.getRaster();

        for (int y = 0; y < image.getHeight(); y++) {
            isBlank = true;

            for (int x = 0; x < image.getWidth(); x++) {
                //Change condition to (raster.getSample(x, y, 3) != 0) 
                //for better performance
                //if (raster.getPixel(x, y, (int[]) null)[3] != 0) {
                if (raster.getSample(x, y, 3) != 0) {
                    isBlank = false;
                    
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                }
            }

            if (!isBlank) {
                if (!minYIsDefined) {
                    minY = y;
                    minYIsDefined = true;
                } else {
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }
        int pad = 0;
        return image.getSubimage(minX - pad, minY - pad, maxX - minX + (pad*2), maxY - minY + (pad*2));
    }

    public static void setHDRenderingHints(Graphics graphics) {
        Graphics2D g = (Graphics2D)graphics;
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    public static List<String> getFontFamilies(){
        Set<String> families = new HashSet<String>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font f : fonts){
            families.add(f.getFamily());
        }
        List list = new ArrayList<String>(families);
        Collections.sort(list);
        return list;
    }
    
    public static List<Color> getStandardColors(){
        List<Color> list = new ArrayList<Color>();
        list.add(Color.BLACK);
        list.add(Color.BLUE);
        list.add(Color.CYAN);
        list.add(Color.DARK_GRAY);
        list.add(Color.GRAY);
        list.add(Color.GREEN);
        list.add(Color.LIGHT_GRAY);
        list.add(Color.MAGENTA);
        list.add(Color.ORANGE);
        list.add(Color.PINK);
        list.add(Color.RED);
        list.add(Color.WHITE);
        list.add(Color.YELLOW);
        return list;
    }
}
