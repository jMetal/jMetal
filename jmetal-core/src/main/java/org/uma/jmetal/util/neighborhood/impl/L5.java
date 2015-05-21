package org.uma.jmetal.util.neighborhood.impl;

//  Neighborhood.java
//
//  Author:
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;


/**
 * Class representing neighborhoods for a <code>Solution</code> into a
 * <code>SolutionSet</code>.
 */
public class L5<S extends Solution> {


  // 8 possible movements north, south, east, west, northeast, northwest,southeast, southwest
  // each movement is represented by an array of two positions, first component represents the
  // movement in the file, second in the column
  private final int [] north      = {-1,  0};
  private final int [] south      = { 1 , 0};
  private final int [] east       = { 0 , 1};
  private final int [] west       = { 0 ,-1};
  private final int [] north_east = {-1,  1};
  private final int [] north_west = {-1, -1};
  private final int [] south_east = { 1 , 1};
  private final int [] south_west = { 1 ,-1};

  // two possible neighborhoods: 4 and 8 neighbors
  private final int [][] neighborhood4 = {north, south, west, east};
  private final int [][] neighborhood8 = {north, south, west, east, north_east, north_west, south_east, south_west};

  private int [][] mesh; // represent the individuals on a rectangular grid
  private int rows;
  private int columns;


  /**
   * Constructor.
   * Defines a neighborhood of shape rows x columns (rows x columns must equal solutionSetSize)
   * @param solutionSetSize The size
   * @param rows the number of rows
   * @param columns the number of columns
   */
  public L5(int solutionSetSize, int rows, int columns) {
    assert (solutionSetSize > 0 && rows * columns == solutionSetSize);
    this.rows = rows;
    this.columns = columns;
    this.createMesh();
  }


  /**
   * Constructor.
   * Defines a neighborhood for solutionSetSize (it has to have an exact squared root)
   * @param solutionSetSize The size.
   */
  public L5(int solutionSetSize) {
    assert (solutionSetSize > 0 && hasExactSquaredRoot(solutionSetSize));
    this.rows = this.columns = (int) Math.sqrt(solutionSetSize);
    this.createMesh();
  }


  /**
   * Checks whether a value has an exact square root
   * @param value (we know is bigger than 0)
   * @return
   */
  private boolean hasExactSquaredRoot(int value) {
    return (value & (value-1)) == 0;
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
   * @param neighboor Represents the neighbor we want to get as a shift of solution. The first component
   * represents the shift on rows, and the second the shift on column
   * @return
   */
  private int getNeighboor(int solution, int [] neighboor) {
    int r = getRow(solution)    > 0 ? (getRow(solution)    + neighboor[0]) % this.rows    : this.rows-1;
    int c = getColumn(solution) > 0 ? (getColumn(solution) + neighboor[1]) % this.columns : this.columns-1;
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
   * @param neigboorhood The list of neighbors we want to obtain as shift regarding to solution
   * @return
   */
  public List<S> getNeigboors(List<S> solutionSet, int solution, int [][] neigboorhood) {
    //SolutionSet that contains the neighbors (to return)
    List<S> neighbors = new ArrayList<>(neigboorhood.length+1);

    for (int [] neighboor : neigboorhood)
      neighbors.add(solutionSet.get(this.getNeighboor(solution,neighboor)));

    return neighbors;
  }



  /**
   * Returns the north,south, east, and west solutions of a given solution
   * @param solutionSet the solution set from where the neighbors are taken
   * @param solution Represents the position of the solution
   *
   */
  public List<S> getFourNeighbors(List<S> solutionSet, int solution) {
    return this.getNeigboors(solutionSet, solution, this.neighborhood4);
  }

  /**
   * Returns the north,south, east, west, northeast, northwest, southeast, and southwest solutions of a given solution
   * @param solutionSet the solution set from where the neighbors are taken
   * @param location Represents the position of the solution
   *
   */
  public List<S> getEightNeighbors(List<S> solutionSet, int location){
    return this.getNeigboors(solutionSet,location,this.neighborhood8);
  }
}

