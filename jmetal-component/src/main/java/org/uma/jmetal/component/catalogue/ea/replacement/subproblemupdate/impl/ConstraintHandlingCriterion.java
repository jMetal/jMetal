package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl;

import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleSupplier;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.SubproblemUpdateCriterion;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * A subproblem update criterion of MOEA/D with a pairwise constraint-handling technique: the
 * technique decides by the constraints of the new and the current solution of a subproblem and,
 * when they do not decide, by their values of the aggregation function, so the new solution
 * replaces the current one if the technique prefers it. The technique is updated with the
 * population and the progress of the run once every {@code population.size()} new solutions,
 * that is, once per generation of MOEA/D.
 *
 * <p>With {@link org.uma.jmetal.util.constrainthandling.ConstrainedDominance} it decides as {@link
 * FeasibilityRulesCriterion}.
 *
 * @param <S> the type of the solutions
 */
public class ConstraintHandlingCriterion<S extends Solution<?>>
    implements SubproblemUpdateCriterion<S> {
  private final PairwiseConstraintHandlingTechnique<S> technique;
  private final DoubleSupplier progress;
  private long updates;

  /**
   * Creates the criterion.
   *
   * @param technique the constraint-handling technique
   * @param progress the progress of the run
   */
  public ConstraintHandlingCriterion(
      PairwiseConstraintHandlingTechnique<S> technique, DoubleSupplier progress) {
    Check.notNull(technique);
    Check.notNull(progress);
    this.technique = technique;
    this.progress = progress;
  }

  @Override
  public boolean replaces(
      S newSolution,
      double newSolutionAggregationValue,
      S currentSolution,
      double currentSolutionAggregationValue) {
    Comparator<S> byAggregationValue =
        (solution1, solution2) ->
            solution1 == newSolution
                ? Double.compare(newSolutionAggregationValue, currentSolutionAggregationValue)
                : Double.compare(currentSolutionAggregationValue, newSolutionAggregationValue);
    return technique.compare(newSolution, currentSolution, byAggregationValue) < 0;
  }

  @Override
  public void update(List<S> population, S newSolution) {
    if (updates % population.size() == 0) {
      technique.update(population, progress.getAsDouble());
    }
    updates++;
  }

  /** The constraint-handling technique. */
  public PairwiseConstraintHandlingTechnique<S> technique() {
    return technique;
  }
}
