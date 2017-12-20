package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class defining an L9 neighborhood of a solution belonging to a list of solutions which is
 * structured as a bi-dimensional mesh. The neighbors are those solutions that are
 * in 1-hop distance
 *
 * Shape:
 *               * * *
 *               * o *
 *               * * *
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class C9<S> extends TwoDimensionalMesh<S> {

  // There are 8 possible movements: north, south, east, west, northeast, northwest,southeast, southwest
  // Each movement is represented by an array of two positions: first component represents the
  // movement in the row, the second one the movement in the column
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
   * Defines a neighborhood for a solution set of rows x columns solutions
   * @param rows the number of rows
   * @param columns the number of columns
   */
  public C9(int rows, int columns) {
    super (rows, columns, neighborhood) ;
  }
}

