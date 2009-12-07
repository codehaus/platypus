/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.interfaces;

import org.pz.platypus.*;

/**
 * Interface for individual commands as stored in the command look-up table.
 */
public interface Commandable
{
    /**
     * get the root of the command. For example, fsize: in [fsize:12pt]
     * @return the root as a string
     */
    public String getRoot();

    /**
     * get a substitute string for the root. This is null except in cases where
     * the command is shorthand for a longer command string.
     *
     * @return the actual command string for which the current command is shorthand
     */
    public String getRootSubstitute();

    /**
     * what kind of argument, if any, does the command take?
     * @return ParamType for the command
     */
    public ParamType getParamType();

    /**
     * Is this command processed or ignored in a code listing?
     * @return boolean indicating whether command is/is not processed in a code listing
     */
    public boolean isAllowedInCode();

    /**
     * method for processing this command. Note this processing is not the processing
     * that occurs in the output (*that* is done by the output plugin). This is the
     * processing needed (if any) to determine the token(s) to generate for this command.
     *
     * @param gdd the GDD
     * @param context the parsing context
     * @param tokens TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
     * @return number of chars to skip to get past the command string in the input. If negative,
     * means an error occurred.
     *
     */

    int process( final GDD gdd, final ParseContext context, final TokenList tokens,
                 final boolean inCode );
}
