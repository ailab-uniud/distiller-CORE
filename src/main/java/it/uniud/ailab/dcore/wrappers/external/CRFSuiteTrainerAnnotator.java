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
package it.uniud.ailab.dcore.wrappers.external;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author red
 */
public class CRFSuiteTrainerAnnotator implements Annotator {

    private String targetColumn = null;

    // Static field containing the file that will contain the training.
    private static Path trainingFileName ;

    /**
     * This column will be the <b>last</b> column in the training file, so it
     * will be used by CRFSuite to train the model.
     *
     * @param targetColumn
     */
    public void setTargetColumn(String targetColumn) {
        this.targetColumn = targetColumn;
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {
        if (targetColumn == null || targetColumn.isEmpty()) {
            throw new RuntimeException(
                    "Target column not set. \n"
                    + "You must set the annotation used by CRFSuite to train the model!");
        }

        if (trainingFileName == null) {
            trainingFileName = createRandomFile();
        }

        printTrainingFile(blackboard, component);

    }

    /**
     * Prints the training file.
     */
    private void printTrainingFile(Blackboard blackboard, DocumentComponent component) {

    }

    /**
     * Generates the training file using a random file name and creating an
     * empty file.
     *
     * @return the path to file that will contain the training data.
     */
    private Path createRandomFile() {

        Path path = Paths.get(FileSystem.getDistillerTmpPath());
        File f;

        do {
            // csv format for easy debugging
            String name = UUID.randomUUID().toString().replace("-", "") + ".csv";
            path = path.resolve(name);
            f = path.toFile();
        } while (f.exists());
                
        // create (touch) the file. 
        try {
            new FileOutputStream(f).close();
        } catch (IOException ex) {
            Logger.getLogger(CRFSuiteTrainerAnnotator.class.getName()).
                    log(Level.SEVERE, "Unable to create training file.", ex);
            throw new RuntimeException("Unable to create training file.");
        }
        
        return path;    
    }

}
