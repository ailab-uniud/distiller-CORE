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
package it.uniud.ailab.dcore.annotation;

/**
 * A set of default annotations. An annotator that produces an annotation that
 * is (conceptually equal to one) listed in this file should use the constant
 * defined there.
 *
 * @author Marco Basaldella
 */
public class DefaultAnnotations {
    
    public static final String ID = "ID";

    public static final String START_INDEX = "StartPosition";

    public static final String END_INDEX = "EndPosition";

    public static final String SENTENCE_COUNT = "SentenceCount";

    public static final String CHAR_COUNT = "CharCount";

    public static final String WORD_COUNT = "WordCount";

    public static final String PHRASES_COUNT = "PhrasesCount";

    public static final String IS_NER = "IsANer";

    public static final String IN_TITLE = "InTitle";

    public static final String IN_ABSTRACT = "InAbstract";

    public static final String TF = "tf";

    public static final String IDF = "idf";

    public static final String TFIDF = "tf-idf";

    public static final String SURFACE = "Surface";

    public static final String POS_TAG = "pos_tag";

    public static final String STEM = "stem";

    public static final String LEMMA = "stem";

    public static final String UPPERCASE_COUNT = "UpperCaseCount";

    public static final String LOWERCASE_COUNT = "LowerCaseCount";

    public static final String DIGITS_COUNT = "DigitsCount";

    public static final String SPACE_COUNT = "SpaceCount";

    public static final String SYMBOLS_COUNT = "SymbolsCount";

    public static final String DASH_COUNT = "DashCount";

    public static final String ALL_UPPERCASE = "AllUppercase";

    public static final String ALL_LOWERCASE = "AllLowercase";

    public static final String INSIDE_CAPITALIZATION = "InsideCapitalization";

    public static final String END_NUMBER = "EndsWithNumber";

}
