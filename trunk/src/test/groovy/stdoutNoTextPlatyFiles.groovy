/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2009 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

 /* tests input files that generate no output PDFs. For example, files that are empty
  * and those that contain only commands (that is, no text)
  *
  * Note: this test relies on certain hard-coded parameters
  *	- the JAR file under test is Platypus.jar located in PLATYPUS_HOME\target\jar
  * - the input test files, commandonly.platy and emptyFile.platy, are located in:
  *			D:\Dev\platypus\testdata\
  *
  * To use these, then the environment will need to be duplicated, or these scripts changed.  
  */
  
if( ! args ) {
    println( "Error! Missing argument. " + "Syntax: thisfile.groovy  working-dir  report-Filename" )
    return
}

def workingDir = args[0]
def File resultsFile = new File( args[1] )

def platypusHome = System.getenv( "PLATYPUS_HOME" )
if( platypusHome == null ) {
    logFailure( "Error: PLATYPUS_HOME not defined" )
    return
}

def jarFile = platypusHome + "\\target\\jar\\Platypus.jar "
//println( "jarFile: " + jarFile )

arg1 = "D:\\Dev\\platypus\\testdata\\commandonly.platy"
arg2 = workingDir + "\\shouldBeEmpty.pdf"
def String javaCall = "java -jar " + jarFile + " " + arg1 + " " + arg2
//println( "javaCall: " + javaCall )

//=== run the tests ===//
Number count = 0
//println( "ResultsFile: " + resultsFile )
////def String resultMsg = noOutputWarning( ++count, javaCall )
    def String describe = "noOutputWarning"
	println( "javacall in test " + count + ": " + javaCall )
    def proc = javaCall.execute()
	def err = proc.err.text

    if( ! err.contains( "Warning: The input file did not generate any output." ))
        resultMsg = getFailureMsg( describe, "expected but did not see a no-output warning:\n" + err )
    else
        resultMsg = getSuccessMsg( describe )
resultsFile.append( resultMsg + "[]\n" )
//resultsFile.append( emptyFileWarning( ++count, javaCall ) + "[]\n" )
println( "Ran test # " + count )

resultsFile.append( verifyNoOutputFile( ++count, "noOutputWarning" ) + "[]\n" )
println( "Ran test # " + count )

arg1 = "D:\\Dev\\platypus\\testdata\\emptyfile.platy"
javaCall = "java -jar " + jarFile + " " + arg1 + " " + arg2
resultsFile.append( emptyFileWarning( ++count, javaCall ) + "[]\n" )
println( "Ran test #" + count )
resultsFile.append( verifyNoOutputFile( ++count, "emptyFileWarning" ) + "[]\n" )
println( "Ran test #" + count )

println( count + " test(s) run" )

return

//===== TESTS =====//

/**
 * Verify that the copyright string appears even when no command line option is specified
 */
def String noOutputWarning( Number testNbr, String javaCall )
{
    def String describe = "noOutputWarning"
	println( "javacall in test " + testNbr + ": " + javaCall )
    def proc = javaCall.execute()
	def err = proc.err.text

    if( ! err.contains( "Warning: The input file did not generate any output." ))
        return( getFailureMsg( describe, "expected but did not see a no-output warning:\n" + err ))
    else
        return( getSuccessMsg( describe ))
}

def String emptyFileWarning( int testNbr, String javaCall )
{
    def String describe = "emptyFileWarning"
    def proc = javaCall.execute()
	def err = proc.err.text

    if( ! err.contains( "Warning: The input file did not generate any output." ))
        return( getFailureMsg( describe, "expected but did not see a no-output warning:\n" + err ))
    else
        return( getSuccessMsg( describe ))
}

def String verifyNoOutputFile( int testNbr, testName )
{
	def String describe = "verifyNoOutputFile for: " + testName
	File outputFile = new File( arg2 )
    if( outputFile.exists() )
        return( getFailureMsg( describe, "Output file should not exist, but was created." ))
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
