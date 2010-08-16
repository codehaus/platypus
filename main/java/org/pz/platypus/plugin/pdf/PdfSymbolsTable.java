/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.pdf;

import org.pz.platypus.PropertyFile;
import org.pz.platypus.GDD;

import java.io.File;
import java.util.Set;

/**
 * Holds all the PDF symbols and their associated actions as listed in the Symbols.list
 * file for the PDF plugin. Used for loading the command table.
 *
 * @author alb
 */
public class PdfSymbolsTable
{
    PropertyFile propFile = null;

    public PdfSymbolsTable( final GDD Gdd )
    {
        assert( Gdd != null );

        load( Gdd );
    }

    /**
     * Compute the filename of the Symbols property file, based on the GDD
     * @param gdd the GDD
     * @return the file name or an empty string if an error occurred.
     */
    String computePropFilename( final GDD gdd )
    {
        if( gdd == null || gdd.getConfigFile() == null ) {
            return( "" );
        }
                
        String filename = gdd.getConfigFile().lookup(
                    "pi.out." + gdd.getOutputPluginPrefix() + ".symbollist" );
        if( filename != null ) {
            filename = gdd.getHomeDirectory() + "config" + gdd.getFileSeparator() + filename;
        }
        else {
            filename = "";
        }

        if ( ! new File( filename ).exists() ) {
            gdd.logSevere(  gdd.getLit( "ERROR.INVALID_PLUGIN_URL" ) + ": " +
                            ( filename.isEmpty() ? "null" : filename ));
            return "";
        }
        return( filename );
    }

    /**
     * Compute the property file name for the symbols and load the symbols
     * into the PropertyFile class
     *
     * @param gdd the GDD
     */
    public void load( final GDD gdd )
    {
        final String  propFilename = computePropFilename( gdd );
        if( ! propFilename.isEmpty() ) {
            propFile = new PropertyFile( propFilename, gdd );
            propFile.load();
        }
    }

    public Set<String> keySet()
    {
        return( propFile.keySet() );        
    }

    public PropertyFile getPropertyFile()
    {
        return( propFile );
    }
}
