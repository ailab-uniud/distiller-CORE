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
package it.uniud.ailab.dcore.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author Giorgia Chiaradia
 */
public class GenderUtils {
    
    /**
     * Enumerator for gender.
     */
    public enum Gender {
        MASCULINE,
        FEMININE,
        NEUTRAL,
        UNDEFINED;
    }
    
    private List<String> masculines = new ArrayList<>();
    private List<String> feminines = new ArrayList<>();
    private List<String> neutrals = new ArrayList<>();
    private String pathM = "gender/masculine.unigrams.txt";
    private String pathF = "gender/feminine.unigrams.txt";
    private String pathN = "gender/neutral.unigrams.txt";
    
    public void createGendersDictionaries() throws IOException {
        
        InputStreamReader is
                = FileSystem.getInputStreamReaderFromPath(getClass().getClassLoader().
                getResource(pathM).getFile());
        List<String> ms
                = new BufferedReader(is).lines().collect(Collectors.toList());
//        List<String> ms = Files.readAllLines(new File(pathM).toPath());
        for (String m : ms) {
            m = m.trim();
            masculines.add(m);
        }
        InputStreamReader is1 = FileSystem.getInputStreamReaderFromPath(getClass().getClassLoader().
                getResource(pathF).getFile());
        List<String> fs
                = new BufferedReader(is1).lines().collect(Collectors.toList());
        for (String f : fs) {
            f = f.trim();
            feminines.add(f);
        }
        
        InputStreamReader is2 = FileSystem.getInputStreamReaderFromPath(getClass().getClassLoader().
                getResource(pathN).getFile());
        List<String> ns
                = new BufferedReader(is2).lines().collect(Collectors.toList());
        for (String n : ns) {
            n = n.trim();
            neutrals.add(n);
        }
    }
    
    public Gender findGender(String word) {

        if (masculines.contains(word)) {
            return Gender.MASCULINE;
        } else if (feminines.contains(word)) {
            return Gender.FEMININE;
        } else if (neutrals.contains(word)) {
            return Gender.NEUTRAL;
        } else {
            return Gender.UNDEFINED;
        }
    }
}
