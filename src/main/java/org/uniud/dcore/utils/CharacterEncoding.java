/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uniud.dcore.utils;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * The FINAL SOLUTION to character encoding issues. STFU & GTFO
 *
 * @author Dado
 */
public class CharacterEncoding {

    public static Charset detectCharset(File file) throws FileNotFoundException, IOException {
        InputStream streamdata = new FileInputStream(file);
        return detectCharsetFromStream(streamdata);
    }

    public static Charset detectCharsetFromStream(InputStream streamdata) throws IOException {
        BufferedInputStream bufferedData = new BufferedInputStream(streamdata);
        CharsetDetector detector = new CharsetDetector();
        detector.setText(bufferedData);
        CharsetMatch match = detector.detect();
        return Charset.forName(match.getName());
    }

    public static String readFromFile(File file, Charset charset) throws IOException {
        InputStream in = new FileInputStream(file);
        return readFromStream(in, charset);
    }

    public static String readFromStream(InputStream in, Charset charset) throws IOException {
        Closeable stream = in;
        try {
            Reader reader = new InputStreamReader(in, charset);
            stream = reader;
            StringBuilder inputBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            while (true) {
                int readCount = reader.read(buffer);
                if (readCount < 0) {
                    break;
                }
                inputBuilder.append(buffer, 0, readCount);
            }
            return inputBuilder.toString().toLowerCase();
        } finally {
            stream.close();
        }
    }
}
