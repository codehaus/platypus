/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.utilities.TextTransforms;

import java.util.Scanner;

/**
 * Implementation of [fcolor: (font color) for PDF plugin
 *
 * @author alb
 */
public class PdfFcolor extends CommandExecutor implements IOutputCommand
{
    public PdfFcolor() { root = "[fcolor:"; };

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        GDD gdd;
        RgbColor newColor;

        /** the way the color is specified in the original command. Should be 999,999,999. */
        String newColorSpec;

        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        gdd = context.getGdd();

        newColorSpec = tok.getParameter().getString();

        if( newColorSpec == null || newColorSpec.isEmpty() ) {
            errMsg( gdd.getLit( "ERROR.INVALID_COLOR" ), tok, gdd );
            return( 0 );
        }

        int[] colors = parseColorSpec( newColorSpec, tok, gdd );
        if( colors.length != 3 ) {
            return( 0 ); // an error occurred and the message has already been logged.
        }

        newColor = new RgbColor( colors[0], colors[1], colors[2] );

        pdf.getFont().setColor( newColor, tok.getSource() );

        return( 0 );
    }

    /**
     * parse the color spec, which should be in the form 999,999,999
     *
     * @param colorSpec the color as specified in the command
     * @param tok the token for the command, used in error messages only
     * @param gdd the global document data container, used because it contains the logger.
     * @return an int array with 3 values for R, G, B. On error, an int array containing no values.
     */
    int[] parseColorSpec( final String colorSpec, final Token tok, GDD gdd )
    {
        assert( colorSpec != null );

        int r = -1;
        int g = -1;
        int b = -1;

        String rgbColor = colorSpec;

        // if it's a macro, then look it up. Colors are in the user-editable strings.
        if( colorSpec.startsWith( "$" )) {
            String macro = TextTransforms.lop( colorSpec, 1 );
            rgbColor = gdd.getUserStrings().getString( macro );
        }

        if( rgbColor == null ) {
            errMsg( "ERROR.UNDEFINED_COLOR", tok, gdd );
            return( new int[0] );
        }

        Scanner scan = new Scanner( rgbColor ).useDelimiter( "," );
        if( scan.hasNext() ) {
            r = scan.nextInt();
            if( scan.hasNext() ) {
                g = scan.nextInt();
                if( scan.hasNext() ) {
                    b = scan.nextInt();
                }
            }
        }

        if(( r < 0 || r > 255 ) || ( g < 0 || g > 255 ) || ( b < 0 || b > 255 )) {
            errMsg( "ERROR.INVALID_COLOR", tok, gdd );
            return( new int[0] );
        }

        int[] colors = { r, g, b };
        return( colors );
    }
//
//    /**
//     * Prints an error message via the GDD error logging facility.
//     * @param msg string of the message
//     * @param t   token where the error occurred
//     * @param gdd contains the logger
//     */
//    private void errMsg( final String msg, final Token t, final GDD gdd )
//    {
//        assert( msg != null );
//        assert( t != null );
//        assert( gdd != null );
//
//        gdd.logWarning( gdd.getLit( "FILE" ) + t.getSource().getFileNumber() + " " +
//                        gdd.getLit( "LINE" ) + t.getSource().getLineNumber() + " " +
//                        msg + " " +
//                        gdd.getLit( "IGNORED" ));
//    }
//
//    public String getRoot()
//    {
//        return( root );
//    }
}
