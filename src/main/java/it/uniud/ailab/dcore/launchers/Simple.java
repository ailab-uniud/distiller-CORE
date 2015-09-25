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
 * Simple invocation of the Distiller on a file given as a command line argument.
 *
 * @author Marco Basaldella
 */
public class Simple {
    
    public static void main(String[] args) throws IOException {
        Distiller d = DistillerFactory.getDefaultCode();
        
        File f = new File(args[0]);
        
        String document = String.join(
                        " ",
                        Files.readAllLines(
                                f.toPath(),StandardCharsets.UTF_8));
        
        d.setVerbose(true);
        d.distill(document);
        
        Blackboard b = d.getBlackboard();
        BlackboardUtils.printScores(b,true);
        BlackboardUtils.printInference(b);
        
        CsvPrinter printer = new CsvPrinter("/tmp/output.txt");
        printer.printGrams(b);
        
    }
    
}
