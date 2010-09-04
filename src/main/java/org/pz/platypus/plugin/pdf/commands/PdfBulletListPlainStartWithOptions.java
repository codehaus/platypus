/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.FontSelector;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.GDD;
import org.pz.platypus.Symbol;
import org.pz.platypus.Token;
import org.pz.platypus.commands.BulletListPlainStartWithOptions;
import org.pz.platypus.interfaces.ICommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfFont;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.utilities.ErrorMsg;
import org.pz.platypus.utilities.TextTransforms;

/**
 * Implementation of turning on a plain bullet list with user-specified
 * options in the PDF plugin
 *
 * @author alb
 */
public class PdfBulletListPlainStartWithOptions extends BulletListPlainStartWithOptions
{
    /**
     * Extracts the bullet character/symbol from the passed parameter and starts a bullet
     * list using it.
     *
     * @param context the PDF data
     * @param tok token containing bullet list options
     * @param tokNum  number in the token stream
     * @return number of additional tokens consumed
     */
    protected int startNewList( final IOutputContext context, Token tok, int tokNum )
    {
        PdfData pdd = (PdfData) context;
        PdfOutfile outFile = pdd.getOutfile();

        String param = tok.getParameter().getString();
        if( ! param.startsWith( "bullet:" )) {
            errorInvalidOption( pdd.getGdd(), tok );
            outFile.startPlainBulletList( DefaultValues.BULLET );
            return( 0 );
        }

        Chunk bulletSymbol;
        param = TextTransforms.lop( param, "bullet:".length() );
        if( param.startsWith( "{" )) {
            bulletSymbol = lookupBulletSymbol( param.substring( 1 ), tok, pdd );
        }
        else {
            bulletSymbol = extractBulletSymbolFromParam( param );
        }

        outFile.startPlainBulletList( bulletSymbol );

        return( 0 );
    }

    /**
     * Look up the symbol in the symbol table and get the Unicode value and any symbol font info.
     * 
     * @param param value passed with the "bullet:" parameter
     * @param pdd the PDF document data
     * @param tok the token for the whole bullet list command
     * @return Chunk containing for the bullet symbol
     */
    Chunk lookupBulletSymbol( final String param, final Token tok, final PdfData pdd )
    {
        assert( pdd != null );
        assert( tok != null );
        assert( param != null );

        GDD gdd = pdd.getGdd();

        StringBuilder sb = new StringBuilder( 10 );
        int i = 0;
        while( i < param.length() &&
                param.charAt( i ) != '}' &&
                param.charAt( i ) != ']' )
            sb.append( param.charAt( i++ ));

        if( param.charAt( i ) != '}' ) {
            errorUnclosedOption( gdd, tok );
            return( new Chunk( DefaultValues.BULLET ));
        }

        String root = "[" + sb.toString() + "]";
        ICommand value = gdd.getCommandTable().getCommand( root );
        if( ! ( value instanceof Symbol )) {
            errorBulletSymbolNotFound( gdd, tok );
        }

        // get the entry for this symbol. Should be a string that specifies the char value
        // as a Unicode value, with an optional font name.
        PdfSymbol processor = (PdfSymbol) pdd.getCommandTable().getCommand( root );
        String symEquivalent = processor.getSymEquivalent();

        // get the Unicode value and font name, if any
        String font = processor.getFontName( symEquivalent, gdd, tok );
        String sym  = processor.getCharCode( symEquivalent );

        if( font == null || font.isEmpty() ) {
            return( new Chunk( sym ));
        }
        else {
            PdfFont newFont = new PdfFont( pdd, font, pdd.getFont() );
            FontSelector fs = new FontSelector();
            fs.addFont( newFont.getItextFont() );
            Chunk chk = new Chunk( sym, newFont.getItextFont() );
            return( chk );
        }
    }

    /**
     * extracts a literal that serves as the bullet character (e.g. -, >, ->, etc.)
     *
     * @param param parameter portion that specifies the bullet character
     * @return bullet character as a string.
     */
    Chunk extractBulletSymbolFromParam( final String param )
    {
        StringBuilder sb = new StringBuilder( 10 );
        int i = 0;
        while( i < param.length() &&
                param.charAt( i ) != '|' &&
                param.charAt( i ) != ']' )
            sb.append( param.charAt( i++ ));
        return new Chunk( sb.toString() );
    }

    //=== error messages ===//
    
    private void errorInvalidOption( final GDD gdd, final Token tok )
    {
        assert gdd != null;
        assert tok != null;

        StringBuilder msg = new StringBuilder( ErrorMsg.location( gdd, tok ));
        msg.append( gdd.getLit( "ERROR.INVALID_OPTION_FOR_BULLET_LIST" ));
        gdd.logWarning( msg.toString() );
    }

    private void errorUnclosedOption(final GDD gdd, final Token tok )
    {
        assert gdd != null;
        assert tok != null;

        StringBuilder msg = new StringBuilder( ErrorMsg.location( gdd, tok ));
        msg.append( gdd.getLit( "ERROR.UNCLOSED_OPTION_FOR_BULLET_LIST" ));
        gdd.logWarning( msg.toString() );
    }

    private void errorBulletSymbolNotFound(final GDD gdd, final Token tok )
    {
        assert gdd != null;
        assert tok != null;

        StringBuilder msg = new StringBuilder( ErrorMsg.location( gdd, tok ));
        msg.append( gdd.getLit( "ERROR.SYMBOL_FOR_BULLET_LIST_NOT_FOUND" ));
        gdd.logWarning( msg.toString() );
    }

}