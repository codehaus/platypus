/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.Token;
import org.pz.platypus.GDD;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * This is the command-equivalent that is called by PDF plugin to 'execute' a symbol.
 * It looks what the chars to ouput are to generate the symbol in PDF and it outputs
 * those as text. If Unicode conversion is necessary, that is performed as well.
 *
 * @author alb
 */
public class PdfSymbol implements IOutputCommand
{
    private String root;
    private String passedValue;

    public PdfSymbol( final String symbolRoot, final String stringToEmit )
    {
        assert( root != null );
        assert( stringToEmit != null );

        root = symbolRoot;
        passedValue = stringToEmit;
    }

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        if( context == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdd = (PdfData) context;

        String fontName = getFontName( passedValue, pdd.getGdd(), tok );
        String charCode = getCharCode( passedValue );

        if( charCode != null && ! charCode.isEmpty() ) {
            pdd.getOutfile().emitChar( charCode, fontName );
        }
        else {
            // if a problem occurred, emit the character escape code as text, such as: \\u12CD.
            pdd.getOutfile().emitText( passedValue );
        }
        return 0;
    }

    /**
     * Get the Unicode character code to emit
     * @param symEquiv the symbol equivalent (consisting of what is on the right of the = sign
     *        in the symbols resource file. Could contain font + char, or just char to emit.
     * @return  char code, or empty string if an error occurs.
     */
    public String getCharCode( final String symEquiv )
    {
        String symEntry;

        // first remove any font specification
        if( symEquiv.startsWith( "{" )) {
            symEntry = symEquiv.substring( symEquiv.indexOf( '}' ) + 1 );
            if( symEntry.isEmpty() ) {
                return( "" );
            }
        }
        else {
            symEntry = symEquiv;
        }

        // should now start with the \\\\ escape sequence
        if( symEntry.startsWith( "\\\\" )) {
            String charAsString = getUnicodeValue( symEntry );
            return( charAsString );
        }

        return( "" );
    }
    
    public String getFontName( final String symbolEquiv, final GDD gdd, final Token tok )
    {
        if( symbolEquiv.startsWith( "{" )) {
            return( extractFontName( symbolEquiv, gdd, tok ));
        }

        // if the symbol entry does not begin with {, there is no font specified.
        return( "" );
    }

    /**
     * Extracts the font name from an extended entry in PdfSymbols.properties, such as:
     * [trademark]={SYMBOL}\\u00E4, where the font name is between braces (so SYMBOL, in this example).
     *
     * @param fontName the string starting with the opening brace
     * @param gdd to look up literals in case of an error
     * @param tok to look up file/line# for use in error message, if any
     * @return the name of the font or an empty string in case of error.
     */
    String extractFontName( final String fontName, final GDD gdd, final Token tok )
    {
        if( fontName == null ) {
            return( "" );
        }

        int closingbrace = fontName.indexOf( '}' );
        if( closingbrace < 2 ) { // neg = { char missing, 0 is not possible, 1 means no name was specified.
            assert( gdd != null );
            assert( tok != null );
            gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                            gdd.getLit( "ERROR.INVALID_SYMBOL_DEFINITION" ) + ": " 
                            +  fontName + " " +
                            gdd.getLit( "IGNORED" ));
            return( "" );
        }

        return( fontName.substring( 1, closingbrace ));
    }

    /**
     * Accepts a symbol specified as a unicode symbol (such as \\u12CD). In Java, this
     * becomes \\\\u12CD. So, this method strips off the leading \ and converts the
     * remaining value into a 1-symbol string.
     *
     * @param symbolAsUnicode the Unicode number as a hex value.
     * @return the 1-symbol string that symbolAsUnicode resolves to
     */
    String getUnicodeValue( final String symbolAsUnicode )
    {
        int k;

        if( symbolAsUnicode.length() < 4 ) {
            return( "" );
        }

        try {
            k = getCharValueForUnicode( symbolAsUnicode.substring( 3 ));
        }
        catch( NumberFormatException nfe ) {
            return( "" );
        }
        String charAsString = new String( Character.toChars( k ));
        return( charAsString );
    }

    /**
     * Converts a unicode value in the format u9999 to a char representing that number,
     * which can be correctly output as the respective glyph by iText.
     *
     * @param unicodeValue four-digit hex value that has to be converted to a char
     * @return char representing the unicode value
     */
    public char getCharValueForUnicode( final String unicodeValue )
    {
        int i = Integer.parseInt( unicodeValue, 16 );
        return (char) i;
    }

    public String getRoot()
    {
        return( root );
    }

    public String getSymEquivalent()
    {
        return( passedValue );
    }
}
