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

import java.util.Map;

/**
 * A generic dataset loader.
 * 
 * @author Marco Basaldella
 */
public abstract class GenericDataset {
    
    /**
     * The path where the evaluator will find the input documents and the
     * gold standard results.
     */
    protected final String datasetPath;
    
    /**
     * The input documents. Each document has an identifier and a content.
     */
    private Map<String, String> inputDocuments;
    /**
     * The keyphrases for the input documents. Each list of gold results
     * is paired to the identifier of the corresponding document.
     */
    private Map<String, String[]> goldResults;
    
    /**
     * A value that indicates wheter if the documents have already been loaded
     * or not.
     */
    private boolean isLoaded;
    
    /**
     * Create a concrete dataset that will contain the data contained in the 
     * specified path.
     * 
     * @param datasetPath 
     */
    public GenericDataset(String datasetPath) {
        this.datasetPath = datasetPath;
        this.isLoaded = false;
    }
    
    /**
     * Loads the input documents and returns them.
     *
     * @return the input documents mapped with their identifier.
     */
    protected abstract Map<String, String> loadInputDocuments(); 

    /**
     * Loads the gold standard result and returns them.
     *
     * @return the gold standard result mapped with the identifier of the
     * document they belong to.
     */
    protected abstract Map<String, String[]> loadGoldResults();

    /**
     * Get the test set documents for the dataset.
     * 
     * @return the test set for the dataset.
     */
    public Map<String, String> getInputDocuments() {
        return inputDocuments;
    }

    /**
     * Get the test set results for the dataset.
     * 
     * @return the expected results on the test set of the dataset.
     */
    public Map<String, String[]> getGoldResults() {
        return goldResults;
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
    protected void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }
    
    public void load() {
        loadInputDocuments();
        loadGoldResults();
    }
    
}
