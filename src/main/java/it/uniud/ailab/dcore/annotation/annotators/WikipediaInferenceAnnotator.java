/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 *
 * 	Licensed under the Apache License, Version 2.0 (the "License");
 * 	you may not use this file except in compliance with the License.
 * 	You may obtain a copy of the License at
 *
 * 	     http://www.apache.org/licenses/LICENSE-2.0
 *
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package it.uniud.ailab.dcore.annotation.annotators;


import static it.uniud.ailab.dcore.annotation.annotators.GenericWikipediaAnnotator.WIKIFLAG;
import static it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator.SCORE;
import it.uniud.ailab.dcore.annotation.AnnotationException;
import it.uniud.ailab.dcore.annotation.annotations.InferenceAnnotation;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.annotations.UriAnnotation;
import static it.uniud.ailab.dcore.annotation.annotators.GenericWikipediaAnnotator.WIKIURI;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;
import it.uniud.ailab.dcore.utils.WikipediaUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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
    private String userAgent;

    /**
     * The query that will be performed using the Wikipedia OpenSearch APIs.
     * Protocol, languages and the page queries need to be appended before and
     * after this string.
     */
    private final String wikipediaQuery = "wikipedia.org/w/api.php?action=query&prop=categories|extracts|links&clshow=!hidden&format=json&pllimit=500&plnamespace=0&titles=";

    // Blacklist of unwanted terms
    private static final List<String> blackTerms = Arrays.asList(new String[]{"null", "International Standard Book Number",
        "Digital object identifier",
        "PubMed Identifier",
        "International Standard Serial Number",
        "Wikisource",
        "Disambigua",
        "disambiguation",
        "stub",
        "Featured Articles"
    });

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
    
    /**
     * The language of the currently analyzed component.
     */
    Locale componentLocale;

    /**
     * Set the user agent used for requests to Wikipedia.
     * 
     * @param userAgent the user agent string
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    
    
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
        componentLocale = component.getLanguage();               
                
        // Retrieve the grams with a "wikiflag", i.e. the one which
        // text is the same as a Wikipedia page title 
        // for example, "Software Engineering". 
        List<Gram> wikiGrams = new LinkedList<>();
        blackboard.getGrams().stream().filter((g) -> 
                (g.hasFeature(WIKIFLAG))).forEach((g) -> {
            wikiGrams.add(g);
        });

        // Build the related and hypernyms lists, by getting the related links
        // and categories respectively of every Wikipedia page found in the 
        // above loop.
        findHyperymsAndRelated(wikiGrams);
        
        hypernyms.entrySet().stream().forEach((hypernym) -> {
            blackboard.addAnnotation(
                    new InferenceAnnotation(
                            HYPERNYMS,hypernym.getKey(),hypernym.getValue(),
                            WikipediaUtils.generateWikiUri(hypernym.getKey(), 
                                    componentLocale)));
        });
        
        related.entrySet().stream().forEach((related) -> {
            blackboard.addAnnotation(
                    new InferenceAnnotation(
                            RELATED,related.getKey(),related.getValue(),
                            WikipediaUtils.generateWikiUri(related.getKey(),
                                    componentLocale)));
        });
    }

    /**
     * Fill the hypernyms and related link maps by querying Wikipedia. The hypernyms
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

            String page = ((UriAnnotation) currentGram.getAnnotation(WIKIURI))
                    .getUriTitle();

            /*
            // get the correct annotation that generated the wikiflag
            TextAnnotation a = (TextAnnotation) currentGram.getTokens().get(0).
                    getAnnotation(WIKIFLAG);

            // the annotations have the same length, so we may have a legit
            // wikipedia surface as the gram
            
            if (a.getTokens().length == currentGram.getTokens().size()) {

                boolean isTagged = true;

                for (int i = 0; i < a.getTokens().length && isTagged; i++) {
                    isTagged = a.getTokens()[i].equals(
                            currentGram.getTokens().get(i));
                }

                if (isTagged) {
                    page = a.getAnnotation();
                }
            }*/

            if (page == null)
                throw new AnnotationException(this,
                        "I couldn't find the correct annotation.");
                        
            page = page.replaceAll(" ", "_");

            // do the query and save the retrieved json in an object.
            String queryAddress = String.format("https://%s.%s%s",
                     componentLocale.getLanguage(), wikipediaQuery, page);

            try {

                con = (HttpURLConnection) (new URL(queryAddress)).openConnection();
                con.setRequestProperty("User-Agent", userAgent);
                con.setRequestMethod("GET");
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Object json = (new JSONParser()).parse(reader);
                // closing connection
                con.disconnect();
                // The retrieved JSON is something like:
                //
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
                            if (!catName.toLowerCase().contains("stub") &&
                                    !catName.contains("Featured Articles") && 
                                    !catName.toLowerCase().contains("disambiguation")) {
                                //System.out.println(catName);
                                if (!wikiCategories.contains(catName) && !blackTerms.contains(catName)) {
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

                            if (!wikiLinks.contains(linkname) && !blackTerms.contains(linkname)) {
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
                } else {
                    hypernyms.put(cat, currentGram.getFeature(SCORE));
                }

             }

            for (String rel : wikiLinks) {
                if (related.containsKey(rel)) {
                    related.replace(rel, 
                            related.get(rel) + currentGram.getFeature(SCORE));
                } else
                    related.put(rel, currentGram.getFeature(SCORE));
            }

        } // for (Gram currentGram : grams)
    } // void findHypernymsAndRelated

} // class
