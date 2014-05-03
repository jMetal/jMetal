//  EpsilonObjectiveComparator.java
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
 * <code>Solution</code> objects) based on epsilon dominance over a given
 * objective function.
 */
public class EpsilonObjectiveComparator implements Comparator{
    
  /**
   * Stores the objective index to compare
   */
  private int objective_;
  
  /**
   * Stores the eta value for epsilon-dominance
   */
  private double eta_;
    
  /** 
   * Constructor.
   * @param nObj Index of the objective to compare.
   * @param eta Value for epsilon-dominance.
   */
  public EpsilonObjectiveComparator(int nObj,double eta) {
    objective_ = nObj;
    eta_       = eta;
  } // EObjectiveComparator
    
 /**
  * Compares two solutions.
  * @param o1 Object representing the first <code>Solution</code>.
  * @param o2 Object representing the second <code>Solution</code>.
  * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
  * respectively.
  */
  public int compare(Object o1, Object o2) {
    if (o1==null)
      return 1;
    else if (o2 == null)
      return -1;
    
    double objetive1 = ((Solution)o1).getObjective(objective_);
    double objetive2 = ((Solution)o2).getObjective(objective_);
        
    //Objetive implements comparable!!! 
    if (objetive1/(1 + eta_) < objetive2) {
      return -1;
    } else if (objetive1/(1 + eta_) > objetive2) {
      return 1;
    } else {
      return 0;
    }
  } // compare
} // EObjectiveComparator
