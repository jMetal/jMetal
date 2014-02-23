//  OverallConstraintViolationComparator.java
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
import jmetal.core.SolutionSet;

// This class implements the ViolationThreshold Comparator 
public class ViolationThresholdComparator
  implements IConstraintViolationComparator {
   
    
  // threshold used for the comparations
  private double threshold_ = 0.0;
 /** 
  * Compares two solutions.
  * @param o1 Object representing the first <code>Solution</code>.
  * @param o2 Object representing the second <code>Solution</code>.
  * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
  * respectively.
  */
  public int compare(Object o1, Object o2) {    
    double overall1, overall2;
    overall1 = ((Solution) o1).getNumberOfViolatedConstraint() * 
                ((Solution)o1).getOverallConstraintViolation();
    overall2 = ((Solution) o2).getNumberOfViolatedConstraint() *
                ((Solution)o2).getOverallConstraintViolation();
        
    if ((overall1 < 0) && (overall2 < 0)) {
      if (overall1 > overall2){
        return -1;
      } else if (overall2 > overall1){
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
  } // compare    
  
  /**
   * Returns true if solutions s1 and/or s2 have an overall constraint
   * violation < 0
   */
  public boolean needToCompare(Solution o1, Solution o2) {
    boolean needToCompare ;
    double overall1, overall2;
    overall1 = Math.abs(((Solution) o1).getNumberOfViolatedConstraint() * 
                ((Solution)o1).getOverallConstraintViolation());
    overall2 = Math.abs(((Solution) o2).getNumberOfViolatedConstraint() *
                ((Solution)o2).getOverallConstraintViolation());

    needToCompare = (overall1 > this.threshold_) || (overall2 > this.threshold_);
    
    return needToCompare ;
  }
  
  
  /**
   * Computes the feasibility ratio
   * Return the ratio of feasible solutions
   */
  public double feasibilityRatio(SolutionSet solutionSet) {
      double aux = 0.0;
      for (int i = 0; i < solutionSet.size(); i++) {
          if (solutionSet.get(i).getOverallConstraintViolation() < 0) {
              aux = aux+1.0;
          }
      }
      return aux / (double)solutionSet.size();
  } // feasibilityRatio
  
  /**
   * Computes the feasibility ratio
   * Return the ratio of feasible solutions
   */
  public double meanOveralViolation(SolutionSet solutionSet) {
      double aux = 0.0;
      for (int i = 0; i < solutionSet.size(); i++) {
          aux += Math.abs(solutionSet.get(i).getNumberOfViolatedConstraint() * 
                          solutionSet.get(i).getOverallConstraintViolation());
      }
      return aux / (double)solutionSet.size();
  } // meanOveralViolation
  
  
  /**
   * Updates the threshold value using the population
   */
  public void updateThreshold(SolutionSet set) {
      threshold_ = feasibilityRatio(set) * meanOveralViolation(set);
               
  } // updateThreshold
  
} // ViolationThresholdComparator