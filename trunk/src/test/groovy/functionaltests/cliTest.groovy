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

return

/**
 * Are we generating a valid copyright strings as part of the output to the console?
 */
def void testCopyrightString( String javaRun )
{
    def String description = "Test: valid copyright message"
    def proc = javaRun.execute()
    def output = proc.in.text

    if( ! output.contains( "(c) Copyright 2006-0" ) ||
        ! output.contains( "Pacific Data Works LLC. All Rights Reserved." ))
        print( "FAILURE in " )
    else
        print( "Success in ")
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
        print( "FAILURE in ")
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
        print( "FAILURE in ")
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
        print( "FAILURE in ")
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
        println( "FAILURE in ${description}. Could not Create empty file" )
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
        print( "FAILURE in ")
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
        println( "FAILURE in ${description}. Could not Create empty file" )
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
        print( "FAILURE in ")
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
        print( "FAILURE in ")
    }
    println( description );
}


