package org.uma.jmetal.util.densityestimator.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

/**
 * This class implements a density estimator based on the hypervolume contribution
 *
 * @author Antonio J. Nebro
 */
public class HypervolumeContributionDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private final HypervolumeContributionAttribute<S> contributionAttribute =
      new HypervolumeContributionAttribute<>();
  private final Hypervolume<S> hypervolume ;

  public HypervolumeContributionDensityEstimator(List<S> referenceFront) {
    hypervolume = new PISAHypervolume<>(new ArrayFront(referenceFront)) ;
  }

  public HypervolumeContributionDensityEstimator(double[] referencePoint) {
    hypervolume = new PISAHypervolume<>(referencePoint) ;
  }

  /**
   * Assigns the hv contribution to all population in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   */

  @Override
  public void compute(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    hypervolume.computeHypervolumeContribution(solutionList) ;
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);

    Double contribution = contributionAttribute.getAttribute(solution) ;
    return contribution != null ? contribution : 0.0 ;
  }

  @Override
  public Comparator<S> comparator() {
    return Comparator.comparing(this::value) ;
  }
}

