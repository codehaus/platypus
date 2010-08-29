/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.pz.platypus.GDD;
import org.pz.platypus.TypefaceMap;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Handles the complexities of creating a font for iText
 *
 * @author alb
 */
public class PdfFontFactory
{
    private GDD gdd;
    private PdfData pdfData;

    public PdfFontFactory( GDD Gdd, PdfData pdd )
    {
        gdd = Gdd;
        pdfData = pdd;
    }

    /**
     * Creates an iText Font object based on a passed-in PdfFont
     *
     * @param f the PdfFont containing the parameters for the font
     * @return the iText Font object
     */
    public Font createItextFont( final PdfFont f )
    {
        PdfFont pf = f;

        int style = 0;
 //       Color col  = new Color( color.getR(), color.getG(), color.getB() );
        Font font = null;

        if( pf == null ) {
            pf = new PdfFont( pdfData );
        }

        String iTextFontName = createItextFontName( pf );

        if( ! isBase14Font( pf.getFace() )) {
            style = computeItextStyle( pf );
            font = getIdentityHFont( iTextFontName, pf.getSize(), style );
        }

        if( font == null ) {   //TODO: identify when this would be the case.
            font = getCp1252Font( iTextFontName, pf.getSize(), style );
        }

        if( font == null || font.getBaseFont() == null ) { //TODO: Make error msg use literals
            gdd.logWarning( "iText could not find font for: " + iTextFontName + ". Using Times-Roman" );
            font = FontFactory.getFont( BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.EMBEDDED,
                   pf.getSize(), style );
        }

// #if debug
//        Set fonts = FontFactory.getRegisteredFonts();
//        Set families = FontFactory.getRegisteredFamilies();

        return( font );
    }
    
    /**
     * iText font style captures bold, italic, strikethru, underline. Since we handle
     * strikethrough and underline ourselves, we use it to communicate italic and bold
     * only. This computation done here.
     *
     * @param f the PdfFont whose style is being checked
     * @return the iText Style
     */
    int computeItextStyle( PdfFont f )
    {
        int style = 0;

        if( f.getItalics() && f.getBold() ) {
            style |= Font.BOLDITALIC;
        }
        else
        if( f.getItalics() ) {
            style |= Font.ITALIC;
        }
        else
        if( f.getBold() ) {
            style |= Font.BOLD;
        }

        return( style );
    }

    /**
     * Gets the iText font name for any of the Base14 fonts.
     *
     * @param font the PdfFont whose name we're looking up.
     * @return the iText name or null if an error has occured
     */
    String computeBase14ItextFontName( final PdfFont font )
    {
        final String typefaceName;
        String iTextFontName = null;
        PdfFont f;

        // in the impossible event this gets passed a null, then
        // replace it with the default font. It might be better
        // to just throw an exception. Should revisit this later.
        if( font == null ) {
            f = new PdfFont( pdfData );
        }
        else {
            f = font;
        }

        typefaceName = f.getFace();

        if ( typefaceName.equals( "COURIER" )) {
            if ( f.getBold() ) {
                if ( f.getItalics() ) {
                    iTextFontName = BaseFont.COURIER_BOLDOBLIQUE;
                }
                else {
                    iTextFontName = BaseFont.COURIER_BOLD;
                }
            }
            else
            if ( f.getItalics() ) {
                iTextFontName = BaseFont.COURIER_OBLIQUE;
            }
            else
                iTextFontName = BaseFont.COURIER;

            return( iTextFontName );
        }

        if ( typefaceName.equals( "HELVETICA" )) {
            if ( f.getBold() ) {
                if ( f.getItalics() ) {
                    iTextFontName = BaseFont.HELVETICA_BOLDOBLIQUE;
                }
                else {
                    iTextFontName = BaseFont.HELVETICA_BOLD;
                }
            }
            else
            if ( f.getItalics() ) {
                iTextFontName = BaseFont.HELVETICA_OBLIQUE;
            }
            else {
                iTextFontName = BaseFont.HELVETICA;
            }

            return( iTextFontName );
        }

        if ( typefaceName.equals( "TIMES_ROMAN" )) {
            if ( f.getBold() )
            {
                if ( f.getItalics() )
                    iTextFontName = BaseFont.TIMES_BOLDITALIC;
                else
                    iTextFontName = BaseFont.TIMES_BOLD;
            }
            else
            if ( f.getItalics() )
                iTextFontName = BaseFont.TIMES_ITALIC;
            else
                iTextFontName = BaseFont.TIMES_ROMAN;

            return( iTextFontName );
        }

        if ( typefaceName.equals( "SYMBOL" )) {
            iTextFontName = BaseFont.SYMBOL;
            return( iTextFontName );
        }

        if ( typefaceName.equals( "DINGBATS" )) {
            iTextFontName = BaseFont.ZAPFDINGBATS;
            return( iTextFontName );
        }

        // in theory, impossible, since the font is validated before the function is called.
        return( iTextFontName );
    }

    /**
     * Get the name by which iText refers to this font. This routine is mostly occupied
     * with the special handling of the Base14 fonts.
     *
     * For all other fonts, this method makes sure the font is registered with iText and
     * returns its name as registered by iText (which is the family name for the font).
     *
     * @param f PdfFont whose iText name we're getting
     * @return a string containing the iText usable name for this font. If passed-in font is
     *             null or the font name is not recognized or found, TIMES_ROMAN is returned.
     */
    String createItextFontName( final PdfFont f )
    {
        if( f == null ) {
            errFontNotFound( "NULL" );
            return( BaseFont.TIMES_ROMAN );
        }

        String typefaceName = f.getFace();

        // if it's a Base14 font, compute the name
        if ( isBase14Font( typefaceName )) {
            return( computeBase14ItextFontName( f ));
        }

        // It's not a base14 font, so is the font already registered in iText?
        if ( isRegisteredWithItext( typefaceName )) {
//        if( FontFactory.isRegistered( typefaceName )) {
            return( typefaceName );
        }

        // If not, load the font from Platypus into iText, then look up the
        // iText name and return that. In case of error, return TIMES_ROMAN.

        if ( ! findAndRegisterFont( typefaceName )) {
            errFontNotFound( typefaceName );
            return( BaseFont.TIMES_ROMAN );
        }

        if ( isRegisteredWithItext( typefaceName )) {
//        if( FontFactory.isRegistered( typefaceName )) {
            return( typefaceName );
        }

        // in theory, cannot get here.
//        Set regFonts = FontFactory.getRegisteredFonts();   // debug statements, in case we do get here.
//        Set regFamilies = FontFactory.getRegisteredFamilies();
        errFontNotFound( typefaceName );
        return( BaseFont.TIMES_ROMAN );
    }

    /**
     * This test whether a font is registered by name or by family name in iText. Either way is
     * good enough for our purposes.
     *
     * @param fontName the font we're checking
     * @return true if iText has the font registered by name or by family name, false otherwise.
     */
    boolean isRegisteredWithItext( final String fontName )
    {
        assert( fontName != null );
        if( FontFactory.isRegistered( fontName )) {
            return( true );
        }

        Set<String> regFamilies = (Set<String>) FontFactory.getRegisteredFamilies();
        if( regFamilies.contains( fontName.toLowerCase())) {
            return( true );
        }

        return( false );
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
            errFontNotFound( typefaceName );
            return( false );
        }
        else {

            for( String fontFile : fontFiles ) {
                registerOneFont( fontFile, typefaceName );
            }

            return( true );
        }
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
     * Opens a font using the IDENTITY-H encoding.
     *
     * @param fontName  the name assigned to the font in the font list
     * @param size the size in points
     * @param style bold, italic, etc.
     * @return the Font if opened; null if the file could not be opened or an error occurred.
     */
    Font getIdentityHFont( final String fontName, float size, int style )
    {
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
     * Get the names of the actual font files that are the implementation of this typeface
     * @param typefaceName the typefaces
     * @return an array of strings containing the file names.
     */
    String[] lookupFontFilenames( String typefaceName )
    {
        assert pdfData != null;
        assert typefaceName != null;

        TypefaceMap typefaceMap = pdfData.getTypefaceMap();
        if( typefaceMap == null  ) {
            pdfData.loadTypefaceMap();
        }

        return( typefaceMap.getFamilyFilenames( typefaceName ));
    }

    /**
     * Actual font registration. Takes care of specious warning emitted by iText registering
     * a TrueType collection (.ttc) file
     *
     * called from RegisterOneFont()
     *
     * @param fontFile the complete filename including the path
     */
    private void registerFont( String fontFile )
    {
        if( ! fontFile.toLowerCase().endsWith( ".ttc" )) {
            FontFactory.register( fontFile );
            return;
        }

        // if the file is a TrueTypeCollection (.ttc), iText generates an error message
        // which it writes to stderr, and then registers the fonts. Since, there's no way
        // to prevent this unneeded message, we do the registration manually here to avoid
        // printing out the error message (about which the user can do nothing).

        String[] names;

        try
        {
            names = BaseFont.enumerateTTCNames(fontFile);
            if( names == null ) {
                errTtcFileHandling( fontFile );
                return;
            }
        }
        catch( DocumentException de ) {
            errTtcFileHandling( fontFile );
            return;
        }
        catch( IOException ioe ) {
            errTtcFileHandling( fontFile );
            return;
        }

        for( int i = 0; i < names.length; i++ ) {
            FontFactory.register( fontFile + "," + i );
        }
    }

    /**
     * Unfortunately, iText does not pass along the exception if the file cannot be found.
     * Rather, iText rethrows the exception internally and prints the stack trace. So, the
     * only way to avoid this is to check for each file first before doing the registration.
     *
     * @param fontFile the file location and name
     * @param typeface the name of the typeface to register the font for
     */
    void registerOneFont( final String fontFile, final String typeface )
    {
        if( fontFile == null ) {
            return;
        }

        File ff = new File( fontFile );
        if( ff.exists() ) {
            registerFont( fontFile );
            gdd.log( "Registered fonts for " + typeface + " in iText" );
        }
        else {
            gdd.logWarning( gdd.getLit( "COULD_NOT_FIND_FONT" ) + " " + fontFile + " " +
            gdd.getLit ( "FROM_FONT_LIST" ) + ". " );
        }
    }

    // = = =  error messages, getters and setters = = = //

    void errFontNotFound( String typefaceName )
    {
        gdd.logWarning( gdd.getLit( "COULD_NOT_FIND") + " " +
                        typefaceName + " " +
                        gdd.getLit( "IN_FONT_REGISTER" ) + ". " +
                        gdd.getLit( "USING_TIMES_ROMAN" ) + "." );
    }

    void errTtcFileHandling( final String fontFile )
    {
        gdd.logWarning( gdd.getLit( "ERROR.REGISTERING_TTC_FONT") + " " + fontFile );
    }
}
