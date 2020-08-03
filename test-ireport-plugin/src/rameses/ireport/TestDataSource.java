/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rameses.ireport;

import java.util.HashMap;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author elmonazareno
 */
public class TestDataSource  implements JRDataSource {

    private static final String[] nameArray = {"Frank", "Joseph", "Marco", "Carlo", "Lenny", "Homer", "Teodor", "Leopold"};
    private static final Integer[] ageArray = {50,30,40,46,44,26,32,21};
            
    private int counter = -1;
    
    private HashMap<String,Integer> fieldsNumber = new HashMap<String, Integer>();
    private int lastFieldsAdded = 0;

    public TestDataSource() {
    }
    
    @Override
    public boolean next() throws JRException {
        if( counter<nameArray.length-1) {
            counter++;
            return true;
        }
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        Integer fieldIndex;
        if( fieldsNumber.containsKey(jrField.getName()))
            fieldIndex = fieldsNumber.get(jrField.getName());
        else {
            fieldsNumber.put(jrField.getName(), lastFieldsAdded);
            fieldIndex = lastFieldsAdded;
            lastFieldsAdded++;
        }
        if(fieldIndex==0) return nameArray[counter];
        else if(fieldIndex==1) return ageArray[counter];
        return "";
    }

    public static JRDataSource getDataSource() {
        return new TestDataSource();
    }
    
}
