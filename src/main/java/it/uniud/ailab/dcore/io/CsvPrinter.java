/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.io;

import com.opencsv.CSVWriter;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.utils.Either;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An utility to print the result of a distillation process on a CSV file.
 *
 * @author Marco Basaldella
 */
public class CsvPrinter extends GenericSheetPrinter {

    /**
     * The delimiter used to separate values.
     */
    private final char delimiter;

    /**
     * Flag used to determine if the output file should contain the column
     * headers or not.
     */
    private final boolean printHeaders;

    /**
     * Default field delimiter.
     */
    private static final char DEFAULT_DELIMITER = ',';
    /**
     * Default print headers option.
     */
    private static final boolean DEFAULT_HEADERS = true;

    /**
     * Instantiates the {@code CsvPrinter} with the default options.
     */
    public CsvPrinter() {
        this(DEFAULT_DELIMITER, DEFAULT_HEADERS);
    }

    /**
     * Instantiates the {@code CsvPrinter} with the default header options.
     *
     * @param delimiter delimiter of the records in a row
     */
    public CsvPrinter(char delimiter) {
        this(delimiter, DEFAULT_HEADERS);
    }

    /**
     * Instantiates the {@code CsvPrinter} with the default delimiter options.
     *
     * @param printHeaders true if the printer should write the headers of the
     * table; false otherwise.
     */
    public CsvPrinter(boolean printHeaders) {
        this(DEFAULT_DELIMITER, printHeaders);
    }

    /**
     * Instantiates the {@code CsvPrinter} with the specified options.
     *
     * @param delimiter delimiter of the records in a row
     * @param printHeaders true if the printer should write the headers of the
     * table; false otherwise.
     */
    public CsvPrinter(char delimiter, boolean printHeaders) {
        this.delimiter = delimiter;
        this.printHeaders = printHeaders;
    }

    /**
     * Write the annotations contained in the document n-grams in the specified
     * file.
     *
     * @param fileName the name of the file to write.
     * @param b the blackboard to analyze.
     */
    public void writeGrams(String fileName, Blackboard b) {

        this.loadGrams(b);
        writeFile(fileName);
    }

    /**
     * Write the annotations contained in the document sentences in the
     * specified file.
     *
     * @param fileName the name of the file to write.
     * @param b the blackboard to analyze.
     */
    public void writeSentences(String fileName, Blackboard b) {
        this.loadSentences(b.getStructure());
        writeFile(fileName);
    }

    /**
     * Writes the annotations contained in the printer on the specified file.
     * 
     * @param fileName the path where to write.
     */
    private void writeFile(String fileName) {

        try {
            CSVWriter writer
                    = new CSVWriter(new FileWriter(fileName),
                            delimiter, CSVWriter.DEFAULT_QUOTE_CHARACTER);

            writer.writeNext(getHeaders().
                    toArray(new String[getHeaders().size()]), false);

            // build the rows            
            for (Map<String, Either<String, Number>> row : this.getRows()) {

                String[] rowArray = new String[getHeaders().size()];

                for (int i = 0; i < getHeaders().size(); i++) {
                    String header = getHeaders().get(i);
                    Either<String, Number> cell = row.get(header);

                    if (cell == null) {
                        if (getHeaderTypes().get(i).isLeft()) {
                            rowArray[i] = "";
                        } else {
                            rowArray[i] = "0";
                        }
                    } else if (cell.isLeft()) { // the cell is a string
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
