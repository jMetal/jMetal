//  EqualSolutions.java
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

import jmetal.core.Solution;

import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based whether all the objective values are
 * equal or not. A dominance test is applied to decide about what solution
 * is the best.
 */
public class EqualSolutions implements Comparator{        
   
  /**
   * Compares two solutions.
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1, or 2 if solution1 is dominates solution2, solution1 
   * and solution2 are equals, or solution1 is greater than solution2, 
   * respectively. 
   */
  public int compare(Object object1, Object object2) {
    if (object1==null)
      return 1;
    else if (object2 == null)
      return -1;
        
    int dominate1 ; // dominate1 indicates if some objective of solution1 
                    // dominates the same objective in solution2. dominate2
    int dominate2 ; // is the complementary of dominate1.
    
    dominate1 = 0 ; 
    dominate2 = 0 ;
    
    Solution solution1 = (Solution)object1;
    Solution solution2 = (Solution)object2;
    
    int flag; 
    double value1, value2;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      flag = (new ObjectiveComparator(i)).compare(solution1,solution2);
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
        dominate1 = 1;
      }
      
      if (flag == 1) {
        dominate2 = 1;
      }
    }
            
    if (dominate1== 0 && dominate2 ==0) {            
      return 0; //No one dominate the other
    }
    
    if (dominate1 == 1) {
      return -1; // solution1 dominate
    } else if (dominate2 == 1) {    
      return 1;    // solution2 dominate
    }
      return 2;
  } // compare
} // EqualSolutions

