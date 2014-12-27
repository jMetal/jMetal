//  OverallConstraintViolationComparator.java
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

package org.uma.jmetal.util.comparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing <code>Solution</code> objects)
 * based on the overall constraint violation of the solutions, as done in NSGA-II.
 */
public class OverallConstraintViolationComparator implements ConstraintViolationComparator {
  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
   * respectively.
   */
  public int compare(Solution solution1, Solution solution2) {
    double violationDegreeSolution1 ;
    double violationDegreeSolution2;
    violationDegreeSolution1 =  solution1.getOverallConstraintViolationDegree();
    violationDegreeSolution2 =  solution2.getOverallConstraintViolationDegree();

    if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 < 0)) {
      if (violationDegreeSolution1 > violationDegreeSolution2) {
        return -1;
      } else if (violationDegreeSolution2 > violationDegreeSolution1) {
        return 1;
      } else {
        return 0;
      }
    } else if ((violationDegreeSolution1 == 0) && (violationDegreeSolution2 < 0)) {
      return -1;
    } else if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 == 0)) {
      return 1;
    } else {
      return 0;
    }
  }
}
