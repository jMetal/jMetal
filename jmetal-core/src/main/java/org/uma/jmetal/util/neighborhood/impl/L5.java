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
public class L5<S extends Solution> implements Neighborhood<S> {
  /*
  Each movement is represented by an array of two positions. The first component represents the
  movement in the file, second in the column
   */
  private final int [] north      = {-1,  0};
  private final int [] south      = { 1 , 0};
  private final int [] east       = { 0 , 1};
  private final int [] west       = { 0 ,-1};

  private final int [][] neighborhood = {north, south, west, east};

  private int [][] mesh;
  private int rows;
  private int columns;

  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize (it has to have an exact squared root)
   */
  public L5() {
  }


  /**
   * Checks whether a value has an exact square root
   * @param number (we know is bigger than 0)
   * @return
   */
  private boolean hasExactSquaredRoot(int number) {
    boolean r = Math.round(Math.sqrt(number))==Math.sqrt(number) ;

    return r ;
    //return (number & (number-1)) == 0;
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
    int r = getRow(solution)    > 0 ? (getRow(solution)    + neighbor[0]) % this.rows    : this.rows-1;
    int c = getColumn(solution) > 0 ? (getColumn(solution) + neighbor[1]) % this.columns : this.columns-1;
    return this.mesh[r][c];
  }


  /**
   * Initializes the mesh of solutions. Each solution is assigned to a row and column within the row
   */
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

    mesh = new int[this.rows][this.columns];
    int solution = 0;
    for (int row = 0; row < this.rows; row++) {
      for (int column = 0; column < this.columns; column++) {
        this.mesh[row][column] = solution++;
      }
    }
  }


  /**
   * Returns a solutionSet containing the neighbors of a given solution
   * @param solutionSet From where neighbors will be obtained
   * @param solution The solution for which the neighbors will be computed
   * @param neighborhood The list of neighbors we want to obtain as shift regarding to solution
   * @return
   */
  public List<S> getFourNeighbors(List<S> solutionSet, int solution, int[][] neighborhood) {
    this.createMesh();

    List<S> neighbors = new ArrayList<>(neighborhood.length+1);

    for (int [] neighbor : neighborhood) {
      neighbors.add(solutionSet.get(this.getNeighbor(solution, neighbor)));
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
    } else if (!this.hasExactSquaredRoot(solutionList.size())) {
      throw new JMetalException("The solution list size must have an exact square root: " +
          solutionList.size());
    } else if (solutionPosition < 0) {
      throw new JMetalException("The solution position value is negative: " + solutionPosition) ;
    } else if (solutionPosition >= solutionList.size()) {
    throw new JMetalException("The solution position value is equal or greater than the solution list size: "
        + solutionPosition) ;
  }

    this.rows = this.columns = (int) Math.sqrt(solutionList.size());

    return this.getFourNeighbors(solutionList, solutionPosition, this.neighborhood);
  }
}

