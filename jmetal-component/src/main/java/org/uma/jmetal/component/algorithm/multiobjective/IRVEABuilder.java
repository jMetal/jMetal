package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/** Builds iRVEA (Liu et al., CEC 2019), including regional protection and an epsilon archive. */
public class IRVEABuilder<S extends Solution<?>> extends RVEABuilder<S> {
  public IRVEABuilder(
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

  public IRVEABuilder(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      double alpha,
      double fr,
      List<double[]> vectors) {
    super(
        Variant.IRVEA,
        problem,
        populationSize,
        maxEvaluations,
        crossover,
        mutation,
        alpha,
        fr,
        vectors);
  }

  /** Sets the upper target for coarse regions (default 40); the simplex lattice may use fewer. */
  public IRVEABuilder<S> setNumberOfSubregions(int numberOfSubregions) {
    Check.that(numberOfSubregions > 0, "The number of subregions must be positive");
    this.numberOfSubregions = numberOfSubregions;
    return this;
  }
}
