/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rameses.ireport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author elmonazareno
 */
public class SampleDataSource  implements JRDataSource {

    private List<Map> list = new ArrayList<Map>();
    private Map currentMap;
    private Iterator<Map> iter;
    
    public SampleDataSource() {
        Map m1 = new HashMap();
        m1.put("lastname", "Nazareno");
        m1.put("firstname", "Elmo");
        m1.put("age", 51);
        list.add( m1 );
        
        m1 = new HashMap();
        m1.put("lastname", "Dela Cruz");
        m1.put("firstname", "Juan");
        m1.put("age", 35);
        list.add( m1 );

        m1 = new HashMap();
        m1.put("lastname", "Rosales");
        m1.put("firstname", "Efren");
        m1.put("age", 64);
        list.add( m1 );
        iter = list.iterator();
    }
    
    @Override
    public boolean next() throws JRException {
        if( iter.hasNext() ) {
            currentMap = iter.next();
            return true;
        }
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        return currentMap.get( jrField.getName() );
    }

    public static JRDataSource getDataSource() {
        return new SampleDataSource();
    }
    
}
