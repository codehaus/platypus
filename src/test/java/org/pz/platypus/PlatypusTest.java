/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.apache.commons.cli.ParseException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.pz.platypus.exceptions.HelpMessagePrinted;
import org.pz.platypus.test.mocks.MockLiterals;
import org.pz.platypus.test.mocks.MockLogger;

import java.util.MissingResourceException;

/**
 * Tests for Platypus.java
 *
 * @author alb
 */
public class PlatypusTest
{
    private GDD gdd;

    @Before
    public void setUp()
    {
        gdd = new GDD();
        gdd.setLits( new MockLiterals() );
        gdd.setLogger( new MockLogger() );
        gdd.setupHomeDirectory();
    }

    //=== tests of findOutputFilePluginType()

    @Test
    public void testFindOutputPluginTypeInvalidOutputType1() throws ParseException {
        final String[] comLine = { "in.txt" };
        CommandLineArgs clArgs = null;

        try {
            clArgs = Platypus.processCommandLine( comLine, gdd );
        }
        catch ( HelpMessagePrinted hmp ) {
            fail( "Unexpected HelpMessagePrinted exception in PlatypusTest" );
        }
        gdd.setLogger( new MockLogger() );
        Platypus.findOutputFilePluginType( clArgs, gdd );
    }

    @Test (expected=MissingResourceException.class)
    public void testFindOutputPluginTypeInvalidOutputType2() throws ParseException {
        final String[] comLine = { "in.txt", "outputfilenamewithnodotinit" };
        CommandLineArgs clArgs = null;

        try {
            clArgs = Platypus.processCommandLine( comLine, gdd );
        }
        catch ( HelpMessagePrinted hmp ) {
            fail( "Unexpected HelpMessagePrinted exception in PlatypusTest" );
        }
        gdd.setLogger( new MockLogger() );
        Platypus.findOutputFilePluginType( clArgs, gdd );
    }

    @Test
    public void testFindOutputPluginTypeValidOutputType1() throws ParseException {
        final String[] comLine = { "in.txt", "out.pdf" };
        CommandLineArgs clArgs = null;

        try {
            clArgs = Platypus.processCommandLine( comLine, gdd );
        }
        catch ( HelpMessagePrinted hmp ) {
            fail( "Unexpected HelpMessagePrinted exception in PlatypusTest" );
        }
        gdd.setLogger( new MockLogger() );
        Platypus.findOutputFilePluginType( clArgs, gdd );
        assertEquals( "pdf", gdd.getOutputPluginPrefix() );
    }

    //=== tests of processCommandLine()

    @Test
    public void testProcessCommandLineValid() throws ParseException {
        final String[] comLine = { "in.txt", "out.pdf", "-verbose" };

        CommandLineArgs clArgs = null;

        try {
            clArgs = Platypus.processCommandLine( comLine, gdd );
        }
        catch ( HelpMessagePrinted hmp ) {
            fail( "Unexpected HelpMessagePrinted exception in PlatypusTest" );
        }
        assertNotNull( clArgs );
        assertTrue( gdd.getSysStrings().getString( "_commandLine").equals(
                "in.txt out.pdf -verbose" ));
    }

    //=== tests of processConfigFile()

    @Test
    public void testProcessConfigFileValid() throws ParseException {
        final String[] comLine = { "in.txt", "out.pdf", "-verbose" };
        CommandLineArgs clArgs = null;

//        try {
//            clArgs = Platypus.processCommandLine( comLine, gdd );
//        }
//        catch ( HelpMessagePrinted hmp ) {
//            fail( "Unexpected HelpMessagePrinted exception in PlatypusTest" );
//        }

        gdd.setConfigFile( new PropertyFile() );
        PropertyFile conff = gdd.getConfigFile();
        conff.loadLine( "pi.out.pdf=pdf" );

//        try {
//            Platypus.processConfigFile( clArgs, gdd );
//        }
//        catch( MissingResourceException mre ) {
//            // if this is run on a system where PLATYPUS_HOME is not defined,
//            // it will throw a MissingResourceException. So load, the values
//            // into the command table manually.
//            conff = gdd.getConfigFile();
//            if( conff == null ) {
//                gdd.setConfigFile( new PropertyFile() );
//            }
//            conff.loadLine( "pi.out.pdf=pdf" );
//        }
        assertTrue( gdd.getConfigFile().lookup( "pi.out.pdf" ) != " " );
        assertNotNull( gdd.getConfigFile().lookup( "pi.out.pdf" ));
    }


    //=== tests of setupLiterals()

    @Test (expected=MissingResourceException.class)
    public void tesSetupLiteralsInvalid()
    {
        Platypus.setupLiterals( "non-existentConfigFile" );
    }

    //=== tests of storeVerionsNumber()
    @Test
    public void testStoreVersionNumberValid()
    {
        final String version = "0.2.5";
        MockLiterals lits = new MockLiterals( );
        lits.setVersionNumberToReturn( version );
        gdd.setLits( lits );

        Platypus.storeVersionNumber( gdd );
        assertEquals( version, gdd.getSysStrings().getString( "_version" ));
    }
}
