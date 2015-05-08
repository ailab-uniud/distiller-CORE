/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
 * 
 * 	Distiller-CORE is free software; you can redistribute it and/or
 * 	modify it under the terms of the GNU Lesser General Public
 * 	License as published by the Free Software Foundation; either
 * 	version 2.1 of the License, or (at your option) any later version.
 *
 * 	Distiller-CORE is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * 	Lesser General Public License for more details.
 *
 * 	You should have received a copy of the GNU Lesser General Public
 * 	License along with this library; if not, write to the Free Software
 * 	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * 	MA 02110-1301  USA or see <http://www.gnu.org/licenses/>.
 */
package org.uniud.dcore.annotation.grams;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.Gram;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;
import org.uniud.dcore.utils.DocumentUtils;

/**
 * Annotates the emotional intensity of grams; currently only the English language
 * is supported.
 * 
 * @author Marco Basaldella
 * @see http://www2.imm.dtu.dk/pubdb/views/publication_details.php?id=6010
 */
public class SyuzhetAnnotator implements Annotator {
    
    public static final String INTENSITY = "Syuzhet";
    private static final String INTENSITY_COUNTER = "Syuzhet$counter";
    
    private enum Mode {
        AVERAGE,
        SUM;
    }
    
    private Mode mode = Mode.SUM;
    
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
    public void annotate(DocumentComponent component) {
        
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
