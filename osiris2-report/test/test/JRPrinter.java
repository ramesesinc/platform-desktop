/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Graphics;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterParameter;
import net.sf.jasperreports.engine.util.JRGraphEnvInitializer;

/**
 *
 * @author wflores
 */
public class JRPrinter implements Printable {
    
    private JasperPrint jprint;
    private int pageOffset;
    private int pageSize;
    
    public JRPrinter( JasperPrint jprint ) throws JRException {
        JRGraphEnvInitializer.initializeGraphEnv();        
        this.jprint = jprint;
    }
    
    public void print( boolean withPrintDialog ) {
        this.pageOffset = 0; 
        this.pageSize = jprint.getPages().size();
        
        PrinterJob job = PrinterJob.getPrinterJob();
        
        try { 
            job.setPrintService( job.getPrintService());
        } catch (PrinterException pe) {;}
        
        PageFormat page = job.defaultPage(); 
        Paper paper = page.getPaper();
        
        job.setJobName("JRPrinter - "+ jprint.getName()); 
        
        double pw = jprint.getPageWidth();
        double ph = jprint.getPageHeight();
        if ( jprint.getOrientation() == 2) {
            page.setOrientation( PageFormat.LANDSCAPE ); 
            paper.setSize(ph, pw); 
            paper.setImageableArea(0.0, 0.0, ph, pw);
        }
        else {
            page.setOrientation( PageFormat.PORTRAIT ); 
            paper.setSize(pw, ph);
            paper.setImageableArea(0.0, 0.0, pw, ph); 
        }
        
        page.setPaper( paper ); 
        
        Book book = new Book();
        book.append( this, page, pageSize );
        job.setPageable( book ); 

        try { 
            if ( withPrintDialog ) {
                if ( job.printDialog()) {
                    job.print();
                }
            }
            else {
                job.print(); 
            }
        }
        catch(RuntimeException re) {
            throw re; 
        }
        catch(Exception e) {
            throw new RuntimeException( e ); 
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if ( Thread.currentThread().isInterrupted()) {
            throw new PrinterException("Current thread interrupted"); 
        }
        
        pageIndex += pageOffset;
        if ( pageIndex < 0 || pageIndex >= pageSize ) {
            return 1; 
        }
        
        try {
            JRGraphics2DExporter exporter = new JRGraphics2DExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jprint );
            exporter.setParameter(JRExporterParameter.PAGE_INDEX, pageIndex );
            exporter.setParameter(JRGraphics2DExporterParameter.GRAPHICS_2D, graphics);
            exporter.exportReport();
        }
        catch(Throwable t) {
            t.printStackTrace(); 
            throw new PrinterException( t.getMessage()); 
        }
        return 0; 
    }
}
