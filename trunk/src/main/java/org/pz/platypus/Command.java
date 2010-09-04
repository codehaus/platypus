/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.interfaces.ICommand;
import org.pz.platypus.utilities.ErrorMsg;

/**
 * The basic abstract class for all commands
 *
 * @author alb
 */
public abstract class Command implements ICommand
{
    /** what kind of parameter does this command take, if any? */
    protected ParamType parameterType;

    /** the command root */
    protected String root;

    /** the substitute root, if this command is shorthand for another */
    protected String rootSubstitute = null;

    /** is this command executed in a listing or code section? */
    protected boolean validInCode;

    /**
     * get the root of the command. For example, [fsize: in [fsize:12pt]
     *
     * @return the root as a string
     */
    public String getRoot()
    {
        return( root );
    }

    /**
     * get a substitute string for the root. This is null except in cases where
     * the command is shorthand for a longer command string.
     *
     * @return the actual command string for which the current command is shorthand
     */
    public String getRootSubstitute()
    {
        return( rootSubstitute );
    }

    /**
     * what kind of argument, if any, does the command take?
     * @return ParamType for the command
     */
    public ParamType getParamType()
    {
        return( parameterType );
    }

    /**
     * Is this command processed or ignored in a code listing?
     * @return boolean indicating whether command is/is not processed in a code listing
     */
    public boolean isAllowedInCode()
    {
        return( validInCode );
    }

    /**
     * method for processing this command. Note this is processing is not the processing
     * that will occur in the output (*that* is done by the output plugin). This is the
     * processing needed (if any) to determine the tokens to generate for this command.
     *
     * @param input the input chars (fron the Platypus file)
     * @param parsePoint the point in the input where the command begins
     * @param tl TokenList to which the generated tokens are added
     * @param inCode Are we currently in a code section?
     * @param source the file# and line# of the current input
     * @param gdd the GDD
     * @return number of chars to skip to get past the command string in the input. If negative,
     * means an error occurred.
     *
     */
    public int process( final char[] input,
                        int parsePoint,
                        final TokenList tl,
                        final boolean inCode,
                        final Source source,
                        GDD gdd )
    {
        return( 0 );
    }

    /**
     * Handles the situation in which a command that is not supported in a listing or
     * code section is encountered while in one.
     *
     * @param tl TokenList being created by the parser
     * @param source the current file# and line#
     * @param gdd Global Document Data
     * @param command the command string as it appeared in the input text
     * @return  the number of characters to advance the parser
     */
    public int notExecutedInCodeSection( final String command,
                                         final TokenList tl, Source source, final GDD gdd )
    {
        ErrorMsg.notAllowedInCode( gdd, getRoot(), source );
        // skip the command.
        return( command.length() );
    }
}
