/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 *  Runs functional tests on Platypus CLI (command-line interface)
 *  @author alb
 */

def ant = new AntBuilder();

// the JAR to test, should be passed to this script as args[0]
if( ! args ) {
    println( "Error! Missing arg. Usage: " +
             "groovy clitest.groovy JAR-file-to-test-with-path. " +
             "Please rerun specifying jar to test")
    return;
}
def String jarUnderTest = args[0]
def String javaRun = "java -jar " + jarUnderTest

copyrightString( javaRun )
return

def void copyrightString( String javaRun )
{
    def String description = "Valid copyright message"
    def proc = javaRun.execute()
    def output = proc.in.text

    if( ! output.contains( "(c) Copyright 2006-0" ) ||
        ! output.contains( "Pacific Data Works LLC. All Rights Reserved." ))
        print( "FAILURE in " )
    else
        print( "Success in ")
    println( description );

}



