package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/** Builds RVEA* (Cheng et al., 2016) with reference-vector regeneration for irregular fronts. */
public class RVEAStarBuilder<S extends Solution<?>> extends RVEABuilder<S> {
  public RVEAStarBuilder(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      double alpha,
      double fr,
      int divisions) {
    this(
        problem,
        populationSize,
        maxEvaluations,
        crossover,
        mutation,
        alpha,
        fr,
        generateVectors(problem, divisions));
  }

  public RVEAStarBuilder(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      double alpha,
      double fr,
      List<double[]> vectors) {
    super(
        Variant.RVEA_STAR,
        problem,
        populationSize,
        maxEvaluations,
        crossover,
        mutation,
        alpha,
        fr,
        vectors);
  }
}
