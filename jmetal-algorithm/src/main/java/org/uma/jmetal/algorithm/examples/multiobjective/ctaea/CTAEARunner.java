package org.uma.jmetal.algorithm.examples.multiobjective.ctaea;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.ctaea.CTAEA;
import org.uma.jmetal.algorithm.multiobjective.ctaea.CTAEABuilder;
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
 * Class for configuring and running the C-TAEA algorithm on the constrained problem C1-DTLZ1 with
 * three objectives. The operators are those of PlatEMO by default: SBX with probability 1.0 and
 * polynomial mutation with probability 1/n, both with distribution index 20.
 */
public class CTAEARunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.cdtlz.C1_DTLZ1";
    String referenceParetoFront = "resources/referenceFronts/C1_DTLZ1.3D.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double crossoverProbability = 1.0;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    // 100 requested weight vectors give 91 with three objectives: 500 generations of 91
    CTAEA<DoubleSolution> algorithm =
        new CTAEABuilder<>(problem, crossover, mutation)
            .setPopulationSize(100)
            .setMaxEvaluations(91 * 500)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.result();
    List<DoubleSolution> feasibleSolutions =
        population.stream().filter(ConstraintHandling::isFeasible).toList();

    long computingTime = algorithmRunner.getComputingTime();
    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info(
        "Feasible solutions: " + feasibleSolutions.size() + " of " + population.size());

    printFinalSolutionSet(population);
    if (!feasibleSolutions.isEmpty()) {
      QualityIndicatorUtils.printQualityIndicators(
          SolutionListUtils.getMatrixWithObjectiveValues(feasibleSolutions),
          VectorUtils.readVectors(referenceParetoFront, ","));
    }
  }
}
