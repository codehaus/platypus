/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2010 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.commands.UrlCommand;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.html.HtmlOutfile;

/**
 * Prints a URL without any cover text.
 *
 * @author ask
 */
public class HtmlUrl extends UrlCommand
{
    @Override
    protected void outputUrl(OutputContextable context, String url )
    {
        //TODO: Atul, you need to check that the URL does not already begin with http://
        //TODO: Even with that I'm not sure adding http:// is right. Let's discuss. ALB
        HtmlData htmlData = (HtmlData) context;
        HtmlOutfile outfile = htmlData.getOutfile();
        outfile.emitText("<a href=" + "\"" + "http://" + url + "\"" + ">" + url + "</a>" );
    }

}