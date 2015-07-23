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

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * This class defines an adaptive grid over a list of solutions as the one used by algorithm PAES.
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 */
public class AdaptiveGrid<S extends Solution<?>> {
  private int bisections;
  private int numberOfObjectives;
  private int[] hypercubes;

  private double[] gridLowerLimits;
  private double[] gridUpperLimits;

  private double[] divisionSize;
  private int mostPopulatedHypercube;

  /**
   * Indicates when an hypercube has solutions
   */
  private int[] occupied;

  /**
   * Constructor.
   * Creates an instance of AdaptiveGrid.
   *
   * @param bisections Number of bi-divisions of the objective space.
   * @param objetives  Number of numberOfObjectives of the problem.
   */
  public AdaptiveGrid(int bisections, int objetives) {
    this.bisections = bisections;
    numberOfObjectives = objetives;
    gridLowerLimits = new double[numberOfObjectives];
    gridUpperLimits = new double[numberOfObjectives];
    divisionSize = new double[numberOfObjectives];
    hypercubes = new int[(int) Math.pow(2.0, this.bisections * numberOfObjectives)];

    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0;
    }
  }

  /**
   * Updates the grid limits considering the solutions contained in a
   * <code>solutionList</code>.
   *
   * @param solutionList The <code>solutionList</code> considered.
   */
  private void updateLimits(List<S> solutionList) {
    for (int obj = 0; obj < numberOfObjectives; obj++) {
      gridLowerLimits[obj] = Double.MAX_VALUE;
      gridUpperLimits[obj] = Double.MIN_VALUE;
    }

    //Find the max and min limits of objetives into the population
    for (int ind = 0; ind < solutionList.size(); ind++) {
      Solution<?> tmpIndividual = solutionList.get(ind);
      for (int obj = 0; obj < numberOfObjectives; obj++) {
        if (tmpIndividual.getObjective(obj) < gridLowerLimits[obj]) {
          gridLowerLimits[obj] = tmpIndividual.getObjective(obj);
        }
        if (tmpIndividual.getObjective(obj) > gridUpperLimits[obj]) {
          gridUpperLimits[obj] = tmpIndividual.getObjective(obj);
        }
      }
    }
  }

  /**
   * Updates the grid adding solutions contained in a specific
   * <code>solutionList</code>.
   * <b>REQUIRE</b> The grid limits must have been previously calculated.
   *
   * @param solutionList The <code>solutionList</code> considered.
   */
  private void addSolutionSet(List<S> solutionList) {
    //Calculate the location of all individuals and update the grid
    mostPopulatedHypercube = 0;
    int location;

    for (int ind = 0; ind < solutionList.size(); ind++) {
      location = location(solutionList.get(ind));
      hypercubes[location]++;
      if (hypercubes[location] > hypercubes[mostPopulatedHypercube]) {
        mostPopulatedHypercube = location;
      }
    }

    //The grid has been updated, so also update ocuppied's hypercubes
    calculateOccupied();
  }


  /**
   * Updates the grid limits and the grid content adding the solutions contained
   * in a specific <code>solutionList</code>.
   *
   * @param solutionList The <code>solutionList</code>.
   */
  public void updateGrid(List<S> solutionList) {
    //Update lower and upper limits
    updateLimits(solutionList);

    //Calculate the division size
    for (int obj = 0; obj < numberOfObjectives; obj++) {
      divisionSize[obj] = gridUpperLimits[obj] - gridLowerLimits[obj];
    }

    //Clean the hypercubes
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0;
    }

    //Add the population
    addSolutionSet(solutionList);
  }


  /**
   * Updates the grid limits and the grid content adding a new
   * <code>Solution</code>.
   * If the solution falls out of the grid bounds, the limits and content of the
   * grid must be re-calculated.
   *
   * @param solution    <code>Solution</code> considered to update the grid.
   * @param solutionSet <code>SolutionSet</code> used to update the grid.
   */
  public void updateGrid(S solution, List<S>  solutionSet) {

    int location = location(solution);
    if (location == -1) {
      //Re-build the Adaptative-Grid
      //Update lower and upper limits
      updateLimits(solutionSet);

      //Actualize the lower and upper limits whit the individual      
      for (int obj = 0; obj < numberOfObjectives; obj++) {
        if (solution.getObjective(obj) < gridLowerLimits[obj]) {
          gridLowerLimits[obj] = solution.getObjective(obj);
        }
        if (solution.getObjective(obj) > gridUpperLimits[obj]) {
          gridUpperLimits[obj] = solution.getObjective(obj);
        }
      }

      //Calculate the division size
      for (int obj = 0; obj < numberOfObjectives; obj++) {
        divisionSize[obj] = gridUpperLimits[obj] - gridLowerLimits[obj];
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
   * Calculates the hypercube of a solution
   *
   * @param solution The <code>Solution</code>.
   */
  public int location(S solution) {
    //Create a int [] to store the range of each objective
    int[] position = new int[numberOfObjectives];

    //Calculate the position for each objective
    for (int obj = 0; obj < numberOfObjectives; obj++) {
      if ((solution.getObjective(obj) > gridUpperLimits[obj])
        || (solution.getObjective(obj) < gridLowerLimits[obj])) {
        return -1;
      } else if (solution.getObjective(obj) == gridLowerLimits[obj]) {
        position[obj] = 0;
      } else if (solution.getObjective(obj) == gridUpperLimits[obj]) {
        position[obj] = ((int) Math.pow(2.0, bisections)) - 1;
      } else {
        double tmpSize = divisionSize[obj];
        double value = solution.getObjective(obj);
        double account = gridLowerLimits[obj];
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

    //Calculate the location into the hypercubes
    int location = 0;
    for (int obj = 0; obj < numberOfObjectives; obj++) {
      location += position[obj] * Math.pow(2.0, obj * bisections);
    }
    return location;
  }

  /**
   * Returns the value of the most populated hypercube.
   *
   * @return The hypercube with the maximum number of solutions.
   */
  public int getMostPopulatedHypercube() {
    return mostPopulatedHypercube;
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

    //Update the most populated hypercube
    if (location == mostPopulatedHypercube) {
      for (int i = 0; i < hypercubes.length; i++) {
        if (hypercubes[i] > hypercubes[mostPopulatedHypercube]) {
          mostPopulatedHypercube = i;
        }
      }
    }

    //If hypercubes[location] now becomes to zero, then update ocuppied hypercubes
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
    if (hypercubes[location] > hypercubes[mostPopulatedHypercube]) {
      mostPopulatedHypercube = location;
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
   * Returns a String representing the grid.
   *
   * @return The String.
   */
  public String toString() {
    String result = "Grid\n";
    for (int obj = 0; obj < numberOfObjectives; obj++) {
      result += "Objective " + obj + " " + gridLowerLimits[obj] + " "
        + gridUpperLimits[obj] + "\n";
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
    for (int hypercube : hypercubes) {
      if (hypercube > 0) {
        inverseSum += 1.0 / (double) hypercube;
      }
    }

    //Calculate a random value between 0 and sumaInversa
    double random = JMetalRandom.getInstance().nextDouble(0.0, inverseSum);
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
    for (int hypercube : hypercubes) {
      if (hypercube > 0) {
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
    int rand = JMetalRandom.getInstance().nextInt(0, occupied.length - 1);
    return occupied[rand];
  }
}

