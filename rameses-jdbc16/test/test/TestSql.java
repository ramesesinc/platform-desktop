/*
 * TestSql.java
 * JUnit based test
 *
 * Created on February 24, 2013, 10:13 AM
 */

package test;

import com.rameses.service.jdbc.DBServiceDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.*;

/**
 *
 * @author Elmo
 */
public class TestSql extends TestCase {
    
    public TestSql(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    public void testHello() throws Exception {
        Class.forName(DBServiceDriver.class.getName()); 
        Connection c = DriverManager.getConnection( "jdbc:rameses://localhost:8070/osiris3/etracs221" );
        System.out.println(c);
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("select * from sariaya_etracs.account limit 5");
        while(rs.next()) {
            System.out.println(rs.getString("accttitle"));
        }
        rs.close();
        s.close();
        c.close();
    }

}
