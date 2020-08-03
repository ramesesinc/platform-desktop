/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rameses.ireport;

import java.util.ArrayList;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;

/**
 *
 * @author elmonazareno
 */
public class TestDataSourceProvider implements JRDataSourceProvider {

    @Override
    public boolean supportsGetFieldsOperation() {
        return false;
    }

    @Override
    public JRField[] getFields(JasperReport jr) throws JRException, UnsupportedOperationException {
        ArrayList fields = new ArrayList();
        JRDesignField field = new JRDesignField();
        field.setName("Name");
        field.setValueClassName("java.lang.String");
        fields.add(field);
        field = new JRDesignField();
        field.setName("Age");
        field.setValueClassName("java.lang.Integer");
        fields.add(field);
        return (JRField[]) fields.toArray(new JRField[fields.size()]);
    }

    @Override
    public JRDataSource create(JasperReport jr) throws JRException {
        return new TestDataSource();
    }

    @Override
    public void dispose(JRDataSource jrds) throws JRException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
