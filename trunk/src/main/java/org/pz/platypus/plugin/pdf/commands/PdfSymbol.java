/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.Token;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * This is the command-equivalent that is called by PDF plugin to 'execute' a symbol.
 * It looks what the chars to ouput are to generate the symbol in PDF and it outputs
 * those as text.
 *
 * @author alb
 */
public class PdfSymbol implements OutputCommandable
{
    private String root;
    private String passedValue;

    public PdfSymbol( final String symbolRoot, final String stringToEmit ) {
        assert( root != null );
        assert( stringToEmit != null );

        root = symbolRoot;
        passedValue = stringToEmit;
    }

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        // emits the string representing the symbol as text. It's generally the char or
        // the Unicode encoding for the char in the form: \\u12CD
        PdfData pdf = (PdfData) context;
        if( passedValue.startsWith( "\\\\" )) {
            pdf.getOutfile().emitChar( getCharValueForUnicode( passedValue ));
        }
        else {
            pdf.getOutfile().emitText( passedValue );    
        }
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
           String str = passedValue.substring( 3 );
           int i = Integer.parseInt( str, 16 );
           return (char) i;
    }

    public String getRoot()
    {
        return( root );
    }
}
