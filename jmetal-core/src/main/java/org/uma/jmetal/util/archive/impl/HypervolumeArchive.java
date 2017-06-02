package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
@SuppressWarnings("serial")
public class HypervolumeArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> comparator;
  Hypervolume<S> hypervolume ;

  public HypervolumeArchive(int maxSize, Hypervolume<S> hypervolume) {
    super(maxSize);
    comparator = new HypervolumeContributionComparator<S>() ;
    this.hypervolume = hypervolume ;
  }

  @Override
  public void prune() {
    if (getSolutionList().size() > getMaxSize()) {
      computeDensityEstimator() ;
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
    hypervolume.computeHypervolumeContribution(archive.getSolutionList(), archive.getSolutionList()) ;
  }

  @Override
  public void sortByDensityEstimator() {
    Collections.sort(getSolutionList(), new HypervolumeContributionComparator<S>());
  }
}
