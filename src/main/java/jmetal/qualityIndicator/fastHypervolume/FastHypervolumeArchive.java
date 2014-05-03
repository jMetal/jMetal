//  FastHypervolumeArchive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2013 Antonio J. Nebro
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

package jmetal.qualityIndicator.fastHypervolume;

import jmetal.core.Solution;
import jmetal.util.archive.Archive;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.EqualSolutions;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on the hypervolume quality indicator
 */
public class FastHypervolumeArchive extends Archive {

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

  private Comparator crowdingDistance_ ;

  public Solution referencePoint_ ;

  /**
   * Constructor.
   * @param maxSize The maximum size of the archive.
   * @param numberOfObjectives The number of objectives.
   */
  public FastHypervolumeArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    maxSize_          = maxSize;
    objectives_       = numberOfObjectives;        
    dominance_        = new DominanceComparator();
    equals_           = new EqualSolutions();
    referencePoint_   = new Solution(objectives_) ;
    for (int i = 0; i < objectives_; i++)
      referencePoint_.setObjective(i, Double.MAX_VALUE) ;

    crowdingDistance_ = new CrowdingComparator();
  } // FastHypervolumeArchive
    
  
  /**
   * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
   * is dominated by any member of the archive, then it is discarded. If the 
   * <code>Solution</code> dominates some members of the archive, these are
   * removed. If the archive is full and the <code>Solution</code> has to be
   * inserted, the solution contributing the least to the HV of the solution set
   * is discarded.
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
      computeHVContribution();

      remove(indexWorst(crowdingDistance_));
      //remove(solutionsList_.size()-1);
    }
    return true;
  } // add
  
     
  /**
   * This method forces to compute the contribution of each solution (required for PAEShv)
   */
  public void computeHVContribution() {
	  if (size() > 2) { // The contribution can be updated

      FastHypervolume fastHV = new FastHypervolume() ;
      fastHV.computeHVContributions(this);
    }
  } // computeHVContribution
} // FastHypervolumeArchive
