/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.exceptions.HelpMessagePrinted;
import org.pz.platypus.exceptions.StopExecutionExecption;
import org.pz.platypus.test.mocks.MockLiterals;
import org.apache.commons.cli.ParseException;

import java.util.logging.Level;

/**
 *
 * @author alb
 */
public class CommandLineArgsTest {

    private CommandLineArgs cl;
    private GDD gdd;

    @Before
    public void setUp() {
        cl = new CommandLineArgs( null );

        gdd = new GDD();
        gdd.setLits( new MockLiterals() );
        gdd.setupLogger( ( "org.pz.platypus.Platypus" ));
        gdd.getLogger().setLevel( Level.OFF );
    }
  
    @Test
    public void testLoadFilenamesOnly() {
        final String infileName = "infile.txt";
        final String outfilename = "outfile.pdf";

        String[] args = { infileName, outfilename };
        cl = new CommandLineArgs( args );

        assertEquals( infileName, cl.lookup( "inputFile" ));
        assertEquals( outfilename, cl.lookup( "outputFile" ));    
     }

    @Test
    public void testInvalidLookups() 
    {
        final String infileName = "infile.txt";
        final String outfilename = "outfile.pdf";

        String[] args = { infileName, outfilename  };
        CommandLineArgs cl = new CommandLineArgs( args );

        assertNull( cl.lookup( null ));
        assertNull( cl.lookup( "non-existent option") );
    }

    @Test
    public void testSpecifyingInfileOnly()
    {
        final String infileName = "infile.txt";

        String[] args = { infileName };
        CommandLineArgs cl = new CommandLineArgs( args );
                
        assertEquals( infileName, cl.lookup( "inputFile" )  );
        assertEquals( null, cl.lookup( "outputFile" ) );
    }

    @Test
    public void testZeroArgOption()
    {
        final String help = "-help";

        String[] args = { help };
        CommandLineArgs cl = new CommandLineArgs( args );

        assertEquals( "true", cl.lookup( "help" ));
        assertEquals( null, cl.lookup( "inputFile") );
    }


    @Test
    public void testZeroAndOneArgOptions()
    {
        final String verbose = "-verbose";
        final String configFile = "-config";
        final String fileName = "config.file";

        String[] args = { verbose, configFile, fileName };
        CommandLineArgs cl = new CommandLineArgs( args );

        assertEquals( "true", cl.lookup( verbose ));
        assertEquals( fileName, cl.lookup( "config" ));
    }
    
    @Test
    public void testVerbose()
    {
        final String verbose = "-verbose";
        final String configFile = "-config";
        final String fileName = "config.file";
        
        String[] args = { verbose, configFile, fileName };
        CommandLineArgs cl = new CommandLineArgs( args );

        try {
            cl.process( gdd );
        }
        catch ( HelpMessagePrinted hmp ) {
            fail( "unexpected HelpMessagePrinted exception occurred" );
        }
        assertTrue( gdd.isClVerbose() );
    }
    
    @Test
    public void testVverbose()
    {
        final String verbose = "-vverbose";
        final String configFile = "-config";
        final String fileName = "config.file";
        
        String[] args = { verbose, configFile, fileName };
        CommandLineArgs cl = new CommandLineArgs( args );
        
        try {
            cl.process( gdd );
        }
        catch ( HelpMessagePrinted hmp ) {
            fail( "unexpected HelpMessagePrinted exception occurred" );
        }
        assertTrue( gdd.isClVerbose() );
        assertTrue( gdd.isClVVerbose() );
    } 
    
    @Test
    public void testCreateCommandLine() {
        final String verbose = "-vverbose";
        final String configFile = "-config";
        final String fileName = "config.file";
        
        String[] args = { verbose, configFile, fileName };
        CommandLineArgs cl = new CommandLineArgs( args );
        assertTrue( "-vverbose -config config.file".equals( cl.createCommandLine( args )));
    }
    
    @Test
    public void testCreateCommandLineWithNoArgs() {
        String[] args = { };
        CommandLineArgs cl = new CommandLineArgs( args );
        System.out.println( cl.createCommandLine( args ));
        assertTrue( " ".equals( cl.createCommandLine( args )));
    }

    @Test
    public void testCreateCommandLine1() {
        CommandLineArgs clArgs = new CommandLineArgs(new String[] { "" });
        String argStr = clArgs.createCommandLine(new String[] { "a", "b", "c" });
        assertEquals("a b c", argStr);
    }

    @Test
    public void testCreateCommandLine2() {
        CommandLineArgs clArgs = new CommandLineArgs(new String[] { "" });
        String argStr = clArgs.createCommandLine(new String[0]);
        assertEquals(" ", argStr);
    }

    @Test(expected = HelpMessagePrinted.class)
    public void testZeroArgsGivingNPE() throws Exception {
        Platypus.processCommandLine( new String[0], gdd );
    }


    @Test(expected = StopExecutionExecption.class)
    public void testUnsupportedOption() throws Exception {
        Platypus.processCommandLine( new String[] { "-NotSupported"}, gdd );
    }

    @Test
    public void testPreProcessingJustInputAndOutput() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { infileName, outfileName } );
        assertArrayEquals(new String[] { "-inputFile", infileName, "-outputFile", outfileName}, cmdLine );
    }

    @Test
    public void testPreProcessingConfigComesBefore() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-config", "config.txt", infileName, outfileName } );
        assertArrayEquals(new String[] { "-config", "config.txt", "-inputFile", infileName, "-outputFile", outfileName}, cmdLine );
    }

    @Test
    public void testPreProcessingConfigComesAfter() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { infileName, outfileName, "-config", "config.txt" } );
        assertArrayEquals(new String[] { "-inputFile", infileName, "-outputFile", outfileName, "-config", "config.txt" }, cmdLine );
    }

    @Test
    public void testPreProcessingConfigBeforeFormatAfter() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-config", "config.txt", infileName, outfileName, "-format", "pdf" } );
        assertArrayEquals(new String[] { "-config", "config.txt", "-inputFile", infileName, "-outputFile", outfileName, "-format", "pdf" }, cmdLine );
    }

    @Test
    public void testPreProcessingConfigInputFileFormatOutputFile() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-config", "config.txt", infileName, "-format", "pdf", outfileName,  } );
        assertArrayEquals(new String[] { "-config", "config.txt", "-inputFile", infileName, "-format", "pdf", "-outputFile", outfileName, }, cmdLine );
    }

    @Test
    public void testPreProcessingVerboseInputFileOutputFile() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-verbose", infileName, outfileName,  } );
        assertArrayEquals(new String[] { "-verbose", "-inputFile", infileName, "-outputFile", outfileName, }, cmdLine );
    }

    @Test
    public void testPreProcessingHelpVVerboseInputFileOutputFile() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-help", "-vverbose", infileName, outfileName,  } );
        assertArrayEquals(new String[] { "-help", "-vverbose", "-inputFile", infileName, "-outputFile", outfileName, }, cmdLine );
    }

    @Test
    public void testPreProcessingHelpInputFileVVerboseOutputFile() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-help", infileName, "-vverbose", outfileName,  } );
        assertArrayEquals(new String[] { "-help", "-inputFile", infileName, "-vverbose", "-outputFile", outfileName, }, cmdLine );
    }

    @Test
    public void testPreProcessingConfigVerboseInputFileHelpFormatOutputFile() throws Exception {
        final String infileName = "infile.txt";
        final String outfileName = "outfile.txt";
        String[] cmdLine = CommandLineArgs.preProcessCommandLine( new String[] { "-config", "config.txt", "-verbose", infileName, "-help", "-format", "pdf", outfileName,  } );
        assertArrayEquals(new String[] { "-config", "config.txt", "-verbose", "-inputFile", infileName, "-help", "-format", "pdf", "-outputFile", outfileName, }, cmdLine );
    }
}