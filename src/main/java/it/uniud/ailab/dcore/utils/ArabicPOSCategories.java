/*
 * Copyright (C) 2017 Artificial Intelligence
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
package it.uniud.ailab.dcore.utils;

/**
 *
 * @author Muhammad Helmy
 */
public enum ArabicPOSCategories {
    NOUN("NN|NNS|NNP|NNPS|DTNN|DTNNS|DTNNP|DTNNPS"),
    ADJ("JJ|JJR|JJS|DTJJ|DTJJR|DTJJS"),
    CONNECTOR("CC|CD|IN|MD|PDT|POS|RP|UH|WDT|WP|WP\\$|WRB|DT"),//PRP|PRP\\$|
    CONNECTORUSD("CC|CD|IN|MD|PDT|POS|PRP|PRP$|RP|UH|WDT|WP|WP$|WRB|DT"),
    VERB("VBD|VBG|VBN|VBP|VBZ|VB"),
    ADVERB("RB|RBR|RBS");
    private ArabicPOSCategories(String tag){
        this.tag = tag;
    }
    private String tag;
    public String getTag(){
        return this.tag;
    }
    public String toString(){
        return this.tag;
    }
}
