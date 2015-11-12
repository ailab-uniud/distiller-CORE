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
package it.uniud.ailab.dcore.launchers;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.Distiller;
import it.uniud.ailab.dcore.DistillerFactory;
import it.uniud.ailab.dcore.io.CsvPrinter;
import it.uniud.ailab.dcore.utils.BlackboardUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Invocation of the Distiller on a file given as a command line argument.
 * With this configuration the distiller uses the coreNLP library of Stanford
 * University to preprocessing. More, it can calculate linguistis feature such 
 * as anaphora resolution, presence in anaphora,...
 * 
 * @author Giorgia Chiaradia
 */
public class StanfordKE {
     
    public static void main(String[] args) throws IOException {
        Distiller d = DistillerFactory.getStanfordCode();
        
        File f = new File("E:/loren.txt");
        
        String document = String.join(
                        " ",
                        Files.readAllLines(
                                f.toPath(),StandardCharsets.UTF_8));
        
        d.setVerbose(true);
        d.distill(document);
        
        Blackboard b = d.getBlackboard();
        BlackboardUtils.printScores(b,true);
        BlackboardUtils.printInference(b);
        
        CsvPrinter printer = new CsvPrinter();
        printer.writeGrams("/tmp/output.txt",b);
        
    }
}
