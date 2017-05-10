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
 * Class representing neighborhoods for a {@link Solution} into a list of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class C9<S extends Solution<?>> extends TwoDimensionalMesh<S> {

  // There are 8 possible movements: north, south, east, west, northeast, northwest,southeast, southwest
  // Each movement is represented by an array of two positions: first component represents the
  // movement in the file, the second one the movement in the column
  private static final int [] north      = {-1,  0};
  private static final int [] south      = { 1 , 0};
  private static final int [] east       = { 0 , 1};
  private static final int [] west       = { 0 ,-1};
  private static final int [] north_east = {-1,  1};
  private static final int [] north_west = {-1, -1};
  private static final int [] south_east = { 1 , 1};
  private static final int [] south_west = { 1 ,-1};

  private static final int [][] neighborhood = {north, south, west, east, north_east,
      north_west, south_east, south_west};

  /**
   * Constructor
   *
   * Defines a neighborhood of shape rows x columns (rows x columns must equal solutionSetSize)
   * @param rows the number of rows
   * @param columns the number of columns
   */
  public C9(int rows, int columns) {
    super (rows, columns, neighborhood) ;
  }
}

