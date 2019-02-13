/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.sigid.device;

import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.SigIdModel;
import com.rameses.rcp.common.SigIdResult;
import com.rameses.rcp.sigid.SigIdDevice;
import com.rameses.rcp.sigid.SigIdParams;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author wflores 
 */
class TopazDevice implements SigIdDevice {

    private SigIdModel model; 
    
    public TopazDevice( SigIdModel model ) {
        this.model = model; 
    }

    public void open() {
        
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 
        final TopazPanel panel = new TopazPanel(); 
        panel.setParams(new SigIdParamsImpl(model)); 
        
        JDialog dialog = null; 
        if (win instanceof Frame) {
            dialog = new JDialog((Frame) win); 
        } else if (win instanceof Dialog) {
            dialog = new JDialog((Dialog) win); 
        } else {
            dialog = new JDialog(); 
        } 
        
        final JDialog jdialog = dialog;        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        dialog.setModal(true); 
        dialog.setResizable(false); 
        dialog.setContentPane(panel);         
        dialog.setTitle(model.getTitle());
        dialog.setSize(model.getWidth(), model.getHeight());
        dialog.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) { 
                try { 
                    panel.stop();
                } catch(Throwable t) {
                    JOptionPane.showMessageDialog(jdialog, "[ERROR] " + t.getClass().getName() + ": " + t.getMessage()); 
                } 
                
                TopazDevice.this.doCloseImpl();
            }
            
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            
            public void windowOpened(WindowEvent e) { 
                try { 
                    panel.start(); 
                } catch(Throwable t) {
                    MsgBox.err(t); 
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            jdialog.dispose(); 
                        }
                    }); 
                }
            }
        }); 
        panel.add(new SelectionListenerImpl( jdialog ));
        
        centerWindow(dialog);
        dialog.setVisible(true); 
    }
    
    private void doCloseImpl() {
        if (model != null) model.onclose();
    }
    
    private void doSelectImpl(SigIdResult info) { 
        if (model != null) { 
            model.onselect(info); 
        } 
    } 
    
    private void centerWindow(Window win) {
        Dimension windim = win.getSize();
        Dimension scrdim = Toolkit.getDefaultToolkit().getScreenSize(); 
        Insets margin = Toolkit.getDefaultToolkit().getScreenInsets(win.getGraphicsConfiguration()); 
        int scrwidth = scrdim.width - (margin.left + margin.right);
        int scrheight = scrdim.height - (margin.top + margin.bottom);
        int x = Math.max((scrwidth - windim.width) / 2, 0) + margin.left;
        int y = Math.max((scrheight - windim.height) / 2, 0) + margin.top;
        win.setLocation(x, y); 
    }

    // <editor-fold defaultstate="collapsed" desc=" SigIdParamsImpl "> 
    
    private class SigIdParamsImpl implements SigIdParams {
        private SigIdModel model; 
        
        SigIdParamsImpl(SigIdModel model) {
            this.model = model; 
        }
        
        public int getPenWidth() { 
            return model.getPenWidth(); 
        }

        public int getImageXSize() {
            return model.getImageXSize(); 
        }

        public int getImageYSize() {
            return model.getImageYSize(); 
        }

        public String getKey() {
            return model.getKey(); 
        }
    }
    
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" SigIdParamsImpl "> 
    
    private class SelectionListenerImpl implements TopazPanel.SelectionListener { 
        
        private JDialog jdialog; 
        
        SelectionListenerImpl( JDialog jdialog ) {
            this.jdialog = jdialog; 
        }
        
        public void onselect(SigIdResult info) {
            jdialog.dispose(); 
            doSelectImpl(info);
        }

        public void onclose() {
            jdialog.dispose(); 
            doCloseImpl(); 
        }
    }
    
    // </editor-fold>        
}
