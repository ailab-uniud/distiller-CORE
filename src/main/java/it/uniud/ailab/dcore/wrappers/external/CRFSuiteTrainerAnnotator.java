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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.io.CsvPrinter;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author red
 */
public class CRFSuiteTrainerAnnotator implements Annotator {

    private String targetAnnotation = null;

    // Static field containing the file that will contain the training.
    private static Path trainingFileName;

    // Width of the window used to train the CRFs, i.e. the number of tokens
    // before and after the current one.
    private int windowWidth = 2;

    // The annotations used to generate the file. The field is static
    // so that the annotator detects them for the first file in the dataset only.
    private static List<String> annotations;

    /**
     * This annotation will be the <b>first</b> column in the training file, so
     * it will be used by CRFSuite to train the model.
     *
     * @param targetAnnotation
     */
    public void setTargetAnnotation(String targetAnnotation) {
        this.targetAnnotation = targetAnnotation;
    }

    /**
     * Width of the window used to train the CRFs, i.e. the number of tokens
     * before and after the current one.
     *
     * @param windowWidth the width of the window
     */
    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        if (targetAnnotation == null || targetAnnotation.isEmpty()) {
            throw new RuntimeException(
                    "Target column not set. \n"
                    + "You must set the annotation used by CRFSuite to train the model!");
        }

        if (trainingFileName == null) {
            trainingFileName = createRandomFile();
        }

        printTrainingFile(blackboard.getStructure());

    }

    /**
     * Prints the training file.
     *
     * @param component the root of the document.
     */
    private void printTrainingFile(DocumentComponent component) {
        if (annotations == null) {
            loadAnnotations(component);
        }

        try (FileWriter fw = new FileWriter(trainingFileName.toFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            for (Sentence s : DocumentUtils.getSentences(component)) {

                for (int i = 0; i < s.getTokens().size(); i++) {

                    Token t = s.getTokens().get(i);

                    // build the line. the first element is the target
                    // annotation
                    List<String> line = new ArrayList<>();
                    Annotation ann = t.getAnnotation(targetAnnotation);
                    line.add(ann.getStringAt(0));

                    // then, we have the surface
                    // pay attention to the cycle condition: the window must
                    // NOT overflow over the end of the sentence and NOT
                    // underflow before the beginning of the sentence.
                    // So, the window width is NOT fixed, but actually variable
                    // To ensure that we are ALWAYS inside the sentence.
                    for (int j = Math.max(0, i - windowWidth);
                            j < Math.min(s.getTokens().size(), i + windowWidth + 1);
                            j++) {
                        // this value is used to mark where the annotation
                        // comes from (previous token, current token...)
                        int index = j - i;
                        String word = "w[" + index + "]=" + s.getTokens().
                                get(j).getText();
                        line.add(word);

                    }

                    for (int subWindowWidth = 1; subWindowWidth <= windowWidth;
                            subWindowWidth++) {

                        for (int startIndex = Math.max(0, i - windowWidth);
                                startIndex <  
                                Math.min(s.getTokens().size() - subWindowWidth, 
                                        i + windowWidth - subWindowWidth + 1);
                                startIndex++) {

                            // the left-hand side of the column: identifies
                            // where the annotations comes from
                            List<String> anns = new ArrayList<>();
                            // the right-hand side of the column contains the
                            // content of the annotation
                            List<String> contents = new ArrayList<>();

                            for (int j = startIndex;
                                    j < startIndex + subWindowWidth + 1;
                                    j++) {
                                // this value is used to mark where the annotation
                                // comes from (previous token, current token...)
                                int index = j - i;
                                anns.add("w[" + index + "]");
                                contents.add(s.getTokens().get(j).getText());
                            }

                            line.add(
                                    String.join("|", anns)
                                    + "="
                                    + String.join("|", contents)
                            );
                        }
                    }
                    
                    if (i == 0) {
                        line.add("__BOS__");
                    } else if (i == s.getTokens().size() - 1) {
                        line.add("__EOS__");
                    }

                    out.println(String.join("\t", line));
                }

                // Empty line to separate tokens
                out.println();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while writing the training file "
                    + trainingFileName.toString());
        }
    }

    /**
     * Loads the annotations IDs. The method creates a dummy CSV printer and
     * lets it do the dirty work to avoid rewriting boilerplate code. Then, the
     * target annotation is removed and the annotations are sorted
     * alphabetically.
     *
     * @param c the component where to look the annotations
     */
    private void loadAnnotations(DocumentComponent c) {
        CsvPrinter printer = new CsvPrinter();
        printer.loadTokens(c);
        annotations = printer.getHeaders();

        if (!annotations.contains(targetAnnotation)) {
            throw new RuntimeException(
                    "The annotation specified in setTargetAnnotation does not exist.");
        }

        annotations.remove(targetAnnotation);
        java.util.Collections.sort(annotations);

    }

    /**
     * Generates the training file using a random file name and creating an
     * empty file.
     *
     * @return the path to file that will contain the training data.
     */
    private Path createRandomFile() {

        Path path = Paths.get(FileSystem.getDistillerTmpPath());
        File f;

        do {
            // csv format for easy debugging
            String name = UUID.randomUUID().toString().replace("-", "") + ".csv";
            path = path.resolve(name);
            f = path.toFile();
        } while (f.exists());

        // create (touch) the file. 
        try {
            new FileOutputStream(f).close();
        } catch (IOException ex) {
            Logger.getLogger(CRFSuiteTrainerAnnotator.class.getName()).
                    log(Level.SEVERE, "Unable to create training file.", ex);
            throw new RuntimeException("Unable to create training file.");
        }

        return path;
    }

}
