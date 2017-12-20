package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class defining an C25 neighborhood of a solution belonging to a list of solutions which is
 * structured as a bi-dimensional mesh. The neighbors are those solutions that are in
 * 2-hop distance or less
 *
 * Shape:
 *             * * * * *
 *             * * * * *
 *             * * o * *
 *             * * * * *
 *             * * * * *
 *
 * @author Esteban López Camacho
 */
@SuppressWarnings("serial")
public class C25<S extends Solution<?>> extends TwoDimensionalMesh<S> {
  
  private static final int [][] neighborhood = {
      {2, -2}, {2, -1}, {2, 0}, {2, 1}, {2, 2},
      {1, -2}, {1, -1}, {1, 0}, {1, 1}, {1, 2},
      {0, -2}, {0, -1}, {0, 1}, {0, 2},
      {-1, -2}, {-1, -1}, {-1, 0}, {-1, 1}, {-1, 2},
      {-2, -2}, {-2, -1}, {-2, 0}, {-2, 1}, {-2, 2}
  };

  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize
   */
  public C25(int rows, int columns) {
    super(rows, columns, neighborhood) ;
  }
}
