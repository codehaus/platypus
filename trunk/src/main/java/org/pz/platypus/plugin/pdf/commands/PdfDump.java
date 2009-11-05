/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf.commands;

import org.pz.platypus.Token;
import org.pz.platypus.GDD;
import org.pz.platypus.UserStrings;
import org.pz.platypus.command.Alignment;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.pdf.PdfData;

/**
 * Dumps various values to the console (for debugging purposes)
 *
 * @author alb
 */
public class PdfDump implements OutputCommandable
{
    private String root = "[dump:";

    public void process( final OutputContextable context, final Token tok, final int tokNum )
    {
        if( context == null || tok == null ) {
            throw new IllegalArgumentException();
        }

        PdfData pdf = (PdfData) context;
        GDD gdd = pdf.getGdd();

        String item = tok.getParameter().getString();
        if( item == null ) {
            gdd.logWarning(
                    gdd.getLit( "FILE#" ) + " " + tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + " " + tok.getSource().getLineNumber() + " " +
                    gdd.getLit( "ERROR.ITEM_TO_DUMP_IS_NULL" ) + " " +
                    gdd.getLit( "IGNORED" ));
            return;
        }
        item = item.toUpperCase();

        if( item.equals( "ALL" )) {
            dumpAll( gdd, pdf, tok, true );
        }
        else if( item.equals( "COMMANDLINE" )) {
            dumpCommandLine( gdd, tok, true );
        }
        else if( item.equals( "PAGE" )) {
            dumpPage( gdd, pdf, tok, true );
        }
        else if( item.equals( "PARAGRAPH" )) {
            dumpParagraph( gdd, pdf, tok, true );
        }
        else if( item.equals( "USERSTRINGS" )) {
            dumpUserStrings( gdd, tok, true );
        }
        else {
            gdd.logWarning(
                    gdd.getLit( "FILE#" ) + " " + tok.getSource().getFileNumber() + " " +
                    gdd.getLit( "LINE#" ) + " " + tok.getSource().getLineNumber() + " " +
                    gdd.getLit( "ERROR.ITEM_TO_DUMP_IS_INVALID" ) + ": " + item + " " +
                    gdd.getLit( "IGNORED" ));
            return;
        }
    }

    /**
     * Dump all available info. (which is a lot, so use sparingly!)
     *
     * @param gdd the GDD containing the needed literals
     * @param pdf the PDF data at this point in the execution
     * @param tok the token of the dump command
     * @param doHeading should the heading be printed? (that is, is this the first element to
     *        be dumped, if part of a series. True = print heading; false = don't print it.
     */
    void dumpAll( final GDD gdd, final PdfData pdf, final Token tok, final boolean doHeading )
    {
        if( doHeading ) {
            doHeading( gdd, tok );
        }

        dumpCommandLine( gdd, tok, false );
        dumpPage( gdd, pdf, tok, false );
        dumpParagraph( gdd, pdf, tok, false );
        dumpUserStrings( gdd, tok, false );
    }

    /**
     * Dump the command line and related data.
     *
     * @param gdd the GDD containing the needed literals
     * @param tok the token of the dump command
     * @param doHeading should the heading be printed? (that is, is this the first element to
     *        be dumped, if part of a series. True = print heading; false = don't print it.
     */
    void dumpCommandLine( final GDD gdd, final Token tok, final boolean doHeading )
    {
        if( doHeading ) {
            doHeading( gdd, tok );
        }
        System.out.println( "Platypus " + gdd.getLit( "VERSION_WORD" ) + ": " +
                            gdd.getSysStrings().getString( "_version" ));
        System.out.println( gdd.getLit( "COMMAND_LINE" ) + ": " +
                            gdd.getSysStrings().getString( "_commandLine" ));
        System.out.println( gdd.getLit( "OUTPUT_FORMAT" ) + ": " +
                            gdd.getSysStrings().getString( "_format" ));
    }

    /**
     * Dump the page dimensions and related data.
     *
     * @param gdd the GDD containing the needed literals
     * @param pdf the PDF data at this point in the execution
     * @param tok the token of the dump command
     * @param doHeading should the heading be printed? (that is, is this the first element to
     *        be dumped, if part of a series. True = print heading; false = don't print it.
     */
    void dumpPage( final GDD gdd, final PdfData pdf, final Token tok, final boolean doHeading )
    {
        if( doHeading ) {
            doHeading( gdd, tok );
        }

        System.out.println( gdd.getLit( "PAGE_WIDTH" ) + ": " + pdf.getPageWidth() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getPageWidthLine().getLineNumber() );
        System.out.println( gdd.getLit( "PAGE_HEIGHT" ) + ": " + pdf.getPageHeight() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getPageHeightLine().getLineNumber() );
        System.out.println( gdd.getLit( "MARGINS" ) + ":" );
        System.out.println( "\t" +
                            gdd.getLit( "TOP" ) + ": " + pdf.getMarginTop() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getMarginTopLine().getLineNumber() );
        System.out.println( "\t" +
                            gdd.getLit( "BOTTOM" ) + ": " + pdf.getMarginBottom() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getMarginBottomLine().getLineNumber() );
        System.out.println( "\t" +
                            gdd.getLit( "LEFT" ) + ": " + pdf.getMarginLeft() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getMarginLeftLine().getLineNumber() );
        System.out.println( "\t" +
                            gdd.getLit( "RIGHT" ) + ": " + pdf.getMarginRight() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getMarginRightLine().getLineNumber() );

        System.out.println( gdd.getLit( "COLUMNS" ) + ": " +
                            pdf.getColumnCount() + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getColumnCountLine().getLineNumber() );
    }

    /**
     * Dump the paragraph dimensions and related data.
     *
     * @param gdd the GDD containing the needed literals
     * @param pdf the PDF data at this point in the execution
     * @param tok the token of the dump command
     * @param doHeading should the heading be printed? (that is, is this the first element to
     *        be dumped, if part of a series. True = print heading; false = don't print it.
     */
    void dumpParagraph( final GDD gdd, final PdfData pdf, Token tok, final boolean doHeading )
    {
        if( doHeading ) {
            doHeading( gdd, tok );
        }

        System.out.println( gdd.getLit( "PARAGRAPH" ) + ":" );

        System.out.println( "\t" +
                            gdd.getLit( "LEFT" ) + " " + gdd.getLit( "INDENT" ) + ": " +
                            pdf.getParagraphIndent() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getParagraphIndentLine().getLineNumber() );

        System.out.println( "\t" +
                            gdd.getLit( "RIGHT" ) + " " + gdd.getLit( "INDENT" ) + ": " +
                            pdf.getParagraphIndentRight() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getParagraphIndentRightLine().getLineNumber() );

        System.out.println( "\t" +
                            gdd.getLit( "FIRST_LINE" ) + " " + gdd.getLit( "INDENT" ) + ": " +
                            pdf.getFirstLineIndent() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getFirstLineIndentLine().getLineNumber() );

        System.out.println( "\t" +
                            gdd.getLit( "NO_INDENT" ) + ": " +
                            ( pdf.getNoIndent() ? gdd.getLit( "YES" ) : gdd.getLit( "NO" )) + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getFirstLineIndentLine().getLineNumber() );

        System.out.println( "\t" +
                            gdd.getLit( "LEADING" ) + ": " +
                            pdf.getLeading() + " " +
                            gdd.getLit( "POINTS") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getLeadingLine().getLineNumber() );

        System.out.println( "\t" +
                            gdd.getLit( "SPACE_TO_SKIP_AT_EO_PARAGRAPH" ) + ": " +
                            pdf.getParagraphSkip() + " " +
                            gdd.getLit( "LINES") + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getParagraphSkipLine() );

        int currAlign = pdf.getAlignment();
        String alignment = new Alignment().toString( currAlign, gdd );
        System.out.println( "\t" +
                            gdd.getLit( "ALIGNMENT" ) + ": " + alignment + " " +
                            gdd.getLit( "LAST_CHANGED_AT" ) + " " +
                            pdf.getFirstLineIndentLine().getLineNumber() );
    }

    /**
     * Dump the user strings, one k,v pair per line. If no strings defined, it states this.
     *
     * @param gdd the GDD containing the needed literals
     * @param tok the token of the dump command
     * @param doHeading should the heading be printed? (that is, is this the first element to
     *        be dumped, if part of a series. True = print heading; false = don't print it.
     */
    void dumpUserStrings( final GDD gdd, final Token tok, final boolean doHeading )
    {
        if( doHeading ) {
            doHeading( gdd, tok );
        }

        UserStrings userStrings = gdd.getUserStrings();
        if( userStrings == null ) {
            gdd.logWarning( gdd.getLit( "USER_DEFINED_STRINGS" ) + ": " +
                            gdd.getLit( "NULL" ));
            return;
        }

        // this call does the print out of user strings, including "none" if none defined
        System.out.println( userStrings.dump( gdd.getLits() ));
    }


    /**
     * Print the heading before the dump that states the location in the file of
     * the [dump: command.
     * @param gdd GDD, for access to literals
     * @param tok token, for access to time and place in file where command encountered.
     */
    void doHeading( final GDD gdd, final Token tok )
    {
        if( gdd == null || tok == null ) {
            return;
        }

        System.out.println( gdd.getLit( "DUMP_DATA" ) + " " +
                            gdd.getLit( "FILE#" ) + " " + tok.getSource().getFileNumber() + " " +
                            gdd.getLit( "LINE#" ) + " " + tok.getSource().getLineNumber() );
    }

    public String getRoot()
    {
        return( root );
    }
}