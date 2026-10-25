package org.uma.jmetal.algorithm.examples.multiobjective.top;

import java.io.IOException;
import java.util.List;
import java.util.OptionalInt;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.top.ToP;
import org.uma.jmetal.algorithm.multiobjective.top.ToPBuilder;
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
 * Class for configuring and running ToP, the two-phase framework with NSGA-II, on the constrained
 * problem LIR-CMOP2. The final population is written to the files VAR.csv and FUN.csv, and the
 * quality indicators are computed on its feasible solutions.
 */
public class ToPRunner extends AbstractAlgorithmRunner {

  /**
   * @param args Command line arguments.
   */
  public static void main(String[] args) throws IOException {
    String problemName = "org.uma.jmetal.problem.multiobjective.lircmop.LIRCMOP2";
    String referenceParetoFront = "resources/referenceFronts/LIRCMOP2.csv";

    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    double mutationProbability = 1.0 / problem.numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    ToP algorithm =
        new ToPBuilder(problem)
            .setPopulationSize(100)
            .setMaxEvaluations(100000)
            .setCr(1.0)
            .setF(0.5)
            .setMutationOperator(mutation)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.result();
    long computingTime = algorithmRunner.getComputingTime();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    OptionalInt phaseTwoStart = algorithm.phaseTwoStart();
    JMetalLogger.logger.info(
        phaseTwoStart.isPresent()
            ? "Phase 2 started after " + phaseTwoStart.getAsInt() + " evaluations"
            : "Phase 2 was not reached");

    printFinalSolutionSet(population);

    List<DoubleSolution> feasibleSolutions =
        population.stream().filter(ConstraintHandling::isFeasible).toList();
    JMetalLogger.logger.info(
        "Feasible solutions: " + feasibleSolutions.size() + " of " + population.size());
    if (!feasibleSolutions.isEmpty()) {
      QualityIndicatorUtils.printQualityIndicators(
          SolutionListUtils.getMatrixWithObjectiveValues(feasibleSolutions),
          VectorUtils.readVectors(referenceParetoFront, ","));
    }
  }
}
