/*
 * Copyright (C) 2017 Artificial Intelligence
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
package it.uniud.ailab.dcore.utils;

/**
 *
 * @author Muhammad
 */
public class KP {
    public static final transient String KPTYPE_NP = "NP", KPTYPE_SENT="S", KPTYPE_FRAG="FRAG", KPTYPE_NNP="NNP", KPTYPE_OTHER="OTHER";
    public static final transient int PURE_LEMMA=4, LEMMA=1, ORIGINAL=0, STEM=2, POS=3, SCORE=5, WITHOUTDT=6, OCCURRENCE=7; 
    public String type = KPTYPE_NP;
}
