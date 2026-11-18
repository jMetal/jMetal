package org.uma.jmetal.algorithm.examples.multiobjective.cmoeams;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.cmoeams.CMOEAMSBuilder;
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
 * Class for configuring and running CMOEA-MS on a constrained problem, with the operators and
 * parameters of the paper and of PlatEMO: simulated binary crossover (probability 1, distribution
 * index 20), polynomial mutation (probability 1/n, distribution index 20) and lambda = 0.5. The
 * whole final population is written to the files VAR.csv and FUN.csv, and the quality indicators
 * are computed on its feasible non-dominated solutions.
 *
 * <p>For the variant with differential evolution (type 2 in PlatEMO), replace the crossover with
 * {@code new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN)}.
 */
public class CMOEAMSRunner extends AbstractAlgorithmRunner {

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

    Algorithm<List<DoubleSolution>> algorithm =
        new CMOEAMSBuilder<>(problem, crossover, mutation)
            .setPopulationSize(100)
            .setMaxEvaluations(50000)
            .setLambda(0.5)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.result();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);

    List<DoubleSolution> feasibleSolutions =
        population.stream().filter(ConstraintHandling::isFeasible).toList();
    JMetalLogger.logger.info(
        "Feasible solutions: " + feasibleSolutions.size() + " of " + population.size());

    if (!feasibleSolutions.isEmpty()) {
      QualityIndicatorUtils.printQualityIndicators(
          SolutionListUtils.getMatrixWithObjectiveValues(
              SolutionListUtils.getNonDominatedSolutions(feasibleSolutions)),
          VectorUtils.readVectors(referenceParetoFront, ","));
    }
  }
}
