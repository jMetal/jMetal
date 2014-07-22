//  Neighborhood.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
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

package org.uma.jmetal.util;

import org.uma.jmetal.core.SolutionSet;

/**
 * Class representing neighborhoods for a <code>Solution</code> into a
 * <code>SolutionSet</code>.
 */
public class Neighborhood {

  /**
   * Maximum rate considered
   */
  private static int MAXRADIO = 2;

  /**
   * Stores the neighborhood.
   * structure [i] represents a neighborhood for a solutiontype.
   * structure [i][j] represents a neighborhood with a ratio.
   * structure [i][j][k] represents a neighbor of a solutiontype.
   */
  private int[][][] structure;

  /**
   * Stores the size of the solutionSet.
   */
  private int solutionSetSize;

  /**
   * Stores the size for each row
   */
  private int rowSize;


  /**
   * Constructor.
   * Defines a neighborhood of a given size.
   *
   * @param solutionSetSize The size.
   */
  public Neighborhood(int solutionSetSize) {
    this.solutionSetSize = solutionSetSize;
    //Create the structure for store the neighborhood
    structure = new int[this.solutionSetSize][MAXRADIO][];

    //For each individual, and different rates the individual has a different
    //number of neighborhoods
    for (int ind = 0; ind < this.solutionSetSize; ind++) {
      for (int radio = 0; radio < MAXRADIO; radio++) {
        if (radio == 0) {
          structure[ind][radio] = new int[8];
        } else if (radio == 1) {
          structure[ind][radio] = new int[24];
        }
      }
    }

    //Calculate the size of a row
    rowSize = (int) Math.sqrt((double) this.solutionSetSize);

    //Calculates the neighbors of a individual
    for (int ind = 0; ind < this.solutionSetSize; ind++) {
      //rate 1
      //North neighbors
      if (ind > rowSize - 1) {
        structure[ind][0][Row.N.ordinal()] = ind - rowSize;
      } else {
        structure[ind][0][Row.N.ordinal()] =
          (ind - rowSize + solutionSetSize) % solutionSetSize;
      }

      //East neighbors
      if ((ind + 1) % rowSize == 0) {
        structure[ind][0][Row.E.ordinal()] = (ind - (rowSize - 1));
      } else {
        structure[ind][0][Row.E.ordinal()] = (ind + 1);
      }

      //Western neighbors
      if (ind % rowSize == 0) {
        structure[ind][0][Row.W.ordinal()] = (ind + (rowSize - 1));
      } else {
        structure[ind][0][Row.W.ordinal()] = (ind - 1);
      }

      //South neighbors
      structure[ind][0][Row.S.ordinal()] = (ind + rowSize) % solutionSetSize;
    }

    for (int ind = 0; ind < this.solutionSetSize; ind++) {
      structure[ind][0][Row.NE.ordinal()] =
        structure[structure[ind][0][Row.N.ordinal()]][0][Row.E.ordinal()];
      structure[ind][0][Row.NW.ordinal()] =
        structure[structure[ind][0][Row.N.ordinal()]][0][Row.W.ordinal()];
      structure[ind][0][Row.SE.ordinal()] =
        structure[structure[ind][0][Row.S.ordinal()]][0][Row.E.ordinal()];
      structure[ind][0][Row.SW.ordinal()] =
        structure[structure[ind][0][Row.S.ordinal()]][0][Row.W.ordinal()];
    }
  }

  /**
   * Returns a <code>SolutionSet</code> with the North, Sout, East and West
   * neighbors solutions of ratio 0 of a given location into a given
   * <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code>.
   * @param location    The location.
   * @return a <code>SolutionSet</code> with the neighbors.
   */
  public SolutionSet getFourNeighbors(SolutionSet solutionSet, int location) throws
    JMetalException {
    //SolutionSet that contains the neighbors (to return)
    SolutionSet neighbors;

    //instance the solutionSet to a non dominated li of individuals
    neighbors = new SolutionSet(24);

    //Gets the neighborhoods N, S, E, W
    int index;

    //North
    index = structure[location][0][Row.N.ordinal()];
    neighbors.add(solutionSet.get(index));

    //South
    index = structure[location][0][Row.S.ordinal()];
    neighbors.add(solutionSet.get(index));

    //East
    index = structure[location][0][Row.E.ordinal()];
    neighbors.add(solutionSet.get(index));

    //West
    index = structure[location][0][Row.W.ordinal()];
    neighbors.add(solutionSet.get(index));

    //Return the list of non-dominated individuals
    return neighbors;
  }

  /**
   * Returns a <code>SolutionSet</code> with the North, Sout, East, West,
   * North-West, South-West, North-East and South-East neighbors solutions of
   * ratio 0 of a given location into a given <code>SolutionSet</code>.
   * solutions of a given location into a given <code>SolutionSet</code>.
   *
   * @param population The <code>SolutionSet</code>.
   * @param individual The individual.
   * @return a <code>SolutionSet</code> with the neighbors.
   */
  public SolutionSet getEightNeighbors(SolutionSet population, int individual) throws
    JMetalException {
    //SolutionSet that contains the neighbors (to return)
    SolutionSet neighbors;

    //instance the population to a non dominated li of individuals
    neighbors = new SolutionSet(24);

    //Gets the neighboords N, S, E, W
    int index;

    //N
    index = this.structure[individual][0][Row.N.ordinal()];
    neighbors.add(population.get(index));

    //S
    index = this.structure[individual][0][Row.S.ordinal()];
    neighbors.add(population.get(index));

    //E
    index = this.structure[individual][0][Row.E.ordinal()];
    neighbors.add(population.get(index));

    //W
    index = this.structure[individual][0][Row.W.ordinal()];
    neighbors.add(population.get(index));

    //NE
    index = this.structure[individual][0][Row.NE.ordinal()];
    neighbors.add(population.get(index));

    //NW
    index = this.structure[individual][0][Row.NW.ordinal()];
    neighbors.add(population.get(index));

    //SE
    index = this.structure[individual][0][Row.SE.ordinal()];
    neighbors.add(population.get(index));

    //SW
    index = this.structure[individual][0][Row.SW.ordinal()];
    neighbors.add(population.get(index));

    //Return the list of non-dominated individuals
    return neighbors;
  }

  /**
   * Enum type for defining the North, South, East, West, North-West, South-West,
   * North-East, South-East neighbor.
   */
  enum Row {
    N, S, E, W, NW, SW, NE, SE
  }
}
