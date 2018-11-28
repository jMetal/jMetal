package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between two {@link Solution} objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionsInObjectiveSpace<S extends Solution<?>>
    implements Distance<S, S> {

  @Override
  public double getDistance(S solution1, S solution2) {
    double diff;
    double distance = 0.0;

    for (int nObj = 0; nObj < solution1.getNumberOfObjectives();nObj++){
      diff = solution1.getObjective(nObj) - solution2.getObjective(nObj);
      distance += Math.pow(diff,2.0);
    }

    return Math.sqrt(distance);
  }
}
