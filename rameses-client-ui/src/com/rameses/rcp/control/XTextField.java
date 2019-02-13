package com.rameses.rcp.control;

import com.rameses.common.MethodResolver;
import com.rameses.rcp.common.MsgBox;
import com.rameses.rcp.common.Opener;
import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.common.TextColumnHandler;
import com.rameses.rcp.constant.TextCase;
import com.rameses.rcp.constant.TrimSpaceOption;
import com.rameses.rcp.control.text.DefaultTextField;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.framework.ClientContext;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.support.TextDocument;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIInput;
import com.rameses.rcp.ui.Validatable;
import com.rameses.rcp.util.ActionMessage;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.rcp.util.UIInputUtil;
import com.rameses.util.ValueUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;

/**
 *
 * @author jaycverg
 */
public class XTextField extends DefaultTextField 
    implements UIInput, Validatable, ActiveControl, MouseEventSupport.ComponentInfo 
{    
    protected Binding binding;
    protected ControlProperty property = new ControlProperty();
    protected ActionMessage actionMessage = new ActionMessage();
    
    private int index;
    private char spaceChar;    
    private String[] replaceExpr;
    private String[] replaceString;    
    private String[] depends = new String[]{};
    private String hint;
    private String inputFormat;
    private String inputFormatErrorMsg;
    private boolean nullWhenEmpty = true;
    private boolean showHint;
    private boolean isHintShown;
    
    private TextDocument document = new TextDocument();
    private TrimSpaceOption trimSpaceOption = TrimSpaceOption.ALL;
    private ActionCommandInvoker actionCommandInvoker; 

    private String securityPattern;
    private String securityChar;
    private String securedValue; //internal value
    
    private int stretchWidth;
    private int stretchHeight; 
    
    public XTextField() {
        super();
        initComponent();
    } 
    
    private void initComponent() {
        document.setTextCase(TextCase.UPPER);

        actionCommandInvoker = new ActionCommandInvoker();        
        addActionMapping(ACTION_MAPPING_KEY_ESCAPE, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try { refresh(); } catch(Throwable t) {;} 
            }
        }); 
        new MouseEventSupport(this).install();
    }
        
    public void paint(Graphics origGraphics) {
        super.paint(origGraphics);
        
        if( showHint && getDocument().getLength() == 0 ) 
        {
            Graphics g = origGraphics.create();
            Font f = getFont();
            FontMetrics fm = g.getFontMetrics(f);
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(f);
            
            Insets margin = getInsets();
            int width = getWidth() - 1 - margin.left - margin.right;
            int height = getHeight() - 1 - margin.top - margin.bottom;
            int x = margin.left;
            int y = (height /2) + (fm.getAscent() / 2 ) + margin.top;

            g.setClip(margin.left, margin.top, width, height);
            g.drawString(" " + getHint(), x, y);
            g.dispose();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="  UIControl implementation  ">

    public void refresh() {
        try {
            updateBackground(); 
            
            String whenExpr = getVisibleWhen();
            if (whenExpr != null && whenExpr.length() > 0) {
                boolean result = false; 
                try { 
                    result = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                setVisible( result ); 
            }

            whenExpr = getDisableWhen();
            if (whenExpr != null && whenExpr.length() > 0) {
                boolean disabled = false; 
                try { 
                    disabled = UIControlUtil.evaluateExprBoolean(binding.getBean(), whenExpr);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
                setEnabled( !disabled ); 
            }
            
            Object value = UIControlUtil.getBeanValue(this);
            if ( isSecured() ) {
                //keep the actual value
                securedValue = (String) value;
                
                String txtValue = "";
                if (value != null) {
                    StringBuffer text = new StringBuffer();
                    Matcher m = Pattern.compile(securityPattern).matcher(value.toString());
                    while (m.find()) {
                        m.appendReplacement(text, repeat(securityChar, m.group().length()));
                    }
                    m.appendTail(text);
                    txtValue = text.toString();
                }                
                super.setText(txtValue); 
                
            } else {
                setValue(value);
            }
        } 
        catch(Exception e) {
            //just block the input when the name is null
            setText("");
            
            if (ClientContext.getCurrentContext().isDebugMode()) e.printStackTrace(); 
        } 
    } 
    
    private String repeat(String str, int count) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<count && sb.length()<count; ++i) sb.append(str);
        if( sb.length() > count) sb.setLength(count);
        return sb.toString();
    }

    protected InputVerifier getChildInputVerifier() {
        return UIInputUtil.VERIFIER; 
    }
    
    public void load() 
    {
        setDocument(document);
        if (showHint) isHintShown = true;

        String cmd = getActionCommand();
        if (cmd != null && cmd.length() > 0) {
            removeActionMapping(ACTION_MAPPING_KEY_ENTER, actionCommandInvoker); 
            addActionMapping(ACTION_MAPPING_KEY_ENTER, actionCommandInvoker); 
        }
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public void validateInput() 
    {
        actionMessage.clearMessages();
        property.setErrorMessage(null);
        if ( ValueUtil.isEmpty( getText() ) ) {
            if ( isRequired() ) {
                actionMessage.addMessage("1001", "{0} is required.", new Object[] { getCaption() });
            }
        } else if ( !ValueUtil.isEmpty(inputFormat) && !getText().matches(inputFormat) ) {
            String msg = null;
            if ( inputFormatErrorMsg != null )
                msg = inputFormatErrorMsg;
            else
                msg = "Invalid input format for {0}";
            
            actionMessage.addMessage(null, msg, new Object[]{ getCaption() });
        }
        
        if ( actionMessage.hasMessages() ) {
            property.setErrorMessage( actionMessage.toString() );
        }
    } 
    
    public Map getInfo() { 
        Map map = new HashMap();
        map.put("actionCommand", getActionCommand());         
        map.put("nullWhenEmpty", isNullWhenEmpty());
        map.put("required", isRequired());
        map.put("focusAccelerator", getFocusAccelerator()); 
        return map;
    }    
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public void setName(String name) 
    {
        super.setName(name);
        super.setText(name);
    }
    
    public Object getValue() 
    {
        if ( isSecured() ) return securedValue;
        
        String txtValue = getText();
        if ( ValueUtil.isEmpty(txtValue) && nullWhenEmpty ) return null;
        
        if ( trimSpaceOption != null ) 
            txtValue = trimSpaceOption.trim(txtValue);
        
        if ( replaceExpr != null && replaceString != null ) 
        {
            for (int i=0; i<replaceExpr.length; ++i) 
            {
                if ( replaceString.length <= i) break;
                
                txtValue = txtValue.replaceAll( replaceExpr[i], replaceString[i] );
            }
        } 
        
        txtValue = renderSpaceChar(txtValue);
        super.setText(txtValue);
        return txtValue;
    }
    
    private KeyEvent keValue;
    
    public void setValue(Object value) {
        if ( value instanceof EventObject ) {
            if (value instanceof KeyEvent) { 
                KeyEvent ke = (KeyEvent) value;
                String sval = ke.getKeyChar()+"";
                super.setText(sval); 
            } 
        } else {
            setText((value == null? "": value.toString())); 
        }
    }
    
    public String[] getDepends() { return depends; }    
    public void setDepends(String[] depends) {
        this.depends = depends;
    }
    
    public int getIndex() { return index; }    
    public void setIndex(int index) {
        this.index = index;
    }

    public Binding getBinding() { return binding; }    
    public void setBinding(Binding binding) {
        this.binding = binding;
    }
        
    public ActionMessage getActionMessage() { return actionMessage; }
    public ControlProperty getControlProperty() { return property; }   
    
    public boolean isRequired() { return property.isRequired(); }    
    public void setRequired(boolean required) {
        property.setRequired(required);
    }
    
    public String getCaption() { return property.getCaption(); }    
    public void setCaption(String caption) { 
        property.setCaption(caption); 
    } 
    
    public char getCaptionMnemonic() { 
        return property.getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        property.setCaptionMnemonic(c);
    }
    
    public int getCaptionWidth() {
        return property.getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        property.setCaptionWidth(width);
    }    
    
    public boolean isNullWhenEmpty() { return nullWhenEmpty; }    
    public void setNullWhenEmpty(boolean nullWhenEmpty) {
        this.nullWhenEmpty = nullWhenEmpty;
    }
        
    public boolean isShowCaption() {
        return property.isShowCaption();
    }    
    public void setShowCaption(boolean showCaption) {
        property.setShowCaption(showCaption);
    }
    
    public Font getCaptionFont() {
        return property.getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        property.setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return property.getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        property.setCaptionFontStyle(captionFontStyle); 
    }     
    
    public Insets getCellPadding() {
        return property.getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        property.setCellPadding(padding);
    }
        
    public TextCase getTextCase() {
        return document.getTextCase();
    }    
    public void setTextCase(TextCase textCase) {
        document.setTextCase(textCase);
    }
    
    public int getMaxLength() {
        return document.getMaxlength();
    }    
    public void setMaxLength(int length) {
        document.setMaxlength(length);
    }
    
    public TrimSpaceOption getTrimSpaceOption() { return trimSpaceOption; }    
    public void setTrimSpaceOption(TrimSpaceOption option) {
        this.trimSpaceOption = option;
    }

    public void setRequestFocus(boolean focus) {
        if ( focus ) requestFocus();
    }
    
    public boolean isImmediate() { return false; }    
    
    public String getHint() { return hint; }    
    public void setHint(String hint) 
    {
        this.hint = hint;
        showHint = !ValueUtil.isEmpty(hint);
    }
    
    public String getInputFormat() { return inputFormat; }    
    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }
    
    public String getInputFormatErrorMsg() { return inputFormatErrorMsg; }    
    public void setInputFormatErrorMsg(String inputFormatErrorMsg) {
        this.inputFormatErrorMsg = inputFormatErrorMsg;
    }
    
    public String[] getReplaceExpr() { return replaceExpr; }    
    public void setReplaceExpr(String[] replaceExpr) {
        this.replaceExpr = replaceExpr;
    }
    
    public String[] getReplaceString() { return replaceString; }    
    public void setReplaceString(String[] replaceString) {
        this.replaceString = replaceString;
    }
    
    public String getSecurityPattern() { return securityPattern; }    
    public void setSecurityPattern(String securityPattern) {
        this.securityPattern = securityPattern;
    }
    
    public String getSecurityChar() { return securityChar; }    
    public void setSecurityChar(String securityChar) {
        this.securityChar = securityChar;
    }
    
    public boolean isSecured() {
        return securityPattern != null && securityPattern.length() > 0 && securityChar != null && securityChar.length() > 0;
    }
    
    public char getSpaceChar() { return spaceChar; } 
    public void setSpaceChar(char spaceChar) { this.spaceChar = spaceChar; } 
    
    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
        if (!(info instanceof TextColumnHandler)) return;
        
        TextColumnHandler chandler = (TextColumnHandler) info; 
        if (chandler.getColumn() == null) return; 
        
        TextCase textcase = chandler.getColumn().getTextCase();
        if (textcase != null) document.setTextCase(textcase); 
    }    
    
    public TextDocument.Filter getFilter() {
        return (document == null? null: document.getFilter()); 
    }
    public void setFilter( TextDocument.Filter filter ) {
        if (document != null) { 
            document.setFilter( filter ); 
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="  Helper methods  ">    
    
    private String renderSpaceChar(String value) 
    {
        if (value != null && spaceChar != '\u0000') 
            value = value.replaceAll(" ", String.valueOf(spaceChar)); 

        return value;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActionCommandInvoker ">    
    
    private class ActionCommandInvoker implements ActionListener 
    {
        XTextField root = XTextField.this; 
        
        public void actionPerformed(ActionEvent e) { 
            try {
                String cmd = root.getActionCommand(); 
                if (cmd == null || cmd.length() == 0) return; 
                
                UIInputUtil.updateBeanValue(root);                 
                Object bean = root.getBinding().getBean();
                Object outcome = MethodResolver.getInstance().invoke(bean, cmd, new Object[]{}); 
                if (outcome instanceof Opener) { 
                    root.getBinding().fireNavigation(outcome); 
                } 
            } catch(Throwable t) { 
                MsgBox.err(t); 
            } 
        } 
        
    }
    
    // </editor-fold>
}
