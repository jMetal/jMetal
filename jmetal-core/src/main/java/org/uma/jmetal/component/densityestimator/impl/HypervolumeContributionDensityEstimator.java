package org.uma.jmetal.component.densityestimator.impl;

import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;
import org.uma.jmetal.util.front.impl.ArrayFront;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements a density estimator based on the hypervolume contribution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class HypervolumeContributionDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator ;
  private Hypervolume<S> hypervolume ;

  public HypervolumeContributionDensityEstimator(List<S> referenceFront) {
    solutionComparator = new DoubleValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING) ;
    hypervolume = new PISAHypervolume<>(new ArrayFront(referenceFront)) ;
  }

  public HypervolumeContributionDensityEstimator(double[] referencePoint) {
    solutionComparator = new DoubleValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING) ;
    hypervolume = new PISAHypervolume<>(referencePoint) ;
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

    hypervolume.computeHypervolumeContribution(solutionList) ;
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

