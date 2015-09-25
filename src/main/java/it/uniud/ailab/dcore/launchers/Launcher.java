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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
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
 * <li>Print the reslit of the computation.</li>
 * </ul>
 *
 *
 * @author Marco Basaldella
 */
public class Launcher {

    private static Options options = new Options();

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();

        Option opt = Option.builder()
                .longOpt("evaluateKE")
                .desc("Evaluate Keyphrase Extraction on the "
                        + "DATASET dataset contained in the FOLDER folder ")
                .hasArg(false)
                .build();

        options.addOption(opt);

        CommandLine cmd;

        try {
            // parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Error while parsing command line options");
            System.err.println(exp.getLocalizedMessage());

            return;

        }

        // if we want to run the evaluation pipeline, just throw all 
        // at the evaluation launcher
        if (cmd.hasOption("evaluateKE")) {
            KeyphraseEvaluation.main(args);
            return;
        }
        
        
        // if no option has been executed, just print the help message
        printHelp();

    }
    
    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("dcore-" +
                Launcher.class.getPackage().getImplementationVersion()
                + ".jar"
                , options);
    }
}
