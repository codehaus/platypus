package org.pz.platypus.plugin.html;

import org.custommonkey.xmlunit.Diff;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.plugin.html.commands.HtmlBoldOff;
import org.pz.platypus.plugin.html.commands.HtmlItalicsOff;
import org.pz.platypus.plugin.html.commands.HtmlItalicsOn;

import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLNotEqual;

/**
 * Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 * <p/>
 * Platypus is (c) Copyright 2006-08 Pacific Data Works LLC. All Rights Reserved.
 * Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 * @author: atul
 * 
 */
public class HtmlDataTest {

    HtmlData htmlData = null;

    @Before
    public void init() {
        htmlData = new HtmlData(null, null); // TODO: pass instances instead of null
    }

    
 public void testForEquality() throws Exception {
        String myControlXML = "<msg><uuid>0x00435A8C</uuid></msg>";
        String myTestXML = "<msg><localId>2376</localId></msg>";
        assertXMLEqual("comparing test xml to control xml", myControlXML, myTestXML);

        assertXMLNotEqual("test xml not similar to control xml", myControlXML, myTestXML);
    }
    
    @Test
    public void testTwoTagsAndNoOverLap() {
        HtmlDocContext htmlDocContext = htmlData.getHtmlDocContext();
        
        htmlDocContext.push("[+b]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "");
        htmlDocContext.push("[+i]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "");
        htmlDocContext.push("[-i]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "[-i]");
        htmlDocContext.push("[-b]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "[-b]");
    }

    @Test
    public void testTwoTagsAndSingleTagOverLap() {
        HtmlDocContext htmlDocContext = htmlData.getHtmlDocContext();

        htmlDocContext.push("[+b]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "");
        htmlDocContext.push("[+i]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "");
        htmlDocContext.push("[-b]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "[-i][-b][+i]");
        htmlDocContext.push("[-i]");
        Assert.assertEquals(htmlDocContext.nestedTags(), "[-i]");        
    }

    @Test
    public void testThreeTagsAndSingleTagOverLap() {
        HtmlDocContext htmlDocContext = htmlData.getHtmlDocContext();
        
        htmlDocContext.push("[+b]");
        Assert.assertEquals("", htmlDocContext.nestedTags());
        htmlDocContext.push("[+i]");
        Assert.assertEquals("", htmlDocContext.nestedTags());
        htmlDocContext.push("[+u]");
        Assert.assertEquals("", htmlDocContext.nestedTags());
        htmlDocContext.push("[-b]");
        Assert.assertEquals("[-u][-i][-b][+i][+u]", htmlDocContext.nestedTags());
        htmlDocContext.push("[-u]");
        Assert.assertEquals("[-u]", htmlDocContext.nestedTags());
        htmlDocContext.push("[-i]");
        Assert.assertEquals("[-i]", htmlDocContext.nestedTags());
    }

    @Test
    public void testThreeTagsAndTwoTagsOverLap() {
        HtmlDocContext htmlDocContext = htmlData.getHtmlDocContext();

        htmlDocContext.push("[+b]");
        Assert.assertEquals("", htmlDocContext.nestedTags());
        htmlDocContext.push("[+i]");
        Assert.assertEquals("", htmlDocContext.nestedTags());
        htmlDocContext.push("[+u]");
        Assert.assertEquals("", htmlDocContext.nestedTags());
        htmlDocContext.push("[-b]");
        Assert.assertEquals("[-u][-i][-b][+i][+u]", htmlDocContext.nestedTags());
        htmlDocContext.push("[-i]");
        Assert.assertEquals("[-u][-i][+u]", htmlDocContext.nestedTags());
        htmlDocContext.push("[-u]");
        Assert.assertEquals("[-u]", htmlDocContext.nestedTags());
    }

    /** There is no way we can pass a commandTable for creating an HtmlData.
     *  So ignoring this test for now.
     */
    @Test
    @Ignore
    public void testNestedTagsCommands() {
        HtmlDocContext htmlDocContext = htmlData.getHtmlDocContext();
        
        htmlDocContext.push("[+b]");
        // Assert.assertEquals(htmlData.nestedTags(), "");
        Assert.assertEquals(0, htmlDocContext.getNestedTagCmds().size());

        htmlDocContext.push("[+i]");
        // Assert.assertEquals(htmlData.nestedTags(), "");
        Assert.assertEquals(0, htmlDocContext.getNestedTagCmds().size());
        
        htmlDocContext.push("[-b]");
        List<OutputCommandable> listOfTagCmds = htmlDocContext.getNestedTagCmds();
        // Assert.assertEquals(htmlData.nestedTags(), "[-i][-b][+i]");
        Assert.assertEquals(3, listOfTagCmds.size());        
        for(OutputCommandable cmd: listOfTagCmds) {
            Assert.assertTrue(cmd instanceof HtmlItalicsOff);
            Assert.assertTrue(cmd instanceof HtmlBoldOff);
            Assert.assertTrue(cmd instanceof HtmlItalicsOn);
        }
        htmlDocContext.push("[-i]");
        listOfTagCmds = htmlDocContext.getNestedTagCmds();
        Assert.assertEquals(1, listOfTagCmds.size()); 
        // Assert.assertEquals(htmlData.nestedTags(), "[-i]");
        for(OutputCommandable cmd: listOfTagCmds) {
            Assert.assertTrue(cmd instanceof HtmlItalicsOff);
        }        
    }

}
