/*
 * FingerPrintResultInfo.java
 *
 * Created on December 19, 2013, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import com.rameses.rcp.common.FingerPrintModel;
import java.io.File;

/**
 *
 * @author wflores
 */
public class FingerPrintResultInfo 
{
    private ImageContext[] results;
    
    FingerPrintResultInfo(ImageContext[] results) {
        this.results = results;
    }
    
    public boolean isEmpty() {
        return (results == null || results.length == 0);
    }
    
    public int size() {
        return (results == null? 0: results.length);
    }
    
    public byte[] getLeftThumbData() {
        return getData(FingerPrintModel.LEFT_THUMB);
    }
    public byte[] getLeftIndexData() {
        return getData(FingerPrintModel.LEFT_INDEX);
    }
    public byte[] getLeftMiddleData() {
        return getData(FingerPrintModel.LEFT_MIDDLE);
    }
    public byte[] getLeftRingData() {
        return getData(FingerPrintModel.LEFT_RING);
    }
    public byte[] getLeftLittleData() {
        return getData(FingerPrintModel.LEFT_LITTLE);
    }
    
    public byte[] getRightThumbData() {
        return getData(FingerPrintModel.RIGHT_THUMB);
    }
    public byte[] getRightIndexData() {
        return getData(FingerPrintModel.RIGHT_INDEX);
    }
    public byte[] getRightMiddleData() {
        return getData(FingerPrintModel.RIGHT_MIDDLE);
    }    
    public byte[] getRightRingData() {
        return getData(FingerPrintModel.RIGHT_RING);
    }    
    public byte[] getRightLittleData() {
        return getData(FingerPrintModel.RIGHT_LITTLE);
    }
    
    public byte[] getLeftThumbFmdData() {
        return getFmdData(FingerPrintModel.LEFT_THUMB);
    }
    public byte[] getLeftIndexFmdData() {
        return getFmdData(FingerPrintModel.LEFT_INDEX);
    }    
    public byte[] getLeftMiddleFmdData() {
        return getFmdData(FingerPrintModel.LEFT_MIDDLE);
    } 
    public byte[] getLeftRingFmdData() {
        return getFmdData(FingerPrintModel.LEFT_RING);
    } 
    public byte[] getLeftLittleFmdData() {
        return getFmdData(FingerPrintModel.LEFT_LITTLE);
    } 
    
    public byte[] getRightThumbFmdData() {
        return getFmdData(FingerPrintModel.RIGHT_THUMB);
    }
    public byte[] getRightIndexFmdData() {
        return getFmdData(FingerPrintModel.RIGHT_INDEX);
    }    
    public byte[] getRightMiddleFmdData() {
        return getFmdData(FingerPrintModel.RIGHT_MIDDLE);
    } 
    public byte[] getRightRingFmdData() {
        return getFmdData(FingerPrintModel.RIGHT_RING);
    } 
    public byte[] getRightLittleFmdData() {
        return getFmdData(FingerPrintModel.RIGHT_LITTLE);
    }     
    
    public byte[] getData(int fingerType) {
        ImageContext ctx = getImageContext(fingerType);
        return (ctx == null? null: ctx.toByteArray()); 
    }
    
    public byte[] getFmdData(int fingerType) {
        ImageContext ctx = getImageContext(fingerType);
        return (ctx == null? null: ctx.getFmdData()); 
    }
    
    private ImageContext getImageContext(int fingerType) {
        if (results == null || results.length == 0) return null;
        
        for (ImageContext ctx : results) {
            if ((ctx.getFingerType() & fingerType) == fingerType) {
                return ctx; 
            }
        }
        return null; 
    }
    
    public void exportToFile(int fingerType, String filename) {
        exportToFile(fingerType, filename, "JPG");
    }
    
    public void exportToFile(int fingerType, String filename, String fileFormat) {
        for (ImageContext ctx : results) {
            if ((ctx.getFingerType() & fingerType) == fingerType) {
                ctx.exportToFile(new File(filename), fileFormat); 
                break; 
            }
        }
    }    
}
