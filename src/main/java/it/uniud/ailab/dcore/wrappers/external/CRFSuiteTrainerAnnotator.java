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
import it.uniud.ailab.dcore.io.FileWriterStage;
import it.uniud.ailab.dcore.io.GenericSheetPrinter;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.Either;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generator of the CRFSuite training files.
 *
 * @author Marco Basaldella
 */
public class CRFSuiteTrainerAnnotator extends GenericSheetPrinter
        implements FileWriterStage {

    public CRFSuiteTrainerAnnotator() {
        super(true);
    }

    private String targetAnnotation = null;

    // Static field containing the file that will contain the training.
    private static Path trainingFileName;

    // Width of the window used to train the CRFs, i.e. the number of tokens
    // before and after the current one.
    private int windowWidth = 2;

    // The annotations used to generate the file. The field is static
    // so that the annotator detects them for the first file in the dataset only.
    private static List<String> annotations;

    // The types of the annotations above
    private static List<Either<String, Number>> annotationTypes;

    // The same annotations contained above, but sorted (used to preserve
    // the index of the original ordering
    private static List<String> sortedAnnotations;

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

    /**
     * Loads the annotations IDs. The method creates a dummy CSV printer and
     * lets it do the dirty work to avoid rewriting boilerplate code. Then, the
     * target annotation is removed and the annotations are sorted
     * alphabetically.
     *
     * @param c the component where to look the annotations
     */
    private void loadAnnotations(DocumentComponent c) {
        loadTokens(c);

        if (!getHeaders().contains(targetAnnotation)) {
            throw new RuntimeException(
                    "The annotation specified in setTargetAnnotation does not exist.");
        }

        removeHeader(targetAnnotation);

        annotations = getHeaders();
        annotationTypes = getHeaderTypes();

        sortedAnnotations = annotations;
        java.util.Collections.sort(sortedAnnotations);

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
            String name = UUID.randomUUID().toString().replace("-", "")
                    + getFileSuffix();
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

    @Override
    public String getFileSuffix() {
        return ".csv";
    }

    @Override
    public void writeFile(String file, Blackboard b) {
        if (annotations == null) {
            loadAnnotations(b.getStructure());
        }

        try (FileWriter fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            for (Sentence s : DocumentUtils.getSentences(b.getStructure())) {

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

                    // here we generate attributes using the tokens around 
                    // the current one.
                    // if e.g. the current token has index 0 and the window 
                    // has width 2, we must generate the attributes from
                    // the token pairs with index (-,2-,1),(-1,0) ... (1,2)
                    // and the triplets with index (-2,-1,0), (-1,0,-1),
                    // (0,1,2). 
                    // Note that the window 2 DOES NOT MEAN that we get at 
                    // most 2 tokens, but that we must consider ALL THE TOKENS
                    // from (token-2) + (token+2).
                    // Generate all the sub-windows
                    for (int subWindowWidth = 1; subWindowWidth <= windowWidth;
                            subWindowWidth++) {

                        // Generate the couples, triplets, etc.
                        // We must be sure not to go before the beginning
                        // of the sentence and after the end of the sentence.
                        for (int startIndex = Math.max(0, i - windowWidth);
                                startIndex
                                < Math.min(s.getTokens().size() - subWindowWidth,
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

                    // now repeat the same for all the other annotations.                    
                    for (String annotation : sortedAnnotations) {
                        // see the cycles above for explanation

                        for (int j = Math.max(0, i - windowWidth);
                                j < Math.min(s.getTokens().size(), i + windowWidth + 1);
                                j++) {
                            int index = j - i;
                            String word = annotation+ 
                                    "[" + index + "]=" + 
                                    getAnnotationValueAsString(t,annotation);
                            line.add(word);

                        }

                        // see the cycles above for explanation
                        for (int subWindowWidth = 1; subWindowWidth <= windowWidth;
                                subWindowWidth++) {
                            for (int startIndex = Math.max(0, i - windowWidth);
                                    startIndex
                                    < Math.min(s.getTokens().size() - subWindowWidth,
                                            i + windowWidth - subWindowWidth + 1);
                                    startIndex++) {

                                List<String> anns = new ArrayList<>();
                                List<String> contents = new ArrayList<>();

                                for (int j = startIndex;
                                        j < startIndex + subWindowWidth + 1;
                                        j++) {
                                    int index = j - i;
                                    anns.add(annotation + "[" + index + "]");
                                    contents.add(
                                            getAnnotationValueAsString(
                                                    s.getTokens().get(j),
                                                    annotation));
                                }

                                line.add(
                                        String.join("|", anns)
                                        + "="
                                        + String.join("|", contents)
                                );
                            }
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

    @Override
    public void run(Blackboard b) {

        if (targetAnnotation == null || targetAnnotation.isEmpty()) {
            throw new RuntimeException(
                    "Target column not set. \n"
                    + "You must set the annotation used by CRFSuite to train the model!");
        }

        if (trainingFileName == null) {
            trainingFileName = createRandomFile();
        }

        writeFile(trainingFileName.toString(), b);
    }

    @Override
    public void writeFile(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the first value of the given annotation on the given token. If the
     * token does not contain an annotation, the method returns a default value.
     * 
     * @param t the token to analyze
     * @param annotator the annotation to retrieve
     * @return 
     */
    private static String
            getAnnotationValueAsString(Token t, 
                    String annotator) {

        int annotationId = annotations.indexOf(annotator);

        if (t.getAnnotation(annotator) == null) {
            if (annotationTypes.get(annotationId).isLeft()) {
                return "§NULL§";
            } else {
                return "0";
            }
        }
        
        Either<String,Number> annotationValue =
                t.getAnnotation(annotator).getValueAt(0);
        
        if (annotationValue.isLeft()) { // the annotation is a string
            return annotationValue.getLeft();
        } else { // the annotation is a number
            return // if there's no decimal part in the numeric
                    // value, avoid printing ".0"
                    annotationValue.getRight().doubleValue()
                    == Math.floor(annotationValue.getRight().doubleValue())
                    ? String.format(
                            Locale.US, "%d",
                            annotationValue.getRight().intValue())
                    : String.format(
                            Locale.US, "%f",
                            annotationValue.getRight().doubleValue());
        }

    }
}
