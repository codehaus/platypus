/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import com.lowagie.text.Paragraph;
import org.pz.platypus.*;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;
import org.pz.platypus.plugin.pdf.PdfData;

import java.util.Scanner;

/**
 * Implementation of [fcolor: (font color) for PDF plugin
 *
 * @author alb
 */
public class PdfFcolor implements IOutputCommand
{
    private String root = "[fcolor:";

    public int process( final IOutputContext context, final Token tok, final int tokNum )
    {
        GDD gdd;
        RgbColor newColor;

        /** the way the new color is specified in the original command. Should be 999,999,999. */
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

        return 0;
    }

    /**
     * parse the color spec, which should be in the form 999,999,999
     */
    int[] parseColorSpec( final String colorSpec, final Token tok, GDD gdd )
    {
        assert( colorSpec != null );

        Scanner scan = new Scanner( colorSpec ).useDelimiter( "," );
        int r = scan.nextInt();
        int g = scan.nextInt();
        int b = scan.nextInt();

        if(( r < 0 || r > 255 ) || ( g < 0 || g > 255 ) || ( b < 0 || b > 255 )) {
            errMsg( "ERROR.INVALID_COLOR", tok, gdd );
            return( new int[0] );
        }

        int[] colors = { r, g, b };
        return( colors );
    }

    /**
     * Prints an error message via the GDD error logging facility.
     * @param msg string of the message
     * @param t   token where the error occurred
     * @param gdd contains the logger
     */
    private void errMsg( final String msg, final Token t, final GDD gdd )
    {
        assert( msg != null );
        assert( t != null );
        assert( gdd != null );

        gdd.logWarning( gdd.getLit( "FILE" ) + t.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE" ) + t.getSource().getLineNumber() + " " +
                        msg + " " +
                        gdd.getLit( "IGNORED" ));
    }
    /**
     * Updates the leading by running the command PdfLeading with the new value
     * @param context context of current token, needed by PdfLeading
     * @param tok the current token for font size, from which to extract data for the leading command
     * @param tokNum the number of the font size token
     * @param newFontSize the font size we're moving to.
     */
    private void updateLeading( final IOutputContext context, final Token tok, final int tokNum,
                                final float newFontSize )
    {
        CommandParameter newLeading = new CommandParameter();
        newLeading.setAmount( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO );
        newLeading.setUnit( UnitType.POINT );

        Token newLeadingToken = new Token(
                tok.getSource(),
                TokenType.COMMAND,
                "[leading:",
                "[leading:",
                newLeading );

        new PdfLeading().process( context, newLeadingToken, tokNum  );
    }

    /**
     * Sees if any text has been written to the current paragraph.
     *
     * @param iTPara the current paragraph
     * @return true if no text has been written, false otherwise.
     */
    boolean inEmptyParagraph( final Paragraph iTPara )
    {
        if( iTPara == null || iTPara.size() == 0 ) {
            return( true );
        }
        return( false );
    }

    public String getRoot()
    {
        return( root );
    }
}
