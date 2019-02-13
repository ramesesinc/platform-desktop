package com.rameses.rcp.framework;

import com.rameses.rcp.common.Action;
import com.rameses.rcp.common.Opener;
import java.util.List;
import java.util.Map;

/**
 * This interface will be implemented to provide actions
 *  category = filters actions only with specified category
 *  context = optional. If provided, it is used to filter the action.
 */
public interface ActionProvider {
    
    boolean hasItems( String category, Object context );
    List<Action> getActions(String category, Object context);
    List<Action> getActionsByType(String type, UIController controller);
    List<Action> lookupActions(String actionType); 
    Opener lookupOpener(String actionType, Map params);
}
