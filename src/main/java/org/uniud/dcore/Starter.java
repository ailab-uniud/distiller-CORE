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
package org.uniud.dcore;

import java.text.DecimalFormat;
import org.joda.time.DateTime;
import org.uniud.dcore.engine.Distiller;

/**
 *
 * @author Marco Basaldella
 */
public class Starter {

    public static void main(String[] args) {
        // Extract information from the incipit of "The Idiot", by Fyodor Dostoyevsky.        
        Distiller d = Distiller.getDefault();
        
        DateTime start = DateTime.now();
        
        d.extract("Towards the end of November, during a thaw, at nine o'clock one morning, a train on the Warsaw and Petersburg railway was approaching the latter city at full speed. The morning was so damp and misty that it was only with great difficulty that the day succeeded in breaking; and it was impossible to distinguish anything more than a few yards away from the carriage windows. Some of the passengers by this particular train were returning from abroad; but the third-class carriages were the best filled, chiefly with insignificant persons of various occupations and degrees, picked up at the different stations nearer town. All of them seemed weary, and most of them had sleepy eyes and a shivering expression, while their complexions generally appeared to have taken on the colour of the fog outside. When day dawned, two passengers in one of the third-class carriages found themselves opposite each other. Both were young fellows, both were rather poorly dressed, both had remarkable faces, and both were evidently anxious to start a conversation. If they had but known why, at this particular moment, they were both remarkable persons, they would undoubtedly have wondered at the strange chance which had set them down opposite to one another in a third-class carriage of the Warsaw Railway Company. ");
        
        DateTime mid = DateTime.now();
        
        // subsequent extractions are faster because only the first one loads definitions
        d.extract("One of them was a young fellow of about twenty-seven, not tall, with black curling hair, and small, grey, fiery eyes. His nose was broad and flat, and he had high cheek bones; his thin lips were constantly compressed into an impudent, ironical—it might almost be called a malicious—smile; but his forehead was high and well formed, and atoned for a good deal of the ugliness of the lower part of his face. A special feature of this physiognomy was its death-like pallor, which gave to the whole man an indescribably emaciated appearance in spite of his hard look, and at the same time a sort of passionate and suffering expression which did not harmonize with his impudent, sarcastic smile and keen, self-satisfied bearing. He wore a large fur—or rather astrachan—overcoat, which had kept him warm all night, while his neighbour had been obliged to bear the full severity of a Russian November night entirely unprepared. His wide sleeveless mantle with a large cape to it—the sort of cloak one sees upon travellers during the winter months in Switzerland or North Italy—was by no means adapted to the long cold journey through Russia, from Eydkuhnen to St. Petersburg. ");
        
        DateTime end = DateTime.now();
        
        long firstExtraction = mid.getMillis() - start.getMillis();
        long secondExtraction = end.getMillis() - mid.getMillis();
        
        DecimalFormat df = new DecimalFormat("#.###");
        
        System.out.println("** Extraction performance **");
        System.out.println("First extraction\t " + df.format(firstExtraction/1000.0) + " s (incl. initialization)");
        System.out.println("Second extraction\t " + df.format(secondExtraction/1000.0) + " s");
        
    }
}