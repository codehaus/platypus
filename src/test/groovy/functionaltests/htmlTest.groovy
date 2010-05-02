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

def String description = "Test: Simple italics."

def void testItalics( String javaRun ) 
{
  setDescription( "Test: Simple italics." )
  test( javaRun, "Atul [+i]rocks[-i]!\n", "Atul <I>rocks</I>!" )
}

def void test( String javaRun, inputStr, expectedStr )
{
    def String testFileName = "testItalics.plat"
    // create a Platypus file containing italicized text.
    testFile = createInputFile(testFileName, inputStr)
    // run Platypus and capture for error message as well as a generated HTML file
    def err = runPlatypus(javaRun, testFileName)

    File htmlFile = openFile("${testFileName}.html");

    if (theRunWasAnError(err)) {
      cleanUp(testFile, htmlFile)
      return
    }

  // read contents of output file into a string	  
    String html = htmlFile.getText()
  
  testExpectedContent(htmlFile, expectedStr) 

	// delete the test Platypus file and the generated HTML file.
    cleanUp(testFile, htmlFile)
}

def void setDescription(String desc) {
  description = desc
}

def void testExpectedContent(File file, String expectedStr) {
  String html = file.getText()
  // test for the expected output. If it doesn't match, fail and show the invalid HTML
  if( html.contains( expectedStr )) {
      println( "Success in " + description )
  }
  else {
      println( "***FAILURE in " + description + ": Generated HTML not as expected." )
      println( "        -> " + html )
  }	
}

def void runPlatypus(String javaRun, String inputFile) {
  String commandLine =  javaRun + " ${inputFile} ${inputFile}.html "
  def proc = commandLine.execute()
  def err = proc.err.text
  err  
}

def void cleanUp(File file1, File file2) {
  file1.delete()
  file2.delete()
}

def boolean theRunWasAnError(String err) {
  if( err != null && err != "" ) {
      println( "***FAILURE in run - Platypus reported an error: " +  err )
  }
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
