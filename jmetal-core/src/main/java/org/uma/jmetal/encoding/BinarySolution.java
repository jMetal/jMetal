//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.encoding;

import org.uma.jmetal.core.Solution;

import java.util.BitSet;

/**
 * @author Antonio J. Nebro
 * @version 0.1
 */
public interface BinarySolution extends Solution<BitSet> {
  public int getNumberOfBits(int index) ;
  public int getTotalNumberOfBits() ;
 }
