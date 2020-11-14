package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.termination.Termination;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import java.util.HashMap;

/**
 * Class representing genetic algorithms which are implemented using the {@link ComponentBasedEvolutionaryAlgorithm}
 * class.
 *
 * @author Antonio J. Nebro
 * */
@SuppressWarnings("serial")
public class GeneticAlgorithm<S extends Solution<?>> extends ComponentBasedEvolutionaryAlgorithm<S> {

  /**
   * Constructor
   *
   * @param evaluation
   * @param initialPopulationCreation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   */
  public GeneticAlgorithm (
      Evaluation<S> evaluation,
      SolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      CrossoverAndMutationVariation<S> variation,
      Replacement<S> replacement) {
    super(
        "Genetic algorithm",
        evaluation,
        initialPopulationCreation,
        termination,
        selection,
        variation,
        replacement);
  }

  /** Constructor */
  public GeneticAlgorithm(
      Problem<S> problem,
      int populationSize,
      int offspringPopulationSize,
      NaryTournamentSelection<S> selectionOperator,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination) {
    this.name = "Genetic algorithm";
    this.problem = problem;
    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0)) ;

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossoverOperator, mutationOperator);

    this.selection = new NaryTournamentMatingPoolSelection<>(selectionOperator, variation.getMatingPoolSize()) ;

    this.termination = termination;

    this.evaluation = new SequentialEvaluation<>(problem);

    this.archive = null;
  }

  @Override
  protected void updateProgress() {
    S bestFitnessSolution = population.stream().min(new ObjectiveComparator<>(0)).get() ;
    attributes.put("BEST_SOLUTION", bestFitnessSolution);

    super.updateProgress();
  }

}
