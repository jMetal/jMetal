package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between two {@link DoubleSolution} objects in solution space.
 *
 * @author
 */
public class EuclideanDistanceBetweenSolutionsInSolutionSpace<S extends Solution<Double>>
    implements Distance<S, S> {

  private final EuclideanDistanceBetweenVectors distance = new EuclideanDistanceBetweenVectors() ;

  @Override
  public double compute(S solution1, S solution2) {
    double[] vector1;
    double[] vector2;
    vector1 = solution1.variables().stream().mapToDouble(value -> value).toArray();
    vector2 = solution2.variables().stream().mapToDouble(value -> value).toArray();
    return distance.compute(vector1, vector2) ;
  }
}
