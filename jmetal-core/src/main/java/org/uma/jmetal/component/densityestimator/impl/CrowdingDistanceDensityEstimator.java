package org.uma.jmetal.component.densityestimator.impl;

import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class CrowdingDistanceDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator ;

  public CrowdingDistanceDensityEstimator() {
    solutionComparator = new DoubleValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING) ;
  }
  /**
   * Assigns crowding distances to all population in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   */

  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).setAttribute(attributeId, Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionList.get(0).setAttribute(attributeId, Double.POSITIVE_INFINITY);
      solutionList.get(1).setAttribute(attributeId, Double.POSITIVE_INFINITY);

      return;
    }

    // Use a new SolutionSet to avoid altering the original solutionSet
    List<S> front = new ArrayList<>(size);
    for (S solution : solutionList) {
      front.add(solution);
    }

    for (int i = 0; i < size; i++) {
      front.get(i).setAttribute(attributeId, 0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      Collections.sort(front, new ObjectiveComparator<S>(i)) ;
      objetiveMinn = front.get(0).getObjective(i);
      objetiveMaxn = front.get(front.size() - 1).getObjective(i);

      // Set de crowding distance
      front.get(0).setAttribute(attributeId, Double.POSITIVE_INFINITY);
      front.get(size - 1).setAttribute(attributeId, Double.POSITIVE_INFINITY);

      for (int j = 1; j < size - 1; j++) {
        distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (objetiveMaxn - objetiveMinn);
        distance += (double)front.get(j).getAttribute(attributeId);
        front.get(j).setAttribute(attributeId, distance);
      }
    }
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

