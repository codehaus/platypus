/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.exceptions.FilenameLookupException;
import org.pz.platypus.exceptions.HelpMessagePrinted;
import org.pz.platypus.exceptions.StopExecutionException;
import org.pz.platypus.exceptions.InvalidInputException;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.MissingResourceException;

/**
 * The main line.
 *
 * @author alb
 */
public class Platypus
{
    private static Literals lits;
    private static PluginLoader pluginLoader;

    /**
     * add the input file to the file list
     *
     * @param clArgs command line containing the name of the input file
     * @param gdd GDD containing the FileLits for this program
     */
    static private void addInputFileToFileList( CommandLineArgs clArgs, GDD gdd )
    {
        final String inputFile = clArgs.lookup( "inputFile" );
        final FileList startingFileList = gdd.getInputFileList();
        try {
            startingFileList.addFilename( inputFile );
        }
        catch( FilenameLookupException fle ) {
            // in theory, this cannot happen.
            throw new MissingResourceException( null, null, null );
        }
    }
    /**
     * output the copyright notice to console
     */
    static private void displayCopyright()
    {
        System.out.println( "Platypus " + lits.getLit( "VERSION" ) + " " +
            lits.getLit( "COPYRIGHT_NOTICE" ));
    }

    /**
     * tries to determine the prefix in the config file for the output plugin. (such as: pdf)
     * First checks the -format option on the command line. If it's not specified there,
     * checks the extension of the output file. Prefix is the lower-case version of the string,
     * and it is stored in the Gdd.
     *
     * @param ClArgs the command line args
     * @param Gdd the GDD
     * @throws MissingResourceException if the output file format cannot be determined.
     */
    static public void findOutputFilePluginType( CommandLineArgs ClArgs, GDD Gdd )
    {
        String pluginType = ClArgs.lookup( "format" );
        if ( pluginType == null || pluginType.isEmpty() ) {

            final String outputFilename = ClArgs.lookup( "outputFile" );
            if ( outputFilename == null || outputFilename.isEmpty() ) {
                Gdd.getLogger().severe( Gdd.getLit( "ERROR.UNKNOWN.OUTPUTFORMAT" ));
                throw new MissingResourceException( null, null, null );
            }

            int i = outputFilename.lastIndexOf( '.' );
            if ( i == -1 || outputFilename.endsWith(".") ) {
                Gdd.getLogger().severe( Gdd.getLit( "ERROR.UNKNOWN.OUTPUTFORMAT" ));
                throw new MissingResourceException( null, null, null );
            }

            pluginType = outputFilename.substring( i+1 );
        }
        Gdd.setOutputPluginPrefix( pluginType.toLowerCase() );
    }

    /**
     * Get the actual name of the plugin, based on its lookup value in config file.
     *
     * Check whether the plug-in is in the default PLATYPUS_HOME\plugins directory.
     * if not, check whether the configuration file gives the plug-ins location.
     *
     * @param pluginJarLookupName name of the plugin file (a JAR file) in the PLATYPUS_HOME\plugins
     * @param gdd the GDD
     * @return the jar file name
     */
    private static String findPluginJar( final String pluginJarLookupName, final GDD gdd )
    {
        if ( ! new File( pluginJarLookupName ).exists() ) {
            String searchName = "pi.out." + gdd.getOutputPluginPrefix() + ".location";
            String pluginJar = gdd.getConfigFile().lookup( searchName );

            if ( pluginJar == null || ! new File( pluginJar ).exists() ) {
                gdd.logSevere( lits.getLit( "ERROR.INVALID_PLUGIN_URL" ) +
                               ": " + searchName );
                throw new MissingResourceException( null, null, null );
            }
            return( pluginJar );
        }
        else {
            return( pluginJarLookupName );
        }
    }

    /**
     * Get the PLATYPUS_HOME directory, tack on the \plugins subdir, then add the output format.jar;
     * Then, try to load this output plugin.
     *
     * @param gdd the GDD
     * @param clArgs the command line args
     * @return plugin location
     * @throws MissingResourceException in event the PLATYPUS_HOME directory has not been defined.
     */
    static private String loadOutputPlugin( final GDD gdd, final CommandLineArgs clArgs )
           throws MissingResourceException
    {

        final String PlatypusDir = gdd.getHomeDirectory();
        final String jarFilename =
                gdd.getConfigFile().lookup( "pi.out." + gdd.getOutputPluginPrefix());

        if ( jarFilename == null ) {
            final String errMsg = gdd.getLit( "ERROR.OUTPUT_TYPE_NOT_IN_CONFIGFILE" ) +
                                    " " + gdd.getOutputPluginPrefix();
            gdd.logSevere( errMsg );
            throw new InvalidInputException( errMsg, Status.ERR_UNSUPPORTED_FORMAT );
        }

        String pluginJar =
                PlatypusDir + "plugins" + gdd.getFileSeparator() + jarFilename;
        pluginJar = findPluginJar( pluginJar, gdd );


        gdd.log( "Loading output plug-in: " + pluginJar );
        return( pluginJar );
    }

    /**
     * The method that actually calls and runs the output plugin
     *
     * @param pluginName name of the plugin file
     * @param gdd the GDD
     * @param clArgs command-line args
     */
    static private void runOutputPlugin( final String pluginName,
                                         final GDD gdd, final CommandLineArgs clArgs )
    {
        pluginLoader = new PluginLoader( pluginName, gdd );
        gdd.log( "Transferring control to output plug-in." );
        pluginLoader.load( clArgs );
    }

    /**
     * Prints out the error message in the event of an unexpected error. Called
     * only from the Platypus.main() and serves as the backup exception handler if
     * some exception is not handled elsewhere.
     *
     * @param ex the exception
     * @param gdd the GDD
     */
    static private void logUnexpectedError( final Exception ex, GDD gdd )
    {
        gdd.logSevere( gdd.getLit( "ERROR.UNEXPECTED" ));
        System.err.println( ex );
    }

    /**
     * Process the command line: store it, validate it, and process the args.
     * @param args the command-line args
     * @param gdd the GDD
     * @throws HelpMessagePrinted if the user specified -help on the command line. In this case,
     *         no further processing is needed (after the help message has been displayed).
     * @return the CommandLineArgs structure that is filled in by this method
     */
    static public CommandLineArgs processCommandLine( final String[] args, GDD gdd ) throws
            HelpMessagePrinted, ParseException {
        CommandLineArgs clArgs = new CommandLineArgs( args );

        final String commandLine = clArgs.createCommandLine( args );
        gdd.getSysStrings().add( "_commandLine", commandLine );

        clArgs.process( gdd );
        gdd.log( "Command line: " + commandLine );

        return( clArgs );
    }

    /**
     * Places the output format into the SystemStrings lookup table
     * @param clArgs command-line args
     * @param gdd the GDD
     */
    static public void putFormatInSystemStrings( CommandLineArgs clArgs, GDD gdd )
    {
        String outputFormat;

        outputFormat =  clArgs.lookup( "format" );
        if( outputFormat == null ) {
            final String outputFilename = clArgs.lookup( "outputFile" );
            outputFormat = outputFilename.substring( outputFilename.lastIndexOf( '.' ) + 1 );
        }
        if( outputFormat == null ) {
            outputFormat = "unknown";
        }
        else {
            outputFormat = outputFormat.toUpperCase();
        }

        gdd.getSysStrings().add( "_format", outputFormat );
    }

    /**
     * Finds the config file, reads it in, and places an instance of it in GDD.
     * If the file can't be found, this method throws MissingResourceException,
     * which is caught in the main line. At that point, Platypus is shut down
     * (the error message having already been written to stderr).
     *
     * @param Clargs the command line args
     * @param Gdd the GDD
     * @throws MissingResourceException in the event the config file cannot be loaded
     */
    static public void processConfigFile( CommandLineArgs Clargs, final GDD Gdd )
           throws MissingResourceException
    {
        final String filename = getConfigFilename( Clargs, Gdd );

        final PropertyFile configFile = new PropertyFile( filename, Gdd );
        if ( configFile.load() != Status.OK ) { //curr: throw unchecked exception at point of error, don't propagate upwards. Catch() already in main().
            throw new MissingResourceException( null, null, null );  //curr: make it a specialized unchecked exception. i.e., ConfigFileLoadException...
        }

        Gdd.setConfigFile( configFile );
    }

    /**
     * Get the name of the config file either from the command file or from the default
     * location
     * @param Gdd the GDD
     * @param ClArgs object containing the command-line args
     * @return string containing the name of the config file.
     */
    private static String getConfigFilename( final CommandLineArgs ClArgs, final GDD Gdd )
    {
        String filename = null;

        if( ClArgs != null ) {
            filename = ClArgs.lookup( "config" );
        }

        // if the config file is not specified on the command line,
        // go to the default: Config.properties in PLATYPUS_HOME directory.

        if ( filename == null || filename.isEmpty() ) {
            filename = Gdd.getHomeDirectory() + "config\\" + "Config.properties";
        }

        return filename;
    }

    /**
     * Reads and parses the input file(s).
     *
     * @param gdd the GDD
     * @param clArgs the command-line arguments
     * @throws NoSuchFieldException if no input file is specified, or it's invalid.
     */
    static public void processInputFile( final GDD gdd, final CommandLineArgs clArgs)
           throws NoSuchFieldException
    {
        final String filename = clArgs.lookup( "inputFile" );

        if ( filename == null ) {
            gdd.logSevere( lits.getLit( "ERROR.MISSING_INPUT_FILE" ));
            throw new NoSuchFieldException();
        }

        final Infile inputFile = new Infile( filename, gdd );
        final int r = inputFile.readFileIntoInputLines( gdd.getInputLines() );
        if( r == Status.FILE_NOT_FOUND_ERR ) {
            gdd.logSevere( lits.getLit( "ERROR.FILE_NOT_FOUND") + " " + filename );
            throw new NoSuchFieldException();
        }
        else if( r == Status.FILE_NOT_READABLE_ERR ) {
            gdd.logSevere( lits.getLit( "ERROR.FILE_NOT_READABLE" ) + " " + filename );
            throw new NoSuchFieldException();
        }
        else {
            gdd.log( "Read input file with " + inputFile.getLineNumber() + " lines: " + filename );
        }

        CommandTable commandTable = new CommandTable( gdd );
        commandTable.loadCommands( gdd.getCommandPropertyFilename() );
        commandTable.loadSymbols();
        gdd.setCommandTable( commandTable );

        //curr: create factory to decide which parser to use (PlatypusParser or LineTokenizeParser)

        //curr: test here for whether parsing is done by Platypus. Is there a use-case for not parsing?
        new PlatypusParser( gdd ).parse( gdd.getInputLines(),
                                         gdd.getInputTokens(),
                                         gdd.getConfigFile(),
                                         gdd.getOutputPluginPrefix() );

        // delete the parsed input lines as input files can be substantial.
        gdd.getInputLines().clear();
    }

    /**
     * GDD is the global document data block. It holds data items frequently used
     * in processing Platypus files. Note the setup steps here need to be sequenced
     * this way, as each depends on steps taken by the previous step(s).
     *
     * @param lits the Literals
     * @return GDD the new gdd
     */
    static private GDD setupGdd( final Literals lits )
    {
        GDD gdd = new GDD();
        gdd.setLits( lits );
        gdd.setupLogger( "org.pz.platypus.Platypus" );
        gdd.setupHomeDirectory();

        return( gdd );
    }

    /**
     * Load the default literals file, which is a property file.
     *
     * @param baseFilename base of the Literals filename. ".properties" extension will be appended
     * @throws MissingResourceException if an error occurs while file is read and loaded.
     */
    static public void setupLiterals( final String baseFilename )
            throws MissingResourceException
    {
        try {
            lits = new Literals( baseFilename );
        }
        catch ( MissingResourceException mre ) {
                throw new MissingResourceException( null, null, null );
        }
    }

    /**
     * Show the usage options. Generally displayed after -help or in the
     * event of command-line option errors.
     *
     * @param Gdd GDD containing the Literals
     */
     public static void showUsage( GDD Gdd )
     {
        if ( lits == null ) {
            setupLiterals( "Platypus" );
            Gdd.setLits( lits );
        }

        System.out.println( '\n' + Gdd.getLit( "USAGE.GENERAL.1" ));
        System.out.println( Gdd.getLit( "USAGE.GENERAL.2" ));
        System.out.println( Gdd.getLit( "USAGE.GENERAL.3" ));
        System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.CONFIG" ));
        System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.FONTLIST" ));
        System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.FORMAT" ));
        System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.HELP" ));
     // System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.PAGESIZE" ));
        System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.VERBOSE" ));
        System.out.println( '\t' + Gdd.getLit( "USAGE.OPTION.VVERBOSE" ));
     }

    /**
     * Stores the current Platypus version number in the table of system strings under _version
     * @param Gdd global document data structure
     */
    public static void storeVersionNumber( final GDD Gdd )
    {
        Gdd.getSysStrings().add( "_version", Gdd.getLit( "VERSION" ));
    }

    /**
     * Where the rubber meets the road...
     *
     * @param args command-line args
     */
    public static void main( String[] args ) throws ParseException {
        GDD gdd = null;
        CommandLineArgs clArgs = null;

        try {
            setupLiterals( "Platypus" );
            displayCopyright();
        }
        catch( MissingResourceException mre ) {
                System.exit( Status.ERR );
        }

        try {
            gdd = setupGdd( lits );
            storeVersionNumber( gdd );
            clArgs = processCommandLine( args, gdd );
            putFormatInSystemStrings( clArgs, gdd );
            addInputFileToFileList( clArgs, gdd );
            processConfigFile( clArgs, gdd );
            findOutputFilePluginType( clArgs, gdd );
        }
        catch( UnsupportedOperationException e ) {
            System.exit( Status.ERR );
        }
        catch( MissingResourceException mre ) {
            System.exit( Status.ERR );
        }
        catch( HelpMessagePrinted hmp ) {
            System.exit( Status.OK );
        } catch ( StopExecutionException see) {
            System.exit( Status.OK );
        }

        try {
            processInputFile( gdd, clArgs );
            final String plugin = loadOutputPlugin( gdd, clArgs );
            runOutputPlugin( plugin, gdd, clArgs );

            gdd.log( "Returned from output plug-in." );

            if( gdd.isClVVerbose() ) {
                gdd.getInputTokens().dump( gdd );
            }
        }
        catch ( NoSuchFieldException nsfe ) {
            System.exit( Status.INVALID_INPUT_FILE );
        }
        catch (InvalidInputException iie) {
            System.exit( iie.getStatus() );
        }
        catch ( Exception ex ) {
            logUnexpectedError( ex, gdd );
            if( gdd.getInputTokens().size() < 1 ) {
                System.err.println( gdd.getLit( "ERROR_OCCURRED_PRIOR_TO_TOKEN_GEN" ));
            }
        }
    }

}
