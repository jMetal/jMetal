package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.densityestimator.impl.ShiftedDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Example of NSGA-II using Shift-Based Density Estimation (SDE) instead of
 * crowding distance.
 *
 * <p>
 * SDE is particularly effective for many-objective optimization as it
 * incorporates
 * both convergence and diversity information.
 *
 * <p>
 * Reference: M. Li, S. Yang, and X. Liu, "Shift-Based Density Estimation for
 * Pareto-Based
 * Algorithms in Many-Objective Optimization," IEEE TEVC, vol. 18, no. 3, pp.
 * 348-365, 2014.
 *
 * @author Antonio J. Nebro
 */
public class NSGAIIWithSDEExample {

  public static void main(String[] args) throws JMetalException, IOException {
    // DTLZ2 with 5 objectives - a many-objective problem where SDE shines
    int numberOfVariables = 14;
    int numberOfObjectives = 5;
    var problem = new DTLZ2(numberOfVariables, numberOfObjectives);

    int populationSize = 100;
    int offspringPopulationSize = 100;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    // Use SDE instead of crowding distance
    var densityEstimator = new ShiftedDensityEstimator<DoubleSolution>();

    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new NSGAIIBuilder<>(
        problem,
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setDensityEstimator(densityEstimator)
        .setTermination(new TerminationByEvaluations(25000))
        .build();

    nsgaii.run();

    List<DoubleSolution> population = nsgaii.result();

    JMetalLogger.logger.info("Total execution time: " + nsgaii.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaii.numberOfEvaluations());
    JMetalLogger.logger.info("Number of solutions: " + population.size());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
