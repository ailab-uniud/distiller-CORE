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
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.annotation.*;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.Blackboard;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import it.uniud.ailab.dcore.annotation.annotations.TextAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Sentence;
import it.uniud.ailab.dcore.persistence.Token;
import org.springframework.beans.factory.annotation.Required;

/**
 * Adds the Wikiflag as defined in
 * {@link it.uniud.ailab.dcore.annotation.annotators.GenericWikipediaAnnotator}
 * to sequence of tokens which text coincides with the title of a Wikipedia page. 
 * 
 * @author Dario De Nart
 * @author Marco Basaldella
 */
public class TagMeTokenAnnotator implements Annotator, GenericWikipediaAnnotator {

    /**
     * The endpoint of TagMe's API.
     */
    private final String tagmeEndpoint = "http://tagme.di.unipi.it/tag";
    
    /**
     * The API key for TagMe. You can get one writing an email to
     * tagme [at] di [dot] unipi [dot] it
     */
    private String apiKey;
    
    /**
     * The JSON parser object.
     */
    private final JSONParser parser = new JSONParser();

    /**
     * Set the API key for querying TagMe. You can get one for tree writing an
     * email to tagme [at] di [dot] unipi [dot] it.
     * 
     * @param apiKey the API key to use
     */
    @Required
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    
    
    /**
     * The method that actually does the job: it tags a text string with
     * Wikipedia page titles when needed, and puts the result in a convenient
     * HashMap.
     * 
     * @param text the text to tag
     * @param lang the language of the text
     * @return an hashmap with the substrings of the input text and their matching
     * Wikipedia page
     */
    private HashMap<String, String> tagSentence(String text, String lang) {
        HashMap<String, String> output = new HashMap();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tagmeEndpoint);
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("key", apiKey));
        params.add(new BasicNameValuePair("lang", lang));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new AnnotationException(
                    this,"Encoding error while building TAGME request URL",e);
        }

        
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                // EntityUtils to get the response content
                String content = EntityUtils.toString(respEntity);
                Object obj = parser.parse(content);
                JSONObject queryblock = (JSONObject) obj;
                JSONArray annotationBlock = (JSONArray) queryblock.get("annotations");
                Iterator<JSONObject> iterator = annotationBlock.iterator();
                while (iterator.hasNext()) {
                    JSONObject tag = (iterator.next());
                    Float rho = Float.parseFloat((String) tag.get("rho"));
                    if (rho > 0.15) {
                        String spot = (String) tag.get("spot");
                        String lemma = (String) tag.get("title");
                        output.put(spot, lemma);
                    }

                }

            }
        } catch (ClientProtocolException e) {
            throw new AnnotationException(this,"Fail while querying TAGME",e);
        } catch (IOException e) {
            throw new AnnotationException(this,"Fail querying TAGME",e);
        } catch (ParseException e) {
            throw new AnnotationException(this,"Fail while parsing TAGME json",e);
        }
        return output;
    }

    /**
     * Annotates a single sentence with the Wikipedia flag. Used as base case 
     * for recursion.
     * 
     * @param sentence the sentence to annotate.
     */
    private void annotateSentence(Sentence sentence) {        
        
        String text = sentence.getText();
        // Retrieve the tagMe annotations using the internal TagMe wrapper
        HashMap<String, String> taggedSentence = tagSentence(text, sentence.getLanguage().getLanguage());
        
        
        
        // Put the annotations in the appropriate token
        
        for (String surface : taggedSentence.keySet()) {
            String matchedSurface = surface;
            List<Token> matchedTokens = new ArrayList<>();
            for (Token t : sentence.getTokens()) {
                String part = t.getText();
                if (!matchedSurface.startsWith(part))
                    continue;
                
                matchedTokens.add(t);
                matchedSurface = matchedSurface.substring(part.length());
                while (matchedSurface.startsWith(" "))
                    matchedSurface = matchedSurface.substring(1);
            }
            
            if (!matchedTokens.isEmpty()) {
                TextAnnotation ann = new TextAnnotation(WIKIFLAG, 
                        matchedTokens.toArray(new Token[matchedTokens.size()]),
                        taggedSentence.get(surface));
                
                matchedTokens.stream().forEach(t -> {
                    t.addAnnotation(ann);});
                
            }
        }
        /*
        for (Token t : sentence.getTokens()) {
            String part = t.getText();
            boolean match = false;
            List<Token> matchedTokens = new ArrayList<>();
            for (String surface : taggedSentence.keySet()) {
                if (surface.contains(part)) {
;
                }
            }
            TextAnnotation ann = new TextAnnotation(WIKIFLAG, matchedTokens, taggedSentence.get(surface));
                    t.addAnnotation(ann);
        }*/
    }

    /**
     * Annotates the document putting a flag over subsequent tokens which
     * match a Wikipedia page title. For example, the tokens "software" and 
     * "engineering", when put one next the other, match the page
     * "software engineering".
     * 
     * @param blackboard
     * @param component 
     */
    @Override
    public void annotate(Blackboard blackboard,DocumentComponent component) {
        
        if (!component.hasComponents()) {
            annotateSentence((Sentence)component);
        } else {
            for (DocumentComponent c : component.getComponents()) {
                annotate(blackboard,c);
            }
        }
    }

}
