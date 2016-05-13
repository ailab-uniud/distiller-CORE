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
package it.uniud.ailab.dcore.wrappers.ontogene;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Keyphrase;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Checks if a keyphrase appears in the CRAFT corpus by looking for it inside
 * the knowtator XML files.
 * 
 * @author Marco Basaldella
 */
public class KnowtatorEntityCheckerAnnotator implements Annotator {

    private String inputDirectory;
    private static Map<String, Set<String>> terms = null;

    public static final String CRAFT_TERM = "CraftTerm";

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        if (terms == null) {
            terms = new HashMap<>();
            if (inputDirectory == null || inputDirectory.isEmpty()) {
                throw new AnnotationException(this,
                        "You must set an input directory.");
            }
            load(inputDirectory);
        }

        Set<String> validTerms;
        try {
            validTerms = terms.get(
                    OntogeneUtils.getCurrentDocumentPMID(inputDirectory));
        } catch (IOException ex) {
            Logger.getLogger(KnowtatorEntityCheckerAnnotator.class.getName()).log(Level.SEVERE, null, ex);
            validTerms = null;
        }
        
        if (validTerms == null) {
            throw new AnnotationException(this,
                        "Unable to determine the PMID of the current document.");
        }

        Collection<Keyphrase> kps = blackboard.getGramsByType(Keyphrase.KEYPHRASE);

        for (Keyphrase k : kps) {
            
            if (validTerms.contains(k.getSurface().toLowerCase(Locale.ENGLISH))) {
                k.addAnnotation(
                        new FeatureAnnotation(CRAFT_TERM, 1));
            } else {
                k.addAnnotation(
                        new FeatureAnnotation(CRAFT_TERM, 0));
            }

        }

    }

    /**
     * This variable must point to the "knowtator-xml" directory inside the
     * CRAFT corpus folder.
     * 
     * @param inputDirectory the path of the "knowtator-xml" inside the CRAFT 
     * corpus folder.
     */
    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    private static void load(String inputDir) throws RuntimeException {
        
        if (!inputDir.endsWith(FileSystem.getSeparator())) {
            inputDir  += FileSystem.getSeparator();
        }
        
        inputDir += "knowtator-xml";
        
        File rootDirectory = new File(inputDir);

        for (File directory : rootDirectory.listFiles()) {
            System.out.println("Analyzing " + directory.getAbsolutePath() + "...");

            if (directory.isDirectory()) {
                if (directory.getName().equals("sections-and-typography")) {
                    System.out.println("Skipping current dir");
                } else {
                    analyzeDir(directory);
                }

            } else {
                throw new RuntimeException("Expecting only directories in the root folder");
            }
        }
    }

    private static void analyzeDir(File directory) {

        for (File f : directory.listFiles()) {
            String fileName = f.getName();
            fileName = fileName.substring(0, fileName.indexOf('.'));

            if (!terms.containsKey(fileName)) {
                System.out.println("Adding document " + fileName + "...");
                terms.put(fileName, new HashSet<String>());
            }

            analyzeFile(f, fileName);
        }
    }

    private static void analyzeFile(File f, String fileID) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(
                    new InputSource(new FileInputStream(f)));

            NodeList nodes = (NodeList) xpath.evaluate(".//spannedText",
                    document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                addTerm(fileID, node.getTextContent());
            }

        } catch (ParserConfigurationException | SAXException | IOException |
                XPathExpressionException ex) {
            Logger.getLogger(KnowtatorEntityCheckerAnnotator.class.getName()).log(Level.SEVERE, null, ex);
            throw new AnnotationException((new KnowtatorEntityCheckerAnnotator()),
                    "Failed to load Knowtator xml file " + f.getAbsolutePath(), ex);
        }
    }

    private static void addTerm(String fileID, String term) {
        term = term.trim().toLowerCase(Locale.ENGLISH);
        terms.get(fileID).add(term);
    }

}
