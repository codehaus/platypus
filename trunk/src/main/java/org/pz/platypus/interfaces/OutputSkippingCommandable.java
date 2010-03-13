/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.interfaces;

import org.pz.platypus.*;

/**
 * Interface for commands that consume multiple tokens in the token stream.
 */
public interface OutputSkippingCommandable
{
    /**
     * get the root of the command. For example, fsize: in [fsize:12pt]
     * @return the root as a string
     */
    String getRoot();

    /**
     * method for processing this command in the output plugin.
     *
     * @param context the context/state of the output document
     * @param tok the command token as found in the token stream
     * @param tokNum the number of the token in the token list
     * @return the number of tokens to skip
     */

    int processSkippingCommand( final OutputContextable context, final Token tok, final int tokNum );
}