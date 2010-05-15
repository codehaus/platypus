/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import com.lowagie.text.Paragraph;
import org.pz.platypus.*;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.pdf.commands.PdfLeading;

/**
 * Implementation of fsize (in font family of commands) for PDF plugin
 *
 * @author alb
 */
public class HtmlFsize implements OutputCommandable
{
    private String root = "[font|size:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

        float currFontSize = htmlData.getFontSize();
        float newFontSize = tok.getParameter().getAmount();

        if( currFontSize == newFontSize ) {
            return 0;
        }

        htmlData.setFontSize( newFontSize, tok.getSource() );

        htmlData.getOutfile().emitFontSizeTag();
        
        // if in an existing paragraph with a minor font size change, we don't change leading
        return 0;
    }

    /**
     * Updates the leading by running the command PdfLeading with the new value
     * @param context context of current token, needed by PdfLeading
     * @param tok the current token for font size, from which to extract data for the leading command
     * @param tokNum the number of the font size token
     * @param newFontSize the font size we're moving to.
     */
    private void updateLeading( final OutputContextable context, final Token tok, final int tokNum,
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