/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.rcp.control;

import com.rameses.rcp.common.TextDocumentModel;
import com.rameses.rcp.common.TextEditorModel;
import com.rameses.rcp.common.TextWriter;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.table.ExprBeanSupport;
import com.rameses.rcp.control.text.TextAreaImpl;
import com.rameses.rcp.framework.ActionHandler;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.UIControlInput;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.UITextComponent;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Beans;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 *
 * @author wflores
 */
public class XTextAreaPanel extends UIControlInput implements UITextComponent {

    private JScrollPane scrollpane;
    private TextAreaEditor editor;

    private ActionHandlerImpl actionHandler;
    
    private TextDocumentModel handlerObject;     
    private TextEditorModel editorModel;    
    private TextWriter textWriterObject; 

    private String handler;
    private String itemExpression;
    private String varName = "item";
    
    private String actionCommand;
    private String focusKeyStroke; 
    
    @Override
    protected void initComponent() {
        super.initComponent();
        setPreferredSize(new Dimension(100, 30)); 
        
        actionHandler = new ActionHandlerImpl();
        
        editor = new TextAreaEditor();
        editor.putClientProperty(UIInput.class, this); 
        scrollpane = new JScrollPane( editor );
        setView( scrollpane ); 
    }
    
    public boolean isLineWrap() {
        return editor.getLineWrap(); 
    }
    public void setLineWrap( boolean lineWrap ) {
        editor.setLineWrap( lineWrap );
    }
    
    public boolean isWrapStyleWord() {
        return editor.getWrapStyleWord(); 
    }
    public void setWrapStyleWord( boolean wrapStyleWord ) {
        editor.setWrapStyleWord( wrapStyleWord ); 
    }

    public String getHandler() { 
        return handler; 
    } 
    public void setHandler(String handler) { 
        this.handler = handler; 
    }
    
    public TrimSpaceOption getTrimSpaceOption() {
        return editor.getTrimSpaceOption();
    }    
    public void setTrimSpaceOption(TrimSpaceOption trimSpaceOption) {
        editor.setTrimSpaceOption( trimSpaceOption ); 
    }
    
    public boolean isAutoScrollDown() { 
        return editor.isAutoScrollDown(); 
    } 
    public void setAutoScrollDown(boolean autoScrollDown) {
        editor.setAutoScrollDown( autoScrollDown ); 
    }
    
    public boolean isExitOnTabKey() { 
        return editor.isExitOnTabKey(); 
    } 
    public void setExitOnTabKey(boolean exitOnTabKey) {
        editor.setExitOnTabKey( exitOnTabKey ); 
    }
    
    public void setName(String name) {
        super.setName(name); 
        
        if (Beans.isDesignTime()) { 
            editor.setText( name ); 
        }
    }

    public TextCase getTextCase() {
        return editor.getTextCase();
    }    
    public void setTextCase(TextCase textCase) {
        editor.setTextCase(textCase); 
    }
    
    public String getHint() { 
        return editor.getHint(); 
    }
    public void setHint(String hint) {
        editor.setHint( hint ); 
    }
    
    public String getItemExpression() { 
        return itemExpression;
    }
    public void setItemExpression(String itemExpression) {
        this.itemExpression = itemExpression; 
    }  
    
    public String getVarName() { 
        return varName; 
    } 
    public void setVarName(String varName) {
        this.varName = varName;
    }    

    @Override
    public boolean isReadonly() {
        return editor.isReadonly(); 
    }
    @Override
    public void setReadonly(boolean readonly) {
        editor.setReadonly(readonly); 
    }
    
    
    @Override
    public void setBinding(Binding binding) {
        Binding old = getBinding(); 
        if ( old != null ) {
            old.getActionHandlerSupport().remove( actionHandler ); 
        }

        super.setBinding( binding );
        
        Binding newbind = getBinding(); 
        if ( newbind != null ) {
            newbind.getActionHandlerSupport().add( actionHandler ); 
        }
    }
        
    @Override
    public void load() {
        editor.load(); 
    }

    @Override
    public void refresh() {
        editor.refresh();
    }

    @Override
    public Object getValue() {
        String text = (textWriterObject == null? editor.getText(): null); 
        if ( ValueUtil.isEmpty(text) && isNullWhenEmpty()) {
            return null;
        }
        
        TrimSpaceOption opt = getTrimSpaceOption(); 
        if ( opt != null ) {
            text = opt.trim(text);
        }
        return text;
    }

    @Override
    public void setValue(Object value) {
        editor.setText(value == null? "" : value.toString()); 
    }

    @Override
    protected void loadComponentInfo(Map info) {
        super.loadComponentInfo(info);
        
        info.put("handler", getHandler()); 
        info.put("itemExpression", getItemExpression()); 
        info.put("varName", getVarName()); 
        info.put("lineWrap", isLineWrap()); 
        info.put("wrapStyleWord", isWrapStyleWord()); 
    }

    // <editor-fold defaultstate="collapsed" desc=" UITextComponent ">
    
    @Override
    public boolean isEditable() {
        return editor.isEditable(); 
    }
    @Override
    public void setEditable(boolean editable) {
        editor.setEditable(editable); 
    }

    @Override
    public Color getDisabledTextColor() {
        return editor.getDisabledTextColor(); 
    }
    @Override
    public void setDisabledTextColor(Color disabledTextColor) {
        editor.setDisabledTextColor( disabledTextColor ); 
    }

    @Override
    public Insets getMargin() {
        return editor.getMargin(); 
    }
    @Override
    public void setMargin(Insets margin) {
        editor.setMargin(margin); 
    }

    @Override
    public String getActionCommand() {
        return actionCommand; 
    }
    @Override
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand; 
    }

    @Override
    public char getFocusAccelerator() {
        return editor.getFocusAccelerator();
    }
    @Override
    public void setFocusAccelerator(char focusAccelerator) {
        editor.setFocusAccelerator( focusAccelerator );
    }

    @Override
    public String getFocusKeyStroke() {
        return focusKeyStroke; 
    }
    
    private KeyStroke focusKeyStrokeObject; 
    
    @Override
    public void setFocusKeyStroke(String focusKeyStroke) {
        this.focusKeyStroke = focusKeyStroke;        
        
        KeyStroke old = this.focusKeyStrokeObject;
        if ( old != null) { 
            unregisterKeyboardAction( old );
        } 
        
        try {
            this.focusKeyStrokeObject = KeyStroke.getKeyStroke( this.focusKeyStroke ); 
        } catch(Throwable t) {
            this.focusKeyStrokeObject = null; 
        } 
        
        if (this.focusKeyStrokeObject != null) {
            registerKeyboardAction(new FocusKeyStrokeAction(), this.focusKeyStrokeObject, JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
    }

    @Override
    public int getHorizontalAlignment() {
        return SwingConstants.LEFT; 
    }
    @Override
    public void setHorizontalAlignment(int horizontalAlignment) {
    }

    @Override
    public String getInputFormat() {
        return null;
    }
    @Override
    public void setInputFormat(String inputFormat) {
    }

    @Override
    public String getInputFormatErrorMsg() {
        return null; 
    }
    @Override
    public void setInputFormatErrorMsg(String inputFormatErrorMsg) {
    }

    @Override
    public int getMaxLength() {
        return 0;
    }
    @Override
    public void setMaxLength(int length) {
    }

    @Override
    public char getSpaceChar() {
        return '\u0000';
    }
    @Override
    public void setSpaceChar(char spaceChar) {
    }

    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" TextAreaEditor ">
    
    private class TextAreaEditor extends TextAreaImpl {

        final XTextAreaPanel root = XTextAreaPanel.this; 

        TextAreaEditor() {
            super();
            
            new MouseEventSupport( this, root ).install();             
        }
        
        @Override
        public void load() {
            super.load();
            root._ui_load();
        }

        @Override
        public void refresh() {
            super.refresh();
            root._ui_refresh();
        }

        @Override
        protected List fetchList(Map params) { 
            return (root.editorModel == null? null: root.editorModel.fetchList(params)); 
        }

        Object createExpressionBean(Object item) {
            Object bean = root.getBindingBean(); 
            ExprBeanSupport beanSupport = new ExprBeanSupport(bean);
            beanSupport.setItem( root.getVarName(), item); 
            return beanSupport.createProxy(); 
        }

        @Override
        protected Object getFormattedText(Object item) {
            String expr = root.getItemExpression();
            if ( expr == null || expr.length() == 0 ) {
                return ( item == null ? null : item.toString());
            } 

            Object exprBean = createExpressionBean( item ); 
            return UIControlUtil.evaluateExpr(exprBean, expr); 
        }

        @Override
        protected String getTemplate(Object item) {
            if ( root.editorModel == null ) {
                return null; 
            }
            
            Object o = editorModel.getTemplate( item );
            return (o == null ? null: o.toString()); 
        }
    }
    
    private void _ui_load() {
        String shandler = getHandler();
        if (shandler != null) {
            Object obj = UIControlUtil.getBeanValue(getBinding(), shandler); 
            editor.configureHandlerObject( obj ); 
            
            handlerObject = null; 
            editorModel = null; 
            textWriterObject = null;

            if (obj instanceof TextDocumentModel) {
                handlerObject = (TextDocumentModel) obj;
            } 
            if (obj instanceof TextEditorModel) { 
                editorModel = (TextEditorModel)obj; 
            } 
            if (obj instanceof TextWriter) {
                textWriterObject = (TextWriter)obj;
            } 
        } 
    }
    
    private void _ui_refresh() {
        int oldCaretPos = editor.getCaretPosition();
        try {
            //force to update component's status
            editor.updateBackground();

            Object value = UIControlUtil.getBeanValue( this );
            if (textWriterObject != null) {
                value = textWriterObject.getText(); 
            } 
            setValue( value );
            
        } 
        catch(Throwable e) {
            editor.setText("");
            
            if (ClientContext.getCurrentContext().isDebugMode()) {
                e.printStackTrace();
            }
        } 
        
        try {
            editor.setCaretPosition( oldCaretPos ); 
        } catch(Throwable ign){;} 
        
        
        if (textWriterObject != null) {
            editor.setEditable(false); 
        } 
        
        Object bean = (getBinding() == null? null : getBinding().getBean()); 
        String whenExpr = getVisibleWhen();
        if (whenExpr != null && whenExpr.length() > 0 && bean != null) {
            boolean result = false; 
            try { 
                result = UIControlUtil.evaluateExprBoolean(bean, whenExpr);
            } catch(Throwable t) {
                t.printStackTrace();
            }
            setVisible( result ); 
        } 
    }
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc=" ActionHandlerImpl ">   
    
    private class ActionHandlerImpl implements ActionHandler {
        
        final XTextAreaPanel root = XTextAreaPanel.this;
        
        public void onBeforeExecute() {
        }

        public void onAfterExecute() {
            if (root.isReadonly() || !root.editor.isEnabled() || !root.editor.isEditable()) {
                return;
            }
            if (!root.editor.getTextDocument().isDirty()) {
                return;
            }
            
            UIInputUtil.updateBeanValue( root );  
        } 
    }
    
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc=" FocusKeyStrokeAction ">   
    
    private class FocusKeyStrokeAction implements ActionListener {
        
        final XTextAreaPanel root = XTextAreaPanel.this;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (root.editor.isEnabled() && root.editor.isFocusable()) {
                root.editor.requestFocus();
            }
        }
    }
    
    // </editor-fold> 
    
}
