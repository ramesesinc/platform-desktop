/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.rameses.io.IOStream;
import java.awt.print.Book;
import java.io.File;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.junit.Test;

/**
 *
 * @author ramesesinc
 */
public class NewEmptyJUnitTest3 {
    
    //@Test 
    public void test1() throws Exception {
        String res = "F:/Backup/E/ETRACS/LGU/DDO_Nabunturan/_nbproject/_etracs25-client-nabunturan/report-1622176806087.jasperprint";
        
        IOStream ios = new IOStream();
        byte[] bytes = ios.toByteArray(new File( res )); 
        Object obj = ios.readObject( bytes ); 
        System.out.println( obj );
        
        JasperPrint jprint = (JasperPrint) obj;
        PrintService printSvc = PrintServiceLookup.lookupDefaultPrintService(); 
        System.out.println("printSvc.name -> "+ printSvc.getName());
                
        PrintRequestAttributeSet printReq = new HashPrintRequestAttributeSet(); 
        //printReq.add(new PrinterResolution(500, 500, PrinterResolution.DPI)); 

        printReq.add(OrientationRequested.PORTRAIT);         
        if ( jprint.getOrientation() == 2 ) { 
            printReq.add(OrientationRequested.LANDSCAPE); 
        }
        
        //new MediaPrintableArea(0.0F, 0.0F, printableWidth / 72.0F, printableHeight / 72.0F, MediaPrintableArea.INCH); 
        
        //javax.print.attribute.standard.
        
        PrintServiceAttributeSet printSvcAttrs = new HashPrintServiceAttributeSet();
        printSvcAttrs.add( new PrinterName( printSvc.getName(), null)); 
        
        JRPrintServiceExporter exporter = new JRPrintServiceExporter(); 
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jprint);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printReq);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printSvcAttrs);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, false);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, false);
        exporter.exportReport();
        
        Book book = new Book();
    }
    

    @Test 
    public void testJasperPrinter() throws Exception {
        String res = "F:/Backup/E/ETRACS/LGU/DDO_Nabunturan/_nbproject/_etracs25-client-nabunturan/report-1622176806087.jasperprint";
        
        IOStream ios = new IOStream();
        byte[] bytes = ios.toByteArray(new File( res )); 
        Object obj = ios.readObject( bytes ); 
        System.out.println( obj );
        
        JasperPrint jprint = (JasperPrint) obj;
        
        JRPrinter printer = new JRPrinter( jprint); 
        printer.print( true );  
    }
}
