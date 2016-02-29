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
package it.uniud.ailab.dcore.io;

import com.opencsv.CSVWriter;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.utils.Either;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An utility to print the blackboard entities in txt files.
 *
 * @author Giorgia Chiaradia
 */
public class TxtPrinter {

    private String toPrint = "";

    /**
     * Write the annotations contained in the document n-grams in the specified
     * file.
     *
     * @param fileName the name of the file to write.
     * @param b the blackboard to analyze.
     */
    public void writePreprocessedText(String fileName, Blackboard b) {

        this.toPrint = b.getStructure().getPreprocessedText();
        writeFile(fileName);
    }

    public void writeFile(String fileName) {

        FileOutputStream fop;
        File file;

        try {

            file = new File(fileName);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = toPrint.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

        } catch (IOException ex) {
            Logger.getLogger(TxtPrinter.class.getName()).log(Level.SEVERE,
                    "Error while writing TXT file", ex);
        }
    }

}
