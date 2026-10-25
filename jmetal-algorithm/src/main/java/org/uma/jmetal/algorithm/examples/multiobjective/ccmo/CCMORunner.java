package org.uma.jmetal.algorithm.examples.multiobjective.ccmo;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.ccmo.CCMO;
import org.uma.jmetal.algorithm.multiobjective.ccmo.CCMOBuilder;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

/**
 * Class to configure and run CCMO on the constrained problem MW1, with the settings of the paper
 * of CCMO (Tian et al., IEEE TEVC 2021): two populations of 100 solutions, 100000 evaluations, SBX
 * with probability 1.0 and polynomial mutation with probability 1/n, both with distribution index
 * 20. It writes the feasible solutions of the result to VAR.csv and FUN.csv and prints their
 * quality indicators.
 *
 * <p>To use the differential evolution variation of PlatEMO instead, give a {@code new
 * DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN)} as crossover and call {@code
 * setVariationType(CCMO.VariationType.DE)}.
 */
public class CCMORunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.mw.MW1";
    String referenceParetoFront = "resources/referenceFronts/MW1.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 1.0;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    CCMO<DoubleSolution> algorithm =
        new CCMOBuilder<>(problem, crossover, mutation)
            .setPopulationSize(100)
            .setMaxEvaluations(100000)
            .setVariationType(CCMO.VariationType.GA)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population =
        algorithm.result().stream().filter(ConstraintHandling::isFeasible).toList();

    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info(
        "Feasible solutions: " + population.size() + " of " + algorithm.result().size());

    printFinalSolutionSet(population);
    if (!population.isEmpty()) {
      QualityIndicatorUtils.printQualityIndicators(
          SolutionListUtils.getMatrixWithObjectiveValues(population),
          VectorUtils.readVectors(referenceParetoFront, ","));
    }
  }
}
