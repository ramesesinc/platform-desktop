package com.rameses.rcp.draw.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import static java.lang.Math.*;

public class Geom {

    private Geom() {
    }

    /**
     * Tests if a point is on a line.
     */
    public static boolean pointInLine(int x1, int y1, int x2, int y2, int px, int py) {
        return pointInLine(x1, y1, x2, y2, px, py, 3d);
    }
    
    /**
     * Tests if a point is on a line.
     */
    public static boolean pointInLine(int x1, int y1, int x2, int y2, int px, int py, double tolerance) {

        Rectangle r = new Rectangle(new Point(x1, y1));
        r.add(x2, y2);
        r.grow(max(2, (int) ceil(tolerance)), max(2, (int) ceil(tolerance)));
        if (!r.contains(px, py)) {
            return false;
        }

        double a, b, x, y;

        if (x1 == x2) {
            return (abs(px - x1) <= tolerance);
        }
        if (y1 == y2) {
            return (abs(py - y1) <= tolerance);
        }

        a = (double) (y1 - y2) / (double) (x1 - x2);
        b = (double) y1 - a * (double) x1;
        x = (py - b) / a;
        y = a * px + b;

        return (min(abs(x - px), abs(y - py)) <= tolerance);
    }    

    
    /**
     * Gets the square distance between two points.
     */
    public static long length2(int x1, int y1, int x2, int y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }

    /**
     * Gets the distance between to points
     */
    public static long length(int x1, int y1, int x2, int y2) {
        return (long) sqrt(length2(x1, y1, x2, y2));
    }

    /**
     * Gets the square distance between two points.
     */
    public static double length2(double x1, double y1, double x2, double y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }

    /**
     * Gets the distance between to points
     */
    public static double length(double x1, double y1, double x2, double y2) {
        return sqrt(length2(x1, y1, x2, y2));
    }

    /**
     * Gets the distance between to points
     */
    public static double length(Point2D.Double p1, Point2D.Double p2) {
        return sqrt(length2(p1.x, p1.y, p2.x, p2.y));
    }
        
    
    /**
     * Standard line intersection algorithm
     * source: http://vision.dai.ed.ac.uk/andrewfg/c-g-a-faq.html
     **/
    public static Point intersect(
            int xa, // line 1 point 1 x
            int ya, // line 1 point 1 y
            int xb, // line 1 point 2 x
            int yb, // line 1 point 2 y
            int xc, // line 2 point 1 x
            int yc, // line 2 point 1 y
            int xd, // line 2 point 2 x
            int yd){ // line 2 point 2 y 

        double denom = ((xb - xa) * (yd - yc) - (yb - ya) * (xd - xc));
        double rnum = ((ya - yc) * (xd - xc) - (xa - xc) * (yd - yc));

        if (denom == 0.0) { // parallel
            if (rnum == 0.0) { // coincident; pick one end of first line
                if ((xa < xb && (xb < xc || xb < xd))
                        || (xa > xb && (xb > xc || xb > xd))) {
                    return new Point(xb, yb);
                } else {
                    return new Point(xa, ya);
                }
            } else {
                return null;
            }
        }

        double r = rnum / denom;
        double snum = ((ya - yc) * (xb - xa) - (xa - xc) * (yb - ya));
        double s = snum / denom;

        if (0.0 <= r && r <= 1.0 && 0.0 <= s && s <= 1.0) {
            int px = (int) (xa + (xb - xa) * r);
            int py = (int) (ya + (yb - ya) * r);
            return new Point(px, py);
        } else {
            return null;
        }
    }
    
    public static Point northWest(Rectangle r) {
        int x = r.x;
        int y = r.y;
        return new Point(x, y);
    }
    
    public static Point northEast(Rectangle r) {
        int x = r.x + r.width;
        int y = r.y;
        return new Point(x, y);
    }
    
    public static Point southEast(Rectangle r) {
        int x = r.x + r.width;
        int y = r.y + r.height;
        return new Point(x, y);
    }
    
    public static Point southWest(Rectangle r) {
        int x = r.x;
        int y = r.y + r.height;
        return new Point(x, y);
    }

    

}
