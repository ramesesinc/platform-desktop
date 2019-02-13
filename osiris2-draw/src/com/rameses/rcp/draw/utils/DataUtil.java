package com.rameses.rcp.draw.utils;

import com.rameses.rcp.draw.support.AttributeKeys;
import com.rameses.util.Base64Cipher;
import com.rameses.util.Base64Coder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class DataUtil {
    
    public static String encode(Object o){
        if (o == null){
            return "";
        } else if (o instanceof String){
            return (String)o;
        } else if (o instanceof Integer){
            return o.toString();
        } else if (o instanceof Long){
            return o.toString();
        } else if (o instanceof Double){
            return o.toString();
        } else if (o instanceof Float){
            return o.toString();
        } else if (o instanceof Boolean){
            return o.toString();
        } else if (o instanceof Image){
            return encodeImage((Image)o);
        }else if (o instanceof Font){
            Font f = (Font)o;
            return f.getName() + "," + f.getStyle()+ "," + f.getSize();
        } else if (o instanceof Color){
            Color c = (Color)o;
            return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            //return "#"+Integer.toHexString(((Color)o).getRGB());
        } else if (o instanceof Enum){
            Enum e = (Enum)o;
            return getEnumName(e);
        } else if (o.getClass() == double[].class){
            return encodeDoubleArray((double[])o);
        }
        throw new RuntimeException("Cannot encode value of class " + o.getClass().getSimpleName());
    }
    
    public static Object decode(Object o, Class clazz){
        if (o == null){
            return null;
        } else if (clazz == String.class){
            return decodeString(o);
        } else if (clazz == Integer.class){
            return decodeInt(o);
        } else if (clazz == Long.class){
            return decodeLong(o);
        } else if (clazz == Double.class){
            return decodeDouble(o);
        } else if (clazz == Float.class){
            return decodeFloat(o);
        } else if (clazz == Boolean.class){
            return decodeBoolean(o);
        } else if (clazz == Font.class){
            return decodeFont(o);
        } else if (clazz == Color.class){
            return decodeColor(o);
        } else if (clazz == double[].class){
            return decodeDoubleArray(o);
        }
        throw new RuntimeException("Cannot decode value of class " + o.getClass().getSimpleName());
    }
    
    public static String decodeString(String key, Map prop){
        return decodeString(prop.get(key));
    }
    
    public static String decodeString(Object val){
        if (val == null){
            return null;
        }
        return val.toString();
    }
    
    public static Integer decodeInt(String key, Map prop){
        return decodeInt(prop.get(key));
    }
    
    public static Integer decodeInt(Object val){
        return toInt(val, 0);
    }
    
    public static Long decodeLong(String key, Map prop){
        return decodeLong(prop.get(key));
    }
        
    public static Long decodeLong(Object val){
        try{
            return new Long(val.toString());
        }catch(Exception e){
            return 0L;
        }
    }
    
    public static Double decodeDouble(String key, Map prop){
        return decodeDouble(prop.get(key));
    }
        
    public static Double decodeDouble(Object val){
        try{
            return new Double(val.toString());
        }catch(Exception e){
            return new Double("0");
        }
    }
    
    public static Float decodeFloat(String key, Map prop){
        return decodeFloat(prop.get(key));
    }
    
    public static Float decodeFloat(Object val){
        try{
            return new Float(val.toString());
        }catch(Exception e){
            return new Float("0");
        }
    }
    
    public static Boolean decodeBoolean(String key, Map prop){
        return decodeBoolean(prop.get(key));
    }
    
    public static Boolean decodeBoolean(Object val){
        try{
            return Boolean.valueOf(val.toString());
        }catch(Exception e){
            return Boolean.valueOf(true);
        }
    }
    
    public static Font decodeFont(Object val){
        Font df = AttributeKeys.FONT_FACE.getDefaultValue();
        String name = df.getName();
        int style = df.getStyle();
        int size = df.getSize();
        
        String[] tokens = val.toString().split(",");
        for (int i = 0; i < tokens.length; i++){
            if (i == 0){
                name = tokens[0];
            }else if (i == 1){
                style = toInt(tokens[1], style);
            }else if (i == 2){
                size = toInt(tokens[2], size);
            }
        }
        return new Font(name, style, size);
    }
    
    public static List<Point> decodePoints(String key, Map prop){
        List<Point> points = new ArrayList<Point>();
        
        Object obj = prop.get(key);
        if (obj instanceof List){
            //[x1,y1,x2,y2,...,xn,yn]
            List list = (List)obj;
            
            if (list.size() % 2 != 0){
                //make sure items is divisible by 2 (pair of xy)
                list.add("0");
            }
            
            for (int i = 0; i < list.size() - 1; i+=2){
                Point p = new Point(0,0);
                p.x = toInt(list.get(i), 0);
                p.y = toInt(list.get(i+1), 0);
                points.add(p);
            }
        }
        else{
            String val = (String)obj;
            if (val == null || val.length() == 0){
                return new ArrayList<Point>();
            }

            String arrs[] = val.split("\\|");
            for(String s : arrs){
                points.add(decodePoint(s));
            }
        }
        return points;
    }
    
    public static Color decodeColor(Object o){
        int expectedLength = 7;
        String shex = o.toString();
        if (shex.length() < expectedLength){
            for ( int i = shex.length(); i < expectedLength; i++ ){
                shex += "0";
            }
        }
        int r = Integer.valueOf( shex.substring( 1, 3 ), 16);
        int g = Integer.valueOf( shex.substring( 3, 5 ), 16);
        int b = Integer.valueOf( shex.substring( 5, 7 ), 16);
        return new Color(r, g, b);
    }
    
    public static double[]  decodeDoubleArray(Object o) {
        String[] tokens = o.toString().split(",");
        
        double[] values = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++){
            values[i] = decodeDouble(tokens[i]);
        }
        return values;
    }
    
    
    public static int toInt(Object o, int defaultValue){
        if (o instanceof Integer){
            return (Integer)o;
        }
        
        try{
            BigDecimal bd = new BigDecimal(o.toString());
            return bd.intValue();
        }catch(Exception e){
            return defaultValue;
        }
    }
    
    
    private static String getEnumName(Enum o) {
        return escape(o.getClass().getName());
    }

        
    private static String escape(String name) {
        // Escape dollar characters by two full-stop characters
        name = name.replaceAll("\\$", "..");
        return name;

    }

    /*
     * Image is represented as 
     *     - image 
     *     - byte[] 
     *     - base64 string
     *     - relative path
     */
    public static Image decodeImage(String key, Map prop) {
        return decodeImage(prop.get(key));
    }
    
    public static Image decodeImage(Object val) {
        Image image = null;
        
        if (val instanceof Image) {
            return (Image)val;
        }else if (val instanceof byte[]){
            image = getImage((byte[])val);
        }else if (val instanceof String && isBase64String(val.toString())){
            image = getImageFromBase64(val.toString());
        }else if (val instanceof String){
            image = getImageFromFile(val.toString());
        }
        return image;
    }

    public static Image getImageFromBase64(String base64Image) {
        try{
            return getImage(Base64Coder.decode(base64Image));
        }
        catch(Exception e){
            System.out.println("decodeImage [ERROR]: " + e.getMessage());
            return null;
        }
    }
    
    public static Image getImage(byte[] image) {
        BufferedImage bi = null;
        try{
            bi = ImageIO.read(new ByteArrayInputStream(image));
        }
        catch(Exception e){
            System.out.println("decodeImage [ERROR]: " + e.getMessage());
        }
        return bi;
        
    }

    /*
     * Point representation:
     *   - [x,y]
     *   - "x,y"
     */
    public static Point decodePoint(String key, Map prop) {
        Object val = prop.get(key);
        if (val instanceof String){
            return decodePoint(val.toString());
        }else if (val instanceof List){
            return decodePoint((List)val);
        }else{
            return new Point(0,0);
        }
        
    }
    
    /*
     * Point represented as "x,y"
     */
    public static Point decodePoint(String s) {
        String[] pts = s.split(",");
        int x = 0;
        int y = 0;
        if (pts.length == 1){
            x = Integer.parseInt(pts[0]);
        }
        else if (pts.length >= 2){
            x = Integer.parseInt(pts[0]);
            y = Integer.parseInt(pts[1]);
        }
        return new Point(x,y);
    }
    
     /*
     * Point represented as [x,y]
     */
    public static Point decodePoint(List p) {
        Point pt = new Point(0,0);
        if (p.size() == 1){
            pt.x = toInt(p.get(0), 0);
        }
        else if (p.size() == 2 ){
            pt.x = toInt(p.get(0), 0);
            pt.y = toInt(p.get(1), 0);
        }
        return pt;
    }

        
    /*
     * Size representation:
     *   - [w,h]
     *   - "w,h"
     */
    public static Dimension decodeSize(String key, Map prop) {
        Point pt = decodePoint(key, prop);
        return new Dimension(pt.x, pt.y);
    }
    
    private static boolean isBase64String(String val){
        Base64Cipher b64 = new Base64Cipher();
        return b64.isEncoded(val);
        
    }

    private static Image getImageFromFile(String filename) {
        try{
            return ImageIO.read(DataUtil.class.getClassLoader().getResource(filename));
        }
        catch(Exception e){
            e.printStackTrace();
            //
        }
        return null;
        
    }

    /* enocde as list: [x,y] */
    public static Object encodePos(Rectangle r) {
        List pos = new ArrayList();
        pos.add(r.x);
        pos.add(r.y);
        return pos;
    }

    /* encode as list: [w,h] */
    public static Object encodeSize(Rectangle r) {
        List size = new ArrayList();
        size.add(r.width);
        size.add(r.height);
        return size;
    }

    /* encode as list: [x1,y1,x2,y2,...,xn,yn] */
    public static Object encodePoints(List<Point> points) {
        List pts = new ArrayList();
        for (Point pt : points){
            pts.add(pt.x);
            pts.add(pt.y);
        }
        return pts;
    }
    
    /* encode as: "0.25,1.00,2.00,3.00" */
    public static String encodeDoubleArray(double[] values) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < values.length; i++){
            if (sb.length() > 0 ){
                sb.append(",");
            }
            sb.append(String.format("%3.2f", values[i]));
        }
        return sb.toString();
    }
    
    public static void putValue(Map map, String key, Object value) {
        if (value != null){
            map.put(key, value);
        }
    }

    public static String encodeImage(Image img) {
        ByteArrayOutputStream baos = null;
        try{
            Base64Cipher b64 = new Base64Cipher();
            baos  = new ByteArrayOutputStream();
            ImageIO.write((BufferedImage)img, "PNG", baos);
            baos.flush();
            byte[] bi = baos.toByteArray();
            return b64.encode(bi);
        }catch(Exception ex){
            ex.printStackTrace();
            return "";
        }
        finally{
            if (baos != null){
                try {baos.close(); } catch(Exception ex){};
            }
        }
    }

    
}
