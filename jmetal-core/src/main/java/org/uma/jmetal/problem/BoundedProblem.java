package org.uma.jmetal.problem;

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
public interface BoundedProblem<T extends Number, S> extends Problem<S> {
  /**
   * @param index
   *          index of the variable
   * @return lower bound of the variable
   */
  public T getLowerBound(int index);

  /**
   * @param index
   *          index of the variable
   * @return upper bound of the variable
   */
  public T getUpperBound(int index);
}
