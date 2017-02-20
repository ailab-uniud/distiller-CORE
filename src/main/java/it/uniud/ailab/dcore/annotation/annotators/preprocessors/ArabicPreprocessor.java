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
package it.uniud.ailab.dcore.annotation.annotators.preprocessors;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.annotators.GenericPreprocessor;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.utils.ArabicDocComponents;
import it.uniud.ailab.dcore.utils.ArabicDocProcessing;
import it.uniud.ailab.dcore.utils.ArabicPOSCategories;
import it.uniud.ailab.dcore.utils.KP;
import it.uniud.ailab.dcore.utils.Phrase;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * The class encapsulates the preprocessing tasks which are performed on the text before extracting candidate keyphrases (CKPs).
 * Preprocessing prepares the text for the next operations of Arabic KPE pipeline.
 * In addition, it creates all specific linguistic structures required for Arabic KPE process like noun phrases  and their components.
 * 
 * 
 * @author Muhammad Helmy
 */
public class ArabicPreprocessor extends GenericPreprocessor{
    /** 
     * arDocComps is a container for all specific structures and text forms which are necessary for the next steps of Arabic KPE.
     * After building arDocComps, it will be added to the {@link Blackboard} as a gram.
     */
    ArabicDocComponents arDocComps = new ArabicDocComponents(ArabicDocComponents.ARABICDOCCOMPONENTS, null, null, ArabicDocComponents.ARABICDOCCOMPONENTS);
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        ArabicDocProcessing.init();
        arDocComps.setOriginalText(blackboard.getRawText());
        arDocComps.setPreProcessedText(ArabicDocProcessing.preProcess(arDocComps.getOriginalText()));
        //Arabic diacritics are removed by Stanford Arabic Segmenter using removeDiacritics option
        arDocComps.setSegmentedText(ArabicDocProcessing.segmentText(arDocComps.getPreProcessedText()));
        arDocComps.setSegmentedText(ArabicDocProcessing.normalizeAlefAndYa(arDocComps.getSegmentedText()));
        arDocComps.setTaggedText(ArabicDocProcessing.POSTageText(arDocComps.getSegmentedText()));
        try {
            generateAllNPs();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ArabicPreprocessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        blackboard.addGram(arDocComps);        
        blackboard.applyPreprocess(arDocComps.getSegmentedText());        
    }
    /**
     * Creates a list of all noun phrases found in the text.
     * The precondition of this function is that all text forms are created and stored in {@link #arDocComps}.
     * Finally, the noun phrases are kept in {@link arDocComps#NPs}
     * 
     * @throws UnsupportedEncodingException 
     */
    private void generateAllNPs() throws UnsupportedEncodingException{
        //split the tagged text into sentences using the tag of PUNC which represents a punctuation symbol
        String[] phrases = arDocComps.getTaggedText().split("(\\S+/PUNC)|([^\\p{InArabic}+]/[A-Z]+)"); 
        //pattern will be used to split phrases into more atomic sentences using verbs as sentence bounderies
        Pattern pattern = Pattern.compile("\\S+/(" + ArabicPOSCategories.VERB + ")");
        ArrayList<String> atomicPhrases = new ArrayList<String>();
        for(String phrase : phrases){
            if(phrase.trim().length()<5)
                continue;
            String[] verbPhrases = phrase.split("\\S+/(" + ArabicPOSCategories.VERB + ")");
            Matcher matcher = pattern.matcher(phrase);
            int i = 1;
            //take care for first verb in the document start
            //verbPhrases[0] = verbPhrases[0].replaceAll("/\\S+", "");
            while (matcher.find()){
                verbPhrases[i] = matcher.group() + " " +  verbPhrases[i];
                i++;
            }
            for(i=0; i<verbPhrases.length; i++){
                if((verbPhrases[i]+" ").split((ArabicPOSCategories.NOUN + "|"  + ArabicPOSCategories.ADJ )).length<3)
                    verbPhrases[i] = "";
                verbPhrases[i] = verbPhrases[i].replaceAll("/\\S+", "");
            }        
            atomicPhrases.addAll(Arrays.asList(verbPhrases));
        }        
        String phrasesText = "";
        for(int i=0; i<atomicPhrases.size(); i++)
          if(atomicPhrases.get(i).length()!=0)
              phrasesText += atomicPhrases.get(i) + ".\n";
        arDocComps.setParsedText(ArabicDocProcessing.parseText(phrasesText));
        //System.err.println(arDocComps.parsedText);
        //System.exit(0);
        getLegalPhrases(KP.KPTYPE_NP, "VP");
        getLegalPhrases(KP.KPTYPE_SENT, "VP|" + ArabicPOSCategories.CONNECTOR);
        getLegalPhrases(KP.KPTYPE_FRAG, "VP|" + ArabicPOSCategories.CONNECTOR);        
    }
    /**
     * Generates the allowed forms of Arabic noun phrases from the parsed text and store them in {@link arDocComps#NPs}.
     * 
     * @param phraseType represents the type of noun phrase that will be generated from the parsed text.
     * @param notAllowedIn involves the tags that are not allowed to appear within the legal phrase.
     */
    private void getLegalPhrases(String phraseType, String notAllowedIn){
        int nextPhraseTypeIndex = arDocComps.getParsedText().indexOf("(" + phraseType);
        while(nextPhraseTypeIndex!=-1){
            int LPCount = 1;
            int phraseEnd = nextPhraseTypeIndex + phraseType.length() + 1;
            while(LPCount!=0 &&  phraseEnd<arDocComps.getParsedText().length()){
                if(arDocComps.getParsedText().charAt(phraseEnd)==')')
                    LPCount--;
                else if(arDocComps.getParsedText().charAt(phraseEnd)=='(')
                    LPCount++;
                phraseEnd++;
            }
            if(arDocComps.getParsedText().substring(nextPhraseTypeIndex, phraseEnd).matches("(" + notAllowedIn + ")"))
                continue;
            //if NP text contains Verbs or other things?
            String phraseArabicText = getArabicText(arDocComps.getParsedText().substring(nextPhraseTypeIndex, phraseEnd));
            Phrase p = new Phrase(phraseArabicText, phraseType);
            int index = arDocComps.getNPs().indexOf(p);
            if(index == -1)
                arDocComps.getNPs().add(p);                
            else{
                if(phraseType.equals(KP.KPTYPE_NP))
                    arDocComps.getNPs().get(index).type = phraseType;
            }
            //System.out.println(p.text + " : " + p.type);
            nextPhraseTypeIndex = arDocComps.getParsedText().indexOf("(" + phraseType, nextPhraseTypeIndex + phraseType.length() + 1);
        }
    }
    /**
     * Generates the Arabic text contained in a given input text.
     * @param text the text from which the Arabic text will be extracted.
     * @return a string containing Arabic text only.
     */
    private String getArabicText(String text){
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(text);
        String arabicText = "";
        while (matcher.find())
            arabicText += matcher.group() + " ";
        return arabicText.trim();
    }
}
