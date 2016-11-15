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
package it.uniud.ailab.dcore.io;

import it.uniud.ailab.dcore.Blackboard;

/**
 * A printer that prints tokens in the CoNLL-2000 / CRF++ format. For more 
 * information, see <a href="http://www.cnts.ua.ac.be/conll2000/chunking/">
 * the CoNLL 2000</a> and the <a href="https://taku910.github.io/crfpp/#format">
 * CRF++</a> pages.
 *
 * @author Marco Basaldella
 */
public class CoNLL2000Printer implements FileWriterStage {

    @Override
    public String getFileSuffix() {
        return "conll";
    }

    @Override
    public void writeFile(String file, Blackboard b) {
        CsvPrinter printer = new CsvPrinter('\t',false,true);
        printer.writeTokens(file,b);
    }
    
}
