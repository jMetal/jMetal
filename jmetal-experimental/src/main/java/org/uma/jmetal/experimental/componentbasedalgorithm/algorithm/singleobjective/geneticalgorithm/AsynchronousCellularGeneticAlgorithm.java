package org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.impl.SingleSolutionReplacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.impl.NeighborhoodMatingPoolSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerBoundedSequenceGenerator;
import org.uma.jmetal.util.termination.Termination;

import java.util.HashMap;

/**
 * Class representing genetic algorithms which are implemented using the {@link
 * ComponentBasedEvolutionaryAlgorithm} class.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class AsynchronousCellularGeneticAlgorithm<S extends Solution<?>>
    extends ComponentBasedEvolutionaryAlgorithm<S> {

  protected SequenceGenerator<Integer> solutionIndexGenerator;

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
  public AsynchronousCellularGeneticAlgorithm(
      Evaluation<S> evaluation,
      SolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      CrossoverAndMutationVariation<S> variation,
      Replacement<S> replacement) {
    super(
        "Asynchronous Cellular Genetic algorithm",
        evaluation,
        initialPopulationCreation,
        termination,
        selection,
        variation,
        replacement);
  }

  /** Constructor */
  public AsynchronousCellularGeneticAlgorithm(
      Problem<S> problem,
      int populationSize,
      Neighborhood<S> neighborhood,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      Termination termination) {
    this.name = "Asynchronous Cellular Genetic algorithm";
    this.problem = problem;
    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();
    this.solutionIndexGenerator = new IntegerBoundedSequenceGenerator(populationSize);

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new SingleSolutionReplacement<>(solutionIndexGenerator, new ObjectiveComparator<>(0));

    this.variation = new CrossoverAndMutationVariation<>(1, crossoverOperator, mutationOperator);

    this.selection =
        new NeighborhoodMatingPoolSelection<>(
            variation.getMatingPoolSize(),
            solutionIndexGenerator,
            neighborhood,
            new NaryTournamentSelection<>(2, new ObjectiveComparator<>(0)),
            false);

    this.termination = termination;

    this.evaluation = new SequentialEvaluation<>(problem);

    this.archive = null;
  }
}
