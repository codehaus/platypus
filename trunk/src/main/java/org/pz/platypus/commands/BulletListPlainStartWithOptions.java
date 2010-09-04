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
import org.pz.platypus.utilities.ErrorMsg;

/**
 * Abstract portion of command that marks the beginning of a plain bullet list (default indent and bullet char)
 *
 * @author alb
 */
public abstract class BulletListPlainStartWithOptions implements IOutputCommand
{
    private final String root = "[list|";

    protected abstract int startNewList( final IOutputContext context, Token tok, int tokNum );

    public int process( IOutputContext context, Token tok, int tokNum )
    {
        if( context == null || tok == null || tokNum < 0 ) {
            throw new IllegalArgumentException();
        }

        if( tok.getParameter() == null || tok.getParameter().getString() == null ) {
            errorNoParameterForStartList( context.getGdd(), tok );
            return( 0 );
        }

        startNewList( context, tok, tokNum );

        return( 0 );
    }

    /**
     * Issue error if no parameter was specified
     * @param gdd container of literals and logger
     * @param tok token with missing parameter
     */
    private void errorNoParameterForStartList( final GDD gdd, final Token tok )
    {
        StringBuilder sb = new StringBuilder( ErrorMsg.location( gdd, tok ));
        sb.append( gdd.getLit( "ERROR.BULLET_LIST_PARAMETERS_NOT_SPECIFIED" ));
        gdd.logWarning( sb.toString() );
    }

    public String getRoot()
    {
        return( root );
    }
}