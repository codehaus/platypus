/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 *
 *  Runs functional tests on the operation of the PDF plugin, which is everything in
 *  PDF generation other than validation of the generated documents
 *
 *  @author alb
 */


// the JAR to test should be passed to this script as args[0]
if( ! args ) {
    println( "Error! Missing arg. Usage: " +
             "groovy pdfOperation.groovy JAR-file-to-test-with-path. " +
             "Please rerun specifying jar to test")
    return;
}

def String jarUnderTest = args[0]
def String javaRun = "java -jar " + jarUnderTest

testPdfValidConfigThenInputAndOutputFiles( javaRun )
testValidErrMessageWhenColumnWidthTooBig( javaRun )
testNoNPEwhenSpecifyingIndents( javaRun )
testNoNPEwhenURLisFirstElementInFile( javaRun )
testNoNPEwhenSymbolisFirstElementInFile( javaRun )
testThereIsOutputOnCodeOnly( javaRun )

return
/**
 * PLATYPUS-39 in JIRA at Codehaus. Make sure that an input file of code listing only
 * does not generate an NPE and results in a PDF file
 */

def void testThereIsOutputOnCodeOnly( String javaRun )
{
  def String description =
      "Test: No NPE and a generated PDF for code listing-only file (consult PLATYPUS-39)"

      // create a file containing one line of text.
      def String testFileName = "testText.plat"

      def File testFile = new File( testFileName )
      PrintWriter pw = new PrintWriter( testFile )
      pw.write(
          "[code|lines:1,5]\n" +
          "int binarySearch( int a, int value )\n" +
          "{\n" +
          "int low = 0;\n" +
          "int high = a.length – 1;\n" +
          "[-code]\n"
      );
      pw.close()

      if ( ! testFile.exists() ) {
          println( "***FAILURE in ${description}. Could not create test file" )
          return
      }

    // run it and test for error message as well as a generated PDF file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.pdf "
    def proc = commandLine.execute()
    def err = proc.err.text

    def File pdfFile = new File( "${testFileName}.pdf" )

    if( ! pdfFile.exists()  &&
        ( err != null &&  err.contains( "The input file did not generate any output." ))
            //&& err == null)
    ) {
        println( "***FAILURE in " + description )
        println( err )
    }
    else {
        println( "Success in " + description )

    }

    testFile.delete()
    pdfFile.delete()
}

/**
 * PLATYPUS-21 in JIRA at Codehaus. Make sure that an input file that specifies
 * the indent before any text has been printed does not generate an NPE.
 */

def void testNoNPEwhenSpecifyingIndents( String javaRun )
{
  def String description =
      "Test: No NPE when specifying indent (consult PLATYPUS-21)"

      // create a file containing one line of text.
      def String testFileName = "testText.plat"
      def File testFile = new File( testFileName )
      PrintWriter pw = new PrintWriter( testFile )
      pw.write(
          "[pagesize:A4]" +
          "[paraindent:30pt]\n" +
          "[paraindentR:2.5cm]\n"
      );
      pw.close()
      if ( ! testFile.exists() ) {
          println( "***FAILURE in ${description}. Could not create test file" )
          return
      }

    // run it and test for error message as well as a generated PDF file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.pdf "
    def proc = commandLine.execute()
    def err = proc.err.text

    def File pdfFile = new File( "${testFileName}.pdf" )

    // tests coded oddly so that it's easy to comment out one of the test to do diagnosis
    if( err != null &&  err.contains( "The input file did not generate any output." ) &&
        ! pdfFile.exists()) {
        println( "Success in " + description )
    }
    else  {
        println( "***FAILURE in " + description )
        println( err )
    }

    testFile.delete()
    pdfFile.delete()
}

/**
 * See PLATYPUS-20 in JIRA at Codehaus. Tests that when [columnwidth: is too wide,
 * the error message returns the correct error message, using the user's original
 * units of size, and explaining why the size is invalid
 */
def void testValidErrMessageWhenColumnWidthTooBig( String javaRun )
{
  def String description =
      "Test: Useful error msg when [columnwidth: is too wide (consult PLATYPUS-20)"

      // create a file containing one line of text.
      def String testFileName = "testText.ptp"
      def File testFile = new File( testFileName )
      PrintWriter pw = new PrintWriter( testFile )
      pw.write(
          "[columns:3][columnwidth:4in][*_version]\n" );
      pw.close()
      if ( ! testFile.exists() ) {
          println( "***FAILURE in ${description}. Could not create test file" )
          return
      }

    // run it and test for error message as well as a generated PDF file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.pdf "
    def proc = commandLine.execute()
    def err = proc.err.text

    def File pdfFile = new File( "${testFileName}.pdf" )

    // tests coded oddly so that it's easy to comment out one of the test to do diagnosis
    if(
        ( err.contains( "column size ignored" ) && err.contains( "will not fit" ) &&
                err.contains( "File #" ))
        &&  pdfFile.exists()
                            ) {
        println( "Success in " + description )
    }
    else  {
        println( "***FAILURE in " + description )
        println( err )
    }

    testFile.delete()
    pdfFile.delete()

}
/**
 * See PLATYPUS-19 in JIRA at Codehaus. Tests that [columns: works as the first command in
 * a Platypus file.
 */
def void testPdfValidConfigThenInputAndOutputFiles( String javaRun )
{
    def String description =
    "Test: input begins with [+footer] [columns:3] (consult PLATYPUS-19)"

    // create a file containing one line of text.
    def String testFileName = "testText.ptp"
    def File testFile = new File( testFileName )
    PrintWriter pw = new PrintWriter( testFile )
    pw.write(
        "[+footer]\n" +
        "[columns:3]\n" +
        "gave their lives that that nation might live. It is altogether fitting and proper that we\n" +
        "should do this today on this battlefield.\n" +
        "But, in a larger sense, we cannot dedicate we cannot consecrate we cannot hallow\n" +
        "this ground. The brave men, living and dead, who struggled here []\n" +
        "gave their lives that that nation might live. It is altogether fitting and proper that we" );
    pw.close()
    if ( ! testFile.exists() ) {
        println( "***FAILURE in ${description}. Could not create test file" )
        return
    }

    // point to the config file in PLATYPUS_HOME
    def envVars = System.getenv()
    def platypusHome = envVars['PLATYPUS_HOME']
    def String configFileName = platypusHome + "/config/Config.properties"

    // run it and test for error message as well as a generated PDF file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.pdf "
    def proc = commandLine.execute()
    def err = proc.err.text

    def File pdfFile = new File( "${testFileName}.pdf" )

    // tests coded oddly so that it's easy to comment out one of the test to do diagnosis
    if(
        ( err.equals( null ) || err.isEmpty() )
        &&  pdfFile.exists()
                            ) {
        println( "Success in " + description )
    }
    else  {
        println( "***FAILURE in " + description )
        println( err )
    }

    testFile.delete()
    pdfFile.delete()
}

/**
 * See PLATYPUS-26 in JIRA at Codehaus. Tests that when a URL is the first element
 * of a in input file, the output file is opened correctly, without causing and NPE.
 */
def void testNoNPEwhenURLisFirstElementInFile( String javaRun )
{
  def String description =
      "Test: No exceptions if URL is first element in an input file (consult PLATYPUS-26)"

      // create a file containing one line of text.
      def String testFileName = "testURL.ptp"
      def File testFile = new File( testFileName )
      PrintWriter pw = new PrintWriter( testFile )
      pw.write(
          "[url:http://www.cnn.com]\n" );
      pw.close()
      if ( ! testFile.exists() ) {
          println( "***FAILURE in ${description}. Could not create test file" )
          return
      }

    // run it and test for error message as well as a generated PDF file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.pdf "
    def proc = commandLine.execute()
    def err = proc.err.text

    def File pdfFile = new File( "${testFileName}.pdf" )

    // tests coded oddly so that it's easy to comment out one of the test to do diagnosis
    if( pdfFile.exists()
                            ) {
        println( "Success in " + description )
    }
    else  {
        println( "***FAILURE in " + description )
        println( err )
    }

    testFile.delete()
    pdfFile.delete()
}

/**
 * See PLATYPUS-26 in JIRA at Codehaus. Tests that when a URL is the first element
 * of a in input file, the output file is opened correctly, without causing and NPE.
 */
def void testNoNPEwhenSymbolisFirstElementInFile( String javaRun )
{
  def String description =
      "Test: No exceptions if symbol is first element in an input file"

      // create a file containing one line of text.
      def String testFileName = "testSymbol.ptp"
      def File testFile = new File( testFileName )
      PrintWriter pw = new PrintWriter( testFile )
      pw.write(
          "[trademark][copyright]\n" );
      pw.close()
      if ( ! testFile.exists() ) {
          println( "***FAILURE in ${description}. Could not create test file" )
          return
      }

    // run it and test for error message as well as a generated PDF file
    String commandLine =  javaRun + " ${testFileName} ${testFileName}.pdf "
    def proc = commandLine.execute()
    def err = proc.err.text

    def File pdfFile = new File( "${testFileName}.pdf" )

    // tests coded oddly so that it's easy to comment out one of the test to do diagnosis
    if( pdfFile.exists()
                            ) {
        println( "Success in " + description )
    }
    else  {
        println( "***FAILURE in " + description )
        println( err )
    }

    testFile.delete()
 //   pdfFile.delete()
}

