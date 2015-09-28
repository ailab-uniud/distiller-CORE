/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.launchers;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The class is responsible for the usage of the Distiller via command-line.
 * It's able to:
 * <ul>
 * <li>Select a pre-defined pipeline and start it, or</li>
 * <li>Load a custom pipeline</li>
 * <li>Select an input document or folder</li>
 * <li>Process the pipeline over the document or the documents contained in the
 * folder</li>
 * <li>Print the result of the computation.</li>
 * </ul>
 *
 *
 * @author Marco Basaldella
 */
public class Launcher {
    
    /**
     * The file or directory to analyze.
     */
    private static File inputPath;
    
    /**
     * The output directory.
     */
    private static File outputPath;
    
    /**
     * The command-line options.
     */
    private static Options options = new Options();
    
    /**
     * Determines whether the annotations over sentences should be printed or not.
     */
    private static boolean printSentences = false;
    
    /**
     * Determines whether the annotations over grams should be printed or not.
     */
    private static boolean printGrams = false;

    /**
     * Starts the Distiller using the specified configuration, analyzing the
     * specified file, writing the output in the specified folder.
     * 
     * @param args the command-line parameters.
     */
    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();

        createOptions();

        CommandLine cmd;

        try {
            // parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            printError("Error while parsing command line options: " + 
                    exp.getLocalizedMessage());
            return;
        }
        
        // if no options has been selected, just return.
        if (cmd.getOptions().length == 0) {
            printHelp();
            return;
        }

        readOptions(cmd);
        
        System.out.println(outputPath.getAbsolutePath());

    }

    private static void readOptions(CommandLine cmd) {
        
        // if the user wants help, display that and close
        if (cmd.hasOption("h")) {
            printHelp();
            return;
        }
        // set the input file/dir
        inputPath = null;
        if (cmd.hasOption("f") && cmd.hasOption("d")) {
            printError("You can set either -f or -d options, not both.");
            return;
        }
        if (cmd.hasOption("f")) {
            inputPath = new File(cmd.getOptionValue("f"));
            if (!inputPath.exists() || !inputPath.isFile()) {
                printError("Invalid path: " + inputPath.getAbsolutePath());
                return;
            }
        } else if (cmd.hasOption("d")) {
            inputPath = new File(cmd.getOptionValue("d"));
            if (!inputPath.exists() || !inputPath.isDirectory()) {
                printError("Invalid path: " + inputPath.getAbsolutePath());
                return;
            }
        }
        if (inputPath == null) {
            printError("No input file or directory detected.");
            return;
        }
        if (cmd.hasOption("o")) {
            outputPath = new File(cmd.getOptionValue("o"));
            if (!outputPath.exists() && !outputPath.mkdir()) {                
                printError("Cannot create output directory.");
                return;
            }
        } else {
            outputPath = new File(System.getProperty("user.dir"));
        }
    }

    /**
     * Generates the command-line options.
     */
    private static void createOptions() {
        // help message
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Display this message")
                .hasArg(false)
                .build()
        );
        
        // load the pipeline
        options.addOption(Option.builder("p")
                .longOpt("pipeline")
                .desc("Use the pipeline contained in the PATH configuration file")
                .hasArg(true)
                .argName("FILE")
                .build()
        );
        
        OptionGroup inputGroup = new OptionGroup();
        //inputGroup.setRequired(true);
        
        // load the input file
        inputGroup.addOption(Option.builder("f")
                .longOpt("file")
                .desc("Analyze the input file FILE")
                .hasArg(true)
                .argName("FILE")
                .build()
        );
        
        // load the input directory
        inputGroup.addOption(Option.builder("d")
                .longOpt("dir")
                .desc("Analyze all files contained in DIR")
                .hasArg(true)
                .argName("DIR")
                .build()
        );
        
        options.addOptionGroup(inputGroup);
        
        // set the output file prefix
        options.addOption(Option.builder("o")
                .longOpt("output-folder")
                .desc("Write the output in PATH")
                .hasArg(true)
                .argName("PATH")
                .build()
        );
        
        // analyze sentences?
        options.addOption(Option.builder("ps")
                .longOpt("print-sentences")
                .desc("Print sentence annotations")
                .hasArg(false)
                .build()
        );
        
        // analyze grams?
        options.addOption(Option.builder("pg")
                .longOpt("print-grams")
                .desc("Print TYPEs annotations over grams")
                .hasArgs()
                .numberOfArgs(2)
                .valueSeparator('=')
                .argName("TYPE")
                .build()
        );
    }

    private static void printError(String message) {
        System.out.println("Error: " + message);
        System.out.println();
        printHelp();
    }

    private static void printHelp() {

        System.out.println("Distiller-CORE library - http://ailab.uniud.it");
        System.out.println();

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("dcore-"
                + Launcher.class.getPackage().getImplementationVersion()
                + ".jar", options);
    }
}
