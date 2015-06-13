package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 * Modified by Juanjo on 07/04/2015
 */
public class CrowdingDistanceArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> crowdingDistanceComparator;
  private DensityEstimator<S> crowdingDistance ;
  

  public CrowdingDistanceArchive(int maxSize) {
    super(maxSize);
	crowdingDistanceComparator = new CrowdingDistanceComparator<S>() ;
    crowdingDistance = new CrowdingDistance<S>() ;
  }

  @Override
  public void prune() {
	  
    if (getSolutionList().size() > getMaxSize()) { 
	      crowdingDistance.computeDensityEstimator(getSolutionList());
	      int index = new SolutionListUtils().findWorstSolution(getSolutionList(), crowdingDistanceComparator) ;
	      getSolutionList().remove(index);
    }
  }

  public void computeDistance() {
	    crowdingDistance.computeDensityEstimator(getSolutionList());
	  }
  
 }
