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

package it.uniud.ailab.dcore.wrappers.external;

import edu.stanford.nlp.international.arabic.process.ArabicTokenizer;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;
import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
//import gpl.pierrick.brihaye.aramorph.AraMorph;
//import gpl.pierrick.brihaye.aramorph.Solution;
import it.uniud.ailab.dcore.engine.Blackboard;

/**
 * A bootstrapper annotator for the English language developed using the 
 * Stanford Core NLP library. The annotator splits the document, performs PoS
 * tagging and Named Entity Recognition. This annotator supports only the 
 * English language.
 *
 * @author Marco Basaldella
 */
public class ArabicStanfordBootstrapper implements Annotator {
    
    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized
     * for re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static MaxentTagger tagger = null;
//    private static AraMorph am;
    /**
     * Annotate the document by splitting the document, performing PoS tagging  
     * and Named Entity Recognition using the Stanford Core NLP tools.
     * 
     * @param component the component to annotate.
     */
    @Override
    public void annotate(Blackboard blackBoard ,DocumentComponent component) {
        if (tagger == null) {
            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
            tagger = new MaxentTagger("D:\\AILab\\stanford-postagger-full-2015-04-20\\models\\arabic.tagger");
            /*Properties props = new Properties();
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
            pipeline = new StanfordCoreNLP(props);*/
        }
//        if(am == null)
//            am = new AraMorph();
        // read some text in the text variable
        String text = component.getText();

        // create an empty Annotation just with the given text
        //Annotation document = new Annotation(text);

        // run all Annotators on this text
        //pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        //List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        
            List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new StringReader(text), ArabicTokenizer.ArabicTokenizerFactory.newTokenizerFactory());
            for (List<HasWord> stanfordSentence : sentences) {
                String stanfordSentenceTxt = "";
                for(HasWord word: stanfordSentence)
                    stanfordSentenceTxt += word.toString() + " ";
                Sentence distilledSentence = new Sentence(stanfordSentenceTxt); 
                //System.out.println("xxx " + distilledSentence.toString());
                distilledSentence.setLanguage(new Locale("ar"));//Locale.ENGLISH);
                // traversing the words in the current sentence
                // a CoreLabel is a CoreMap with additional token-specific methods
                List<TaggedWord> tagedStanfordSentence = tagger.tagSentence(stanfordSentence);
                for (TaggedWord token : tagedStanfordSentence/*.get(TokensAnnotation.class)*/) {


                    // this is the text of the token
                    String word = token.word();//toString();//get(TextAnnotation.class);
                    Token t = new Token(word);                

                    // this is the POS tag of the token                
                    t.setPoS(token.tag());//(PartOfSpeechAnnotation.class));
                    //System.out.println(t.getText() + " : " + t.getPoS());
//                    if(am.analyzeToken(word))
//                        t.setStem(((Solution)am.getWordSolutions(word).iterator().next()).getLemma());
//                    else
                        t.setStem(word);
                    //System.out.println(t.getStem()); 
                    // this is the Stem
                    //t.setStem(token.get(LemmaAnnotation.class));

                    // this is the NER label of the token
                    //String ne = token.get(NamedEntityTagAnnotation.class);
                    //t.addAnnotation(
                      //  new org.uniud.dcore.persistence.Annotation("StanfordNER",word,ne));

                    distilledSentence.addToken(t);
                }

                ((DocumentComposite) component).addComponent(distilledSentence);
                        
            }
        
    }

}
