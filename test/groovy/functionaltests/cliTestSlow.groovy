/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 *  Runs functional tests on the Platypus CLI (command-line interface)
 *  The tests in this script are segregated from those in cliTest.groovy
 *  because they run slowly.
 *
 *  Script assumes that PLATYPUS_HOME has been set correctly.
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


testFontlistOutputToScreen( jarUnderTest )


return


/**
 * See PLATYPUS-10 and PLATYPUS-11 at Codehaus.
 * Test that running -fontlist does not throw spurious announcements about .ttc files out to stdout.
 * Requires special command-line to run test because of large memory requirements for -fontlist.
 */
def void testFontlistOutputToScreen( String jarUnderTest )
{
    def String description = "Test: -fontlist runs quietly (Consult PLATYPUS-10 and -11)"

    // run it and test for error message
    javaCommandLine = "java -Xmx256M -jar " + jarUnderTest + " -fontlist"
    def proc = javaCommandLine.execute()
    def output = proc.in.text
    def err = proc.err.text

   if( err.isEmpty() && ! output.contains( "ttc" )) {
        print( "Success in " + description )
    }
    else  {
        print( "***FAILURE in " + description + "\n" )
        print err
    }
}
