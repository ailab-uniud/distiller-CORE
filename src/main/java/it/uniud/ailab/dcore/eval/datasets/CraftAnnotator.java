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

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.io.IOBlackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import it.uniud.ailab.dcore.utils.ComparablePair;
import it.uniud.ailab.dcore.utils.DocumentUtils;
import it.uniud.ailab.dcore.wrappers.ontogene.KnowtatorEntityCheckerAnnotator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Loader for the CRAFT corpus.<br />
 * <br />
 * The input directory should be strucured as <br />
 * . <br />
 * ├── tsv the TSV files produced by OntoGene (for processing with OntoGene
 * pipeline) <br />
 * ├── txt txt files from the CRAFT corpus <br />
 * └── knowtator-xml should contain one or more ontologies contained in the
 * knowtator-xml folder of the CRAFT corpus
 *
 *
 * @author Marco Basaldella
 */
public class CraftAnnotator implements Annotator {

    private SortedMap<ComparablePair<Integer, Integer>, String> documentTermMap = new TreeMap<>();
    private String datasetPath;

    private List<String> excludes = Arrays.asList("sections-and-typography");
    
    public final static String CRAFT_TOKEN = "Craft_Token";

    private void loadTerms() {

        Path datasetPath = Paths.get(IOBlackboard.getCurrentDocument());
        String fileID = datasetPath.getFileName().toString() + ".knowtator.xml";

        datasetPath = datasetPath.getParent().getParent().resolve("knowtator-xml");

        for (File directory : datasetPath.toFile().listFiles()) {

            if (directory.isDirectory()) {

                if (!excludes.contains(directory.getName())) {
                    System.out.println("Analyzing " + directory.getAbsolutePath() + "...");
                    analyzeFile(directory.toPath().resolve(fileID).toFile());
                }

            } else {
                throw new RuntimeException("Expecting only directories in the knowtator-xml folder");
            }
        }
    }

    /**
     * Analyze a single file in a knowtator-xml folder and extract all the
     * annotations contained in it.
     *
     * @param f the file to analyze.
     */
    private void analyzeFile(File f) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(
                    new InputSource(new FileInputStream(f)));

            NodeList nodes = (NodeList) xpath.evaluate(".//annotation",
                    document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {

                Element el = (Element) nodes.item(i);
                Node spanNode = el.getElementsByTagName("span").item(0);

                int spanStart
                        = Integer.parseInt(spanNode.getAttributes().
                                getNamedItem("start").getNodeValue());
                int spanEnd
                        = Integer.parseInt(spanNode.getAttributes().
                                getNamedItem("end").getNodeValue());

                Node textNode = el.getElementsByTagName("spannedText").item(0);

                String text = textNode.getTextContent();

                documentTermMap.put(new ComparablePair(spanStart, spanEnd), text);
            }

        } catch (ParserConfigurationException | SAXException | IOException |
                XPathExpressionException ex) {
            Logger.getLogger(KnowtatorEntityCheckerAnnotator.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(
                    "Failed to load Knowtator xml file " + f.getAbsolutePath(), ex);
        }
    }

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        // load the terms
        loadTerms();

        // annotate the tokens using these terms
        annotateTokens(component);

    }

    private void annotateTokens(DocumentComponent component) {
        int currentTerm = 0;
        // annotate the tokens
        for (Sentence s : DocumentUtils.getSentences(component)) {
            for (int i = 0; i < s.getTokens().size(); i++) {
                Token t = s.getTokens().get(i);

                if (t.getAnnotation(DefaultAnnotations.START_INDEX).
                        getNumberAt(0).intValue()
                        >= documentTermMap.firstKey().getLeft()) {

                    t.addAnnotation(
                            new TextAnnotation(CRAFT_TOKEN,"B_CRAFT"));

                    for (int j = i + 1;
                            j < s.getTokens().size()
                            && s.getTokens().
                            get(j).
                            getAnnotation(DefaultAnnotations.START_INDEX).
                            getNumberAt(0).intValue()
                            < documentTermMap.firstKey().getRight();
                            j++) {

                        s.getTokens().get(j).addAnnotation(
                            new TextAnnotation(CRAFT_TOKEN,"I_CRAFT"));

                        // this is safe because
                        // 1- the upper limit of the two for cycles is the same 
                        // 2- no tokens will be skipped because even if the 
                        // annotations spans just one token the condition will be
                        // immediatly false; likewise, the condition fails as
                        // soon the last token is added, so in the next iteration
                        // the intermediate for cycle we'll have i = j+1, i.e.
                        // no tokens will be skipped.
                        i = j;
                    }

                    documentTermMap.remove(documentTermMap.firstKey());
                    //  if the term map is finished, we don't need to analyze
                    // the remaining tokens.
                    if (documentTermMap.isEmpty()) {
                        return;
                    }
                } else {
                    // the token is not marked in the CRAFT corpus.
                    t.addAnnotation(
                            new TextAnnotation(CRAFT_TOKEN,"NO_CRAFT"));
                }
            }
        }
    }

}
