package org.uma.jmetal.util.evaluator;

import java.util.Arrays;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Checks the solutions that a problem has just evaluated, so that the evaluators reject a NaN as
 * soon as a problem returns it.
 *
 * <p>A NaN has no order. Pareto dominance ignores a NaN objective, so a solution whose objectives
 * are all NaN is never dominated, and a NaN constraint counts as satisfied. The crowding distance of
 * a NaN solution is also NaN, which the sorts rank above an infinite distance. Algorithms therefore
 * keep NaN solutions: they fill the population of NSGA-II, SMS-EMOA or AGE-MOEA, win the subproblems
 * of MOEA/D with Tchebycheff aggregation, and make NSGA-III fail later with an unrelated exception.
 * A NaN is almost always a defect of the problem, so it is reported where it appears.
 *
 * <p>Infinite values are accepted: they have an order, and a problem may use them on purpose.
 */
public final class EvaluationCheck {

  private EvaluationCheck() {}

  /**
   * Checks that none of the objectives and constraints of an evaluated solution, nor its precomputed
   * overall constraint violation degree (if any), is NaN.
   *
   * @param solution the solution, already evaluated; anything else than a {@link Solution} is not
   *     checked
   * @param problem the problem that evaluated it, named in the message of the exception
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if a value is NaN
   */
  public static void noNaNValues(Object solution, Problem<?> problem) {
    if (solution instanceof Solution<?> evaluated) {
      Check.that(
          Arrays.stream(evaluated.objectives()).noneMatch(Double::isNaN)
              && Arrays.stream(evaluated.constraints()).noneMatch(Double::isNaN)
              && !Double.isNaN(ConstraintHandling.overallConstraintViolationDegree(evaluated)),
          () ->
              "Problem "
                  + problem.name()
                  + " evaluated a solution to NaN. "
                  + evaluated);
    }
  }
}
