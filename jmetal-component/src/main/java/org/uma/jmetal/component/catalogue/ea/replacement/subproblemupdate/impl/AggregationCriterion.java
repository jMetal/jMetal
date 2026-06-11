package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl;

import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.SubproblemUpdateCriterion;
import org.uma.jmetal.solution.Solution;

/**
 * Default subproblem update criterion of unconstrained MOEA/D: the new solution replaces the
 * current one if it has a better (lower) aggregation function value.
 *
 * @param <S> Solution type
 */
public class AggregationCriterion<S extends Solution<?>> implements SubproblemUpdateCriterion<S> {

  @Override
  public boolean replaces(
      S newSolution,
      double newSolutionAggregationValue,
      S currentSolution,
      double currentSolutionAggregationValue) {
    return newSolutionAggregationValue < currentSolutionAggregationValue;
  }
}
