/*
 * OpenerProvider.java
 *
 * Created on October 6, 2010, 1:38 PM
 * @author jaycverg
 */

package com.rameses.rcp.framework;

import com.rameses.rcp.common.Opener;
import java.util.List;
import java.util.Map;

public interface OpenerProvider {
    
    @Deprecated
    List<Opener> getOpeners(String type, Object context);
    
    Opener lookupOpener(String invokerType, Map params); 
    List<Opener> lookupOpeners(String invokerType, Map params);
}
