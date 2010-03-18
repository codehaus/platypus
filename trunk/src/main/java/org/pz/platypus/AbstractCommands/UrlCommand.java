package org.pz.platypus.AbstractCommands;

import org.pz.platypus.GDD;
import org.pz.platypus.Token;
import org.pz.platypus.interfaces.OutputCommandable;
import org.pz.platypus.interfaces.OutputContextable;

/**
 * Created by IntelliJ IDEA.
 * User: atul
 * Date: Mar 18, 2010
 * Time: 8:10:11 AM
 */
public abstract class UrlCommand implements OutputCommandable {
    private final String root = "[url:"; // final is good ;-)

    protected abstract void outputUrl(final OutputContextable context, String url, String coverText);

    public void process(OutputContextable context, Token tok, int tokNum) {
        if( context == null || tok == null || tok.getParameter().getString() == null ) {
            throw new IllegalArgumentException();
        }

        String urlParameter = tok.getParameter().getString();
        String url;
        String coverText = null;

        // test for "|text: after URL, which would signal presence of cover text. If found,
        // set url and coverText to the respective strings in urlParameter; else, it's all
        // URL, so set url and leave coverText = null
        int textFlag = urlParameter.indexOf( "|text:" );
        if( textFlag > 0 ) {
            coverText = urlParameter.substring( textFlag + "|text:".length() );
            url = urlParameter.substring( 0, textFlag - 1);
        }
        else {
            url = urlParameter;
        }

        if( url == null ) {
            showErrorMsg( tok, context );
            return;
        }

        outputUrl(context, url, coverText);
    }

    public String getRoot()
    {
        return( root );
    }

    /**
     * Show error message, giving location in Platypus input file
     * @param tok contains the location data
     * @param context contains the location of the logger and literals file
     */    
    private void showErrorMsg(Token tok, OutputContextable context) {
        GDD gdd = context.getGdd();
        gdd.logWarning( gdd.getLit( "FILE#" ) + ": " + tok.getSource().getFileNumber() + " " +
                        gdd.getLit( "LINE#" ) + ": " + tok.getSource().getLineNumber() + " " +
                        gdd.getLit( "ERROR.URL_IS_NULL" ) + " " +
                        gdd.getLit( "IGNORED" ));

    }
    
}
