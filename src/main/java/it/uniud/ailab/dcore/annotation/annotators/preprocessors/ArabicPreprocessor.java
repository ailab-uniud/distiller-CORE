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
 *
 * @author Muhammad Helmy
 */
public class ArabicPreprocessor extends GenericPreprocessor{
    ArabicDocComponents arDocComps = new ArabicDocComponents(ArabicDocComponents.ARABICDOCCOMPONENTS, null, null, ArabicDocComponents.ARABICDOCCOMPONENTS);
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        ArabicDocProcessing.init();
        arDocComps.originalText = blackboard.getRawText();
        arDocComps.preProcessedText = ArabicDocProcessing.preProcess(arDocComps.originalText);   
        arDocComps.segmentedText = ArabicDocProcessing.segmentText(arDocComps.preProcessedText);
        arDocComps.segmentedText = ArabicDocProcessing.normalizeAlefAndYa(arDocComps.segmentedText);
        arDocComps.taggedText = ArabicDocProcessing.POSTageText(arDocComps.segmentedText);
        try {
            generateAllNPs();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ArabicPreprocessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        blackboard.addGram(arDocComps);
        blackboard.applyPreprocess(arDocComps.segmentedText);        
    }
    private void generateAllNPs() throws UnsupportedEncodingException{
        String[] phrases = arDocComps.taggedText.split("(\\S+/PUNC)|([^\\p{InArabic}+]/[A-Z]+)"); 
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
        arDocComps.parsedText = ArabicDocProcessing.parseText(phrasesText);
        System.err.println(arDocComps.parsedText);
        System.exit(0);
        getLegalPhrases(KP.KPTYPE_NP, "VP");
        getLegalPhrases(KP.KPTYPE_SENT, "VP|" + ArabicPOSCategories.CONNECTOR);
        getLegalPhrases(KP.KPTYPE_FRAG, "VP|" + ArabicPOSCategories.CONNECTOR);        
    }
    private void getLegalPhrases(String phraseType, String notAllowedIn){
        int nextPhraseTypeIndex = arDocComps.parsedText.indexOf("(" + phraseType);
        while(nextPhraseTypeIndex!=-1){
            int LPCount = 1;
            int phraseEnd = nextPhraseTypeIndex + phraseType.length() + 1;
            while(LPCount!=0 &&  phraseEnd<arDocComps.parsedText.length()){
                if(arDocComps.parsedText.charAt(phraseEnd)==')')
                    LPCount--;
                else if(arDocComps.parsedText.charAt(phraseEnd)=='(')
                    LPCount++;
                phraseEnd++;
            }
            if(arDocComps.parsedText.substring(nextPhraseTypeIndex, phraseEnd).matches("(" + notAllowedIn + ")"))
                continue;
            //if NP text contains Verbs or other things?
            String phraseArabicText = getArabicText(arDocComps.parsedText.substring(nextPhraseTypeIndex, phraseEnd));
            Phrase p = new Phrase(phraseArabicText, phraseType);
            int index = arDocComps.NPs.indexOf(p);
            if(index == -1)
                arDocComps.NPs.add(p);                
            else{
                if(phraseType.equals(KP.KPTYPE_NP))
                    arDocComps.NPs.get(index).type = phraseType;
            }
            //System.out.println(p.text + " : " + p.type);
            nextPhraseTypeIndex = arDocComps.parsedText.indexOf("(" + phraseType, nextPhraseTypeIndex + phraseType.length() + 1);
        }
    }
    private String getArabicText(String text){
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(text);
        String arabicText = "";
        while (matcher.find())
            arabicText += matcher.group() + " ";
        return arabicText.trim();
    }
}
