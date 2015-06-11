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
package it.uniud.ailab.dcore.annotation.component;


import static it.uniud.ailab.dcore.annotation.generic.WikipediaAnnotator.WIKIFLAG;
import static it.uniud.ailab.dcore.engine.Evaluator.SCORE;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.InferenceAnnotation;
import it.uniud.ailab.dcore.annotation.TextAnnotation;
import it.uniud.ailab.dcore.annotation.generic.WikipediaAnnotator;
import it.uniud.ailab.dcore.annotation.token.TagMeTokenAnnotator;
import it.uniud.ailab.dcore.engine.Annotator;
import it.uniud.ailab.dcore.engine.Blackboard;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// Seeing if something makes a match in Wikipedia it's easy
// The API has a nice lookup utility called OpenSearch, that we can use like

// http://en.wikipedia.org/w/api.php?action=opensearch&limit=10&namespace=0&format=jsonfm&search=<SEARCH QUERY>

// OpenSearch finds article titles that contain the given search query

// Getting Hypernyms in Wikipedia is quite easy, since almost any article is 
// pidgeonholed in one (or more) categories
// the API gives us the chanche of accessing these categories, given a page title
// the API request is something like this:

// http://en.wikipedia.org/w/api.php?action=parse&prop=categories&format=json&page=<PAGE TITLE>

// where PAGE TITLE is the article's title with spaces replaced with underscores
// Sadly, Wikipedia includes in the category list a lot of business information
// such as "Articles_with_dead_external_links_from_August_2012" which are total
// useless for us. 
    
// Luckly there is an option to remove this garbage, it's called clshow=!hidden 
// the query address is:
    
// http://en.wikipedia.org/w/api.php?action=query&prop=categories&clshow=!hidden&format=json&titles=<PAGE TITLE>
    
// Once given the correct title of a page, the Wikipedia API allows us to 
// perform the combined query that grabs both categories and outgoing links:
    
// http://en.wikipedia.org/w/api.php?action=query&prop=categories|extracts&clshow=!hidden&format=json&titles=<PAGE TITLE>

/**
 * This class annotates a DocumentComponent with information inferenced from
 * Wikipedia. This information is composed by related concepts and hypernyms of
 * the keyphrases found in the document.
 *
 * @author Marco Basaldella
 * @author Dario De Nart
 */
public class WikipediaInferenceAnnotator implements Annotator {

    /**
     * Annotation signature of related Wikipedia pages.
     */
    public static final String RELATED = "Wiki$related";

    /**
     * Annotations signature of hypernyms Wikipedia pages.
     */
    public static final String HYPERNYMS = "Wiki$hyper";

    /**
     * The user agent that will be used for HTTP requests (since Wikipedia
     * requests it).
     */
    private String userAgent = "Distiller (Artificial Intelligence Laboratory; carlo.tasso@uniud.it|dario.denart@uniud.it|basaldella.marco@spes.uniud.it) University Of Udine";

    /**
     * The query that will be performed using the Wikipedia OpenSearch APIs.
     */
    private final String wikipediaQuery = "http://en.wikipedia.org/w/api.php?action=query&prop=categories|extracts|links&clshow=!hidden&format=json&pllimit=500&plnamespace=0&titles=";
    // Unwanted otugoing links (trivial/nonsense/plain useless)
    private ArrayList<String> linkBlacklist = new ArrayList<>();
    // horrible hardcoded solution
    private static String[] blackTerms = {"null", "International Standard Book Number",
        "Digital object identifier",
        "PubMed Identifier",
        "International Standard Serial Number",
        "Wikisource",
        "Disambigua"};

    /**
     * Maps the categories associated with a page.
     */
    private Map<String, Double> hypernyms
            = new HashMap<>();

    /**
     * Maps the related links (the "See Also" section) of a Wikipedia page.
     */
    private Map<String, Double> related
            = new HashMap<>();
    
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {

        // Retrieve the grams with a "wikiflag", i.e. the one which
        // text is the same as a Wikipedia page title 
        // for example, "Software Engineering". 
        List<Gram> wikiGrams = new LinkedList<>();
        blackboard.getGrams().stream().filter((g) -> 
                (g.hasFeature(WikipediaAnnotator.WIKIFLAG))).forEach((g) -> {
            wikiGrams.add(g);
        });

        // Build the related and hypernyms lists, by getting the related links
        // and categories respectively of every Wikipedia page found in the 
        // above loop.
        findHyperymsAndRelated(wikiGrams);
        
        hypernyms.entrySet().stream().forEach((hypernym) -> {
            blackboard.addAnnotation(
                    new InferenceAnnotation(
                            HYPERNYMS,hypernym.getKey(),hypernym.getValue()));
        });
        
        related.entrySet().stream().forEach((related) -> {
            blackboard.addAnnotation(
                    new InferenceAnnotation(
                            RELATED,related.getKey(),related.getValue()));
        });
    }

    /**
     * Fill the hypernims and related link maps by quertying Wikipedia. The hypernyms
     * map contains the categories found for every page, while the related map
     * contains the related links.
     * 
     * @param grams the grams to analyze.
     */
    private void findHyperymsAndRelated(List<Gram> grams) {
        
        HttpURLConnection con = null;
        BufferedReader reader = null;

        // We may pipe several article titles in one query, but for some awkward reason,
        // the API won't give us the full category list of the requested terms, nor the full definition
        for (Gram currentGram : grams) {

            // this will contain the categories (i.e. our hypernyms)s
            ArrayList<String> wikiCategories = new ArrayList<>();

            // this will contain the related links
            ArrayList<String> wikiLinks = new ArrayList<>();

            String page = null;

            // get the correct annotation that generated the wikiflag
            for (TextAnnotation a : currentGram.getTokens().get(0).
                    getAnnotations(TagMeTokenAnnotator.WIKIFLAG)) {
                // the annotations have the same length, so we may have a legit
                // wikipedia surface as the gram
                if (a.getTokens().length == currentGram.getTokens().size()) {
                    
                    boolean isTagged = true;
                    
                    for (int i = 0; i < a.getTokens().length && isTagged; i++) {
                        isTagged = a.getTokens()[i].equals(
                                    currentGram.getTokens().get(i));
                    }
                    
                    if (isTagged)
                        page = a.getAnnotation();
                    
                }                    
                    
            }
                
            if (page == null)
                throw new AnnotationException(this,
                        "I couldn't find the correct annotation.");
                        
            page = page.replaceAll(" ", "_");

            // do the query and save the retrieved json in an object.
            String queryAddress = wikipediaQuery + page;

            try {

                con = (HttpURLConnection) (new URL(queryAddress)).openConnection();
                con.setRequestProperty("User-Agent", userAgent);
                con.setRequestMethod("GET");
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Object json = (new JSONParser()).parse(reader);
                // closing connection
                con.disconnect();
                // allright, then we've got a nice JSON file...
                // it's something like this:
                // "query": {
                //        "pages": {
                //            "<PAGE ID NUMBER>": {
                //                "pageid": "<PAGE ID NUMBER>",
                //                "ns": 0,
                //                "title": "<PAGE TITLE>",
                //                "categories": [
                //                    {
                //                        "ns": 14,
                //                        "title": "Category:<CATEGORY 1>"
                //                    },
                //                    {
                //                        "ns": 14,
                //                        "title": "Category:<CATEGORY 2>"
                //                    },
                //                    {
                //                        "ns": 14,
                //                        "title": "Category:<CATEGORY 3>"
                //                    }
                //                ],
                //                "extract":"<TEXT>",
                //                "links": [
                //                    {
                //                        "ns": 0,
                //                        "title": "<LINK 1>"
                //                    },
                //                    {
                //                        "ns": 0,
                //                        "title": "<LINK 2>"
                //                    },
                //                    {
                //                        "ns": 0,
                //                        "title": "<LINK 3>"
                //                    }
                //                 ]
                //            }
                //        }
                //    }
                //}
                // note that NOT ALL the wikis have the "extract" property in the API
                // therefore we may not assume that it will always be there
                JSONObject queryblock = (JSONObject) json;
                JSONObject pagesBlock = (JSONObject) queryblock.get("query");
                JSONObject idBlock = (JSONObject) pagesBlock.get("pages");

                // if we pipe'd more than one title, we'll have more than one pageId entry
                for (Iterator it = idBlock.keySet().iterator(); it.hasNext();) {

                    String pageId = (String) it.next();
                    JSONObject block = (JSONObject) idBlock.get(pageId);
                    // finally... The Categories!
                    JSONArray categories = (JSONArray) block.get("categories");
                    if (categories != null) {
                        Iterator<JSONObject> iterator = categories.iterator();
                        while (iterator.hasNext()) {
                            JSONObject category = (iterator.next());
                            String catName = (String) category.get("title");
                            catName = catName.replaceFirst("Category:", "");
                            catName = catName.replaceFirst("Categoria:", "");
                            if (!catName.toLowerCase().contains("stub") && !catName.contains("Featured Articles") && !catName.toLowerCase().contains("disambiguation")) {
                                //System.out.println(catName);
                                if (!wikiCategories.contains(catName)) {
                                    wikiCategories.add(catName);
                                }
                            }
                        }
                    }
                    
                    // We can find related entities in the text
                    // many articles have a "See Also" section that begins with
                    //          <h2>See also</h2>\n<ul>
                    // and ends with:
                    //          </ul>

                    // To retrieve these links, we don't need to scrap HTML.
                    // We can just read the list of links included in the JSON
                    // the drawback of this approach is that some pages have huge
                    // amounts of links and many of them are uninteresting
                    
                    // For example, almost any page has a reference to the
                    // definition of ISBN (contained in the references)
                    // or of some other kind of wide-used identifier such as:
                    // Pub-Med index,
                    // Digital-Object-Identifier,
                    // International Standard Book Number,
                    // Wikisource, and so on.
                    
                    JSONArray links = (JSONArray) block.get("links");
                    if (links != null) {
                        Iterator<JSONObject> iterator = links.iterator();
                        while (iterator.hasNext()) {
                            JSONObject link = (iterator.next());
                            String linkname = (String) link.get("title");

                            if (!wikiLinks.contains(linkname) && !linkBlacklist.contains(linkname)) {
                                wikiLinks.add(linkname);
                            }

                        }
                    }
                }

            } catch (ParseException ex) {
                throw new AnnotationException(this,
                        "Error while parsing JSON by Wikipedia for page: " + page,ex);
            } catch (MalformedURLException ex) {
                throw new AnnotationException(this,
                        "Malformed Wikipedia URL: " + queryAddress,ex);
            } catch (IOException ex) {
                throw new AnnotationException(this,
                        "Error while reading Wikipedia",ex);
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException ex) {
                    throw new AnnotationException(this,
                            "Error while reading Wikipedia",ex);
                }
            }

            // Update the results.
            
            // How does it work? The strenght of an hypernym or related concept
            // is the sum of all the scores of the KPs which generate it.
            
            // So, for example, if the KPs "Software" and "Engineering" have
            // both score 0.5, and both have the related link "Software Engineering", 
            // the strength of the "Software Engineering" related concept is 
            // going to be 1.
            
            for (String cat : wikiCategories) {
                                
                if (hypernyms.containsKey(cat)) {
                    hypernyms.replace(cat, 
                            hypernyms.get(cat) + currentGram.getFeature(SCORE));
                } else
                    hypernyms.put(cat, currentGram.getFeature(SCORE));

            }
            
            for (String rel : wikiLinks) {
                if (related.containsKey(rel)) {
                    related.replace(rel, 
                            related.get(rel) + currentGram.getFeature(SCORE));
                } else
                    related.put(rel, currentGram.getFeature(SCORE));
            }

        } // for (Gram g : grams)
    } // void findHypernymsAndRelated

} // class
