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

/**
 * A class used by I/O classes to share informations about the reading/writing
 * process.
 * 
 * @author Marco Basaldella
 */
public final class IOBlackboard {
    
    // Where the output classes should write
    private static String outputPathPrefix;

    /**
     * Get the prefix of the output path where the module should write.
     * 
     * @return the output path.
     */
    public static String getOutputPathPrefix() {
        return outputPathPrefix;
    }

    /**
     * Set the prefix of the output path where the module should write.
     * 
     * @param outputPath the output path.
     */
    public static void setOutputPathPrefix(String outputPath) {
        outputPathPrefix = outputPath;
    }
}
