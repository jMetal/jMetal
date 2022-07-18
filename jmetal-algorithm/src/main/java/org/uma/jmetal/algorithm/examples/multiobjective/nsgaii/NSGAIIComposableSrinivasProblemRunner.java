package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.io.FileNotFoundException;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.impl.ComposableDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class to configure and run the NSGA-II algorithm to solve the
 * {@link org.uma.jmetal.problem.multiobjective.Srinivas} problem, which is defined dynamically by
 * using the {@link ComposableDoubleProblem} class.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIComposableSrinivasProblemRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    String referenceParetoFront = "resources/referenceFrontsCSV/Srinivas.csv" ;

    @NotNull var problem = new ComposableDoubleProblem()
        .setName("Srinivas")
        .addVariable(-20.0, 20.0)
        .addVariable(-20.0, 20.0)
        .addFunction((x) ->  2.0 + (x[0] - 2.0) * (x[0] - 2.0) + (x[1] - 1.0) * (x[1] - 1.0))
        .addFunction((x) ->  9.0 * x[0] - (x[1] - 1.0) * (x[1] - 1.0))
        .addConstraint((x) -> 1.0 - (x[0] * x[0] + x[1] * x[1]) / 225.0)
        .addConstraint((x) -> (3.0 * x[1] - x[0]) / 10.0 - 1.0) ;

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    @NotNull var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    @NotNull var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
        new RankingAndCrowdingDistanceComparator<>());

    var algorithm = new NSGAIIBuilder<>(problem, crossover, mutation, 100)
        .setSelectionOperator(selection)
        .setMaxEvaluations(25000)
        .setDominanceComparator(new DominanceWithConstraintsComparator<>())
        .build() ;

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
