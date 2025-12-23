package org.uma.jmetal.component.examples.multiobjective.nsgaiii;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Example of running the component-based NSGA-III algorithm on DTLZ1 with 3
 * objectives.
 *
 * @author Antonio J. Nebro
 */
public class NSGAIIIExample {

  public static void main(String[] args) throws JMetalException, IOException {
    var problem = ProblemFactory.<DoubleSolution>loadProblem(
        "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2");

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int numberOfDivisions = 12;

    EvolutionaryAlgorithm<DoubleSolution> nsgaiii = new NSGAIIIBuilder<>(
        problem,
        numberOfDivisions,
        crossover,
        mutation)
        .setTermination(new TerminationByEvaluations(25000))
        .build();

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
