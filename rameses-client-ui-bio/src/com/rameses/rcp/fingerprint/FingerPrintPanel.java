/*
 * FingerPrintPanel.java
 *
 * Created on December 17, 2013, 2:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import com.digitalpersona.uareu.*;
import com.rameses.rcp.common.FingerPrintModel;
import com.rameses.rcp.common.MsgBox;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author wflores
 */
class FingerPrintPanel extends JPanel 
{
    private FingerPrintDataModel model;
    
    private Reader reader;
    private boolean streaming;
    
    private CaptureThread capture_thread;
    private ImageContext imageContext;
    
    public FingerPrintPanel() {
        model = new FingerPrintDataModel();
        addContainerListener(new ContainerListenerImpl());
        setFingerType(FingerPrintModel.RIGHT_THUMB + FingerPrintModel.LEFT_THUMB);
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
    
    ImageContext[] getImageContexts() {
        List<ImageContext> list = new ArrayList();
        Component[] comps = getComponents();
        for (int i=0; i<comps.length; i++) {
            if (!(comps[i] instanceof FingerPrintImage)) continue;
            
            FingerPrintImage fpi = (FingerPrintImage) comps[i];
            ImageContext ctx = fpi.getImageContext();
            if (ctx != null) list.add(ctx);
        }
        return list.toArray(new ImageContext[list.size()]);
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
        FingerPrintDataModel.Item dmi = model.getSelectedItem();
        if (dmi != null) dmi.setImageContext(imageContext);
        
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
    
    // <editor-fold defaultstate="collapsed" desc=" Getters/Setters ">
    
    public void setFingerType(int type) { 
        removeAll(); 
        
        if ((type & FingerPrintModel.LEFT_THUMB) == FingerPrintModel.LEFT_THUMB) {
            add(new FingerPrintImage("Left Thumb", FingerPrintModel.LEFT_THUMB));
        }
        if ((type & FingerPrintModel.RIGHT_THUMB) == FingerPrintModel.RIGHT_THUMB) {
            add(new FingerPrintImage("Right Thumb", FingerPrintModel.RIGHT_THUMB));
        }
        
        if (model.getSelectedItem() == null) {
            model.setSelectedItem(model.getItem(0));
        }
        model.refresh();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ContainerListenerImpl ">
    
    private class ContainerListenerImpl implements ContainerListener {
        FingerPrintPanel root = FingerPrintPanel.this;
        
        public void componentAdded(ContainerEvent e) {
            Component c = e.getChild();
            if (!(c instanceof FingerPrintDataModel.Item)) return;
            
            FingerPrintDataModel.Item item = (FingerPrintDataModel.Item)c;
            root.model.add(item);
        }
        
        public void componentRemoved(ContainerEvent e) {
            Component c = e.getChild();
            if (!(c instanceof FingerPrintDataModel.Item)) return;
            
            FingerPrintDataModel.Item item = (FingerPrintDataModel.Item)c;
            root.model.remove(item);
        }
    }
    
    // </editor-fold>
}
