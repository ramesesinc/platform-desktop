/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.sigid.device;

import com.florentis.signature.DynamicCapture;
import com.florentis.signature.SigCtl;
import com.florentis.signature.SigObj;
import com.rameses.rcp.common.SigIdModel;
import com.rameses.rcp.common.SigIdResult;
import com.rameses.rcp.sigid.SigIdDevice;

/**
 *
 * @author wflores 
 */
class STU430Device implements SigIdDevice {

    private SigIdModel model;
    private int width;
    private int height;
    
    public STU430Device( SigIdModel model ) { 
        this.model = model; 
        this.width = model.getWidth();
        this.height = model.getHeight();
    }
        
    public void open() {        
        SigCtl sigCtl = new SigCtl(); 
        DynamicCapture dc = new DynamicCapture();
        int rescap = dc.capture(sigCtl, " ", " ", null, null); 
        if ( rescap == 0 ) {
            SigObj sig = sigCtl.signature();
            int flags = SigObj.outputBinary | SigObj.color32BPP | SigObj.encodeData;
            int pwidth = ( this.width > 0 ? this.width : 300 );
            int pheight = ( this.height > 0 ? this.height : 150 );
            Object binres = sig.renderBitmap(null, pwidth, pheight, "image/png", 1.0f, 0xff0000, 0xffffff, 0.0f, 0.0f, flags);
            
            SigIdResultImpl result = new SigIdResultImpl();
            result.imageData = (byte[]) binres; 
            result.sigString = sig.sigText(); 
            result.numOfStrokes = 1; 
            doSelect( result ); 
            
        } else {
            switch (rescap) {
                case 1: 
                    //Cancelled 
                    doClose();
                    break;
                case 100:
                    throw new RuntimeException("Signature tablet not found");
                case 103:
                    throw new RuntimeException("Capture not licensed");
            } 
        } 
    } 
    
    private void doSelect( Object result ) {
        if ( model != null ) {
            model.onselect( result );
        }
    }
    
    private void doClose() {
        if ( model != null ) {
            model.onclose(); 
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc=" SigIdResultImpl "> 
        
    private class SigIdResultImpl implements SigIdResult {
        
        private byte[] imageData;
        private String sigString;
        private String keyString;
        private int keyReceipt; 
        private int numOfStrokes; 
        
        public String getSigString() { 
            return sigString; 
        }
        public String getKeyString() { 
            return keyString; 
        } 
        public int getKeyReceipt() { 
            return keyReceipt; 
        }
        public int getNumberOfStrokes() { 
            return numOfStrokes;
        } 
        public byte[] getImageData() { 
            return imageData; 
        } 
        public void dump() {
            System.out.println("keyReceipt=" + keyReceipt);
            System.out.println("numOfStrokes=" + numOfStrokes);
            System.out.println("keyString=" + keyString);
            System.out.println("sigString=" + sigString);
            System.out.println("imageData=" + getImageData());  
        }
    }
    
    // </editor-fold>    
}
