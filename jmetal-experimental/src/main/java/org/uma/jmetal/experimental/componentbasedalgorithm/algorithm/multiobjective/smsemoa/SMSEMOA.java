package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicatorold.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicatorold.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
@SuppressWarnings("serial")
public class SMSEMOA<S extends Solution<?>> extends NSGAII<S> {

  /** Constructor */
  public SMSEMOA(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination,
      Hypervolume<S> hypervolume,
      Ranking<S> ranking) {
    super(problem, populationSize, 1, crossoverOperator, mutationOperator, termination, ranking);

    this.replacement = new SMSEMOAReplacement<>(ranking, hypervolume);

    this.selection = new RandomMatingPoolSelection<>(variation.getMatingPoolSize());
  }

  /** Constructor */
  public SMSEMOA(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination) {
    this(
        problem,
        populationSize,
        crossoverOperator,
        mutationOperator,
        termination,
        new PISAHypervolume<>(),
        new FastNonDominatedSortRanking<>());
  }

  @Override
  public String getName() {
    return "SMS-EMOA";
  }

  @Override
  public String getDescription() {
    return "SMS-EMOA";
  }
}
