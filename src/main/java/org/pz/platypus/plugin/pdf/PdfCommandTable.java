/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.GDD;
import org.pz.platypus.interfaces.ICommandTable;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.plugin.pdf.commands.*;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * Contains all the commands used in the PDF plugins,
 * keyed by command root. Class is based substantially
 * on org.pz.Platypus.CommandTable
 *
 * @author alb
 */
@SuppressWarnings("unchecked")
public class PdfCommandTable  implements ICommandTable
{
    /** the hashtable into which the commands are loaded */
    private HashMap<String, IOutputCommand> commandTable;

    public PdfCommandTable()
    {
        commandTable = new HashMap<String, IOutputCommand>( 300 );
    }

    /**
     * Load the commands and symbols into the command table.
     * Commands are loaded in alpha order
     *
     * @param gdd GDD
     */
    public void load( GDD gdd )
    {
        loadCommands();
        loadSymbols( gdd );
    }

    /**
     * load commands in to the command table
     */
    public void loadCommands()
    {
        add( new DefUserString() );
        add( new PdfAlignment() );
        add( new PdfBoldOff() );
        add( new PdfBoldOn() );
        add( new PdfBulletListPlainEnd() );
        add( new PdfBulletListPlainStart() );
        add( new PdfBulletListPlainStartWithOptions() );
        add( new PdfCodeOff() );
        add( new PdfCodeOn() );
        add( new PdfCodeWithOptions() );
        add( new PdfColumnCount() );
        add( new PdfColumnWidth() );
        add( new PdfDump() );
        add( new PdfEol() );
        add( new PdfEolTreatment() );
        add( new PdfEoParagraph() );
        add( new PdfFcolor() );
        add( new PdfFface() );
        add( new PdfFirstLineIndent() );
        add( new PdfFooterOn() );
        add( new PdfFooterOff() );
        add( new PdfFsize() );
        add( new PdfItalicsOff() );
        add( new PdfItalicsOn() );
        add( new PdfHardCR() );
        add( new PdfLeading() );
        add( new PdfMarginBottom() );
        add( new PdfMarginLeft() );
        add( new PdfMarginRight() );
        add( new PdfMarginTop() );
        add( new PdfMarginsMirrored() );
        add( new PdfNewPage() );
        add( new PdfNoIndent() );
        add( new PdfPageHeight() );
        add( new PdfPageSize() );
        add( new PdfPageWidth() );
        add( new PdfParagraphIndent() );
        add( new PdfParagraphIndentRight() );
        add( new PdfParagraphSkip() );
        add( new PdfPrintVariable() );
        add( new PdfRestoreFormat() );
        add( new PdfSaveFormat() );
//        add( new PdfScript() );
        add( new PdfStrikethruOff() );
        add( new PdfStrikethruOn() );
        add( new PdfUnderlineOff() );
        add( new PdfUnderlineOn() );
        add( new PdfUrl() );
        add( new PdfUrlWithCoverText() );
        add( new PdfUrlWithCoverTextEnd() );
    }

    /**
     * load the symbols and special characters. These are loaded from a text file.
     *
     * @param gdd the global document data
     */
    public void loadSymbols( final GDD gdd )
    {
        try {
            PdfSymbolsTable pst = new PdfSymbolsTable( gdd );
            Set<String> symbols = pst.keySet();
            for( String symbol : symbols ) {
                add( new PdfSymbol( symbol, pst.getPropertyFile().lookup( symbol )));
            }
        }
        catch( MissingResourceException mre ) {
            // Do nothing. Error message has already been displayed.
            // by exiting method now, any symbols will generate a warning
            // that the symbol is not recognized
        }
    }

    /**
     * add a OutputCommandable item to the hash table, using its root as the key to the entry
     * @param entry to be added (either a command or a symbol)
     */
    public void add( final IOutputCommand entry )
    {
        commandTable.put( entry.getRoot(), entry );
    }

    //=== getters and setters ===/

    /**
     * Lookup a command by its root
     * @param root command root (portion ending in the first | : or ] character
     * @return the OutputCommandable class found, or null on error
     */
    public IOutputCommand getCommand( final String root )
    {
        return( commandTable.get( root ));
    }

    /**
     * How many entries in the table
     * @return number of entries
     */
    public int getSize()
    {
        return( commandTable.size() );
    }
}
