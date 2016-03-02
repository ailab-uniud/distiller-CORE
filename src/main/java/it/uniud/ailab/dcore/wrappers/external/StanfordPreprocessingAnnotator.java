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
package it.uniud.ailab.dcore.wrappers.external;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.data.Dictionaries;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A preprocessing annotator for the English language developed using the
 * Stanford Core NLP library. This annotator detect coreferences in the
 * document: particularly it finds out all the pronouns anaphors, so to
 * substitue them with the mention they refer to. This annotator supports only
 * the English language.
 *
 * @author Giorgia Chiaradia
 */
public class StanfordPreprocessingAnnotator implements Annotator {

    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized for
     * re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static StanfordCoreNLP pipeline = null;

    /**
     * The languages that the n-gram generator will process and their POS
     * pattern database paths.
     */
    private Map<Locale, String> posDatabasePaths;

    /**
     * The regular expressions used to check if the gram has a valid pos
     * pattern.
     */
    private Map<String, Integer> validPosPatterns;
    private Annotation document;

    public StanfordPreprocessingAnnotator() {
        validPosPatterns = new HashMap<>();
        posDatabasePaths = new HashMap<>();
        posDatabasePaths.put(Locale.ENGLISH,
                getClass().getClassLoader().
                getResource("anaphora/anaphora.json").getFile());
    }

    /**
     * Annotate the document by splitting the document, tokenizing it,
     * performing PoS tagging and Named Entity Recognition using the Stanford
     * Core NLP tools.
     *
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        try {
            loadDatabase(component.getLanguage());
        } catch (IOException | ParseException ex) {
            Logger.getLogger(StanfordPreprocessingAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String text = component.getText();
        text = text.replaceAll("\\[.*?\\]", "");
        
        document = new Annotation(text);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,dcoref");
        pipeline = new StanfordCoreNLP(props);
        // run all Annotators on this text
        pipeline.annotate(document);
       
        for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
            CorefChain.CorefMention cm = cc.getRepresentativeMention();
            
            Collection<Set<CorefChain.CorefMention>> mentionMap
                    = cc.getMentionMap().values();

            for (Set<CorefChain.CorefMention> mentions : mentionMap) {
                if (!mentions.isEmpty()) {
                    for (CorefChain.CorefMention reference : mentions) {
                        
                            if(reference.mentionSpan.equalsIgnoreCase(cm.mentionSpan))
                                continue;
                            
                            substituteAnaphor(reference, cm.mentionSpan);
                    }
                }
            }
        }
        
        String newText = makePreprocessedText();
        component.setPreprocessedText(newText);
    }

    private void substituteAnaphor(CorefChain.CorefMention reference, String mention) {

        String anaphor = reference.mentionSpan.toLowerCase();
        Integer type = 0;
        for (String ptn : validPosPatterns.keySet()) {
            if (anaphor.matches(ptn)) {
                type = validPosPatterns.get(ptn);
                System.out.println(ptn + " for " + anaphor + " - type " + type);
            }
        }

        if(type > 0){//is a pronominal anaphor
            //get the list of tokens which form the sentence in which the anaphor appear
            List<CoreLabel> tokens = document.get(SentencesAnnotation.class)
                    .get(reference.sentNum - 1).get(TokensAnnotation.class);

            //if the reference has more than one token (it is not the case of pronouns 
            //but it can be useful for other types of anaphora)
            if (reference.startIndex - 1 < reference.endIndex - 1) {
                for (int i = reference.startIndex - 1; i < reference.endIndex - 1; i++) {
                    if (i == reference.endIndex - 2) {
                        substituteAnaphorByType(tokens.get(i), mention, type);
                    } else {
                        tokens.get(i).setWord("");
                    }
                }
            } else {
                substituteAnaphorByType(tokens.get(reference.endIndex - 2), mention, type);
            }
        }
    }

    private void substituteAnaphorByType(CoreLabel token, String mention, Integer type) {
        switch (type) {
            case 1: //personal pronouns (I, you, he,..., him, them,...)
                token.setWord(mention);
                break;
            case 2: //possessive pronouns (its, his, ...)
                token.setWord(mention + "'s");
                break;
            case 3: //reflexive pronouns (myself,...)
                token.setWord(mention);
                break;
            case 4: //relative pronouns (who,which, ...)
                token.setWord(mention);
                break;
            case 5: //demonstrative pronouns (this, ...)
                token.setWord(mention);
                break;
        }
        
    }

    private void loadDatabase(Locale lang) throws IOException, ParseException {
        // Get the POS pattern file and parse it.

        InputStreamReader is
                = FileSystem.getInputStreamReaderFromPath(posDatabasePaths.get(lang));

        BufferedReader reader = new BufferedReader(is);
        Object obj = (new JSONParser()).parse(reader);
        JSONObject fileblock = (JSONObject) obj;
        JSONArray pagesBlock = (JSONArray) fileblock.get("anaphora");

        Iterator<JSONObject> iterator = pagesBlock.iterator();
        JSONObject anBlock = null;
        while (iterator.hasNext()) {
            anBlock = (iterator.next());
        }

        JSONArray patternBlock = (JSONArray) anBlock.get("anaphoricPP");

        Iterator<JSONObject> patternIterator = patternBlock.iterator();

        // put the patterns in the hashmap
        while (patternIterator.hasNext()) {
            JSONObject pattern = (patternIterator.next());
            String POSpattern = (String) pattern.get("pattern");
            String t = (String) pattern.get("type");
            Integer type = Integer.valueOf(t);
            validPosPatterns.put(POSpattern, type);
        }
    }

    private String makePreprocessedText() {
        String newText = "";
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap s : sentences) {
            String sentence = s.toString();

            for (CoreLabel token : s.get(TokensAnnotation.class)) {

                if (!token.word().equalsIgnoreCase(token.originalText())
                        && token.originalText().matches("^[a-zA-Z0-9]+$")) {

                    sentence = sentence.replaceFirst("(\\s|^)(" + token.originalText() + ")(\\s|$)", " " + token.word() + " ");
                }

            }
            newText = newText + " " + sentence;
        }
        return newText;
    }
}
