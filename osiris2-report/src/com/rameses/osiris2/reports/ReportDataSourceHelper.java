
package com.rameses.osiris2.reports;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class ReportDataSourceHelper implements JRDataSource 
{
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateTimeFormatter;
    private Map<String,DecimalFormat> numberFormats; 
    
    private Object source; 
    
    public ReportDataSourceHelper() {
        this( null );
    }
    
    public ReportDataSourceHelper( Object source ) {
        this.source = source; 
        
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        numberFormats = new HashMap();         
    }
    
    
    // <editor-fold defaultstate="collapsed" desc=" JRDataSource implementaiton ">
    
    public boolean next() throws JRException {
        return false;
    }
    
    public Object getFieldValue(JRField jRField) throws JRException {
        return null;
    }    
    
    // </editor-fold>
    
    
    private DecimalFormat getNumberFormat( String pattern ) {
        if ( pattern == null ) { pattern = "#,##0.00"; } 
        
        DecimalFormat decf = numberFormats.get( pattern ); 
        if ( decf == null ) { 
            decf = new DecimalFormat( pattern );
            numberFormats.put( pattern, decf ); 
        }
        return decf; 
    }
    
    public Object resolveObject( Object obj ) {
        if (obj instanceof ReportDataSource) {
            return ((ReportDataSource)obj).getSource();
        } else { 
            return obj; 
        } 
    }
        
    public String toString( Object value ) {
        if ( value == null ) { 
            return null; 
        } else {
            return value.toString(); 
        } 
    }
    
    public Date toDate() {
        return toDate( source ); 
    }
    
    public Date toDate( Object value ) {
        if (value == null) {
            return null; 
        } else if (value instanceof Date) {
            return (Date) value; 
        } else {
            return convertDate( value, false );
        }
    }
    
    public Timestamp toDateTime() {
        return toDateTime( source ); 
    }
    
    public Timestamp toDateTime( Object value ) {
        if (value == null) {
            return null; 
        } else if (value instanceof Timestamp) {
            return (Timestamp) value; 
        } else if (value instanceof Date) {
            return new java.sql.Timestamp( ((Date)value).getTime() ); 
        } else {
            return (Timestamp) convertDate( value, true ); 
        } 
    } 
    
    public BigDecimal toDecimal() {
        return toDecimal( source ); 
    }
    
    public BigDecimal toDecimal( Object value ) {
        if (value == null) { 
            return null; 
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value; 
        } else { 
            return new BigDecimal( value.toString() );
        } 
    } 
    
    public Integer toInteger() {
        return toInteger( source ); 
    }
    
    public Integer toInteger( Object value ) {
        if (value == null) { 
            return null; 
        } else if (value instanceof Integer) {
            return (Integer) value; 
        } else if (value instanceof Number) {
            return new Integer( ((Number)value).intValue() ); 
        } else { 
            return new Integer( value.toString() );
        } 
    } 
    
    public Long toLong() {
        return toLong( source ); 
    }
    
    public Long toLong( Object value ) {
        if (value == null) { 
            return null; 
        } else if (value instanceof Long) {
            return (Long) value; 
        } else if (value instanceof Number) {
            return new Long( ((Number)value).longValue() ); 
        } else { 
            return new Long( value.toString() );
        } 
    } 
    
    private Date convertDate( Object value, boolean allowHourMinSec ) {
        try { 
            if ( value == null ) { return null; } 
            
            String[] arr = value.toString().split(" "); 
            if ( allowHourMinSec ) {
                Date dt = null; 
                if ( arr.length > 1 ) {
                    dt = dateTimeFormatter.parse( value.toString() ); 
                } else {
                    dt = dateFormatter.parse( arr[0] ); 
                } 
                return new java.sql.Timestamp( dt.getTime() ); 
            } else { 
                Date dt = dateFormatter.parse( arr[0] ); 
                return new java.sql.Date( dt.getTime() ); 
            }
        } catch(RuntimeException re) {
            throw re; 
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e); 
        }
    }
    
    public int getDaysDiff(Object dtfrom, Object dtto) {
        java.util.Date startDate = convertDate( dtfrom, false );
        java.util.Date endDate = convertDate( dtto, false );
        
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        
        startCal.setTime(startDate);
        endCal.setTime(endDate );
        
        long startMillis = startCal.getTimeInMillis();
        long endMillis = endCal.getTimeInMillis();
        
        // Calculate no. of days using diff in milliseconds
        long diff = endMillis - startMillis;
        return (int)(diff / (24 * 60 * 60 * 1000) + 1); 
    }
    
    public int getYearsDiff( Object dtfrom, Object dtto ) {
        java.util.Date startDate = convertDate( dtfrom, false );
        java.util.Date endDate = convertDate( dtto, false );
        
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        
        startCal.setTime(startDate);
        endCal.setTime(endDate );
        
        int years = 0;         
        if (startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)) {
            while ( startCal.before(endCal) ) {
                startCal.add(Calendar.YEAR, 1); 
                years++; 
            } 
        }
        return years; 
    }
    
    public int getQtr() { 
        return getQtr( source ); 
    }
    
    public int getQtr( Object value ) { 
        Date dt = toDate( value ); 
        if ( dt == null ) {
            return -1; 
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime( dt ); 
            int month = cal.get(Calendar.MONTH) + 1; 
            if ( month >= 1 && month <=3 ) {
                return 1; 
            } else if ( month >= 4 && month <=6 ) {
                return 2; 
            } else if ( month >= 7 && month <=9 ) {
                return 3; 
            } else if ( month >= 10 && month <=12 ) {
                return 4; 
            } else {
                return -1; 
            }
        } 
    }
    
    public String formatNumber( String pattern ) {
        return formatNumber( source, pattern ); 
    }
    public String formatNumber( String pattern, String zerovalue ) {
        return formatNumber( source, pattern, zerovalue ); 
    }
        
    public String formatNumber( Object value, String pattern ) {
        return formatNumber( value, pattern, null ); 
    }
        
    public String formatNumber( Object value, String pattern, String zerovalue ) {
        BigDecimal num = toDecimal( value); 
        if ( num == null) {
            return zerovalue;
        } else if ( num.doubleValue() != 0.0 ) {
            return getNumberFormat( pattern ).format( num );
        } else if ( zerovalue == null ) {
            return getNumberFormat( pattern ).format( num ); 
        } else {
            return zerovalue; 
        }
    }
} 
