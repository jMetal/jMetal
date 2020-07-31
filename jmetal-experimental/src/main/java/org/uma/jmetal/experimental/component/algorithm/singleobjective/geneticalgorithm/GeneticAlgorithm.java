package org.uma.jmetal.experimental.component.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.experimental.component.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.experimental.component.catalogue.evaluation.Evaluation;
import org.uma.jmetal.experimental.component.catalogue.evaluation.SequentialEvaluation;
import org.uma.jmetal.experimental.component.catalogue.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.experimental.component.catalogue.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.component.catalogue.replacement.Replacement;
import org.uma.jmetal.experimental.component.catalogue.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.experimental.component.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.component.catalogue.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.experimental.component.catalogue.termination.Termination;
import org.uma.jmetal.experimental.component.catalogue.variation.impl.CrossoverAndMutationVariation;
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
      InitialSolutionsCreation<S> initialPopulationCreation,
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
}
