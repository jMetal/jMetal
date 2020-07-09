package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.ComponentBasedEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement ;
import org.uma.jmetal.component.replacement.impl.MOEADReplacement;
import org.uma.jmetal.component.selection.MatingPoolSelection ;
import org.uma.jmetal.component.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation ;
import org.uma.jmetal.component.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.archive.Archive ;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.observable.Observable ;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map ;

/**
 * This class is intended to provide an implementation of the MOEA/D-DE algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class MOEADDE extends ComponentBasedEvolutionaryAlgorithm<DoubleSolution> {

  /** Constructor */
  public MOEADDE(
      String name,
      Problem<DoubleSolution> problem,
      Observable<Map<String, Object>> observable,
      Map<String, Object> attributes,
      InitialSolutionsCreation<DoubleSolution> createInitialPopulation,
      Variation<DoubleSolution> variation,
      MatingPoolSelection<DoubleSolution> selection,
      Replacement<DoubleSolution> replacement,
      Termination termination,
      Evaluation<DoubleSolution> evaluation,
      Archive<DoubleSolution> archive) {
    
    this.name = name;
    this.problem = problem;
    this.observable = observable;
    this.attributes = attributes;
    this.createInitialPopulation = createInitialPopulation;
    this.variation = variation;
    this.selection = selection;
    this.replacement = replacement;
    this.termination = termination;
    this.evaluation = evaluation;
    this.archive = archive;
  }
  
  /**
   * Constructor
   * 
   * @deprecated Use instead
   *             {@link #create(Evaluation, InitialSolutionsCreation, Termination, PopulationAndNeighborhoodMatingPoolSelection, DifferentialCrossoverVariation, MOEADReplacement)}
   */
  @Deprecated
  public MOEADDE(
        Evaluation<DoubleSolution> evaluation,
        InitialSolutionsCreation<DoubleSolution> initialPopulationCreation,
        Termination termination,
        PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
        DifferentialCrossoverVariation variation,
        MOEADReplacement<DoubleSolution> replacement) {
        super(
        "MOEAD-DE",
        evaluation,
        initialPopulationCreation,
        termination,
        selection,
        variation,
        replacement);
        }


  /** Creates a {@link MOEADDE}. */
  public static MOEADDE create(
      Evaluation<DoubleSolution> evaluation,
      InitialSolutionsCreation<DoubleSolution> initialPopulationCreation,
      Termination termination,
      PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
      DifferentialCrossoverVariation variation,
      MOEADReplacement<DoubleSolution> replacement) {
    
    String name = "MOEAD-DE" ;
    return new MOEADDE(
        name,
        null,
        new DefaultObservable<>(name),
        new HashMap<>(),
        initialPopulationCreation,
        variation,
        selection,
        replacement,
        termination,
        evaluation,
        null);
    }
  
  /**
   * Constructor with the parameters used in the paper describing MOEA/D-DE.
   *
   * @param problem
   * @param populationSize
   * @param f
   * @param cr
   * @param neighborhoodSelectionProbability
   * @param maximumNumberOfReplacedSolutions
   * @param neighborhoodSize
   * @param termination
   * @deprecated Use instead
   *             {@link #create(Problem, int, double, double, AggregativeFunction, double, int, int, String, Termination)}.
   */
  public MOEADDE(
      Problem<DoubleSolution> problem,
      int populationSize,
      double cr,
      double f,
      AggregativeFunction aggregativeFunction,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborhoodSize,
      String weightVectorDirectory,
      Termination termination) {
    this.name = "MOEAD-DE" ;
    this.problem = problem ;
    this.observable = new DefaultObservable<>(name);
    this.attributes = new HashMap<>();

    SequenceGenerator<Integer> subProblemIdGenerator =
        new IntegerPermutationGenerator(populationSize);

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    PolynomialMutation mutation =
        new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int offspringPopulationSize = 1;
    this.variation =
        DifferentialCrossoverVariation.createWithDefaultMatingPoolSize(
            offspringPopulationSize, crossover, mutation, subProblemIdGenerator);

    WeightVectorNeighborhood<DoubleSolution> neighborhood = null ;
    if (problem.getNumberOfObjectives() == 2) {
      neighborhood = new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    } else {
      try {
        neighborhood =
            new WeightVectorNeighborhood<>(
                populationSize,
                problem.getNumberOfObjectives(),
                neighborhoodSize,
                 weightVectorDirectory);
      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    this.selection =
        new PopulationAndNeighborhoodMatingPoolSelection<>(
            ((DifferentialCrossoverVariation) variation)
                .getCrossover()
                .getNumberOfRequiredParents(),
            subProblemIdGenerator,
            neighborhood,
            neighborhoodSelectionProbability,
            true);

    this.replacement =
        new MOEADReplacement<DoubleSolution>(
            (PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution>) selection,
            neighborhood,
            aggregativeFunction,
            subProblemIdGenerator,
            maximumNumberOfReplacedSolutions);

    this.termination = termination ;

    this.evaluation = new SequentialEvaluation<>(problem) ;

    this.archive = null ;
  }
  
  /**
   * Creates a {@link MOEADDE}.
   *
   * @param problem
   * @param populationSize
   * @param f
   * @param cr
   * @param neighborhoodSelectionProbability
   * @param maximumNumberOfReplacedSolutions
   * @param neighborhoodSize
   * @param termination
   */
  public static MOEADDE create(
      Problem<DoubleSolution> problem,
      int populationSize,
      double cr,
      double f,
      AggregativeFunction aggregativeFunction,
      double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions,
      int neighborhoodSize,
      String weightVectorDirectory,
      Termination termination) {
    String name = "MOEAD-DE" ;
    Observable<Map<String, Object>> observable = new DefaultObservable<>(name);
    Map<String, Object> attributes = new HashMap<>();

    SequenceGenerator<Integer> subProblemIdGenerator =
        new IntegerPermutationGenerator(populationSize);

    InitialSolutionsCreation<DoubleSolution> createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    PolynomialMutation mutation =
        new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int offspringPopulationSize = 1;
    Variation<DoubleSolution> variation =
        DifferentialCrossoverVariation.createWithDefaultMatingPoolSize(
            offspringPopulationSize, crossover, mutation, subProblemIdGenerator);

    WeightVectorNeighborhood<DoubleSolution> neighborhood = null ;
    if (problem.getNumberOfObjectives() == 2) {
      neighborhood = new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    } else {
      try {
        neighborhood =
            new WeightVectorNeighborhood<>(
                populationSize,
                problem.getNumberOfObjectives(),
                neighborhoodSize,
                 weightVectorDirectory);
      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    MatingPoolSelection<DoubleSolution> selection =
        new PopulationAndNeighborhoodMatingPoolSelection<>(
            ((DifferentialCrossoverVariation) variation)
                .getCrossover()
                .getNumberOfRequiredParents(),
            subProblemIdGenerator,
            neighborhood,
            neighborhoodSelectionProbability,
            true);

    Replacement<DoubleSolution> replacement =
        new MOEADReplacement<DoubleSolution>(
            (PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution>) selection,
            neighborhood,
            aggregativeFunction,
            subProblemIdGenerator,
            maximumNumberOfReplacedSolutions);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;

    Archive<DoubleSolution> archive = null ;
    
    return new MOEADDE(
        name,
        problem,
        observable,
        attributes,
        createInitialPopulation,
        variation,
        selection,
        replacement,
        termination,
        evaluation,
        archive
        );
  }
}
