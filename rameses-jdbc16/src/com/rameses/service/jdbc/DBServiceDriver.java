/*
 * DBServiceDriver.java
 * Created on December 28, 2011, 3:42 PM
 *
 * Rameses Systems Inc
 * www.ramesesinc.com
 *
 */

package com.rameses.service.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author jzamss
 */
public class DBServiceDriver implements Driver {
    public static String JDBC_PREFIX = "jdbc:rameses://";
    
    static {
        try {
            java.sql.DriverManager.registerDriver(new DBServiceDriver());
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }
    
    public Connection connect(String url, Properties info) throws SQLException {
        String s = url.substring( JDBC_PREFIX.length() );
        String arr[] = s.split("/");
        if(arr.length != 4 )
            return new DBServiceConnection(arr[0],arr[1],arr[2]);
        else 
            return new DBServiceConnection(arr[0],arr[1],arr[2],arr[3]);
    }
    
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(JDBC_PREFIX);
    }
    
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[]{};
    }
    
    public int getMajorVersion() {
        return 1;
    }
    
    public int getMinorVersion() {
        return 0;
    }
    
    public boolean jdbcCompliant() {
        return true;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
