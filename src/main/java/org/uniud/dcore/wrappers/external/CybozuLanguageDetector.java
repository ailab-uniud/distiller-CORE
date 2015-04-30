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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uniud.dcore.engine.Annotator;
import org.uniud.dcore.persistence.AnnotatorException;
import org.uniud.dcore.persistence.DocumentComponent;

/**
 *
 * @author Marco Basaldella
 */
public class CybozuLanguageDetector implements Annotator {


    public String detect(String text) throws LangDetectException {
        
        // retrieve the language database embedded in the jar
        DetectorFactory.loadProfile(
            getClass().getClassLoader().getResource("cybozu").getFile());
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }
    
    @Override
    public void annotate(DocumentComponent component) throws AnnotatorException {
        
        String lang = "";
        try {
            lang = detect(component.getText());
        } catch (LangDetectException ex) {
            throw new AnnotatorException("CybozuLanguageDetector - error during language detection",ex);
        }
        if (lang.isEmpty()) {
            throw new AnnotatorException("CybozuLanguageDetector could not detect a language");
        }        
        Locale loc = Locale.forLanguageTag(lang);
        component.setLanguage(loc);
    }
    
}
