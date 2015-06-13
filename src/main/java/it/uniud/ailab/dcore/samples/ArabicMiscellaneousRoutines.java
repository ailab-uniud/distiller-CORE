/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.uniud.ailab.dcore.samples;
//import gpl.pierrick.brihaye.aramorph.AraMorph;
//import gpl.pierrick.brihaye.aramorph.Solution;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
/**
 *
 * @author muhammad.alameldien
 */
public class ArabicMiscellaneousRoutines {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        /*AraMorph am = new AraMorph();
        am.analyzeToken("كتبها");
        System.out.println(((Solution)am.getWordSolutions("كتبها").iterator().next()).getStemArabicVocalization());
                */
        Scanner scanner = null;
        BufferedWriter output = null;
        try {
            scanner = new Scanner(new File("D:\\AILab\\Test\\Test\\posPatterns.json"));
            File file = new File("D:\\AILab\\Test\\Test\\arabPOSPatterns.json");
            output = new BufferedWriter(new FileWriter(file));
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }    
        String patternLine;
        while(scanner.hasNextLine())
        {
            String firstLine = scanner.nextLine();
            String nGramStr = firstLine+ "\n";
            patternLine= scanner.nextLine();
            String endLine = scanner.nextLine() + "\n" + scanner.nextLine();
            nGramStr +=  patternLine + "\n" + endLine+ "\n";
            try{
                output.write(nGramStr);
            }catch ( Exception e ) {
                e.printStackTrace();
            }    
            //System.out.println(patternLine + ":" + patternLine.indexOf(":")+ ":" + patternLine.length());
            String tagStr = patternLine.substring(patternLine.indexOf(":")+2, patternLine.length()-2);
            System.out.println(tagStr);
            String[] tags = tagStr.split("/");
            int njC = 0;
            ArrayList<Integer> list = new ArrayList<Integer>();
            int i = 0;
            for(String s: tags){
                if(s.equals("NN")|| s.equals("NNS")|| s.equals("NNP")|| s.equals("NNPS")|| s.equals("JJ")){
                    list.add(i);
                    njC++;
                }
                i++;
            }
            if(njC == 1){ 
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                for(i=0; i<list.get(0); i++)
                    nGramStr += tags[i] + "/";
                nGramStr += "DT" + tags[i];
                for(i=list.get(0)+1; i<tags.length; i++)
                    nGramStr += "/" + tags[i];
                nGramStr += "\"," + "\n" + endLine;
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }  
            }
            else if(njC == 2){
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += "DT" + tags[0] + "/";
                for(i=1; i<list.get(1); i++)
                    nGramStr +=  tags[i] + "/";
                nGramStr += "DT" + tags[list.get(1)];
                for(i=list.get(1)+1; i<tags.length; i++)
                    nGramStr += "/" + tags[i] ;
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }  
                
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += "DT" + tags[0];
                for(i=list.get(0)+1; i<tags.length; i++)
                    nGramStr += "/" + tags[i] ;
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
            }
                
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                for(i=0; i<list.get(1); i++)
                    nGramStr += tags[i] + "/";
                nGramStr += "DT" + tags[list.get(1)];
                for(i=list.get(1)+1; i<tags.length; i++)
                    nGramStr += "/" + tags[i] ;
                nGramStr += "\"," + "\n" + endLine;
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        else if(njC == 3){
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += "DT" + tags[0] + "/";
                nGramStr += "DT" + tags[1] + "/";
                nGramStr += "DT" + tags[2];
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }  
                
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += "DT" + tags[0] + "/";
                nGramStr += "DT" + tags[1] + "/";
                nGramStr += tags[2] ;
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }
                
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += "DT" + tags[0] + "/";
                nGramStr += tags[1] + "/";
                nGramStr += "DT" + tags[2];
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }  
                
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += "DT" + tags[0] + "/";
                nGramStr += tags[1] + "/";
                nGramStr += tags[2];
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
            }
            
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += tags[0] + "/";
                nGramStr += "DT" + tags[1] + "/";
                nGramStr += "DT" + tags[2];
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }  
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += tags[0] + "/";
                nGramStr += "DT" + tags[1] + "/";
                nGramStr += tags[2];
                nGramStr += "\"," + "\n" + endLine+ "\n";
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                } 
                nGramStr = firstLine + "\n" + patternLine.substring(0, patternLine.indexOf(":")+2);
                nGramStr += tags[0] + "/";
                nGramStr += tags[1] + "/";
                nGramStr += "DT" + tags[2];
                nGramStr += "\"," + "\n" + endLine;
                try{
                    output.write(nGramStr);
                }catch ( Exception e ) {
                    e.printStackTrace();
                }  
        }
        }    
        try {
            output.flush();
            scanner.close();
            output.close();            
        } catch ( Exception e ) {
            e.printStackTrace();
        }    
    }
    
}