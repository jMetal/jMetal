//  AdaptiveGrid.java
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

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.random.PseudoRandom;

/**
 * This class defines an adaptive grid over a SolutionSet as the one used the
 * algorithm PAES.
 */
public class AdaptiveGrid {

  /**
   * Number of bi-divisions of the objective space
   */
  private int bisections;

  /**
   * Objectives of the problem
   */
  private int objectives;

  /**
   * Number of solutions into a specific hypercube in the adaptative grid
   */
  private int[] hypercubes;

  /**
   * Grid lower bounds
   */
  private double[] lowerLimits;

  /**
   * Grid upper bounds
   */
  private double[] upperLimits;

  /**
   * Size of hypercube for each dimension
   */
  private double[] divisionSize;

  /**
   * Hypercube with maximum number of solutions
   */
  private int mostPopulated;

  /**
   * Indicates when an hypercube has solutions
   */
  private int[] occupied;

  /**
   * Constructor.
   * Creates an instance of AdaptativeGrid.
   *
   * @param bisections Number of bi-divisions of the objective space.
   * @param objetives  Number of objectives of the problem.
   */
  public AdaptiveGrid(int bisections, int objetives) {
    this.bisections = bisections;
    objectives = objetives;
    lowerLimits = new double[objectives];
    upperLimits = new double[objectives];
    divisionSize = new double[objectives];
    hypercubes = new int[(int) Math.pow(2.0, this.bisections * objectives)];

    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0;
    }
  }


  /**
   * Updates the grid limits considering the solutions contained in a
   * <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code> considered.
   */
  private void updateLimits(SolutionSet solutionSet) {
    //Init the lower and upper limits 
    for (int obj = 0; obj < objectives; obj++) {
      //Set the lower limits to the max real
      lowerLimits[obj] = Double.MAX_VALUE;
      //Set the upper limits to the min real
      upperLimits[obj] = Double.MIN_VALUE;
    }

    //Find the max and min limits of objetives into the population
    for (int ind = 0; ind < solutionSet.size(); ind++) {
      Solution tmpIndividual = solutionSet.get(ind);
      for (int obj = 0; obj < objectives; obj++) {
        if (tmpIndividual.getObjective(obj) < lowerLimits[obj]) {
          lowerLimits[obj] = tmpIndividual.getObjective(obj);
        }
        if (tmpIndividual.getObjective(obj) > upperLimits[obj]) {
          upperLimits[obj] = tmpIndividual.getObjective(obj);
        }
      }
    }
  }

  /**
   * Updates the grid adding solutions contained in a specific
   * <code>SolutionSet</code>.
   * <b>REQUIRE</b> The grid limits must have been previously calculated.
   *
   * @param solutionSet The <code>SolutionSet</code> considered.
   */
  private void addSolutionSet(SolutionSet solutionSet) {
    //Calculate the location of all individuals and update the grid
    mostPopulated = 0;
    int location;

    for (int ind = 0; ind < solutionSet.size(); ind++) {
      location = location(solutionSet.get(ind));
      hypercubes[location]++;
      if (hypercubes[location] > hypercubes[mostPopulated]) {
        mostPopulated = location;
      }
    }

    //The grid has been updated, so also update ocuppied's hypercubes
    calculateOccupied();
  }


  /**
   * Updates the grid limits and the grid content adding the solutions contained
   * in a specific <code>SolutionSet</code>.
   *
   * @param solutionSet The <code>SolutionSet</code>.
   */
  public void updateGrid(SolutionSet solutionSet) {
    //Update lower and upper limits
    updateLimits(solutionSet);

    //Calculate the division size
    for (int obj = 0; obj < objectives; obj++) {
      divisionSize[obj] = upperLimits[obj] - lowerLimits[obj];
    }

    //Clean the hypercubes
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0;
    }

    //Add the population
    addSolutionSet(solutionSet);
  }


  /**
   * Updates the grid limits and the grid content adding a new
   * <code>Solution</code>.
   * If the solutiontype falls out of the grid bounds, the limits and content of the
   * grid must be re-calculated.
   *
   * @param solution    <code>Solution</code> considered to update the grid.
   * @param solutionSet <code>SolutionSet</code> used to update the grid.
   */
  public void updateGrid(Solution solution, SolutionSet solutionSet) {

    int location = location(solution);
    if (location == -1) {
      //Re-build the Adaptative-Grid
      //Update lower and upper limits
      updateLimits(solutionSet);

      //Actualize the lower and upper limits whit the individual      
      for (int obj = 0; obj < objectives; obj++) {
        if (solution.getObjective(obj) < lowerLimits[obj]) {
          lowerLimits[obj] = solution.getObjective(obj);
        }
        if (solution.getObjective(obj) > upperLimits[obj]) {
          upperLimits[obj] = solution.getObjective(obj);
        }
      }

      //Calculate the division size
      for (int obj = 0; obj < objectives; obj++) {
        divisionSize[obj] = upperLimits[obj] - lowerLimits[obj];
      }

      //Clean the hypercube
      for (int i = 0; i < hypercubes.length; i++) {
        hypercubes[i] = 0;
      }

      //add the population
      addSolutionSet(solutionSet);
    }
  }

  /**
   * Calculates the hypercube of a solutiontype.
   *
   * @param solution The <code>Solution</code>.
   */
  public int location(Solution solution) {
    //Create a int [] to store the range of each objetive
    int[] position = new int[objectives];

    //Calculate the position for each objetive
    for (int obj = 0; obj < objectives; obj++) {
      if ((solution.getObjective(obj) > upperLimits[obj])
        || (solution.getObjective(obj) < lowerLimits[obj])) {
        return -1;
      } else if (solution.getObjective(obj) == lowerLimits[obj]) {
        position[obj] = 0;
      } else if (solution.getObjective(obj) == upperLimits[obj]) {
        position[obj] = ((int) Math.pow(2.0, bisections)) - 1;
      } else {
        double tmpSize = divisionSize[obj];
        double value = solution.getObjective(obj);
        double account = lowerLimits[obj];
        int ranges = (int) Math.pow(2.0, bisections);
        for (int b = 0; b < bisections; b++) {
          tmpSize /= 2.0;
          ranges /= 2;
          if (value > (account + tmpSize)) {
            position[obj] += ranges;
            account += tmpSize;
          }
        }
      }
    }

    //Calcualate the location into the hypercubes
    int location = 0;
    for (int obj = 0; obj < objectives; obj++) {
      location += position[obj] * Math.pow(2.0, obj * bisections);
    }
    return location;
  }

  /**
   * Returns the value of the most populated hypercube.
   *
   * @return The hypercube with the maximum number of solutions.
   */
  public int getMostPopulated() {
    return mostPopulated;
  }

  /**
   * Returns the number of solutions into a specific hypercube.
   *
   * @param location Number of the hypercube.
   * @return The number of solutions into a specific hypercube.
   */
  public int getLocationDensity(int location) {
    return hypercubes[location];
  }

  /**
   * Decreases the number of solutions into a specific hypercube.
   *
   * @param location Number of hypercube.
   */
  public void removeSolution(int location) {
    //Decrease the solutions in the location specified.
    hypercubes[location]--;

    //Update the most poblated hypercube
    if (location == mostPopulated) {
      for (int i = 0; i < hypercubes.length; i++) {
        if (hypercubes[i] > hypercubes[mostPopulated]) {
          mostPopulated = i;
        }
      }
    }

    //If hypercubes[location] now becomes to zero, then update ocupped hypercubes
    if (hypercubes[location] == 0) {
      this.calculateOccupied();
    }
  }

  /**
   * Increases the number of solutions into a specific hypercube.
   *
   * @param location Number of hypercube.
   */
  public void addSolution(int location) {
    //Increase the solutions in the location specified.
    hypercubes[location]++;

    //Update the most poblated hypercube
    if (hypercubes[location] > hypercubes[mostPopulated]) {
      mostPopulated = location;
    }

    //if hypercubes[location] becomes to one, then recalculate 
    //the occupied hypercubes
    if (hypercubes[location] == 1) {
      this.calculateOccupied();
    }
  }

  /**
   * Returns the number of bi-divisions performed in each objective.
   *
   * @return the number of bi-divisions.
   */
  public int getBisections() {
    return bisections;
  }

  /**
   * Retunrns a String representing the grid.
   *
   * @return The String.
   */
  public String toString() {
    String result = "Grid\n";
    for (int obj = 0; obj < objectives; obj++) {
      result += "Objective " + obj + " " + lowerLimits[obj] + " "
        + upperLimits[obj] + "\n";
    }
    return result;
  }

  /**
   * Returns a random hypercube using a rouleteWheel method.
   *
   * @return the number of the selected hypercube.
   */
  public int rouletteWheel() {
    //Calculate the inverse sum
    double inverseSum = 0.0;
    for (int aHypercubes_ : hypercubes) {
      if (aHypercubes_ > 0) {
        inverseSum += 1.0 / (double) aHypercubes_;
      }
    }

    //Calculate a random value between 0 and sumaInversa
    double random = PseudoRandom.randDouble(0.0, inverseSum);
    int hypercube = 0;
    double accumulatedSum = 0.0;
    while (hypercube < hypercubes.length) {
      if (hypercubes[hypercube] > 0) {
        accumulatedSum += 1.0 / (double) hypercubes[hypercube];
      }

      if (accumulatedSum > random) {
        return hypercube;
      }

      hypercube++;
    }

    return hypercube;
  }

  /**
   * Calculates the number of hypercubes having one or more solutions.
   * return the number of hypercubes with more than zero solutions.
   */
  public int calculateOccupied() {
    int total = 0;
    for (int aHypercubes_ : hypercubes) {
      if (aHypercubes_ > 0) {
        total++;
      }
    }

    occupied = new int[total];
    int base = 0;
    for (int i = 0; i < hypercubes.length; i++) {
      if (hypercubes[i] > 0) {
        occupied[base] = i;
        base++;
      }
    }

    return total;
  }

  /**
   * Returns the number of hypercubes with more than zero solutions.
   *
   * @return the number of hypercubes with more than zero solutions.
   */
  public int occupiedHypercubes() {
    return occupied.length;
  }


  /**
   * Returns a random hypercube that has more than zero solutions.
   *
   * @return The hypercube.
   */
  public int randomOccupiedHypercube() {
    int rand = PseudoRandom.randInt(0, occupied.length - 1);
    return occupied[rand];
  }
}

