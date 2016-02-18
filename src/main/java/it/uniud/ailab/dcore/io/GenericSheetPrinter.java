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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotable;
import it.uniud.ailab.dcore.annotation.Annotation;
import it.uniud.ailab.dcore.persistence.*;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.utils.Either;
import it.uniud.ailab.dcore.utils.Either.Left;
import java.util.ArrayList;
import java.util.Collection;
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
    private final boolean allowDuplicates;

    private final String ID_COLUMN = "ID";

    /**
     * Generates a generic sheet printer specifying whether it should allow
     * lines with the same ID or not.
     *
     * @param allowDuplicates true if lines with the same ID are allowed; false
     * otherwise.
     */
    protected GenericSheetPrinter(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
        init();
    }

    /**
     * Writes the annotations contained in the printer on the specified file.
     *
     * @param fileName the path where to write.
     */
    public abstract void writeFile(String fileName);

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
     * Add all the values of another printer into the current printer.
     *
     * @param p the printer to merge into the current one.
     */
    public void addPrinter(GenericSheetPrinter p) {

        // merge headers
        for (int i = 0; i < p.getHeaders().size(); i++) {
            int h;
            if ((h = headers.indexOf(p.getHeaders().get(i))) >= 0) {
                if (!(headerTypes.get(h).isLeft()
                        == p.getHeaderTypes().get(i).isLeft())) {
                    throw new UnsupportedOperationException("Trying to merge "
                            + "header with the same name but different type: "
                            + p.getHeaders().get(i));
                }
            } else {
                headers.add(p.getHeaders().get(i));
                headerTypes.add(p.getHeaderTypes().get(i));
            }
        }

        // merge lines
        for (Map<String, Either<String, Number>> row : p.getRows()) {
            rows.add(row);
        }
    }

    /**
     * Stores a generic annotable object.
     *
     * @param a the Annotable to print.
     */
    private void addRow(Annotable annotable) {

        Map<String, Either<String, Number>> row = new HashMap<>();

        String rowId = annotable.getIdentifier();

        if (containsRow(rowId) && !allowDuplicates) {
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
        Collection<Gram> grams = b.getKeyphrases();
        for (Gram g : b.getKeyphrases()) {
            Keyphrase k = (Keyphrase) g;
            addRow(k);
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
            Keyphrase k = (Keyphrase) g;
            addRow(k);
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
     * Adds to all rows a field with the specified value.
     * 
     * @param key the name of the column to add.
     * @param value the value of the field to add.
     */
    public void addToAll(String key, String value) {
        headers.add(key);
        headerTypes.add(new Left<>(key));
        for (Map<String, Either<String, Number>> row: rows) {
            row.put(key, new Left<>(value));
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
