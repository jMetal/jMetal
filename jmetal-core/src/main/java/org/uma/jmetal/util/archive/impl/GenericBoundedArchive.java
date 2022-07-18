package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;

/**
 * This class implements a generic bound archive.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class GenericBoundedArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> comparator;
  private DensityEstimator<S> densityEstimator ;

  public GenericBoundedArchive(int maxSize, @NotNull DensityEstimator<S> densityEstimator) {
    super(maxSize);
    this.densityEstimator = densityEstimator ;
    comparator = densityEstimator.getComparator() ;
  }

  @Override
  public void prune() {
    if (getSolutionList().size() > getMaxSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), comparator) ;
      getSolutionList().remove(worst);
    }
  }

  @Override
  public Comparator<S> getComparator() {
    return comparator ;
  }

  @Override
  public void computeDensityEstimator() {
    densityEstimator.compute(getSolutionList());
  }
}
