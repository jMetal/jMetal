package org.uma.jmetal.component.algorithm.multiobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RVEAReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Class to configure and build an instance of the RVEA (Reference Vector Guided Evolutionary Algorithm)
 * using the component-based architecture.
 *
 * @param <S>
 */
public class RVEABuilder<S extends Solution<?>> {
  private String name;
  private Problem<S> problem;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public RVEABuilder(Problem<S> problem, int populationSize, int maxEvaluations,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, double alpha, double fr, int h) {
    this.name = "RVEA";
    this.problem = problem;
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    int maxGenerations = maxEvaluations / populationSize; // estimation of maximum generations
    
    RVEAEnvironmentalSelection<S> environmentalSelection =
        new RVEAEnvironmentalSelection<>(problem.numberOfObjectives(), maxGenerations, alpha, fr, h);

    this.replacement = new RVEAReplacement<>(environmentalSelection);

    this.variation = new CrossoverAndMutationVariation<>(
        populationSize, crossover, mutation);

    this.selection = new RandomSelection<>(variation.matingPoolSize());

    this.termination = new TerminationByEvaluations(maxEvaluations);
    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public RVEABuilder<S> setTermination(Termination termination) {
    this.termination = termination;
    return this;
  }

  public RVEABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;
    return this;
  }

  public RVEABuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    this.createInitialPopulation = solutionsCreation;
    return this;
  }

  public RVEABuilder<S> setSelection(Selection<S> selection) {
    this.selection = selection;
    return this;
  }

  public RVEABuilder<S> setVariation(Variation<S> variation) {
    this.variation = variation;
    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
