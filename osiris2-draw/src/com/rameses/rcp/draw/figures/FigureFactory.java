package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.interfaces.Figure;
import java.util.Map;

public class FigureFactory {
    
    public static Figure createFigure(Map prop){
        Figure f = initFigure(prop);
        if (f == null){
            return null;
        }
        f.readAttributes(prop);
        return f;
    }
    
    
    private static Figure initFigure(Map prop){
        String className = (String) prop.get("class");
        if (className == null){
            return null;
        }
        try{
            return (Figure) Class.forName(className).newInstance();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
