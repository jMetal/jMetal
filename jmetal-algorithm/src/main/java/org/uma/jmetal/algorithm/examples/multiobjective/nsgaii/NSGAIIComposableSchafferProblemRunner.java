package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.impl.ComposableDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class to configure and run the NSGA-II algorithm to solve the
 * {@link org.uma.jmetal.problem.multiobjective.Schaffer} problem, which is defined dynamically by
 * using the {@link ComposableDoubleProblem} class.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIComposableSchafferProblemRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Invoking command:
    java org.uma.jmetal.runner.multiobjective.nsgaii.NSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    var referenceParetoFront = "resources/referenceFrontsCSV/Schaffer.csv" ;

    var problem = new ComposableDoubleProblem()
              .setName("Schaffer")
              .addVariable(-10, 10)
              .addVariable(-10, 10)
              .addFunction((x) -> x[0] * x[0])
              .addFunction((x) -> (x[0] - 2.0) * (x[0] - 2.0));

    var crossoverProbability = 0.9 ;
    var crossoverDistributionIndex = 20.0 ;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
              new RankingAndCrowdingDistanceComparator<DoubleSolution>());

      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, 100)
              .setSelectionOperator(selection)
              .setMaxEvaluations(25000)
              .build();

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute() ;

    var population = algorithm.getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
}
