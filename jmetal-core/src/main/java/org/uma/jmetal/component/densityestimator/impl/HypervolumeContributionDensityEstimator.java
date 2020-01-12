package org.uma.jmetal.component.densityestimator.impl;

import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements a density estimator based on the hypervolume contribution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class HypervolumeContributionDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator ;
  private List<S> referenceFront ;
  private Hypervolume<S> hypervolume = new PISAHypervolume<>();

  public HypervolumeContributionDensityEstimator(List<S> referenceFront) {
    solutionComparator = new DoubleValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING) ;
    this.referenceFront = referenceFront ;
  }
  /**
   * Assigns the hv contribution to all population in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   */

  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    hypervolume.computeHypervolumeContribution(solutionList, referenceFront) ;
  }

  @Override
  public String getAttributeId() {
    return attributeId ;
  }

  @Override
  public Comparator<S> getSolutionComparator() {
    return solutionComparator ;
  }

  @Override
  public List<S> sort(List<S> solutionList) {
    Collections.sort(solutionList, getSolutionComparator());

    return solutionList ;
  }
}

