package com.rameses.rcp.control;

import com.rameses.rcp.common.PropertySupport;
import com.rameses.rcp.framework.Binding;
import com.rameses.rcp.support.MouseEventSupport;
import com.rameses.rcp.ui.ActiveControl;
import com.rameses.rcp.ui.ControlProperty;
import com.rameses.rcp.ui.UIControl;
import com.rameses.rcp.util.UIControlUtil;
import com.rameses.util.Base64Cipher;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.beans.Beans;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Windhel
 */

public class XImageViewer extends JPanel implements UIControl, ActiveControl, MouseEventSupport.ComponentInfo 
{    
    private Binding binding;
    private int index;
    private boolean advanced;
    private boolean fitImage;
    private String[] depends;
    private boolean dynamic;
    
    //calculate dimension on demand
    private int width = -1;
    private int height = -1;
    
    private String emptyImage;
    private Icon emptyImageIcon;
    
    private JSlider zoomSlider;
    private JCheckBox fitImgCheckBox = new JCheckBox("Fit:");
    private TitledBorder sliderBorder;
    private JScrollPane scrollPane = new JScrollPane();
    private ImageCanvas canvas  = new ImageCanvas();
    private JPanel columnHeader = new JPanel();
    private JLabel lblZoom = new JLabel("Zoom: 100%");
    
    private double zoomLevel = 1;
    private double fitPercentageWidth = 1.0;
    private double fitPercentageHeight = 1.0;
    private double scaleWidth = 1.0;
    private double scaleHeight = 1.0;
    private double scale = 1.0;
    private AffineTransform at;
    
    private int stretchWidth;
    private int stretchHeight;     
    private String visibleWhen;
    
    public XImageViewer() {
        if( !Beans.isDesignTime() ) {
            init();
        }
        
        if( Beans.isDesignTime() ) {
            setPreferredSize(new Dimension(40,40));
        }
        new MouseEventSupport(this).install();         
    }
    
    private void init() {
        super.setLayout(new BorderLayout());
        super.setBorder(BorderFactory.createEtchedBorder());
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(canvas);
        
        super.add(scrollPane, BorderLayout.CENTER);
        
        fitImgCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(fitImage)
                    fitImage = false;
                else
                    fitImage = true;
                
                if( fitImage ) {
                    zoomSlider.setValue(100);
                    zoomSlider.setEnabled(false);
                } else {
                    zoomSlider.setEnabled(true);
                }
                
                
                repaint();
            }
        });
        setFitImage(true);
    }
    
    public void setLayout(LayoutManager mgr) {;}
    
    public void refresh() {
        if ( dynamic ) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    render();
                }
            });
        }
        
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
    }
    
    public void load() {
        if ( !dynamic ) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    render();
                }
            });
        }
    }
    
    public int compareTo(Object o) {
        return UIControlUtil.compare(this, o);
    }
    
    public Map getInfo() { 
        return null; 
    }      
    
    public int getStretchWidth() { return stretchWidth; } 
    public void setStretchWidth(int stretchWidth) {
        this.stretchWidth = stretchWidth; 
    }

    public int getStretchHeight() { return stretchHeight; } 
    public void setStretchHeight(int stretchHeight) {
        this.stretchHeight = stretchHeight;
    }    
    
    public String getVisibleWhen() { return visibleWhen; } 
    public void setVisibleWhen( String visibleWhen ) {
        this.visibleWhen = visibleWhen;
    }    
    
    //<editor-fold defaultstate="collapsed" desc="  helper method(s)  ">
    private Image getImage() {
        Image image = null;
        try {
            Object value = null;
            
            try {
                value = UIControlUtil.getBeanValue(this);
            } catch(Throwable t) {;}
            
            if( value != null ) {
                if ( value instanceof InputStream ) {
                    image = ImageIO.read((InputStream)value);
                } 
                else {
                    ImageIcon ii = resolveImage( value ); 
                    image = (ii == null ? null : ii.getImage()); 
                }
            }
        } 
        catch(Throwable t) {
            t.printStackTrace();
        }
        
        return image;
    }
        
    private void attachAdvancedOptions() {
        if( advanced ) {
            //if ( columnHeader == null ) {
            columnHeader.setBorder(BorderFactory.createEtchedBorder());
            columnHeader.setLayout( new FlowLayout(FlowLayout.LEFT, 1, 1) );
            
            zoomSlider = new JSlider(10,200,100);
            zoomSlider.addChangeListener(new ChangeListener() 
            {
                public void stateChanged(ChangeEvent e) 
                {
                    zoomLevel = (zoomSlider.getValue()/100.00);
                    //sliderBorder.setTitle("Zoom: " + (int)(zoomLevel * 100) + "%");
                    lblZoom.setText("Zoom: " + (int)(zoomLevel * 100) + "%");
                    
                    canvas.repaint();
                    canvas.revalidate();
                }
            });
            if( fitImage ) zoomSlider.setEnabled(false);
            
            columnHeader.add(lblZoom);
            columnHeader.add(zoomSlider);
            columnHeader.add(fitImgCheckBox);
            //}
            add(columnHeader, BorderLayout.NORTH);
        } else {
            remove(columnHeader);
        }
    }
    
    private void render() {
        if(advanced == true) {
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        } else if(fitImage == true) {
            advanced = false;
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        }
    }
    
    private Base64Cipher base64;
    private Base64Cipher getBase64Cipher() {
        if (base64 == null) {
            base64 = new Base64Cipher(); 
        }
        return base64; 
    }    
    
    private ImageIcon resolveImage( Object value ) throws Exception {
        if ( value == null ) {
            return null;
        }
        else if (value instanceof byte[]) { 
            return new ImageIcon((byte[]) value); 
        } 
        else if (value instanceof URL) {
            return new ImageIcon((URL) value); 
        } 
        else if (value instanceof File) {
            return new ImageIcon( ((File) value).toURI().toURL() ); 
        } 
        else if (value instanceof ImageIcon) {
            return (ImageIcon) value;
        } 
        else if ( value instanceof Image ) {
            return new ImageIcon((Image) value); 
        } 
        else if (value instanceof String) { 
            String str = value.toString().toLowerCase(); 
            if (str.matches("[a-zA-Z]{1,}://.*")) { 
                return new ImageIcon(new URL(value.toString())); 
            } 
            else if ( getBase64Cipher().isEncoded(value.toString())) {
                Object o = getBase64Cipher().decode(value.toString(), false); 
                return resolveImage( o ); 
            }
            else {
                URL url = getClass().getClassLoader().getResource((String) value);
                if ( url != null ) {
                    return new ImageIcon( url ); 
                }
            }
        } 
        
        return null; 
    }    
    
    //</editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc="  Getters/Setters  ">
    
    public String[] getDepends() {
        return depends;
    }
    
    public void setDepends(String[] depends) {
        this.depends = depends;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void setBinding(Binding binding) {
        this.binding = binding;
    }
    
    public Binding getBinding() {
        return binding;
    }
    
    public boolean isAdvanced() {
        return advanced;
    }
    
    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
        attachAdvancedOptions();
    }
    
    public boolean isFitImage() {
        return fitImage;
    }
    
    public void setFitImage(boolean fitImage) {
        this.fitImage = fitImage;
        if( fitImgCheckBox != null )
            fitImgCheckBox.setSelected(fitImage);
    }
    
    public boolean isDynamic() {
        return dynamic;
    }
    
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
        
    public String getEmptyImage() {
        return emptyImage;
    }
    
    public void setEmptyImage(String emptyImageName) {
        this.emptyImage = emptyImageName;
    }
    
    public Icon getEmptyImageIcon() {
        return emptyImageIcon;
    }
    
    public void setEmptyImageIcon(Icon emptyImageIcon) {
        this.emptyImageIcon = emptyImageIcon;
    }
    
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if( canvas != null ) canvas.setBackground(bg);
    }

    public void setPropertyInfo(PropertySupport.PropertyInfo info) {
    }
    
    // </editor-fold>
        
    //<editor-fold defaultstate="collapsed" desc="  ImageCanvas (class)  ">
    private class ImageCanvas extends JPanel {
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Image image = getImage();            
            boolean usingEmptyIcon = false;
            
            if ( image == null && emptyImageIcon instanceof ImageIcon ) {
                usingEmptyIcon = true;
                image = ((ImageIcon) emptyImageIcon).getImage();
            }
            
            if ( image == null ) return;
            
            width = (int) (image.getWidth(this) * zoomLevel);
            height = (int) (image.getHeight(this) * zoomLevel);
            
            if( isFitImage() && !Beans.isDesignTime() ) {
                calculateFit(image);
                Graphics2D g2 = (Graphics2D)g.create();
                at = AffineTransform.getTranslateInstance(fitPercentageWidth, fitPercentageHeight);
                at.scale(scale, scale);
                g2.drawImage(image, at, this);
                updateSize(image.getWidth(this), image.getHeight(this));
                g2.dispose();
            } else {
                g.drawImage(image, 0, 0, width, height, null);
                updateSize(width, height);
                g.dispose();
            }
            
            if( !usingEmptyIcon ) image.flush();
        }
        
        private void updateSize(final int width, final int height) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    setPreferredSize(new Dimension(width, height));
                }
            });
        }
        
        private void calculateFit(Image image) {
            scaleWidth = scrollPane.getViewport().getExtentSize().getWidth() / width;
            scaleHeight = scrollPane.getViewport().getExtentSize().getHeight() / height;
            scale = Math.min(scaleWidth, scaleHeight);
            fitPercentageWidth = (scrollPane.getViewport().getExtentSize().getWidth() - (scale * image.getWidth(this)))/2;
            fitPercentageHeight = (scrollPane.getViewport().getExtentSize().getHeight() - (scale * image.getHeight(this)))/2;
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" ActiveControl implementation ">    
    
    private ControlProperty property;
    
    public ControlProperty getControlProperty() { 
        if ( property == null ) {
            property = new ControlProperty(); 
        } 
        return property; 
    } 
    
    public String getCaption() { 
        return getControlProperty().getCaption(); 
    }    
    public void setCaption(String caption) { 
        getControlProperty().setCaption( caption ); 
    }
    
    public char getCaptionMnemonic() {
        return getControlProperty().getCaptionMnemonic();
    }    
    public void setCaptionMnemonic(char c) {
        getControlProperty().setCaptionMnemonic(c);
    }

    public int getCaptionWidth() {
        return getControlProperty().getCaptionWidth();
    }    
    public void setCaptionWidth(int width) {
        getControlProperty().setCaptionWidth(width);
    }

    public boolean isShowCaption() {
        return getControlProperty().isShowCaption();
    } 
    public void setShowCaption(boolean show) {
        getControlProperty().setShowCaption(show);
    }
    
    public Font getCaptionFont() {
        return getControlProperty().getCaptionFont();
    }    
    public void setCaptionFont(Font f) {
        getControlProperty().setCaptionFont(f);
    }
    
    public String getCaptionFontStyle() { 
        return getControlProperty().getCaptionFontStyle();
    } 
    public void setCaptionFontStyle(String captionFontStyle) {
        getControlProperty().setCaptionFontStyle(captionFontStyle); 
    }    
    
    public Insets getCellPadding() {
        return getControlProperty().getCellPadding();
    }    
    public void setCellPadding(Insets padding) {
        getControlProperty().setCellPadding(padding);
    }    

    // </editor-fold>        
    
}