/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
package org.pz.platypus;

import org.pz.platypus.commandline.SpecifiedOptions;
import org.pz.platypus.commandline.SupportedOptions;
import org.pz.platypus.exceptions.HelpMessagePrinted;

/**
 * Puts specified command-line options into a tree and processes those it can.
 *
 * @author alb
 */
public class CommandLineArgs
{
    @SuppressWarnings("unchecked")

    /** a tree containing all supported/allowed command-line options and their argument counts */
    private final SupportedOptions validOptions;

    /** a tree containing the command-line switches and their settings as specified by the user */
    private final SpecifiedOptions clArgs;

    /**
     * Creates a tree of SpecifiedOptions. The tree always contains input and output filenames.
     * If these were not specified, their corresponding argument is null.
     *
     * Constraint: assumes that all options have either 0 or 1 valid arguments.
     *
     * @param args command-line args passed to main()
     * //curr: limit constructor to just setting up data structures
     *
     */
    public CommandLineArgs( final String[] args )
    {
        validOptions = new SupportedOptions();

        if ( args == null || args.length == 0 ) {
            clArgs = null;
            return;
        }

        clArgs = new SpecifiedOptions();

        int howManyFileArgs = loadFileNames( args, clArgs );
        loadSpecifiedOptions( args, howManyFileArgs, clArgs );
    }

    /**
     * Creates a single string from all the args specified by the user on the command line
     *
     * @param args the user-specified args
     * @return the command line as one string. If there were no args, a single space is returned.
     */
    public String createCommandLine( final String[] args )
    {
        StringBuffer sb = new StringBuffer( args.length * 15 );

        if ( args.length == 0 ) {
            return( " " );
        }

        for( String arg : args ) {
            sb.append( arg );
            sb.append( ' ' );
        }

        return( sb.toString().trim() );
    }

    /**
     * Load the input and output filenames, if any were specified
     *
     * @param args the command-line args
     * @param options
     * @return how many command-line args were processed. Can only be 0, 1, 2.
     */
    private int loadFileNames( final String[] args, SpecifiedOptions options )
    {
        int i = 0;

        if ( ! args[0].startsWith( "-" ) ) {
            options.add( "inputFile", args[0] );
            i++;

            if ( args.length == 1 ) {
                return( i );
            }

            if ( args.length > 1 && ! args[1].startsWith( "-" )) {
                options.add( "outputFile", args[1] );
                i++;
            }
        }
        return( i );
    }

    /**
     * Load the options specified on the command line
     * @param args array of individual args from the command-line
     * @param startingArg which arg to start with (should be 0-2 depending on whether files were specified)
     * @param options a list of the specified options and their arguments, if any.
     */
    private void loadSpecifiedOptions( final String[] args, int startingArg,
                                       SpecifiedOptions options )
    {
//        System.out.println( "args count " + args.length + '\n' +
//                "startingArg: " + startingArg );

        for ( int currentArg = startingArg; currentArg < args.length; currentArg++ )
        {
//            System.out.println( "current Arg#: " + currentArg );

            if ( validOptions.containsArg( args[currentArg] )) {
                int argsForOption = validOptions.getArgCount( args[currentArg] );

                if ( argsForOption == 0 ) {
                    options.add( args[currentArg], "true" );
                    continue;
                }

                if ( ! areMoreArguments( args, currentArg )) {
                    System.out.println( "Error! Missing argument for " + args[currentArg] +
                            ". Ignored." );
                    continue;
                }

                options.add( args[currentArg], args[currentArg + 1] );
                currentArg++;
            }
            else {
                System.out.println( "Error: Invalid option specified: " +
                                    args[currentArg] +
                                    "Ignored." );
            }
            //curr: else arg not recognized processing.
        }
    }

    /**
     * Tests to see whether there are more options to process
     *
     * @param args
     * @param currentArg
     * @return yea/nay
     */
    private boolean areMoreArguments( final String[] args, final int currentArg ) {
        return( args.length > currentArg );
    }

    /**
     * Determines whether an item was specified on the command-line. And if so,
     * what any argument for it is.
     *
     * @param argToFind argument to lookup
     * @return null if the item was not specified; "" if the item was specified by had no argument,
     *  and the argument if item was specified and has an argument.
     */
    public String lookup( final String argToFind )
    {
        String argValue = null;

        if ( argToFind == null ) {
            return( null );
        }

        if ( clArgs.containsArg( argToFind )) {
            argValue = "";

            final String value = clArgs.getArg( argToFind );
            if ( value != null ) {
                argValue = value;
            }
        }
        return( argValue );
    }

    /**
     * Processes some command-line options
     *
     * @param gdd GDD that will hold the state of some switches (such as verbosity)
     * @throws HelpMessagePrinted if no command-line args specified
     */   //curr: improve method name
    public void process( GDD gdd ) throws HelpMessagePrinted
    {
        if( clArgs == null ) {
            Platypus.showUsage( gdd );
            System.err.println( gdd.getLit( "PLEASE_RERUN_WITH_FILENAMES" ));
            throw new UnsupportedOperationException( "ERR" ); //curr: delete ERR
        }  //curr: combine with graf below, using same exit exception.

        // Note: the -config option is entirely handled in Platypus.processConfigFile(), not here.

        if ( clArgs.containsArg( "-help" )) {
            Platypus.showUsage( gdd );
            // this exception exits Platypus without further processing and w/out
            // an error message. Technically, this is not an exception, but it's
            // the cleanest way of closing down from here.
            throw new HelpMessagePrinted( "OK" );
        }

        if ( clArgs.containsArg( "-verbose" )) {
            gdd.setClVerbose( true );
        }

        if ( clArgs.containsArg( "-vverbose" )) {
            gdd.setClVVerbose( true );
        }
            //FIXTHIS: Cannot be calling the PDF plugin from here. Move this to non PDF function
            //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if ( clArgs.containsArg( "-fontlist" )) {
            TypefaceMap tfm = new TypefaceMap( gdd );
            tfm.loadFamilies();
            tfm.writeMapToFile( gdd.getHomeDirectory() + "config/fontlist.txt" );
            throw new HelpMessagePrinted( "OK" ); //todo: fix this way of escaping processing.
        }
    }

    // === getters and setters ===//

    public SpecifiedOptions getClArgs()
    {
        return( clArgs );
    }
}
