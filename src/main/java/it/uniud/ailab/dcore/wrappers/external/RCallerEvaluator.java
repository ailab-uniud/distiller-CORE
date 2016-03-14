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

import com.github.rcaller.rStuff.RCaller;
import com.github.rcaller.rStuff.RCode;
import com.github.rcaller.util.Globals;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.io.CsvPrinter;
import it.uniud.ailab.dcore.io.GenericSheetPrinter;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.utils.Either;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Marco Basaldella
 */
public class RCallerEvaluator implements Annotator {

    private String modelPath;

    private String modelParameters;

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        Collection<Keyphrase> keyphrases
                = blackboard.getGramsByGenericType(Keyphrase.KEYPHRASE);

        RPrinter rPrinter = new RPrinter();
        rPrinter.loadKeyphrases(blackboard);

        rPrinter.writeFile(null);

        // Step 1: generate the candidates file
        FileSystem.createDirectoryIfNotExists(
                FileSystem.getDistillerTmpPath());

        String candidatePath = FileSystem.getDistillerTmpPath().
                concat(FileSystem.getSeparator()).
                concat("candidates.csv");

        CsvPrinter candidatePrinter = new CsvPrinter();
        candidatePrinter.loadKeyphrases(blackboard);
        candidatePrinter.writeFile(candidatePath);

        // Step 2: move the models to the tmp path
        // TODO: implement step 2
        // Step 3: predict with R
        RCaller caller = new RCaller();
        Globals.detect_current_rscript();
        caller.setRscriptExecutable(Globals.Rscript_current);

        String modelPath = getClass().
                getClassLoader().
                getResource("models/keyphrase-extraction/glm.model").
                toString();

        modelPath = modelPath.substring(5);
        System.out.println(modelPath);

        RCode rCode = new RCode();
        rCode.addRCode("load(\"" + modelPath + "\")");
        rCode.addRCode("predictions <- read.csv(\""
                + candidatePath
                + "\",stringsAsFactors = FALSE)");
        rCode.addRCode("predictions$score <- predict(model,newdata = predictions, type=\"response\")");

        caller.setRCode(rCode);
        caller.runAndReturnResult("predictions");

        String[] idChecks = caller.getParser().getAsStringArray("ID");
        double[] predictions = caller.getParser().getAsDoubleArray("score");

        int kpCounter = 0;
        for (Keyphrase kp : keyphrases) {
            // coherence check: if for some reason we are getting the
            // wrong KP from the printer, shut down everything.
            if (!kp.getIdentifier().equals(idChecks[kpCounter])) {
                throw new AnnotationException(
                        this, "ERROR: non-matching keyphrase in R code printer");
            }
            kp.putFeature(
                    it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator.SCORE,
                    predictions[kpCounter]);
            
            kpCounter++;
        }

    }

    private static class RPrinter extends GenericSheetPrinter {

        private String colNames = "";

        private String[] rows;

        public RPrinter() {
            super(false);
        }

        @Override
        public void writeFile(String fileName) {

            // Write the column names
            colNames = "c(";

            String[] headers = getHeaders().toArray(new String[getHeaders().size()]);
            for (int i = 0; i < headers.length; i++) {
                headers[i] = "\"" + headers[i] + "\"";
            }

            colNames = colNames + String.join(",",
                    headers);

            colNames = colNames + ")";

            rows = new String[getRows().size()];
            int currentRow = 0;

            for (Map<String, Either<String, Number>> row : getRows()) {
                String rowString = "list(";
                String[] rowArray = new String[getHeaders().size()];

                for (int i = 0; i < getHeaders().size(); i++) {
                    String header = getHeaders().get(i);
                    Either<String, Number> cell = row.get(header);

                    if (cell == null) {
                        if (getHeaderTypes().get(i).isLeft()) {
                            rowArray[i] = "";
                        } else {
                            rowArray[i] = "0";
                        }
                    } else if (cell.isLeft()) { // the value is a string
                        rowArray[i] = "\"" + cell.getLeft() + "\"";
                    } else { // the value is a number
                        rowArray[i]
                                = // if there's no decimal part in the numeric
                                // value, avoid printing ".0"
                                cell.getRight().doubleValue()
                                == Math.floor(cell.getRight().doubleValue())
                                        ? String.format(
                                                Locale.US, "%d",
                                                cell.getRight().intValue())
                                        : String.format(
                                                Locale.US, "%f",
                                                cell.getRight().doubleValue());
                    }
                }

                rowString = rowString + String.join(",", rowArray) + ")";
                rows[currentRow++] = rowString;

            } // for (Map<String, Either<String, Number>> row : getRows())
        }

    }

}
