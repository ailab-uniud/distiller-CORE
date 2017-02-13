/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniud.ailab.dcore.utils;

/**
 *
 * @author Muhammad Helmy
 */
public class ArabicConstants {
    public static String AL = "\u0627\u0644"; //Arabic Determiner Article (Alef Lam in UTF-8)
    public static String stopWords;
    static{
        readStopWords();
    }
    private static void readStopWords() {
        String filePath = ArabicConstants.class.getClassLoader().getResource("ailab.stopwords/ar-KPMinerPlusStopwords.txt").getFile();
        stopWords = ArabicDocProcessing.readDocumentText(filePath);
        stopWords = ArabicDocProcessing.preprocessDoc(stopWords);
        stopWords = " " + stopWords.replace("\r","").replace("\n"," ") + " ";
        stopWords = ArabicDocProcessing.normalizeAlefAndYa(stopWords);
    }
}