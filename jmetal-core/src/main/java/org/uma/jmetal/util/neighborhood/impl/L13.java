package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.neighborhood.util.TwoDimensionalMesh;

/**
 * Class representing neighborhoods for a solution into a list of solutions
 *
 * @author Esteban López Camacho
 */
@SuppressWarnings("serial")
public class L13<S extends Solution<?>> extends TwoDimensionalMesh<S> {
  
  private static final int [][] neighborhood = {
      {2, 0},
      {1, -1}, {1, 0}, {1, 1},
      {0, -2}, {0, -1}, {0, 1}, {0, 2},
      {-1, -1}, {-1, 0}, {-1, 1},
      {-2, 0}
  };

  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize
   */
  public L13(int rows, int columns) {
    super(rows, columns, neighborhood) ;
  }
}
