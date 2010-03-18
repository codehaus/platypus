/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus.plugin.html.commands;

import org.pz.platypus.AbstractCommands.UrlCommand;
import org.pz.platypus.interfaces.OutputContextable;
import org.pz.platypus.plugin.html.HtmlData;
import org.pz.platypus.plugin.html.HtmlOutfile;

/**
 * Prints a URL without any cover text.
 *
 * @author alb
 */
public class HtmlUrl extends UrlCommand
{
    @Override
    protected void outputUrl(OutputContextable context, String url, String coverText) {
        HtmlData htmlData = (HtmlData) context;
        HtmlOutfile outfile = htmlData.getOutfile();
        outfile.emitText("<a href=" + "\"" + "http://" + url + "\"" + ">");
    }

}