/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import com.lowagie.text.Font;
import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.DefaultValues;

/**
 * Handles fonts for the PDF plugin
 *
 * @author alb
 */
public class PdfFont implements Cloneable
{
    /** the iText font */
    private Font iTfont = null;

    /** the font typeface in the form of a string used by iText */
    private String typeface = null;

    /** font size */
    private float size = 0f;

    /** color of font */
//    private RgbColor color;

    /** is font bold? */
    private boolean bold;

    /** is fond italic? */
    private boolean italics;

    /** file and line number of last change */
    private Source source;

    /** font factory for creating an iText font */
    private PdfFontFactory fontFactory;

    private GDD gdd;
    private PdfData pdfData;

    public PdfFont( PdfData pdd )
    {
        pdfData = pdd;
        gdd = pdd.getGdd();
        fontFactory = new PdfFontFactory( gdd, pdd );

        setToDefault();
    }

    /**
     * Constructor for cloning an existing PdfFont, but specifying a different typeface.
     *
     * @param pdd PDF data
     * @param fontName the name of the new font/typeface
     * @param existingFont the font to clone the other attributes from
     */
    public PdfFont( PdfData pdd, String fontName, PdfFont existingFont )
    {
        gdd = pdd.getGdd();
        pdfData = pdd;
        typeface = fontName;
        size = existingFont.getSize();
        bold = existingFont.getBold();
        italics = existingFont.getItalics();
        source = existingFont.getSource();

        iTfont = fontFactory.createItextFont( this );
    }

    /**
     * Initializes all PdfFont fields to defaults, and sets line number to 0
     */
    public void setToDefault()
    {
        typeface  = DefaultValues.FONT_TYPEFACE;
//        color     = new RgbColor();
        size      = DefaultValues.FONT_SIZE;
        bold      = DefaultValues.FONT_BOLD;
        italics   = DefaultValues.FONT_ITALIC;
        source    = new Source();

        iTfont = fontFactory.createItextFont( this );
    }

    @Override
    public PdfFont clone()
    {
        Object clonedFont = null;

        try {
            clonedFont = super.clone();
        }
        catch (CloneNotSupportedException e) {
            //This should not happen, since this class is Cloneable.
        }

        return( (PdfFont) clonedFont );
    }

    //=== getters and setters ===//

    public boolean getBold()
    {
        return( bold );
    }

    public boolean getItalics()
    {
        return( italics );
    }

    /**
     * Get the iText font
     *
     * @return  the iText font
     */
    public Font getItextFont()
    {
        return( iTfont );
    }

    public String getFace()
    {
        return( typeface );
    }

    public float getSize()
    {
        return( size );
    }

    public Source getSource()
    {
        return( source );
    }

    /**
     * set bold on/off and re-create the iText font to record the change.
     *
     * @param onOff the new value for the italics setting
     * @param newSource the file and line # of the token that changed italics
     */
    public void setBold( final boolean onOff, final Source newSource )
    {
        if( bold != onOff ) {
            bold = onOff;
            source = newSource;
//        iTfont = createFont( this );
        iTfont = fontFactory.createItextFont( this );
        }
    }

    /**
     * set italics on/off and re-create the iText font to record the change.
     *
     * @param onOff the new value for the italics setting
     * @param newSource the file and line # of the token that changed italics
     */
    public void setItalics( final boolean onOff, final Source newSource )
    {
        if( italics != onOff ) {
            italics = onOff;
            source = newSource;

            iTfont = fontFactory.createItextFont( this );
        }
    }

    public void setSize( final float newSize, final Source newSource )
    {
        if( size != newSize ) {
            size = newSize;
            source = newSource;

        iTfont = fontFactory.createItextFont( this );
        }
    }

    public void setFace( final String newFace, final Source newSource )
    {
        if( ! typeface.equals( newFace )) {
            typeface = newFace;
            source = newSource;

            iTfont = fontFactory.createItextFont( this );
        }
    }
}
