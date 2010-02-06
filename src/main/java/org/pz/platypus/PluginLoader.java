/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-10 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */

package org.pz.platypus;

import org.pz.platypus.exceptions.InvalidConfigFileException;
import org.pz.platypus.utilities.TextTransforms;

import java.net.*;
import java.lang.reflect.*;

/**
 * Loads a plug-in
 *
 * @author alb
 */
public class PluginLoader
{
    /** the actual name of the plugin JAR file on the disk */
    private final String pluginLocation;

    private GDD gdd;

    public PluginLoader( final String pluginJar, final GDD Gdd  )
    {
        pluginLocation = pluginJar;
        gdd = Gdd;
    }

    /**
     * Load the Start class from the plug-in JAR
     * @param clArgs command line args
     */
    public void load( final org.pz.platypus.CommandLineArgs clArgs )
    {
        URL pluginUrl = createPluginUrl();
        if ( pluginUrl == null ) {
            return;   // TODO: create an error message
        }

        URL[] urls = { pluginUrl };
        URLClassLoader pluginLoader = new URLClassLoader( urls );
        Thread.currentThread().setContextClassLoader( pluginLoader );

        try {
            String className;

                //curr: figure out how to not hard code the class path
//
//            Class pluginStart = pluginLoader.loadClass( "org.pz.platypus.plugin." +
//                                    gdd.getOutputPluginPrefix() + ".Start" );
//
            //------ due to bug in JDK 1.6, must use Class.forName(), rather than code above.
            // see:  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6434149

            if( pluginLocation.endsWith( "Start.class" )) {
            // Note: this functionality was never completed. Code for it left here in the event
            // it's needed at a later point. ALB 2010-02-06
                className = TextTransforms.truncate( pluginLocation, ".class".length() );
            }
            else {
                className = "org.pz.platypus.plugin." +
                               gdd.getOutputPluginPrefix() + ".Start";
            }
            
            Class pluginStart = Class.forName( className, false, pluginLoader );

            Object plugin = pluginStart.newInstance();

            Class[] classParams = { GDD.class, CommandLineArgs.class };
            Method method1;

            try {
                method1 = pluginStart.getMethod( "process", classParams  );
            }
            catch( NoSuchMethodException nsme ) {
                gdd.logSevere( gdd.getLit( "ERROR.INVALID_PLUGIN_NO_PROCESS_METHOD" ));
                return;
            }

            try {
                method1.invoke( plugin, gdd, clArgs );
            }
            catch( InvalidConfigFileException icfe) {
                return; //error message has already been displayed
            }
            catch( InvocationTargetException ite ) {
                System.err.println( "Invocation target exception" + ite );
                ite.printStackTrace();
            }
        }
        catch ( ClassNotFoundException cnf ) {
            System.err.println( "class not found " + cnf );
        }
        catch ( InstantiationException ie ) {
            System.err.println( ie );
        }
        catch ( IllegalAccessException ie ) {
            System.err.println( ie );
        }        
    }

    /**
     * Converts a plugin's JAR file name+address into a URL suitable for class loading
     * @return a valid URL, if all went well; else null;
     */
    public URL createPluginUrl()
    {
        URL urlForPlugin = null;
        try {
            urlForPlugin = new URL( "file:" + pluginLocation );
//            gdd.logFinest( gdd.getLit( "PLUGIN_URL" ) + " " + urlForPlugin.toString() );
        }
        catch ( MalformedURLException e){
            gdd.logSevere( gdd.getLit( "ERROR.INVALID_PLUGIN_URL" ) + ": " +
                    pluginLocation + "\n" + e );
            urlForPlugin = null;
        }
        finally {
            return( urlForPlugin );
        }
    }
}
