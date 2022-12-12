package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.KnnDensityEstimator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 * Modified by Juanjo on 07/04/2015
 */
@SuppressWarnings("serial")
public class KNNDistanceArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> knnDistanceComparator;
  private DensityEstimator<S> knnDensityEstimator ;

  public KNNDistanceArchive(int maxSize, int k) {
    super(maxSize);
    knnDensityEstimator = new KnnDensityEstimator<S>(k);
    knnDistanceComparator = Comparator.comparing(knnDensityEstimator::getValue).reversed() ;
  }

  @Override
  public void prune() {
    if (solutions().size() > maximumSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(solutions(), knnDistanceComparator) ;
      solutions().remove(worst);
    }
  }

  @Override
  public Comparator<S> comparator() {
    return knnDistanceComparator ;
  }

  @Override
  public void computeDensityEstimator() {
    knnDensityEstimator.compute(solutions());
  }
}
