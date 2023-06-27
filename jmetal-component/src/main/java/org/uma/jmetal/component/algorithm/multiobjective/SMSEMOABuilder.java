package org.uma.jmetal.component.algorithm.multiobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Class to configure and build an instance of the SMS-EMOA algorithm
 *
 * @param <S>
 */
public class SMSEMOABuilder<S extends Solution<?>> {
  private String name;
  private Ranking<S> ranking;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public SMSEMOABuilder(Problem<S> problem, int populationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    name = "SMS-EMOA";

    ranking = new FastNonDominatedSortRanking<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    Hypervolume<S> hypervolume = new PISAHypervolume<>();
    this.replacement = new SMSEMOAReplacement<>(ranking, hypervolume);

    this.variation =
        new CrossoverAndMutationVariation<>(1, crossover, mutation);

    this.selection = new RandomSelection<>(variation.getMatingPoolSize());

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public SMSEMOABuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public SMSEMOABuilder<S> setRanking(Ranking<S> ranking) {
    this.ranking = ranking;
    Hypervolume<S> hypervolume = new PISAHypervolume<>();
    this.replacement = new SMSEMOAReplacement<>(ranking, hypervolume);

    return this;
  }

  public SMSEMOABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
