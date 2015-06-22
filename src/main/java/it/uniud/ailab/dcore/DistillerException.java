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
package it.uniud.ailab.dcore;

/**
 * An exception during the distillation process. Used to force the uses of
 * messages to explain the error when the distiller throws an exception.
 *
 * @author Marco Basaldella
 */
public class DistillerException extends RuntimeException {
    
    public DistillerException(String message) {
        super(message);
    }
    
    public DistillerException(String message, Throwable cause) {
        super(message,cause);
    }
}
