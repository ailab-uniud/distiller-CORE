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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotable;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.persistence.*;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.Either;
import it.uniud.ailab.dcore.utils.Either.Left;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A printer class that generates an abstract spreadsheet-like output. This
 * class is not meant to perform any "real" I/O, but is designed to provide a
 * simple, common way to retrieve information from the Blackboard in an easily
 * navigable format.
 *
 *
 * @author Marco Basaldella
 */
public abstract class GenericSheetPrinter {

    private List<String> headers;
    private List<Map<String, Either<String, Number>>> rows;
    private List<Either<String, Number>> headerTypes;

    private final String ID_COLUMN = "ID";

    protected GenericSheetPrinter() {
    }

    /**
     * Returns the headers detected by the printer.
     *
     * @return the headers of the table.
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * Return the types of the headers detected by the printer.
     *
     * @return the types of the headers of the table.
     */
    public List<Either<String, Number>> getHeaderTypes() {
        return headerTypes;
    }

    /**
     * Returns the rows detected by the printer.
     *
     * @return the rows of the table.
     */
    public List<Map<String, Either<String, Number>>> getRows() {
        return rows;
    }

    /**
     * Searches the table to check if contains a row with a certain identifier.
     *
     * @param id the identifier of the row to search
     * @return true if the table contains the row
     */
    public boolean containsRow(String id) {
        for (Map<String, Either<String, Number>> row : rows) {
            if (row.get(ID_COLUMN).getLeft().equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Stores a generic annotable object.
     *
     * @param a the Annotable to print.
     */
    private void addRow(Annotable annotable) {

        Map<String, Either<String, Number>> row = new HashMap<>();

        String rowId = annotable.getIdentifier();

        if (containsRow(rowId)) {
            return;
        }

        row.put(ID_COLUMN, new Left<>(rowId));

        for (Annotation a : annotable.getAnnotations()) {

            // If it's a single-valued annotation, just add the value.
            // elsewhise, customize the headers with a counter.
            if (a.size() == 1) {
                // Check if the annotation is already tracked in the headers
                if (!headers.contains(a.getAnnotator())) {
                    headers.add(a.getAnnotator());
                    headerTypes.add(a.getValueAt(0));
                }

                row.put(a.getAnnotator(), a.getValueAt(0));

            } else {
                List<String> newHeaders = new ArrayList<>();
                List<Either<String, Number>> newHeaderTypes
                        = new ArrayList<>();

                for (int i = 0; i < a.size(); i++) {
                    newHeaders.add(a.getAnnotator() + "$" + i);
                    newHeaderTypes.add(a.getValueAt(i));
                }

                // Check if the annotation is already tracked in the headers 
                // else, add all the new headers
                if (!headers.contains(newHeaders.get(0))) {
                    headers.addAll(newHeaders);
                    headerTypes.addAll(newHeaderTypes);
                }

                for (int i = 0; i < newHeaders.size(); i++) {
                    row.put(newHeaders.get(i), a.getValueAt(i));
                }

            }
        }

        // add the row
        rows.add(row);

    }

    /**
     * Prints all the annotations of the sentences contained in the component.
     *
     * @param c the component to analyze.
     */
    public void loadSentences(DocumentComponent c) {

        init();

        if (!c.hasComponents()) {
            // c is a sentence, so print its annotations
            addRow(c);
        } else {
            for (Sentence s : DocumentUtils.getSentences(c)) {
                addRow(s);
            }
        }
    }
    
    /**
     * Loads all the n-grams in the sheet.
     *
     * @param b the component to analyze.
     */
    public void loadGrams(Blackboard b) {

        init();

        for (Gram g : b.getGrams()) {
            addRow(g);
        }
    }

    /**
     * Loads all the n-grams in the sheet.
     *
     * @param c the component to analyze.
     */
    public void loadGrams(DocumentComponent c) {

        init();

        for (Gram g : c.getGrams()) {
            addRow(g);
        }
    }

    /**
     * Loads all the tokens in the sheet.
     *
     * @param c the component to analyze.
     */
    public void loadTokens(DocumentComponent c) {

        init();

        List<Token> tokens;

        if (!c.hasComponents()) {
            tokens = ((Sentence) c).getTokens();
        } else {
            tokens = new ArrayList<>();
            for (Sentence s : DocumentUtils.getSentences(c)) {
                tokens.addAll(s.getTokens());
            }
        }

        for (Token t : tokens) {
            addRow(t);
        }
    }

    /**
     * Clear the spreadsheet.
     */
    private void init() {
        headers = new ArrayList<>();
        rows = new ArrayList<>();
        headerTypes = new ArrayList<>();

        headers.add(ID_COLUMN);
        headerTypes.add(new Left<>(ID_COLUMN));
    }
}
