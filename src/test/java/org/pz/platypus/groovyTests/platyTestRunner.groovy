/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 *  Runs integration tests on Platypus
 */

def ant = new AntBuilder()

def version     = "0.6"
def copyright   = "Integration Test Framework v. ${version} " +
                    "(c) Copyright 2009 Pacific Data Works LLC. All rights reserved."

def dataDir     = "d:/dev/platypus/testdata/"
def rootDir     = "d:/dev/platypus/platypus/"
def rptDir      = "d:/dev/platypus/report/integrationTests/"
def runDir      = rootDir + "target/jar/"

def rptName     = null;


    ant.echo( copyright )
    ant.mkdir( dir: new File( rptDir ))
    rptName = setupTestResultsFile( rptDir )





































/**
 * Sets up the name for the test resultsfile (workdir/TestResults-yyyymmdd-hhmm.platy) and
 * on the rare chance the file already exists, warns that it will be overwritten.
 */
def String setupTestResultsFile( String resultsDir  )
{
	String resultsFileName
	today = new Date()

    resultsFileName = resultsDir +
        String.format( '%tY', today ) + String.format( '%tm', today ) +
        String.format( '%td', today ) + "-" +
        String.format( '%tH', today) + String.format( '%tM', today ) +
        ".platy"

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
 * Write content to the test results file
 */
def output( String filename, String content )
{
    new File( filename ).append( content )
}