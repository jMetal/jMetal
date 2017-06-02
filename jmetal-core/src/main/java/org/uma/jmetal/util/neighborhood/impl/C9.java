package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class representing neighborhoods for a solution into a list of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class C9<S> extends TwoDimensionalMesh<S> {

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

