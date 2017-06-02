package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between two {@link DoubleSolution} objects in solution space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionsInSolutionSpace<S extends Solution<Double>>
    implements Distance<S, S> {

  @Override
  public double getDistance(S solution1, S solution2) {
    double distance = 0.0;

    double diff;
    for (int i = 0; i < solution1.getNumberOfVariables() ; i++){
      diff = solution1.getVariableValue(i) - solution2.getVariableValue(i);
      distance += Math.pow(diff,2.0);
    }

    return Math.sqrt(distance);
  }
}
