/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.*;
import org.pz.platypus.interfaces.*;
import org.pz.platypus.plugin.pdf.*;

import com.lowagie.text.Paragraph;

/**
 * Implementation of fsize (in font family of commands) for PDF plugin
 *
 * @author alb
 */
public class PdfFsize implements OutputCommandable
{
    private String root = "[font|size:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;

        float currFontSize = pdf.getFontSize();
        float newFontSize = tok.getParameter().getAmount();

        if( currFontSize == newFontSize ) {
            return;
        }

        pdf.setFontSize( newFontSize, tok.getSource() );

        // need to change the leading when the font size changes.
        // The rules are complex:
        // 1) If this is for a new paragraph, then we make the change immediately (1.2 x font size)
        // 2) If we're in an existing paragraph:
        //    a) if the new font is smaller, we don't change the leading of the current par.
        //       However, we do set a new leading in PdfData, so that the next paragraph will
        //       reflect the new leading.
        //    b) if the new font is bigger, we change the leading only if the new font size is
        //       > 3pts || > 20% larger than the current font. This prevents leading changes for
        //       resulting from changing a single word from 10-pt to 12pt, for example.

        Paragraph iTPara = pdf.getOutfile().getItPara();

        if( inEmptyParagraph( iTPara )) {
            updateLeading( context, tok, tokNum, newFontSize );

            if( iTPara != null ) {
                pdf.getOutfile().getItPara().setLeading(
                                      newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO );
            }
            return;
        }

        // in an existing paragraph

        if( newFontSize < currFontSize ) {  // change leading for next paragraph
            pdf.setLeading( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO,
                            tok.getSource() );
            return;
        }

        // in an existing paragraph and new font size is more than a little bigger
        if( newFontSize - currFontSize > 3.0f || newFontSize > currFontSize * 1.2f  ) {
            updateLeading( context, tok, tokNum, newFontSize );

            if( newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO >
                    pdf.getOutfile().getItPara().getLeading() ) {
                pdf.getOutfile().getItPara().setLeading(
                    newFontSize * DefaultValues.LEADING_TO_FONT_SIZE_RATIO );
            }
        }

        // if in an existing paragraph with a minor font size change, we don't change leading
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
