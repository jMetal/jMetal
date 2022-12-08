package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.util.List;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII45;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Class to configure and run the implementation of the NSGA-II algorithm included in
 * {@link NSGAII45}
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAII45Runner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   * @throws JMetalException
   */
  public static void main(String[] args) {

    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";

    var problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    var selection = new BinaryTournamentSelection<>(
        new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    var algorithm = new NSGAII45<>(problem, 25000, 100, crossover, mutation,
        selection, new SequentialSolutionListEvaluator<>());

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
        .execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    printQualityIndicators(population, referenceParetoFront);
  }
}
