/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;

/**
 * Abstract portion of command that marks the beginning of a plain bullet list (default indent and bullet char)
 *
 * @author alb
 */
public abstract class BulletListStartPlain implements OutputCommandable
{
    private final String root = "[list]";

    protected abstract int startNewList( final OutputContextable context, Token tok, int tokNum );

    public int process( OutputContextable context, Token tok, int tokNum )
    {
        if( context == null || tok == null || tokNum < 0 ) {
            throw new IllegalArgumentException();
        }

        startNewList( context, tok, tokNum );

        return( 0 );
    }

    public String getRoot()
    {
        return( root );
    }
}