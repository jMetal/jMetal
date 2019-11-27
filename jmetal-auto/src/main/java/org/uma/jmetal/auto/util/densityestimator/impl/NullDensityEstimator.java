package org.uma.jmetal.auto.util.densityestimator.impl;

import org.uma.jmetal.auto.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.auto.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.solution.Solution;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NullDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator ;

  public NullDensityEstimator() {
    solutionComparator = new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING) ;
  }
  /**
   * Assigns crowding distances to all population in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   */

  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    solutionList.forEach(solution -> solution.setAttribute(attributeId, 0));
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

