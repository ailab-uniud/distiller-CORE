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
package it.uniud.ailab.dcore.annotation.annotators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.DocumentUtils;

/**
 * Annotates the emotional intensity of grams; currently only the English language
 * is supported.
 * 
 * @author Marco Basaldella
 * @see http://www2.imm.dtu.dk/pubdb/views/publication_details.php?id=6010
 */
public class SyuzhetAnnotator implements Annotator {
    
    public static final String INTENSITY = "Syuzhet";
    private static final String INTENSITY_COUNTER = "Syuzhet_counter";
    
    private enum Mode {
        AVERAGE,
        SUM;
    }
    
    private Mode mode = Mode.AVERAGE;
    
    private Map<String,Integer> weights = new HashMap<>();

    private Map<Gram,Integer> counter = new HashMap<>();
    
    /**
     * Loads the word valence database created by Finn Årup Nielsen.
     */
    private void loadDefinitions() {
        String csvFile = getClass().getClassLoader().getResource("afinn/afinn.txt").getFile();
	BufferedReader br = null;
	String line;
	String cvsSplitBy = "\t";
 
	try {
            
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
        	String[] parsedLine = line.split(cvsSplitBy);
                weights.put(parsedLine[0],Integer.parseInt(parsedLine[1]));
 
            }
 
	} catch (FileNotFoundException e) {
            e.printStackTrace();
	} catch (IOException e) {
            e.printStackTrace();
	} finally {
            if (br != null) {
		try {
                    br.close();
		} catch (IOException e) {
                     // don't care
		}
            }
	}
    }

    /**
     * Annotates the n-grams with their document intensity. The gram document
     * intensity is calculated as the mean intensity of the phrases where
     * the gram appears, where the intensity of a sentence is the sum of the
     * intensity of all its words.
     * 
     * @param component 
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
        // skip non-english components
        if (!component.getLanguage().getLanguage().equals("en")) {
            return;
        }
        
        loadDefinitions();
        
        List<Sentence> sentences = DocumentUtils.getSentences(component);
        
        for (Sentence s : sentences) {
            
            if (s.getGrams().isEmpty())
                continue;
            
            int accumulator = 0;
            for (Token t : s.getTokens()) {
                
                if (weights.containsKey(t.getText()))
                    accumulator+= weights.get(t.getText());
            }
            
            if (mode == Mode.AVERAGE) {
            
                for (Gram g : s.getGrams())
                {
                    if (!g.hasFeature(INTENSITY_COUNTER)) {
                        g.putFeature(INTENSITY, Math.abs(accumulator));
                        g.putFeature(INTENSITY_COUNTER,1);
                    } else {
                        double counter = g.getFeature(INTENSITY_COUNTER);

                        // recursively increment the average
                        double avg = Math.abs(accumulator) + 
                                (g.getFeature(INTENSITY) *
                                counter);

                        avg = avg / ++counter;
                        g.putFeature(INTENSITY,avg);
                        g.putFeature(INTENSITY_COUNTER,counter);
                    }
                }
            } else { // (mode == Mode.SUM)
                for (Gram g : s.getGrams())
                {
                    if (!g.hasFeature(INTENSITY_COUNTER)) {
                        g.putFeature(INTENSITY, Math.abs(accumulator));
                        g.putFeature(INTENSITY_COUNTER,1);
                    } else {
                        // increment the intensity
                        double avg = Math.abs(accumulator) + g.getFeature(INTENSITY);
                        g.putFeature(INTENSITY,avg);
                    }
                }
            }
        }
    }
    
}
