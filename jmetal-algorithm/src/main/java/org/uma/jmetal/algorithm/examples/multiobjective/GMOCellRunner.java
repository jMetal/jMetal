package org.uma.jmetal.algorithm.examples.multiobjective;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class to configure and run the MOCell algorithm with DM support applying the concept of
 * g-dominance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GMOCellRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException Invoking command: java
   *     org.uma.jmetal.runner.multiobjective.MOCellRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, FileNotFoundException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    String referenceParetoFront = "";

    String problemName;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0];
      referenceParetoFront = args[1];
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv";
    }

    problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    selection =
        new BinaryTournamentSelection<>(
            new RankingAndCrowdingDistanceComparator<>());

    List<Double> referencePoint = Arrays.asList(0.3, 0.8);

    @NotNull ArchiveWithReferencePoint<DoubleSolution> archive =
        new CrowdingDistanceArchiveWithReferencePoint<>(100, referencePoint);

    algorithm =
        new MOCellBuilder<DoubleSolution>(problem, crossover, mutation)
            .setSelectionOperator(selection)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .setArchive(archive)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
