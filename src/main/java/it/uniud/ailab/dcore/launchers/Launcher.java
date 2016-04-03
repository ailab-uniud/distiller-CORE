/*
 * Copyright (C) 2015 Artificial Intelligence
 * Laboratory @ University of Udine.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package it.uniud.ailab.dcore.launchers;

import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.DistillerFactory;
import it.uniud.ailab.dcore.eval.GenericDataset;
import it.uniud.ailab.dcore.eval.datasets.SemEval2010;
import it.uniud.ailab.dcore.eval.kp.KeyphraseEvaluatorAll;
import it.uniud.ailab.dcore.eval.training.KeyphraseTrainingSetGenerator;
import it.uniud.ailab.dcore.io.CsvPrinter;
import it.uniud.ailab.dcore.io.GenericSheetPrinter;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.utils.FileSystem;
import it.uniud.ailab.dcore.utils.Pair;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
 *
 * The input files should be saved in UTF-8 or UTF-16 format.
 * </ul>
 *
 * @author Marco Basaldella
 *
 * Add a new KE configuration for linguistic feature calculation, named
 * stanfordKE
 * @modify Giorgia Chiaradia
 */
public class Launcher {

    /**
     * A shared distiller instance.
     */
    private static Distiller distiller;

    private enum Mode {

        DEFAULT,
        EVALUATION,
        TRAINING_GENERATION;
    }

    private static Mode mode = Mode.DEFAULT;

    /**
     * The file or directory to analyze.
     */
    private static File inputPath;

    /**
     * The output directory.
     */
    private static File outputPath;

    /**
     * The path of the pipeline to use.
     */
    private static File configPath;

    /**
     * Which of the default pipelines has been selected by the user.
     */
    private static String defaultConfig = null;

    /**
     * Which of the packaged pipelines has been selected by the user.
     */
    private static String packagedConfig = null;

    /**
     * The command-line options.
     */
    private static final Options options = new Options();

    /**
     * The language to use to distill.
     */
    private static Locale language = null;

    /**
     * Verbose mode flag.
     */
    private static boolean verbose = false;

    /**
     * Preprocess mode. Use this only if you want to preprocess text using
     * sections.
     */
    private static boolean preprocess;

    /**
     * The dataset that will be used to perform evaluation or training.
     */
    private static String dataset = "";

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
            printError("Error while parsing command line options: "
                    + exp.getLocalizedMessage());
            return;
        }

        // if no options has been selected, just return.
        if (cmd.getOptions().length == 0) {
            printHelp();
            return;
        }

        // read the options.
        if (readOptions(cmd)) {
            // everything's good! proceed
            doWork();
        } else {
            printError("Unexpected error while parsing command line options\n"
                    + "Please contact the developers of the framwork to get "
                    + "additional help.");
            return;
        }
    }

    /**
     * Reads the command line options.
     *
     * @param cmd the command line options.
     * @return true if everything have been parsed right; false otherwise.
     */
    private static boolean readOptions(CommandLine cmd) {

        // if the user wants help, display that and close
        if (cmd.hasOption("h")) {
            printHelp();
            return true;
        }

        // read mode 
        if (cmd.hasOption("e")) {
            mode = Mode.EVALUATION;
            dataset = cmd.getOptionValue("e");
        }

        // read mode 
        if (cmd.hasOption("t")) {
            mode = Mode.TRAINING_GENERATION;
            dataset = cmd.getOptionValue("t");
        }

        // set the input file/dir
        inputPath = null;
        if (cmd.hasOption("f") && cmd.hasOption("d")) {
            printError("You can set either -f or -d options, not both.");
            return false;
        }

        if (cmd.hasOption("f")) {
            inputPath = new File(cmd.getOptionValue("f"));
            if (!inputPath.exists() || !inputPath.isFile()) {
                printError("Invalid path: " + inputPath.getAbsolutePath());
                return false;
            }
        } else if (cmd.hasOption("d")) {
            inputPath = new File(cmd.getOptionValue("d"));
            if (!inputPath.exists() || !inputPath.isDirectory()) {
                printError("Invalid path: " + inputPath.getAbsolutePath());
                return false;
            }
        }
        if (inputPath == null) {
            printError("No input file or directory detected.");
            return false;
        }

        if (cmd.hasOption("o")) {
            outputPath = new File(cmd.getOptionValue("o"));
            if (!outputPath.exists() && !outputPath.mkdir()) {
                printError("Cannot create output directory.");
                return false;
            }
        } else {
            outputPath = new File(System.getProperty("user.dir"));
        }

        int optionCount
                = (cmd.hasOption("c") ? 1 : 0)
                + (cmd.hasOption("cd") ? 1 : 0)
                + (cmd.hasOption("cp") ? 1 : 0);

        if (optionCount > 1) {
            printError("You should specify only one pipeline!");
            return false;
        } else if (optionCount < 1) {
            printError("You should specify a pipeline!");
            return false;
        } else if (cmd.hasOption("c")) {
            configPath = new File(cmd.getOptionValue("c"));
            if (!configPath.exists() || !configPath.isFile()) {
                printError("Invalid path: " + configPath.getAbsolutePath());
                return false;
            }
        } else if (cmd.hasOption("cd")) {
            defaultConfig = cmd.getOptionValue("cd");
        } else if (cmd.hasOption("cp")) {
            packagedConfig = cmd.getOptionValue("cp");
        }

        if (cmd.hasOption("v")) {
            verbose = true;
        }

        if (cmd.hasOption("l")) {
            language = new Locale(cmd.getOptionValue("l"));
        }

        return true;
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

        // work modes: evaluation
        options.addOption(Option.builder("e")
                .longOpt("evaluate")
                .desc("Evaluate the pipeline using the DATASET dataset")
                .hasArg(true)
                .argName("DATASET")
                .build()
        );

        // work modes: training
        options.addOption(Option.builder("t")
                .longOpt("training-generation")
                .desc("Generate a training set for machine learning "
                        + "using the DATASET dataset")
                .hasArg(true)
                .argName("DATASET")
                .build()
        );

        // load the pipeline
        options.addOption(Option.builder("c")
                .longOpt("config-file")
                .desc("Use the configuration located in PATH")
                .hasArg(true)
                .argName("FILE")
                .build()
        );

        // load the pipeline-2
        options.addOption(Option.builder("cd")
                .longOpt("config-default")
                .desc("Use one of the default configurations (deprecated)")
                .hasArg(true)
                .argName("PIPELINE")
                .build()
        );

        // load the pipeline-3
        options.addOption(Option.builder("cp")
                .longOpt("config-packaged")
                .desc("Use one of the pre-packaged configurations")
                .hasArg(true)
                .argName("PIPELINE")
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
                .desc("Analyze all files contained in DIR (not recursive)")
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

        // verbose distillation
        options.addOption(Option.builder("v")
                .longOpt("verbose")
                .desc("Print details while extracting")
                .hasArg(false)
                .build());

        options.addOption(Option.builder("l")
                .longOpt("language")
                .desc("LANGUAGE of the input document (optional)")
                .hasArg(true)
                .argName("LANGUAGE")
                .build()
        );
    }

    /**
     * Displays an error message followed by the instructions on how to use the
     * Launcher.
     *
     * @param message the error message.
     */
    private static void printError(String message) {
        System.out.println("Error: " + message);
        System.out.println();
        printHelp();
    }

    /**
     * Displays the instructions.
     */
    private static void printHelp() {

        System.out.println("Distiller-CORE library - http://ailab.uniud.it");
        System.out.println();

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("dcore-"
                + Launcher.class.getPackage().getImplementationVersion()
                + ".jar", options);
    }

    /**
     * Decide what Distillation (single or directory) execute and run it.
     */
    private static void doWork() {

        switch (mode) {

            case EVALUATION:
                evaluate();
                break;
            case TRAINING_GENERATION:
                generateTrainingSet();
                break;

            default:
                try {
                    if (inputPath.isFile()) {
                        analyzeFile(inputPath);
                    } else {
                        analyzeDir(inputPath);
                    }
                } catch (IOException ioe) {
                    System.err.println(ioe.getLocalizedMessage());
                    System.err.println(ioe.toString());
                }
        }

    }

    /**
     * Performs the evaluation of a Distiller pipeline on the specified dataset,
     * deferring the work to the appropriate class.
     */
    private static void evaluate() {

        System.out.println("Launching evaluation...");

        if (!inputPath.isDirectory()) {
            System.err.println(
                    "You should set the folder containing the evaluation files as input.");
        }

        setupDistiller();

        GenericDataset kpDataset;

        switch (dataset) {
            case "semeval":
                kpDataset = new SemEval2010(inputPath.getAbsolutePath());
                break;
            default:
                kpDataset = null;
        }

        if (kpDataset == null) {
            throw new UnsupportedOperationException(
                    "Unknown dataset:" + dataset);
        }

        (new KeyphraseEvaluatorAll(kpDataset)).
                evaluate(distiller);

    }

    /**
     * Generates the training set of a Distiller pipeline on the specified
     * dataset, deferring the work to the appropriate class.
     */
    private static void generateTrainingSet() {

        System.out.println("Launching training set generation...");

        if (!inputPath.isDirectory()) {
            printError(
                    "You should set the folder containing the dataset files as input.");
            return;
        }

        if (outputPath == null || !outputPath.isDirectory()) {
            printError(
                    "You should set an output folder for the training set files.");
        }

        setupDistiller();

        GenericDataset kpDataset;

        switch (dataset) {
            case "semeval":
                kpDataset = new SemEval2010(inputPath.getAbsolutePath());
                break;
            default:
                kpDataset = null;
        }

        if (kpDataset == null) {
            throw new UnsupportedOperationException(
                    "Unknown dataset:" + dataset);
        }

        KeyphraseTrainingSetGenerator trainingGenerator
                = new KeyphraseTrainingSetGenerator(kpDataset);

        IOBlackboard.setDocumentsFolder(kpDataset.getTrainingFolder());

        List<Pair<String, GenericSheetPrinter>> trainingDocuments
                = trainingGenerator.generateTrainingSet(distiller);

        GenericSheetPrinter trainingSet = new CsvPrinter(CsvPrinter.DEFAULT_DELIMITER, true, true);

        for (Pair<String, GenericSheetPrinter> tr : trainingDocuments) {

            GenericSheetPrinter p = tr.getRight();
            trainingSet.addPrinter(p);

        }

        String filePath = outputPath.getAbsolutePath()
                + FileSystem.getSeparator()
                + dataset + ".training.txt";
        trainingSet.writeFile(filePath);
        System.out.println(
                "Saved training file in " + filePath);

        IOBlackboard.setDocumentsFolder(kpDataset.getTestFolder());

        List<Pair<String, GenericSheetPrinter>> testDocuments
                = trainingGenerator.generateTestSet(distiller);

        GenericSheetPrinter testSet = new CsvPrinter(CsvPrinter.DEFAULT_DELIMITER, true, true);

        for (Pair<String, GenericSheetPrinter> tr : testDocuments) {

            GenericSheetPrinter p = tr.getRight();
            testSet.addPrinter(p);

        }

        filePath = outputPath.getAbsolutePath()
                + FileSystem.getSeparator()
                + dataset + ".test.txt";

        testSet.writeFile(filePath);
        System.out.println(
                "Saved training file in " + filePath);

    }

    /**
     * Distill the content of a file.
     *
     * @param filePath the file to analyze.
     *
     * @throws IOException if there's an error reading the file.
     */
    private static void analyzeFile(File filePath) throws IOException {

        setupDistiller();

        String fileName = filePath.toPath().getFileName().toString();

        IOBlackboard.setCurrentDocument(filePath.getAbsolutePath());

        String document = loadDocument(filePath);
        List<String> docLines = loadDocumentAsList(filePath);
        IOBlackboard.setOutputPathPrefix(outputPath.getAbsolutePath()
                + FileSystem.getSeparator()
                + fileName);
        distiller.distill(document, docLines);

    }

    /**
     * Load the document trying different charsets. The charset tried, are, in
     * order:
     * <ul>
     * <li>UTF-16;</li>
     * <li>UTF-8;</li>
     * <li>US-ASCII.</li>
     * </ul>
     *
     * @param filePath the path of the document
     * @return the text of the document
     * @throws IOException if the charset is not supported
     */
    private static String loadDocument(File filePath) throws IOException {

        String document = "";

        IOException exception = null;
        // try different charsets. if none is recognized, throw the
        // exception detected when reading.
        try {
            document = String.join(" ", Files.readAllLines(
                    filePath.toPath(), StandardCharsets.UTF_8));

        } catch (java.nio.charset.MalformedInputException e) {
            exception = e;
        }

        if (exception != null) {
            try {
                exception = null;
                document = String.join(" ", Files.readAllLines(
                        filePath.toPath(), StandardCharsets.UTF_16));

            } catch (java.nio.charset.MalformedInputException e) {
                exception = e;
            }
        }

        if (exception != null) {
            try {
                exception = null;
                document = String.join(" ", Files.readAllLines(
                        filePath.toPath(), StandardCharsets.US_ASCII));

            } catch (java.nio.charset.MalformedInputException e) {
                exception = e;
            }
        }

        // no charset has been recognized
        if (exception != null) {
            throw exception;
        }
        return document;
    }

    /**
     * Load the document dividing text into lines and collect them in a list,
     * trying different charsets. The charset tried, are, in order:
     * <ul>
     * <li>UTF-16;</li>
     * <li>UTF-8;</li>
     * <li>US-ASCII.</li>
     * </ul>
     *
     * @param filePath the path of the document
     * @return the text of the document
     * @throws IOException if the charset is not supported
     */
    private static List<String> loadDocumentAsList(File filePath) throws IOException {

        List<String> lines = new ArrayList<>();

        IOException exception = null;
        // try different charsets. if none is recognized, throw the
        // exception detected when reading.
        try {

            lines = Files.readAllLines(
                    filePath.toPath(), StandardCharsets.UTF_8);

        } catch (java.nio.charset.MalformedInputException e) {
            exception = e;
        }

        if (exception != null) {
            try {
                exception = null;

                lines = Files.readAllLines(
                        filePath.toPath(), StandardCharsets.UTF_16);

            } catch (java.nio.charset.MalformedInputException e) {
                exception = e;
            }
        }

        if (exception != null) {
            try {
                exception = null;

                lines = Files.readAllLines(
                        filePath.toPath(), StandardCharsets.US_ASCII);

            } catch (java.nio.charset.MalformedInputException e) {
                exception = e;
            }
        }

        // no charset has been recognized
        if (exception != null) {
            throw exception;
        }
        return lines;
    }

    /**
     * Distill the content of a directory.
     *
     * @param inputPath the directory analyze.
     *
     * @throws IOException if there's an error reading the file.
     */
    private static void analyzeDir(File inputPath) throws IOException {
        File folderPath = inputPath;

        IOBlackboard.setDocumentsFolder(inputPath.getAbsolutePath());

        for (File f : folderPath.listFiles()) {

            System.out.println("Analyzing " + f.getAbsolutePath() + "...");
            analyzeFile(f);

        }

    }

    /**
     * Configures the shared Distiller instance.
     */
    private static void setupDistiller() {
        distiller = null;

        if (defaultConfig == null && packagedConfig == null) {
            distiller = DistillerFactory.loadFromXML(configPath);
        } else if (defaultConfig == null) {
            distiller = DistillerFactory.loadFromPackagedXML(
                    "pipelines"
                    + FileSystem.getSeparator()
                    + packagedConfig
                    + ".xml");
        } else if (defaultConfig.equals("simpleKE")) {
            distiller = DistillerFactory.getDefaultCode();
            //use this configuration to use stanford coreNLP parser and add linguistis
            //features to distiller
        } else if (defaultConfig.equals("stanfordKE")) {
            distiller = DistillerFactory.getStanfordCode();

        }// add other default pipelines HERE
        // please remeber to document the new pipeline in the help message
        // that is printed below 
        else {

            System.out.println("Unrecognized configuration. Supported parameters:");
            System.out.println("- simpleKE : simple, offline keyphrase extraction");
            System.out.println();

            printError("Please select a valid configuration.");
            return;
        }

        if (language != null) {
            distiller.setLocale(language);
        }

        distiller.setVerbose(verbose);
    }
}
