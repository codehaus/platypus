//
//  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
//
//  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
//  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
//
 
// The test framework
//
// Receives two arguments:
// -the name of a file containing the test suite scripts to run (one filename per line)
// -the work directory where output files go and where the test report goes.
//
// @author alb
//

def version = "0.0.5"
printCopyright( version )

if( ! args || args.length < 2 ) {
    println(
        "Error! Missing argument. Syntax: framework.groovy test-suite-file  working-directory [printfile-name]" )
    return
}

String suiteFile = args[0]
if( ! new File( suiteFile ).exists() ) {
    println( "Error! Missing test-suite-file: ${args[0]}")
}

def String workingDirName = args[1]
setupWorkingDir( workingDirName )
def String resultsFileName = setupTestResultsFile( workingDirName )

//
// main test processing
//

List scripts = new File( suiteFile ).readLines()
scripts.each { scriptName ->
                if( ! scriptName.startsWith( '#' )) {
                    println "Running test script: " + scriptName
                    output( resultsFileName, "\n[+b]${scriptName}[-b][]\n")
                    runScript( scriptName, workingDirName, resultsFileName )
                //  println "Finished running script: " + scriptName
                }
}

//TODO: count successes and failures and report the total to stdout and the bottom of the report.



//=========================================================================================//

def runScript( String scriptFilename, String workingDir, String resultsFilename )
{
    def File script = new File( scriptFilename )

    if( ! script.exists() ) {
        println( "Cannot find script: " + scriptFilename )
        return
    }

    // a list of args to pass to script
    def List args = [ workingDir, resultsFilename ]
    new GroovyShell().run( script, (String[]) args )
}

def printCopyright( String version )
{
    println( "Functional Test Framework v. ${version} " +
             "(c) 2009 Pacific Data Works LLC. All rights reserved." )
}

/**
 * Write content to the test results file
 */
def output( String filename, String content )
{
    new File( filename ).append( content )
}

/**
 * Sets up the name for the test resultsfile (workdir/TestResults-yyyymmdd-hhmm.platy) and
 * on the rare chance the file already exists, warns that it will be overwritten.
 */
def String setupTestResultsFile( String workDir  )
{
	String resultsFileName
	today = new Date()
	
	if( args.length > 2 ) {
		resultsFileName = workDir + "\\" + args[2]
	}
	else {
		resultsFileName = workDir + "\\TestResults" + "-" +
			String.format( '%tY', today ) + String.format( '%tm', today ) +
			String.format( '%td', today ) + "-" +
			String.format( '%tH', today) + String.format( '%tM', today ) +
			".platy"
	}
    println "Test results file name: " + resultsFileName

    File resultsFile = new File( resultsFileName )
    if( resultsFile.exists() ) {
        println( "Warning! Test results file will be overwritten: ${resultsFileName}" )
    }

    def String heading =
            "[align:center][fsize:20pt][ff:Arial][+b]Platypus Functional Tests Results[-b][]\n" +
            "[fsize:11pt]" +
            String.format( '%tF', today ) + " --- " +
            String.format( '%tT', today ) + "[paraskip:0]" + "\n\n" +
            "[ff:COURIER]" + "[fsize:9pt][align:left][]\n\n"
    output( resultsFileName, heading );

    return resultsFileName
}

/**
 * Create the working directory if it does not already exist.
 */
def setupWorkingDir( String dirName )
{
    def ant = new AntBuilder()
    ant.mkdir( dir:dirName )
}





