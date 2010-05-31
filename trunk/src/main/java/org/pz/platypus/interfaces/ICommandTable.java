/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.interfaces;

import org.pz.platypus.GDD;

/**
 * Interface for plug-in command table
 *
 * @author alb
 */
public interface ICommandTable
{
    /** load the commands for in this table */
    public void loadCommands();

    /** load the symbols/special chars in this table
     *  @param gdd Global document data
     */
    public void loadSymbols( final GDD gdd );

    /** load an individual item
     *
     *  @param command to be added
     */
    public void add( OutputCommandable command );

    /** get the processor for a command or symbol
     *
     *  @param root, the unique key for identifying/looking up a command
     *  @return the command, or null on error
     */
    public OutputCommandable getCommand( final String root );

    /** get size of table
     *
     *  @return the number of entries in the table
     */
    public int getSize();
}
