package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;

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
    if (solutions().size() > maximumSize()) {
      computeDensityEstimator() ;
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
    hypervolume.computeHypervolumeContribution(archive.solutions(), archive.solutions()) ;
  }
}
