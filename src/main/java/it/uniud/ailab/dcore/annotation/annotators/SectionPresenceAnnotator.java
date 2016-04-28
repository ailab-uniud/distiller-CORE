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
package it.uniud.ailab.dcore.annotation.annotators;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.Annotator;
import it.uniud.ailab.dcore.annotation.DefaultAnnotations;
import it.uniud.ailab.dcore.annotation.annotations.FeatureAnnotation;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.Gram;

/**
 * Annotates gram with their presence in the title and in the abstract.
 *
 * @author Marco Basaldella
 */
public class SectionPresenceAnnotator implements Annotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        if (component.getIdentifier().equals(
                GenericStructureAnnotator.SECTION_TITLE)) {
            annotateRec(component, GenericStructureAnnotator.SECTION_TITLE);
        } else if (component.getIdentifier().equals(
                GenericStructureAnnotator.SECTION_ABSTRACT)) {
            annotateRec(component, GenericStructureAnnotator.SECTION_ABSTRACT);
        } else {

            // recursively look for abstract, title, etc.            
            if (component.hasComponents()) {
                for (DocumentComponent c : component.getComponents()) {
                    annotate(blackboard, c);
                }
            }
        }

    }

    private void annotateRec(DocumentComponent component, String sectionName) {
        
        String annotation = sectionName;
        
        if (annotation.equals(GenericStructureAnnotator.SECTION_TITLE)) {
            annotation = DefaultAnnotations.IN_TITLE;
        } else if (annotation.equals(GenericStructureAnnotator.SECTION_ABSTRACT)) {
            annotation = DefaultAnnotations.IN_ABSTRACT;
        }
        
        // traverse the tree
        if (component.hasComponents()) {
            for (DocumentComponent c : component.getComponents()) {
                annotateRec(c, sectionName);
            }
        } else {
            // we're in a sentence
            for (Gram g : component.getGrams()) {
                g.addAnnotation(
                 new FeatureAnnotation(annotation,1));
            }
        }
    }
}

