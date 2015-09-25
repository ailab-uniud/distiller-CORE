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
     * The output file name.
     */
    private final String fileName;
    
    /**
     * Flag used to determine if the output file should contain the column 
     * headers or not.
     */
    private final boolean printHeaders;
    
    public CsvPrinter(String fileName) {
        this(fileName,',',true);
    }
    
    public CsvPrinter(String fileName,char delimiter) {
        this(fileName,delimiter,true);
    }
    
    public CsvPrinter(String fileName,boolean printHeaders) {
        this(fileName,',',printHeaders);
    }
    
    public CsvPrinter(String fileName,char delimiter,boolean printHeaders) {
        this.fileName = fileName;
        this.delimiter = delimiter;
        this.printHeaders = printHeaders;
    }
    
    public void printGrams(Blackboard b) {
        
        this.loadGrams(b.getStructure());
        
        writeFile();
        
    }

    private void writeFile() {
        
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName),delimiter);
            
            writer.writeNext(getHeaders().
                    toArray(new String[getHeaders().size()]));
            
            // build the rows            
            for (Map<String,Either<String,Number>> row : this.getRows()) {
                
                String[] rowArray = new String[getHeaders().size()];
                
                for (int i = 0; i < getHeaders().size(); i++) {
                    String header = getHeaders().get(i);
                    Either<String,Number> cell = row.get(header);
                    if (cell.isLeft())
                        rowArray[i] = cell.getLeft();
                    else 
                        rowArray[i] = String.format(
                                Locale.US, "%f", cell.getRight().doubleValue());
                }
                
                writer.writeNext(rowArray);                
            }
            
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CsvPrinter.class.getName()).log(Level.SEVERE, 
                    "Error while writing CSV file", ex);
        }
    }
    
    
}
