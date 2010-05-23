/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.DefaultValues;
import org.pz.platypus.commands.BulletListPlainStart;
import org.pz.platypus.commands.BulletListPlainStartWithOptions;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.PdfOutfile;
import org.pz.platypus.utilities.ErrorMsg;
import org.pz.platypus.utilities.TextTransforms;
import com.lowagie.text.Chunk;

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
    protected int startNewList( final OutputContextable context, Token tok, int tokNum )
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
            bulletSymbol = lookupBulletSymbol( param );
        }
        else {
            bulletSymbol = extractBulletSymbolFromParam( param );
        }

        outFile.startPlainBulletList( bulletSymbol );

        return( 0 );
    }

    Chunk lookupBulletSymbol( final String param )
    {

        return new Chunk( ">" ); //TODO
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

    private void errorInvalidOption( final GDD gdd, final Token tok )
    {
        StringBuilder msg = new StringBuilder( ErrorMsg.location( gdd, tok ));
        msg.append( gdd.getLit( "ERROR.INVALID_OPTION_FOR_BULLET_LIST" ));
        gdd.logWarning( msg.toString() );
    }

}