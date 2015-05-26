package org.uma.jmetal.util.neighborhood.impl;


import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;

import java.util.ArrayList;
import java.util.List;


/**
 * Class defining a L5 neighborhood of a solution belonging to a list of solutions which is
 * structured as a bi-dimensional square mesh. The neighbors are those solutions that are in the positions
 * North, South, East and West
 */
public class TwoDimensionalMesh<S extends Solution> implements Neighborhood<S> {
  private int rows ;
  private int columns ;
  private final int [] north      = {-1,  0};
  private final int [] south      = { 1 , 0};
  private final int [] east       = { 0 , 1};
  private final int [] west       = { 0 ,-1};

  private final int [][] neighborhood = {north, south, west, east};

  private int [][] mesh;

  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize (it has to have an exact squared root)
   */
  public TwoDimensionalMesh(int rows, int columns) {
    this.rows = rows ;
    this.columns = columns ;

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
    return solution / this.rows;
  }

  /**
   * Returns the column on the mesh where solution is located
   * @param solution Represents the location of the solution
   * @return
   */
  private int getColumn(int solution) {
    return solution % this.columns;
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
  public List<S> findNeighbors(List<S> solutionSet, int solution, int [][] neighborhood) {
    List<S> neighbors = new ArrayList<>(neighborhood.length+1);

    for (int [] neighbor : neighborhood) {
      int index = getNeighbor(solution, neighbor) ;
      neighbors.add(solutionSet.get(index));
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
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.size() == 0) {
      throw new JMetalException("The solution list is empty") ;
    } else if (solutionPosition < 0) {
      throw new JMetalException("The solution position value is negative: " + solutionPosition) ;
    } else if (solutionList.size() != rows * columns) {
      throw new JMetalException("The solution list size " + solutionList.size() + " is not"
          + "equal to the grid size: " + rows + " * " + columns) ;
    }
    else if (solutionPosition >= solutionList.size()) {
      throw new JMetalException("The solution position value " + solutionPosition +
          " is equal or greater than the solution list size "
          + solutionList.size()) ;
    }

    return findNeighbors(solutionList, solutionPosition, neighborhood);
  }
}

