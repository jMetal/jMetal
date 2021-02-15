package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
@Deprecated
public class CrowdingDistance<S extends Solution<?>>
    extends GenericSolutionAttribute<S, Double> implements DensityEstimator<S>{

  /**
   * Assigns crowding distances to all solutions in a <code>SolutionSet</code>.
   *
   * @param solutionList The <code>SolutionSet</code>.
   * @throws JMetalException
   */

  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
      return;
    }

    if (size == 2) {
      solutionList.get(0).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
      solutionList.get(1).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);

      return;
    }

    // Use a new SolutionSet to avoid altering the original solutionSet
    List<S> front = new ArrayList<>(size);
    for (S solution : solutionList) {
      front.add(solution);
    }

    for (int i = 0; i < size; i++) {
      front.get(i).attributes().put(getAttributeIdentifier(), 0.0);
    }

    double objetiveMaxn;
    double objetiveMinn;
    double distance;

    int numberOfObjectives = solutionList.get(0).objectives().length ;

    for (int i = 0; i < numberOfObjectives; i++) {
      // Sort the population by Obj n
      front.sort(new ObjectiveComparator<S>(i));
      objetiveMinn = front.get(0).getObjective(i);
      objetiveMaxn = front.get(front.size() - 1).getObjective(i);

      // Set de crowding distance
      front.get(0).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);
      front.get(size - 1).attributes().put(getAttributeIdentifier(), Double.POSITIVE_INFINITY);

      for (int j = 1; j < size - 1; j++) {
        distance = front.get(j + 1).getObjective(i) - front.get(j - 1).getObjective(i);
        distance = distance / (objetiveMaxn - objetiveMinn);
        distance += (double)front.get(j).getAttribute(getAttributeIdentifier());
        front.get(j).attributes().put(getAttributeIdentifier(), distance);
      }
    }
  }

  @Override
  public Object getAttributeIdentifier() {
    return this.getClass() ;
  }
}

