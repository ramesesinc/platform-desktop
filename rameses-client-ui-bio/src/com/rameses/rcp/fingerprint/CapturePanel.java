/*
 * CapturePanel.java
 *
 * Created on December 8, 2013, 11:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import com.digitalpersona.uareu.*;
import com.rameses.rcp.common.MsgBox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author wflores 
 */
class CapturePanel extends JPanel 
{
    private static final long serialVersionUID = 1L;

    private Reader reader;
    private boolean streaming;
    
    private CaptureThread capture_thread; 
    private ImageContext imageContext;
    
    public CapturePanel() {
        setPreferredSize(new Dimension(360, 390));
        setBackground(Color.WHITE); 
    } 
    
    void start(Reader reader, boolean streaming) {
        if (capture_thread != null) return;
        
        this.reader = reader;
        this.streaming = streaming;
        startCaptureThread();
    }
    
    void stop() { 
        if (capture_thread != null) {
            capture_thread.cancel();
        } 
    }
    
    void waitForCaptureThread() { 
        if (capture_thread != null) capture_thread.join(1000); 
    }
    
    byte[] getImageData() {
        try { 
            BufferedImage bi = (imageContext == null? null: imageContext.getImage());
            if (bi == null) return null;
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            ImageIO.write(bi, "JPG", baos);
            return baos.toByteArray(); 
        } catch(Throwable t) {
            t.printStackTrace();
            return null; 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" helper/override methods "> 
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageContext == null) return;
        
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(getBackground()); 
        
        // gets the current clipping area
        Rectangle clip = g2.getClipBounds();
        g2.clearRect(0, 0, clip.width, clip.height); 
        
        int x = Math.max((width - imageContext.getWidth())/2, 0);
        int y = Math.max((height - imageContext.getHeight())/2, 0);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(imageContext.getImage(), x, y, null); 
    }    
    
    private void startCaptureThread() {
        capture_thread = new CaptureThread(reader, streaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        capture_thread.start(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionPerformedImpl(e);
            }
        });
    }
    
    private void actionPerformedImpl(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (!CaptureThread.ACT_CAPTURE.equals(cmd)) return;
        
        ImageContext newImageContext = null;
        
        //event from capture thread
        boolean cancelled = false;        
        CaptureThread.CaptureEvent evt = (CaptureThread.CaptureEvent)e;
        if (evt.getException() != null) {
            UareUException ue = evt.getException();
            String str = String.format("%s returned error %d \n%s", "Capture", (ue.getCode() & 0xffff), ue.toString());
            MsgBox.err(str); 
            
        } else if (evt.getResult() != null) { 
            Reader.CaptureResult result = evt.getResult(); 
            if (result.image != null && result.quality == Reader.CaptureQuality.GOOD) { 
                //display image
                newImageContext = new ImageContext(result.image); 
            } else if (result.quality == Reader.CaptureQuality.CANCELED) {
                //capture or streaming was cancelled, just quit 
                cancelled = true;
            } else { 
                //bad quality
                MsgBox.err(result.quality); 
            }            
        } else if (evt.getStatus() != null) {
            String str = String.format("Reader status: %s", evt.getStatus().toString());
            MsgBox.err(str); 
        }
        
        imageContext = newImageContext;
        fireImageDataChanged(imageContext); 
        repaint(); 
        
        if (!cancelled) {
            if (!streaming) {
                //restart capture thread
                waitForCaptureThread();
                startCaptureThread();
            }
        } else { 
            //capturing is cancelled
        }        
    }     
    
    private void fireImageDataChanged(final ImageContext ctx) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                byte[] data = (ctx == null? null: ctx.getImageData()); 
                firePropertyChange("imageDataChanged", "", data); 
            }
        });
    }
        
    // </editor-fold>    

}
