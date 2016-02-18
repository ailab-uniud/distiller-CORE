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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for I/O.
 *
 * @author Marco Basaldella
 */
public class FileSystem {

    /**
     * Get an {@link java.io.InputStreamReader} from the provided path, 
     * where the path can  point both to the underlying OS' file system or to 
     * a file contained inside a JAR file.
     *
     * @param path the path which needs to be converted to an InputStream
     * @return the resulting {@link java.io.InputStreamReader} 
     * @throws FileNotFoundException if the file does not exists
     */
    public static InputStreamReader getInputStreamReaderFromPath(String path)
            throws FileNotFoundException {
        InputStreamReader isr;

        // running from command-line and loading inside the JAR
        if (path.contains("!")) {
            isr = new InputStreamReader(
                    (new FileSystem()).getClass().getResourceAsStream(
                            path.substring(
                                    path.lastIndexOf("!") + 1)));
        } else {
            // normal operation
            isr = new FileReader(path);
        }

        return isr;
    }

    /**
     * Get an {@link java.io.InputStream} from the provided path, 
     * where the path can  point both to the underlying OS' file system or to 
     * a file contained inside a JAR file.
     *
     * @param path the path which needs to be converted to an InputStream
     * @return the resulting {@link java.io.InputStream}
     * @throws FileNotFoundException if the file does not exists
     */
    public static InputStream getInputStreamFromPath(String path) throws FileNotFoundException {

        InputStream is;

        // running from command-line and loading inside the JAR
        if (path.contains("!")) {
            is = (new FileSystem()).getClass().getResourceAsStream(
                    path.substring(
                            path.lastIndexOf("!") + 1));
        } else {
            // normal operation
            is = new FileInputStream(path);
        }

        return is;
    }

    /**
     * Get the temporary directory path of the underlying file system. On
     * Windows, it should return the value of the %TMP% (or %TEMP%) environment
     * variable.
     *
     * @return the temporary directory path.
     * @see <a href="https://blogs.msdn.microsoft.com/oldnewthing/20150417-00/?p=44213/">
     * Why are there both TMP and TEMP environment variables, and which one is
     * right?</a>
     */
    public static String getTmpPath() {
        return System.getProperty("java.io.tmpdir");
    }
    
    /**
     * Get the separator for the underlying OS. 
     * 
     * @return the path separator for the underlying OS.
     */
    public static String getSeparator() {
        return java.io.File.separator;
    }
}
