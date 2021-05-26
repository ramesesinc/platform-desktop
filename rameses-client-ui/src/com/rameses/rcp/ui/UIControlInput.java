/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.ui;

import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.util.ValueUtil;
import java.util.Map;

/**
 *
 * @author wflores
 */
public abstract class UIControlInput extends UIControlPanel 
    implements UIInput, Validatable {
    
    private boolean nullWhenEmpty;
    private boolean readonly; 
    private boolean requestFocus;
    private boolean immediate;
    
    private boolean required;
    private ActionMessage actionMessage; 
    
    private TrimSpaceOption trimSpaceOption;

    @Override
    protected void initComponent() {
        super.initComponent();
        trimSpaceOption = TrimSpaceOption.NONE; 
    }
    
    @Override
    public abstract void load();

    @Override
    public abstract void refresh();

    @Override
    public abstract void setValue(Object value);

    public boolean isNullWhenEmpty() {
        return nullWhenEmpty; 
    }
    public void setNullWhenEmpty( boolean nullWhenEmpty ) {
        this.nullWhenEmpty = nullWhenEmpty;
    }

    @Override
    public boolean isReadonly() {
        return readonly; 
    }
    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly; 
    }

    public boolean isRequestFocus() {
        return requestFocus; 
    }
    @Override
    public void setRequestFocus(boolean requestFocus) {
        this.requestFocus = requestFocus; 
    }

    @Override
    public boolean isImmediate() {
        return immediate; 
    }
    public void setImmediate( boolean immediate ) {
        this.immediate = immediate;
    }
    
    public TrimSpaceOption getTrimSpaceOption() {
        return trimSpaceOption;
    }    
    public void setTrimSpaceOption(TrimSpaceOption option) {
        this.trimSpaceOption = option;
    }    

    @Override
    protected void loadComponentInfo(Map info) {
        super.loadComponentInfo(info);
        
        info.put("immediate", isImmediate()); 
        info.put("nullWhenEmpty", isNullWhenEmpty()); 
        info.put("readonly", isReadonly()); 
        info.put("trimSpaceOption", getTrimSpaceOption()); 
        info.put("caption", getCaption()); 
        info.put("required", isRequired()); 
    } 
    
    // <editor-fold defaultstate="collapsed" desc=" Validatable implementation ">    
    
    public String getCaption() {
        return super.getCaption(); 
    }
    public void setCaption( String caption ) {
        super.setCaption( caption );
    }
    
    public boolean isRequired() {
        return required; 
    }
    public void setRequired( boolean required ) {
        this.required = required; 
    }
    
    public ActionMessage getActionMessage() { 
        if ( actionMessage == null ) {
            actionMessage = new ActionMessage(); 
        }
        return actionMessage; 
    }
    
    public void validateInput() {
        ActionMessage am = getActionMessage();
        am.clearMessages();
        
        ControlProperty cp = getControlProperty();
        cp.setErrorMessage( null ); 
        
        if ( isRequired() && ValueUtil.isEmpty( getValue())) {
            am.addMessage("", "{0} is required", new Object[]{ getCaption() });
            cp.setErrorMessage( am.toString());
        }
    }
    
    // </editor-fold>
}
