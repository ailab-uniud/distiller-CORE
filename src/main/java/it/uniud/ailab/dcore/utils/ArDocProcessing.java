/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniud.ailab.dcore.utils;

import gpl.pierrick.brihaye.aramorph.AraMorph;
import gpl.pierrick.brihaye.aramorph.Solution;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author muhammad.alameldien
 */
public final class ArDocProcessing {
    public static String preprocessDoc(String originalText){
        originalText = originalText.replaceAll(" +", " ");
        originalText = originalText.replaceAll("\u00A0", "");
        //originalText = originalText.replaceAll("\r", "");
        originalText = originalText.replaceAll(" \n", "\n");
        originalText = originalText.replaceAll("\n ", "\n");
        originalText = originalText.replaceAll(" \r", "\r");
        originalText = originalText.replaceAll("\r ", "\r");
        return originalText.trim();
        
    } 
    public static String purifyDoc(String preProcessedText){
        preProcessedText = preProcessedText.replaceAll("[^\u0600-\u06FF\u0030-\u0039\u0020\r\n]", " ");
        preProcessedText = preProcessedText.replaceAll("[^\u0621-\u063A\u0640-\u0652\u0660-\u0669\u0030-\u0039\u0020\r\n]", " ");
        preProcessedText = preProcessedText.replaceAll("[^\u0621-\u063A\u0641-\u064A\u0660-\u0669\u0030-\u0039\u0020\r\n]", "");
        return preprocessDoc(preProcessedText);        
    }
    public static String lemmatizeDoc(AraMorph am, String pureText){
        Pattern pattern = Pattern.compile("\\p{InArabic}+");
        Matcher matcher = pattern.matcher(pureText);
        String lemmatizedText = "";
        while (matcher.find()) {
            String word = matcher.group();
            if(am.analyzeToken(word))
                lemmatizedText += ((Solution)am.getWordSolutions(word).iterator().next()).getLemma() + " ";
            else
                lemmatizedText += word + " ";
        }
        lemmatizedText = lemmatizedText.replaceAll("ـ[\u0030-\u0039]", "");
        lemmatizedText = lemmatizedText.replaceAll("[^\u0621-\u063A\u0641-\u064A\u0660-\u0669\u0030-\u0039\u0020]", "");
        return lemmatizedText.trim();       
    }    
}