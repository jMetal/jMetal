//  CrowdingArchive.java
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

package jmetal.util.archive;

import jmetal.core.*;

import java.util.Comparator;

import jmetal.util.comparators.*;
import jmetal.util.Distance;

/**
 * This class implements a bounded archive based on crowding distances (as
 * defined in NSGA-II).
 */
public class CrowdingArchive extends Archive {
  
  /** 
   * Stores the maximum size of the archive.
   */
  private int maxSize_;
  
  /**
   * stores the number of the objectives.
   */
  private int objectives_;    
  
  /**
   * Stores a <code>Comparator</code> for dominance checking.
   */
  private Comparator dominance_;
  
  /**
   * Stores a <code>Comparator</code> for equality checking (in the objective
   * space).
   */
  private Comparator equals_; 

  /**
   * Stores a <code>Comparator</code> for checking crowding distances.
   */
  private Comparator crowdingDistance_; 
  
  /**
   * Stores a <code>Distance</code> object, for distances utilities
   */
  private Distance distance_; 
    
  /**
   * Constructor. 
   * @param maxSize The maximum size of the archive.
   * @param numberOfObjectives The number of objectives.
   */
  public CrowdingArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    maxSize_          = maxSize;
    objectives_       = numberOfObjectives;        
    dominance_        = new DominanceComparator();
    equals_           = new EqualSolutions();
    crowdingDistance_ = new CrowdingDistanceComparator();
    distance_         = new Distance();
    
  } // CrowdingArchive
    
  
  /**
   * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
   * is dominated by any member of the archive, then it is discarded. If the 
   * <code>Solution</code> dominates some members of the archive, these are
   * removed. If the archive is full and the <code>Solution</code> has to be
   * inserted, the solutions are sorted by crowding distance and the one having
   * the minimum crowding distance value.
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false 
   * otherwise.
   */
  public boolean add(Solution solution){
    int flag = 0;
    int i = 0;
    Solution aux; //Store an solution temporally
    while (i < solutionsList_.size()){
      aux = solutionsList_.get(i);            
            
      flag = dominance_.compare(solution,aux);
      if (flag == 1) {               // The solution to add is dominated
        return false;                // Discard the new solution
      } else if (flag == -1) {       // A solution in the archive is dominated
        solutionsList_.remove(i);    // Remove it from the population            
      } else {
          if (equals_.compare(aux,solution)==0) { // There is an equal solution 
        	                                      // in the population
            return false; // Discard the new solution
          }  // if
          i++;
      }
    }
    // Insert the solution into the archive
    solutionsList_.add(solution);        
    if (size() > maxSize_) { // The archive is full
      distance_.crowdingDistanceAssignment(this,objectives_);      
      remove(indexWorst(crowdingDistance_));
    }        
    return true;
  } // add
} // CrowdingArchive
