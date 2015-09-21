/*
 * 	Copyright (C) 2015 Artificial Intelligence
 * 	Laboratory @ University of Udine.
 * 
 * 	This file is part of the Distiller-CORE library.
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
package it.uniud.ailab.dcore.annotation;

import it.uniud.ailab.dcore.utils.Either;
import it.uniud.ailab.dcore.utils.Either.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract annotation. It forces child annotators to identify themselves
 * by calling the one-argument constructor, where they have to pass their 
 * identifier as a string. <br/>
 * 
 * To add values to an annotation one can create its own fields, but it is
 * <b>strongly</b>
 * advised to use the provided {@code addNumber()} and {@code addString()} 
 * methods. This way, these values can be automatically retrieved by other 
 * classes even if they don't know what is the <b>actual</b> annotation they're
 * dealing with.<br/>
 * 
 * To retrieve the stored values simply keep track of the order in which
 * they're added to the array. Since all the values should be added by the
 * constructor of the concrete annotation this should not really be a problem.
 * Hint: if you're adding a value in the values array outside of the constructor,
 * you're probably doing it wrong!<br/>
 * 
 * It's obviously possible to mix both approaches. For example, the 
 * {@link it.uniud.ailab.dcore.annotation.annotations.TextAnnotation} class
 * uses both the values array and a custom property. In this case, it's not 
 * possible to retrieve the custom property automatically, but all the 
 * "output-worthy" information is stored in the values array.<br/>
 * 
 * Identifiers should be set as {@code public static final} fields in each 
 * Annotator class, such as in 
 * {@link it.uniud.ailab.dcore.annotation.annotators.GenericEvaluatorAnnotator}.
 * 
 * @author Marco Basaldella
 */
public abstract class Annotation {
   
    /**
     * The identifier of the annotator that produced the annotation.
     */
    protected final String annotator;
    
    /**
     * The values of the annotation.
     */
    private final List<Either<String,Number>> values;
    
    protected Annotation(String annotator) {
        this.annotator = annotator;
        values = new ArrayList<>();
    }
    
    /**
     * Gets the identifier of the annotator that produced the annotation.
     * 
     * @return the identifier of the annotator.
     */
    public String getAnnotator() {
        return annotator;
    }
    
    /**
     * Adds a string to the values list.
     * @param s the annotation to add
     */
    public void addString(String s) {
        values.add(new Left<>(s));
    }
    
    /**
     * Adds a numeric to the values list.
     * @param n the annotation to add.
     */
    public void addNumber(Number n) {
        values.add(new Right<>(n));
    }
    
    /**
     * Checks if the value in position {@code i} is a String
     * 
     * @param i the position of the value to check
     * @return true if it's a String; false otherwise
     */
    public boolean isString(int i) {
        return values.get(i).isLeft();
    }
    
    /**
     * Checks if the value in position {@code i} is a Number
     * 
     * @param i the position of the value to check
     * @return true if it's a Number; false otherwise
     */
    public boolean isNumber(int i) {
        return values.get(i).isRight();
    }
    
    /**
     * Gets the string value in position {@code i} of the values array.
     * @param i the position of the annotation to retrieve
     * @return the requested annotation
     */
    public String getStringAt(int i) {
        if (isString(i))
            return values.get(i).getLeft();
        else 
            throw new UnsupportedOperationException(
                    "Extracting String from a Number field.");
    }
    
    /**
     * Gets the numeric value in position {@code i} of the values array.
     * @param i the position of the annotation to retrieve
     * @return the requested annotation
     */
    public Number getNumberAt(int i) {
        if (isNumber(i))
            return values.get(i).getRight();
        else 
            throw new UnsupportedOperationException(
                    "Extracting Number from a String field.");
    }   
    
    /**
     * Gets the value in position {@code i} of the values array.
     * @param i the position of the annotation to retrieve
     * @return the requested annotation
     */
    public Object getValueAt(int i) {
        if (isString(i))
            return values.get(i).getLeft();
        else 
            return values.get(i).getRight();
    }    
    
    @Override
    public String toString() {
        String output = annotator + ": ";
        
        
        return output.substring(0,output.length() - 1);
    }   
}


