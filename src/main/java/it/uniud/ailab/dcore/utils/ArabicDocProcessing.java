/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniud.ailab.dcore.utils;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import gpl.pierrick.brihaye.aramorph.AraMorph;
import gpl.pierrick.brihaye.aramorph.Solution;
import java.io.File;
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
 * @author Muhammed Helmy
 */
public class ArabicDocProcessing {

    public final static int PREPROCESS = 1, SEGMENT = 2, PARSE = 3;
    private static AraMorph am = new AraMorph();
    private static ArabicSegmenter ar = null;
    public static String generatedLemmas;

    public static void init() {
        generatedLemmas = " ";
    }

    public static void stop() {
        generatedLemmas = null;
    }

    public static String readDocumentText(String filePath) {
        String docContent = "";
        try {
            File file = new File(filePath);
            docContent = new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return docContent;
    }

    public static String preprocessDoc(String originalText) {
        originalText = originalText.replaceAll(" +", " ");
        originalText = originalText.replaceAll("\u00A0", "");
        originalText = originalText.replaceAll("\uFEFF", "");
        originalText = originalText.replaceAll(" \n", "\n");
        originalText = originalText.replaceAll("\n ", "\n");
        originalText = originalText.replaceAll(" \r", "\r");
        originalText = originalText.replaceAll("\r ", "\r");
        return originalText.trim();

    }

    public static String preProcess(String text) {
        String preProcessedTxt = ArabicDocProcessing.preprocessDoc(text);
        return preProcessedTxt.replaceAll("\n", " . ").replaceAll("\r", " . ").
                replaceAll("([\\.]+[\\s]+[\\.]+)", ".").replaceAll("([\\.]+)", ".").
                replaceAll("([\\s]+)", " ");
    }

    public static String purifyDoc(String preProcessedText) {
        preProcessedText = preProcessedText.replaceAll("[^\u0600-\u06FF\u0030-\u0039\u0020\r\n]", " ");
        preProcessedText = preProcessedText.replaceAll("[^\u0621-\u063A\u0640-\u0652\u0660-\u0669\u0030-\u0039\u0020\r\n]", " ");
        preProcessedText = preProcessedText.replaceAll("[^\u0621-\u063A\u0641-\u064A\u0660-\u0669\u0030-\u0039\u0020\r\n]", "");
        return preprocessDoc(preProcessedText);
    }

    public static String lemmatizeDoc(String pureText) {
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(pureText);
        String lemmatizedText = "";
        while (matcher.find()) {
            String word = matcher.group();
            if (generatedLemmas.contains(" " + word + " ")) {
                lemmatizedText += word + " ";
            } else if (am.analyzeToken(word)) {
                String lw = ((Solution) am.getWordSolutions(word).
                        iterator().next()).getLemma();
                if (lw == null || lw.length() < 1) {
                    lw = word;
                }
                lemmatizedText += lw + " ";
                generatedLemmas += lemmatizedText + " ";
            } else {
                lemmatizedText += word + " ";
                generatedLemmas += word + " ";
            }
        }
        lemmatizedText = lemmatizedText.replaceAll("ـ[\u0030-\u0039]", "");
        lemmatizedText = lemmatizedText.replaceAll("[^\u0621-\u063A\u0641-\u064A\u0660-\u0669\u0030-\u0039\u0020]", "");
        return lemmatizedText.trim();
    }

    public static String processText(int process, String text) throws UnsupportedEncodingException {

        if (process == SEGMENT) {
            Properties props = new Properties();
            props.put("loadClassifier", ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabic-segmenter-atb+bn+arztrain.ser.gz").getFile());
            props.put("orthoOptions", "normArDigits,normArPunc,normAlif,removeDiacritics,removeTatweel,removeQuranChars,removeProMarker,removeMorphMarker,removeLengthening,atbEscaping,useUTF8Ellipsis");

            if (ar == null) {

                ar = new ArabicSegmenter(props);
                ar.loadSegmenter(ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabic-segmenter-atb+bn+arztrain.ser.gz").getFile(), props);
            }
            text = ar.segmentString(text);
        } 
        else if (process == PARSE) {
            LexicalizedParser.main(new String[]{ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabicFactored.ser.gz").getFile(), "-"});
        }
        return text;       
    }

    public static String POSTageText(String segmentedText) {
        Properties props = new Properties();
        props.put("tokenizerOptions", "latexQuotes=false,asciiQuotes=false,unicodeQuotes=false");
        MaxentTagger tagger = new MaxentTagger(ArabicDocProcessing.class.getClassLoader().getResource("stanford/arabic.tagger").getFile(), props);
        String taggedText = tagger.tagString(segmentedText);//"";

        return taggedText;
    }

    public static boolean isArabic(String text) {
        String textWithoutSpace = text.trim().replaceAll(" ", ""); //to ignore whitepace
        for (int i = 0; i < textWithoutSpace.length();) {
            int c = textWithoutSpace.codePointAt(i);
            //range of arabic chars/symbols is from 0x0600 to 0x06ff
            //the arabic letter 'لا' is special case having the range from 0xFE70 to 0xFEFF
            if (c >= 0x0600 && c <= 0x06FF || (c >= 0xFE70 && c <= 0xFEFF)) {
                i += Character.charCount(c);
            } else {
                return false;
            }

        }
        return true;
    }

    public static String normalizeAlefAndYa(String text) {
        return text.replaceAll("\u0649", "\u064A").replaceAll("(\u0622|\u0623|\u0625)", "\u0627");
    }

    public static String desegmentTokens(String docText, String segmentedText) throws UnsupportedEncodingException {
        ArrayList<String> words = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(docText);
        while (matcher.find()) {
            String word = matcher.group();
            if (StringUtils.countMatches(docText, word) > 5 && !words.contains(word)
                    && !ArabicConstants.stopWords.contains(word)) {
                words.add(word);

            }
        }
        segmentedText = " " + segmentedText + " ";
        for (String word : words) {
            if (segmentedText.contains(word)) {
                continue;
            }
            String segText = processText(SEGMENT, word).trim();
            if (segText.split(" ").length > 1) {
                segmentedText = segmentedText.replace(" " + segText + " ", " " + word + " ");
            }
        }
        return segmentedText.trim();
    }
}
