/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.rtf;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import org.pz.platypus.GDD;
import org.pz.platypus.Source;
import org.pz.platypus.TypefaceMap;
import org.pz.platypus.DefaultValues;

/**
 * Handles fonts for the RTF plugin
 *
 * @author alb
 */
public class RtfFont
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
    private RtfData rtfData;

    RtfFont( RtfData rtfData )
    {
        this.rtfData = rtfData;
        gdd = rtfData.getGdd();
        setToDefault();
    }

    /**
     * Constructor for cloning an existing PdfFont, but specifying a different typeface.
     *
     * @param rdd PDF data
     * @param fontName the name of the new font/typeface
     * @param existingFont the font to clone the other attributes from
     */
    public RtfFont( RtfData rdd, String fontName, RtfFont existingFont )
    {
        gdd = rdd.getGdd();
        rtfData = rdd;
        typeface = fontName;
        size = existingFont.getSize();
        bold = existingFont.getBold();
        italics = existingFont.getItalics();
        source = existingFont.getSource();

        iTfont = createFont( this );
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
    Font createFont( final RtfFont f )
    {
        int style = 0;
 //       Color col  = new Color( color.getR(), color.getG(), color.getB() );
        Font font = null;

        String iTextFontName = createItextFontName( f );
        if( iTextFontName == null ) { // if the font is not in the fontlist nor is it a Base14 font
            f.typeface = DefaultValues.FONT_TYPEFACE;
            iTextFontName = BaseFont.TIMES_ROMAN;
        }

        if( ! isBase14Font( f.typeface )) {
            style = computeItextStyle();
            font = getIdentityHFont( iTextFontName, size, style );
        }

        if( font == null ) {
            font = getCp1252Font( iTextFontName, size, style );
        }

        if( font == null || font.getBaseFont() == null ) { //TODO: Make error msg use literals
            gdd.logWarning( "iText could not find font for: " + iTextFontName + ". Using Times-Roman" );
            font = FontFactory.getFont( BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.EMBEDDED,
                    size, style );
        }

        return( font );
    }

    /**
     * Opens a font using the IDENTITY-H encoding.
     *
     * @param fontName  the name assigned to the font in the font list
     * @param size the size in points
     * @param style bold, italic, etc.
     * @return the Font if opened; null if the file could not be opened or an error occurred.
     */
    Font getIdentityHFont( final String fontName, float size, int style )
    {
        // for the time being, if the font is an .otf font, don't open it with IDENTITY-H. Symbola is an
        // exception that seems to work.
        // need to explore the problem further in iText. It's recorded as PLATYPUS-32 in JIRA at Codehaus.
        // returning null forces the calling routine to open the font with CP1252 encoding, which is
        // fine for .otf fonts
        if( isFileOtf( fontName ) && ( ! fontName.toLowerCase().equals( "symbola" ))) {
            return( null );
        }

        Font font;
        try {
            font = FontFactory.getFont( fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, style );
        }
        catch( Exception ex ) {
            font = null;
        }

        return( font );
    }

    /**
     * Looks up a font in the fontlist and determines whether it uses the .otf font format.
     * @param fontName name of font to verify
     * @return true if it's an .otf font, otherwise false.
     */
    private boolean isFileOtf(String fontName)
    {
        String[] fontFilenames = lookupFontFilenames( fontName );
        if( fontFilenames.length > 0 ) {
            if( fontFilenames[0].endsWith( ".otf" )) {
                return( true );
            }
        }
        return( false );
    }

    /**
     * Gets the font with CP1252 (aka WINANSI) encoding
     * @param fontName name of font to get
     * @param size  size in points
     * @param style bold, italic, etc.
     * @return the font, or null if an error occurred.
     */
    Font getCp1252Font( final String fontName, float size, int style )
    {
        Font font;
        try {

            font = FontFactory.getFont( fontName, BaseFont.CP1252, BaseFont.EMBEDDED, size, style );
        }
        catch( Exception ex ) {
            font = null;
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
     * with the special handling of the base14 fonts.
     *
     * For all other fonts, this method makes sure the font is registered with iText and
     * returns its name as registered by iText (which is the family name for the font).
     *
     * @param f PdfFont whose iText name we're getting
     * @return a string containing the iText usable name for this font.
     */
    String createItextFontName( final RtfFont f )
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
        // It's not a base14 font. So make sure we've loaded the font files for Platypus
        // then look up this font among them. If it's still not there, then return
        // a TIMES_ROMAN and note the error.
        {
            if( ! FontFactory.isRegistered( typefaceName )) {
                if ( ! findAndRegisterFont(typefaceName)) {
                    return (null);
                }
            }

            if( FontFactory.isRegistered( typefaceName )) {
                iTextFontName = typefaceName;
            }
            else {
                // in theory, cannot get here.
                gdd.logWarning( gdd.getLit( "COULD_NOT_FIND") + " " + typefaceName + " " +
                                gdd.getLit( "IN_FONT_REGISTER" ) + ". " +
                                gdd.getLit( "USING_TIMES_ROMAN" ) + "." );
                iTextFontName = null;
            }
        }
        return( iTextFontName );
    }

    /**
     * Get the filenames from the typefaceMap and register them in iText's FontFactory.
     *
     * @param typefaceName name of the typeface
     * @return true if the font is now registered, false if an error occurred. (Font was not in fontlist.)
     */
    boolean findAndRegisterFont( String typefaceName )
    {
        String[] fontFiles = lookupFontFilenames( typefaceName );
        if( fontFiles.length == 0 ) {
            gdd.logWarning( gdd.getLit( "COULD_NOT_FIND") + " " + typefaceName + " " +
                            gdd.getLit( "IN_FONT_LIST" ) + ". " +  gdd.getLit( "USING_TIMES_ROMAN" ) + "." );
            return( false );
        }
        else {
            for( String fontFile : fontFiles ) {
                FontFactory.register( fontFile, typefaceName );
            }
            gdd.log( "Registered fonts for " + typefaceName + " in iText" );
            return( true );
        }
    }

    /**
     * Get the names of the actual font files that are the implementation of this typeface
     * @param typefaceName the typefaces
     * @return an array of strings containing the file names.
     */
    String[] lookupFontFilenames( String typefaceName )
    {
        assert rtfData != null;
        assert typefaceName != null;

        TypefaceMap typefaceMap = rtfData.getTypefaceMap();
        if( typefaceMap == null  ) {
            rtfData.loadTypefaceMap();
        }

        return( typefaceMap.getFamilyFilenames( typefaceName ));
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