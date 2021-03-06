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
package it.uniud.ailab.dcore.eval;

import java.util.Comparator;
import java.util.Map;

/**
 * A generic dataset loader. The concrete implementation will take care the duty
 * to load the results into the appropriate structure and provide a Comparator
 * implementation that follows the logic of the dataset.
 * 
 * For example, keyphrase evaluation may be performed both on "simple" keyphrases,
 * or on lemmatized keyphrases, or on stemmed keyphrases.
 * 
 * @author Marco Basaldella
 */
public abstract class GenericDataset implements Comparator<String> {
    
    /**
     * The path where the evaluator will find the input documents and the
     * gold standard results.
     */
    protected final String datasetPath;
    
    /**
     * The training documents. Each document has an identifier and a content.
     */
    private Map<String, String> trainingDocuments;
    /**
     * The expected answers for the test documents. Each list of training answers
     * is paired to the identifier of the corresponding document.
     */
    private Map<String, String[]> trainingAnswers;
    
    /**
     * The test documents. Each document has an identifier and a content.
     */
    private Map<String, String> testDocuments;
    /**
     * The expected answers for the test documents. Each list of test answers
     * is paired to the identifier of the corresponding document.
     */
    private Map<String, String[]> testAnswers;
    
    /**
     * A value that indicates wheter if the documents have already been loaded
     * or not.
     */
    private boolean isLoaded;
    
    /**
     * An output-friendly string that identifies the dataset.
     */
    private final String identifier;
    
    /**
     * Create a concrete dataset that will contain the data contained in the 
     * specified path.
     * 
     * @param datasetPath The folder where the Dataset will look for the document. 
     * @param identifier An output-friendly string that identifies the dataset.
     */
    public GenericDataset(String datasetPath,String identifier) {
        this.datasetPath = datasetPath;
        this.isLoaded = false;
        this.identifier = identifier;
    }
    
    /**
     * Get an output-friendly string that identifies the dataset.
     * 
     * @return an output-friendly string that identifies the dataset.
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Compares a <b>candidate</b> item with a <b>dataset provided</b> item. Please 
     * note that the the object to test <b>must</b> be passed as first parameter,
     * while the object to test against <b>must</b> be passed as second 
     * parameter.
     * 
     * @param o1 the object to test, generated by the Distiller
     * @param o2 the reference object, provided by the training set.
     * @return 0 if o1 and o2 are equal, another number (indetermined) otherwise.
     */
    @Override
    public abstract int compare(String o1, String o2);
    
    /**
     * Loads the input documents and returns them.
     *
     * @return the input documents mapped with their identifier.
     */
    protected abstract Map<String, String> loadTestSet(); 

    /**
     * Loads the test set answers and returns them.
     *
     * @return the test set answers mapped with the identifier of the
     * document they belong to.
     */
    protected abstract Map<String, String[]> loadTestAnswers();
    
    /**
     * Loads the training documents and returns them.
     *
     * @return the training documents mapped with their identifier.
     */
    protected abstract Map<String, String> loadTrainingSet(); 

    /**
     * Loads the training set expected answers and returns them.
     *
     * @return the training set answers mapped with the identifier of the
     * document they belong to.
     */
    protected abstract Map<String, String[]> loadTrainingAnswers();

    /**
     * Get the test set documents for the dataset.
     * 
     * @return the test set for the dataset.
     */
    public Map<String, String> getTestSet() {
        return testDocuments;
    }

    /**
     * Get the test set results for the dataset.
     * 
     * @return the expected results on the test set of the dataset.
     */
    public Map<String, String[]> getTestAnswers() {
        return testAnswers;
    }
    
    /**
     * Get the training set documents for the dataset.
     * 
     * @return the training set for the dataset.
     */
    public Map<String, String> getTrainingSet() {
        return trainingDocuments;
    }

    /**
     * Get the training set results for the dataset.
     * 
     * @return the expected results on the training set of the dataset.
     */
    public Map<String, String[]> getTrainingAnswers() {
        return trainingAnswers;
    }
    
    /**
     * Gets the dataset status: true if the data has already been loaded,
     * false otherwise.
     * 
     * @return the dataset status.
     */
    public boolean isLoaded() {
        return isLoaded;
    }
    
    /**
     * Sets the dataset status.
     * 
     * @param isLoaded the dataset status.
     */
    private void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
    
    /**
     * Load the dataset. 
     */
    public void load() {
        trainingDocuments = loadTrainingSet();
        testDocuments = loadTestSet();
        trainingAnswers = loadTrainingAnswers();
        testAnswers = loadTestAnswers();
        
        setLoaded(true);
    }

    /**
     * Get the folder that contains the training set.
     *
     * @return the folder that contains the training set.
     */
    public abstract String getTrainingFolder();

    /**
     * Get the folder that contains the test set.
     *
     * @return the folder that contains the test set.
     */
    public abstract String getTestFolder();
    
}
