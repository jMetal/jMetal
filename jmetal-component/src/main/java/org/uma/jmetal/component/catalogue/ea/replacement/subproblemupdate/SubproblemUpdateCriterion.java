package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 * Criterion used by MOEA/D-like replacement components to decide whether a new solution must
 * replace the current solution of a subproblem. The default criterion in unconstrained MOEA/D is
 * to compare the aggregation function values, but constrained variants (e.g., feasibility rules,
 * epsilon constraint handling, or violation thresholds) combine those values with constraint
 * violation information.
 *
 * @param <S> Solution type
 */
public interface SubproblemUpdateCriterion<S extends Solution<?>> {

  /**
   * Decides whether the new solution must replace the current solution of a subproblem.
   *
   * @param newSolution the newly created solution
   * @param newSolutionAggregationValue aggregation function value of the new solution
   * @param currentSolution the solution currently assigned to the subproblem
   * @param currentSolutionAggregationValue aggregation function value of the current solution
   * @return true if the new solution must replace the current one
   */
  boolean replaces(
      S newSolution,
      double newSolutionAggregationValue,
      S currentSolution,
      double currentSolutionAggregationValue);

  /**
   * Hook invoked once per replacement step (i.e., once per new evaluated solution), before any
   * {@link #replaces} call, so that stateful criteria can update their internal state (epsilon
   * levels, violation thresholds, feasibility ratios, etc.). Implementations can detect generation
   * boundaries by counting invocations, since a generation amounts to {@code population.size()}
   * replacement steps in the steady-state MOEA/D flow.
   *
   * @param population the current population
   * @param newSolution the newly created solution
   */
  default void update(List<S> population, S newSolution) {
    // Stateless criteria do not need to update anything
  }
}
