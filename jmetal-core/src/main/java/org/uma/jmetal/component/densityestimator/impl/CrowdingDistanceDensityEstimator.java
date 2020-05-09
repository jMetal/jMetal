package org.uma.jmetal.component.densityestimator.impl;

import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
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
    List<S> front = new ArrayList<>(solutionList);

    for (int i = 0; i < size; i++) {
      front.get(i).setAttribute(attributeId, 0.0);
    }

    int numberOfObjectives = solutionList.get(0).getNumberOfObjectives() ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      front.sort(new ObjectiveComparator<>(i));

      // It may be beneficial to change this according to https://dl.acm.org/citation.cfm?doid=2463372.2463456.
      // The additional change that may be beneficial is that if we have only two distinct objective values,
      //   we also don't update the crowding distance, as they all will "go to eleven",
      //   which makes no sense as this objective just appears to be non-discriminating.

      double minObjective = front.get(0).getObjective(i);
      double maxObjective = front.get(front.size() - 1).getObjective(i);
      if (minObjective == maxObjective) {
        continue; // otherwise all crowding distances will be NaN = 0.0 / 0.0 except for two
      }

      // Set the crowding distance for the extreme points
      front.get(0).setAttribute(attributeId, Double.POSITIVE_INFINITY);
      front.get(size - 1).setAttribute(attributeId, Double.POSITIVE_INFINITY);

      // Increase the crowding distances for all the intermediate points
      for (int j = 1; j < size - 1; j++) {
        double distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (maxObjective - minObjective);
        distance += (double) front.get(j).getAttribute(attributeId);
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
    solutionList.sort(getSolutionComparator());
    return solutionList ;
  }
}
