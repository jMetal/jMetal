package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * Constraint violation (CV) of a solution as C-TAEA uses it: a non-negative value that is zero
 * if and only if the solution is feasible.
 *
 * <p>jMetal considers a constraint satisfied when its value is not negative, and its overall
 * constraint violation degree is the sum of the negative values; the CV is its absolute value,
 * which is the {@code sum(max(0, con))} of PlatEMO, whose constraints are satisfied when they are
 * not positive.
 */
public final class ConstraintViolation {
  private ConstraintViolation() {}

  /**
   * Returns the constraint violation of a solution.
   *
   * @param solution the solution
   * @return the absolute value of its overall constraint violation degree
   */
  public static double of(Solution<?> solution) {
    return Math.abs(ConstraintHandling.overallConstraintViolationDegree(solution));
  }

  /**
   * Returns whether a solution is feasible, that is, whether its constraint violation is zero.
   *
   * @param solution the solution
   * @return true if its constraint violation is zero
   */
  public static boolean isFeasible(Solution<?> solution) {
    return of(solution) == 0.0;
  }
}
