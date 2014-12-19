//  DominanceComparator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing <code>Solution</code> objects)
 * based on a constraint violation, as done in NSGA-II.
 */
public class DominanceComparator implements Comparator<Solution> {
  private ConstraintViolationComparator constraintViolationComparator;

  /** Constructor */
  public DominanceComparator() {
    this(new OverallConstraintViolationComparator()) ;
  }

  /** Constructor */
  public DominanceComparator(ConstraintViolationComparator constraintComparator) {
    constraintViolationComparator = constraintComparator ;
  }
  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 dominates solution2, both are
   * non-dominated, or solution1  is dominated by solution2, respectively.
   */
  @Override
  public int compare(Solution solution1, Solution solution2) {
    // TODO: test for null are needed here
    int result ;
    result = constraintViolationComparator.compare(solution1, solution2) ;
    if (result == 0) {
      result = dominanceTest(solution1, solution2) ;
    }

    return result ;
  }

  private int dominanceTest(Solution solution1, Solution solution2) {
    int result ;
    boolean solution1Dominates = false ;
    boolean solution2Dominates = false ;

    int flag;
    double value1, value2;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      value1 = solution1.getObjective(i);
      value2 = solution2.getObjective(i);
      if (value1 < value2) {
        flag = -1;
      } else if (value1 > value2) {
        flag = 1;
      } else {
        flag = 0;
      }

      if (flag == -1) {
        solution1Dominates = true ;
      }

      if (flag == 1) {
        solution2Dominates = true ;
      }
    }

    if (solution1Dominates == solution2Dominates) {
      // non-dominated solutions
      result = 0;
    } else if (solution1Dominates) {
      // solution1 dominates
      result = -1;
    } else {
      // solution2 dominates
      result = 1;
    }
    return result ;
  }
}
