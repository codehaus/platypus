/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 *  Runs functional tests on the Platypus CLI (command-line interface)
 *
 *  @author alb
 */

import org.apache.commons.cli.ParseException

// the JAR to test should be passed to this script as args[0]
if( ! args ) {
    println( "Error! Missing arg. Usage: " +
             "groovy clitest.groovy JAR-file-to-test-with-path. " +
             "Please rerun specifying jar to test")
    return;
}

def String jarUnderTest = args[0]
def String javaRun = "java -jar " + jarUnderTest


testCopyrightString( javaRun )
testHelpOutput( javaRun )
testHelpOutputEqualToNoCommandLineArgs( javaRun )
testValidCLButMissingInputFile( javaRun )
testEmptyInputFile( javaRun )
testUnsupportedFormat( javaRun )
testFormatSpecifiedButNoFiles( javaRun )
testInputFileOnly( javaRun )
testPdfInputOutput( javaRun )
testPdfInputFileOutputFileVerbose( javaRun )
testPdfInputFileOutputFileVeryVerbose( javaRun )
testListingSimpleInputOutput( javaRun )
testListingInputOutputVeryVerbose( javaRun )
testPdfInputOutputInvalidConfigFile( javaRun )
testPdfInputOutputWithExtraUnsupportedOption( javaRun )

return

/**
 * Are we generating a valid copyright strings as part of the output to the console?
 */
def void testCopyrightString( String javaRun )
{
    def String description = "Test: valid copyright message"
    def proc;
    def output;

    try {
        proc = javaRun.execute()
        output = proc.in.text
    }
    catch( ParseException th ) {
        println( "***FAILURE in " + description + " Exception: " + th.dump() )
        return
    }

    if( output.contains( "(c) Copyright 2006-0" ) &&
        output.contains( "Pacific Data Works LLC. All Rights Reserved." ))
        print( "Success in " )
    else
        print( "***FAILURE in " )
    println( description );
}

/**
 * Test the output of the -help command
 */
def void testHelpOutput( String javaRun )
{
    def String description = "Test: valid -help output"
    javaRun += " -help"
    def proc = javaRun.execute()
    def output = proc.in.text

    if( output.contains( "(c) Copyright 2006-0" ) &&
        output.contains( "Platypus converts a Platypus document to PDF" ) &&
        output.contains( "Usage: java -cp . -jar Platypus.jar input-file output-file [options]." ) &&
        output.contains( "Options are:" ) &&
        output.contains( "this help screen" ) &&
        output.contains( "-vverbose" ))
        print( "Success in " )
    else
        print( "***FAILURE in " )
    println( description );
}

/**
 * Test that the output of running Platypus with no command-line args is the same as
 * running it with -help
 */
def void testHelpOutputEqualToNoCommandLineArgs( String javaRun )
{
    def String description = "Test: -help generates the same output as no CLI parameters"
    def proc = javaRun.execute()
    def output = proc.in.text

    javaRun += " -help"
    def procWithHelp = javaRun.execute()
    def outputFromHelp = procWithHelp.in.text

    if( output == outputFromHelp ) // in Groovy, strings are compared with ==
        print( "Success in " )
    else
        print( "***FAILURE in " )
    println( description );
}

/**
 * Does a valid command line, but for a non-existent input file generate the right error?
 */
def void testValidCLButMissingInputFile( String javaRun )
{
    def String description = "Test: non-existent input file"
    javaRun += " nonexistent.plat nonexistent.pdf"
    def proc = javaRun.execute()
    def err = proc.err.text

    if( err.contains( "Error: Cannot find file: nonexistent.plat" ))
        print( "Success in " )
    else  {
        print( "***FAILURE in " )
    }
    println( description );
}

/**
 * Test warning for processing an empty file.
 *
 * Create an empty file; process it; see if the correct warning message is generated; delete file.
 */
def void testEmptyInputFile( String javaRun )
{
    def String description = "Test: empty input file"

    // create the empty file
    def String emptyFileName = "emptyFile.plat"
    def File emptyFile = new File( emptyFileName )
    PrintWriter pw = new PrintWriter( emptyFile )
    pw.write( "" )
    pw.close()
    if ( ! emptyFile.exists() ) {
        println( "***FAILURE in ${description}. Could not Create empty file" )
        return
    }

    // run it and test for error message
    javaRun += " ${emptyFileName} ${emptyFileName}.pdf"
    def proc = javaRun.execute()
    def err = proc.err.text

    if( err.contains( "Warning: The input file did not generate any output." ))  {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    emptyFile.delete()
}

/**
 * Is the correct error message generated if an invalid (unsupported) -format is specified?
 *
 * An empty file is created for this test, because Platypus will catch the fact that the input
 * file does not exist (and exit with an error message) before it checks for a valid output format.
 */
def void testUnsupportedFormat( String javaRun )
{
    def String description = "Test: unsupported -format"

    // create the empty file
    def String emptyFileName = "emptyFile.plat"
    def File emptyFile = new File( emptyFileName )
    PrintWriter pw = new PrintWriter( emptyFile )
    pw.write( "" )
    pw.close()
    if ( ! emptyFile.exists() ) {
        println( "***FAILURE in ${description}. Could not Create empty file" )
        return
    }

    // run it and test for error message
    javaRun += " ${emptyFileName} ${emptyFileName}.out -format fungus"
    def proc = javaRun.execute()
    def err = proc.err.text

    if( err.contains(
        "Error: Configuration file does not contain support for output format: fungus" ))  {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    emptyFile.delete()
}

/**
 * If -format is specified, but no files are given, do we get a valid error message?
 */
def void testFormatSpecifiedButNoFiles( String javaRun )
{
    def String description = "Test: -format, but no input/output files specified"

    javaRun += " -format pdf"
    def proc = javaRun.execute()
    def err = proc.err.text

    if( err.contains( "Error: No file to process was specified. Exiting..." ))
        print( "Success in " )
    else  {
        print( "***FAILURE in " )
    }
    println( description );
}

/**
 * Test of simple PDF file generation, when only the input file is specified.
 */
def void testInputFileOnly( String javaRun )
{
    def String description = "Test: input-file (only)"
	// should generate a valid pdf file named hello.pdf

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName}"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File pdfFile = new File( "hello.pdf" )

    if(( err == null || err.isEmpty() ) && ( pdfFile.exists() )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    pdfFile.delete()
}

/**
 * Test of simple PDF file generation.
 */
def void testPdfInputOutput( String javaRun )
{
    def String description = "Test: input-file output-file.pdf"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.pdf"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File pdfFile = new File( "${helloFileName}.pdf" )

    if(( err == null || err.isEmpty() ) && ( pdfFile.exists() )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    pdfFile.delete()
}

/**
 * Test for valid output from PDF generation with -verbose option
 */
def void testPdfInputFileOutputFileVerbose( String javaRun )
{
    def String description = "Test: input-file output-file.pdf -verbose"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.pdf -verbose"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File pdfFile = new File( "${helloFileName}.pdf" )

    // make sure output contains some verbose logging
    if(( err == null || err.isEmpty() ) &&
        pdfFile.exists() &&
        output.contains( "Property file loaded with") &&
        output.contains( "Processing starting in PDF plug-in" ) &&
        output.contains( "Closed output PDF file" )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    pdfFile.delete()
}

/**
 * Test for valid output from PDF generation with -vverbose option
 */
def void testPdfInputFileOutputFileVeryVerbose( String javaRun )
{
    def String description = "Test: input-file output-file.pdf -vverbose"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.pdf -vverbose"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File pdfFile = new File( "${helloFileName}.pdf" )

    // make sure the output has -verbose logging and -vverbose token list
    if(( err == null || err.isEmpty() ) &&
        pdfFile.exists() &&
        output.contains( "Property file loaded with") &&
        output.contains( "Processing starting in PDF plug-in" ) &&
        output.contains( "Closed output PDF file" ) &&
        output.contains( "Line 0001: Text") &&
        output.contains( "hello, world" )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    pdfFile.delete()
}

/**
 * Test of valid listing generation.
 */
def void testListingSimpleInputOutput( String javaRun )
{
    def String description = "Test: input-file output-file.html -format listing"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.html -format listing"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File listingFile = new File( "${helloFileName}.html" )

    if(( err == null || err.isEmpty() ) && ( listingFile.exists() )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    listingFile.delete()
}

/**
 * Does the HTML listing work correctly with -vverbose? This validates both -verbose and -vverbose
 * output data.
 */
def void testListingInputOutputVeryVerbose( String javaRun )
{
    def String description = "Test: input-file output-file.html -format listing -vverbose"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.html -format listing -vverbose"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File listingFile = new File( "${helloFileName}.html" )

    if(( err == null || err.isEmpty() ) &&
        listingFile.exists() &&
        output.contains( "Property file loaded with" ) &&
        output.contains( "Loading output plug-in" ) &&
        output.contains( "Returned from output plug-in." ) &&
        output.contains( "Line 0001: Text") &&
        output.contains( "hello, world" )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    listingFile.delete()
}

/**
 * Test valid input and output files but specification of a non-existent config file (via -config)
 */
def void testPdfInputOutputInvalidConfigFile( String javaRun )
{
    def String description = "Test: input-file output-file.pdf -config invalid-file"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.pdf -config fungus.properties"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File pdfFile = new File( "${helloFileName}.pdf" )

    if(( err.contains( "Error: Configuration file could not be found:") )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    pdfFile.delete()
}

/**
 * Test response if an unsupported option is specified on the command line
 */
def void testPdfInputOutputWithExtraUnsupportedOption( String javaRun )
{
    def String description = "Test: input-file output-file.pdf -unsupported-option"

    // create a file containing one line of text.
    def String helloFileName = "hello.plat"
    def File helloFile = new File( helloFileName )
    PrintWriter pw = new PrintWriter( helloFile )
    pw.write( "hello, world" )
    pw.close()
    if ( ! helloFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create text file" )
        return
    }

    // run it and test for error message
    javaRun += " ${helloFileName} ${helloFileName}.pdf -tomato"
    def proc = javaRun.execute()
    def output = proc.in.text
    def err = proc.err.text

    def File pdfFile = new File( "${helloFileName}.pdf" )

   // if(( err.contains( "Error: Invalid option specified:") )) {
   if(( err.contains( "Unrecognized option:") )) {
        print( "Success in " )
    }
    else  {
        print( "***FAILURE in " )
    }
    println( description );

    helloFile.delete()
    pdfFile.delete()
}