/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */
package it.uniud.ailab.dcore;

/**
 *
 * @author Marco
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
