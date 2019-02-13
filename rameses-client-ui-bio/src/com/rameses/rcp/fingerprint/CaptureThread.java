/*
 * CaptureThread.java
 *
 * Created on December 8, 2013, 11:25 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import com.digitalpersona.uareu.*;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author wflores
 */
class CaptureThread extends Thread 
{
    public final static String ACT_CAPTURE = "finger_print_capture_thread";   
    
    private ActionListener actionListener;
    private boolean cancelled;
    private CaptureEvent last_capture_event;
    
    private Reader reader;
    private boolean streaming;
    private Fid.Format img_format;
    private Reader.ImageProcessing img_proc;
        
    public CaptureThread(Reader reader, boolean streaming) {
        this(reader, streaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT); 
    }
    
    public CaptureThread(Reader reader, boolean streaming, Fid.Format img_format, Reader.ImageProcessing img_proc){
        this.cancelled = false;
        this.reader = reader;
        this.streaming = streaming;
        this.img_format = img_format;
        this.img_proc = img_proc;
    }
 
    public void start(ActionListener actionListener){
        this.actionListener = actionListener; 
        super.start();
    }
    
    public void join(int milliseconds){
        try{
            super.join(milliseconds);
        } catch(InterruptedException e) { 
            e.printStackTrace(); 
        }
    }
    
    public CaptureEvent getLastCaptureEvent(){
        return last_capture_event;
    }
    
    private void capture(){
        try { 
            //wait for reader to become ready
            boolean reader_ready = false;
            while (!reader_ready && !cancelled) { 
                Reader.Status rstat = reader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rstat.status) { 
                    //if busy, wait a bit
                    try{
                        Thread.sleep(100);
                    } catch(InterruptedException e) {
                        e.printStackTrace(); 
                        break; 
                    }
                    
                } else if (Reader.ReaderStatus.READY == rstat.status || Reader.ReaderStatus.NEED_CALIBRATION == rstat.status){
                    //ready for capture
                    reader_ready = true;
                    break;
                    
                } else {
                    //reader failure
                    notifyListener(ACT_CAPTURE, null, rstat, null);
                    break;
                }
            }
            
            if (cancelled) {
                Reader.CaptureResult result = new Reader.CaptureResult();
                result.quality = Reader.CaptureQuality.CANCELED;
                notifyListener(ACT_CAPTURE, result, null, null);
            }
            
            if (reader_ready) {
                //capture
                Reader.CaptureResult result = reader.Capture(img_format, img_proc, 500, -1);
                notifyListener(ACT_CAPTURE, result, null, null);
            }
            
        } catch(UareUException e){
            notifyListener(ACT_CAPTURE, null, null, e);
        }
    } 
    
    private void stream(){
        try {
            //wait for reader to become ready
            boolean reader_ready = false;
            while (!reader_ready && !cancelled) {
                Reader.Status rstat = reader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rstat.status){
                    //if busy, wait a bit
                    try{
                        Thread.sleep(100);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    
                } else if (Reader.ReaderStatus.READY == rstat.status || Reader.ReaderStatus.NEED_CALIBRATION == rstat.status) { 
                    //ready for capture
                    reader_ready = true;
                    break;
                    
                } else { 
                    //reader failure
                    notifyListener(ACT_CAPTURE, null, rstat, null);
                    break; 
                } 
            }
            
            if (reader_ready) { 
                //start streaming
                reader.StartStreaming();
                
                //get images
                while (!cancelled) { 
                    Reader.CaptureResult result = reader.GetStreamImage(img_format, img_proc, 500);
                    notifyListener(ACT_CAPTURE, result, null, null);
                }
                
                //stop streaming
                reader.StopStreaming();
            } 
        } catch(UareUException e){
            notifyListener(ACT_CAPTURE, null, null, e);
        }
        
        if (cancelled) { 
            Reader.CaptureResult result = new Reader.CaptureResult();
            result.quality = Reader.CaptureQuality.CANCELED;
            notifyListener(ACT_CAPTURE, result, null, null);
        }
    }    
    
    public void cancel(){
        cancelled = true;
        try { 
            if (!streaming) reader.CancelCapture();
        } catch (Throwable e) {}
    }
    
    public void run() { 
        if (streaming) { 
            stream(); 
        } else {  
            capture(); 
        } 
    } 
    
    private void notifyListener(String action, Reader.CaptureResult result, Reader.Status status, UareUException exception) { 
        final CaptureEvent evt = new CaptureEvent(this, action, result, status, exception);
        
        //store last capture event
        last_capture_event = evt;
        
        if (actionListener == null || action == null || action.equals("")) return;
        
        //invoke listener on EDT thread
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                actionListener.actionPerformed(evt); 
            }
        });
    }
    
 
    // <editor-fold defaultstate="collapsed" desc=" CaptureEvent "> 
    
    public class CaptureEvent extends ActionEvent 
    {
        private final static long serialVersionUID = 1L;
        
        public Reader.CaptureResult result;
        public Reader.Status        status;
        public UareUException       exception;
        
        CaptureEvent(Object source, String action, Reader.CaptureResult result, Reader.Status status, UareUException exception){
            super(source, ActionEvent.ACTION_PERFORMED, action);
            this.result = result;
            this.status = status;
            this.exception = exception;
        }
        
        public Reader.CaptureResult getResult() { return result; } 
        public Reader.Status getStatus() { return status; } 
        public UareUException getException() { return exception; } 
    }
    
    // </editor-fold>
}
