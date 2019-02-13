package com.rameses.rcp.ui;

/**
 *
 * @author jaycverg
 */
public interface UICommand extends UIControl {

    boolean isAutoRefresh();
    boolean isDefaultCommand();
    boolean isImmediate();
    boolean isUpdate();
 
    String getTarget();
    String getActionName();
    String getPermission();

}
