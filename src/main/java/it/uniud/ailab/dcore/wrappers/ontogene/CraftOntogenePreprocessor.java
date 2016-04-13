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
import it.uniud.ailab.dcore.annotation.annotators.GenericPreprocessor;
import it.uniud.ailab.dcore.annotation.annotators.GenericStructureAnnotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;
import it.uniud.ailab.dcore.utils.FileSystem;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class preprocesses NXML documents from the CRAFT corpus mimicking how
 * python-ontogene performs XML to plain text translation.
 *
 * @author Marco Basaldella
 * @see text_import/file_import.py in python-ontogene
 */
public class CraftOntogenePreprocessor extends GenericPreprocessor
        implements GenericStructureAnnotator {

    // a handy class-wide XPath instance to avoid reinstantiation in 
    // the recursive methods
    private XPath xpath;

    // the title of the document
    private String titleString;

    // the abstract of the document
    private String abstractString;

    // the body of the document
    private String bodyString;

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        xpath = XPathFactory.newInstance().newXPath();

        // Apply the same principles as the python-ontogene
        // To look at the original python code, look at  text_import/file_import.py
        applyPreprocess(blackboard, NXMLToArticle(blackboard.getText()));

        DocumentComposite titleComponent = new DocumentComposite(
                titleString,
                SECTION_TITLE
        );
        
        DocumentComposite abstractComponent = new DocumentComposite(
                abstractString,
                SECTION_ABSTRACT
        );
        
        DocumentComposite bodyComponent = new DocumentComposite(
                bodyString,
                SECTION_PREFIX + "body"
        );
        
        ((DocumentComposite) blackboard.getStructure()).
                addComponent(titleComponent);
        
        ((DocumentComposite) blackboard.getStructure()).
                addComponent(abstractComponent);
        
        ((DocumentComposite) blackboard.getStructure()).
                addComponent(bodyComponent);

    }

    /**
     * Convert a NXML input to a plain text one, following the same conversion
     * principles used by python-ontogene.
     *
     * @param nxml the original NXML string.
     * @return the scrapped plain text output.
     */
    private String NXMLToArticle(String nxml) {

        String cleanedText = "";
        try {

            // load the original xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            db.setEntityResolver((String publicId, String systemId) -> {
                Logger.getLogger(CraftOntogenePreprocessor.class.getName())
                        .log(
                                Level.WARNING,
                                "Ignoring {0}, {1}", new Object[]{publicId, systemId});
                return new InputSource(new StringReader(""));
            });

            Document sourceDocument = db.parse(new InputSource(
                    new StringReader(nxml)));

            // transform it to an ontogene-xml formatted document
            StreamSource stylesource = new StreamSource(
                    FileSystem.getInputStreamFromPath(
                            this.getClass().getClassLoader().getResource(
                                    "ontogene/pmc_to_og_xml.xsl").getFile()));

            Transformer transformer
                    = TransformerFactory.newInstance().
                    newTemplates(stylesource).newTransformer();

            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(sourceDocument),
                    new StreamResult(stringWriter));
            stringWriter.flush();

            // parse the transformed document
            Document transformedDocument = db.parse(
                    new InputSource(new StringReader(stringWriter.toString())));

            titleString = xpath.evaluate(
                    ".//title",
                    transformedDocument);

            titleString = strip(titleString);

            Node abstractNode = (Node) xpath.evaluate(
                    ".//abstract", transformedDocument,
                    XPathConstants.NODE);

            abstractString = NXMLRecursiveDescent(abstractNode);

            Node bodyNode = (Node) xpath.evaluate(
                    ".//body", transformedDocument,
                    XPathConstants.NODE);

            bodyString = NXMLRecursiveDescent(bodyNode);

            // assemble the document
            titleString = titleString + "\n\n";
            abstractString = "Abstract" + "\n\n" + abstractString; // + "\n\n";

            cleanedText = titleString + abstractString + bodyString;

        } catch (ParserConfigurationException | SAXException | IOException |
                TransformerException | XPathExpressionException ex) {
            Logger.getLogger(CraftOntogenePreprocessor.class.getName()).log(
                    Level.SEVERE, "Error while parsing NXML", ex);
            throw new AnnotationException(this, "Failed to parse NXML.", ex);
        }

        return cleanedText;

    }

    private String NXMLRecursiveDescent(Node node) {
        String retString = "";
        String newLine = "\n\n";

        if (node.getChildNodes().getLength() == 0 &&
                node.getTextContent().length() > 0) {
            retString += node.getTextContent() + newLine;
        }

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node child = node.getChildNodes().item(i);
            retString += NXMLRecursiveDescent(child);
        }

        return retString;
    }

    /**
     * Strips XML tags from a string, leaving only the content <b>inside</b>
     * that tags.
     *
     * @param originalString the XML string.
     * @return the content inside the XML tags in the input string.
     */
    private String strip(String originalString) {
        return originalString.replaceAll("<[^>]+>", "");

    }

}
