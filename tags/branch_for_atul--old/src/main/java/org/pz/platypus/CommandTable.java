/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.command.*;
import org.pz.platypus.interfaces.Commandable;
import org.pz.platypus.plugin.pdf.commands.DefUserString;

import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

/**
 * Table of all supported commands in Platypus. It's implemented using a key consisting
 * of the command root (a String that is long enough to uniquely identify the command).
 * All commands in the Platypus file are looked up in here; Command object that is
 * returned contains all the info for parsing the command.
 *
 * @author alb
 */
@SuppressWarnings("unchecked")
public class CommandTable
{
    /** the hashtable into which the commands are loaded */
    protected HashMap<String, Commandable> commandTable;

    private GDD gdd;

    /**
     * This constructor is included here only because it facilitates writing
     * mock Literals class for testing. It's not called from Platypus working code.
     */
    public CommandTable()
    {
        super();
    }

    public CommandTable( final GDD Gdd )
    {
        if( Gdd == null ) {
            throw new IllegalArgumentException( "GDD param null in CommandTable()" );          
        }

        assert( Gdd.getLogger() != null );

        gdd = Gdd;
        commandTable = new HashMap<String, Commandable>( 300 );
    }

    /**
     * Load the commands and symbols into the command table.
     * Commands are loaded in alpha order
     */
    public void load()
    {
        loadCommands( gdd.getCommandPropertyFilename() );
        loadSymbols();
        gdd.log( "Command table in Platypus loaded with " + commandTable.size() + " commands" );
    }

    /**
     * load commands in to the command table
     *
     * @param commandFilename the file from which to read the commands
     */
    void loadCommands( final String commandFilename )
    {
        PropertyFile propf = loadCommandFile( commandFilename );
        if( propf.getSize() > 0 ) {
            loadCommandsFromFile( propf );
        }

        loadRemainingCommands();
    }

    /**
     * Family commands are not generally load-able from the property file;
     * so they have to be loaded manually, as done here.
     */
    void loadRemainingCommands()
    {
        add( new FontFamily() );
///>>>        add( new DefUserString() );
    }

    /**
     * Opens and reads the property file containing the commands
     *
     * @param commandFilename the name of the file to open and read
     * @return the PropertyFile that was open and read
     */
    PropertyFile loadCommandFile( final String commandFilename )
    {
        PropertyFile commandFile = readCommandFile( commandFilename  );
        return( commandFile );
    }

    /**
     * Read a property file listing commands to load into the Platy parser command table
     * @param filename file with the commands
     * @return the property file
     */
    PropertyFile readCommandFile( final String filename )
    {
        if( filename == null || filename.isEmpty() ) {
            return( null ); //todo add error handling/warning
        }

        String fullFilename = gdd.getHomeDirectory() + "config\\" + filename;
        PropertyFile pf = new PropertyFile( fullFilename, gdd );
        pf.load();
        return( pf );
    }

    /**
     * Loads the commands from the property file into the command table
     * @param pf the property file to load from
     *
     * @return number of commands loaded
     */
    int loadCommandsFromFile( final PropertyFile pf )
    {
        assert( pf != null );

        int i = 0;

        Set commands =  pf.getContents().keySet();
        for( Object root : commands )
        {
            loadCommand( (String) root, (String) pf.getContents().get( root ));
            i++;
        }
        return( i );
    }

    /**
     * adds an individual command from the property file into the Platypus parser command table
     * @param root the key for the command
     * @param attrib the command attributes, that specify the command type and other attributes
     */
    public void loadCommand( final String root, final String attrib )
    {
        char[] attribs = attrib.toCharArray();
        switch( attribs[0] )
        {
            case '0':   // commands with 0 parameters
                add( new Command0( root, attribs[1] ));
                break;

            case 's':   // commands with 1 parameter consisting of a string
                add( new CommandS( root, attribs[1] ));
                break;

            case 'v':   // commands with 1 parameter consisting of a value
                add( new CommandV( root, attribs[1] ));
                break;

            case 'r':   // replacement command (it's an alias for an individual family command)
                add( new CommandR( root, attrib, this ));
                break;

            default:
                gdd.logWarning( gdd.getLit( "ERROR.INVALID_COMMAND_FILE_ENTRY" ) +
                                root + " " + gdd.getLit( "IGNORED" ));
        }
    }

    /**
     * load symbols and special characters. These are loaded from a text file.
     */
    void loadSymbols()
    {
        SymbolsList sl = new SymbolsList( gdd );
        sl.load();
        ArrayList<String> symbols = sl.getList();
        for( String symbol : symbols ) {
            add( new Symbol( symbol ));
        }
    }

    /**
     * add a Commandable item to the hash table, using its root as the key to the entry
     * @param entry to be added (either a command or a symbol)
     */
    public void add( Commandable entry )
    {
        commandTable.put( entry.getRoot(), entry );
    }
    //=== getters and setters ===/

    /**
     * Lookup a command by its root
     * @param root command root (portion ending in the first | : or ] character
     * @return the Commandable class found, or null on error
     */
    public Commandable getCommand( final String root )
    {
        return( (Commandable) commandTable.get( root ));
    }
}
