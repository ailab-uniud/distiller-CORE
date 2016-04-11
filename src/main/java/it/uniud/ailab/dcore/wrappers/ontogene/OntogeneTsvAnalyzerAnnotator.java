package it.uniud.ailab.dcore.wrappers.ontogene;

import com.opencsv.CSVReader;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
/**
 *
 * @author Marco Basaldella
 */
public class OntogeneTsvAnalyzerAnnotator implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        String tsvFileName = IOBlackboard.getCurrentDocument();

        tsvFileName = tsvFileName.substring(tsvFileName.lastIndexOf('-') + 1);
        tsvFileName = tsvFileName.substring(0, tsvFileName.lastIndexOf('.'));
        tsvFileName = tsvFileName + ".tsv";

        File f = new File(IOBlackboard.getDocumentsFolder());
        tsvFileName = f.getParent() + FileSystem.getSeparator()
                + "tsv" + FileSystem.getSeparator() + tsvFileName;

        Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).
                log(Level.INFO, "Looking for TSV file in path: {0}", tsvFileName);

        CSVReader reader;

        try {
            reader = new CSVReader(new FileReader(tsvFileName), '\t');
        } catch (FileNotFoundException ex) {
            throw new AnnotationException(this, "Unable to open " + tsvFileName, ex);
        }

        String[] nextLine;

        List<String> terms = new ArrayList<>();

        try {

            int prevStart = -1;
            int prevEnd = -1;
            String currentWord = null;

            while ((nextLine = reader.readNext()) != null) {

                int curStart = Integer.parseInt(nextLine[2]);
                int curEnd = Integer.parseInt(nextLine[3]);

                // (start) we're matching a new word: just update the indices
                if (currentWord == null) {
                    currentWord = nextLine[4];
                    prevStart = curStart;
                    prevEnd = curEnd;
                } else {
                    // if we've already matched a word, 
                    // check the indices: if we're expanding, substitute 
                    // the word; if the indices stay the same, don't do
                    // anything; if the FIRST index moves, save the word
                    // and start a new match.

                    if (curStart > prevStart) {
                        if (curEnd > prevEnd) {
                            terms.add(currentWord);
                            currentWord = nextLine[4];
                        }
                        prevStart = curStart;
                        prevEnd = curEnd;

                    } else {
                        if (curEnd > prevEnd) {
                            currentWord = nextLine[4];
                            prevEnd = curEnd;
                        }
                    } // else
                } // if currentWord == null
            } // while
        } catch (IOException ex) {
            Logger.getLogger(OntogeneTsvAnalyzerAnnotator.class.getName()).log(
                    Level.SEVERE, "Error while processing TSV file");

            throw new AnnotationException(this, "Error while processing " + tsvFileName, ex);
        }
    }

}
