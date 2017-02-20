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
package it.uniud.ailab.dcore.utils;

import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Token;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the data required specifically for Arabic KPE processes.
 * 
 * @author Muhammad Helmy
 */
public class ArabicDocComponents extends Gram{
    public static final String ARABICDOCCOMPONENTS = "ARABICDOCCOMPONENTS";
    /**
     * originalText is the actual text of the document which is stored in its file.
     */
    private String originalText;
    /**
     * preProcessedText is the original text after removing multiple spaces, lines, punctuation marks, ... etc
     */
    private String preProcessedText;
    /**
     * segmentedText is the preProcessedText after removing Arabic diacritics and splitting it into atomic tokens
     */
    private String segmentedText;
    /**
     * parsedText is the segmented text after applying the parsing operations to get the various type of phrases contained in the text.
     */
    private String parsedText;
    /**
     * taggedText is the segmented text after annotating each atomic token with its POS-tag
     */
    private String taggedText;
    /**
     * NPs: contains all the legal noun phrases found by the parser in the text
     */    
    private ArrayList<Phrase> NPs;
    public ArabicDocComponents(String identifier, List<Token> sequence, String surface, String type) {
        super(identifier, sequence, surface,ARABICDOCCOMPONENTS);
        NPs = new ArrayList<Phrase>();
    }    

    /**
     * Get {@see #originalText}
     * @return  the originalText
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Set {@see #originalText}.
     * @param originalText 
     */
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    /**
     * Get {@see #preProcessedText}
     * @return the preProcessedText
     */
    public String getPreProcessedText() {
        return preProcessedText;
    }

    /**
     * Set {@see #preProcessedText}.
     * @param preProcessedText the preProcessedText to set
     */
    public void setPreProcessedText(String preProcessedText) {
        this.preProcessedText = preProcessedText;
    }

    /**
     * Get {@see #parsedText}
     * @return the parsedText
     */
    public String getParsedText() {
        return parsedText;
    }

    /**
     * Set {@see #parsedText}.
     * @param parsedText the parsedText to set
     */
    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    /**
     * Get {@see #taggedText}
     * @return the taggedText
     */
    public String getTaggedText() {
        return taggedText;
    }

    /**
     * Set {@see #taggedText}.
     * @param taggedText the taggedText to set
     */
    public void setTaggedText(String taggedText) {
        this.taggedText = taggedText;
    }

    /**
     * Get {@see #segmentedText}
     * @return the segmentedText
     */
    public String getSegmentedText() {
        return segmentedText;
    }

    /**
     * Set {@see #segmentedText}.
     * @param segmentedText the segmentedText to set
     */
    public void setSegmentedText(String segmentedText) {
        this.segmentedText = segmentedText;
    }

    /**
     * Get {@see #NPs}
     * @return the NPs
     */
    public ArrayList<Phrase> getNPs() {
        return NPs;
    }

    /**
     * Set {@see #NPs}.
     * @param NPs the NPs to set
     */
    public void setNPs(ArrayList<Phrase> NPs) {
        this.NPs = NPs;
    }
}