package com.rameses.rcp.draw.support;

import com.rameses.rcp.draw.interfaces.Figure;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


public class AttributeKeys {
    private static final Map<String, AttributeKey> keys = new HashMap<String, AttributeKey>();
    
    /**
     * Canvas default size
     */
    public final static AttributeKey<Dimension> CANVAS_SIZE = new AttributeKey<Dimension>("canvasBackgroundColor", Dimension.class, new Dimension(400,300));
    /**
     * Canvas background color
     */
    public final static AttributeKey<Color> CANVAS_BACKGROUND_COLOR = new AttributeKey<Color>("canvasBackgroundColor", Color.class, Color.WHITE);
    
    /**
     * Figure minimum width
     */
    public final static AttributeKey<Integer> FIGURE_MINIMUM_WIDTH = new AttributeKey<Integer>("figureMinimumWidth", Integer.class, 2);
    /**
    /**
     * Figure minimum width
     */
    public final static AttributeKey<Integer> FIGURE_MINIMUM_HEIGHT = new AttributeKey<Integer>("figureMinimumHeight", Integer.class, 2);
    /**
    /**
     * Figure fill color
     */
    //public final static AttributeKey<Color> FILL_COLOR = new AttributeKey<Color>("fillColor", Color.class, new Color(230, 253, 199));
    public final static AttributeKey<Color> FILL_COLOR = new AttributeKey<Color>("fillColor", Color.class, Color.WHITE);
    /**
     * Stroke color
     */
    public final static AttributeKey<Color> STROKE_COLOR = new AttributeKey<Color>("strokeColor", Color.class, Color.BLUE);
    /**
     * Stroke width. A double used to construct a BasicStroke or the outline of a DoubleStroke.
     */
    public final static AttributeKey<Double> STROKE_WIDTH = new AttributeKey<Double>("strokeWidth", Double.class, 1d);
    /**
     * Stroke miter limit factor. 
     */
    public final static AttributeKey<Double> STROKE_MITER_LIMIT = new AttributeKey<Double>("strokeMiterLimitFactor", Double.class, 3d);
    /**
     * A boolean used to indicate whether STROKE_DASHES and STROKE_DASH_PHASE
     * shall be interpreted as factors of STROKE_WIDTH, or whether they are
     * absolute values.
     */
    public final static AttributeKey<Boolean> IS_STROKE_DASH_FACTOR = new AttributeKey<Boolean>("isStrokeDashFactor", Boolean.class, true);
    /**
     * A double used to specify the starting phase of the stroke dashes.
     */
    public final static AttributeKey<Double> STROKE_DASH_PHASE = new AttributeKey<Double>("strokeDashPhase", Double.class, 0d);
    /**
     * An array of doubles used to specify the dash pattern in
     * a BasicStroke;
     */
    public final static AttributeKey<double[]> STROKE_DASHES = new AttributeKey<double[]>("strokeDashes", double[].class, null);
    /**
     * Stroke join. One of the BasicStroke.CAP_... values used to
     * construct a BasicStroke.
     */
    public final static AttributeKey<Integer> STROKE_CAP = new AttributeKey<Integer>("strokeCap", Integer.class, BasicStroke.CAP_BUTT);
    
    public static enum StrokeType {
        BASIC
    }
    /**
     * Stroke type. The value of this attribute is either VALUE_STROKE_TYPE_BASIC
     * or VALUE_STROKE_TYPE_DOUBLE.
     */
    public final static AttributeKey<StrokeType> STROKE_TYPE = new AttributeKey<StrokeType>("strokeType", StrokeType.class, StrokeType.BASIC);

    /**
     * Center text on figure.
     */
    public final static AttributeKey<Boolean> CENTER_TEXT = new AttributeKey<Boolean>("centerText", Boolean.class, new Boolean(true));
    /**
     * Text color.
     */
    public final static AttributeKey<Color> TEXT_COLOR = new AttributeKey<Color>("textColor", Color.class, Color.BLACK);
    /**
     * Text background color.
     */
    public final static AttributeKey<Color> TEXT_BACKGROUND = new AttributeKey<Color>("textBackground", Color.class, Color.WHITE);
    /**
     * Font face.
     */
    public final static AttributeKey<Font> FONT_FACE = new AttributeKey<Font>("fontFace", Font.class, new Font("VERDANA", Font.PLAIN, 12));
    /**
     * Factor for the stroke inner width. This is a double. The default value is 2.
     */
    public final static AttributeKey<Double> STROKE_INNER_WIDTH_FACTOR = new AttributeKey<Double>("innerStrokeWidthFactor", Double.class, 2d);
    /**
     * Stroke join.
     */
    public final static AttributeKey<Integer> STROKE_JOIN = new AttributeKey<Integer>("strokeJoin", Integer.class, BasicStroke.JOIN_MITER);
    
    public final static AttributeKey<Dimension> WORKFLOW_NODE_DIMENSION = new AttributeKey<Dimension>("workflowNodeDimension", Dimension.class, new Dimension(32, 32));
    
    /*
     * Rotation angle in Degees
     */
    public final static AttributeKey<Double> ROTATION_ANGLE = new AttributeKey<Double>("rotation", Double.class, 0d);
    
    
    public static Stroke getStroke(Figure f) {
        double strokeWidth = f.get(STROKE_WIDTH);
        double miterLimit = f.get(STROKE_MITER_LIMIT);
        double dashFactor = f.get(IS_STROKE_DASH_FACTOR) ? strokeWidth : 1d;
        double dashPhase = f.get(STROKE_DASH_PHASE);
        double[] ddashes = f.get(STROKE_DASHES);
        float[] dashes = null;
        boolean isAllZeroes = true;
        if (ddashes != null) {
            dashes = new float[ddashes.length];
            double dashSize = 0f;
            for (int i = 0; i < dashes.length; i++) {
                dashes[i] = Math.max(0f, (float) (ddashes[i] * dashFactor));
                dashSize += dashes[i];
                if (isAllZeroes && dashes[i] != 0) {
                    isAllZeroes = false;
                }
            }
            if (dashes.length % 2 == 1) {
                dashSize *= 2;
            }
            if (dashPhase < 0) {
                dashPhase = dashSize + dashPhase % dashSize;
            }
        }
        if (isAllZeroes) {
            // don't draw dashes, if all values are 0.
            dashes = null;
        }
        switch (f.get(STROKE_TYPE)) {
            case BASIC:
            default:
                return new BasicStroke((float) strokeWidth,
                        f.get(STROKE_CAP),
                        f.get(STROKE_JOIN),
                        (float)miterLimit,
                        dashes, Math.max(0, (float) (dashPhase * dashFactor)));
            //not reached
        }
    }
    

    
    
    public static AttributeKey findByKey(String key) {
        AttributeKey ak = keys.get(key);
        if (ak == null){
            ak = getAttributeKey(key);
            if (ak != null){
                keys.put(key, ak);
            }
        }
        return ak;
    }

    private static AttributeKey getAttributeKey(String key){
        for (Field fld : AttributeKeys.class.getFields()){
            int mod = fld.getModifiers();
            if (Modifier.isFinal(mod) && Modifier.isStatic(mod)){
                try{
                    AttributeKey ak = (AttributeKey)fld.get(null);
                    if (ak.getKey().equals(key)){
                        return ak;
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    break;
                }
            }
        }
        return null;
    }
    
}
