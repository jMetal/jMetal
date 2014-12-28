//  ViolationThresholdComparator.java
//
//  Author:
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Juan J. Durillo
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
package org.uma.jmetal.util.comparator.impl;


import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;

import java.util.List;

// This class implements the ViolationThreshold Comparator
public class ViolationThresholdComparator<S extends Solution> implements
    ConstraintViolationComparator {

  private double threshold = 0.0;

  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
   * respectively.
   */
  @Override
  public int compare(Solution solution1, Solution solution2) {
    double overall1, overall2;
    overall1 = solution1.getNumberOfViolatedConstraints() *
        solution1.getOverallConstraintViolationDegree();
    overall2 = solution2.getNumberOfViolatedConstraints() *
        solution2.getOverallConstraintViolationDegree();

    if ((overall1 < 0) && (overall2 < 0)) {
      if (overall1 > overall2) {
        return -1;
      } else if (overall2 > overall1) {
        return 1;
      } else {
        return 0;
      }
    } else if ((overall1 == 0) && (overall2 < 0)) {
      return -1;
    } else if ((overall1 < 0) && (overall2 == 0)) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Returns true if solutions s1 and/or s2 have an overall constraint
   * violation with value less than 0
   */
  public boolean needToCompare(Solution solution1, Solution solution2) {
    boolean needToCompare;
    double overall1, overall2;
    overall1 = Math.abs(solution1.getNumberOfViolatedConstraints() *
        solution1.getOverallConstraintViolationDegree());
    overall2 = Math.abs(solution2.getNumberOfViolatedConstraints() *
        solution2.getOverallConstraintViolationDegree());

    needToCompare = (overall1 > this.threshold) || (overall2 > this.threshold);

    return needToCompare;
  }

  /**
   * Computes the feasibility ratio
   * Return the ratio of feasible solutions
   */
  public double feasibilityRatio(List<S> solutionSet) {
    double aux = 0.0;
    for (int i = 0; i < solutionSet.size(); i++) {
      if (solutionSet.get(i).getOverallConstraintViolationDegree() < 0) {
        aux = aux + 1.0;
      }
    }
    return aux / (double) solutionSet.size();
  }

  /**
   * Computes the feasibility ratio
   * Return the ratio of feasible solutions
   */
  public double meanOverallViolation(List<S> solutionSet) {
    double aux = 0.0;
    for (int i = 0; i < solutionSet.size(); i++) {
      aux += Math.abs(solutionSet.get(i).getNumberOfViolatedConstraints() *
          solutionSet.get(i).getOverallConstraintViolationDegree());
    }
    return aux / (double) solutionSet.size();
  }


  /**
   * Updates the threshold value using the population
   */
  public void updateThreshold(List<S> set) {
    threshold = feasibilityRatio(set) * meanOverallViolation(set);
  }
}
