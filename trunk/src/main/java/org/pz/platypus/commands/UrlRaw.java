/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;

/**
 * Abstract portion of bare URL command
 *
 * @author ask
 */
public abstract class UrlRaw implements IOutputCommand
{
    private final String root = "[url:";

    protected abstract void outputUrl( final IOutputContext context, String url );

    public int process( IOutputContext context, Token tok, int tokNum )
    {
        if( context == null || tok == null || tok.getParameter() == null ) {
            throw new IllegalArgumentException();
        }

        String urlParameter = tok.getParameter().getString();
        if( urlParameter == null ) {
            showErrorMsg( context, tok );
        }
        else {
            outputUrl( context, urlParameter );
        }
        
        return 0;
    }

    public String getRoot()
    {
        return( root );
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param context contains the location of the logger and literals file
     */    
    private void showErrorMsg( IOutputContext context, Token tok ) {
        GDD gdd = context.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_IS_NULL" ) + " " +
                        gdd.getLit( "IGNORED" ));

    }
    
}
