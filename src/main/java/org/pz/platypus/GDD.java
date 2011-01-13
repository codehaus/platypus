/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2011 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.utilities.PlatypusHomeDirectory;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.MissingResourceException;
//import java.util.Stack;
import java.util.logging.*;

/**
 * Global document data. Is a singleton, although not created as such due to testing repercussions.
 *
 * @author alb
 * @author atul
 */
public class GDD
{
    private boolean         clVerbose = false;      // command line -verbose switch
    private boolean         clVVerbose = false;     // command line -vverbose switch
    private String          commandPropertyFilename;
    private CommandTable    commandTable;
    private PropertyFile    configFile;
    private String          expandedMacro;
    private String          fileSeparator;          // fileSeparator char for user platform
//    private Stack<Infile>   filesToProcess;
    private String          homeDirectory;
    private boolean         inCode;
    private FileList        inputFileList;
    private LineList        inputLines;
    private TokenList       inputTokens;
    private Literals        lits;
    private Logger          logger;
    private String          outputPluginPrefix;
    private String          symbolsListFilename;
    private SystemStrings   sysStrings;
    private Map<String,String> userEnv;
    private UserStrings     userStrings;

    public GDD()
    {
        sysStrings = new SystemStrings();
        initialize();
    }

    /**
     * initialize many of the fields in GDD. To completelely initialize GDD, the following
     * calls are must be made (cf. Platypus.java.setupGdd()):
     * - setLits()
     * - setupLogger()
     * - setupHomeDirectory()
     *
     * This sequence is due to the fact that the latter functions depend on lits.
     */
    public void initialize()
    {
        commandPropertyFilename = "Commands.properties";    // the default name
        commandTable = null;
        expandedMacro = null;
        fileSeparator = System.getProperty( "file.separator" );
        inCode = false;
        inputFileList = new FileList();
        inputLines = new LineList();
        inputTokens = new TokenList();
        lits = null;
        outputPluginPrefix = null;
        symbolsListFilename = "Symbols.list";
        sysStrings = new SystemStrings();
        userEnv = System.getenv();
        userStrings = new UserStrings();
        loadRgbColorsIntoUserStrings( userStrings );
    }

    /**
     * Loads the 16 HTML standard colors as RGB values into the user strings map
     * @param userMacros the KV table containing user-defined macros
     */
    public void loadRgbColorsIntoUserStrings( final UserStrings userMacros )
    {
        userMacros.add( "AQUA",    "0,255,255" );    // x00FFFF
        userMacros.add( "BLACK",   "0,0,0" );        // x000000
        userMacros.add( "BLUE",    "0,0,255" );      // x0000FF
        userMacros.add( "FUSCHIA", "255,0,255" );    // xFF00FF
        userMacros.add( "GRAY",    "128,128,128" );  // x808080
        userMacros.add( "GREY",    "128,128,128" );  // x808080 --- same as GRAY
        userMacros.add( "GREEN",   "0,128,0" );      // x008000
        userMacros.add( "LIME",    "0,255,0" );      // x00FF00
        userMacros.add( "MAROON",  "128,0,0" );      // x800000
        userMacros.add( "NAVY",    "0,0,128" );      // x000080
        userMacros.add( "OLIVE",   "128,128,0" );    // x808000
        userMacros.add( "PURPLE",  "128,0,128" );    // x800080
        userMacros.add( "RED",     "255,0,0" );      // xFF0000
        userMacros.add( "SILVER",  "192,192,192" );  // xC0C0C0
        userMacros.add( "TEAL",    "0,128,128" );    // x008080
        userMacros.add( "WHITE",   "255,255,255" );  // xFFFFFF
        userMacros.add( "YELLOW",  "255,255,0" );    // xFFFF00
    }

    /**
     * sets up the home directory for Platypus; that is, the directory from
     * which Platypus is being run. The /config /fonts and /plugins directory
     * are subdirectories of this directory and we later need their location.
     */
    public void setupHomeDirectory()
    {
        PlatypusHomeDirectory phd = new PlatypusHomeDirectory( this.getClass() );
        homeDirectory = phd.get();
        if( homeDirectory == null ) {
            logger.severe( "ERROR.PLATYPUSHOME_UNDEFINED" );
            throw new MissingResourceException( null, null, null );
        }
    }

    /**
     * set up the logger we'll use and give it our own formatting.
     * This logger writes to the console only.
     *
     * @param loggerName name for the logger
     */
    public void setupLogger( final  String loggerName )
    {
        logger = Logger.getLogger( loggerName == null ? "org.pz.platypus.Platypus" : loggerName );

        ConsoleHandler consh = new ConsoleHandler();
        consh.setLevel( Level.ALL );
        consh.setFormatter( new LogFormatter( lits ));
        logger.addHandler( consh );
        logger.setUseParentHandlers( false );

        // By default we log only errors and warnings. To get info-level messages, the
        // user must specify -verbose on command line. To get all info (mostly for debug),
        // user must specify -vverborse on command line. (-vverbose = very verbose)
        logger.setLevel( Level.WARNING );
    }


    /**
     * Status logger. Writes to stdout on -verbose or higher setting. Does not use
     * Literals file, as we want all diagnostic data in English for our use. Also
     * adds the date and time to the log message sent by the calling method.
     *
     * @param msg the message to log to the console
     */
    public void log( final String  msg )
    {
        if( isClVerbose() ) {
            String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
            System.out.println( s + " " + msg );
        }
    }

    //=== following are just simple logging shortcuts ===/
    public void logFine( final String msg ) { logger.fine( msg ); }
    public void logFiner( final String msg ) { logger.finer( msg ); }
    public void logFinest( final String msg ) { logger.finest( msg ); }
    public void logInfo( final String msg ) { logger.info( msg ); }
    public void logSevere( final String msg ) { logger.severe( msg ); }
    public void logWarning( final String msg )
        { logger.warning( msg ); }

    //=== getters and setters ===//


    public String getCommandPropertyFilename()
    {
        return( commandPropertyFilename );
    }

    public void setCommandPropertyFilename( final String commandPropertyFilename )
    {
        this.commandPropertyFilename = commandPropertyFilename;
    }

    public CommandTable getCommandTable() {
        return( commandTable );
    }

    public void setCommandTable( final CommandTable newCommandTable )
    {
        commandTable = newCommandTable;
    }

    public PropertyFile getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile( PropertyFile newConfigFile )
    {
        configFile = newConfigFile;
    }

    public String getExpandedMacro()
    {
        return( expandedMacro );
    }

    public void setExpandedMacro( final String expandedMacro )
    {
        this.expandedMacro = expandedMacro;
    }

    public String getFileSeparator()
    {
        return( fileSeparator );
    }

    public String getHomeDirectory()
    {
        return( homeDirectory );
    }

    public boolean isInCode()
    {
        return( inCode );
    }

    public void setInCode( final boolean trueOrFalse )
    {
        inCode = trueOrFalse;
    }

    public FileList getInputFileList()
    {
        return( inputFileList );
    }

    public void setInputFileList( final  FileList newInputFileList )
    {
        inputFileList = newInputFileList;
    }

    public LineList getInputLines()
    {
        return( inputLines );
    }

    public TokenList getInputTokens()
    {
        return( inputTokens );
    }

    public void setInputTokens( final TokenList newInputTokens )
    {
        inputTokens = newInputTokens;
    }

    public String getLit( final String key )
    {
        return( lits.getLit( key ));
    }

    public Literals getLits()
    {
        return( lits );
    }

    public void setLits( final Literals newLits )
    {
        lits = newLits;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void setLogger( final Logger newLogger )
    {
        logger = newLogger;
    }


    public String getOutputPluginPrefix()
    {
        return outputPluginPrefix;
    }

    public void setOutputPluginPrefix( final String outputPluginPrefix )
    {
        this.outputPluginPrefix = outputPluginPrefix;
    }

    public String getPlatypusDirectory()
    {
        if ( userEnv == null ) {
            return( null );
        }

        return( userEnv.get( "PLATYPUS_HOME" ));
    }

    public String getSymbolsListFilename()
    {
        return(  symbolsListFilename );
    }

    public SystemStrings getSysStrings()
    {
        return sysStrings;
    }

    public UserStrings getUserStrings()
    {
        return userStrings;
    }

    public boolean isClVerbose()
    {
        return clVerbose;
    }

    public void setClVerbose( boolean clVerbose )
    {
        this.clVerbose = clVerbose;
        this.logger.setLevel( Level.FINE );
    }

    public boolean isClVVerbose()
    {
        return clVVerbose;
    }

    /**
     * -vverbose implies -verbose, so both are set. The order in which they're set
     * is important as the logger level has to be set correctly.
     *
     * @param beVerbose  true/false setting
     */
    public void setClVVerbose( boolean beVerbose )
    {
        setClVerbose( beVerbose );
        this.clVVerbose = beVerbose;
        this.logger.setLevel( Level.FINEST );
    }

    public Map<String, String> getUserEnv()
    {
        return userEnv;
    }

}
