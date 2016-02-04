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

/**
 * Utility class for I/O.
 *
 * @author Marco Basaldella
 */
public class IO {

    /**
     * Get the temporary directory path of the underlying file system. On 
     * Windows, it should return the value of the %TMP% (or %TEMP%) environment
     * variable.
     * 
     * @return the temporary directory path.
     * @see <a href="https://blogs.msdn.microsoft.com/oldnewthing/20150417-00/?p=44213/">
     * Why are there both TMP and TEMP environment variables, and which one is right?</a>
     */
    public static String getTmpPath() {
        return System.getProperty("java.io.tmpdir");
    }
}
