/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.rameses.osiris2.reports.ReportDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elmonazareno
 */
public class TestPrint {
    
    public TestPrint() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() throws Exception {
        List data = new ArrayList();
        Map d1 = new HashMap();
        d1.put("lastname","Nazareno");
        d1.put("firstname","Nazareno");
        data.add(d1);
        Map d2 = new HashMap();
        d2.put("lastname","Zamora");
        d2.put("firstname","Jess");
        data.add(d2);
        
        String path= "/Users/elmonazareno/Desktop/testpdf/";
        
        File f = new File(path+"test_report.jasper");
        if( !f.exists() ) {
            String srptname = ""; //reportPath + name.replaceAll("jrxml", "jasper");
            File f1 = new File(path+"test_report.jrxml");
            if(!f1.exists()) throw new Exception("File not found");
            FileOutputStream fos = new FileOutputStream(f);
            FileInputStream fis = new FileInputStream( f1 );
            JasperCompileManager.compileReportToStream( fis,fos );
            fos.flush();
            fos.close();
            fis.close();
            f = new File(path+"test_report.jasper");
        }
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(f);
        Map params = new HashMap();
        ReportDataSource rs = new ReportDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, rs);

        JasperExportManager.exportReportToPdfFile(jasperPrint, path+"testdata.pdf"); 
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //JasperExportManager.exportReportToPdfStream(jasperPrint, bos);
        //byte[] b = bos.toByteArray();
    }
}
