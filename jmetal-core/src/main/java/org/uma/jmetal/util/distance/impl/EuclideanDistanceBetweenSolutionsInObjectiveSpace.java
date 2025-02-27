package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between two {@link Solution} objects in objective space.
 *
 * @author
 */
public class EuclideanDistanceBetweenSolutionsInObjectiveSpace<S extends Solution<?>>
    implements Distance<S, S> {

  private final EuclideanDistanceBetweenVectors distance = new EuclideanDistanceBetweenVectors() ;

  @Override
  public double compute(S solution1, S solution2) {
    return distance.compute(solution1.objectives(), solution2.objectives()) ;
  }
}
