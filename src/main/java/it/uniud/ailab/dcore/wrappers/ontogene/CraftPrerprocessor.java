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
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Marco Basaldella
 */
public class CraftPrerprocessor extends GenericPreprocessor {

    // a handy class-wide XPath instance to avoid reinstantiation in 
    // the recursive methods
    private XPath xpath;

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        xpath = XPathFactory.newInstance().newXPath();

        // Apply the same principles as the python-ontogene
        // To look at the original python code, look at  text_import/file_import.py
        applyPreprocess(blackboard, NXMLToArticle(blackboard.getText()));

    }

    /**
     * Convert a NXML input to a plain text one, following the same conversion
     * principles used by python-ontogene.
     *
     * @param nxml the original NXML string.
     * @return the scrapped plain text output.
     */
    private String NXMLToArticle(String nxml) {

        try {
            String cleanedText = "";

            InputSource source = new InputSource(new StringReader(nxml));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            db.setEntityResolver((String publicId, String systemId) -> {
                Logger.getLogger(CraftPrerprocessor.class.getName())
                        .log(
                                Level.WARNING,
                                "Ignoring {0}, {1}", new Object[]{publicId, systemId});
                return new InputSource(new StringReader(""));
            });

            Document document = db.parse(source);

            String title = xpath.evaluate(
                    "/article/front/article-meta/title-group/article-title",
                    document);
            title = strip(title);

//    # Locate title, abstract, body nodes
//    title = text_node(tree, './/title')
//    abstract = tree.find('.//abstract')
//    body = tree.find('.//body')
//
//    # Unescape XML character references
//    # Separate title, abstract and body with blank lines (matching CRAFT format)
//    title_str = title.strip() + "\n\n"
//    abstract_str = "Abstract" + "\n\n" + unescape(
//        nxml_recursive_descent(abstract)).strip() + "\n\n"
//    body_str = unescape(nxml_recursive_descent(body))
//
//    # Try to get a missing PMID.
//    if pmid is None:
//        try:
//            pmid = tree.find('.//article').get('pid')
//        except AttributeError:
//            pass
//
//    article = Article(pmid)
//    article.add_section('title', title_str)
//    article.add_section('abstract', abstract_str)
//    article.add_section('body', body_str)
            return cleanedText;
        } catch (ParserConfigurationException | SAXException |
                IOException | XPathExpressionException ex) {
            Logger.getLogger(CraftPrerprocessor.class.getName()).log(Level.SEVERE, null, ex);
            throw new AnnotationException(this, "Failed to parse NXML.", ex);
        }

    }

    private String NXMLRecursiveDescent() {
        String retString = "";
        String newline = "\n\n";

//      if element.text:
//        string += element.text + nl
//      for child in element:
//        string += nxml_recursive_descent(child)
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
