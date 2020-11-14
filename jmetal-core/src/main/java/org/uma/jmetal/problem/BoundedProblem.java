package org.uma.jmetal.problem;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.bounds.Bounds;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link BoundedProblem} is a {@link Problem} for which solution boundaries
 * exist. Boundaries restrict each variable to be within an interval. This
 * interval may be different for each variable of the solution.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <T>
 *          Type of boundary, typically {@link Integer} or {@link Double}
 * @param <S>
 *          Type of {@link Problem} solutions
 */
public interface BoundedProblem<T extends Number & Comparable<T>, S> extends Problem<S> {
  /**
   * @param index
   *          index of the variable
   * @return lower bound of the variable
   * @deprecated Use {@link #getBoundsForVariables()}.{@link List#get(int) get(int)}.{@link Bounds#getLowerBound() getLowerBound()} instead.
   */
  @Deprecated
  T getLowerBound(int index);

  /**
   * @param index
   *          index of the variable
   * @return upper bound of the variable
   * @deprecated Use {@link #getBoundsForVariables()}.{@link List#get(int) get(int)}.{@link Bounds#getUpperBound() getUpperBound()} instead.
   */
  @Deprecated
  T getUpperBound(int index);

  /**
   * @return A list with pairs <lower bound, upper bound> for each of the decision variables
   * @deprecated Use {@link #getBoundsForVariables()} instead.
   */
  @Deprecated
  List<Pair<T, T>> getBounds() ;

  /**
   * @return A list with {@link Bounds} <lower bound, upper bound> for each of the decision variables
   */
  default List<Bounds<T>> getBoundsForVariables() {
    return getBounds().stream().map(Bounds::fromPair).collect(Collectors.toList());
  }
}
