package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl;

import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.SubproblemUpdateCriterion;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * Constraint handling criterion based on the feasibility rules of the constrained dominance
 * principle (K. Deb, "An efficient constraint handling method for genetic algorithms", 2000)
 * adapted to decomposition-based subproblem update:
 *
 * <ul>
 *   <li>A feasible solution beats an infeasible one.
 *   <li>Between two infeasible solutions, the one with the lowest overall constraint violation
 *       degree wins.
 *   <li>Between two feasible solutions (or solutions with the same violation degree), the one with
 *       the best aggregation function value wins.
 * </ul>
 *
 * @param <S> Solution type
 */
public class FeasibilityRulesCriterion<S extends Solution<?>>
    implements SubproblemUpdateCriterion<S> {

  @Override
  public boolean replaces(
      S newSolution,
      double newSolutionAggregationValue,
      S currentSolution,
      double currentSolutionAggregationValue) {
    double newViolation =
        Math.abs(ConstraintHandling.overallConstraintViolationDegree(newSolution));
    double currentViolation =
        Math.abs(ConstraintHandling.overallConstraintViolationDegree(currentSolution));

    if (newViolation == currentViolation) {
      return newSolutionAggregationValue < currentSolutionAggregationValue;
    }
    return newViolation < currentViolation;
  }
}
