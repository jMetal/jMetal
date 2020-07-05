package org.uma.jmetal.solution.doublesolution;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.metadata.Metadata;

/**
 * Interface representing a double solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleSolution extends Solution<Double> {
  Double getLowerBound(int index) ;
  Double getUpperBound(int index) ;
  
  public static Metadata<DoubleSolution, List<Double>> lowerBoundsMetadata() {
    return solution -> IntStream.range(0, solution.getNumberOfVariables())//
        .mapToObj(i -> solution.getLowerBound(i))//
        .collect(Collectors.toList());
  }

  public static Metadata<DoubleSolution, List<Double>> upperBoundsMetadata() {
    return solution -> IntStream.range(0, solution.getNumberOfVariables())//
        .mapToObj(i -> solution.getUpperBound(i))//
        .collect(Collectors.toList());
  }
}
