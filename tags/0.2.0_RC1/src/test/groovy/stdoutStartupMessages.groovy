/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2009 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

 // test messages to stdout/stderr when Platypus does no document conversion, specifically:
 // the -help command
 // command lines missing arguments

if( ! args ) {
    println( "Error! Missing argument. " + "Syntax: thisfile.groovy  working-dir  report-Filename" )
    return
}

// println( "in stdoutStartupMessages" )

def workingDir = args[0]
def File resultsFile = new File( args[1] )

def platypusHome = System.getenv( "PLATYPUS_HOME" )
if( platypusHome == null ) {
    logFailure( "Error: PLATYPUS_HOME not defined" )
    return
}

def jarFile = platypusHome + "\\target\\jar\\Platypus.jar"

def String javaCall = "java -jar " + jarFile
def arg1, arg2, arg3, arg4, arg5

//=== run the tests ===/
int count = 1

resultsFile.append( copyrightString( count++, javaCall ) + "[]\n" )
resultsFile.append( errorMsgWhenNoArgSpecified( count++, javaCall ) + "[]\n" )
resultsFile.append( helpCommandLineOption( count++, javaCall ) + "[]\n" )

println( --count + " test(s) run" )

return

//===== TESTS =====//

/**
 * Verify that the copyright string appears even when no command line option is specified
 */
def String copyrightString( int testNbr, String javaCall )
{
    def String describe = "copyrightString"
    def proc = javaCall.execute()
    def output = proc.in.text

    if( ! output.contains( "(c) Copyright 2006-0" ) ||
        ! output.contains( "Pacific Data Works LLC. All Rights Reserved." ))
        return( getFailureMsg( describe, "Missing or incomplete copyright string" ))
    else
        return( getSuccessMsg( describe ))
}

/**
 *  Verify that the no-arg error messages provides:
 *  1) a description of Platypus
 *  2) instructions for correcting the problem
 *  3) usage info
 */
def String errorMsgWhenNoArgSpecified( int testNbr, String javaCall )
{
    def String describe = "errorMsgWhenNoArgSpecified"
    def proc = javaCall.execute()
    def output = proc.in.text
    def err = proc.err.text

    if( ! output.contains( "Platypus converts a Platypus document to PDF" ))
        return( getFailureMsg( describe,
                               "No-arg error message is missing the description of Platypus" ))
    else
    if( ! output.contains( "Usage: ") || ! output.contains( "Options are:" ))
        return( getFailureMsg( describe,
                               "No-arg error message is missing usage info" ))
    else
    if( ! err.contains( "Please rerun specifying valid input and output files. Exiting..." ))
        return( getFailureMsg( describe, proc.err.txt + "\n" +
                               "No-arg error message is missing directions to solve" ))
    else
        return( getSuccessMsg( describe ))
}

/**
 *  Verify that the -help command-line option (which simply prints a usage message) provides:
 *  1) copyright info
 *  2) the description of what Platypus is
 *  and verify that it does not contain an error message
 */

def String helpCommandLineOption( int testNbr, String javaCall )
{
    def String describe = "helpCommandLineOption"
    javaCall += " -help"
    def proc = javaCall.execute()
    def output = proc.in.text

    if( ! output.contains( "Platypus converts a Platypus document to PDF" ))
        return( getFailureMsg( describe,
                               "-help option output is missing the description of Platypus" ))
    else
    if( ! output.contains( "(c) Copyright 2006-0" ))
        return( getFailureMsg( describe,
                               "-help option output is missing copyright notice" ))
    else
    if( output.contains( "Please rerun specifying" ))
        return( getFailureMsg( describe,
                               "-help option incorrectly contains error message" ))
    else
        return( getSuccessMsg( describe ))
}


//================================================================================================//

def String getFailureMsg( String testName, String errMsg )
{
    return( "Failure: " + testName + ": " + errMsg )
}

def String getSuccessMsg( String testName )
{
    return( "Passed:  " + testName )
}
