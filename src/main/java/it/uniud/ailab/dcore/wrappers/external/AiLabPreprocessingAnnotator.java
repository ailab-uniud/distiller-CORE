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
package it.uniud.ailab.dcore.wrappers.external;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.utils.FileSystem;
import it.uniud.ailab.dcore.utils.GenderUtils;
import it.uniud.ailab.dcore.utils.GenderUtils.Gender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A bootstrapper annotator for the English language developed using the
 * Stanford Core NLP library for parsing and uniudAiLab pronominal resolution
 * algorithm. This annotator splits the document, tokenizes it and performs PoS
 * tagging and Named Entity Recognition togethere with parsing. Based on
 * relations in parsing trees and on some rules by Lappin, the annotator finds
 * out all the pronouns (every type - possessive, relative, personal and
 * demonstrative) and substitute them with the preferred candidate. This
 * annotator supports only the English language.
 *
 * @author Giorgia Chiaradia
 */
public class AiLabPreprocessingAnnotator implements Annotator {

    /**
     * The Stanford NLP pipeline. The field is marked static to be optimized for
     * re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator.
     */
    private static StanfordCoreNLP pipeline = null;

    /**
     * The text which will be preprocessed by the annotator. First it is created
     * using a method to correctly identify sections of the document in the
     * blackboard; then it is parsed to find out and substitute all the
     * pronouns.
     */
    private String txt = "";

    private List<Pronoun> prononus = new ArrayList<>();
    /**
     * The languages that the n-gram generator will process and their POS
     * pattern database paths.
     */
    private String posDatabasePathPl = "anaphora/pleonasticPr.json";
    private String posDatabasePathPr = "anaphora/pronouns.json";

    private Annotation document;
    private GenderUtils genderUtil = new GenderUtils();

    /**
     * *
     * Regexes for checking pleonastic pronouns.
     */
    private String modalAdjPt; //with modal adjs
    private String cognVbPt; //with past of cognitive verbs
    private String partVbPtA;
    private String partVbPt2A;
    private String partVbPtB;
    private String partVbPt2B;
    private String structureMC5; //with to be verb: these must be use to check 
    private String structureMC4; //the structure of the sentence before cogn or modal
    private String structureMC3;
    private String structureMC2;
    private String structureMC1;

    /**
     * *
     * Regexes for checking pronouns.
     */
    private String theyPt;
    private String shePt;
    private String hePt;
    private String itPt;

    private boolean onlyPronouns;

    /**
     * Enumerator for number.
     */
    public enum Num {
        SINGULAR,
        PLURAL;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        try {

            genderUtil.createGendersDictionaries();
            loadDatabasePleonastic();
            loadDatabasePronouns();
            getSectionedDoc(blackboard.getTextLines());
            String preprocText = parseDoc();
            preprocText = preprocText.replaceAll("-LRB-", "(");
            preprocText = preprocText.replaceAll("-RRB-", ")");
            preprocText = preprocText.replaceAll("-LSB-", "[");
            preprocText = preprocText.replaceAll("-RSB-", "]");
            component.setPreprocessedText(preprocText);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(AiLabPreprocessingAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Load all the regex to find out pleonastic it, which must not be
     * substitute.
     *
     * @throws IOException
     * @throws ParseException
     */
    private void loadDatabasePleonastic() throws IOException, ParseException {

        InputStreamReader is
                = FileSystem.getInputStreamReaderFromPath(getClass().getClassLoader().
                        getResource(posDatabasePathPl).getFile());

        BufferedReader reader = new BufferedReader(is);
        Object obj = (new JSONParser()).parse(reader);
        JSONObject fileblock = (JSONObject) obj;

        JSONObject s1 = (JSONObject) fileblock.get("structureMC1");
        structureMC1 = (String) s1.get("pattern");

        JSONObject s2 = (JSONObject) fileblock.get("structureMC2");
        structureMC2 = (String) s2.get("pattern");

        JSONObject s3 = (JSONObject) fileblock.get("structureMC3");
        structureMC3 = (String) s3.get("pattern");

        JSONObject s4 = (JSONObject) fileblock.get("structureMC4");
        structureMC4 = (String) s4.get("pattern");

        JSONObject s5 = (JSONObject) fileblock.get("structureMC5");
        structureMC5 = (String) s5.get("pattern");

        JSONObject modal = (JSONObject) fileblock.get("modalAdj");
        modalAdjPt = (String) modal.get("pattern");

        JSONObject cogn = (JSONObject) fileblock.get("cognVerb");
        cognVbPt = (String) cogn.get("pattern");

        JSONObject partVbA = (JSONObject) fileblock.get("partVerbA");
        partVbPtA = (String) partVbA.get("pattern");

        JSONObject partVb2A = (JSONObject) fileblock.get("partVerb2A");
        partVbPt2A = (String) partVb2A.get("pattern");

        JSONObject partVbB = (JSONObject) fileblock.get("partVerbB");
        partVbPtB = (String) partVbB.get("pattern");

        JSONObject partVb2B = (JSONObject) fileblock.get("partVerb2B");
        partVbPt2B = (String) partVb2B.get("pattern");
    }

    private void loadDatabasePronouns() throws IOException, ParseException {

        InputStreamReader is
                = FileSystem.getInputStreamReaderFromPath(getClass().getClassLoader().
                        getResource(posDatabasePathPr).getFile());

        BufferedReader reader = new BufferedReader(is);
        Object obj = (new JSONParser()).parse(reader);
        JSONObject fileblock = (JSONObject) obj;

        JSONObject s1 = (JSONObject) fileblock.get("it");
        itPt = (String) s1.get("pattern");

        JSONObject s2 = (JSONObject) fileblock.get("he");
        hePt = (String) s2.get("pattern");

        JSONObject s3 = (JSONObject) fileblock.get("she");
        shePt = (String) s3.get("pattern");

        JSONObject s4 = (JSONObject) fileblock.get("they");
        theyPt = (String) s4.get("pattern");
    }

    /**
     * Try to correctly read the plain text file: use new line instead of space
     * if the line: - contains only caps text; - start with a number + dot.
     * Endly it set the text variable with the rightly formatted text.
     *
     * @param lines : the lines composing the document.
     * @throws IOException
     */
    private void getSectionedDoc(List<String> lines) throws IOException {

        //index for the list of lines
        int i = 0;

        //it says if lines must be added to section or the main text
        boolean inSect = true;

        //collect all the lines of a section
        String section = "";

        while (i < lines.size()) {
            String currentLine = lines.get(i);
            String nextLine = "";

            //the line is the title of the abstract section
            if (currentLine.matches("^ABSTRACT$|^Abstract$")) {
                String preProSection = section; //create a new section ...
                txt = txt + preProSection;// ...and add the new one to the whole text
                section = ""; //now clear the section
                txt = txt + "\n" + currentLine + ".\n"; //add the current line (which is the title "abstract")
                i++;
                inSect = false; //turn off the insection variable

            } //the line is the title of a section, i.e. is something like 2.1 BlaBla Bla 
            //or is made of only capital letters
            else if (currentLine.matches("([0-9\\.]+|[0-9]+\\.+)(\\s[A-Z]+[a-z]*:?)+(\\s[A-Za-z]+:?)*")
                    || currentLine.matches("([0-9\\.]+|[0-9]+\\.+)(\\s[A-Z]+:?)+")) {
                String preProSection = section; //create a new section ...
                txt = txt + preProSection;// ...and add the new one to the whole text
                section = "";//now clear the section

                //if the next line is made of only capital letters, 
                //it could be the continuation of the previous one. So..
                if (i + 1 < lines.size()
                        && lines.get(i + 1).matches("^[A-Z]+$|[A-Z]+")) {
                    nextLine = lines.get(i + 1);
                    txt = txt + "\n" + currentLine + " " + nextLine + ".\n"; //we get the next line and add it to the prevoius
                    i = i + 2;

                } //if the next line is made of number plus dot plus a sentence starting with capital letter, 
                //it could be a subsection of the main section identified by the title in the previous line. So..
                else if (i + 1 < lines.size()
                        && lines.get(i + 1).matches("([0-9\\.]+|[0-9]+\\.+)(\\s[A-Z]+[a-z]*:?)+(\\s[A-Za-z]+:?)*")) {
                    nextLine = lines.get(i + 1);
                    txt = txt + "\n" + currentLine + ".\n";
                    txt = txt + "\n" + nextLine + ".\n";
                    i = i + 2;
                } else {
                    txt = txt + "\n" + currentLine + ".\n";
                    i++;
                }
                inSect = false; //turn off the insection variable
            } else {
                inSect = true; //turn on the insection variable
            }

            //if we are in a section we add every line to a section variable which 
            //collect all the lines between one title and another
            if (inSect) {
                section = section + currentLine + " ";
                i++;
            }

            //if it is the last line of the text,we add the last section to the whole text
            if (i == lines.size()) {
                txt = txt + section;
            }

        }

    }

    private String parseDoc() {

        String preprocessedText = "";
        if (pipeline == null) {
            // creates a StanfordCoreNLP object, with POS tagging, lemmatization, 
            //NER, parsing, and coreference resolution 
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
            pipeline = new StanfordCoreNLP(props);

        }
        document = new Annotation(txt);

        // run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        int i = 0;
        for (CoreMap sentence : sentences) {

            String sentStr = sentence.toString();

            SemanticGraph sentTree = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);

            substitutePRPrs(sentStr, sentTree, i);
            if (!onlyPronouns) {
                substituteDemonstrativePrs(sentStr, sentTree);
            }

            preprocessedText += preprocessedSentence(sentTree);
            i++;

        }
        return preprocessedText;
    }

    private void substituteDemonstrativePrs(String sentStr, SemanticGraph sentTree) {
        if (sentStr.matches(".*\\b(that|which)\\b.*")) {

            List<IndexedWord> itNodes = sentTree.getAllNodesByWordPattern("that|which");
            for (IndexedWord itNode : itNodes) {
                if ((itNode.word().equalsIgnoreCase("that") || itNode.word().equalsIgnoreCase("which"))
                        && !itNode.tag().matches("IN|DT")) {
                    IndexedWord precedente = sentTree.getNodeByIndex(itNode.index() - 1);
                    int j = itNode.index() - 1;
                    if (precedente.word().matches(",")) {
                        j--;
                    } else if (precedente.word().matches("\\[|\\(|\\{")) {
                        continue;
                    }
                    boolean existVb = false;
                    String expandThat = "";
                    while (j >= 1) {
                        IndexedWord node = sentTree.getNodeByIndex(j);
                        if (node.tag().matches("VB|VBP|VBZ|VBD|VBN")) {

                            existVb = true;
                            break;

                        } else if (node.tag().matches("TO|\\p{Punct}")) {
                            existVb = false;
                            break;
                        } else if (node.tag().matches("IN") && j - 1 >= 1) {
                            if (node.word().equalsIgnoreCase("of")) {

                                expandThat = node.word() + " " + expandThat;
                            } else {
                                existVb = true;
                                break;
                            }

                        } else if (node.word().matches("\\]|\\)|\\}")) {
                            while (j >= 1 && node.word().matches("\\[|\\(|\\{")) {
                                j--;
                            }
                            j--;
                        } else {
                            expandThat = node.word() + " " + expandThat;
                        }
                        j--;
                    }
                    if (existVb) {
                        itNode.setWord("; " + expandThat);
                    }

                }
            }

        }

    }

    private void substitutePRPrs(String sentStr, SemanticGraph sentTree, int i) {
        if (sentStr.matches("(^|.*\\b)" + itPt + "\\b.*")) {
            
            List<IndexedWord> itNodes = sentTree.getAllNodesByWordPattern(itPt);
            for (IndexedWord itNode : itNodes) {
                if (!checkPleonastic(sentTree, sentStr, itNode)) {
                    Pronoun p = new Pronoun(itNode, Gender.NEUTRAL, Num.SINGULAR);
                    selectCandidates(p, sentTree.getParent(itNode), sentTree, i - 1);
                    itNode.setWord(p.getProbableCandidate().word());
                }
            }
        }
        if (sentStr.matches("(^|.*\\b)" + hePt + "\\b.*")) {
            List<IndexedWord> itNodes = sentTree.getAllNodesByWordPattern(hePt);
            for (IndexedWord itNode : itNodes) {
                Pronoun p = new Pronoun(itNode, Gender.MASCULINE, Num.SINGULAR);
                selectCandidates(p, sentTree.getParent(itNode), sentTree, i - 1);
                itNode.setWord(p.getProbableCandidate().word());
            }
        }
        if (sentStr.matches("(^|.*\\b)" + shePt + "\\b.*")) {
            List<IndexedWord> itNodes = sentTree.getAllNodesByWordPattern(shePt);
            for (IndexedWord itNode : itNodes) {
                Pronoun p = new Pronoun(itNode, Gender.FEMININE, Num.SINGULAR);
                selectCandidates(p, sentTree.getParent(itNode), sentTree, i - 1);
                itNode.setWord(p.getProbableCandidate().word());
            }
        }
        if (sentStr.matches("(^|.*\\b)" + theyPt + "\\b.*")) {
            List<IndexedWord> itNodes = sentTree.getAllNodesByWordPattern(theyPt);
            for (IndexedWord itNode : itNodes) {
                Pronoun p = new Pronoun(itNode, Gender.NEUTRAL, Num.PLURAL);
                selectCandidates(p, sentTree.getParent(itNode), sentTree, i - 1);
                itNode.setWord(p.getProbableCandidate().word());
            }
        }
    }

    private String preprocessedSentence(SemanticGraph tree) {
        String preprocSent = "";
        for (int i = 1; i < tree.size(); i++) {
            preprocSent += " " + tree.getNodeByIndex(i).word();
        }
        return preprocSent + ".\n";
    }

    private void selectCandidates(Pronoun pronoun, IndexedWord dad, SemanticGraph tree, int j) {
//        System.out.println(tree.toString(SemanticGraph.OutputFormat.READABLE));

        IndexedWord node = pronoun.getNode();

        String rel = null;
        String relPr = null;
        for (GrammaticalRelation r : tree.relns(dad)) {
            rel = r.getShortName();

        }
        if (rel == null) {
            rel = GrammaticalRelation.ROOT.getShortName();
        }
        for (GrammaticalRelation r : tree.relns(node)) {
            relPr = r.getShortName();
        }
        pronoun.setRel(relPr);

        List<String> posSubs = new ArrayList<>();
        if (rel.equals(GrammaticalRelation.ROOT.getShortName())
                && (!relPr.equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName())
                && !relPr.equals(EnglishGrammaticalRelations.INDIRECT_OBJECT.getShortName()))) {
            //cerco tra i siblings se esite un nodo che ha figlio subj -> frase principale
            Collection<IndexedWord> siblings = tree.getSiblings(node);
            for (IndexedWord s : siblings) {
                Collection<IndexedWord> children = tree.descendants(s);

                for (IndexedWord c : children) {
                    String relC = "";
                    if (c.index() < node.index() - 1) {
                        for (GrammaticalRelation r : tree.relns(c)) {
                            relC = r.getShortName();
                        }

                        if (relC.equals(EnglishGrammaticalRelations.NOMINAL_SUBJECT.getShortName())
                                || relC.equals(EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT.getShortName())
                                || relC.equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName())
                                || relC.equals(EnglishGrammaticalRelations.AGENT.getShortName())) {

                            if (!c.word().equalsIgnoreCase(node.word())) {

                                evaluateCandidate(c, relC, pronoun, tree, node.index(), false);

                            }
                        }
                    }

                }
            }
            if (pronoun.getProbableCandidate().word() != null) {
                posSubs.add(pronoun.getProbableCandidate().word());
            }

        } else if (relPr.equals(EnglishGrammaticalRelations.INDIRECT_OBJECT.getShortName()) //                || relPr.equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName())
                //                || relPr.equals(EnglishGrammaticalRelations.NOUN_COMPOUND_MODIFIER.getShortName())
                ) {
            for (TypedDependency t : tree.typedDependencies()) {
                if (t.reln().toString().matches("\\b(nsubj.*|dobj|iobj)\\b") && t.dep().index() < node.index() - 1) {

                    evaluateCandidate(t.dep(), t.reln().toString(), pronoun, tree, node.index(), false);
                }

            }
            if (pronoun.getProbableCandidate().word() != null) {
                posSubs.add(pronoun.getProbableCandidate().word());
            }

        } else if (rel.equals(EnglishGrammaticalRelations.XCLAUSAL_COMPLEMENT.getShortName())
                || rel.equals(EnglishGrammaticalRelations.CLAUSAL_COMPLEMENT.getShortName())
                || rel.equals(EnglishGrammaticalRelations.ADJECTIVAL_COMPLEMENT.getShortName())) {
            for (TypedDependency t : tree.typedDependencies()) {
                if (t.reln().toString().matches("\\b(nsubj.*|dobj|iobj|nmod.*)\\b") && t.dep().index() < node.index() - 1) {
                    evaluateCandidate(t.dep(), t.reln().toString(), pronoun, tree, node.index(), true);
                }

            }
            if (pronoun.getProbableCandidate().word() != null) {
                posSubs.add(pronoun.getProbableCandidate().word());
            }
        } else if (rel.contains(EnglishGrammaticalRelations.MODIFIER.getShortName())
                || relPr.equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName()) //                || relPr.contains(EnglishGrammaticalRelations.MODIFIER.getShortName())
                ) {
            for (TypedDependency t : tree.typedDependencies()) {
                if (t.reln().toString().matches("\\b(nsubj.*|dobj|iobj|nmod.*)\\b") && t.dep().index() < node.index() - 1) {
                    evaluateCandidate(t.dep(), t.reln().toString(), pronoun, tree, node.index(), true);
                }

            }
            if (pronoun.getProbableCandidate().word() != null) {
                posSubs.add(pronoun.getProbableCandidate().word());
            }

        } else if (rel.equals(EnglishGrammaticalRelations.ADV_CLAUSE_MODIFIER.getShortName())) {
            for (TypedDependency t : tree.typedDependencies()) {
                if (t.reln().toString().matches("\\b(nsubj.*|dobj|iobj|nmod.*)\\b") && t.dep().index() < node.index() - 1) {

                    evaluateCandidate(t.dep(), t.reln().toString(), pronoun, tree, node.index(), false);
                }

            }
            if (pronoun.getProbableCandidate().word() != null) {
                posSubs.add(pronoun.getProbableCandidate().word());
            }
        } else {
            //cerco tra i siblings del padre -> frase dipendente
            while (dad != null && posSubs.isEmpty()) {

                if (!tree.getSiblings(dad).isEmpty()) {

                    for (TypedDependency t : tree.typedDependencies()) {
                        if (t.dep().index() < node.index() - 1
                                && t.reln().toString().matches("\\b(nsubj.*|dobj|iobj|nmod.*)\\b")) {
                           
                            evaluateCandidate(t.dep(), t.reln().toString(), pronoun, tree, node.index(), false);

                        }
                    }
                }

                IndexedWord nonno = tree.getParent(dad);
                dad = nonno;
                if (pronoun.getProbableCandidate().word() != null) {
                    posSubs.add(pronoun.getProbableCandidate().word());
                }
            }
        }
        if (!posSubs.isEmpty() && pronoun.getProbableCandidate().word().toLowerCase().equals("that")) {
            posSubs.clear();
            IndexedWord prob = tree.getNodeByIndex(pronoun.getProbableCandidate().index() + 1);
            Set<IndexedWord> thatW = tree.descendants(prob);
            String wordProb = prob.word() + " ";
            for (IndexedWord w : thatW) {
                if (!w.word().equals("that")) {
                    wordProb = wordProb + w.word() + " ";
                }
            }
        } else if (posSubs.isEmpty() || pronoun.getProbableCandidate().tag().matches("DT|PRP.*")) {
            posSubs.clear();
            pronoun.resetMax();
            while ((posSubs.isEmpty() || pronoun.getProbableCandidate().tag().matches("DT|PRP.*")) && j >= 0) {

                CoreMap sentPerv = document.get(CoreAnnotations.SentencesAnnotation.class).get(j);
                SemanticGraph g = sentPerv.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
                for (TypedDependency t : g.typedDependencies()) {
                    if (t.reln().toString().matches("\\b(nsubj.*|dobj|iobj|nmod:agent)\\b") //                            
                            && t.dep().toString().matches(".*\\/(NN.*|PRP.*)")) {
                        if (t.dep().toString().matches(".*\\/PRP.*")
                                && !t.dep().word().equalsIgnoreCase(node.word())) {
                            continue;
                        }
                        evaluateCandidate(t.dep(), t.reln().toString(), pronoun, g, g.size() - 1, false);
                    }
                }
                if (pronoun.getProbableCandidate().word() != null) {
                    posSubs.add(pronoun.getProbableCandidate().word());
                }

                j--;
            }

        }

    }

    private boolean checkPleonastic(SemanticGraph sentTree, String sentStr, IndexedWord itNode) {
        boolean checked = false;

        int indexItNode = itNode.index();
        String itSent = "";
        Set<IndexedWord> nodes = sentTree.vertexSet();
        for (int n = indexItNode + 1; n < nodes.size(); n++) {
            IndexedWord w = sentTree.getNodeByIndex(n);

            itSent = itSent + w.lemma() + "/" + w.tag();
            if (w.word().matches("\\p{Punct}")) {
                itSent = itSent + w.word();
                break;
            }
        }
        itSent = itSent + ".";
        //case of sentenceces with modal adjectives or cognitive verbs
        if (itSent.matches(structureMC1)
                || itSent.matches(structureMC2)
                || itSent.matches(structureMC3)) {
            if (itSent.matches(modalAdjPt)
                    || itSent.matches(cognVbPt)
                    || itSent.matches(structureMC4)
                    || itSent.matches(structureMC5)) {
                checked = true;
            }
        } else if (itSent.matches(partVbPtA)
                || itSent.matches(partVbPtB)) {
            checked = true;
        } else if (indexItNode - 1 > 0) {
            IndexedWord pred = sentTree.getNodeByIndex(indexItNode - 1);
            if (pred.lemma().matches(partVbPt2A)
                    && itSent.matches(partVbPt2B)) {
                checked = true;
            }
        }

        return checked;
    }

    private void evaluateCandidate(IndexedWord c, String relC, Pronoun pronoun, SemanticGraph tree, int indexM, boolean normal) {
        Gender nodeG = pronoun.getGender();
        Num numG = pronoun.getNumber();
        IndexedWord node = pronoun.getNode();
        double score = 0.0;

        if (numG == Num.PLURAL) {
            if (c.tag().matches("DT|NNPS|NNS") || (c.tag().matches("PRP.*") && !c.word().equals(node.word()))) {
                double index = ((double) c.index()) / ((double) indexM);
                score = pronoun.scoreCandidate(1.0, relC, index, normal);
                pronoun.addCandidate(getCandidate(c, tree,""), score);
            }

            if (tree.getNodeByIndex(c.index() + 1).tag().equals("CC")
                    && tree.getNodeByIndex(c.index() + 2).tag().matches("NN.*")) {

                String relW2 = "";
                for (GrammaticalRelation g : tree.relns(tree.getNodeByIndex(c.index() + 2))) {
                    relW2 = g.getShortName();
                    if (relC.equals(relW2)) {
                        String w1 = tree.getNodeByIndex(c.index() + 1).word();
                        String w2 = tree.getNodeByIndex(c.index() + 2).word();

                        IndexedWord w = new IndexedWord("", c.sentIndex(), c.index());
                        w.setWord(c.word() + " " + w1 + " " + w2);
                        w.setTag(c.tag());
                        pronoun.addCandidate(w, score + 1.0);
                        break;
                    }

                }

            }

        } else if (numG == Num.SINGULAR
                && c.tag().matches("DT|NN|NNP") || (c.tag().matches("PRP.*") && !c.word().equals(node.word()))) {
            Gender subG = genderUtil.findGender(c.word().toLowerCase());

            double index = ((double) c.index()) / ((double) indexM);
            if (subG == nodeG) {
                score = pronoun.scoreCandidate(1.0, relC, index, normal);
                pronoun.addCandidate(getCandidate(c, tree, pronoun.getRel()), score);
            } else if (subG == Gender.UNDEFINED || (nodeG == Gender.NEUTRAL && subG != nodeG)) {
                score = pronoun.scoreCandidate(0.75, relC, index, normal);
                pronoun.addCandidate(getCandidate(c, tree, pronoun.getRel()), score);
            }

        }

    }

    private IndexedWord getCandidate(IndexedWord node, SemanticGraph tree, String relPr) {

        IndexedWord w = new IndexedWord("", node.sentIndex(), node.index());
        String totalWord = "";
        Set<IndexedWord> children = tree.descendants(node);
        for (IndexedWord c : children) {
            if (c.index() < node.index()) {
                totalWord += " " + c.word();
            }
        }
        totalWord += " " + node.word();
        if(relPr.equals("nmod:poss")){
            totalWord+="'s";
        }
        w.setWord(totalWord);
        w.setTag(node.tag());
        return w;
    }
    
    
    public void setOnlyPronouns(boolean onlyPr){
        this.onlyPronouns = onlyPr;
    }

    public boolean getOnlyPronouns(){
        return onlyPronouns;
    }
    
    
    /**
     * *
     * A supporting class to save all the pronouns in text and associate to them
     * all the informations to score their possible substitutes, like gender,
     * number, relation in the parsing tree.
     *
     * Every pronoun is a IndexedNode, a node of the tree, so containing tag,
     * originaltext, lemma, and so on. For every pronoun there is a list of
     * possible substitutes which are IndexedNode too.
     */
    private class Pronoun {

        /**
         * The pronoun that must be substituted.
         */
        private IndexedWord pronoun;

        /**
         * A map of all the node in the sentence tree which can substitute the
         * pronoun and their related scores.
         */
        private Map<IndexedWord, Double> candidates;

        /**
         * The number (singular or plural) of the pronoun.
         */
        private final Num number;

        /**
         * The gender of the pronoun (masculine, feminine or neutral).
         */
        private final Gender gender;
        
        /**
         * The grammatical relation of the pronoun in the sentence.
         */
        private String rel;

        /**
         * The most probable node among the candidates.
         */
        private IndexedWord max = new IndexedWord();

        /**
         * Empirical weights for gender, relation and position used in the
         * weighted sum to score each pronoun.
         */
        private double wGendre = 0.5;
        private double wRel = 0.7;
        private double wPos = 0.2;
        

        public Pronoun(IndexedWord w, Gender g, Num n) {
            this.pronoun = w;
            this.candidates = new HashMap<>();
            this.gender = g;
            this.number = n;
        }

        public void addCandidate(IndexedWord candidate, double score) {
            if (candidates.isEmpty()) {
                max = candidate;
            } else {
                Double maxSc = Collections.max(candidates.values());
                if (score >= maxSc) {
                    max = candidate;
                }
            }
            this.candidates.put(candidate, score);
        }

        public void resetMax() {
            candidates.clear();
        }

        /**
         * match the perfect candidate choosing the one with the greater score
         *
         * @return
         */
        public IndexedWord getProbableCandidate() {
            return this.max;
        }

        public double scoreCandidate(double gen, String rel, double index, boolean comp) {
            double relSc;
            if (rel.equals(EnglishGrammaticalRelations.NOMINAL_SUBJECT.getShortName())
                    || rel.equals(EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT.getShortName())) {
                relSc = 4.0;
            } else if (rel.equals(EnglishGrammaticalRelations.AGENT.getShortName())) {
                relSc = 3.0;
            } else if (rel.contains(EnglishGrammaticalRelations.MODIFIER.getShortName())) {
                relSc = 2.0;
            } else if (rel.equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName())
                    || rel.equals(EnglishGrammaticalRelations.INDIRECT_OBJECT.getShortName())) {
                relSc = 1.0;
            } else {
                relSc = 0.0;
            }
            if (comp) {
                relSc = 5.0 - relSc;
                wPos = 0.5;
            }
            return (wGendre * gen + wRel * (relSc / 4.0) + wPos * index);

        }

        IndexedWord getNode() {
            return pronoun;
        }

        Gender getGender() {
            return this.gender;
        }

        Num getNumber() {
            return this.number;
        }

        public void setRel(String relPr) {
            this.rel = relPr;
        }
        
        public String getRel(){
            return rel;
        }
        
    }

}
