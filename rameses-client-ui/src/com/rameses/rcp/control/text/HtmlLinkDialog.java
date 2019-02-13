/*
 * HtmlLinkDialog.java
 *
 * Created on April 5, 2014, 7:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author wflores
 */
final class HtmlLinkDialog 
{
    public final static int APPROVE_OPTION = 1;
    public final static int CANCEL_OPTION = 2;
    
    private JTextField editor;
    private String caption;
    private String value;
    
    
    public HtmlLinkDialog() {
    }
    
    public String getCaption() { return caption; } 
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    public String getValue() { return value; } 
    public void setValue(String value) {
        this.value = value;
    }    
    
    public void setValue(Object value) {
        this.value = (value == null? null: value.toString()); 
    }
    
    public int open(Component invoker) {
        Component root = (invoker == null? null: SwingUtilities.getRoot(invoker));
        JDialog d = null;
        if (root instanceof Frame) {
            d = new JDialog((Frame)root);
        } else if (root instanceof Dialog) {
            d = new JDialog((Dialog)root);
        } else {
            d = new JDialog();
        }
        d.setModal(true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
        d.setTitle("Hyperlink");
        d.setContentPane(getContent(d));
        d.pack();
        d.setResizable(false);
        d.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {
                editor.selectAll();
            }
        });
        d.setLocationRelativeTo(invoker); 
        d.setVisible(true);
        
        Integer opt = (Integer)editor.getClientProperty("ACTION_RESULT");
        return (opt == null? 0: opt.intValue());
    }
    
    private JPanel getContent(final JDialog dialog) {
        JLabel lbltitle = new JLabel();
        lbltitle.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        lbltitle.setText(caption == null? "": caption); 
        
        editor = new JTextField(); 
        editor.setText(value == null? "": value);
        editor.putClientProperty("ACTION_RESULT", CANCEL_OPTION);
        editor.setPreferredSize(new Dimension(200, 20)); 
        
        JPanel toppanel = new JPanel(new BorderLayout());
        toppanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        toppanel.add(lbltitle, BorderLayout.NORTH);
        toppanel.add(editor, BorderLayout.SOUTH);     
        
        Dimension dim = toppanel.getPreferredSize();
        toppanel.setPreferredSize(new Dimension(300, dim.height));
        
        JButton approveButton = getApproveButton(dialog);
        
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS)); 
        toolbar.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(approveButton, BorderLayout.WEST);
        toolbar.add(Box.createHorizontalStrut(5));
        toolbar.add(getCancelButton(dialog), BorderLayout.EAST);
        toolbar.add(Box.createHorizontalGlue());

        JPanel bottompanel = new JPanel(new BorderLayout());
        bottompanel.add(toolbar, BorderLayout.EAST);
        
        JPanel body = new JPanel(new BorderLayout());
        body.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        body.add(toppanel, BorderLayout.NORTH);
        body.add(toolbar, BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(approveButton); 
        return body;
    } 
    
    private JButton getApproveButton(final JDialog dialog) {
        JButton btn = new JButton("  OK  ");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editor.putClientProperty("ACTION_RESULT", APPROVE_OPTION);
                setValue(editor.getText()); 
                dialog.dispose(); 
            }
        });
        return btn;
    }
    
    private JButton getCancelButton(final JDialog dialog) {
        JButton btn = new JButton("Cancel");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editor.putClientProperty("ACTION_RESULT", CANCEL_OPTION);
                setValue(null); 
                dialog.dispose(); 
            }
        });
        return btn;
    }    
}
