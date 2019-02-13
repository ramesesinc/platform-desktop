/*
 * PermitAllSecurityProvider.java
 *
 * Created on May 21, 2009, 3:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.rameses.osiris2;

/**
 * Default security provider that allows all.
 */
public class PermitAllSecurityProvider implements SecurityProvider {
    
    public boolean checkPermission(String domain, String role, String name) {
        return true;
    }
    
}
