/*
 * ImageContext.java
 *
 * Created on December 17, 2013, 2:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.fingerprint;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fid.Fiv;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author wflores
 */
class ImageContext 
{    
    private Fid fid;
    private BufferedImage image;
    private byte[] data; 
    private int width;
    private int height;
    
    private int fingerType;
    private byte[] byteArray;
    private Fmd fmd; 

    ImageContext(Fid fid) { 
        this.fid = fid;
        Fiv view = fid.getViews()[0];
        width = view.getWidth();
        height = view.getHeight();
        data = view.getImageData();
        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, width, height, data);
    }    
    
    public BufferedImage getImage() { return image; }
    public byte[] getImageData() { return data; } 
    public int getWidth() { return width; } 
    public int getHeight() { return height; } 
    
    public int getFingerType() { return fingerType; } 
    void setFingerType(int fingerType) {
        this.fingerType = fingerType; 
    }
    
    public byte[] toByteArray() { 
        try { 
            if (image == null) return null; 
            if (byteArray == null) { 
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ImageIO.write(image, "JPG", baos); 
                byteArray = baos.toByteArray(); 
            }
            return byteArray;
        } catch(Throwable t) { 
            return null; 
        } 
    }    
    
    public void exportToFile(File file) {
        exportToFile(file, "JPG");
    }
    
    public void exportToFile(File file, String fileFormat) {
        try {
            ImageIO.write(image, fileFormat, file); 
        } catch(RuntimeException re) { 
            throw re; 
        } catch(Exception ex) { 
            throw new RuntimeException(ex.getMessage(), ex); 
        } 
    } 
    
    public byte[] getFmdData() { 
        if (fmd == null) { 
            try { 
                Engine engine = UareUGlobal.GetEngine(); 
                fmd = engine.CreateFmd(fid, Fmd.Format.ANSI_378_2004); 
            } catch(UareUException ue) {
                String str = String.format("%s returned error %d \n%s", "CreateFmd", (ue.getCode() & 0xffff), ue.toString());
                throw new RuntimeException(str);
                
            } catch(RuntimeException re) {
                throw re; 
                
            } catch(Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex); 
            } 
        }
        return fmd.getData();
    }
    
    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();        
        char[] hexChars = new char[bytes.length * 2];
        int v = 0;
        for (int j=0; j<bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars); 
    } 
}
