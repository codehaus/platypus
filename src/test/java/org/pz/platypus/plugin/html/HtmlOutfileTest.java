package org.pz.platypus.plugin.html;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * HtmlOutfile Tester.
 *
 * @author <Authors name>
 * @since <pre>08/23/2010</pre>
 * @version 1.0
 */
public class HtmlOutfileTest extends TestCase {
    public HtmlOutfileTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetInParagraph() throws Exception {
        //TODO: Test goes here...
    }

    public void testSetHtmlData() throws Exception {
        //TODO: Test goes here...
    }

    public void testSetMarginTop() throws Exception {
        //TODO: Test goes here...
    }

    public static Test suite() {
        return new TestSuite(HtmlOutfileTest.class);
    }

    public void testEmitTextQuotesSpecialHtmlChars() throws Exception {
        HtmlOutfile htmlOutfile = new HtmlOutfile();
        // String emittedText = htmlOutfile.emitText("<");

    }
    
}
