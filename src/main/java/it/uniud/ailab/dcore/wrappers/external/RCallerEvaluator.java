/*
 * Copyright (C) 2016 Artificial Intelligence
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
package it.uniud.ailab.dcore.wrappers.external;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.github.rcaller.util.Globals;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.io.CsvPrinter;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class allows the Disitller to evaluate the odds that an n-gram is a
 * keyphrase by using a pre-trained machine learning model by calling a
 * <a href="https://www.r-project.org/">R</a> process instance. The model should
 * have been trained beforehand <i>outside</i> the Distiller.
 *
 * @author Marco Basaldella
 */
public class RCallerEvaluator implements Annotator {

    /**
     * See {@link #setModelPath(java.lang.String)
     */
    private String modelPath = "models/keyphrase-extraction/glm.model";

    /**
     * See {@link #setModelParameters(java.lang.String) }.
     */
    private String modelParameters = "type=\"response\"";

    /**
     * See {@link #setRequires(java.lang.String) }.
     */
    private String requires = "";

    /**
     * Sets the path to a RFIle containing the machine learning model to be used
     * with R's <i>predict</i> function to calculate the probability that a
     * candidate ngram is an actual keyphrase. The path can point both to a
     * packaged file or to an external model. The model itself should be saved
     * as a variable called <i>model</i> inside the RData file. The default
     * value points to an embedded logistic regression model.
     *
     * @param modelPath the path to the RData file containing the model.
     */
    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    /**
     * The parameters to be used with R's <i>predict</i> function. The default
     * value adds the parameters for the default logistic regression model.
     *
     * @param modelParameters a string containing the parameters for R's
     * <i>predict</i> function.
     */
    public void setModelParameters(String modelParameters) {
        this.modelParameters = modelParameters;
    }

    /**
     * The packages required by R to run the predict command, separated by a
     * comma.
     *
     * @param requires packages required by R, separated by comma.
     */
    public void setRequires(String requires) {
        this.requires = requires;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        Collection<Keyphrase> keyphrases
                = blackboard.getGramsByType(Keyphrase.KEYPHRASE);

        // Step 1: generate the candidates file
        FileSystem.createDirectoryIfNotExists(
                FileSystem.getDistillerTmpPath());

        String candidatePath = FileSystem.getDistillerTmpPath().
                concat(FileSystem.getSeparator()).
                concat("candidates.csv");

        CsvPrinter candidatePrinter = new CsvPrinter();
        candidatePrinter.loadKeyphrases(blackboard);
        candidatePrinter.writeFile(candidatePath);

        // Step 2: move the models to the Distiller's temporary folder
        String tmpModelPath
                = FileSystem.getDistillerTmpPath().
                concat(FileSystem.getSeparator()).
                concat("model.RData");

        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(
                    FileSystem.getInputStreamFromPath(
                            getClass().getClassLoader().
                            getResource(modelPath).getFile()),
                    new File(tmpModelPath)
            );

        } catch (IOException ex) {
            Logger.getLogger(RCallerEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            throw new AnnotationException(this,
                    "Error while copying model file to temporary directory");
        } catch (NullPointerException ex) {
            // The model required is not packaged in the JAR: just use
            // the provided path
            tmpModelPath = modelPath;
        }

        // Step 3: predict with R
        RCaller caller = RCaller.create();
        Globals.detect_current_rscript();

        RCode rCode = RCode.create();

        // load packages (if any)
        if (requires != null && !requires.isEmpty()) {
            String[] packageNames = requires.split(",");
            for (String packageName : packageNames) {
                rCode.addRCode("require(\"" + packageName + "\")");
            }
        }

        rCode.addRCode("load(\"" + tmpModelPath + "\")");
        rCode.addRCode("predictions <- read.csv(\""
                + candidatePath
                + "\",stringsAsFactors = FALSE)");

        String prediction = "predictions$score <- predict(model,newdata = predictions";

        if (modelParameters != null && !modelParameters.isEmpty()) {
            prediction = prediction.concat(",").concat(modelParameters);
        }
        prediction = prediction.concat(")");

        rCode.addRCode(prediction);

        caller.setRCode(rCode);
        caller.runAndReturnResult("predictions");

        String[] idChecks = caller.getParser().getAsStringArray("ID");
        double[] predictions = caller.getParser().getAsDoubleArray("score");

        // Step 4: collect predictions and store them in the KP object.
        int kpCounter = 0;
        for (Keyphrase kp : keyphrases) {
            // coherence check: if for some reason we are getting the
            // wrong KP from the printer, shut down everything.
            if (!kp.getIdentifier().equals(idChecks[kpCounter])) {
                Logger.getLogger(RCallerEvaluator.class.getName()).log(Level.WARNING,
                        "Non-matching keyphrase in R output file: {0}",
                        kp.getIdentifier());
            }
            kp.putFeature(
                    it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator.SCORE,
                    predictions[kpCounter]);

            kpCounter++;
        }
    }
}
