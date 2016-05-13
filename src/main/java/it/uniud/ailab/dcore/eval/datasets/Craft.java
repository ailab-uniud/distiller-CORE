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
package it.uniud.ailab.dcore.eval.datasets;

import it.uniud.ailab.dcore.eval.GenericDataset;
import it.uniud.ailab.dcore.utils.FileSystem;
import it.uniud.ailab.dcore.wrappers.ontogene.KnowtatorEntityCheckerAnnotator;
import it.uniud.ailab.dcore.wrappers.ontogene.OntogeneUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
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
 * Loader for the CRAFT corpus. 
 * 
 * The input directory should be strucured as
 * .
 * ├── tsv      the TSV files produced by OntoGene (for processing with OntoGene pipeline)
 * ├── txt      txt files from the CRAFT corpus
 * └── knowtator-xml      should contain one or more ontologies contained in the knowtator-xml folder of the CRAFT corpus

 *
 * @author Marco Basaldella
 */
public class Craft extends GenericDataset {
    
    private Map<String, Set<String>> documentTermMap = new HashMap<>();

    public Craft(String goldStandardPath) {
        super(goldStandardPath, "CRAFT");
    }

    @Override
    public Map<String, String> loadTestSet() {

        // zero-sized output
        Map<String, String> documents = new HashMap<>();
        return documents;
    }

    @Override
    public Map<String, String[]> loadTestAnswers() {

        // default is a zero-sized output array
        Map<String, String[]> keyphrases = null;
        return keyphrases;
    }

    @Override
    protected Map<String, String> loadTrainingSet() {
        Map<String, String> documents = new HashMap<>();

        try {
            File[] dir = new File(datasetPath + 
                    FileSystem.getSeparator() +
                    "txt").listFiles();
            Arrays.sort(dir);

            for (File f : dir) {

                String document = String.join(
                            "\n",
                            Files.readAllLines(
                                    f.toPath(), StandardCharsets.UTF_8));

                    String docName = f.getName().substring(0,f.getName().indexOf("."));

                    documents.put(docName, document);
            }
        } catch (IOException ex) {
            Logger.getLogger(SemEval2010.class.getName()).log(Level.SEVERE, null, ex);
        }

        return documents;
    }

    @Override
    protected Map<String, String[]> loadTrainingAnswers() {

        Map<String, String[]> keyphrases = new HashMap<>();

        String inputDir = datasetPath;
        
        if (!inputDir.endsWith(FileSystem.getSeparator())) {
            inputDir  += FileSystem.getSeparator();
        }
        
        inputDir += "knowtator-xml";
        
        File rootDirectory = new File(inputDir);

        for (File directory : rootDirectory.listFiles()) {
            System.out.println("Analyzing " + directory.getAbsolutePath() + "...");

            if (directory.isDirectory()) {
                analyzeDir(directory);
            } else {
                throw new RuntimeException("Expecting only directories in the root folder");
            }
        }
        
        for (Map.Entry<String,Set<String>> documentTerms : documentTermMap.entrySet()) {
            
            keyphrases.put(
                documentTerms.getKey(),
                    documentTerms.getValue().toArray(new String[0]));
            
        }
        
        return keyphrases;
    }

    private void analyzeDir(File directory) {

        for (File f : directory.listFiles()) {
            String fileName = f.getName();
            fileName = fileName.substring(0, fileName.indexOf('.'));
            
            String ontogeneID = null;
            try {
                ontogeneID = OntogeneUtils.
                        PMIDtoOntogeneFileID(datasetPath, fileName);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to determine the correct "
                        + "Ontogene ID of document " + fileName,ex);
            }

            if (!documentTermMap.containsKey(ontogeneID)) {
                System.out.println("Adding document " + ontogeneID + "...");
                documentTermMap.put(ontogeneID, new HashSet<String>());
            }

            analyzeFile(f, ontogeneID);
        }
    }

    private void analyzeFile(File f, String fileID) {
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
            throw new RuntimeException(
                    "Failed to load Knowtator xml file " + f.getAbsolutePath(), ex);
        }
    }

    private void addTerm(String fileID, String term) {
        term = term.trim().toLowerCase(Locale.ENGLISH);
        documentTermMap.get(fileID).add(term);
    }

    /**
     * Compares a <b>candidate</b> item with a <b>dataset provided</b> item. Please
     * note that the the object to test <b>must</b> be passed as first
     * parameter, while the object to test against <b>must</b> be passed as
     * second parameter.
     *
     * @param o1 the object to test, generated by the Distiller
     * @param o2 the reference object, provided by the training set.
     * @return 0 if o1 and o2 are equal, another number (indetermined)
     * otherwise.
     */
    @Override
    public int compare(String o1, String o2) {
        return o1.trim().toLowerCase().compareTo(o2.trim().toLowerCase());
        
    }

    /**
     * {@inheritDoc} 
     * @return {@inheritDoc} 
     */
    @Override
    public String getTrainingFolder() {
        return datasetPath + FileSystem.getSeparator()+ "train";
    }

    /**
     * {@inheritDoc} 
     * @return {@inheritDoc} 
     */
    @Override
    public String getTestFolder() {
        return datasetPath + FileSystem.getSeparator()+ "test";
    }

}
