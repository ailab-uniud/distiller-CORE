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

/**
 * Exception thrown when an annotation error occurs.
 *
 * @author Marco Basaldella
 */
public class AnnotationException extends RuntimeException {
    
    public AnnotationException(Annotator sender, String message) {
        super("Error while annotating\n\t" + 
                "Annotator " + sender.getClass().getName() + 
                " caused an exception with message:\n\t" + message);
    }
    
    public AnnotationException(Annotator sender,
            Throwable cause) {
        super("Error while annotating\n\t" + 
                "Annotator " + sender.getClass().getName() + 
                " caused an exception. ",cause);
    }
    
    public AnnotationException(Annotator sender, String message,
            Throwable cause) {
        super("Error while annotating\n\t" + 
                "Annotator " + sender.getClass().getName() + 
                " caused an exception with message:\n\t" + message,cause);
    }
    
    
    
}
