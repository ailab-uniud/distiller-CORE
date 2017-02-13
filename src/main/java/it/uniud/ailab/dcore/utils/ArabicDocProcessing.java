/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniud.ailab.dcore.utils;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import gpl.pierrick.brihaye.aramorph.AraMorph;
import gpl.pierrick.brihaye.aramorph.Solution;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Muhammad Helmy
 */
public class ArabicDocProcessing {
    public final static int PREPROCESS=1, SEGMENT = 2, PARSE=3;
    private static AraMorph am; 
    private static ArabicSegmenter arSeg;
    public static String generatedLemmas;
    static{
        Properties props = new Properties();
        props.put("loadClassifier", ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabic-segmenter-atb+bn+arztrain.ser.gz").getFile());
        props.put("orthoOptions", "normArDigits,normArPunc,normAlif,removeDiacritics,removeTatweel,removeQuranChars,removeProMarker,removeMorphMarker,removeLengthening,atbEscaping,useUTF8Ellipsis");
        arSeg = new ArabicSegmenter(props);
        arSeg.loadSegmenter(ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabic-segmenter-atb+bn+arztrain.ser.gz").getFile(), props);
        am = new AraMorph();
    }
    public static void init(){
        generatedLemmas = " ";
    }
    public static void stop(){
        generatedLemmas = null;
    }
    public static String readDocumentText(String filePath){
        String docContent = "";
        try{
            File file = new File(filePath);
            docContent = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return docContent;   
    }
    public static String preprocessDoc(String originalText){
        originalText = originalText.replaceAll(" +", " ");
        originalText = originalText.replaceAll("\u00A0", "");
        originalText = originalText.replaceAll("\uFEFF", "");
        originalText = originalText.replaceAll("\r", "\n");
        originalText = originalText.replaceAll(" \n", "\n");
        originalText = originalText.replaceAll("\n ", "\n");
        originalText = originalText.replaceAll("([\n]+)", "\n");
        //originalText = originalText.replaceAll(" \r", "\n");
        //originalText = originalText.replaceAll("\r ", "\n");
        return originalText.trim();
        
    } 
    public static String preProcess(String text){
        String preProcessedTxt = ArabicDocProcessing.preprocessDoc(text);
        return preProcessedTxt.replaceAll("\n", " . ").replaceAll("\r", " . ").replaceAll("([\\.]+[\\s]+[\\.]+)", ".").replaceAll("([\\.]+)", ".").replaceAll("([\\s]+)", " ");        
    }
    public static String purifyDoc(String preProcessedText){
        preProcessedText = preProcessedText.replaceAll("[^\u0600-\u06FF\u0030-\u0039\u0020\r\n]", " ");
        preProcessedText = preProcessedText.replaceAll("[^\u0621-\u063A\u0640-\u0652\u0660-\u0669\u0030-\u0039\u0020\r\n]", " ");
        preProcessedText = preProcessedText.replaceAll("[^\u0621-\u063A\u0641-\u064A\u0660-\u0669\u0030-\u0039\u0020\r\n]", "");
        return preprocessDoc(preProcessedText);        
    }
    public static String lemmatizeDoc(String pureText){
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(pureText);
        String lemmatizedText = "";
        while (matcher.find()) {
            String word = matcher.group();            
            if(generatedLemmas.contains(" " + word + " "))
                lemmatizedText += word + " ";
            else if(am.analyzeToken(word)){
                String lw = ((Solution)am.getWordSolutions(word).iterator().next()).getLemma();
                if(lw==null || lw.length()<1)
                    lw = word;
                lemmatizedText += lw + " ";
                generatedLemmas += lemmatizedText + " ";
            }
            else{
                lemmatizedText += word + " "; 
                generatedLemmas += word + " ";
            }
        }      
        lemmatizedText = lemmatizedText.replaceAll("ـ[\u0030-\u0039]", "");
        lemmatizedText = lemmatizedText.replaceAll("[^\u0621-\u063A\u0641-\u064A\u0660-\u0669\u0030-\u0039\u0020]", "");
        return lemmatizedText.trim();       
    } 
    public static String  segmentText(String text){
        return arSeg.segmentString(text);
    }
    public static String  parseText(String text) throws UnsupportedEncodingException{
        /*System.out.println(text);
        System.exit(0);
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        InputStream oldSysIn = System.in;
        System.setIn(stream);        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, "UTF-8");
        PrintStream oldSysOut = System.out;
        System.setOut(ps);
        LexicalizedParser.main(new String[]{ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabicFactored.ser.gz").getFile(),"-"});
        System.setIn(oldSysIn);
        System.out.flush();
        System.setOut(oldSysOut);
        return baos.toString();  */
        String parserModel = ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabicFactored.ser.gz").getFile();
        LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
        TokenizerFactory<CoreLabel> tokenizerFactory =  PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        String[] sents = text.split("\n");
        String parsedText = "";
        for(int i=0; i<sents.length; i++){
            Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sents[i]));
            List<CoreLabel> rawWords2 = tok.tokenize();
            Tree parse = lp.apply(rawWords2);
            parsedText += parse.pennString();
        }
        //parse.pennPrint();
        //System.out.println();
        return parsedText;
    }
    public static String POSTageText(String segmentedText){
        Properties props = new Properties();
        props.put("tokenizerOptions", "latexQuotes=false,asciiQuotes=false,unicodeQuotes=false");
        MaxentTagger tagger = new MaxentTagger(ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabic.tagger").getFile(),props);        
        String taggedText = tagger.tagString(segmentedText);//"";
        /*List<List<HasWord>> sentences = tagger.tokenizeText(new BufferedReader(new StringReader(segmentedText)));
        for (List<HasWord> sentence : sentences) {
            List<TaggedWord> tSentence = tagger.tagSentence(sentence);            
            taggedText += Sentence.listToString(tSentence, false)+" ";
        }*/
        //taggedText = taggedText.trim();
        return taggedText;
    }
    public static boolean isArabic(String text){
        String textWithoutSpace = text.trim().replaceAll(" ",""); //to ignore whitepace
        for (int i = 0; i < textWithoutSpace.length();) {
            int c = textWithoutSpace.codePointAt(i);
          //range of arabic chars/symbols is from 0x0600 to 0x06ff
            //the arabic letter 'لا' is special case having the range from 0xFE70 to 0xFEFF
            if (c >= 0x0600 && c <=0x06FF || (c >= 0xFE70 && c<=0xFEFF)) 
                i += Character.charCount(c);   
            else                
                return false;

        } 
        return true;
      }
    public static String normalizeAlefAndYa(String text){
        return text.replaceAll("\u0649", "\u064A").replaceAll("(\u0622|\u0623|\u0625)", "\u0627");
    }
    public static String desegmentTokens(String docText, String segmentedText) throws UnsupportedEncodingException{
        ArrayList<String> words = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(docText);
        while (matcher.find()) {
            String word = matcher.group();
            if(StringUtils.countMatches(docText, word)>5 && !words.contains(word)
                    && !ArabicConstants.stopWords.contains(word)){
                words.add(word);
                
            }
        }
        segmentedText = " " + segmentedText + " ";
        for(String word:words){
            if(segmentedText.contains(word))
                continue;
            String segText = segmentText(word).trim();
            if(segText.split(" ").length>1)
                segmentedText = segmentedText.replace(" " + segText + " ", " " + word + " ");
        }
        return segmentedText.trim();
    }
}