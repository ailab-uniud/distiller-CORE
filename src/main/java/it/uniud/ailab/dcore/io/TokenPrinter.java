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
package it.uniud.ailab.dcore.io;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple printer that prints out the tokens and/or their annotations.
 *
 * @author Marco Basaldella
 */
public class TokenPrinter implements FileWriterStage {

    private boolean printText = true;
    private boolean printPoS = true;
    private boolean printLemma = false;
    private boolean printStem = false;

    /**
     * A printer that writes the text tokens in SURFACE/POS/LEMMA/STEM format.
     * This contructor enables only the writing of surface and PoS.
     */
    public TokenPrinter() {
        this(true,true,false,false);
    }

    /**
     * A printer that writes the text tokens in SURFACE/POS/LEMMA/STEM format.
     * 
     * @param printText set to TRUE for printing the surface of a token.
     * @param printPoS set to TRUE for printing the PoS of a token.
     * @param printLemma set to TRUE for printing the lemma of a token.
     * @param printStem set to TRUE for printing the stem of a token.
     */
    public TokenPrinter(boolean printText, boolean printPoS, boolean printLemma,
            boolean printStem) {
        this.printText = printText;
        this.printPoS = printPoS;
        this.printLemma = printLemma;
        this.printStem = printStem;
    }

    /**
     * Set the printer to print/not to print the text of the token.
     * @param printText set to TRUE to print the text of the token; false otherwise.
     */
    public void setPrintText(boolean printText) {
        this.printText = printText;
    }

    /**
     * Set the printer to print/not to print the PoS tag of the token.
     * 
     * @param printPoS set to TRUE to print the PoS of the token; false otherwise.
     */
    public void setPrintPoS(boolean printPoS) {
        this.printPoS = printPoS;
    }

    /**
     * Set the printer to print/not to print the lemma of the token.
     * 
     * @param printLemma set to TRUE to print the lemma of the token; false otherwise.
     */
    public void setPrintLemma(boolean printLemma) {
        this.printLemma = printLemma;
    }

    /**
     * Set the printer to print/not to print the stem of the token.
     * 
     * @param printStem set to TRUE to print the stem of the token; false otherwise.
     */
    public void setPrintStem(boolean printStem) {
        this.printStem = printStem;
    }

    /**
     * Writes the tokens to the provided path.
     * 
     * @param file the output path.
     * @param b the blackboard to read.
     */
    @Override
    public void writeFile(String file, Blackboard b) {
        File fout = new File(file);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (Sentence s : DocumentUtils.getSentences(b.getStructure())) {

                List<Token> tokens = s.getTokens();

                for (Token t : tokens) {

                    StringBuilder sb = new StringBuilder();

                    if (printText) {
                        sb.append(t.getIdentifier());
                    }

                    if (printPoS) {
                        if (printText) {
                            sb.append("/");
                        }
                        sb.append(t.getPoS());
                    }

                    if (printStem) {
                        if (printPoS || printText) {
                            sb.append("/");
                        }
                        sb.append(t.getStem());
                    }

                    if (printLemma) {
                        if (printStem || printPoS || printText) {
                            sb.append("/");
                        }
                        sb.append(t.getLemma());
                    }
                    sb.append(" ");
                    bw.append(sb.toString());
                }

                bw.newLine();
            }

            bw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvPrinter.class.getName()).log(Level.SEVERE,
                    "Error while writing file", ex);
        } catch (IOException ex) {
            Logger.getLogger(CsvPrinter.class.getName()).log(Level.SEVERE,
                    "Error while writing CSV file", ex);
        }

    }

    /**
     * Get the suffix of the output file.
     * 
     * @return the suffix of the output file.
     */
    @Override
    public String getFileSuffix() {
        return "tokens.csv";
    }
}
