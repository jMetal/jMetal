package org.uma.jmetal.solution.doublesolution;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.metadata.Metadata;

/**
 * Interface representing a double solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleSolution extends Solution<Double> {
  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Deprecated
  Double getLowerBound(int index) ;
  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Deprecated
  Double getUpperBound(int index) ;
  
  /**
   * It is often the case that we use both bounds together. Searching twice the
   * same index may be counter productive in this case. This methods allows to
   * offer this optimization, although its default implementation just delegates
   * to the separate methods.
   * 
   * @deprecated Use {@link Metadata} for additional attributes of {@link Solution} or variables.
   */
  @Deprecated
  default Bounds<Double> getBounds(int index) {
    DoubleSolution solution = this;
    Double lowerBound = solution.getLowerBound(index);
    Double upperBound = solution.getUpperBound(index);
    return Bounds.create(lowerBound, upperBound);
  }
  
  /**
   * {@link Metadata} implementation for {@link #getBounds(int)}.
   * 
   * @return the {@link Metadata}
   */
  public static Metadata<DoubleSolution, List<Bounds<Double>>> boundsMetadata() {
    return solution -> IntStream.range(0, solution.getNumberOfVariables())//
        .mapToObj(solution::getBounds)//
        .collect(toList());
  }
}
