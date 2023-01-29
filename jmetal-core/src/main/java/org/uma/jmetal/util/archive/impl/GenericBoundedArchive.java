package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;

/**
 * This class implements a generic bound archive.
 *
 * @author Antonio J. Nebro
 */
public class GenericBoundedArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> comparator;
  private DensityEstimator<S> densityEstimator ;

  public GenericBoundedArchive(int maxSize, DensityEstimator<S> densityEstimator) {
    super(maxSize);
    this.densityEstimator = densityEstimator ;
    comparator = densityEstimator.comparator() ;
  }

  @Override
  public void prune() {
    if (solutions().size() > maximumSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(solutions(), comparator) ;
      solutions().remove(worst);
    }
  }

  @Override
  public Comparator<S> comparator() {
    return comparator ;
  }

  @Override
  public void computeDensityEstimator() {
    densityEstimator.compute(solutions());
  }
}
