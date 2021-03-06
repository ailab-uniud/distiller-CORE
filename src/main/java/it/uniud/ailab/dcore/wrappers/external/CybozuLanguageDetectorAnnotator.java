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
package it.uniud.ailab.dcore.wrappers.external;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import java.util.Locale;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * Wrapper for the Cybozu Language Detector Library.
 * 
 * @author Marco Basaldella
 * @see <a href="https://code.google.com/p/language-detection/">language-detection</a>
 */
public class CybozuLanguageDetectorAnnotator implements Annotator {

    /**
     * The Cybozu detector object. The field is marked static to be optimized
     * for re-use, so that subsequent calls of annotate() don't have to reload
     * definitions every time, even for different instances of the annotator..
     */
    private static Detector detector = null;
    
    /**
     * The language profiles of the detector.
     */
    private final String profiles[] = new String[] { "af","ar","bg","bn","cs","da","de","el",
        "en","es","et","fa","fi","fr","gu","he","hi","hr","hu","id","it","ja",
        "kn","ko","lt","lv","mk","ml","mr","ne","nl","no","pa","pl","pt","ro",
        "ru","sk","sl","so","sq","sv","sw","ta","te","th","tl","tr","uk","ur",
        "vi","zh-cn","zh-tw"};
    

    /**
     * Wraps the Cybozu lybrary and detects the language over a specified
     * text.
     * 
     * @param text the text to analyze.
     * @return the code of the language detected
     * @throws LangDetectException when the model can't be loaded
     */
    public String detect(String text) throws LangDetectException {
        
        
        if (detector == null) {
            // retrieve the language database embedded in the jar
            // load the models inside an array then put them in
            // the library

            String[] models = new String[profiles.length];
            for (int i = 0; i < profiles.length; i++) {
                InputStream s = getClass().getClassLoader().
                        getResourceAsStream("cybozu/" + profiles[i]);
                try {
                    models[i] = IOUtils.toString(s, "UTF-8");
                } catch (IOException ex) {
                    Logger.getLogger(CybozuLanguageDetectorAnnotator.class.getName()).log(
                            Level.SEVERE, "Cannot load cybozu model " + profiles[i], ex);
                }
            }
            DetectorFactory.loadProfile(Arrays.asList(models));
        }
        detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }

    /**
     * Wraps the Cybozu lybrary and detects the most used probable language 
     * of the specified {@link it.uniud.ailab.dcore.persistence.DocumentComponent}.
     * Note: the component supports only components written in a single language
     * and with no children.
     * 
     * @param blackboard the current blackboard
     * @param component the component to analyze
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
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
