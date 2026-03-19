package org.uma.jmetal.component.examples.multiobjective.nsgaiii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Example of running the component-based NSGA-III algorithm on DTLZ2 with 5
 * objectives,
 * using two-layer reference points.
 *
 * <p>
 * This demonstrates the many-objective optimization capabilities of NSGA-III.
 *
 * @author Antonio J. Nebro
 */
public class NSGAIIIManyObjectiveExample {

  public static void main(String[] args) throws JMetalException, IOException {
    // DTLZ2 with 5 objectives (14 variables = 5 + 10 - 1)
    int numberOfVariables = 14;
    int numberOfObjectives = 5;
    var problem = new DTLZ2(numberOfVariables, numberOfObjectives);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    // Two-layer reference points for 5 objectives
    int outerDivisions = 6;
    int innerDivisions = 3;

    EvolutionaryAlgorithm<DoubleSolution> nsgaiii = new NSGAIIIBuilder<>(
        problem,
        outerDivisions,
        innerDivisions,
        crossover,
        mutation)
        .setTermination(new TerminationByEvaluations(50000))
        .build();

    JMetalLogger.logger.info("Reference points: " + nsgaiii.attributes());
    JMetalLogger.logger.info("Population size: " +
        ((NSGAIIIBuilder<DoubleSolution>) null != null ? "N/A" : "See builder"));

    nsgaiii.run();

    List<DoubleSolution> population = nsgaiii.result();

    JMetalLogger.logger.info("Total execution time: " + nsgaiii.totalComputingTime() + "ms");
    JMetalLogger.logger.info("Number of evaluations: " + nsgaiii.numberOfEvaluations());
    JMetalLogger.logger.info("Number of solutions: " + population.size());

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");
  }
}
