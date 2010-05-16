/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html;

import org.pz.platypus.GDD;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.plugin.html.commands.*;

import java.util.HashMap;

/**
 * Contains all the commands used in the PDF plugins,
 * keyed by command root. Class is based substantially
 * on org.pz.Platypus.CommandTable
 *
 * @author alb
 */
@SuppressWarnings("unchecked")
public class HtmlCommandTable
{
    /** the hashtable into which the commands are loaded */
    private HashMap<String, OutputCommandable> commandTable;

    public HtmlCommandTable()
    {
        commandTable = new HashMap<String, OutputCommandable>( 300 );
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
    void loadCommands()
    {
        add( new HtmlPageWidth() );
        add( new HtmlEol() );
        add( new HtmlMarginLeft() );
        add( new HtmlItalicsOn() );
        add( new HtmlItalicsOff() );
        add( new HtmlBoldOn() );
        add( new HtmlBoldOff() );
        add( new HtmlUnderlineOn() );
        add( new HtmlUnderlineOff() );
        add( new HtmlUrl() );
        add( new HtmlStrikethruOn() );
        add( new HtmlStrikethruOff() );
        add( new HtmlEoParagraph() );
        add( new HtmlParagraphIndent() );
        add( new HtmlParagraphIndentRight() );
        add( new HtmlHardCR() );
        add( new HtmlFsize() );
        add( new HtmlMarginRight() );
        add( new HtmlMarginTop() );
    }

    /**
     * load the symbols and special characters. These are loaded from a text file.
     *
     * @param gdd the global document data
     */
    void loadSymbols( final GDD gdd )
    {
    }

    /**
     * add a OutputCommandable item to the hash table, using its root as the key to the entry
     * @param entry to be added (either a command or a symbol)
     */
    void add( final OutputCommandable entry )
    {
        commandTable.put( entry.getRoot(), entry );
    }

    //=== getters and setters ===/

    /**
     * Lookup a command by its root
     * @param root command root (portion ending in the first | : or ] character
     * @return the OutputCommandable class found, or null on error
     */
    public OutputCommandable getCommand( final String root )
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