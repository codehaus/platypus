/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.GDD;
import org.pz.platypus.Source;

/**
 * Handles fonts for the PDF plugin
 *
 * @author alb
 */
public class HtmlFont
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

    private GDD gdd;
    private HtmlData pdfData;

    HtmlFont( HtmlData pdfData )
    {
        this.pdfData = pdfData;
        gdd = pdfData.getGdd();
        setToDefault();
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

        iTfont = createFont( this );
    }

    /**
     * Creates an iText Font object based on the class fields
     * @param f the PdfFont containing the parameters for the font
     * @return the iText Font object
     */
    Font createFont( final HtmlFont f )
    {
        int style = 0;
 //       Color col  = new Color( color.getR(), color.getG(), color.getB() );
        Font font;

        String iTextFontName = createItextFontName( f );
        if( ! isBase14Font( f.typeface )) {
            style = computeItextStyle();
        }

        try {
        font = FontFactory.getFont( iTextFontName, BaseFont.CP1252, BaseFont.EMBEDDED,
                    size, style );
        }
        catch( Exception ex ) {
            System.out.println( "Exception in PdfFont.createFont() for FontFactory.getFont() for "
            + iTextFontName );
            font = null;
        }

        if( font == null || font.getBaseFont() == null ) {
            gdd.logWarning( "iText could not find font for: " + iTextFontName + ". Using Times-Roman" );
            font = FontFactory.getFont( BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.EMBEDDED,
                    size, style );
        }

        return( font );
    }


    /**
     * Determines whehter the current font is one of the Base14 Acrobat fonts, built into
     * every PDF reader. These fonts require special hanldling, and so this routine helps
     * identify them, based on their font face name.
     *
     * @param fontName name of the font face
     * @return true if it's a base14 font name, false otherwise.
     */
    boolean isBase14Font( final String fontName )
    {
        if( fontName.equals( "COURIER" ) ||
            fontName.equals( "DINGBATS" ) ||
            fontName.equals( "HELVETICA" ) ||
            fontName.equals( "SYMBOL" )  ||
            fontName.equals( "TIMES_ROMAN" )) {
            return( true );
        }

        return( false );
    }

    /**
     * Get the name by which iText refers to this font. This routine is mostly occupied
     * with the special handling of the base14 fonts. For all other fonts, this routine
     * simply returns its existing name.
     *
     * @param f PdfFont whose iText name we're getting
     * @return a string containing the iText usable name for this font.
     */
    String createItextFontName( final HtmlFont f )
    {
        String iTextFontName;
        String typefaceName = f.typeface;

        // handle the different versions of base14 fonts
        if ( typefaceName.equals( "COURIER" )) {
            if ( f.bold )
            {
                if ( f.italics )
                    iTextFontName = BaseFont.COURIER_BOLDOBLIQUE;
                else
                    iTextFontName = BaseFont.COURIER_BOLD;
            }
            else
            if ( f.italics )
                iTextFontName = BaseFont.COURIER_OBLIQUE;
            else
                iTextFontName = BaseFont.COURIER;
        }
        else
        if ( typefaceName.equals( "HELVETICA" )) {
            if ( f.bold )
            {
                if ( f.italics )
                    iTextFontName = BaseFont.HELVETICA_BOLDOBLIQUE;
                else
                    iTextFontName = BaseFont.HELVETICA_BOLD;
            }
            else
            if ( f.italics )
                iTextFontName = BaseFont.HELVETICA_OBLIQUE;
            else
                iTextFontName = BaseFont.HELVETICA;
        }
        else
        if ( typefaceName.equals( "TIMES_ROMAN" )) {
            if ( f.bold )
            {
                if ( f.italics )
                    iTextFontName = BaseFont.TIMES_BOLDITALIC;
                else
                    iTextFontName = BaseFont.TIMES_BOLD;
            }
            else
            if ( f.italics )
                iTextFontName = BaseFont.TIMES_ITALIC;
            else
                iTextFontName = BaseFont.TIMES_ROMAN;
        }
        else
        if ( typefaceName.equals( "SYMBOL" )) {
            iTextFontName = BaseFont.SYMBOL;
        }
        else
        if ( typefaceName.equals( "DINGBATS" )) {
            iTextFontName = BaseFont.ZAPFDINGBATS;
        }
        else
        // not a base14 font. So make sure we've loaded the font files for Platypus
        // then look up this font among them. If it's still not there, then return
        // a TIMES_ROMAN and note the error.
        {
//            if( htmlData.getTypefaceMap() == null ) {
//                TypefaceMap typefaceMap = new TypefaceMap( htmlData.getGdd() );
//                typefaceMap.loadFamilies();
//                htmlData.setTypefaceMap( typefaceMap );
//            }

            // if the font files for this typeface/font family have not been previously registered,
            // then get the filenames from the typefaceMap and register them in iText's FontFactory
            if( ! FontFactory.isRegistered( typefaceName )) {
                String[] fontFiles = pdfData.getTypefaceMap().getFamilyFilenames( typefaceName );
                for( String fontFile : fontFiles ) {
                    FontFactory.register( fontFile );
                }
                gdd.log( "Registered fonts for " + typefaceName + " in iText" );
            }

            if( FontFactory.isRegistered( typefaceName )) {
                iTextFontName = typefaceName;
            }
            else {
            // the filename does not exist on the system, so substitute TIMES_ROMAN
                iTextFontName = BaseFont.TIMES_ROMAN;
            }
//            }
//            else {
//                gdd.logInfo(
//                        gdd.getLit( "FILE#" ) + " " + source.getFileNumber() + " " +
//                        gdd.getLit( "LINE#" ) + " " + source.getLineNumber() + ": " +
//                        gdd.getLit( "ERROR.INVALID_FONT_TYPEFACE" ) + " " +
//                        f.typeface + " " +
//                        gdd.getLit( "IGNORED" ));
//                iTextFontName = typeface;
        }
        return( iTextFontName );
    }

    /**
     * iText font style captures bold, italic, strikethru, underline. Since we handle
     * strikethrough and underline ourselves, we use it to communicate italic and bold
     * only. This computation done here.
     *
     * @return the iText Style
     */
    int computeItextStyle()
    {
        int style = 0;

        if( italics && bold ) {
            style |= Font.BOLDITALIC;
        }
        else
        if( italics ) {
            style |= Font.ITALIC;
        }
        else
        if( bold ) {
            style |= Font.BOLD;
        }

        return( style );
    }

    //=== getters and setters ===//
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
            iTfont = createFont( this );
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
            iTfont = createFont( this );
        }
    }

    public void setSize( final float newSize, final Source newSource )
    {
        if( size != newSize ) {
            size = newSize;
            source = newSource;
            iTfont = createFont( this );
        }
    }

    public void setFace( final String newFace, final Source newSource )
    {
        if( ! typeface.equals( newFace )) {
            typeface = newFace;
            source = newSource;
            iTfont = createFont( this );
        }
    }
}