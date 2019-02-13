/*
 * InputVerifierProxy.java
 *
 * Created on May 7, 2013, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.rcp.control.text;

import com.rameses.rcp.ui.UIInputVerifier;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 *
 * @author wflores
 */
public class InputVerifierProxy extends InputVerifier
{
    private InputVerifier verifier; 
    private InputVerifier child; 
    private boolean processing; 
    private boolean enabled; 
    
    public InputVerifierProxy() { 
        this(null); 
    }
    
    public InputVerifierProxy(InputVerifier verifier) 
    {
        this.verifier = verifier;
        this.processing = false;
        this.enabled = true; 
    }    

    public boolean isEnabled() { return enabled; } 
    public void setEnabled(boolean enabled) { this.enabled = enabled; } 
    
    public InputVerifier getChild() { return child; } 
    public void setChild(InputVerifier child) { this.child = child; }
    
    public void destroy() 
    {
        this.verifier = null;
        this.child = null;
    }
   
    public boolean verify(JComponent input) 
    {
        if (!enabled) return true;
        if (processing) return false;
        
        try
        {
            processing = true; 
            input.firePropertyChange("detachInputVerifier", false, true); 

            if (!input.isEnabled()) return true; 
            if (input instanceof JTextComponent) 
            {
                JTextComponent jtxt = (JTextComponent) input; 
                if (!jtxt.isEditable()) return true;
            }

            if (input instanceof UIInputVerifier) 
            {
                boolean b = ((UIInputVerifier) input).verify(input); 
                if (!b) return false; 
            }
            
            InputVerifier[] verifiers = new InputVerifier[] { verifier, child };
            for (InputVerifier iv : verifiers)
            {
                if (iv == null) continue;
                
                boolean b = iv.verify(input);
                processing = true; 
                
                if (!b) return false;
            }
            return true;
        }
        catch(RuntimeException rex) {
            throw rex;
        }
        finally 
        {
            input.firePropertyChange("detachInputVerifier", true, false); 
            processing = false;
        }
    }
    
}

