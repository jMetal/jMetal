package org.uma.jmetal.util.distance.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.Distance;

import java.util.Arrays;

/**
 * Class for calculating the Euclidean distance between two {@link DoubleSolution} objects in solution space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionsInSolutionSpace<S extends Solution<Double>>
    implements Distance<S, S> {

  private final EuclideanDistanceBetweenVectors distance = new EuclideanDistanceBetweenVectors() ;

  @Override
  public double compute(@NotNull S solution1, @NotNull S solution2) {
      var result = new double[10];
      var count1 = 0;
    for (var aDouble : solution1.variables()) {
      double value1 = aDouble;
      if (result.length == count1) result = Arrays.copyOf(result, count1 * 2);
      result[count1++] = value1;
    }
    result = Arrays.copyOfRange(result, 0, count1);
      var vector1 = result;
      var arr = new double[10];
      var count = 0;
    for (var value : solution2.variables()) {
      double v = value;
      if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
      arr[count++] = v;
    }
    arr = Arrays.copyOfRange(arr, 0, count);
      var vector2 = arr;
    return distance.compute(vector1, vector2) ;
  }
}
