package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.replacement.impl.MOEADReplacement;
import org.uma.jmetal.component.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.observable.impl.DefaultObservable;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

import java.util.HashMap;

/**
 * This class is intended to provide an implementation of the MOEA/D-DE algorithm including a constructor with the
 * typical parameters.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class MOEADDE extends MOEAD<DoubleSolution> {

  /** Constructor */
  public MOEADDE(
      Problem<DoubleSolution> problem,
      int populationSize,
      InitialSolutionsCreation<DoubleSolution> initialSolutionsCreation,
      DifferentialCrossoverVariation variation,
      PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> selection,
      MOEADReplacement replacement,
      Termination termination) {
    super(
        problem,
        populationSize,
        initialSolutionsCreation,
        variation,
        selection,
        replacement,
        termination);
  }

  /**
   * Constructor with the parameters used in the paper describing MOEA/D-DE.
   *
   * @param problem
   * @param populationSize
   * @param maxNumberOfEvaluations
   * @param f
   * @param cr
   * @param neighborhoodSelectionProbability
   * @param maximumNumberOfReplacedSolutions
   * @param neighborhoodSize
   */
  public MOEADDE(Problem<DoubleSolution> problem,
                 int populationSize,
                 int maxNumberOfEvaluations,
                 double cr,
                 double f,
                 AggregativeFunction aggregativeFunction,
                 double neighborhoodSelectionProbability,
                 int maximumNumberOfReplacedSolutions,
                 int neighborhoodSize) {
    this.problem = problem ;
    this.populationSize = populationSize ;

    this.offspringPopulationSize = 1 ;

    SequenceGenerator<Integer> subProblemIdGenerator =
            new IntegerPermutationGenerator(populationSize);

    this.initialSolutionsCreation = new RandomSolutionsCreation<>(problem, populationSize) ;

    DifferentialEvolutionCrossover crossover =
            new DifferentialEvolutionCrossover(
                    cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    PolynomialMutation mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    this.variation =
            new DifferentialCrossoverVariation(
                    offspringPopulationSize, crossover, mutation, subProblemIdGenerator);

    WeightVectorNeighborhood<DoubleSolution> neighborhood =
            new WeightVectorNeighborhood<>(
                    populationSize,
                    neighborhoodSize);

    this.selection =
            new PopulationAndNeighborhoodMatingPoolSelection<>(
                    ((DifferentialCrossoverVariation)variation).getCrossover().getNumberOfRequiredParents(),
                    subProblemIdGenerator,
                    neighborhood,
                    neighborhoodSelectionProbability,
                    true);

    this.replacement =
            new MOEADReplacement(
                    (PopulationAndNeighborhoodMatingPoolSelection)selection,
                    neighborhood,
                    aggregativeFunction,
                    subProblemIdGenerator,
                    maximumNumberOfReplacedSolutions);

    this.termination = new TerminationByEvaluations(maxNumberOfEvaluations) ;

    this.evaluation = new SequentialEvaluation<>();

    this.algorithmStatusData = new HashMap<>();
    this.observable = new DefaultObservable<>("MOEA/D algorithm");
  }
}
