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
package it.uniud.ailab.dcore.wrappers.ontogene;

import com.opencsv.CSVReader;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility to be used for annotators that use the Ontogene Framework.
 *
 * @author Marco Basaldella
 */
public class OntogeneUtils {

    public static String getCurrentDocumentID() {
        String fileID = IOBlackboard.getCurrentDocument();

        if (fileID.endsWith("xml")) {

            fileID = fileID.substring(fileID.lastIndexOf('-') + 1);
            fileID = fileID.substring(0, fileID.lastIndexOf('.'));

        } else {

            fileID = fileID.substring(fileID.lastIndexOf(FileSystem.getSeparator()) + 1);
            fileID = fileID.substring(0, fileID.lastIndexOf('.'));

        }
        return fileID;
    }

    /**
     * Gets the current document's Pubmed ID.
     *
     * @param craftFolder the folder where the CRAFT corpus is located.
     * @return the Pubmed ID of the current document, or null if it can't be 
     * located.
     * @throws java.io.FileNotFoundException if the mappings file cannot be
     * found
     */
    public static String getCurrentDocumentPubmedID(String craftFolder)
            throws FileNotFoundException, IOException {

        if (!craftFolder.endsWith(FileSystem.getSeparator())) {
            craftFolder += FileSystem.getSeparator();
        }

        craftFolder += "articles" + FileSystem.getSeparator() + "ids"
                + FileSystem.getSeparator();

        CSVReader reader = new CSVReader(
                new FileReader(craftFolder
                        + FileSystem.getSeparator() + "craft-idmappings-release"),
                '\t');

        String extension = "-" + getCurrentDocumentID() + ".nxml";

        String[] line;
        while ((line = reader.readNext()) != null) {
            
            // skip comments
            if (line[0].startsWith("#")) {
                continue;
            }

            if (line[0].endsWith(extension)) {
                return line[2];
            }
        }
        
        return null;
    }

}
