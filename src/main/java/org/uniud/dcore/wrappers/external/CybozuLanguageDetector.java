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
package org.uniud.dcore.wrappers.external;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import java.util.Locale;
import org.uniud.dcore.annotation.AnnotationException;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.DocumentComponent;

/**
 * Wrapper for the Cybozu Language Detector Library.
 * 
 * @author Marco Basaldella
 * @see <a href="https://code.google.com/p/language-detection/">language-detection</a>
 */
public class CybozuLanguageDetector implements Annotator {

    /**
     * The Cybozu detector object. The field is marked static to be optimized
     * for re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator..
     */
    Detector detector = null;
    

    /**
     * Wraps the Cybozu lybrary and detects the language over a specified
     * text.
     * 
     * @param text the text to analyze.
     * @return the code of the language detected
     * @throws LangDetectException
     */
    public String detect(String text) throws LangDetectException {

        if (detector == null) {
            // retrieve the language database embedded in the jar
            DetectorFactory.loadProfile(
                    getClass().getClassLoader().getResource("cybozu").getFile());            
        }
        detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }

    /**
     * Wraps the Cybozu lybrary and detects the most used probable language 
     * of the specified {@link org.uniud.dcore.persistence.DocumentComponent}.
     * Note: the component supports only components written in a single language
     * and with no children.
     * 
     * @param component
     * @throws AnnotatorException 
     */
    @Override
    public void annotate(DocumentComponent component) {
        
        String lang = "";
        try {
            lang = detect(component.getText());
        } catch (LangDetectException ex) {
            throw new AnnotationException(this,"CybozuLanguageDetector - error during language detection",ex);
        }
        if (lang.isEmpty()) {
            throw new AnnotationException(this,"CybozuLanguageDetector could not detect a language");
        }        
        Locale loc = Locale.forLanguageTag(lang);
        component.setLanguage(loc);
    }
    
}
