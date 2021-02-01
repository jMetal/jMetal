package org.uma.jmetal.util.densityestimator.impl;

import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.front.impl.ArrayFront;

import java.util.Comparator;
import java.util.List;

/**
 * This class implements a density estimator based on the hypervolume contribution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class HypervolumeContributionDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private String attributeId = getClass().getName();
  private Hypervolume<S> hypervolume ;

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
  public Double getValue(S solution) {
    Check.notNull(solution);

    Double result = 0.0 ;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId) ;
    }
    return result ;
  }

  @Override
  public Comparator<S> getComparator() {
    return Comparator.comparing(this::getValue) ;
  }
}

