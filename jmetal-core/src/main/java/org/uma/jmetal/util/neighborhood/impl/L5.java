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

package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class defining a L5 neighborhood of a solution belonging to a list of solutions which is
 * structured as a bi-dimensional square mesh. The neighbors are those solutions that are in the positions
 * North, South, East and West
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class L5<S extends Solution<?>> extends TwoDimensionalMesh<S> {
  private static final int [] north      = {-1,  0};
  private static final int [] south      = { 1 , 0};
  private static final int [] east       = { 0 , 1};
  private static final int [] west       = { 0 ,-1};

  private static final int [][] neighborhood = {north, south, west, east};

  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize (it has to have an exact squared root)
   */
  public L5(int rows, int columns) {
    super(rows, columns, neighborhood) ;
  }
}

