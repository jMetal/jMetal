//  HypervolumeArchive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo<juanjod@gmail.com>
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

package jmetal.util.archive;

import jmetal.core.Solution;
import jmetal.qualityIndicator.fastHypervolume.wfg.Point;
import jmetal.qualityIndicator.fastHypervolume.wfg.WFGHV;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Distance;
import jmetal.util.comparators.CrowdingDistanceComparator;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.EqualSolutions;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on crowding distances (as
 * defined in NSGA-II).
 */
public class WFGHypervolumeArchive extends Archive {
  
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
   * Stores a <code>Distance</code> object, for distances utilities
   */
  private Distance distance_; 
      
  private MetricsUtil utils_ ;
  
  private double      offset_ ;
  private Comparator crowdingDistance_; 
  private WFGHV wfg = null;

  /**
   * Constructor. 
   * @param maxSize The maximum size of the archive.
   * @param numberOfObjectives The number of objectives.
   */
  public WFGHypervolumeArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    maxSize_          = maxSize;
    objectives_       = numberOfObjectives;        
    dominance_        = new DominanceComparator();
    equals_           = new EqualSolutions();
    distance_         = new Distance();
    utils_            = new MetricsUtil() ;
    offset_           = 100 ;
    crowdingDistance_ = new CrowdingDistanceComparator();

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
        
      // computing the reference point
      double [] vector = new double[objectives_];
      for (int o = 0; o < objectives_; o++) {
          vector[0] = this.get(0).getObjective(o);
      }
      for (int j = 1; j < this.size(); j++) {
         for (int o = 0; o < objectives_; o++) {
             if (this.get(j).getObjective(o) > vector[o]) {
                 vector[o] = 1.0;//this.get(j).getObjective(o);
             }
         }
      }
      Point p = new Point(vector);
      wfg = new WFGHV(this.objectives_,this.size(),p);
      //remove(indexWorst(crowdingDistance_));
      
      remove(wfg.getLessContributorHV(this));
    }
    return true;
  } // add
  
     
  public void computeHVContribution() {
      // computing the reference point
      double [] vector = new double[objectives_];
      for (int o = 0; o < objectives_; o++) {
          vector[0] = this.get(0).getObjective(o);
      }
      for (int j = 1; j < this.size(); j++) {
         for (int o = 0; o < objectives_; o++) {
             if (this.get(j).getObjective(o) > vector[o]) {
                 vector[o] = 1.0;//this.get(j).getObjective(o);
             }
         }
      }
      Point p = new Point(vector);
      wfg = new WFGHV(this.objectives_,this.size(),p);
      //remove(indexWorst(crowdingDistance_));
      wfg.getLessContributorHV(this);
  }
  /**
   * This method forces to compute the contribution of each solution (required for PAEShv)
   */
  /*
  public void actualiseHVContribution() {
	  if (size() > 2) { // The contribution can be updated
	      double[][] frontValues = this.writeObjectivesToMatrix();
	      int getNumberOfObjectives = objectives_;
	      // STEP 1. Obtain the maximum and minimum values of the Pareto front
	      double[] maximumValues = utils_.getMaximumValues(this.writeObjectivesToMatrix(), getNumberOfObjectives);
	      double[] minimumValues = utils_.getMinimumValues(this.writeObjectivesToMatrix(), getNumberOfObjectives);
	      // STEP 2. Get the normalized front
	      double[][] normalizedFront = utils_.getNormalizedFront(frontValues, maximumValues, minimumValues);
	      // compute offsets for reference point in normalized space
	      double[] offsets = new double[maximumValues.length];
	      for (int i = 0; i < maximumValues.length; i++) {
	        offsets[i] = offset_ / (maximumValues[i] - minimumValues[i]);
	      }
	      // STEP 3. Inverse the pareto front. This is needed because the original
	      //metric by Zitzler is for maximization problems
	      double[][] invertedFront = utils_.invertedFront(normalizedFront);
	      // shift away from origin, so that boundary points also get a contribution > 0
	      for (double[] point : invertedFront) {
	        for (int i = 0; i < point.length; i++) {
	          point[i] += offsets[i];
	        }
	      }

	      // calculate contributions and sort
	      double[] contributions = utils_.hvContributions(objectives_, invertedFront);
	      for (int i = 0; i < contributions.length; i++) {
	        // contribution values are used analogously to crowding distance
	        this.get(i).setCrowdingDistance(contributions[i]);
	      }	     	    	      
	    }	  
  } // computeHVContribution
  */
  
  
  /**
   * This method returns the location (integer position) of a solution in the archive.
   * For that, the equals_ comparator is used
   * 
   */
  /*
  public int getLocation(Solution solution) {
	  int location = -1;
	  int index = 0;
	  while ((index < size()) && (location!=-1) ) {
		  if (equals_.compare(solution, get(index))==0) {
			  location = index;
		  }
		  index++;
	  }
	  return location;
  }  
  */
  
} // HypervolumeArchive
