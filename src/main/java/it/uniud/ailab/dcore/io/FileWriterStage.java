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
package it.uniud.ailab.dcore.io;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.Stage;

/**
 * A generic stage used to write on files.
 *
 * @author Marco Basaldella
 */
public interface FileWriterStage extends Stage {

    /**
     * To avoid confusion, each different
     * {@link it.uniud.ailab.dcore.io.FileWriterStage} should write files using
     * a different suffix, defining such suffix using this method return value.
     *
     * @return the suffix of the file to write.
     */
    public abstract String getFileSuffix();

    /**
     * Writes informations from the {@link it.uniud.ailab.dcore.Blackboard} in a
     * file, which path is specified as parameter.
     *
     * @param file the path of the file to write.
     * @param b the Blackboard to read.
     */
    public abstract void writeFile(String file, Blackboard b);

    /**
     * This method allows the {@link it.uniud.ailab.dcore.Pipeline} to run the
     * Stage. This default implementation allows the concrete
     * {@link it.uniud.ailab.dcore.io.FileWriterStage}s to ignore the overlying
     * pipeline.
     *
     * @param b the Blackboard to read.
     */
    @Override
    default void run(Blackboard b) {
        writeFile(
                IOBlackboard.getOutputPathPrefix().
                concat(".").
                concat(getFileSuffix()).
                concat(".csv"),
                b);
    }

}
