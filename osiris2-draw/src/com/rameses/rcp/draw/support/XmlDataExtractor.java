package com.rameses.rcp.draw.support;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.XmlSlurper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class XmlDataExtractor {
    private final String xml;
    private final String root;
    
    public XmlDataExtractor(final String xml, String root){
        this.xml = xml;
        this.root = root;
    }
    
    public Object extract(String gpath){
        return extract(gpath, new HashMap());
    }
    public Object extract(String gpath, Map params){
        final Binding binding = createBinding(params);
        final GroovyShell shell = createGroovyShell(binding);
        return shell.evaluate(gpath);
    }
    
    private GroovyShell createGroovyShell(final Binding binding){
        return new GroovyShell(getClass().getClassLoader(), binding);
    }
    
    private Binding createBinding(Map params){
        final Binding binding = new Binding();
        try{
            binding.setVariable(root,  new XmlSlurper().parseText(xml));
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        catch(SAXException e){
            throw new RuntimeException(e);
        }
        catch(ParserConfigurationException e){
            throw new RuntimeException(e);
        }
        binding.setVariable("xml", xml);
        
        if (params != null && !params.isEmpty()){
            Iterator itr = params.entrySet().iterator();
            while (itr.hasNext()){
                Map.Entry entry = (Map.Entry)itr.next();
                binding.setVariable((String) entry.getKey(), entry.getValue());
            }
        }
        
        return binding; 
    }
}
