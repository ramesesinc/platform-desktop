package com.rameses.rcp.draw.figures;

import com.rameses.rcp.draw.interfaces.Figure;
import com.rameses.util.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FigureCache {
    private static FigureCache instance;
    private List<String> categories;
    
    // key is category, 
    private static Map<String, List<Figure>> categoryCache;
    private static Map<String, Figure> typeCache;
    
    private FigureCache(){
        categoryCache = new HashMap<String, List<Figure>>();
        typeCache = new HashMap<String, Figure>();
        categories = new ArrayList<String>();
    }
    
    public static FigureCache getInstance(){
        if (instance == null){
            instance = new FigureCache();
            instance.initCache();
        }
        return instance;
    }
    
    public List<String> getCategories(){
        return categories;
    }
    
    
    public List getCategories(String regex) {
        List<String> list = new ArrayList<String>();
        for (String category : categories){
            if (category.toUpperCase().matches(regex.toUpperCase())){
                list.add(category);
            }
        }
        return list;
    }
    
    public List<Figure> getFigures(String category){
        return categoryCache.get(category);
    }
    
    public Figure getFigure(String type){
        return typeCache.get(type);
    }
    
    private void initCache() {
        Iterator itr = Service.providers(Figure.class, FigureCache.class.getClassLoader());
        while(itr.hasNext()){
            Figure f  = (Figure) itr.next();
            
            String category = f.getCategory();
            if (!categoryCache.containsKey(category)){
                categoryCache.put(category, new ArrayList<Figure>());
                categories.add(category);
            }
            List list = categoryCache.get(category);
            list.add(f);
            
            if (!typeCache.containsKey(f.getType())){
                typeCache.put(f.getType(), f);
            }
        }
    }
    
}
