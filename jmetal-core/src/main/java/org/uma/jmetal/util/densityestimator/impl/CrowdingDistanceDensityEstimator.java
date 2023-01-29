package org.uma.jmetal.util.densityestimator.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CrowdingDistanceDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private final String attributeId = getClass().getName();

  /**
   * Assigns crowding distances to all population in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   */
  @Override
  public void compute(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionList.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      solutionList.get(1).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      return;
    }

    // Use a new SolutionSet to avoid altering the original solutionSet
    List<S> front = new ArrayList<>(solutionList);

    for (int i = 0; i < size; i++) {
      front.get(i).attributes().put(attributeId, 0.0);
    }

    int numberOfObjectives = solutionList.get(0).objectives().length ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      front.sort(new ObjectiveComparator<>(i));

      // It may be beneficial to change this according to https://dl.acm.org/citation.cfm?doid=2463372.2463456.
      // The additional change that may be beneficial is that if we have only two distinct objective values,
      //   we also don't update the crowding distance, as they all will "go to eleven",
      //   which makes no sense as this objective just appears to be non-discriminating.

      double minObjective = front.get(0).objectives()[i];
      double maxObjective = front.get(front.size() - 1).objectives()[i];
      if (minObjective == maxObjective) {
        continue; // otherwise all crowding distances will be NaN = 0.0 / 0.0 except for two
      }

      // Set the crowding distance for the extreme points
      front.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      front.get(size - 1).attributes().put(attributeId, Double.POSITIVE_INFINITY);

      // Increase the crowding distances for all the intermediate points
      for (int j = 1; j < size - 1; j++) {
        double distance = front.get(j + 1).objectives()[i] - front.get(j - 1).objectives()[i];
        distance = distance / (maxObjective - minObjective);
        distance += (double) front.get(j).attributes().get(attributeId);
        front.get(j).attributes().put(attributeId, distance);
      }
    }
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);

    Double result = 0.0 ;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId) ;
    }
    return result ;
  }

  @Override
  public Comparator<S> getComparator() {
    return Comparator.comparing(this::value).reversed() ;
  }
}
