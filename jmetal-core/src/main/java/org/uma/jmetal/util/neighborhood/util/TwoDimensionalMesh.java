package org.uma.jmetal.util.neighborhood.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.neighborhood.Neighborhood;

/**
 * Class defining a bi-dimensional mesh.
 */
@SuppressWarnings("serial")
public class TwoDimensionalMesh<S> implements Neighborhood<S> {
  private final int rows ;
  private final int columns ;
  private final int[][] neighborhood ;
  private int [][] mesh;

  /**
   * Constructor.
   * Defines a neighborhood for list of solutions
   */
  public TwoDimensionalMesh(int rows, int columns, int[][]neighborhood) {
    this.rows = rows ;
    this.columns = columns ;

    this.neighborhood = neighborhood ;

    createMesh();
  }

  private void createMesh() {
    // idea: if rows = 5, and columns=3, we need to fill the mesh
    // as follows
    // --------
    //|00-01-02|
    //|03-04-05|
    //|06-07-08|
    //|09-10-11|
    //|12-13-14|
    // --------

    mesh = new int[rows][columns];
    int solution = 0;
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        mesh[row][column] = solution++;
      }
    }
  }

  /**
   * Returns the row on the mesh where solution is located
   * @param solution Represents the location of the solution
   * @return
   */
  private int getRow(int solution) {
    return solution / columns;
  }

  /**
   * Returns the column on the mesh where solution is located
   * @param solution Represents the location of the solution
   * @return
   */
  private int getColumn(int solution) {
    return solution % columns;
  }

  /**
   * Returns the neighbor of solution
   * @param solution Represents the location of the solution
   * @param neighbor Represents the neighbor we want to get as a shift of solution. The first component
   * represents the shift on rows, and the second the shift on column
   * @return
   */
  private int getNeighbor(int solution, int [] neighbor) {
    int row = getRow(solution) ;
    int col = getColumn((solution)) ;

    int r ;
    int c ;

    r = (row + neighbor[0]) % this.rows ;
    if (r < 0)
      r = rows - 1;

    c = (col + neighbor[1]) % this.columns ;
    if (c < 0)
      c = columns - 1 ;

    return this.mesh[r][c];
  }

  /**
   * Returns a solutionSet containing the neighbors of a given solution
   * @param solutionSet From where neighbors will be obtained
   * @param solution The solution for which the neighbors will be computed
   * @param neighborhood The list of neighbors we want to obtain as shift regarding to solution
   * @return
   */
  private List<S> findNeighbors(List<S> solutionSet, int solution, int [][] neighborhood) {
      List<S> neighbors = new ArrayList<>(neighborhood.length + 1);
      for (int[] neighbor : neighborhood) {
          int i = getNeighbor(solution, neighbor);
          S s = solutionSet.get(i);
          neighbors.add(s);
      }

      return neighbors;
  }

  /**
   * Returns the north,south, east, and west solutions of a given solution
   * @param solutionList the solution set from where the neighbors are taken
   * @param solutionPosition Represents the position of the solution
   *
   */
  public List<S> getNeighbors(List<S> solutionList, int solutionPosition) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);
    Check.that(solutionPosition >= 0, "The solution position value is negative: " + solutionPosition);
    Check.that(solutionList.size() == rows * columns, "The solution list size " + solutionList.size() + " is not"
            + "equal to the grid size: " + rows + " * " + columns);
    Check.that(solutionPosition < solutionList.size(), "The solution position value " + solutionPosition +
            " is equal or greater than the solution list size "
            + solutionList.size()) ;

    return findNeighbors(solutionList, solutionPosition, neighborhood);
  }
}

