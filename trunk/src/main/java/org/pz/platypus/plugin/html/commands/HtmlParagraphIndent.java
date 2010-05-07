/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.exceptions.InvalidCommandParameterException;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.pdf.PdfData;
import org.pz.platypus.plugin.pdf.TextIndenter;

/**
 * Implementation of indentation of a full paragraph for HTML plugin
 *
 * @author ask
 */
public class HtmlParagraphIndent implements OutputCommandable
{
    private String root = "[paraindent:";

    public int process( final OutputContextable context, final Token tok, final int tokNum )
    {
        float newIndent;

        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        HtmlData htmlData = (HtmlData) context;

//        TextIndenter indenter = new TextIndenter( tok, htmlData );
//
//        try {
//            newIndent = indenter.calculateIndent();
//        }
//        catch( InvalidCommandParameterException icpe ) {
//            showErrorMsg( tok, htmlData );
//            return 0;
//        }
//
//        if ( newIndent != htmlData.getParagraphIndent() ) {
//            htmlData.setParagraphIndent( newIndent, tok.getSource() );
//        }
        return 0;
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param pdf contains the location of the logger and literals file
     */
    void showErrorMsg( final Token tok, final PdfData pdf )
    {
        GDD gdd = pdf.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.INVALID_PARAGRAPH_INDENT" ) + " " +
                        gdd.getLit( "IGNORED" ));

    }

    public String getRoot()
    {
        return( root );
    }
}