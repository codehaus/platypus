/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 *  Runs functional tests on the HTML plugin.
 *
 *  @author alb
 */

 //-------------------- start of main line ------------------------//

// the JAR to test should be passed to this script as args[0]
if( ! args ) {
    println( "Error! Missing arg.\n" +
             "Usage: groovy htmlTest.groovy JAR-file-to-test-with-path.\n" +
             "Please rerun specifying JAR to test (with full path)")
    return;
}

// create the command line for running Platypus
def String jarUnderTest = args[0]
def String javaRun = "java -jar " + jarUnderTest

// run the test(s)
testItalics( javaRun ) 

 
return

//----------------------- end of main line -----------------------//

/**
 * Test whether simple italics work correctly in the HTML plugin
 */

def void testItalics( String javaRun )
{
  def String description =
      "Test: Simple italics."

    def String testFileName = "testItalics.plat"
    // create a Platypus file containing italicized text.
    testFile = createInputFile(testFileName, "Atul [+i]rocks[-i]!\n")
    // run Platypus and capture for error message as well as a generated HTML file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.html "
    def proc = commandLine.execute()
    def err = proc.err.text

    File htmlFile = openFile("${testFileName}.html");

    if (theRunWasAnError(err)) {
      cleanUp(testFile, htmlFile)
      return
    }

	// did Platypus report any errors?
    if( err != null && err != "" ){
        println( "***FAILURE in " + description + "Platypus reported an error: " +  err )
		testFile.delete()
		htmlFile.delete()
    }

  // read contents of output file into a string	  
    String html = htmlFile.getText()


	// test for the expected output. If it doesn't match, fail and show the invalid HTML
	if( html.contains( "Atul <I>rocks</I>!" )) {
		println( "Success in " + description )
	}
	else {
		println( "***FAILURE in " + description + ": Generated HTML not as expected." )
		println( "        -> " + html )
	}
	
	// delete the test Platypus file and the generated HTML file.
    testFile.delete()
    htmlFile.delete()
}

def void cleanUp(File file1, File file2) {
  file1.delete()
  file2.delete()
}

def boolean theRunWasAnError(String err) {

}

def File openFile(String fileName)
{
  def File file = new File( fileName )
  if( ! file.exists() ) {
      throw new Exception( "***FAILURE in " + description + " No such file " + "${fileName}" )
  }
  file  
}

// creates a platypus input file with the specified content
def File createInputFile(String fileName, String fileContents)
{
  def File testFile = new File( fileName )
  PrintWriter pw = new PrintWriter( testFile )
  pw.write( fileContents );
  pw.close()

  // Make sure we have successfully created the input file
  if ( ! testFile.exists() ) {
    throw new Exception("***FAILURE in ${description}. Could not create " + "${fileName}" + " file")
  }
  return testFile
}

def void testUrlWithPrefix( String javaRun )
{
  
}
