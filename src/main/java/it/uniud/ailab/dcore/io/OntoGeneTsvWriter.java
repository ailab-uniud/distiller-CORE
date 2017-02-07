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

import com.opencsv.CSVWriter;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator;
import it.uniud.ailab.dcore.utils.Either;
import it.uniud.ailab.dcore.wrappers.ontogene.OntogeneTsvAnalyzerAnnotator;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pipeline stage that prints the annotations of the sentences.
 *
 * @author Marco Basaldella
 */
public class OntoGeneTsvWriter extends CsvPrinter implements FileWriterStage {

    @Override
    public String getFileSuffix() {
        return "distilled.tsv";
    }

    @Override
    public void writeFile(String file, Blackboard b) {
        loadKeyphrases(b);

        try {
            CSVWriter writer
                    = new CSVWriter(new FileWriter(file),
                            '\t', CSVWriter.DEFAULT_QUOTE_CHARACTER);

            String[] headers = {
                OntogeneTsvAnalyzerAnnotator.DOCUMENT_ID,
                GenericEvaluatorAnnotator.SCORE,
                DefaultAnnotations.START_INDEX,
                DefaultAnnotations.END_INDEX,
                DefaultAnnotations.SURFACE
            };

            // build the rows            
            for (Map<String, Either<String, Number>> row : this.getRows()) {

                String[] rowArray = new String[getHeaders().size()];

                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i];
                    Either<String, Number> cell = row.get(header);

                    if (cell.isLeft()) { // the cell is a string
                        rowArray[i] = cell.getLeft();
                    } else { // the cell is a number
                        rowArray[i]
                                = // if there's no decimal part in the numeric
                                // value, avoid printing ".0"
                                cell.getRight().doubleValue()
                                == Math.floor(cell.getRight().doubleValue())
                                ? String.format(
                                        Locale.US, "%d",
                                        cell.getRight().intValue())
                                : String.format(
                                        Locale.US, "%f",
                                        cell.getRight().doubleValue());
                    }
                }

                writer.writeNext(rowArray, false);
            }

            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(CsvPrinter.class.getName()).log(Level.SEVERE,
                    "Error while writing CSV file", ex);
        }
    }

}
