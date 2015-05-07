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
package org.uniud.dcore.annotation;

import org.uniud.dcore.engine.Annotator;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.uniud.dcore.persistence.Annotation;
import org.uniud.dcore.persistence.DocumentComponent;
import org.uniud.dcore.persistence.Sentence;
import org.uniud.dcore.persistence.Token;

/**
 *
 * @author Dado
 */
public class TagMeTokenAnnotator implements Annotator {

    private final String tagmeEndpoint = "http://tagme.di.unipi.it/tag";
    private final String apiKey = "Dario.de.NART.2014";
    
    public static final String TAGMEANNOTATION = "Tagme";

    // JSON parser
    private final JSONParser parser;

    public TagMeTokenAnnotator() {
        parser = new JSONParser();
    }

    //fat-ass provate mathod that does everything
    // returns a nice HashMap containing spot/lemma pairs
    private HashMap<String, String> tagDocument(String text, String lang) {
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
            // writing error to Log
            e.printStackTrace();
        }
        /*
         * Execute the HTTP Request
         */
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
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (ParseException ex) {
            System.out.println("Badass JSON parsing fail in TAGME Gate");
            Logger.getLogger(TagMeTokenAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    public void annotateSentence(Sentence sentence) {
        
        
        String text = sentence.getText();
        // Retrieve the tagMe annotations using the internal TagMe wrapper
        HashMap<String, String> taggedSentence = tagDocument(text, sentence.getLanguage().getLanguage());
        
        // Put the annotations in the appropriate token
        for (Token t : sentence.getTokens()) {
            String part = t.getText();
            for (String surface : taggedSentence.keySet()) {
                if (surface.contains(part)) {
                    Annotation ann = new Annotation(TAGMEANNOTATION, surface, taggedSentence.get(surface));
                    t.addAnnotation(ann);
                }
            }
        }
    }

    @Override
    public void annotate(DocumentComponent component) {
        
        if (!component.hasComponents()) {
            annotateSentence((Sentence)component);
        } else {
            for (DocumentComponent c : component.getComponents()) {
                annotate(c);
            }
        }
    }

}
