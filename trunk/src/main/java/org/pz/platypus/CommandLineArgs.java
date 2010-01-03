/***
 *  Platypus: Page Layout and Typesetting Software (free at platypus.pz.org)
 *
 *  Platypus is (c) Copyright 2006-09 Pacific Data Works LLC. All Rights Reserved.
 *  Licensed under Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)
 */
package org.pz.platypus;

import org.pz.platypus.exceptions.HelpMessagePrinted;
import org.pz.platypus.exceptions.StopExecutionException;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses apache commons CLI to parse the command line.
 *
 * @author alb
 * @author atul - moved the command line parsing to apache commons CLI
 */
public class CommandLineArgs {

    @SuppressWarnings("unchecked")

    private Options options = new Options();
    private CommandLineParser parser = new GnuParser();
    private CommandLine line;

    /**
     * Configures the CLI parser with a list of valid Options.
     * And parses the command line args.
     *
     * @param args command-line args passed to main()
     */
    public CommandLineArgs(final String[] args) {
        initOptions();

        if (args == null || args.length == 0) {
            parseArguments(new String[]{"-help"});
        } else {
            parseArguments(args);
        }
    }

    private void parseArguments(String[] args) {
        try {
            String[] preprocessedArgs = CommandLineArgs.preProcessCommandLine(args);
            line = parser.parse(options, preprocessedArgs);
        } catch (ParseException e) {
            System.err.println(e.getLocalizedMessage());
            throw new StopExecutionException(e.getLocalizedMessage());
        }
    }

    private void initOptions() {
        Option[] optsArr = getSupportedOptions();

        for (Option opt : optsArr) {
            options.addOption(opt);
        }
    }

    /**
     * All valid supported options are here.
     * @return a list of such options.
     */
    private Option[] getSupportedOptions() {
        Option inputFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("input file")
                .create("inputFile");
        Option outputFile = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("output file")
                .create("outputFile");
        Option configFile = OptionBuilder.withArgName("configfile")
                .hasArg()
                .withDescription("config file")
                .create("config");
        Option format = OptionBuilder.withArgName("formatname")
                .hasArg()
                .withDescription("output format")
                .create("format");

        Option verbose = new Option("verbose", "verbose help");
        Option vverbose = new Option("vverbose", "very verbose help");
        Option fontlist = new Option("fontlist", "list of fonts");
        Option help = new Option("help", "print this message");

        return new Option[]{inputFile, outputFile, configFile, format, verbose, vverbose, fontlist, help};
    }


    /**
     * Creates a single string from all the args specified by the user on the command line
     *
     * @param args the user-specified args
     * @return the command line as one string. If there were no args, a single space is returned.
     */
    public String createCommandLine(final String[] args) {

        StringBuffer sb = new StringBuffer(args.length * 15);

        if (args.length == 0) {
            return (" ");
        }

        for (String arg : args) {
            sb.append(arg);
            sb.append(' ');
        }

        return (sb.toString().trim());
    }

    /**
     * Determines whether an item was specified on the command-line. And if so,
     * what any argument for it is.
     *
     * @param argToFind argument to lookup
     * @return null if the item was not specified; "" if the item was specified but had no argument,
     *         and the argument if item was specified and has an argument.
     */
    public String lookup(final String argToFind) {
        if (argToFind == null) {
            return (null);
        }
        if (shouldGenerateOutputOption(argToFind)) {
            return generateOutputFile();
        }
        if (!line.hasOption(argToFind))
            return null;
        String opt = line.getOptionValue(argToFind);
        if (opt == null)
            return "true";
        return opt;
    }

    private String generateOutputFile() {
        String inputFile = line.getOptionValue("inputFile");
        String ret = getNameMinusExtension(inputFile);
        return appendFormatExtension(ret);
    }

    private String appendFormatExtension(String ret) {
        final String formatExt = getFormatExtension();
        return ret + "." + formatExt;
    }

    private String getFormatExtension() {
        String formatExtension = "pdf";
        if (line.hasOption("format")) {
            formatExtension = line.getOptionValue("format");
        }
        return formatExtension;
    }

    private String getNameMinusExtension(String str) {
        String ret = str;
        StringBuilder fileBuilder = new StringBuilder(str);
        int lastDotAt = fileBuilder.lastIndexOf(".");
        if (lastDotAt != -1) {
            ret = ret.substring(0, lastDotAt);
        }
        return ret;
    }

    private boolean shouldGenerateOutputOption(final String argStr) {
        if (argStr.equals("outputFile")) {
            if (!line.hasOption("outputFile")) {
                if (line.hasOption("inputFile")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Processes some command-line options
     *
     * @param gdd GDD that will hold the state of some switches (such as verbosity)
     * @throws HelpMessagePrinted if no command-line args specified
     */   //curr: improve method name
    public void process(GDD gdd) throws HelpMessagePrinted {
        showUsageIfZeroArgs(gdd);

        // Note: the -config option is entirely handled in Platypus.processConfigFile(), not here.

        processHelpOption(gdd);

        processVerboseOption(gdd);

        processVeryVerboseOption(gdd);

        processFontListOption(gdd);
    }

    private void processFontListOption(GDD gdd) throws HelpMessagePrinted {
        if (line.hasOption("fontlist")) {
            TypefaceMap tfm = new TypefaceMap(gdd);
            tfm.loadFamilies();
            tfm.writeMapToFile(gdd.getHomeDirectory() + "config/fontlist.txt");
            throw new HelpMessagePrinted("OK"); //todo: fix this way of escaping processing.
        }
    }

    private void processVeryVerboseOption(GDD gdd) {
        if (line.hasOption("vverbose")) {
            gdd.setClVVerbose(true);
        }
    }

    private void processVerboseOption(GDD gdd) {
        if (line.hasOption("verbose")) {
            gdd.setClVerbose(true);
        }
    }

    private void processHelpOption(GDD gdd) throws HelpMessagePrinted {
        if (line.hasOption("help")) {
            Platypus.showUsage(gdd);
            // this exception exits Platypus without further processing and w/out
            // an error message. Technically, this is not an exception, but it's
            // the cleanest way of closing down from here.
            throw new HelpMessagePrinted("OK");
        }
    }

    private void showUsageIfZeroArgs(GDD gdd) {
        if (line.getOptions().length == 0) {
            Platypus.showUsage(gdd);
            System.err.println(gdd.getLit("PLEASE_RERUN_WITH_FILENAMES"));
            throw new UnsupportedOperationException("ERR"); //curr: delete ERR
        }  //curr: combine with graf below, using same exit exception.
    }

    /**
     * Injects the -inputFile and -outputFile options before correct barewords
     * The algorithm first checks if we have encountered an option with zero or one argument.
     * If so, it copies the elements to the result array.
     * Else, it injects -inputFile before the first bareword
     * and -outputFile after the second bareword.
     *
     * @param args
     * @return correctly injects -inputFile and -outputFile before the barewords...
     */

    public static String[] preProcessCommandLine(final String[] args) {
        final List<String> newArgs = new ArrayList<String>();
        boolean inOption = false;
        boolean inputSeen = false;

        for (String arg : args) {
            if (inOption == false) {
                if (isArgAnOption(arg)) {
                    if (doesOptionHaveArg(arg)) {
                        inOption = true;
                    }
                } else if (inputSeen == false) { // we have encountered a bareword
                    newArgs.add("-inputFile"); // first bareword is taken as input file
                    inputSeen = true;
                } else if (inputSeen == true) {
                    newArgs.add("-outputFile"); // next bareword is output file
                    inputSeen = false;
                }
            } else if (inOption == true) {
                inOption = false; // this bareword is an option argument
            }
            newArgs.add(arg);
        }
        return newArgs.toArray(new String[0]); // java collections idiom for converting to an array :-)
    }

    private static boolean doesOptionHaveArg(String arg) {
        return arg.equals("-config") || arg.equals("-format");

    }

    private static boolean isArgAnOption(String arg) {
        if (!arg.isEmpty()) {
            if (arg.charAt(0) == '-') {
                return true;
            }
        }
        return false;
    }
}
