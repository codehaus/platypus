/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.exceptions.InvalidConfigFileException;

import java.util.ArrayList;
import java.io.*;
import java.util.Collections;
import java.util.List;

/**
 * Contains a list of symbols that are used in Platypus. These symbols are loaded from
 * a configuration file in $PLATYPUS_HOME/config into this SymbolList. In turn, the list
 * is loaded into the CommandTable, where the Platypus parser will use the symbols.
 *
 * @author alb
 */
public class SymbolsList
{
    private GDD gdd;
    private ArrayList<String> symbolsList;

    public SymbolsList( GDD Gdd )
    {
        assert( Gdd != null );
        assert( Gdd.getSymbolsListFilename() != null );

        gdd = Gdd;
        symbolsList = new ArrayList<String>();
    }

    public void load()
    {
        BufferedReader inReader = null;
        String filename = gdd.getHomeDirectory() + "config" + gdd.getFileSeparator() +
                          gdd.getSymbolsListFilename();

        File symbolFile = new File( filename );
        if( ! symbolFile.exists() || symbolFile.isDirectory() ){
            throw new InvalidConfigFileException(
                    gdd.getLit( "ERROR.MISSING_CONFIG_FILE" ) + " ", filename );
        }

        try {
            inReader = new BufferedReader( new FileReader( filename ));
            String line;

            while (( line = inReader.readLine()) != null ) {
                loadLine( line.trim() );
            }
        }
        catch ( FileNotFoundException e ) {
            throw new InvalidConfigFileException(
                    gdd.getLit( "ERROR.PROCESSING_FILE" ) + " ", filename );
        }
        catch( IOException ioe ) {
               throw new InvalidConfigFileException(
                    gdd.getLit( "ERROR.PROCESSING_FILE" ) + " ", filename );
        }
        finally {
            if( inReader != null ) {
                try {
                    inReader.close();
                }
                catch( IOException ioe ) {
                    // do nothing
                }
                gdd.log( "Symbols file loaded with " + symbolsList.size() +
                         " entries: " + filename );
            }
        }
    }

    /**
     * Loads an individual symbol from the file into the list. Symbols whose names do not begin
     * with a [ are ignored.
     *
     * @param symbolName the symbol as found in the configuration file
     */
    void loadLine( final String symbolName )
    {
        if( symbolName.charAt( 0 ) == '[' ) {
            symbolsList.add( symbolName );
        }
    }

    List<String> getList()
    {
        return( Collections.unmodifiableList(symbolsList) );
    }

}
