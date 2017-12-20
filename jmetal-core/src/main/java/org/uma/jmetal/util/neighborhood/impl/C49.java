package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class defining an C49 neighborhood of a solution belonging to a list of solutions which is
 * structured as a bi-dimensional mesh. The neighbors are those solutions that are in
 * 3-hop distance or less
 *
 * Shape:
 *            * * * * * * *
 *            * * * * * * *
 *            * * * * * * *
 *            * * * o * * *
 *            * * * * * * *
 *            * * * * * * *
 *            * * * * * * *
 *
 * @author Esteban López Camacho
 */
@SuppressWarnings("serial")
public class C49<S extends Solution<?>> extends TwoDimensionalMesh<S> {
  
  private static final int [][] neighborhood = {
      {3, -3}, {3, -2}, {3, -1}, {3, 0}, {3, 1}, {3, 2}, {3, 3},
      {2, -3}, {2, -2}, {2, -1}, {2, 0}, {2, 1}, {2, 2}, {2, 3},
      {1, -3}, {1, -2}, {1, -1}, {1, 0}, {1, 1}, {1, 2}, {1, 3}, 
      {0, -3}, {0, -2}, {0, -1}, {0, 1}, {0, 2}, {0, 3},
      {-1, -3}, {-1, -2}, {-1, -1}, {-1, 0}, {-1, 1}, {-1, 2}, {-1, 3},
      {-2, -3}, {-2, -2}, {-2, -1}, {-2, 0}, {-2, 1}, {-2, 2}, {-2, 3},
      {-3, -3}, {-3, -2}, {-3, -1}, {-3, 0}, {-3, 1}, {-3, 2}, {-3, 3}
  };

  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize
   */
  public C49(int rows, int columns) {
    super(rows, columns, neighborhood) ;
  }
}
