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
package it.uniud.ailab.dcore.launchers;

import it.uniud.ailab.dcore.*;
import it.uniud.ailab.dcore.utils.BlackboardUtils;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

/**
 * A simple class that demonstrates some extractions with the distiller.
 *
 * @author Marco Basaldella
 */
public class SampleInference {

    // The incipit of "The Idiot", by Fyodor Dostoyevsky.   
    private static String dosto = "Towards the end of November, during a thaw, at nine o'clock one morning, a train on the Warsaw and Petersburg railway was approaching the latter city at full speed. The morning was so damp and misty that it was only with great difficulty that the day succeeded in breaking; and it was impossible to distinguish anything more than a few yards away from the carriage windows. Some of the passengers by this particular train were returning from abroad; but the third-class carriages were the best filled, chiefly with insignificant persons of various occupations and degrees, picked up at the different stations nearer town. All of them seemed weary, and most of them had sleepy eyes and a shivering expression, while their complexions generally appeared to have taken on the colour of the fog outside. When day dawned, two passengers in one of the third-class carriages found themselves opposite each other. Both were young fellows, both were rather poorly dressed, both had remarkable faces, and both were evidently anxious to start a conversation. If they had but known why, at this particular moment, they were both remarkable persons, they would undoubtedly have wondered at the strange chance which had set them down opposite to one another in a third-class carriage of the Warsaw Railway Company. ";
    
    /**
     * The first three paragraphs of the javadoc page of SortedMap.
     *
     * @see http://docs.oracle.com/javase/7/docs/api/java/util/SortedMap.html
     */
    private static String sortedMap = "A Map that further provides a total ordering on its keys. The map is ordered according to the natural ordering of its keys, or by a Comparator typically provided at sorted map creation time. This order is reflected when iterating over the sorted map's collection views (returned by the entrySet, keySet and values methods). Several additional operations are provided to take advantage of the ordering. (This interface is the map analogue of SortedSet.) "
            + "All keys inserted into a sorted map must implement the Comparable interface (or be accepted by the specified comparator). Furthermore, all such keys must be mutually comparable: k1.compareTo(k2) (or comparator.compare(k1, k2)) must not throw a ClassCastException for any keys k1 and k2 in the sorted map. Attempts to violate this restriction will cause the offending method or constructor invocation to throw a ClassCastException. "
            + "Note that the ordering maintained by a sorted map (whether or not an explicit comparator is provided) must be consistent with equals if the sorted map is to correctly implement the Map interface. (See the Comparable interface or Comparator interface for a precise definition of consistent with equals.) This is so because the Map interface is defined in terms of the equals operation, but a sorted map performs all key comparisons using its compareTo (or compare) method, so two keys that are deemed equal by this method are, from the standpoint of the sorted map, equal. The behavior of a tree map is well-defined even if its ordering is inconsistent with equals; it just fails to obey the general contract of the Map interface. ";

    /**
     * A news from the Top Gear website.
     */
    private static String topGear = "Got three-hundred-and-something grand burning a hole in your jeans pocket, and a desire for a very yellow slice of the early Nineties on your driveway? "
            + "Then good news, because this 1994 Ferrari F512M is going under the hammer at Silverstone Auction's May Sale, where it's expected to fetch between £280,000 and £330,000. "
            + "A lot of cash, but it's something of a museum piece. The 512M was the final version of the Testarossa, Ferrari's mid-engined, 12-cylinder bruiser built from 1984 to 1996. "
            + "Introduced at the Paris Motor Show in 1994, this F512M employed a 5.0-litre flat 12 engine producing 446bhp - a pretty meaty output by the standards of the day. It would, according to the official figures, get from 0-62mph in 4.7 seconds, with a top speed of 196mph. "
            + "Just 501 of these M-spec 512s were built, before production ceased and Ferrari inexplicably forgot about building mid-engined 12-cylinder supercars. The 512M cost around £140,000 new in 1994, which, accounting for inflation, works out around £230,000 in modern money. "
            + "We're told this particular example was sold to a German customer in 1994. From there, it was snapped up by an owner in Japan, with whom it has resided since. The 512M has covered just 11,000 miles in its 21-year life. "
            + "OK, it may not be such a classical, timeless Ferrari as that 250 GT SWB California we showed you last week, but the 512M is every bit as much a period piece - not to mention a whole lot cheaper. And yellower. "
            + "A snip at £330,000, or would you save your (completely non-hypothetical cash) for a modern 458 Special, with enough change left over for, ooh, a Porsche Cayman GT4?";

    private static String annaSavoia = "Anna Paleologina, nata Giovanna di Savoia (1306  Costantinopoli, 1359), fu imperatrice bizantina. "
            + "Era figlia di Amedeo V di Savoia e della seconda moglie Maria di Brabante, essendo così per parte di madre nipote di Giovanni I di Brabante, duca di Brabante, e di Margherita di Fiandra. "
            + "Divenne la seconda moglie di Andronico III Paleologo, che era vedovo di Adelaide di Braunschweig da cui aveva avuto un figlio morto infante. Stando a Giovanni VI Cantacuzeno, il matrimonio si tenne nell'ottobre del 1326. Anna, di fede cattolica, dovette per l'occasione convertirsi alla fede ortodossa cambiando il suo nome da Giovanna ad Anna. "
            + "Diede al marito un figlio: Giovanni (18 giugno 1332-16 febbraio 1391), imperatore bizantino dal 1341 al 1376 e dal 1379 al 1391. "
            + "Divenne imperatrice nel 1328. "
            + "Alla morte di Andronico III, avvenuta il 15 giugno 1341, Anna venne nominata reggente per il figlio Giovanni V Paleologo, ruolo che ricoprì dal 1341 al 1347[1]. Dovette tuttavia dividere il potere con il ministro del marito Giovanni Cantacuzeno col quale si scontrò in quanto ella voleva portare avanti una politica filopapista. Cantacuzeno riuscì a divenire imperatore col nome di Giovanni VI e di fatto divise il potere con Giovanni V finché si ritirò in convento lasciandolo unico imperatore. ";
    
    
    private static String softEngIT = "L'ingegneria del software identifica una formalizzazione del processo di realizzazione e di manutenzione di un sistema informativo. Per tale associazione con una idea quasi biologica di vita si parla spesso di ciclo di vita di un software, concetto che ha assunto con il passare dei decenni un'importanza sempre più elevata, spostando l'idea di software come manufatto verso un'idea di prodotto industriale. " +
            "La necessità di creare una scienza che si occupi della realizzazione dei sistemi informativi nasce dalla necessità di sviluppare prodotti sempre più complessi ed evoluti che rispondano a esigenze di correttezza del processo realizzativo e di facile manutenzione. " +
            "Il software come prodotto industriale diventa anche oggetto di un attento esame per estendere le capacità di realizzazione dello stesso. Nasce in pratica un concetto simile alle ottimizzazioni da catena di montaggio per le industrie del secolo scorso. Si cercano quindi di identificare nella realizzazione del software, quegli obiettivi a cui tengono le industrie del software, come qualità del software realizzato e soprattutto di rilasciare un prodotto perfettamente documentato e facilmente \"manutenibile\". " +
            "La nuova scienza, l'ingegneria del software, si preoccupa effettivamente di concretizzare queste esigenze, cercando di definire modelli che permettano a team di tecnici di realizzare in cooperazione prodotti sempre più evoluti e di qualità. " +
            "L'ingegneria del software definisce quindi un insieme di processi, ovvero sequenze di fasi che individuano tappe specifiche nella realizzazione di un sistema software tutte documentate e ispezionabili, che offrano in sostanza perfetta visibilità alla diversa tipologia degli utenti del sistema, per il controllo dei singoli prodotti e/o per l'eventuale manutenzione.";

    private static String softEngPT = "Engenharia de Software é uma área da computação voltada à especificação, desenvolvimento e manutenção de sistemas de software, com aplicação de tecnologias e práticas de gerência de projetos e outras disciplinas, visando organização, produtividade e qualidade. "
            + "Atualmente, essas tecnologias e práticas englobam linguagens de programação, banco de dados, ferramentas, plataformas, bibliotecas, padrões, processos e a questão da Qualidade de software."
            + "Os fundamentos científicos para a engenharia de software envolvem o uso de modelos abstratos e precisos que permitem ao engenheiro especificar, projetar, implementar e manter sistemas de software, avaliando e garantindo suas qualidades. Além disso, a engenharia de software deve oferecer mecanismos para se planejar e gerenciar o processo de desenvolvimento de um sistema computacional.";

    public static void main(String[] args) {
        
        String[] texts = new String[] {
            dosto,
            topGear,
            sortedMap,
            softEngIT,
//            softEngPT
        };
             
        Distiller d = DistillerFactory.getDefault();
        d.setVerbose(true);

        Instant[] times = new Instant[texts.length + 1];
        
        times[0] = Instant.now();
        
        for (int i = 0; i < texts.length; i++) {
            DistilledOutput output = d.distill(texts[i]);
            Blackboard b = d.getBlackboard();
            BlackboardUtils.printScores(b,true);
            BlackboardUtils.printInference(b);
            times[i+1] = Instant.now();        
        }
        
        System.out.println("** Extraction performance **");
        DecimalFormat df = new DecimalFormat("#.###");
        
        for (int i = 0; i < texts.length; i++) {
            System.out.printf(
                    "Iteration %d:\t %s seconds\n",
                    i,
                    df.format(
                            (Duration.between(times[i],times[i+1]).toMillis())
                                    / 1000.0));
        }        
    }
}
