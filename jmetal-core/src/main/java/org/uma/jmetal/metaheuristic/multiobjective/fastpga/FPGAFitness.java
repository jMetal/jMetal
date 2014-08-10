//  FPGAFitness.java
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

package org.uma.jmetal.metaheuristic.multiobjective.fastpga;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;

/**
 * This class implements facilities for calculating the fitness for the
 * FPGA algorithm
 */
public class FPGAFitness {
  private static final Comparator<Solution> dominanceComparator = new DominanceComparator();
  /**
   * Need the population to assign the fitness, this population may contain
   * solutions in the population and the archive
   */
  private SolutionSet solutionSet = null;

  /**
   * Constructor.
   * Create a new instance of Spea2Fitness
   *
   * @param solutionSet The solutionSet to assign the fitness
   * @param problem     The problem to solve
   */
  public FPGAFitness(SolutionSet solutionSet, Problem problem) {
    this.solutionSet = solutionSet;
    for (int i = 0; i < this.solutionSet.size(); i++) {
      this.solutionSet.get(i).setLocation(i);
    }
  }

  /**
   * Assign FPGA fitness to the solutions. Similar to the SPEA2 fitness.
   */
  public void fitnessAssign() {
    double[] strength = new double[solutionSet.size()];

    for (int i = 0; i < solutionSet.size(); i++) {
      if (solutionSet.get(i).getRank() == 0) {
        solutionSet.get(i).setFitness(solutionSet.get(i).getCrowdingDistance());
      }
    }

    //Calculate the strength value
    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionSet.size(); i++) {
      for (int j = 0; j < solutionSet.size(); j++) {
        if (dominanceComparator.compare(solutionSet.get(i), solutionSet.get(j)) == -1) {
          strength[i] += 1.0;
        }
      }
    }

    //Calculate the fitness
    for (int i = 0; i < solutionSet.size(); i++) {
      double fitness = 0.0;
      for (int j = 0; j < solutionSet.size(); j++) {
        int flag = dominanceComparator.compare(solutionSet.get(i), solutionSet.get(j));
        if (flag == -1) {
          fitness += strength[j];
        } else if (flag == 1) {
          fitness -= strength[j];
        }
      }
    }
  }
}
