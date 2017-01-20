/*
 * Copyright (C) 2017 Artificial Intelligence
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

import com.github.jcrfsuite.CrfTagger;
import com.github.jcrfsuite.util.Pair;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class uses the CRFSuite software to tag the tokens using a pre-trained
 * model.
 *
 * @author Marco Basaldella
 */
public class CRFSuiteEvaluator implements Annotator {

    public static final String CRF_TAG = "CRF_Tag";

    private String modelPath;

    private String targetAnnotation;

    private int windowWidth;

    private List<String> ignores;

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public void setTargetAnnotation(String targetAnnotation) {
        this.targetAnnotation = targetAnnotation;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setIgnores(List<String> ignores) {
        this.ignores = ignores;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        if (modelPath == null) {
            throw new AnnotationException(this, "You must specify a model path for the CRFSuite library.");
        }

        // prepare the model file, copying it to the tmp folder if necessary
        // Step 2: move the models to the Distiller's temporary folder
        String tmpModelPath
                = FileSystem.getDistillerTmpPath().
                concat(FileSystem.getSeparator()).
                concat("model.crfsuite");

        try {
            org.apache.commons.io.FileUtils.copyInputStreamToFile(
                    FileSystem.getInputStreamFromPath(
                            getClass().getClassLoader().
                            getResource(modelPath).getFile()),
                    new File(tmpModelPath)
            );

        } catch (IOException ex) {
            Logger.getLogger(CRFSuiteEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            throw new AnnotationException(this,
                    "Error while copying model file to temporary directory");
        } catch (NullPointerException ex) {
            // The model required is not packaged in the JAR: just use
            // the provided path
            tmpModelPath = modelPath;
        }

        CrfTagger tagger = new CrfTagger(tmpModelPath);

        CRFSuiteTrainerAnnotator taggerFileGenerator = new CRFSuiteTrainerAnnotator();

        taggerFileGenerator.setTargetAnnotation(targetAnnotation);
        taggerFileGenerator.setIgnores(ignores);
        taggerFileGenerator.setWindowWidth(windowWidth);
        
        Logger.getLogger(CRFSuiteEvaluator.class.getName()).log(Level.INFO, 
                "Generating the input file for the CRF tagger...");
        Path taggerFile = taggerFileGenerator.generateTaggerFile(blackboard);

        List<List<Pair<String, Double>>> tags = null;

        Logger.getLogger(CRFSuiteEvaluator.class.getName()).log(Level.INFO, 
                "Calling CRFSuite...");
        try {
            tags = tagger.tag(taggerFile.toString());
        } catch (IOException ex) {
            Logger.getLogger(CRFSuiteEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            throw new AnnotationException(this, "Error calling the CRFSuite library.", ex);
        }

        // check if tagged was performed correctly
        if (tags.size() != DocumentUtils.getSentences(blackboard.getStructure()).size()) {
            throw new AnnotationException(this,
                    "Error while tagging: mismatching numbers of sequences/sentences.");
        }

        List<Sentence> sentences = DocumentUtils.getSentences(blackboard.getStructure());

        for (int i = 0; i < tags.size(); i++) {

            // check again tagging correctness, sentence by sentence
            if (tags.get(i).size() != sentences.get(i).getTokens().size()) {
                if (tags.size() != DocumentUtils.getSentences(blackboard.getStructure()).size()) {
                    throw new AnnotationException(this,
                            "Error while tagging: mismatching number of tokens in sentence \""
                            + sentences.get(i).getText().substring(0, 20)
                            + "\"...");
                }
            }

            for (int j = 0; j < tags.get(i).size(); j++) {
                String tag = tags.get(i).get(j).first;

                sentences.get(i).getTokens().get(j).addAnnotation(
                        new TextAnnotation(CRF_TAG,tag));

            }
        }
        
        // if everything went fine, delete the tmp file used by CRFSuite to
        // perform the tagging, since it can be quite big (~150-200 MB on
        // a ~10 page document)
        taggerFile.toFile().delete();

    }

}
