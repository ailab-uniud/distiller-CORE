/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
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
package it.uniud.ailab.dcore;

/**
 * A class that summarizes the relevant output of the distiller in a simple, 
 * easy to navigate and JSON-ready structure.
 *
 * @author Marco Basaldella
 */
public class DistilledOutput {
    
    private boolean extractionCompleted;
    private String errorMessage;
    
    private DetectedGram[] grams;
    private InferredConcept[] relatedConcepts;
    private InferredConcept[] hypernyms;
    
    private String originalText;
    private String detectedLanguage;

    public boolean isExtractionCompleted() {
        return extractionCompleted;
    }

    public void setExtractionCompleted(boolean extractionCompleted) {
        this.extractionCompleted = extractionCompleted;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public DetectedGram[] getGrams() {
        return grams;
    }
    
    public void initializeGrams(int size) {
        this.grams = new DetectedGram[size];
        for (int i = 0; i < grams.length; i++)
            grams[i] = new DetectedGram();
    }

    public void setGrams(DetectedGram[] grams) {
        this.grams = grams;
    }

    public InferredConcept[] getRelatedConcepts() {
        return relatedConcepts;
    }
    
    public void initializeRelatedConcepts(int size) {
        this.relatedConcepts = new InferredConcept[size];
        for (int i = 0; i < relatedConcepts.length; i++)
            relatedConcepts[i] = new InferredConcept();
    }

    public void setRelatedConcepts(InferredConcept[] relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }
    
    
    public void initializeHypernyms(int size) {
        this.hypernyms = new InferredConcept[size];
        for (int i = 0; i < hypernyms.length; i++)
            hypernyms[i] = new InferredConcept();
    }

    public InferredConcept[] getHypernyms() {
        return hypernyms;
    }

    public void setHypernyms(InferredConcept[] hypernyms) {
        this.hypernyms = hypernyms;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(String detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }
    
    
    public class DetectedGram {
        private String surface;
        private String conceptName;
        private String conceptPath;
        private double keyphraseness;
        
        public String getSurface() {
            return surface;
        }

        public void setSurface(String surface) {
            this.surface = surface;
        }

        public String getConceptName() {
            return conceptName;
        }

        public void setConceptName(String conceptName) {
            this.conceptName = conceptName;
        }

        public String getConceptPath() {
            return conceptPath;
        }

        public void setConceptPath(String conceptPath) {
            this.conceptPath = conceptPath;
        }

        public double getKeyphraseness() {
            return keyphraseness;
        }

        public void setKeyphraseness(double keyphraseness) {
            this.keyphraseness = keyphraseness;
        }
    }
    
    public class InferredConcept {
        private String concept;
        private String conceptPath;
        private double score;

        public String getConceptPath() {
            return conceptPath;
        }

        public void setConceptPath(String conceptPath) {
            this.conceptPath = conceptPath;
        }
        

        public String getConcept() {
            return concept;
        }

        public void setConcept(String concept) {
            this.concept = concept;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }        
    }
    
}
