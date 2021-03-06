/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.commands;

import org.pz.platypus.Token;
import org.pz.platypus.interfaces.IOutputCommand;
import org.pz.platypus.interfaces.IOutputContext;

/**
 * Abstract portion of command that marks the beginning of a plain bullet list (default indent and bullet char)
 *
 * @author alb
 */
public abstract class BulletListPlainEnd implements IOutputCommand
{
    private final String root = "[-list]";

    protected abstract int endBulletList( final IOutputContext context, Token tok, int tokNum );

    public int process( IOutputContext context, Token tok, int tokNum )
    {
        if( context == null || tok == null || tokNum < 0 ) {
            throw new IllegalArgumentException();
        }

        return( endBulletList( context, tok, tokNum ));
    }

    public String getRoot()
    {
        return( root );
    }
}