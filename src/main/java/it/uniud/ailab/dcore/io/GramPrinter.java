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

/**
 * Pipeline stage that prints the annotations of the sentences.
 *
 * @author Marco Basaldella
 */
public class GramPrinter implements FileWriterStage {
    
    private boolean printOriginalText;

    @Override
    public String getFileSuffix() {
        return "grams.csv";
    }

    @Override
    public void writeFile(String file, Blackboard b) {
        CsvPrinter printer = new CsvPrinter();
        printer.setPrintOriginalText(printOriginalText);
        printer.writeGrams(file, b);
    }

    /**
     * Add a column with the original text when printing the {@link
     * it.uniud.ailab.dcore.persistence.Gram}.
     *
     * @param printOriginalText set the value to true to print the original text
     * of Annotable objects.
     */
    public void setPrintOriginalText(boolean printOriginalText) {
        this.printOriginalText = printOriginalText;
    }
}
