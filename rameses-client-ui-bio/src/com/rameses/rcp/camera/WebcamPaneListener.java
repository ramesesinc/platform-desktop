/*
 * WebcamPaneListener.java
 *
 * Created on December 5, 2013, 9:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.camera;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;

/**
 *
 * @author wflores
 */
interface WebcamPaneListener 
{
    void oncreate(Webcam webcam); 
    void onchangeResolution(Dimension dim);
    void onselect(byte[] bytes); 
    void oncancel();
}
