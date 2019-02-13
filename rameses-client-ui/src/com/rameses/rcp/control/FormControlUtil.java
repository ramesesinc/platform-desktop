/*
 * FormControlUtil.java
 *
 * Created on October 18, 2010, 1:21 PM
 * @author jaycverg
 */

package com.rameses.rcp.control;

import com.rameses.common.PropertyResolver;
import com.rameses.rcp.common.FormControl;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.SubControlModel;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.FormSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.ui.UISubControl;
import com.rameses.rcp.util.UIControlUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComponent;

public class FormControlUtil {
    
    private static final String CONF = "META-INF/form-controls.properties";
    private static final String HTML_TAGS = "<\\/?html>|<\\/?body>";
    private static FormControlUtil instance;
    
    
    public static synchronized final FormControlUtil getInstance() {
        if ( instance == null ) {
            instance = new FormControlUtil();
        }
        return instance;
    }
    
    
    private Properties controlsIndex;
    private List<ValueResolver> resolvers = new ArrayList();
    
    FormControlUtil() {
        resolvers.add(new DefaultValueResolver());
        controlsIndex = new Properties();
        try {
            Enumeration en = ClientContext.getCurrentContext().getClassLoader().getResources(CONF);
            while( en.hasMoreElements() ) {
                URL u = (URL) en.nextElement();
                try {
                    controlsIndex.load(u.openStream());
                } catch(Exception e) {;}
            }
            
        } catch(Exception e) {;}
    }
    
    public Properties getControlsIndex() {
        return controlsIndex; 
    }
    
    public UIControl getControl(FormControl fc) {
        String className = (String) controlsIndex.get(fc.getType());
        if ( className == null ) {
            System.out.println("FormPanel Warning: " + fc.getType() + " is not supported.");
            return null;
        }
        
        try {
            ClientContext ctx = ClientContext.getCurrentContext();
            Class clazz = ctx.getClassLoader().loadClass(className);
            UIControl uic = (UIControl) clazz.newInstance();
            setProperties(uic, fc.getProperties());

            int width=0, height=0;
            Object ov = fc.getProperties().get("preferredSize");
            String[] vals = (ov == null? null: ov.toString().split(",")); 
            if (vals != null) {
                try { width = Integer.parseInt(vals[0].trim()); } catch(Throwable t){;} 
                try { height = Integer.parseInt(vals[1].trim()); } catch(Throwable t){;} 
            }            
            
            if (fc.getType().matches("decimal|integer|date|mask")) {
                width = (width == 0? 100: width);
            }

            height = (height == 0? 20: height);             
            ((JComponent) uic).setPreferredSize(new Dimension(width, height));            
            return uic; 
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        } 
        
        return null;
    }
    
    public void addValueResolver(ValueResolver res) {
        if( !resolvers.contains(res) ) resolvers.add(res);
    }
    
    public boolean removeValueResolver(ValueResolver res) {
        return resolvers.remove(res);
    }
    
    public String renderHtml(List<UIControl> controls, XFormPanel panel) {
        return renderHtml(controls, panel, false);
    }
    
    public String renderHtml(List<UIControl> controls, XFormPanel panel, boolean partial) {
        StringBuffer sb = new StringBuffer();
        if( !partial ) {
            sb.append("<html>")
            .append("<head>")
            .append("<style> body, td, div, span { ")
            .append("  font-family: \"" + panel.getFont().getFamily() + "\"; ")
            .append("  font-size: " + panel.getFont().getSize())
            .append("}</style>")
            .append("</head>")
            .append("<body>");
        }
        sb.append("<table>");
        
        boolean first = true;
        for(UIControl c : controls) {
            if( !(c instanceof ActiveControl) ) continue;
            
            ControlProperty cp = ((ActiveControl) c).getControlProperty();
            sb.append("<tr>");
            if( cp.isShowCaption() ) {
                sb.append("<td valign='top'><b>" + cp.getCaption() + ":</b></td>")
                .append("<td valign='top'>");
            } else {
                sb.append("<td valign='top' colspan='2'>");
            }
            
            Color fg = ((Component) c).getForeground();
            String strColor = null;
            if( fg != null ) {
                strColor = "rgb(" + fg.getRed() + "," + fg.getGreen() + "," + fg.getBlue() + ")";
            }
            
            if( strColor != null ) {
                sb.append("<font color='" + strColor + "'>");
            }
            
            Object value = null;
            try {
                if( c instanceof UISubControl ) {
                    UISubControl sc = (UISubControl) c;
                    Object handler = sc.getHandlerObject();
                    if( handler instanceof Opener ) {
                        Opener opener = (Opener) handler;
                        if( opener.getHandle() != null && opener.getHandle() instanceof SubControlModel ) {
                            value = ((SubControlModel) opener.getHandle()).getHtmlFormat();
                        }
                    }
                } else if ( c instanceof XFormPanel ) {
                    XFormPanel fp = (XFormPanel) c;
                    value = renderHtml( fp.getAllControls(), fp, true );
                } else if ( c instanceof XLabel ) {
                    value = ((XLabel) c).getValue();
                } else {
                    value = UIControlUtil.getBeanValue(c);
                }
            } catch(Exception e){;}

            if( !first && FormSupport.CATEGORY_LABEL.equals(c.getName()) ) {
                sb.append("<br>");
            }
            
            sb.append((value==null? "" : value.toString().replaceAll(HTML_TAGS, "")));
            
            if( strColor != null ) {
                sb.append("</font>");
            }
            
            sb.append("</td>")
            .append("</tr>");
            
            first = false;
        }
        sb.append("</table>");
        if( !partial ) {
            sb.append("</body>")
            .append("</html>");
        }
        
        return sb.toString();
    }
    
    public Map buildHtmlValueFormat(List<FormControl> controls, Object entity) {
        Map valueIndex = new HashMap();
        
        Binding b = new Binding();
        b.setBean(entity);
        
        for(FormControl fc : controls) {
            UIControl c = getControl(fc);
            c.setBinding(b);
            c.load();
            c.refresh();
            if( !(c instanceof ActiveControl) ) continue;
            
            Object value = null;
            if( c instanceof UISubControl ) {
                UISubControl sc = (UISubControl) c;
                Object handler = sc.getHandlerObject();
                if( handler instanceof Opener ) {
                    Opener opener = (Opener) handler;
                    if( opener.getHandle() != null && opener.getHandle() instanceof SubControlModel ) {
                        value = ((SubControlModel) opener.getHandle()).getHtmlFormat();
                    }
                }
            } else if ( c instanceof XFormPanel ) {
                XFormPanel fp = (XFormPanel) c;
                value = renderHtml( fp.getAllControls(), fp, true );
            } else if ( c instanceof XLabel ) {
                value = ((XLabel) c).getValue();
            } else {
                value = UIControlUtil.getBeanValue(c);
            }
            
            String name = (String) fc.getProperties().get("name");
            if( name != null )
                valueIndex.put(name, value==null? "" : value );
            
            //set control to null
            c = null;
        }
        b = null;
        
        return valueIndex;
    }
    
    public Map buildPrintValueFormat(List<FormControl> controls, Object entity) {
        Map valueIndex = new HashMap();
        
        Binding b = new Binding();
        b.setBean(entity);
        
        for(FormControl fc : controls) {
            UIControl c = getControl(fc);
            c.setBinding(b);
            c.load();
            c.refresh();
            if( !(c instanceof ActiveControl) ) continue;
            
            Object value = null;
            if( c instanceof UISubControl ) {
                UISubControl sc = (UISubControl) c;
                Object handler = sc.getHandlerObject();
                if( handler instanceof Opener ) {
                    Opener opener = (Opener) handler;
                    if( opener.getHandle() != null && opener.getHandle() instanceof SubControlModel ) {
                        value = ((SubControlModel) opener.getHandle()).getPrintFormat();
                    }
                }
            } else if ( c instanceof XFormPanel ) {
                XFormPanel fp = (XFormPanel) c;
                value = renderHtml( fp.getAllControls(), fp, true );
            } else if ( c instanceof XLabel ) {
                value = ((XLabel) c).getValue();
            } else {
                value = UIControlUtil.getBeanValue(c);
            }
            
            String name = (String) fc.getProperties().get("name");
            if( name != null )
                valueIndex.put(name, value==null? "" : value );
            
            //set control to null
            c = null;
        }
        b = null;
        
        return valueIndex;
    }
    
    //<editor-fold defaultstate="collapsed" desc="  helper methods  ">
    private void setProperties(Object control, Map properties) 
    {
        if ( properties == null ) return;
        
        ClientContext ctx = ClientContext.getCurrentContext();
        PropertyResolver resolver = PropertyResolver.getInstance();
        
        for( Object oo : properties.entrySet()) 
        {
            Map.Entry me = (Map.Entry)oo;
            try 
            {                
                String key = me.getKey()+"";
                Object value = resolveValue(key, me.getValue());
                resolver.setProperty(control, key, value ); 
            } 
            catch(Exception e) {}
        }
    }
    
    private Object resolveValue( String name, Object value )  {
        if(value==null) return null;
        
        for(ValueResolver vr : resolvers) {
            Object vv = vr.resolve(name, value);
            if( vv!=null ) return vv;
        }
        return value;
    }
    
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="  ValueResolver  ">
    
    public static interface ValueResolver {
        
        Object resolve(String name, Object value);
        
    }
    
    private static class DefaultValueResolver implements ValueResolver {
        
        private static final Pattern RGB_PATTERN = Pattern.compile("rgb\\((\\d+),(\\d+),(\\d+)\\)");
        private static final Pattern FONT_PATTERN = Pattern.compile("([\\s\\w]+)\\s+(\\w+)\\s+(\\d+)$");
        
        
        public Object resolve(String name, Object value) {
            if ( name != null && value instanceof String ) {
                String strValue = value.toString();
                
                if( ("preferredSize".equals(name) || "size".equals(name))) {
                    String[] ss = strValue.split("\\s*,\\s*");
                    return new Dimension(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
                }
                
                if ( name.matches(".*([Bb]ackground|[Ff]oreground|[Cc]olor).*") ) {
                    //a hex color value is expected
                    //example #33ff00
                    if( strValue.matches("#[a-f\\d]{3,6}") ) {
                        return Color.decode(strValue);
                    }
                    
                    //an rgb color value is expected
                    //example: rgb(200, 219, 227)
                    if( strValue.startsWith("rgb") ) {
                        Matcher m = RGB_PATTERN.matcher(strValue.replace(" ", ""));
                        if ( m.matches() ) {
                            int r = Integer.parseInt(m.group(1));
                            int g = Integer.parseInt(m.group(2));
                            int b = Integer.parseInt(m.group(3));
                            return new Color(r,g,b);
                        }
                    }
                }
                
                //return a string array for depends
                if( "depends".equals(name) ) {
                    return strValue.split("\\s*,\\s*");
                }
                
                if( name.matches(".*[Ff]ont.*") ) {
                    Matcher m = FONT_PATTERN.matcher(strValue);
                    if ( m.matches() ) {
                        try {
                            String fName = m.group(1);
                            String style = m.group(2).toUpperCase();
                            int fStyle = "BOLD".equals(style)? Font.BOLD : "ITALIC".equals(style)? Font.ITALIC : Font.PLAIN;
                            int fSize = Integer.parseInt(m.group(3));
                            
                            return new Font(fName, fStyle, fSize);
                            
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return new Font(null);
                }
                
                //for textCase
                if( name.equals("textCase") ) {
                    return TextCase.valueOf( strValue );
                }
                
                //for trimSpaceOption
                if( name.equals("trimSpaceOption") ) {
                    return TrimSpaceOption.valueOf( strValue );
                }
            }
            
            return null;
        }
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  UIProperties  ">
    
    private class UIProperties implements Map, PropertySupport.PropertyInfo
    {
        private Map properties;
        
        UIProperties(Map properties) {
            this.properties = properties;
        }

        public int size() { return properties.size(); }

        public boolean isEmpty() { 
            return properties.isEmpty(); 
        }

        public boolean containsKey(Object key) { 
            return properties.containsKey(key); 
        }

        public boolean containsValue(Object value) { 
            return properties.containsValue(value);
        }

        public Object get(Object key) {
            return properties.get(key);
        }

        public Object put(Object key, Object value) {
            return null;
        }

        public Object remove(Object key) {
            return properties.remove(key);
        }

        public void putAll(Map map) {}
        public void clear() {}

        public Set keySet() { return properties.keySet(); }
        public Collection values() { return properties.values(); }
        public Set<Map.Entry> entrySet() { return properties.entrySet(); }        
    }
    
    // </editor-fold>
}
