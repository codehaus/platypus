/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
package org.pz.platypus;

import org.pz.platypus.exceptions.HelpMessagePrinted;
import org.apache.commons.cli.*;

/**
 * Puts specified command-line options into a tree and processes those it can.
 *
 * @author alb
 */
public class CommandLineArgs
{

    @SuppressWarnings("unchecked")

    private Options options = new Options();    
    private CommandLineParser parser = new GnuParser();
    private CommandLine line;
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
    public CommandLineArgs( final String[] args ) {

        if ( args == null || args.length == 0 ) {
            return;
        }

        // Atul - moving to commons cli
        initOptions();

        parseArguments(args);
    }

    private void parseArguments(String[] args) {
        try {
            line = parser.parse( options, args );
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void initOptions() {
        Option[] optsArr = getSupportedOptions();

        for (Option opt : optsArr) {
            options.addOption(opt);
        }
    }

    private Option[] getSupportedOptions() {
        Option inputFile   = OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "input file" )
                .create( "inputFile" );
        Option outputFile   = OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "output file" )
                .create( "outputFile" );
        Option configFile   = OptionBuilder.withArgName( "configfile" )
                .hasArg()
                .withDescription(  "config file" )
                .create( "config" );
        Option format   = OptionBuilder.withArgName( "configfile" )
                .hasArg()
                .withDescription(  "config file" )
                .create( "config" );

        Option verbose = new Option( "verbose", "verbose help" );
        Option vverbose = new Option( "vverbose", "very verbose help" );
        Option fontlist = new Option( "fontlist", "list of fonts" );
        Option help = new Option( "help", "print this message" );

        return new Option[] { inputFile, outputFile, configFile, format, verbose, vverbose, fontlist, help };
    }


    /**
     * Creates a single string from all the args specified by the user on the command line
     *
     * @param args the user-specified args
     * @return the command line as one string. If there were no args, a single space is returned.
     */
    public String createCommandLine( final String[] args ) {
        
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
     * Determines whether an item was specified on the command-line. And if so,
     * what any argument for it is.
     *
     * @param argToFind argument to lookup
     * @return null if the item was not specified; "" if the item was specified by had no argument,
     *  and the argument if item was specified and has an argument.
     */
    public String lookup( final String argToFind )
    {
        if ( argToFind == null ) {
            return( null );    
        }
        if (!line.hasOption(argToFind))
            return null;
        String opt = line.getOptionValue(argToFind);
        if (opt == null)
            return "true";
        return opt;
    }

    /**
     * Processes some command-line options
     *
     * @param gdd GDD that will hold the state of some switches (such as verbosity)
     * @throws HelpMessagePrinted if no command-line args specified
     */   //curr: improve method name
    public void process( GDD gdd ) throws HelpMessagePrinted
    {
        showUsageIfZeroArgs(gdd);

        // Note: the -config option is entirely handled in Platypus.processConfigFile(), not here.

        processHelpOption(gdd);

        processVerboseOption(gdd);

        processVeryVerboseOption(gdd);

        processFontListOption(gdd);
    }

    private void processFontListOption(GDD gdd) throws HelpMessagePrinted {
        if ( line.hasOption( "fontlist" )) {
            TypefaceMap tfm = new TypefaceMap( gdd );
            tfm.loadFamilies();
            tfm.writeMapToFile( gdd.getHomeDirectory() + "config/fontlist.txt" );
            throw new HelpMessagePrinted( "OK" ); //todo: fix this way of escaping processing.
        }
    }

    private void processVeryVerboseOption(GDD gdd) {
        if ( line.hasOption( "vverbose" )) {
            gdd.setClVVerbose( true );
        }
    }

    private void processVerboseOption(GDD gdd) {
        if ( line.hasOption( "verbose" )) {
            gdd.setClVerbose( true );
        }
    }

    private void processHelpOption(GDD gdd) throws HelpMessagePrinted {
        if ( line.hasOption( "help" )) {
            Platypus.showUsage( gdd );
            // this exception exits Platypus without further processing and w/out
            // an error message. Technically, this is not an exception, but it's
            // the cleanest way of closing down from here.
            throw new HelpMessagePrinted( "OK" );
        }
    }

    private void showUsageIfZeroArgs(GDD gdd) {
        if( line.getOptions().length == 0) {
            Platypus.showUsage( gdd );
            System.err.println( gdd.getLit( "PLEASE_RERUN_WITH_FILENAMES" ));
            throw new UnsupportedOperationException( "ERR" ); //curr: delete ERR
        }  //curr: combine with graf below, using same exit exception.
    }

}
