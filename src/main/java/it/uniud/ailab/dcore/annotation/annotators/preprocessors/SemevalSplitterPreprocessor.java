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
package it.uniud.ailab.dcore.annotation.annotators.preprocessors;

import it.uniud.ailab.dcore.Blackboard;
import it.uniud.ailab.dcore.annotation.annotators.GenericStructureAnnotator;
import it.uniud.ailab.dcore.persistence.DocumentComponent;
import it.uniud.ailab.dcore.persistence.DocumentComposite;

/**
 * Splits a SEMEVAL document on the blackboard using structural information.
 *
 * @author Marco Basaldella
 */
public class SemevalSplitterPreprocessor implements
        GenericStructureAnnotator {

    @Override
    public void annotate(Blackboard blackboard, DocumentComponent component) {

        String[] lines = blackboard.getText().split("\n");

        int abstractLine = -1;
        int contentLine = -1;

        int i = 0;
        for (; i < lines.length && abstractLine < 0; i++) {
            String line = lines[i];
            if (line.matches("^ABSTRACT$|^Abstract$")) {
                abstractLine = i;
            }
        }

        for (; i < lines.length
                && contentLine < 0; i++) {
            String line = lines[i];
            if (line.matches("^Categories and Subject(s)? Descriptors.*")) {
                contentLine = i;
            }
        }

        String[] titleLine = new String[abstractLine];
        String[] abstractLines = new String[contentLine - abstractLine];
        String[] contentLines = new String[lines.length - contentLine];

        for (i = 0; i < abstractLine; i++) {
            titleLine[i] = lines[i];
        }

        for (i = abstractLine; i < contentLine; i++) {
            abstractLines[i - abstractLine] = lines[i];
        }

        for (i = contentLine; i < lines.length; i++) {
            contentLines[i - contentLine] = lines[i];
        }

        String titleText = String.join("\n", titleLine);
        String abstractText = String.join("\n", abstractLines);
        String contentText = String.join("\n", contentLines);

        DocumentComposite titleComponent = new DocumentComposite(
                titleText,
                SECTION_TITLE
        );

        DocumentComposite abstractComponent = new DocumentComposite(
                abstractText,
                SECTION_ABSTRACT
        );

        DocumentComposite bodyComponent = new DocumentComposite(
                contentText,
                SECTION_PREFIX + "body"
        );

        ((DocumentComposite) blackboard.getStructure()).
                addComponent(titleComponent);

        ((DocumentComposite) blackboard.getStructure()).
                addComponent(abstractComponent);

        ((DocumentComposite) blackboard.getStructure()).
                addComponent(bodyComponent);

    }

}
