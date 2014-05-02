//  FitnessAndCrowdingDistanceComparator.java
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

package jmetal.util.comparators;

import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on the fitness and crowding distance.
 */
public class FitnessAndCrowdingDistanceComparator implements Comparator{
  
  /** 
   * stores a comparator for check the fitness value of the solutions
   */
  private static final Comparator fitnessComparator_ =
                              new FitnessComparator();
  /** 
   * stores a comparator for check the crowding distance
   */
  private static final Comparator crowdingDistanceComparator_ = 
                              new CrowdingDistanceComparator();
  
 /**
  * Compares two solutions.
  * @param solution1 Object representing the first <code>Solution</code>.
  * @param solution2 Object representing the second <code>Solution</code>.
  * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than 
  * solution2, respectively.
  */
  public int compare(Object solution1, Object solution2) {    
    
    int flag = fitnessComparator_.compare(solution1,solution2);
    if (flag != 0) {
      return flag;
    } else {
      return crowdingDistanceComparator_.compare(solution1,solution2);        
    }
  } // compares
} // FitnessAndCrowdingDistanceComparator
