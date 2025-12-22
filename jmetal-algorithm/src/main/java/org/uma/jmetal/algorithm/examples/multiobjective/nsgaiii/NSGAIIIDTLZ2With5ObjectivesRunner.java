package org.uma.jmetal.algorithm.examples.multiobjective.nsgaiii;

import java.util.List;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Class to configure and run NSGA-III on DTLZ2 with 5 objectives.
 *
 * <p>
 * This example demonstrates the use of two-layer reference points, which is
 * recommended for problems with many objectives (>3) as per the NSGA-III paper.
 *
 * <p>
 * Reference: K. Deb and H. Jain, "An Evolutionary Many-Objective Optimization
 * Algorithm Using Reference-Point-Based Nondominated Sorting Approach, Part I:
 * Solving Problems With Box Constraints," IEEE Transactions on Evolutionary
 * Computation, vol. 18, no. 4, pp. 577-601, Aug. 2014.
 */
public class NSGAIIIDTLZ2With5ObjectivesRunner extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws JMetalException {
    // DTLZ2 with 5 objectives requires at least 5 + k - 1 = 5 + 10 - 1 = 14
    // variables
    int numberOfVariables = 14;
    int numberOfObjectives = 5;

    var problem = new DTLZ2(numberOfVariables, numberOfObjectives);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability,
        crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability,
        mutationDistributionIndex);

    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<>(
        new RankingAndCrowdingDistanceComparator<>());

    // For 5 objectives, use two-layer reference points as recommended in the paper:
    // - Outer layer: 6 divisions -> C(6+5-1, 5-1) = C(10,4) = 210 points
    // - Inner layer: 3 divisions -> C(3+5-1, 5-1) = C(7,4) = 35 points
    // Total: 245 reference points (population size will be rounded up to multiple
    // of 4)
    var algorithm = new NSGAIIIBuilder<>(problem)
        .setCrossoverOperator(crossover)
        .setMutationOperator(mutation)
        .setSelectionOperator(selection)
        .setMaxIterations(500)
        .setNumberOfDivisions(6) // Outer layer divisions
        .setSecondLayerDivisions(3) // Inner layer divisions (set to 0 to disable)
        .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.result();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Number of solutions: " + population.size());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
