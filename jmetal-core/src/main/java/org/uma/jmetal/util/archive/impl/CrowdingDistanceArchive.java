package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 * Modified by Juanjo on 07/04/2015
 */
@SuppressWarnings("serial")
public class CrowdingDistanceArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> crowdingDistanceComparator;
  private DensityEstimator<S> crowdingDistance ;

  public CrowdingDistanceArchive(int maxSize, DominanceComparator<S> dominanceComparator) {
    super(maxSize, dominanceComparator);
    crowdingDistance = new CrowdingDistanceDensityEstimator<>();
    crowdingDistanceComparator = Comparator.comparing(crowdingDistance::getValue).reversed() ;
  }

  public CrowdingDistanceArchive(int maxSize) {
    this(maxSize, new DefaultDominanceComparator<>()) ;
  }

  @Override
  public void prune() {
    if (solutions().size() > maximumSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(solutions(), crowdingDistanceComparator) ;
      solutions().remove(worst);
    }
  }

  @Override
  public Comparator<S> comparator() {
    return crowdingDistanceComparator ;
  }

  @Override
  public void computeDensityEstimator() {
    crowdingDistance.compute(solutions());
  }
}
